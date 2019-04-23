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
package eu.europa.ec.fisheries.uvms.exchange.service.domain.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.mapper.LogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.search.SearchFieldMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

/**
 **/
@Stateless
@Slf4j
public class ExchangeLogModelBean implements ExchangeLogModel {
    
    @EJB
    private ExchangeLogDao logDao;

    @Override
    public ExchangeLogType getExchangeLogByGuid(String guid) {
        ExchangeLogType exchangeLogType;
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(guid, null);
            exchangeLogType = LogMapper.toModel(exchangeLog);
        } catch (Exception e) {
            log.error("[ERROR] when getting exchange log by GUID. {} {}", guid, e.getMessage());
            exchangeLogType = null;
        }
        return exchangeLogType;
    }

    @Override
    public ExchangeLogType getExchangeLogByGuidAndType(String guid, TypeRefType typeRefType) {
        try {
            ExchangeLog exchangeLogByGuid = logDao.getExchangeLogByGuid(guid, typeRefType);
            return LogMapper.toModel(exchangeLogByGuid);
        } catch (Exception e) {
            log.error("[ERROR] when getting exchange log by GUID. {}] {}", guid, e.getMessage());
        }
        return null;
    }

    @Override
    public Set<ExchangeLogType> getExchangeLogByRefUUIDAndType(String refUUID, TypeRefType typeRefType) {
        Set<ExchangeLogType> exchangeLogTypeSet = new HashSet<>();
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(refUUID, Collections.singletonList(typeRefType));
            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                for (ExchangeLog exchangeLog : exchangeLogByTypesRefAndGuid) {
                    exchangeLogTypeSet.add(LogMapper.toModel(exchangeLog));
                }
            }
        } catch (Exception e) {
            log.error("[ERROR] when getting exchange log by refUUID. {}] {}", refUUID, e.getMessage());
            exchangeLogTypeSet = null;
        }
        return exchangeLogTypeSet;
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
            for (ExchangeLogStatus log : logList) {
                uniqueExchangeLogGuid.add(log.getLog().getGuid());
                logTypeMap.put(log.getLog().getGuid(), log.getLog().getTypeRefType());
            }

            //TODO not two db-calls?
            for (String guid : uniqueExchangeLogGuid) {
                ExchangeLog log = logDao.getExchangeLogByGuid(guid);
                ExchangeLogStatusType statusType = LogMapper.toStatusModel(log);
                logStatusHistoryList.add(statusType);
            }

            return logStatusHistoryList;
        } catch (ExchangeDaoException e) {
            log.error("[ERROR] when get Exchange log status history {}] {} ", query, e.getMessage());
            throw new ExchangeModelException("Error when get Exchange log status history ");
        }
    }

    @Override
    public ExchangeLogType createExchangeLog(ExchangeLogType logType, String username) throws ExchangeModelException {
        if (logType == null) {
            throw new InputArgumentException("No logType to create");
        }
        if (logType.getType() == null) {
            throw new InputArgumentException("No type in logType to create");
        }
        try {
            ExchangeLog exchangeLog = LogMapper.toNewEntity(logType, username);
            ExchangeLog persistedLog = logDao.createLog(exchangeLog);
            return LogMapper.toModel(persistedLog);
        } catch (ExchangeDaoException ex) {
            log.error("[ERROR] when creating Exchange logType {} {}] {}", logType, username, ex.getMessage());
            throw new ExchangeModelException("Error when creating Exchange logType ");
        }
    }


    @Override
    public ExchangeLogType updateExchangeLogStatus(ExchangeLogStatusType status, String username) throws ExchangeModelException {
        if (status == null || status.getGuid() == null || status.getGuid().isEmpty()) {
            throw new InputArgumentException("No exchange log to update status");
        }
        if (status.getHistory() == null || status.getHistory().isEmpty() || status.getHistory().size() != 1) {
            throw new InputArgumentException("Non valid status to update to");
        }
        try {
            ExchangeLogStatusHistoryType updateStatus = status.getHistory().get(0);
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(status.getGuid());
            List<ExchangeLogStatus> statusList = exchangeLog.getStatusHistory();
            statusList.add(LogMapper.toNewStatusEntity(exchangeLog, updateStatus.getStatus(), username));
            exchangeLog.setStatus(updateStatus.getStatus());
            ExchangeLog retEntity = logDao.updateLog(exchangeLog);
            return LogMapper.toModel(retEntity);
        } catch (ExchangeDaoException ex) {
            log.error("[ERROR] when update status of Exchange log {} {}] {}", status, username, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log", ex);
        }
    }

    @Override
    public ExchangeLogType updateExchangeLogBusinessError(ExchangeLogStatusType status, String businessError) throws ExchangeModelException {
        if (status == null || status.getGuid() == null || status.getGuid().isEmpty()) {
            throw new InputArgumentException("No exchange log to update status");
        }
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(status.getGuid());
            exchangeLog.setBusinessError(businessError);
            ExchangeLog retEntity = logDao.updateLog(exchangeLog);
            return LogMapper.toModel(retEntity);
        } catch (ExchangeDaoException ex) {
            log.error("[ERROR] when update status of Exchange log {}] {}", status, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log", ex);
        }
    }

    @Override
    public ExchangeLogStatusType getExchangeLogStatusHistory(String guid, TypeRefType typeRefType) throws ExchangeModelException {
        if (guid == null || guid.isEmpty())
            throw new InputArgumentException("Non valid guid to fetch log status history");
        try {
            if (typeRefType == null || TypeRefType.UNKNOWN.equals(typeRefType)) {
                return LogMapper.toStatusModel(logDao.getExchangeLogByGuid(guid));
            } else {
                List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(guid, Arrays.asList(typeRefType));
                if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                    return LogMapper.toStatusModel(exchangeLogByTypesRefAndGuid.get(0));
                }
            }
        } catch (ExchangeDaoException e) {
            log.error("[ERROR] when getting status history Exchange log {} {}] {}", guid, typeRefType, e.getMessage());
            throw new ExchangeModelException("Error when getting status history of Exchange log ");
        }
        return null;
    }

    @Override
    public List<ExchangeLogStatusType> getExchangeLogsStatusHistories(String guid, List<TypeRefType> typeRefType) {
        List<ExchangeLogStatusType> logStatusTypeList = new ArrayList<>();
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(guid, typeRefType);
            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                for (ExchangeLog log : exchangeLogByTypesRefAndGuid) {
                    logStatusTypeList.add(LogMapper.toStatusModel(log));
                }
            }
        } catch (Exception e) {
            log.error("[ERROR] when getting status history Exchange log {} {}] {}", guid, typeRefType, e.getMessage());
            return logStatusTypeList;
        }
        return logStatusTypeList;
    }

    @Override
    public ExchangeLogType setPollStatus(PollStatus pollStatus, String username) throws ExchangeModelException {
        ExchangeLogType logType = null;
        if (pollStatus == null || pollStatus.getPollGuid() == null) {
            throw new InputArgumentException("No poll id to update status");
        }
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(pollStatus.getPollGuid(), Collections.singletonList(TypeRefType.POLL));

            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                List<ExchangeLogStatus> statusList = exchangeLogByTypesRefAndGuid.get(0).getStatusHistory();
                statusList.add(LogMapper.toNewStatusEntity(exchangeLogByTypesRefAndGuid.get(0), pollStatus.getStatus(), username));
                exchangeLogByTypesRefAndGuid.get(0).setStatus(pollStatus.getStatus());
                ExchangeLog retEntity = logDao.updateLog(exchangeLogByTypesRefAndGuid.get(0));
                logType = LogMapper.toModel(retEntity);
            }
        } catch (ExchangeDaoException ex) {
            log.error("[ERROR] when set poll status {} {}] {}", pollStatus, username, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log ");
        }
        return logType;
    }

    @Override
    public LogWithRawMsgAndType getExchangeLogRawXmlByGuid(String guid) {
        LogWithRawMsgAndType logWrapper = new LogWithRawMsgAndType();
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(guid);
            if (exchangeLog != null){
                logWrapper.setRawMsg(exchangeLog.getTypeRefMessage());
                logWrapper.setType(exchangeLog.getTypeRefType());
                logWrapper.setRefGuid(exchangeLog.getTypeRefGuid());
                logWrapper.setDataFlow(exchangeLog.getDf());
            }
        } catch (ExchangeDaoException e) {
            log.error("[ERROR] Couldn't find Log with the following GUID : [[" + guid + "]]", e);
        }
        return logWrapper;
    }
}