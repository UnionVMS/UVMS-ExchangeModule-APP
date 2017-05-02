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

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

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
     * Process a received sales report
     *
     * @param message received sales report
     */
    void processSalesReport(@Observes @SalesReportEvent ExchangeMessageEvent message);

    /**
     * Process a received sales query
     *
     * @param message received sales query
     */
    void processSalesQuery(@Observes @SalesQueryEvent ExchangeMessageEvent message);

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
     * Send Sales Query Response to FLUX
     * @param message
     */
    void sendSalesMessageResponse(@Observes @SendSalesMessageEvent ExchangeMessageEvent message) throws ServiceException, ServiceException;

    /**
     * Receive Sales message
     * @param message
     */
    void receiveSalesMessage(@Observes @ReceiveSalesResponseEvent ExchangeMessageEvent message) throws ServiceException;
}