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

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;

@Local
public interface ExchangeLogService {

    ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId, String username) throws ExchangeLogException;

    ExchangeLogType log(ExchangeLogType log, String username) throws ExchangeLogException;

    /**
     * Create a new log entry.
     * @param request TODO STIJN: describe
     * @param logType TODO STIJN: describe
     * @param status TODO STIJN: describe
     * @param messageType TODO STIJN: describe
     * @param messageText TODO STIJN: describe
     * @param incoming TODO STIJN: describe
     * @return the created log entry
     */
    ExchangeLogType log(ExchangeBaseRequest request, LogType logType, ExchangeLogStatusTypeType status, TypeRefType messageType, String messageText, boolean incoming) throws ExchangeLogException;

    ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException;

    List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException;

    List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException;

    ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException;

    String createUnsentMessage(String senderReceiver, Date timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties, String username) throws ExchangeLogException;

    void resend(List<String> messageIdList, String username) throws ExchangeLogException;

    ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid, String userName) throws ExchangeLogException;

    PollStatus setPollStatus(String messageId, String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    void removeUnsentMessage(String messageId, String username) throws ExchangeLogException;

}