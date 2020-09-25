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
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.service.search.ExchangeSearchField;
import eu.europa.ec.fisheries.uvms.exchange.service.search.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.search.SearchValue;
import eu.europa.ec.fisheries.uvms.exchange.service.search.SortFieldMapperEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<ExchangeLog> getExchangeLogListPaginated(Integer page, Integer listSize, String sql, List<SearchValue> searchKeyValues) {
        LOG.debug("SQL QUERY IN LIST PAGINATED: " + sql);
        TypedQuery<ExchangeLog> query = em.createQuery(sql, ExchangeLog.class);
        HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = SearchFieldMapper.combineSearchFields(searchKeyValues);
        setQueryParameters(query, orderedValues);
        query.setFirstResult(listSize * (page - 1));
        query.setMaxResults(listSize);
        return query.getResultList();
    }

    private CriteriaQuery<ExchangeLog> queryBuilderPredicate(List<SearchValue> searchKeyValues, Sorting sorting, boolean logicalAnd){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ExchangeLog> cq = criteriaBuilder.createQuery(ExchangeLog.class);
        Root<ExchangeLog> log = cq.from(ExchangeLog.class);

        HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = SearchFieldMapper.combineSearchFields(searchKeyValues);
        List<Predicate> predicates = new ArrayList<>();

        for (ExchangeSearchField searchField : orderedValues.keySet()) {
            List<SearchValue> searchValues = orderedValues.get(searchField);
            boolean multipleSearchValuesForField = searchValues.size() > 1;
            boolean containsWildcard = searchValues.stream().anyMatch(searchValue -> searchValue.getValue().contains("*"));

            if (multipleSearchValuesForField) {
                if (containsWildcard) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    log.get(
                                            searchField.getFieldName()
                                    )
                            ), "%" + leaf.getSearchValue().replace("*", "%").toLowerCase() + "%"
                            )
                    );
                } else if (searchField.getClazz().equals(Boolean.class)) {
                    List<Boolean> collect = searchValues.stream().map(SearchValue::getValue).map(Boolean::valueOf).collect(Collectors.toList());
                    predicates.add(criteriaBuilder.in(log.get(searchField.getFieldName())).value(collect));
                } else if (searchField.getClazz().equals(Instant.class)) {
                    throw new IllegalArgumentException("Having several from or to dates in a search makes no sense and is not supported");
                } else if (searchField.getClazz().equals(String.class)) {
                    List<String> collect = searchValues.stream().map(SearchValue::getValue).collect(Collectors.toList());
                    predicates.add(criteriaBuilder.in(log.get(searchField.getFieldName())).value(collect));
                } else if (searchField.getClazz().equals(ExchangeLogStatusTypeType.class)) {
                    List<ExchangeLogStatusTypeType> collect = searchValues.stream().map(SearchValue::getValue).map(ExchangeLogStatusTypeType::fromValue).collect(Collectors.toList());
                    predicates.add(criteriaBuilder.in(log.get(searchField.getFieldName())).value(collect));
                } else if (searchField.getClazz().equals(TypeRefType.class)) {
                    List<TypeRefType> collect = searchValues.stream().map(SearchValue::getValue).map(TypeRefType::fromValue).collect(Collectors.toList());
                    predicates.add(criteriaBuilder.in(log.get(searchField.getFieldName())).value(collect));
                } else if (searchField.getClazz().equals(UUID.class)) {
                    List<UUID> collect = searchValues.stream().map(SearchValue::getValue).map(UUID::fromString).collect(Collectors.toList());
                    predicates.add(criteriaBuilder.in(log.get(searchField.getFieldName())).value(collect));
                }

            } else {

                SearchValue searchValue = searchValues.get(0);
                if (containsWildcard) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    log.get(
                                            searchField.getFieldName()
                                    )
                            ), "%" + searchValue.getValue().replace("*", "%").toLowerCase() + "%"
                            )
                    );
                } else if (searchField.getClazz().equals(Boolean.class)) {
                    predicates.add(criteriaBuilder.equal(log.get(searchField.getFieldName()), Boolean.valueOf(searchValue.getValue())));
                } else if (searchField.getClazz().equals(Instant.class)) {
                    Instant value = DateUtils.stringToDate(searchValue.getValue());
                    if (searchField.equals(ExchangeSearchField.FROM_DATE)) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(log.get(searchField.getFieldName()), value));
                    } else if (searchField.equals(ExchangeSearchField.TO_DATE)) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(log.get(searchField.getFieldName()), value));
                    }
                } else if (searchField.getClazz().equals(String.class)) {
                    predicates.add(criteriaBuilder.equal(log.get(searchField.getFieldName()), searchValue.getValue()));
                } else if (searchField.getClazz().equals(ExchangeLogStatusTypeType.class)) {
                    predicates.add(criteriaBuilder.equal(log.get(searchField.getFieldName()), ExchangeLogStatusTypeType.fromValue(searchValue.getValue())));
                } else if (searchField.getClazz().equals(TypeRefType.class)) {
                    predicates.add(criteriaBuilder.equal(log.get(searchField.getFieldName()), TypeRefType.fromValue(searchValue.getValue())));
                } else if (searchField.getClazz().equals(UUID.class)) {
                    predicates.add(criteriaBuilder.equal(log.get(searchField.getFieldName()), UUID.fromString(searchValue.getValue())));
                }
            }
        }

        if (logicalAnd) {
            cq.where(criteriaBuilder.and(predicates.stream().toArray(Predicate[]::new)));
        } else {
            cq.where(criteriaBuilder.or(predicates.stream().toArray(Predicate[]::new)));
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

        return cq;
    }

    public Long getExchangeLogListSearchCount(String countSql, List<SearchValue> searchKeyValues) {
        LOG.debug("SQL QUERY IN LIST COUNT: " + countSql);
        TypedQuery<Long> query = em.createQuery(countSql, Long.class);
        HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = SearchFieldMapper.combineSearchFields(searchKeyValues);
        setQueryParameters(query, orderedValues);
        return query.getSingleResult();
    }

    public ExchangeLog getExchangeLogByGuid(UUID logGuid) {
        return em.find(ExchangeLog.class, logGuid);
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
