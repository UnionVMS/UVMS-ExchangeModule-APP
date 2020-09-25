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
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class SearchFieldMapper {

    private static final Logger LOG = LoggerFactory.getLogger(SearchFieldMapper.class);


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
            case TYPE_GUID:
                return ExchangeSearchField.TYPE_GUID;
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
    public static SortFieldMapperEnum mapSortField(SortField key) {
        switch (key) {
            case DATE_RECEIVED:
                return SortFieldMapperEnum.DATE_RECEIVED;
            case SOURCE:
                return SortFieldMapperEnum.SOURCE;
            case TYPE:
                return SortFieldMapperEnum.TYPE;
            case SENDER_RECEIVER:
                return SortFieldMapperEnum.SENDER_RECEIVER;
            case RULE:
                return SortFieldMapperEnum.RULE;
            case RECEPIENT:
                return SortFieldMapperEnum.RECEPIENT;
            case STATUS:
                return SortFieldMapperEnum.STATUS;
            case DATE_FORWARDED:
                return SortFieldMapperEnum.DATE_FORWARDED;
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
