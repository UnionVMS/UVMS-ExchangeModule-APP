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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.ParseException;
import java.util.*;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.mapper.LogMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 **/
@Stateless
public class ExchangeLogModelBean {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogModelBean.class);

    @EJB
    private ExchangeLogDaoBean logDao;

    public ExchangeLogType getExchangeLogByGuid(UUID guid) {
        ExchangeLogType exchangeLogType;
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(guid, null);
            exchangeLogType = LogMapper.toModel(exchangeLog);
            // Enriches the "first level logs" with info related to the related logs.
            enrichDtosWithRelatedLogsInfo(Collections.singletonList(exchangeLogType));
        } catch (Exception e) {
            LOG.error("[ERROR] when getting exchange log by GUID. {} {}", guid, e.getMessage());
            exchangeLogType = null;
        }
        return exchangeLogType;
    }

    public ExchangeLogType getExchangeLogByGuidAndType(UUID guid, TypeRefType typeRefType) {
        try {
            ExchangeLog exchangeLogByGuid = logDao.getExchangeLogByGuid(guid, typeRefType);
            return LogMapper.toModel(exchangeLogByGuid);
        } catch (Exception e) {
            LOG.error("[ERROR] when getting exchange log by GUID. {}] {}", guid, e.getMessage());
        }
        return null;
    }

    public Set<ExchangeLogType> getExchangeLogByRefUUIDAndType(UUID refUUID, TypeRefType typeRefType) {
        Set<ExchangeLogType> exchangeLogTypeSet = new HashSet<>();
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(refUUID, Collections.singletonList(typeRefType));
            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                for (ExchangeLog exchangeLog : exchangeLogByTypesRefAndGuid) {
                    exchangeLogTypeSet.add(LogMapper.toModel(exchangeLog));
                }
            }
        } catch (Exception e) {
            LOG.error("[ERROR] when getting exchange log by refUUID. {}] {}", refUUID, e.getMessage());
            exchangeLogTypeSet = null;
        }
        return exchangeLogTypeSet;
    }

    public ListResponseDto getExchangeLogListByQuery(ExchangeListQuery query) throws ExchangeModelException {
        if (query == null) {
            throw new IllegalArgumentException("Exchange list query is null");
        }
        if (query.getPagination() == null) {
            throw new IllegalArgumentException("Pagination in Exchange query is null");
        }
        if (query.getExchangeSearchCriteria() == null) {
            throw new IllegalArgumentException("No search criterias in Exchange query");
        }
        try {
            ListResponseDto response = new ListResponseDto();
            List<ExchangeLogType> exchLogTypes = new ArrayList<>();

            Integer page = query.getPagination().getPage();
            Integer listSize = query.getPagination().getListSize();

            List<SearchValue> searchKeyValues = SearchFieldMapper.mapSearchField(query.getExchangeSearchCriteria().getCriterias());

            String sql = SearchFieldMapper.createSelectSearchSql(searchKeyValues, true, query.getSorting());
            LOG.debug("sql:" + sql);
            String countSql = SearchFieldMapper.createCountSearchSql(searchKeyValues, true);
            LOG.debug("countSql:" + countSql);
            Long numberMatches = logDao.getExchangeLogListSearchCount(countSql, searchKeyValues);

            List<ExchangeLog> exchangeLogEntityList = logDao.getExchangeLogListPaginated(page, listSize, sql, searchKeyValues);
            for (ExchangeLog entity : exchangeLogEntityList) {
                exchLogTypes.add(LogMapper.toModel(entity));
            }

            // Enriches the "first level logs" with info related to the related logs.
            enrichDtosWithRelatedLogsInfo(exchLogTypes);

            int numberOfPages = (int) (numberMatches / listSize);
            if (numberMatches % listSize != 0) {
                numberOfPages += 1;
            }

            response.setTotalNumberOfPages(numberOfPages);
            response.setCurrentPage(query.getPagination().getPage());
            response.setExchangeLogList(exchLogTypes);
            return response;
        } catch (ExchangeDaoException | ParseException ex) {
            LOG.error("[ERROR] when getting ExchangeLogs by query {}] {} ", query, ex.getMessage());
            throw new ExchangeModelException(ex.getMessage(), ex);
        }
    }

    private void enrichDtosWithRelatedLogsInfo(List<ExchangeLogType> exchangeLogList) {
        List<UUID> guids = new ArrayList<>();
        for (ExchangeLogType log : exchangeLogList) {
            guids.add(UUID.fromString(log.getGuid()));
        }
        List<ExchangeLog> relatedLogs = logDao.getExchangeLogByRangeOfRefGuids(guids);
        if (CollectionUtils.isNotEmpty(relatedLogs)) {
            for (ExchangeLog logEntity : relatedLogs) {
                RelatedLogInfo refLogInfo = new RelatedLogInfo();
                refLogInfo.setGuid(logEntity.getId().toString());
                refLogInfo.setType(logEntity.getTypeRefType().toString());
                for (ExchangeLogType logType : exchangeLogList) {
                    if (StringUtils.equals(logEntity.getTypeRefGuid().toString(), logType.getGuid())) {
                        logType.getRelatedLogData().add(refLogInfo);
                    }
                }
            }
        }
    }

    public List<ExchangeLogStatusType> getExchangeLogStatusHistoryByQuery(ExchangeHistoryListQuery query) throws ExchangeModelException {
        if (query == null) {
            throw new IllegalArgumentException("Exchange status list query is null");
        }
        try {
            List<ExchangeLogStatusType> logStatusHistoryList = new ArrayList<>();

            String sql = SearchFieldMapper.createSearchSql(query);
            List<ExchangeLogStatus> logList = logDao.getExchangeLogStatusHistory(sql, query);

            for (ExchangeLogStatus log : logList) {
                ExchangeLogStatusType statusType = LogMapper.toStatusModel(log.getLog());
                logStatusHistoryList.add(statusType);
            }

            return logStatusHistoryList;
        } catch (ExchangeDaoException e) {
            LOG.error("[ERROR] when get Exchange log status history {}] {} ", query, e.getMessage());
            throw new ExchangeModelException("Error when get Exchange log status history ");
        }
    }

    public ExchangeLog createExchangeLog(ExchangeLog log, String username) throws ExchangeModelException {
        if (log == null) {
            throw new IllegalArgumentException("No logType to create");
        }
        if (log.getType() == null) {
            throw new IllegalArgumentException("No type in logType to create");
        }
        try {
            return logDao.createLog(log);
        } catch (ExchangeDaoException ex) {
            LOG.error("[ERROR] when creating Exchange logType {} {}] {}", log, username, ex.getMessage());
            throw new ExchangeModelException("Error when creating Exchange logType ", ex);
        }
    }


    public ExchangeLog updateExchangeLogStatus(ExchangeLogStatus status, String username, UUID logId) throws ExchangeModelException {
        if (status == null || logId == null ) {
            throw new IllegalArgumentException("No exchange log to update status");
        }
        if (status.getStatus() == null) {
            throw new IllegalArgumentException("Non valid status to update to");
        }
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(logId);
            status.setLog(exchangeLog);
            exchangeLog.setStatus(status.getStatus());
            exchangeLog.getStatusHistory().add(status);
            exchangeLog.setUpdatedBy(username);

            ExchangeLog retEntity = logDao.updateLog(exchangeLog);
            return retEntity;
        } catch (ExchangeDaoException ex) {
            LOG.error("[ERROR] when update status of Exchange log {} {}] {}", status, logId, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log", ex);
        }
    }

    public ExchangeLogType updateExchangeLogBusinessError(ExchangeLogStatusType status, String businessError) throws ExchangeModelException {
        if (status == null || status.getGuid() == null || status.getGuid().isEmpty()) {
            throw new IllegalArgumentException("No exchange log to update status");
        }
        try {
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(UUID.fromString(status.getGuid()));
            exchangeLog.setBusinessError(businessError);
            ExchangeLog retEntity = logDao.updateLog(exchangeLog);
            return LogMapper.toModel(retEntity);
        } catch (ExchangeDaoException ex) {
            LOG.error("[ERROR] when update status of Exchange log {}] {}", status, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log", ex);
        }
    }

    public ExchangeLogStatusType getExchangeLogStatusHistory(UUID guid, TypeRefType typeRefType) throws ExchangeModelException {
        if (guid == null) {
            throw new IllegalArgumentException("Non valid guid to fetch log status history");
        }

        ExchangeLog returnLog = null;
        if (typeRefType == null || TypeRefType.UNKNOWN.equals(typeRefType)) {
            returnLog = logDao.getExchangeLogByGuid(guid);
        } else {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(guid, Arrays.asList(typeRefType));
            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                returnLog = exchangeLogByTypesRefAndGuid.get(0);
            }
        }
        return LogMapper.toStatusModel(returnLog);
    }

    public List<ExchangeLogStatusType> getExchangeLogsStatusHistories(UUID guid, List<TypeRefType> typeRefType) {
        List<ExchangeLogStatusType> logStatusTypeList = new ArrayList<>();
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(guid, typeRefType);
            if (CollectionUtils.isNotEmpty(exchangeLogByTypesRefAndGuid)) {
                for (ExchangeLog log : exchangeLogByTypesRefAndGuid) {
                    logStatusTypeList.add(LogMapper.toStatusModel(log));
                }
            }
        } catch (Exception e) {
            LOG.error("[ERROR] when getting status history Exchange log {} {}] {}", guid, typeRefType, e.getMessage());
            return logStatusTypeList;
        }
        return logStatusTypeList;
    }

    public ExchangeLogType setPollStatus(PollStatus pollStatus, String username) throws ExchangeModelException {
        ExchangeLogType logType = null;
        if (pollStatus == null || pollStatus.getPollGuid() == null) {
            throw new IllegalArgumentException("No poll id to update status");
        }
        try {
            List<ExchangeLog> exchangeLogByTypesRefAndGuid = logDao.getExchangeLogByTypesRefAndGuid(UUID.fromString(pollStatus.getPollGuid()), Collections.singletonList(TypeRefType.POLL));
            for(ExchangeLog log : exchangeLogByTypesRefAndGuid){
                List<ExchangeLogStatus> statusList = log.getStatusHistory();
                statusList.add(LogMapper.toNewStatusEntity(log, pollStatus.getStatus(), username));
                log.setStatus(pollStatus.getStatus());
                ExchangeLog retEntity = logDao.updateLog(log);
                logType = LogMapper.toModel(retEntity);
            }
        } catch (ExchangeDaoException ex) {
            LOG.error("[ERROR] when set poll status {} {}] {}", pollStatus, username, ex.getMessage());
            throw new ExchangeModelException("Error when update status of Exchange log ");
        }
        return logType;
    }

    public LogWithRawMsgAndType getExchangeLogRawXmlByGuid(UUID guid) {     //
        LogWithRawMsgAndType logWrapper = new LogWithRawMsgAndType();
            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(guid);
            if (exchangeLog != null){
                String rawMsg = exchangeLog.getTypeRefMessage();
                logWrapper.setRawMsg(rawMsg);
                logWrapper.setType(exchangeLog.getTypeRefType());
                logWrapper.setRefGuid(exchangeLog.getTypeRefGuid().toString());
            }
        return logWrapper;
    }
}