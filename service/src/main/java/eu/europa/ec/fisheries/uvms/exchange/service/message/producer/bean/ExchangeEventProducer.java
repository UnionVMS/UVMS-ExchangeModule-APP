package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

@Stateless
@LocalBean
public class ExchangeEventProducer extends AbstractProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeEventProducer.class);

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_EXCHANGE_EVENT)
    private Queue destination;

    @Resource(mappedName = "java:/jms/queue/UVMSExchange")
    private Queue replyToQueue;

    public String sendExchangeEventMessage(String text, String function){
        try {

            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, function);
            return sendModuleMessageWithProps(text, replyToQueue, properties);

        } catch (JMSException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeErrorEvent message) {
        try {
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            this.sendResponseMessageToSender(message.getJmsMessage(), data);
        } catch (Exception e) {
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @Override
    public Destination getDestination() {
        return destination;
    }
}
