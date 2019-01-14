package eu.europa.ec.fisheries.uvms.exchange.message.config;


import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean.ExchangeConsumerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Queue;

@Stateless
public class ExchangeConfigProducerBean extends AbstractProducer implements ConfigMessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeConsumerBean.class);

    private Queue exchangeINQueue;

    @PostConstruct
    public void init() {
        exchangeINQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String text) throws ConfigMessageException {
        try {
            return sendModuleMessage(text, exchangeINQueue);
        } catch (MessageException e) {
            LOG.error("[ERROR] Error when sending config message!", e);
            throw new ConfigMessageException("Error when sending config message.");
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_CONFIG;
    }
}
