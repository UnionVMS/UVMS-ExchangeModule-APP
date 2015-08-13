package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.module.v1.CreatePollRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.EventService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

@Stateless
public class ExchangeEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventServiceBean.class);

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @EJB
    MessageProducer producer;

    @EJB
    ExchangeMessageConsumer consumer;

    @EJB
    ExchangeService exchangeService;

    @Resource(lookup = ExchangeModelConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    private Connection connection = null;
    private Session session = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void getData(@Observes @MessageRecievedEvent EventMessage message) {
        LOG.info("Received MessageRecievedEvent");

        TextMessage requestMessage = message.getJmsMessage();
        try {
            ExchangeBaseRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(requestMessage, ExchangeBaseRequest.class);
            TextMessage textMessage = message.getJmsMessage();

            switch (baseRequest.getMethod()) {
            case LIST_SERVICES:
                LOG.info("LIST_SERVICES");

                List<ServiceType> serviceList = exchangeService.getServiceList();

                connectQueue();

                String getServiceListResponse = ExchangeDataSourceResponseMapper.mapServiceTypeListToStringFromResponse(serviceList);
                TextMessage getServiceListMessage = session.createTextMessage(getServiceListResponse);
                getServiceListMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(getServiceListMessage);

                break;
            case REGISTER_SERVICE:
                LOG.info("REGISTER_SERVICE");
                TextMessage registrationMessage = message.getJmsMessage();
                ServiceType registratingService = ExchangeModuleRequestMapper.mapToServiceTypeFromRequest(registrationMessage);
                ServiceType registratedService = exchangeService.registerService(registratingService);

                connectQueue();

                String registerServiceResponse = ExchangeModuleResponseMapper.createRegisterServiceResponse(registratedService);
                TextMessage registerMessage = session.createTextMessage(registerServiceResponse);
                registerMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(registerMessage);

                break;
            case UNREGISTER_SERVICE:
                LOG.info("UNREGISTER_SERVICE");
                TextMessage unregistrationMessage = message.getJmsMessage();
                ServiceType unregistratingService = ExchangeModuleRequestMapper.mapToServiceTypeFromRequest(unregistrationMessage);
                ServiceType unregistratedService = exchangeService.unregisterService(unregistratingService);

                connectQueue();

                String unregisterServiceResponse = ExchangeModuleResponseMapper.createRegisterServiceResponse(unregistratedService);
                TextMessage unregisterMessage = session.createTextMessage(unregisterServiceResponse);
                unregisterMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(unregisterMessage);

                break;

            case CREATE_POLL:
                LOG.info("CREATE POLL");

                CreatePollRequest createpollRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, CreatePollRequest.class);
                PollType type = createpollRequest.getPoll();

                ServiceType retrievedService = exchangeService.getService(type.getServiceId());

                // Do some logic to send the data to the plugin
                // if success send OK back
                String pollResponse = ExchangeModuleResponseMapper.mapCreatePollResponseToString(AcknowledgeType.OK);
                // If not success send NOK back
                // String pollOkResponse =
                // ExchangeModuleResponseMapper.mapCreatePollResponseToString(AcknowledgeType.NOK);

                connectQueue();

                TextMessage createPollMessage = session.createTextMessage(pollResponse);
                createPollMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(createPollMessage);
                break;

            default:
                LOG.warn("No such method exists:{}", baseRequest.getMethod());
                break;
            }

        } catch (ExchangeModelMapperException | ExchangeServiceException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when sending response back to recipient : " + e.getMessage()));
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        try {
            connectQueue();
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message
                    .getJmsMessage().getJMSMessageID());

            ExchangeFault request = new ExchangeFault();

            request.setMessage(message.getErrorMessage());

            String data = JAXBMarshaller.marshallJaxBObjectToString(request);

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSCorrelationID());
            session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (ExchangeModelMapperException | JMSException e) {
            LOG.error("Error when returning Error message to recipient", e.getMessage());
        } finally {
            disconnectQueue();
        }
    }

    private void connectQueue() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    private void disconnectQueue() {
        try {
            connection.stop();
            connection.close();
        } catch (JMSException e) {
            LOG.warn("[ Error when stopping or closing JMS queue. ] {}", e.getMessage(), e.getStackTrace());
        }
    }

}
