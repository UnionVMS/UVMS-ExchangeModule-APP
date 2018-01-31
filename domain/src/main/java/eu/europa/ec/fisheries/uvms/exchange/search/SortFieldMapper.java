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

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;

import java.util.Date;

/**
 * Created by sanera on 30/11/2017.
 */
public enum SortFieldMapper {
    DATE_RECEIVED("dateReceived", SearchTable.LOG, Date.class),
    SOURCE("source",  SearchTable.LOG, String.class),
    TYPE("typeRefType", SearchTable.LOG, TypeRefType.class),
    SENDER_RECEIVER("senderReceiver",  SearchTable.LOG, String.class),
    RULE("fwdRule",  SearchTable.LOG, String.class),
    RECEPIENT("recipient",  SearchTable.LOG, String.class),
    STATUS("status",SearchTable.LOG, ExchangeLogStatusTypeType.class),
    DATE_FORWARDED("fwdDate",SearchTable.LOG, Date.class);

    private final String fieldName;
    private final SearchTable searchTables;
    private Class clazz;

    private SortFieldMapper(String fieldName, SearchTable searchTables, Class clazz) {
        this.fieldName = fieldName;

        this.searchTables = searchTables;
        this.clazz = clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

    public SearchTable getSearchTables() {
        return searchTables;
    }


    public Class getClazz() {
        return clazz;
    }
}
