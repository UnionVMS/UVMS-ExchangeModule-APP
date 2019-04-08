package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

@Stateless
@LocalBean
public class ExchangeAssetProducer extends AbstractProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeAssetProducer.class);

    @Resource(mappedName = "java:/jms/queue/UVMSExchange")
    private Queue replyToQueue;

    public String forwardToAsset(String text, String function) {
        try {

            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, function);
            return sendModuleMessageWithProps(text, replyToQueue, properties);

        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_ASSET_EVENT;
    }
}
