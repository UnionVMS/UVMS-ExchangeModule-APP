package eu.europa.ec.fisheries.uvms.exchange.message.config;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractConsumer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean.ExchangeConsumerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

@Stateless
public class ExchangeConfigConsumerBean extends AbstractConsumer implements ConfigMessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeConsumerBean.class);

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_EXCHANGE;
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        } catch (MessageException e) {
            LOG.error("Error while trying to get config!",e);
        }
        return null;
    }

}
