package eu.europa.ec.fisheries.uvms.exchange.message.producer;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;

@Local
public interface MessageProducer {

    public String sendDataSourceMessage(String text, DataSourceQueue queue) throws ExchangeMessageException;

    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException;

}
