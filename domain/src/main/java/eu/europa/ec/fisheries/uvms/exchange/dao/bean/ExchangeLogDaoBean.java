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
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;
import eu.europa.ec.fisheries.uvms.exchange.dao.Dao;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
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
                Date from = searchQuery.getTypeRefDateFrom();
                query.setParameter("from", from);
            }
            if (searchQuery.getTypeRefDateTo() != null) {
                Date to = searchQuery.getTypeRefDateTo();
                query.setParameter("to", to);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new ExchangeDaoException("[ERROR] when getting search list ] ");
        }
    }

    @Override
    public ExchangeLog getExchangeLogByGuid(String logGuid) {
        return getExchangeLogByGuid(logGuid, null);
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
    public ExchangeLog getExchangeLogByGuid(String logGuid, TypeRefType typeRefType) {
        try {
            TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeConstants.LOG_BY_GUID, ExchangeLog.class);
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
    public List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<String> logGuids) {
        if(CollectionUtils.isEmpty(logGuids)){
            return new ArrayList<>();
        }
        TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeConstants.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, ExchangeLog.class);
        query.setParameter("refGuids", logGuids);
        return query.getResultList();
    }


    @Override
    public List<ExchangeLog> getExchangeLogByTypesRefAndGuid(String typeRefGuid, List<TypeRefType> types) {
        try {
			TypedQuery<ExchangeLog> namedQuery = em.createNamedQuery(ExchangeConstants.LOG_BY_TYPE_REF_AND_GUID, ExchangeLog.class);
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

}