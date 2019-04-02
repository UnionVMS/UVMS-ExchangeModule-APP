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
package eu.europa.ec.fisheries.uvms.exchange.service.message.producer;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.PluginErrorEventCarrier;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import javax.ejb.Local;
import javax.jms.TextMessage;

@Local
public interface ExchangeMessageProducer {      //leave be for now ;(

    String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException;

    String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException;
    
    void sendModuleResponseMessage(TextMessage message, String text) throws MessageException;

    String sendRulesMessage(String text) throws ConfigMessageException;

    String sendRulesMessage(String text, String messageSelector) throws ExchangeMessageException;
    
    String sendMovementMessage(String text, String groupId) throws ExchangeMessageException;

    void sendModuleErrorResponseMessage(ExchangeErrorEvent event);

    void sendPluginErrorResponseMessage(PluginErrorEventCarrier event);

    void sendModuleAckMessage(String messageId, MessageQueue queue, String text);

    String forwardToAsset(String text, String function) throws ExchangeMessageException;
}