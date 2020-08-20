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
package eu.europa.ec.fisheries.uvms.exchange.service.dao;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.Sorting;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.contract.search.ExchangeSearchBranch;
import eu.europa.ec.fisheries.uvms.exchange.model.contract.search.ExchangeSearchInterface;
import eu.europa.ec.fisheries.uvms.exchange.model.contract.search.ExchangeSearchLeaf;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.service.search.ExchangeSearchField;
import eu.europa.ec.fisheries.uvms.exchange.service.search.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.search.SortFieldMapperEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class ExchangeLogDaoBean extends AbstractDao {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogDaoBean.class);

    public List<ExchangeLogStatus> getExchangeLogStatusHistory(String sql, ExchangeHistoryListQuery searchQuery) {
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
    }

    public Long getLogCount(ExchangeSearchBranch queryTree) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
        Root<ExchangeLog> logRoot = cq.from(ExchangeLog.class);

        cq.select(criteriaBuilder.count(logRoot));
        Predicate predicateQuery = queryBuilderPredicate(queryTree, criteriaBuilder, logRoot);

        if (predicateQuery != null) {
            cq.where(predicateQuery);
        }

        return em.createQuery(cq).getSingleResult();
    }

    public List<ExchangeLog> getLogListSearchPaginated(Integer pageNumber, Integer pageSize, ExchangeSearchBranch searchBranch, Sorting sorting) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ExchangeLog> cq = criteriaBuilder.createQuery(ExchangeLog.class);
        Root<ExchangeLog> log = cq.from(ExchangeLog.class);

        Predicate predicateQuery = queryBuilderPredicate(searchBranch, criteriaBuilder, log);
        if (predicateQuery != null) {
            cq.where(predicateQuery);
        }

        if (sorting != null && sorting.getSortBy() != null) {
            SortFieldMapperEnum sortField = SearchFieldMapper.mapSortField(sorting.getSortBy());
            if (sorting.isReversed()) {
                cq.orderBy(criteriaBuilder.desc(log.get(sortField.getFieldName())));
            } else {
                cq.orderBy(criteriaBuilder.asc(log.get(sortField.getFieldName())));
            }
        }else {
            cq.orderBy((criteriaBuilder.desc(log.get("updateTime"))));
        }
        TypedQuery<ExchangeLog> query = em.createQuery(cq);
        query.setFirstResult(pageSize * (pageNumber - 1)); // offset
        query.setMaxResults(pageSize); // limit
        return query.getResultList();
    }

    private Predicate queryBuilderPredicate(ExchangeSearchBranch searchBranch, CriteriaBuilder criteriaBuilder, Root<ExchangeLog> logRoot) {
        if (searchBranch.getFields() == null || searchBranch.getFields().isEmpty() || searchBranch.getFields().size() < 1) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<>();

        for (ExchangeSearchInterface field : searchBranch.getFields()) {
            if (!field.isLeaf()) {
                if (!((ExchangeSearchBranch) field).getFields().isEmpty()) {
                    predicates.add(queryBuilderPredicate((ExchangeSearchBranch) field, criteriaBuilder, logRoot));
                }
            } else {
                ExchangeSearchLeaf leaf = (ExchangeSearchLeaf) field;
                ExchangeSearchField exchangeSearchField = SearchFieldMapper.mapCriteria(leaf.getSearchField());
                if (leaf.getSearchValue().contains("*")) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    logRoot.get(
                                            exchangeSearchField.getFieldName()
                                    )
                            ), "%" + leaf.getSearchValue().replace("*", "%").toLowerCase() + "%"
                        )
                    );
                } else if (exchangeSearchField.getClazz().equals(Boolean.class)) {
                    predicates.add(criteriaBuilder.equal(logRoot.get(exchangeSearchField.getFieldName()), Boolean.valueOf(leaf.getSearchValue())));
                } else if (exchangeSearchField.getClazz().equals(Instant.class)) {
                    Instant value = DateUtils.stringToDate(leaf.getSearchValue());
                    if(exchangeSearchField.equals(ExchangeSearchField.FROM_DATE)){
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(logRoot.get(exchangeSearchField.getFieldName()), value));
                    }else if(exchangeSearchField.equals(ExchangeSearchField.TO_DATE)){
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(logRoot.get(exchangeSearchField.getFieldName()), value));
                    }
                } else if (exchangeSearchField.getClazz().equals(String.class)) {
                    predicates.add(criteriaBuilder.equal(logRoot.get(exchangeSearchField.getFieldName()), leaf.getSearchValue()));
                } else if (exchangeSearchField.getClazz().equals(ExchangeLogStatusTypeType.class)) {
                    predicates.add(criteriaBuilder.equal(logRoot.get(exchangeSearchField.getFieldName()), ExchangeLogStatusTypeType.fromValue(leaf.getSearchValue())));
                } else if (exchangeSearchField.getClazz().equals(TypeRefType.class)) {
                    predicates.add(criteriaBuilder.equal(logRoot.get(exchangeSearchField.getFieldName()), TypeRefType.fromValue(leaf.getSearchValue())));
                }
            }
        }
        if (searchBranch.isLogicalAnd()) {
            return criteriaBuilder.and(predicates.stream().toArray(Predicate[]::new));
        } else {
            return criteriaBuilder.or(predicates.stream().toArray(Predicate[]::new));
        }
    }

    public ExchangeLog getExchangeLogByGuid(UUID logGuid) {
        return em.find(ExchangeLog.class, logGuid);
    }

    public ExchangeLog createLog(ExchangeLog log) {
        em.persist(log);
        return log;
    }

    public ExchangeLog getExchangeLogByGuid(UUID logGuid, TypeRefType typeRefType) {
        try {
            TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeLog.LOG_BY_GUID, ExchangeLog.class);
            query.setParameter("typeRefType", typeRefType);
            query.setParameter("guid", logGuid);
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            // Don't need to actually do anything when no entity was found!
            LOG.error("Error when getting log by id: {} and type: {}", logGuid, typeRefType);
        }
        return null;
    }

    public List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<UUID> logGuids) {
        if (CollectionUtils.isEmpty(logGuids)) {
            return new ArrayList<>();
        }
        TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeLog.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, ExchangeLog.class);
        query.setParameter("refGuids", logGuids);
        return query.getResultList();
    }


    public List<ExchangeLog> getExchangeLogByTypesRefAndGuid(UUID typeRefGuid, List<TypeRefType> types) {
        try {
            TypedQuery<ExchangeLog> namedQuery = em.createNamedQuery(ExchangeLog.LOG_BY_TYPE_REF_AND_GUID, ExchangeLog.class);
            namedQuery.setParameter("typeRefGuid", typeRefGuid);
            namedQuery.setParameter("typeRefTypes", types);
            return namedQuery.getResultList();
        } catch (NoResultException e) {
            // Don't need to actually do anything when no entity was found!
        }
        return null;
    }

    public ExchangeLog updateLog(ExchangeLog entity) {
        em.merge(entity);
        em.flush();
        return entity;
    }

    public ExchangeLog getLatestLog() {
        TypedQuery<ExchangeLog> namedQuery = em.createNamedQuery(ExchangeLog.LATEST_LOG, ExchangeLog.class);
        namedQuery.setMaxResults(1);
        return namedQuery.getResultList().get(0);
    }
}
