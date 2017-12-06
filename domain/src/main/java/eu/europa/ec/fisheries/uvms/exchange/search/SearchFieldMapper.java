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
package eu.europa.ec.fisheries.uvms.exchange.search;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeHistoryListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListCriteriaPair;
import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeSearchMapperException;

/**
 **/
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
    public static String createSelectSearchSql(List<SearchValue> searchFields, boolean isDynamic) throws ParseException {
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
        selectBuffer.append(" order by " + SearchTable.LOG.getTableAlias() + ".updateTime desc ");
        LOG.debug("[ SQL: ] " + selectBuffer.toString());
        return selectBuffer.toString();
    }

    /**
     *
     * Creates a JPQL count query based on the search fields. This is used for
     * when paginating lists
     *
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws ParseException
     */
    public static String createCountSearchSql(List<SearchValue> searchFields, boolean isDynamic) throws ParseException {
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
     *
     * Created the complete search SQL with joins and sets the values based on
     * the criterias
     *
     * @param criterias
     * @param dynamic
     * @return
     * @throws ParseException
     */
    private static String createSearchSql(List<SearchValue> criterias, boolean dynamic) throws ParseException {

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
                            .append(setValueAsType(searchValue))
                            .append(" OR ").append(buildTableAliasname(searchValue.getField())).append(" IS NULL ")
                            .append(" ) ");
                } else if (criteria.getValue().size() > 1) {
                	builder
                		.append(" ( ")
                		.append(buildTableAliasname(criteria.getKey())).append(" IN (:").append(criteria.getKey().getSQLReplacementToken()).append(") ")
                		.append(" OR ").append(buildTableAliasname(criteria.getKey())).append(" IS NULL ")
                		.append(" ) ");
                }
            }
        }

        return builder.toString();
    }

    /**
     *
     * Creates at String that sets values based on what class the SearchValue
     * has. A String class returns [ = 'value' ] A Integer returns [ = value ]
     * Date is specificaly handled and can return [ >= 'datavalue' ] or [ <=
     * 'datavalue' ]
     *
     * @param entry
     * @return
     * @throws ParseException
     */
    private static String setValueAsType(SearchValue entry) throws ParseException {
        StringBuilder builder = new StringBuilder();
        if (entry.getField().getClazz().isAssignableFrom(Date.class)) {
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
        	if(entry.getValue().contains("*")) {
        		builder.append(" LIKE ").append(buildValueFromClassType(entry));
        	} else {
        		builder.append(" = ").append(buildValueFromClassType(entry));
        	}
        }

        return builder.toString();
    }

    /**
     *
     * Builds a table alias for the query based on the search field
     *
     * EG [ theTableAlias.theColumnName ]
     *
     * @param field
     * @return
     */
    private static String buildTableAliasname(ExchangeSearchField field) {
        StringBuilder builder = new StringBuilder();
        builder.append(field.getSearchTables().getTableAlias()).append(".").append(field.getFieldName());
        return builder.toString();
    }

    /**
     *
     * Returns the representation of the value
     *
     * if Integer [ value ] else [ 'value' ]
     *
     *
     * @param entry
     * @return
     */
    private static String buildValueFromClassType(SearchValue entry) {
        StringBuilder builder = new StringBuilder();
        if (entry.getField().getClazz().isAssignableFrom(Integer.class)) {
            builder.append(entry.getValue());
        } else if(entry.getField().getClazz().isAssignableFrom(Boolean.class)){
        	if("TRUE".equalsIgnoreCase(entry.getValue()) || "T".equalsIgnoreCase(entry.getValue())) {
        		builder.append(true);
        	} else {
        		builder.append(false);
        	}
        } else {
        	if(entry.getValue().contains("*")) {
        		String value = entry.getValue().replace("*", "%");
        		builder.append("'").append(value).append("'");
        	} else {
        		builder.append("'").append(entry.getValue()).append("'");
        	}
        }
        return builder.toString();
    }

    /**
     *
     * Builds an IN JPQL representation for lists of values
     *
     * The resulting String = [ mc.value IN ( 'ABC123', 'ABC321' ) ]
     *
     *
     * @param searchValues
     * @param field
     * @return
     */
    private static String buildInSqlStatement(List<SearchValue> searchValues, ExchangeSearchField field) {
        StringBuilder builder = new StringBuilder();

        builder.append(buildTableAliasname(field));

        builder.append(" IN ( ");
        
        boolean first = true;
        for (SearchValue searchValue : searchValues) {
            if (first) {
                first = false;
                builder.append(buildValueFromClassType(searchValue));
            } else {
                builder.append(", ").append(buildValueFromClassType(searchValue));
            }
        }
        builder.append(" )");
        return builder.toString();
    }

    /**
     *
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
                values.put(search.getField(), new ArrayList<SearchValue>(Arrays.asList(search)));
            }

        }
        return values;
    }

    /**
     *
     * Converts List<ListCriteria> to List<SearchValue> so that a JPQL query can
     * be built based on the criterias
     *
     * @param listCriterias
     * @return
     * @throws MovementDaoMappingException
     */
    public static List<SearchValue> mapSearchField(List<ExchangeListCriteriaPair> listCriterias) throws ExchangeSearchMapperException {

        if (listCriterias == null || listCriterias.isEmpty()) {
            LOG.debug(" Non valid search criteria when mapping ListCriterias to SearchValue, List is null or empty");
            return new ArrayList<>();
        }

        List<SearchValue> searchFields = new ArrayList<>();
        for (ExchangeListCriteriaPair criteria : listCriterias) {
            try {
                ExchangeSearchField field = mapCriteria(criteria.getKey());
                searchFields.add(new SearchValue(field, criteria.getValue()));
            } catch (ExchangeSearchMapperException ex) {
                LOG.debug("[ Error when mapping to search field.. continuing with other criterias ]");
            }
        }

        return searchFields;
    }

    /**
     *
     * Maps the Search Key to a SearchField. All SearchKeys that are not a part
     * of Movement are excluded
     *
     * @param key
     * @return
     * @throws MovementSearchMapperException
     */
    private static ExchangeSearchField mapCriteria(SearchField key) throws ExchangeSearchMapperException {
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
            default:
                throw new ExchangeSearchMapperException("No field found: " + key.name());
        }

    }

	public static String createSearchSql(ExchangeHistoryListQuery query) {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT els FROM ExchangeLogStatus els ");
		builder.append("INNER JOIN FETCH els.log log ");
		boolean status = query.getStatus() != null && !query.getStatus().isEmpty();
		boolean type = query.getType() != null && !query.getType().isEmpty();
		if(status || type || query.getTypeRefDateFrom() != null || query.getTypeRefDateTo() != null) {
			builder.append(" WHERE ");
		}
		boolean first = true;
		if(query.getStatus() != null && !query.getStatus().isEmpty()) {
			String sqlStatus = " els.status IN :status ";
			if(first) {
				builder.append(sqlStatus);
				first = false;
			} else {
				builder.append(" AND ").append(sqlStatus);
			}
		}
		if(query.getType() != null && !query.getType().isEmpty()) {
			String sqlType = " log.typeRefType IN :type ";
			if(first) {
				builder.append(sqlType);
				first = false;
			} else {
				builder.append(" AND ").append(sqlType);
			}
		}
		if(query.getTypeRefDateFrom() != null) {
			String from = " els.statusTimestamp >= :from ";
			if(first) {
				builder.append(from);
				first = false;
			} else {
				builder.append(" AND ").append(from);
			}
		}
		if(query.getTypeRefDateTo() != null) {
			String to = " els.statusTimestamp <= :to ";
			if(first) {
				builder.append(to);
				first = false;
			} else {
				builder.append(" AND ").append(to);
			}
		}
		
		return builder.toString();
	}

}