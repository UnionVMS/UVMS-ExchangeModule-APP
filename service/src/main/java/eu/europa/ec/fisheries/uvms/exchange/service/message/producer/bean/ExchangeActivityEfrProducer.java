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

@Stateless
public class ExchangeActivityEfrProducer extends AbstractProducer {

    @Resource(mappedName = "java:/jms/queue/EfrExchangeToActivity")
    private Queue destination;

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void sendEfrSaveReport(String text) throws JMSException {
        final Map<String, String> jmsFunctionProperty = Map.of(MessageConstants.JMS_FUNCTION_PROPERTY, ExchangeModuleMethod.EFR_SAVE_REPORT.name());
        sendModuleMessageWithProps(text, destination, jmsFunctionProperty);
    }
}
