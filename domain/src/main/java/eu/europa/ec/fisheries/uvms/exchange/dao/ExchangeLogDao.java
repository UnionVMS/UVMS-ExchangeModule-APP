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
package eu.europa.ec.fisheries.uvms.exchange.dao;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchValue;

/**
 **/
@Local
public interface ExchangeLogDao {

    ExchangeLog createLog(ExchangeLog log) throws ExchangeDaoException;

    List<ExchangeLog> getExchangeLogListPaginated(Integer page, Integer listSize, String sql, List<SearchValue> searchKeyValues) throws ExchangeDaoException;

    Long getExchangeLogListSearchCount(String countSql, List<SearchValue> searchKeyValues) throws ExchangeDaoException;

	ExchangeLog getExchangeLogByGuid(String logGuid) throws NoEntityFoundException, ExchangeDaoException;

	ExchangeLog getExchangeLogByTypeRefAndGuid(String typeRefGuid, TypeRefType type) throws NoEntityFoundException, ExchangeDaoException;
	
	ExchangeLog updateLog(ExchangeLog exchangeLog) throws ExchangeDaoException;
	
	List<ExchangeLogStatus> getExchangeLogStatusHistory(String sql, ExchangeHistoryListQuery query) throws ExchangeDaoException;

	List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<String> guids);
}