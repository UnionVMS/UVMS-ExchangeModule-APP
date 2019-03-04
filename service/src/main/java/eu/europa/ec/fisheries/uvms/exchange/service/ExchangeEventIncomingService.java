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

import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeMessageEvent;

@Local
public interface ExchangeEventIncomingService {

    /**
     * Ping Exchange APP module
     *
     * @param message
     */
    void ping(ExchangeMessageEvent message);

    /**
     * Get plugin list from APP module
     *
     * @param message
     */
    void getPluginListByTypes(ExchangeMessageEvent message);

    void processReceivedMovementBatch(ExchangeMessageEvent message);

    /**
     * Process a received Movement
     *
     * @param message
     */
    void processMovement(ExchangeMessageEvent message);

    /**
     * Logs and sends a received asset information to Asset
     *
     * @param message received asset information message
     */
    void receiveAssetInformation(ExchangeMessageEvent message);

    /**
     * Logs and sends a query asset information to FLUX fleet plugin
     *
     * @param message query asset information message
     */
    void queryAssetInformation(ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales report through to Rules
     *
     * @param message received sales report
     */
    void receiveSalesReport(ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales query through to Rules
     *
     * @param message received sales query
     */
    void receiveSalesQuery(ExchangeMessageEvent message);

    /**
     * Logs and sends a received sales response through to Rules
     * @param message
     */
    void receiveSalesResponse(ExchangeMessageEvent message);

    /**
     * Process answer of commands sent to plugins
     *
     * @param message
     */
    void processAcknowledge(ExchangeMessageEvent message);

    /**
     * Process answer of ping sent to plugins
     *
     * @param message
     */
    void processPluginPing(ExchangeMessageEvent message);

    /**
     * Process FLUXFAReportMessage coming from Flux Activity plugin
     * @param message
     */
    void processFLUXFAReportMessage(ExchangeMessageEvent message);

    void processFAQueryMessage(ExchangeMessageEvent message);

    void processFluxFAResponseMessage(ExchangeMessageEvent message);

    /**
     * Process MDR sync response message sent to Flux MDR plugin
     * @param message
     */
    void sendResponseToRulesModule(ExchangeMessageEvent message);

    void receiveInvalidSalesMessage(ExchangeMessageEvent event);

    /**
     * Checks for a reference in log table for a certain type of message
     * @param event
     */
    void logRefIdByTypeExists(ExchangeMessageEvent event);

    /**
     * Checks for a guid in log table for a certain type of message
     * @param event
     */
    void logIdByTypeExists(ExchangeMessageEvent event);

}