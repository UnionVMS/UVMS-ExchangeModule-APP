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
package eu.europa.ec.fisheries.uvms.exchange.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.mapper.LogMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeSearchMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.exchange.model.remote.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.ParseException;
import java.util.*;

/**
 **/
@Stateless
public class ExchangeLogModelBean implements ExchangeLogModel {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeLogModelBean.class);

    @EJB
    private ExchangeLogDao logDao;


    @Override
    public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeModelException {
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(guid);
            ExchangeLogType exchangeLogType = LogMapper.toModel(exchangeLog);

            // Enriches the "first level logs" with info related to the related logs.
            enrichDtosWithRelatedLogsInfo(Collections.singletonList(exchangeLogType));
            return exchangeLogType;
        } catch (ExchangeDaoException e) {
            LOG.error("[ Error when getting exchange log by GUID. {}] {}",guid, e.getMessage());
            throw new ExchangeModelException("Error when getting exchange log by GUID.");
        }
    }


    @Override
    public ListResponseDto getExchangeLogListByQuery(ExchangeListQuery query) throws ExchangeModelException {
        if (query == null) {
            throw new InputArgumentException("Exchange list query is null");
        }
        if (query.getPagination() == null) {
            throw new InputArgumentException("Pagination in Exchange query is null");
        }
        if (query.getExchangeSearchCriteria() == null) {
            throw new InputArgumentException("No search criterias in Exchange query");
        }
        try {
            ListResponseDto response = new ListResponseDto();
            List<ExchangeLogType> exchLogTypes = new ArrayList<>();

            Integer page = query.getPagination().getPage();
            Integer listSize = query.getPagination().getListSize();

            List<SearchValue> searchKeyValues = SearchFieldMapper.mapSearchField(query.getExchangeSearchCriteria().getCriterias());
            
            String sql = SearchFieldMapper.createSelectSearchSql(searchKeyValues, true,query.getSorting());
            LOG.info("sql:"+sql);
            String countSql = SearchFieldMapper.createCountSearchSql(searchKeyValues, true);
            LOG.info("countSql:"+countSql);
            Long numberMatches = logDao.getExchangeLogListSearchCount(countSql, searchKeyValues);
            
            List<ExchangeLog> exchangeLogEntityList = logDao.getExchangeLogListPaginated(page, listSize, sql, searchKeyValues);
            for (ExchangeLog entity : exchangeLogEntityList) {
                exchLogTypes.add(LogMapper.toModel(entity));
            }

            // Enriches the "first level logs" with info related to the related logs.
            enrichDtosWithRelatedLogsInfo(exchLogTypes);

            int numberOfPages = (int) ( numberMatches / listSize);
            if (numberMatches % listSize != 0) {
                numberOfPages += 1;
            }

            response.setTotalNumberOfPages(numberOfPages);
            response.setCurrentPage(query.getPagination().getPage());
            response.setExchangeLogList(exchLogTypes);
            return response;
        } catch (ExchangeSearchMapperException | ExchangeDaoException | ParseException ex) {
            LOG.error("[ Error when getting ExchangeLogs by query {}] {} ",query, ex.getMessage());
            throw new ExchangeModelException(ex.getMessage());
        }
    }

    private void enrichDtosWithRelatedLogsInfo(List<ExchangeLogType> exchangeLogList) {
        List<String> guids = new ArrayList<>();
        for(ExchangeLogType log : exchangeLogList){
            guids.add(log.getGuid());
        }
        List<ExchangeLog> relatedLogs= logDao.getExchangeLogByRangeOfRefGuids(guids);
        if(CollectionUtils.isNotEmpty(relatedLogs)){
            for(ExchangeLog logEntity : relatedLogs){
                RelatedLogInfo refLogInfo = new RelatedLogInfo();
                refLogInfo.setGuid(logEntity.getGuid());
                refLogInfo.setType(logEntity.getTypeRefType().toString());
                for(ExchangeLogType logType : exchangeLogList){
                    if(StringUtils.equals(logEntity.getTypeRefGuid(), logType.getGuid())){
                        logType.getRelatedLogData().add(refLogInfo);
                    }
                }
            }
        }
    }

    @Override
	public List<ExchangeLogStatusType> getExchangeLogStatusHistoryByQuery(ExchangeHistoryListQuery query) throws ExchangeModelException {
        if (query == null) {
            throw new InputArgumentException("Exchange status list query is null");
        }
        
        try {
        	List<ExchangeLogStatusType> logStatusHistoryList = new ArrayList<>();
        
        	String sql = SearchFieldMapper.createSearchSql(query);
        	List<ExchangeLogStatus> logList = logDao.getExchangeLogStatusHistory(sql, query);
        	
        	Set<String> uniqueExchangeLogGuid = new HashSet<>();
            Map<String, TypeRefType> logTypeMap = new HashMap<>();
        	for(ExchangeLogStatus log : logList) {
        		uniqueExchangeLogGuid.add(log.getLog().getGuid());
                logTypeMap.put(log.getLog().getGuid(), log.getLog().getTypeRefType());
        	}

        	//TODO not two db-calls?
        	for(String guid : uniqueExchangeLogGuid) {
                ExchangeLog log = logDao.getExchangeLogByGuid(guid);
                ExchangeLogStatusType statusType = LogMapper.toStatusModel(log);
                logStatusHistoryList.add(statusType);
        	}
        	
        	return logStatusHistoryList;
        } catch (ExchangeDaoException e) {
        	LOG.error("[ Error when get Exchange log status history {}] {} ",query, e.getMessage());
            throw new ExchangeModelException("Error when get Exchange log status history ");
        }
	}
    
    @Override
    public ExchangeLogType createExchangeLog(ExchangeLogType log, String username) throws ExchangeModelException {
    	if(log == null) {
    		throw new InputArgumentException("No log to create");
    	}
    	if(log.getType() == null) {
    		throw new InputArgumentException("No type in log to create");
    	}
    	
        try {
            ExchangeLog exchangeLog = LogMapper.toNewEntity(log, username);
            ExchangeLog persistedLog = logDao.createLog(exchangeLog);
            return LogMapper.toModel(persistedLog);
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when creating Exchange log {} {}] {}",log,username, ex.getMessage());
            throw new ExchangeModelException("Error when creating Exchange log ");
        }
    }


    @Override
	public ExchangeLogType updateExchangeLogStatus(ExchangeLogStatusType status, String username) throws ExchangeModelException {
		if(status == null || status.getGuid() == null || status.getGuid().isEmpty()) {
			throw new InputArgumentException("No exchange log to update status");
		}
		if(status.getHistory() == null || status.getHistory().isEmpty() || status.getHistory().size() != 1) {
			throw new InputArgumentException("Non valid status to update to");
		}
		try {
			ExchangeLogStatusHistoryType updateStatus = status.getHistory().get(0);
			ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(status.getGuid());
			List<ExchangeLogStatus> statusList = exchangeLog.getStatusHistory();
			statusList.add(LogMapper.toNewStatusEntity(exchangeLog, updateStatus.getStatus(), username));
			exchangeLog.setStatus(updateStatus.getStatus());
			ExchangeLog retEntity = logDao.updateLog(exchangeLog);
			ExchangeLogType retType = LogMapper.toModel(retEntity);
			return retType;
		} catch (ExchangeDaoException ex) {
			LOG.error("[ Error when update status of Exchange log {} {}] {}",status,username, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log", ex);
		}
	}

	@Override
	public ExchangeLogStatusType getExchangeLogStatusHistory(String guid, TypeRefType typeRefType) throws ExchangeModelException {
		if(guid == null || guid.isEmpty()) throw new InputArgumentException("Non valid guid to fetch log status history");
		try {
			if(typeRefType == null || TypeRefType.UNKNOWN.equals(typeRefType)) {
				return LogMapper.toStatusModel(logDao.getExchangeLogByGuid(guid));
			} else {
				return LogMapper.toStatusModel(logDao.getExchangeLogByTypeRefAndGuid(guid, typeRefType));
			}
		} catch (ExchangeDaoException e) {
			LOG.error("[ Error when getting status history Exchange log {} {}] {}",guid,typeRefType, e.getMessage());
            throw new ExchangeModelException("Error when getting status history of Exchange log ");
		}
	}


    @Override
    public ExchangeLogType setPollStatus(PollStatus pollStatus, String username)throws ExchangeModelException {
        if(pollStatus == null || pollStatus.getPollGuid() == null){
            throw new InputArgumentException("No poll id to update status");
        }

        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByTypeRefAndGuid(pollStatus.getPollGuid(), TypeRefType.POLL);
            List<ExchangeLogStatus> statusList = exchangeLog.getStatusHistory();
            statusList.add(LogMapper.toNewStatusEntity(exchangeLog, pollStatus.getStatus(), username));
            exchangeLog.setStatus(pollStatus.getStatus());
            ExchangeLog retEntity = logDao.updateLog(exchangeLog);
            ExchangeLogType retType = LogMapper.toModel(retEntity);
            return retType;
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when set poll status {} {}] {}",pollStatus,username, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log ");
        }
    }
}