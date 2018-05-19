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
import java.util.Date;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogWithValidationResults;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogWithRawMsgAndType;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypeProperty;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Local
public interface ExchangeLogService {

    ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId, String username) throws ExchangeLogException;

    ExchangeLogType log(ExchangeLogType log, String username) throws ExchangeLogException;

    /**
     * Create a new log entry.
     * @param request the incoming exchange request
     * @param logType the type of the log
     * @param status the status of the message (does it needs to be validated, is it valid, ...)
     * @param messageType the type of the message
     * @param messageText XML representation of the incoming/outgoing message
     * @param incoming is this an incoming message (then true) or an outgoing message (then false)?
     * @return the created log entry
     */
    ExchangeLogType log(ExchangeBaseRequest request, LogType logType, ExchangeLogStatusTypeType status, TypeRefType messageType, String messageText, boolean incoming) throws ExchangeLogException;

    ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    /**
     * Adds a new log status to a log with the specified log guid.
     *
     * Since the guid is not something that an end user will have to, this method is assumed to be used by the system.
     * Therefore, the logged username will be "SYSTEM".
     * 
     * @param logGuid guid of the log. Notice that this is NOT the internal id.
     * @param logStatus the next status
     * @return the updated log
     * @throws ExchangeLogException when something goes wrong
     */
    ExchangeLogType updateStatus(String logGuid, ExchangeLogStatusTypeType logStatus, Boolean duplicate) throws ExchangeLogException;

    List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException;

    List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException;

    String createUnsentMessage(String senderReceiver, Date timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties, String username) throws ExchangeLogException;

    void resend(List<String> messageIdList, String username) throws ExchangeLogException;

    ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid, String userName) throws ExchangeLogException;

    PollStatus setPollStatus(String messageId, String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    void removeUnsentMessage(String messageId, String username) throws ExchangeLogException;

    ExchangeLogWithValidationResults getExchangeLogRawMessageAndValidationByGuid(String guid, LogWithRawMsgAndType rawMsg) throws ExchangeLogException;

}