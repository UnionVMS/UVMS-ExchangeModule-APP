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

import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.source.GetDataRequest;

//@formatter:off
@MessageDriven(mappedName = MessageConstants.EXCHANGE_MESSAGE_IN_QUEUE, activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.EXCHANGE_MESSAGE_IN_QUEUE_NAME)
})
//@formatter:on
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @MessageRecievedEvent
    Event<EventMessage> messageRecievedEvent;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            LOG.info("Message received in exchange");
            GetDataRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, GetDataRequest.class);
            messageRecievedEvent.fire(new EventMessage(textMessage, request.getId().toString()));
        } catch (ExchangeModelMapperException | NullPointerException e) {
            LOG.error("[ Error when receiving message in exchange: ]", e);
            errorEvent.fire(new EventMessage(textMessage, "Error when receiving message in exchange: " + e.getMessage()));
        }
    }

}
