package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface ExchangeEventIncomingService {

    // Asynch response handler for processed movements
    void handleProcessedMovement(@Observes @HandleProcessedMovementEvent ExchangeMessageEvent message);

    /**
     * Ping Exchange APP module
     * @param message
     */
    public void ping(@Observes @PingEvent ExchangeMessageEvent message);

    /**
     * Get plugin list from APP module
     * @param message
     */
    public void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message);

    /**
     * Process a received Movement
     * @param message
     */
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message);
    
    /**
     * Process answer of commands sent to plugins
     * @param message
     */
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message);
    
    /**
     * Process answer of ping sent to plugins
     * @param message
     */
    public void processPluginPing(@Observes @PluginPingEvent ExchangeMessageEvent message);


    /**
     * Process FLUXFAReportMessage coming from Flux Activity plugin
     * @param message
     */
    public void processFLUXFAReportMessage(@Observes @SetFluxFAReportMessageEvent ExchangeMessageEvent message);
}
