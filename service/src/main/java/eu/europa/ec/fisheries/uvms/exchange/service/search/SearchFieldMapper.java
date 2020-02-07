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
package eu.europa.ec.fisheries.uvms.exchange.service.search;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class SearchFieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(SearchFieldMapper.class);

    /**
     * Creates a search SQL based on the search fields
     *
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws ParseException
     */
    public static String createSelectSearchSql(List<SearchValue> searchFields, boolean isDynamic, Sorting sorting) {
        StringBuilder selectBuffer = new StringBuilder();
        selectBuffer.append("SELECT DISTINCT ")
                .append(SearchTable.LOG.getTableAlias())
                .append(" FROM ")
                .append(SearchTable.LOG.getTableName())
                .append(" ")
                .append(SearchTable.LOG.getTableAlias())
                .append(" ");
        if (searchFields != null) {
            selectBuffer.append(createSearchSql(searchFields, isDynamic));
        }
        getSortingString(sorting, selectBuffer);
        LOG.debug("[ SQL: ] " + selectBuffer.toString());
        return selectBuffer.toString();
    }

    private static void getSortingString(Sorting sorting, StringBuilder selectBuffer) {
        if (sorting != null && sorting.getSortBy() != null) {
            SortField sortField = sorting.getSortBy();
            SortFieldMapper sortFieldMapper = null;
            if (sortField != null) {
                sortFieldMapper = mapSortField(sortField);
            }
            String fieldName = sortFieldMapper.getFieldName();
            String sortingDirection = "ASC";
            if (sorting.isReversed()) {
                sortingDirection = "DESC";
            }
            selectBuffer.append(" order by " + SearchTable.LOG.getTableAlias() + ".").append(fieldName).append(" ").append(sortingDirection);
        } else {
            selectBuffer.append(" order by " + SearchTable.LOG.getTableAlias() + ".updateTime desc ");
        }
    }

    /**
     * Creates a JPQL count query based on the search fields. This is used for
     * when paginating lists
     *
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws ParseException
     */
    public static String createCountSearchSql(List<SearchValue> searchFields, boolean isDynamic) {
        StringBuilder countBuffer = new StringBuilder();
        countBuffer.append("SELECT COUNT(").append(SearchTable.LOG.getTableAlias()).append(") FROM ")
                .append(SearchTable.LOG.getTableName())
                .append(" ")
                .append(SearchTable.LOG.getTableAlias())
                .append(" ");
        if (searchFields != null) {
            countBuffer.append(createSearchSql(searchFields, isDynamic));
        }
        LOG.debug("[ COUNT SQL: ]" + countBuffer.toString());
        return countBuffer.toString();
    }

    /**
     * Created the complete search SQL with joins and sets the values based on
     * the criterias
     *
     * @param criterias
     * @param dynamic
     * @return
     * @throws ParseException
     */
    private static String createSearchSql(List<SearchValue> criterias, boolean dynamic) {

        String OPERATOR = " OR ";
        if (dynamic) {
            OPERATOR = " AND ";
        }

        StringBuilder builder = new StringBuilder();
        HashMap<ExchangeSearchField, List<SearchValue>> orderedValues = combineSearchFields(criterias);

        if (!orderedValues.isEmpty()) {

            builder.append("WHERE ");

            boolean first = true;
            for (Map.Entry<ExchangeSearchField, List<SearchValue>> criteria : orderedValues.entrySet()) {

                if (first) {
                    first = false;
                } else {
                    builder.append(OPERATOR);
                }

                if (criteria.getValue().size() == 1) {
                    SearchValue searchValue = criteria.getValue().get(0);
                    builder
                            .append(" ( ")
                            .append(buildTableAliasname(searchValue.getField()))
                            .append(setParameter(searchValue))
                            .append(" ) ");
                } else if (criteria.getValue().size() > 1) {
                    builder
                            .append(" ( ")
                            .append(buildTableAliasname(criteria.getKey())).append(" IN (:").append(criteria.getKey().getSQLReplacementToken()).append(") ")
                            .append(" ) ");
                }
            }
        }

        return builder.toString();
    }

    private static String setParameter(SearchValue entry) {
        StringBuilder builder = new StringBuilder();
        if (entry.getField().getClazz().isAssignableFrom(Instant.class)) {
            switch (entry.getField()) {
                case FROM_DATE:
                    builder.append(" >= ").append(":").append(entry.getField().getSQLReplacementToken());
                    break;
                case TO_DATE:
                    builder.append(" <= ").append(":").append(entry.getField().getSQLReplacementToken());
                    break;
                default:
                    builder.append(" = ").append(":").append(entry.getField().getSQLReplacementToken());
                    break;
            }
        } else {
            builder.append(" = ").append(":").append(entry.getField().getSQLReplacementToken());
        }

        return builder.toString();
    }

    /**
     * Builds a table alias for the query based on the search field
     * <p>
     * EG [ theTableAlias.theColumnName ]
     *
     * @param field
     * @return
     */
    private static String buildTableAliasname(ExchangeSearchField field) {
        return field.getSearchTables().getTableAlias() + "." + field.getFieldName();
    }

    public static <T> T buildValueFromClassType(SearchValue entry, Class<T> valueType) {
        StringBuilder builder = new StringBuilder();

        try {

            if (valueType.isAssignableFrom(String.class)) {
                if (entry.getValue().contains("*")) {
                    String value = entry.getValue().replace("*", "%");
                    builder.append("'").append(value).append("'");
                } else {
                    builder.append(entry.getValue());
                }

                return valueType.cast(builder.toString());
            } else if (valueType.isAssignableFrom(Boolean.class)) {
                if ("TRUE".equalsIgnoreCase(entry.getValue()) || "T".equalsIgnoreCase(entry.getValue())) {
                    return valueType.cast(Boolean.TRUE);
                } else {
                    return valueType.cast(Boolean.FALSE);
                }
            } else if (valueType.isAssignableFrom(Instant.class)) {
                return valueType.cast(DateUtils.parseToUTCDateTime(entry.getValue()));
            } else if (valueType.isAssignableFrom(Integer.class)) {
                return valueType.cast(Integer.valueOf(entry.getValue()));
            } else if (valueType.isAssignableFrom(TypeRefType.class)) {
                return valueType.cast(TypeRefType.valueOf(entry.getValue()));
            } else if (valueType.isAssignableFrom(ExchangeLogStatusTypeType.class)) {
                return valueType.cast(ExchangeLogStatusTypeType.valueOf(entry.getValue()));
            }

            return valueType.cast(entry.getValue());
        } catch (ClassCastException cce) {
            LOG.error("Error casting parameter: " + entry.getField().getFieldName() + " having value: " + entry.getValue(), cce);
            return null;
        }
    }

    /**
     * Takes all the search values and categorizes them in lists to a key
     * according to the SearchField
     *
     * @param searchValues
     * @return
     */
    public static HashMap<ExchangeSearchField, List<SearchValue>> combineSearchFields(List<SearchValue> searchValues) {
        HashMap<ExchangeSearchField, List<SearchValue>> values = new HashMap<>();
        for (SearchValue search : searchValues) {
            if (values.containsKey(search.getField())) {
                values.get(search.getField()).add(search);
            } else {
                values.put(search.getField(), new ArrayList<SearchValue>(Collections.singletonList(search)));
            }
        }
        return values;
    }

    /**
     * Converts List<ListCriteria> to List<SearchValue> so that a JPQL query can
     * be built based on the criterias
     *
     * @param listCriterias
     * @return
     * @throws
     */
    public static List<SearchValue> mapSearchField(List<ExchangeListCriteriaPair> listCriterias) {

        if (CollectionUtils.isEmpty(listCriterias)) {
            LOG.debug(" Non valid search criteria when mapping ListCriterias to SearchValue, List is null or empty");
            return new ArrayList<>();
        }

        List<SearchValue> searchFields = new ArrayList<>();
        for (ExchangeListCriteriaPair criteria : listCriterias) {
            try {
                if (SearchField.MESSAGE_DIRECTION.equals(criteria.getKey())) {
                    SearchValue searchValue = getSearchValueForMessageDirection(criteria);
                    if (searchValue == null) {
                        continue;
                    }
                    searchFields.add(searchValue);
                } else {
                    ExchangeSearchField field = mapCriteria(criteria.getKey());
                    searchFields.add(new SearchValue(field, criteria.getValue()));
                }

            } catch (IllegalArgumentException ex) {
                LOG.debug("[ Error when mapping to search field.. continuing with other criterias ]");
            }
        }

        return searchFields;
    }

    private static SearchValue getSearchValueForMessageDirection(ExchangeListCriteriaPair criteria) {
        if (!SearchField.MESSAGE_DIRECTION.equals(criteria.getKey()) || criteria.getValue() == null) {
            return null;
        }
        MessageDirection messageDirection = MessageDirection.valueOf(criteria.getValue());
        SearchValue searchValue = null;
        switch (messageDirection) {
            case INCOMING:
                searchValue = new SearchValue(ExchangeSearchField.TRANSFER_INCOMING, "true");
                break;
            case OUTGOING:
                searchValue = new SearchValue(ExchangeSearchField.TRANSFER_INCOMING, "false");
                break;
        }
        return searchValue;

    }

    /**
     * Maps the Search Key to a SearchField. All SearchKeys that are not a part
     * of Movement are excluded
     *
     * @param key
     * @return
     * @throws
     */
    public static ExchangeSearchField mapCriteria(SearchField key) {
        switch (key) {
            case TRANSFER_INCOMING:
                return ExchangeSearchField.TRANSFER_INCOMING;
            case RECIPIENT:
                return ExchangeSearchField.RECIPIENT;
            case DATE_RECEIVED_TO:
                return ExchangeSearchField.TO_DATE;
            case DATE_RECEIVED_FROM:
                return ExchangeSearchField.FROM_DATE;
            case SENDER_RECEIVER:
                return ExchangeSearchField.SENDER_RECEIVER;
            case TYPE:
                return ExchangeSearchField.TYPE;
            case STATUS:
                return ExchangeSearchField.STATUS;
            case SOURCE:
                return ExchangeSearchField.SOURCE;
            default:
                throw new IllegalArgumentException("No field found: " + key.name());
        }
    }

    /**
     * Maps the Search Key to a SearchField. All SearchKeys that are not a part
     * of Movement are excluded
     *
     * @param key
     * @return
     * @throws
     */
    public static SortFieldMapper mapSortField(SortField key) {
        switch (key) {
            case DATE_RECEIVED:
                return SortFieldMapper.DATE_RECEIVED;
            case SOURCE:
                return SortFieldMapper.SOURCE;
            case TYPE:
                return SortFieldMapper.TYPE;
            case SENDER_RECEIVER:
                return SortFieldMapper.SENDER_RECEIVER;
            case RULE:
                return SortFieldMapper.RULE;
            case RECEPIENT:
                return SortFieldMapper.RECEPIENT;
            case STATUS:
                return SortFieldMapper.STATUS;
            case DATE_FORWARDED:
                return SortFieldMapper.DATE_FORWARDED;
            default:
                throw new IllegalArgumentException("No field found: " + key.name());
        }
    }

    public static String createSearchSql(ExchangeHistoryListQuery query) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT els FROM ExchangeLogStatus els ");
        builder.append("INNER JOIN FETCH els.log log ");
        boolean status = query.getStatus() != null && !query.getStatus().isEmpty();
        boolean type = query.getType() != null && !query.getType().isEmpty();
        if (status || type || query.getTypeRefDateFrom() != null || query.getTypeRefDateTo() != null) {
            builder.append(" WHERE ");
        }
        boolean first = true;
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            String sqlStatus = " els.status IN :status ";
            if (first) {
                builder.append(sqlStatus);
                first = false;
            } else {
                builder.append(" AND ").append(sqlStatus);
            }
        }
        if (query.getType() != null && !query.getType().isEmpty()) {
            String sqlType = " log.typeRefType IN :type ";
            if (first) {
                builder.append(sqlType);
                first = false;
            } else {
                builder.append(" AND ").append(sqlType);
            }
        }
        if (query.getTypeRefDateFrom() != null) {
            String from = " els.statusTimestamp >= :from ";
            if (first) {
                builder.append(from);
                first = false;
            } else {
                builder.append(" AND ").append(from);
            }
        }
        if (query.getTypeRefDateTo() != null) {
            String to = " els.statusTimestamp <= :to ";
            if (first) {
                builder.append(to);
                first = false;
            } else {
                builder.append(" AND ").append(to);
            }
        }

        return builder.toString();
    }

}
