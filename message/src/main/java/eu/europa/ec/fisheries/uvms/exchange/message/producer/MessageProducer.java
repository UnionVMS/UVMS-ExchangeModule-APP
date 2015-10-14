package eu.europa.ec.fisheries.uvms.exchange.message.producer;

import javax.ejb.Local;
import javax.enterprise.event.Observes;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;

@Local
public interface MessageProducer {

    public String sendMessageOnQueue(String text, DataSourceQueue queue) throws ExchangeMessageException;

    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException;

    public String sendPluginTypeEventBusMessage(String text, PluginType pluginType) throws ExchangeMessageException;
    
    public String sendConfigMessage(String text) throws ExchangeMessageException;

    public void sendModuleResponseMessage(TextMessage message, String text);
    
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent event);
}
