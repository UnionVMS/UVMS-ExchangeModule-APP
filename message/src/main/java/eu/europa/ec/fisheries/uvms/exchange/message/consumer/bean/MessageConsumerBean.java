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

import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.EventMessage;

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
    @MessageRecievedEvent
    Event<EventMessage> messageReceivedEvent;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        LOG.info("Message received in Exchange Message MDB");

        TextMessage textMessage = (TextMessage) message;
        try {
            messageReceivedEvent.fire(new EventMessage(textMessage));
        } catch (NullPointerException e) {
            LOG.error("[ Error when receiving message in exchange: ]", e);
            errorEvent.fire(new EventMessage(textMessage, "Error when receiving message in exchange: " + e.getMessage()));
        }
    }

}
