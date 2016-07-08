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

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Local
public interface ExchangeLogService {

    public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId, String username) throws ExchangeLogException;

    public ExchangeLogType log(ExchangeLogType log, String username) throws ExchangeLogException;

    public ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException;

    public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException;

    public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException;

    public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException;

    public String createUnsentMessage(String senderReceiver, XMLGregorianCalendar timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties, String username) throws ExchangeLogException;

    public void resend(List<String> messageIdList, String username) throws ExchangeLogException;

    public ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid, String userName) throws ExchangeLogException;

    public PollStatus setPollStatus(String messageId, String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException;

    public void removeUnsentMessage(String messageId, String username) throws ExchangeLogException;
}