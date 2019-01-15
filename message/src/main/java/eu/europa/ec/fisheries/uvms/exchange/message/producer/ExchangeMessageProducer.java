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
package eu.europa.ec.fisheries.uvms.exchange.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import javax.ejb.Local;
import javax.enterprise.event.Observes;
import javax.jms.TextMessage;

@Local
public interface ExchangeMessageProducer {

    String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException;

    String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException;
    
    void sendModuleResponseMessage(TextMessage message, String text) throws MessageException;

    String sendRulesMessage(String text, String messageSelector) throws ExchangeMessageException;

    void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent event);

    void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginMessageEvent event);

}