package eu.europa.ec.fisheries.uvms.exchange.message.consumer;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;

@Local
public interface ExchangeConfigMessageConsumer {

    public <T> T getMessage(String correlationId, Class type) throws ExchangeMessageException;

}
