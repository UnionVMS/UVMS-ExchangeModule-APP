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
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PluginBaseRequest;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

@Local
public interface ExchangeEventOutgoingService {

    /**
     * Send a report to a plugin
     *
     * @param message
     */
    public void sendReportToPlugin( TextMessage message);

    /**
     * Send a command to a plugin
     *
     * @param message
     */
    public void sendCommandToPlugin(TextMessage message);

    /**
     * Sends MDR sync message to the MDR plugin
     * @param message
     */
    void forwardMdrSyncMessageToPlugin(TextMessage message);

    String sendCommandToPluginFromRest(SetCommandRequest request);

    /**
     * Sends FLUX FA response message to ERS/Activity plugin
     * @param message
     */
    void sendFLUXFAResponseToPlugin(TextMessage message);

    /**
     * Sends a Sales response to the FLUX plugin
     * @param salesResponse the sales response that needs to be sent
     * @param pluginType type of the plugin which the Sales response should be sent through
     * @throws ExchangeModelMarshallException
     * @throws ExchangeMessageException
     */
    void sendSalesResponseToPlugin(eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest salesResponse, PluginType pluginType) throws ExchangeModelMarshallException, ExchangeMessageException;


    /**
     * Sends a Sales report to the FLUX plugin
     * @param salesReport
     * @throws ExchangeModelMarshallException
     * @throws ExchangeMessageException
     */
    void sendSalesReportToFLUX(eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest salesReport) throws ExchangeModelMarshallException, ExchangeMessageException;

    void sendFLUXFAQueryToPlugin(TextMessage message);

    void sendFLUXFAReportToPlugin(TextMessage message);

    void sendAssetInformationToFLUX(PluginBaseRequest request) throws ExchangeModelMarshallException, ExchangeMessageException;

    /**
     * Logs and sends a sales response to FLUX
     * @param message
     */
    void sendSalesResponse(TextMessage message);

    /**
     * Logs and sends a sales report to FLUX
     * @param message
     */
    void sendSalesReport(TextMessage message);

    /**
     * Logs and sends a send asset information to FLUX fleet plugin
     *
     * @param event send asset information message
     */
    void sendAssetInformation(TextMessage event);

    void updateLogStatus(TextMessage message);

    void updateLogBusinessError(TextMessage message);

    /**
     * Async response handler for processed movements
     *
     * @param message
     */
    void handleProcessedMovement(TextMessage message);

    void handleProcessedMovementBatch(TextMessage message);

}
