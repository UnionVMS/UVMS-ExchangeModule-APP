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
package eu.europa.ec.fisheries.uvms.exchange.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypeProperty;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypePropertyKey;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessageProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnsentMessageMapper {


    public static UnsentMessageType toModel(UnsentMessage entity) {
		UnsentMessageType model = new UnsentMessageType();
		model.setDateReceived(Date.from(entity.getDateReceived()));
		model.setSenderReceiver(entity.getSenderReceiver());
		model.setMessageId(entity.getGuid().toString());
		model.setRecipient(entity.getRecipient());
		model.setMessage(entity.getMessage());
        model.getProperties().addAll(mapToUnsentMessagePropertyModel(entity));
		return model;
	}

    private static List<UnsentMessageTypeProperty> mapToUnsentMessagePropertyModel(UnsentMessage entity) {
        List<UnsentMessageProperty> properties = entity.getProperties();
        List<UnsentMessageTypeProperty> unsentMessageTypeProperties = new ArrayList<>();
        for(UnsentMessageProperty property : properties){
            UnsentMessageTypeProperty unsentMessageTypeProperty = new UnsentMessageTypeProperty();
            unsentMessageTypeProperty.setKey(UnsentMessageTypePropertyKey.fromValue(property.getKey().value()));
            unsentMessageTypeProperty.setValue(property.getValue());
            unsentMessageTypeProperties.add(unsentMessageTypeProperty);
        }
        return unsentMessageTypeProperties;
    }

    public static List<UnsentMessageType> toModel(List<UnsentMessage> list) {
		List<UnsentMessageType> modelList = new ArrayList<>();
		for(UnsentMessage entity : list) {
			modelList.add(toModel(entity));
		}
		return modelList;
	}
}