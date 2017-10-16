/*
 ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;

@Local
public interface ExchangeEventIncomingService {

    /**
     * Async response handler for processed movements
     *
     * @param message
     */
    void handleProcessedMovement(@Observes @HandleProcessedMovementEvent ExchangeMessageEvent message);

    /**
     * Ping Exchange APP module
     *
     * @param message
     */
    void ping(@Observes @PingEvent ExchangeMessageEvent message);

    /**
     * Get plugin list from APP module
     *
     * @param message
     */
    void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message);

    /**
     * Process a received Movement
     *
     * @param message
     */
    void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales report through to Rules
     *
     * @param message received sales report
     */
    void receiveSalesReport(@Observes @ReceiveSalesReportEvent ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales query through to Rules
     *
     * @param message received sales query
     */
    void receiveSalesQuery(@Observes @ReceiveSalesQueryEvent ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales response through to Rules
     * @param message
     */
    void receiveSalesResponse(@Observes @ReceiveSalesResponseEvent ExchangeMessageEvent message);

    /**
     * Logs and sends a sales report to FLUX
     * @param message
     */
    void sendSalesReport(@Observes @SendSalesReportEvent ExchangeMessageEvent message);

    /**
     * Logs and sends a sales response to FLUX
     * @param message
     */
    void sendSalesResponse(@Observes @SendSalesResponseEvent ExchangeMessageEvent message);

    /**
     * Process answer of commands sent to plugins
     *
     * @param message
     */
    void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message);

    /**
     * Process answer of ping sent to plugins
     *
     * @param message
     */
    void processPluginPing(@Observes @PluginPingEvent ExchangeMessageEvent message);

    /**
     * Process FLUXFAReportMessage coming from Flux Activity plugin
     * @param message
     */
    void processFLUXFAReportMessage(@Observes @SetFluxFAReportMessageEvent ExchangeMessageEvent message);

    /**
     * Process MDR sync response message sent to Flux MDR plugin
     * @param message
     */
    void sendResponseToRulesModule(@Observes @MdrSyncResponseMessageEvent ExchangeMessageEvent message);

    /**
     * Updates the status of a message log.
     * @param message
     */
    void updateLogStatus(@Observes @UpdateLogStatusEvent ExchangeMessageEvent message);

    void receiveInvalidSalesMessage(@Observes @ReceiveInvalidSalesMessageEvent ExchangeMessageEvent event);
}