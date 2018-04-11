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

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PluginBaseRequest;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface ExchangeEventOutgoingService {

    /**
     * Send a report to a plugin
     *
     * @param message
     */
    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message);

    /**
     * Send a command to a plugin
     *
     * @param message
     */
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message);

    /**
     * Sends MDR sync message to the MDR plugin
     * @param message
     */
    void forwardMdrSyncMessageToPlugin(@Observes @MdrSyncRequestMessageEvent ExchangeMessageEvent message);

    /**
     * Sends FLUX FA response message to ERS/Activity plugin
     * @param message
     */
    void sendFLUXFAResponseToPlugin(@Observes @SendFLUXFAResponseToPluginEvent ExchangeMessageEvent message);

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

    void sendFLUXFAQueryToPlugin(@Observes @SendFaQueryToPluginEvent ExchangeMessageEvent message);

    void sendFLUXFAReportToPlugin(@Observes @SendFaReportToPluginEvent ExchangeMessageEvent message);

    void sendAssetInformationToFLUX(PluginBaseRequest request) throws ExchangeModelMarshallException, ExchangeMessageException;
}
