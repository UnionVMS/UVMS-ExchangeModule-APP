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
package eu.europa.ec.fisheries.uvms.exchange.dao.bean;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.*;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.dao.Dao;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.search.ExchangeSearchField;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.exchange.search.SearchValue;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 **/
@Stateless
public class ExchangeLogDaoBean extends Dao implements ExchangeLogDao {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogDaoBean.class);

    @Override
    public List<ExchangeLogStatus> getExchangeLogStatusHistory(String sql, ExchangeHistoryListQuery searchQuery) throws ExchangeDaoException {
        try {
            LOG.debug("SQL query for status history " + sql);
            TypedQuery<ExchangeLogStatus> query = em.createQuery(sql, ExchangeLogStatus.class);
            if (searchQuery.getStatus() != null && !searchQuery.getStatus().isEmpty()) {
                query.setParameter("status", searchQuery.getStatus());
            }
            if (searchQuery.getType() != null && !searchQuery.getType().isEmpty()) {
                query.setParameter("type", searchQuery.getType());
            }
            if (searchQuery.getTypeRefDateFrom() != null) {
                Instant from = searchQuery.getTypeRefDateFrom().toInstant();
                query.setParameter("from", from);
            }
            if (searchQuery.getTypeRefDateTo() != null) {
                Instant to = searchQuery.getTypeRefDateTo().toInstant();
                query.setParameter("to", to);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new ExchangeDaoException("[ERROR] when getting search list ] ");
        }
    }

    @Override
    public List<ExchangeLog> getExchangeLogListPaginated(Integer page, Integer listSize, String sql, List<SearchValue> searchKeyValues) throws ExchangeDaoException {
        try {
            LOG.debug("SQL QUERY IN LIST PAGINATED: " + sql);
            TypedQuery<ExchangeLog> query = em.createQuery(sql, ExchangeLog.class);
            HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = SearchFieldMapper.combineSearchFields(searchKeyValues);
            setQueryParameters(query, orderedValues);
            query.setFirstResult(listSize * (page - 1));
            query.setMaxResults(listSize);
            return query.getResultList();
        } catch (Exception e) {
            throw new ExchangeDaoException("[ERROR] when getting list.");
        }
    }

    @Override
    public Long getExchangeLogListSearchCount(String countSql, List<SearchValue> searchKeyValues) throws ExchangeDaoException {
        LOG.debug("SQL QUERY IN LIST COUNT: " + countSql);
        TypedQuery<Long> query = em.createQuery(countSql, Long.class);
        HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = SearchFieldMapper.combineSearchFields(searchKeyValues);
        setQueryParameters(query, orderedValues);
        return query.getSingleResult();
    }

    @Override
    public ExchangeLog getExchangeLogByGuid(UUID logGuid) throws ExchangeDaoException {
        return getExchangeLogByGuid(logGuid, null);
    }

    private void setQueryParameters(Query query, HashMap<ExchangeSearchField, List<SearchValue>> orderedValues) {
        for (Map.Entry<ExchangeSearchField, List<SearchValue>> criteria : orderedValues.entrySet()) {
            if (criteria.getValue().size() > 1) {
                query.setParameter(criteria.getKey().getSQLReplacementToken(), criteria.getValue());
            } else {
               query.setParameter(criteria.getKey().getSQLReplacementToken(), SearchFieldMapper.buildValueFromClassType(criteria.getValue().get(0), criteria.getKey().getClazz()));
            }
        }
    }


    @Override
    public ExchangeLog createLog(ExchangeLog log) throws ExchangeDaoException {
        try {
            em.persist(log);
            return log;
        } catch (PersistenceException e) {
            throw new ExchangeDaoException("[ERROR] creating log.", e);
        }
    }

    @Override
    public ExchangeLog getExchangeLogByGuid(UUID logGuid, TypeRefType typeRefType) {
        try {
            TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeLog.LOG_BY_GUID, ExchangeLog.class);
            query.setParameter("typeRefType", typeRefType);
            query.setParameter("guid", logGuid);
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            // Don't need to actually do anything when no entity was found!
            // LOG.error("[ERROR] when getting entity by ID. {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<UUID> logGuids) {
        if(CollectionUtils.isEmpty(logGuids)){
            return new ArrayList<>();
        }
        TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeLog.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, ExchangeLog.class);
        query.setParameter("refGuids", logGuids);
        return query.getResultList();
    }


    @Override
    public List<ExchangeLog> getExchangeLogByTypesRefAndGuid(UUID typeRefGuid, List<TypeRefType> types) {
        try {
			TypedQuery<ExchangeLog> namedQuery = em.createNamedQuery(ExchangeLog.LOG_BY_TYPE_REF_AND_GUID, ExchangeLog.class);
            namedQuery.setParameter("typeRefGuid", typeRefGuid);
            namedQuery.setParameter("typeRefTypes", types);
            return namedQuery.getResultList();
        } catch (NoResultException e) {
            // Don't need to actually do anything when no entity was found!
            // LOG.error("[ERROR] when getting entity by ID. {}", e.getMessage());
        }
        return null;
    }

    @Override
    public ExchangeLog updateLog(ExchangeLog entity) throws ExchangeDaoException {
        try {
            em.merge(entity);
            em.flush();
            return entity;
        } catch (Exception e) {
            throw new ExchangeDaoException("[ERROR] when updating entity ]", e);
        }
    }

    @Override
    public ExchangeLog getLatestLog(){
        TypedQuery<ExchangeLog> namedQuery = em.createNamedQuery(ExchangeLog.LATEST_LOG, ExchangeLog.class);
        namedQuery.setMaxResults(1);
        return namedQuery.getResultList().get(0);
    }

}