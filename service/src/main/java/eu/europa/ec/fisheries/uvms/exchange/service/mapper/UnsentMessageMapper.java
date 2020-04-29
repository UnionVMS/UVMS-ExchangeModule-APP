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
package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.unsent.UnsentMessage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UnsentMessageMapper {

    public static List<UnsentMessageType> toModel(List<UnsentMessage> list) {
        return list.stream().map(UnsentMessageMapper::toModel).collect(Collectors.toList());
    }

    private static UnsentMessageType toModel(UnsentMessage entity) {
        UnsentMessageType model = new UnsentMessageType();
        model.setDateReceived(Date.from(entity.getDateReceived()));
        model.setSenderReceiver(entity.getSenderReceiver());
        model.setMessageId(entity.getGuid().toString());
        model.setRecipient(entity.getRecipient());
        model.setMessage(entity.getMessage());
        return model;
    }
}
