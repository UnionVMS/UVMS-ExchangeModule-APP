package eu.europa.ec.fisheries.uvms.exchange.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless
@LocalBean
public class ExchangeToExchangeProducerBean extends AbstractProducer {
    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_EXCHANGE_EVENT;
    }
}