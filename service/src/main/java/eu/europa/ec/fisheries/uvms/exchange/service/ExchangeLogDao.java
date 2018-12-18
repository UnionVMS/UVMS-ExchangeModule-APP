/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import eu.europa.ec.fisheries.uvms.commons.service.dao.AbstractDAO;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ColumnType;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.DirectionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

@Slf4j
public class ExchangeLogDao extends AbstractDAO<ExchangeLog> {

    private EntityManager em;

    public ExchangeLogDao(EntityManager em) {
        this.em = em;
    }

    public Long count(@NotNull Map<String, Object> queryParameters) {
        TypedQuery<Long> query = getEntityManager().createNamedQuery(ExchangeLog.COUNT_LIST_EXCHANGE, Long.class);
        for (Map.Entry<String, Object> entry : queryParameters.entrySet()){
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getSingleResult();

    }

    public List<ExchangeLog> list(@NotNull Map<String, Object> queryParameters, @NotNull Map<ColumnType, DirectionType> orderBy, @NotNull Integer firstResult, @NotNull Integer maxResult) {
        List resultList = null;
        try {
            String queryString = em.createNamedQuery(ExchangeLog.LIST_EXCHANGE).unwrap(org.hibernate.Query.class).getQueryString();
            log.info(queryString);
            StringBuilder builder = new StringBuilder(queryString).append(" ORDER BY s.");
            if (MapUtils.isNotEmpty(orderBy)){
                Map.Entry<ColumnType, DirectionType> next = orderBy.entrySet().iterator().next();
                builder.append(next.getKey().propertyName()).append(" ").append(next.getValue().name());
            }
            else {
                builder.append("id ASC");
            }
            Query selectQuery = getEntityManager().createQuery(queryString);
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

    @Override public EntityManager getEntityManager() {
        return em;
    }
}
