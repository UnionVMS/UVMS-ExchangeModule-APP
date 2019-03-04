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
package eu.europa.ec.fisheries.uvms.exchange.model.remote;

import javax.ejb.Remote;
import java.util.List;
import java.util.Set;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogWithRawMsgAndType;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;

/**
 **/
@Remote
public interface ExchangeLogModel {

    ListResponseDto getExchangeLogListByQuery(ExchangeListQuery query) throws ExchangeModelException;

    ExchangeLogType createExchangeLog(ExchangeLogType log, String username) throws ExchangeModelException;
    
    ExchangeLogType updateExchangeLogStatus(ExchangeLogStatusType status, String username) throws ExchangeModelException;
    
    ExchangeLogStatusType getExchangeLogStatusHistory(String guid, TypeRefType typeRefType) throws ExchangeModelException;

    List<ExchangeLogStatusType> getExchangeLogsStatusHistories(String guid, List<TypeRefType> typeRefType) throws ExchangeModelException;

    List<ExchangeLogStatusType> getExchangeLogStatusHistoryByQuery(ExchangeHistoryListQuery query) throws ExchangeModelException;

	ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeModelException;

    ExchangeLogType getExchangeLogByGuidAndType(String guid, TypeRefType typeRefType) throws ExchangeModelException;

    Set<ExchangeLogType> getExchangeLogByRefUUIDAndType(String refUUID, TypeRefType typeRefType) throws ExchangeModelException;

    ExchangeLogType setPollStatus(PollStatus pollStatus, String username) throws ExchangeModelException;

    LogWithRawMsgAndType getExchangeLogRawXmlByGuid(String guid);
}