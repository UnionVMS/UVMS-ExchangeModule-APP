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

/**
 * Created by sanera on 30/11/2017.
 */
public enum SortFieldMapperEnum {
    DATE_RECEIVED("dateReceived", Instant.class),
    SOURCE("source", String.class),
    TYPE("typeRefType", TypeRefType.class),
    SENDER_RECEIVER("senderReceiver", String.class),
    RULE("fwdRule", String.class),
    RECEPIENT("recipient", String.class),
    STATUS("status", ExchangeLogStatusTypeType.class),
    DATE_FORWARDED("fwdDate", Instant.class);

    private final String fieldName;
    private Class<?> clazz;

    SortFieldMapperEnum(String fieldName, Class<?> clazz) {
        this.fieldName = fieldName;

        this.clazz = clazz;
    }

    public String getFieldName() {
        return fieldName;
    }



    public Class<?> getClazz() {
        return clazz;
    }
}
