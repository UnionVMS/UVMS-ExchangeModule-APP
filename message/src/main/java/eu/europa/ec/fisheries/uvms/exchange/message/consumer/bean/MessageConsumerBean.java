package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

//@formatter:off
@MessageDriven(mappedName = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE_NAME)
})
//@formatter:on
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @PluginConfigEvent
    Event<ExchangeMessageEvent> pluginConfigEvent;

    @Inject
    @SetMovementEvent
    Event<ExchangeMessageEvent> processMovementEvent;

    @Inject
    @PingEvent
    Event<ExchangeMessageEvent> pingEvent;

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        LOG.info("Message received in Exchange Message MDB");

        TextMessage textMessage = (TextMessage) message;
        try {
            ExchangeBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeBaseRequest.class);

            switch (request.getMethod()) {
                case LIST_SERVICES:
                    pluginConfigEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_COMMAND:
                case SET_REPORT:
                    //TODO IMPLEMENT LIST SERVICES, SET COMMAND AND SET REPORT
                    LOG.info("IMPLEMENT LIST SERVICES, SET COMMAND AND SET REPORT");
                    break;
                case SET_MOVEMENT_REPORT:
                    break;
                case PING:
                    pingEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", request.getMethod());
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Method not implemented")));
            }

        } catch (NullPointerException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when receiving message in exchange: ]", e);
            errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Error when receiving message in exchange: " + e.getMessage())));
        }
    }

}
