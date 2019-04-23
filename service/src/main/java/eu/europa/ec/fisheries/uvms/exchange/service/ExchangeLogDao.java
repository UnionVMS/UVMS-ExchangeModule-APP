/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.constant.ExchangeConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExchangeLogDao extends AbstractDAO<ExchangeLog> {

    private EntityManager em;

    public ExchangeLogDao(EntityManager em) {
        this.em = em;
    }

    public Long count(Map<String, Object> queryParameters) {
        TypedQuery<Long> query = getEntityManager().createNamedQuery(ExchangeLog.COUNT_LIST_EXCHANGE, Long.class);
        for (Map.Entry<String, Object> entry : queryParameters.entrySet()){
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getSingleResult();

    }

    public ExchangeLog getExchangeLogByGuid(String logGuid) {
        try {
            TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeConstants.LOG_BY_GUID, ExchangeLog.class);
            query.setParameter("typeRefType", null);
            query.setParameter("guid", logGuid);
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    public List<ExchangeLog> list(Map<String, Object> queryParameters, String sortingColumn, boolean order, Integer firstResult, Integer maxResult) {
        List resultList = new ArrayList();
        if (MapUtils.isEmpty(queryParameters) || sortingColumn == null || firstResult == null || maxResult == null){
            return  resultList;
        }
        try {
            String queryString = em.createNamedQuery(ExchangeLog.LIST_EXCHANGE).unwrap(org.hibernate.Query.class).getQueryString();
            StringBuilder builder = new StringBuilder(queryString).append(" ORDER BY e.").append(sortingColumn).append(order ? " ASC" : " DESC");
            log.debug(builder.toString());
            Query selectQuery = getEntityManager().createQuery(builder.toString());
            for (Map.Entry<String, Object> entry : queryParameters.entrySet()){
                selectQuery.setParameter(entry.getKey(), entry.getValue());
            }
            selectQuery.setFirstResult(firstResult);
            selectQuery.setMaxResults(maxResult);
            resultList = selectQuery.getResultList();

        }
        catch (Exception e){
            log.error(e.getLocalizedMessage(),e);
        }
        return resultList;
    }

    public List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<String> logGuids) {
        if(CollectionUtils.isEmpty(logGuids)){
            return new ArrayList<>();
        }
        TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeConstants.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, ExchangeLog.class);
        query.setParameter("refGuids", logGuids);
        return query.getResultList();
    }

    @Override public EntityManager getEntityManager() {
        return em;
    }
}
