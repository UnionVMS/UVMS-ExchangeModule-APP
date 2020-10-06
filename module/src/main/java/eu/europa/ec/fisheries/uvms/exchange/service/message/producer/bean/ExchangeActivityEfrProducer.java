package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.Map;

import static eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants.ACTIVITY_EVENT_QUEUE;

@Stateless
public class ExchangeActivityEfrProducer extends AbstractProducer {

    @Resource(mappedName = "java:/" + ACTIVITY_EVENT_QUEUE)
    private Queue destination;

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void sendEfrSaveActivity(String text) throws JMSException {
        final Map<String, String> jmsFunctionProperty = Map.of(MessageConstants.JMS_FUNCTION_PROPERTY, ExchangeModuleMethod.EFR_SAVE_ACTIVITY.name());
        sendModuleMessageWithProps(text, destination, jmsFunctionProperty);
    }
}
