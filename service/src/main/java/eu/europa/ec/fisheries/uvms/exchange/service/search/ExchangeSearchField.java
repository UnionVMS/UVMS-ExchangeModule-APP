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

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;

import java.time.Instant;

public enum ExchangeSearchField {

	TRANSFER_INCOMING("transferIncoming", "transferIncoming", Boolean.class),
    FROM_DATE("dateReceived", "fromDate", Instant.class),
    TO_DATE("dateReceived", "toDate", Instant.class),
    SENDER_RECEIVER("senderReceiver", "senderReceiver", String.class),
    RECIPIENT("recipient", "recipient", String.class),
    STATUS("status", "status", ExchangeLogStatusTypeType.class),
    TYPE("typeRefType","typeRefType", TypeRefType.class),
    SOURCE("source","source", String.class);

    private final String fieldName;
    private final String sqlReplacementToken;
    private final Class<?> clazz;

    ExchangeSearchField(String fieldName, String sqlReplacementToken, Class<?> clazz) {
        this.fieldName = fieldName;
        this.sqlReplacementToken = sqlReplacementToken;
        this.clazz = clazz;
    }

    public String getFieldName() {
        return fieldName;
    }


    public String getSQLReplacementToken() {
        return sqlReplacementToken;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
