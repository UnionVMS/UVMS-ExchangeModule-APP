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

import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.source.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
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
    ExchangeService service;

    @Resource(lookup = MessageConstants.CONNECTION_FACTORY)
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

            switch (baseRequest.getMethod()) {
            case LIST_SERVICES:
                LOG.info("LIST_SERVICES");

                List<ServiceType> serviceList = service.getServiceList();

                connectQueue();

                String response = ExchangeDataSourceResponseMapper.mapServiceTypeListToStringFromResponse(serviceList);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(responseMessage);

                break;
            case REGISTER_SERVICE:
                LOG.info("REGISTER_SERVICE - not implemented");
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
