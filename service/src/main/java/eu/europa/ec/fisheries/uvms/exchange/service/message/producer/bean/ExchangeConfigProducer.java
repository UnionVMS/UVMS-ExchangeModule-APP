package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class ExchangeConfigProducer extends AbstractProducer implements ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeConfigProducer.class);

    @Override
    public String sendConfigMessage(String text) {
        try{
            return sendModuleMessage(text, JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE));
        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_CONFIG;
    }
}