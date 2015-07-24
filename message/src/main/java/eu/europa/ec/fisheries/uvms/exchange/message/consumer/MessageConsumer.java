package eu.europa.ec.fisheries.uvms.exchange.message.consumer;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;

@Local
public interface MessageConsumer {

    public <T> T getMessage(String correlationId, Class type) throws ExchangeMessageException;

}
