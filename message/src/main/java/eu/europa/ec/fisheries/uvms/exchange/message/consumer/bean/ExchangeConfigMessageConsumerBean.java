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

import eu.europa.ec.fisheries.uvms.exchange.message.event.ConfigMessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;

//@formatter:off
@MessageDriven(mappedName = ExchangeModelConstants.CONFIG_STATUS_TOPIC, activationConfig = {
  @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
  @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_TOPIC),
  @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.CONFIG_STATUS_TOPIC_NAME)
})
//@formatter:on
public class ExchangeConfigMessageConsumerBean implements MessageListener {

    @Inject
    @ConfigMessageRecievedEvent
    Event<ExchangeMessageEvent> configMessageReceivedEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        configMessageReceivedEvent.fire(new ExchangeMessageEvent(textMessage));
    }

}
