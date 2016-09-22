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
     *
     * @param message
     */
    public void ping(@Observes @PingEvent ExchangeMessageEvent message);

    /**
     * Get plugin list from APP module
     *
     * @param message
     */
    public void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message);

    /**
     * Process a received Movement
     *
     * @param message
     */
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message);

    /**
     * Process answer of commands sent to plugins
     *
     * @param message
     */
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message);

    /**
     * Process answer of ping sent to plugins
     *
     * @param message
     */
    public void processPluginPing(@Observes @PluginPingEvent ExchangeMessageEvent message);

}
