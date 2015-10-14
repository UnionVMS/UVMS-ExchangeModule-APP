package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

import eu.europa.ec.fisheries.uvms.exchange.message.event.ConfigMessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;

@Local
public interface EventService {

    public void getPluginConfig(@Observes @PluginConfigEvent ExchangeMessageEvent message);

    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message);
    
    public void ping(@Observes @PingEvent ExchangeMessageEvent message);

    public void receiveConfigMessageEvent(@Observes @ConfigMessageRecievedEvent ExchangeMessageEvent message);

    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message);
    
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message);
    
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message);
}
