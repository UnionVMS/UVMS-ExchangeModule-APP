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
package eu.europa.ec.fisheries.uvms.exchange.service.domain;

import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockData {

	public static String SETTING_KEY = "setting.key";
	
    public static ServiceType getModel(long id) {
        ServiceType dto = new ServiceType();
        dto.setName("Plugin name");
        dto.setDescription("Some description");
        dto.setServiceClassName("the.qualified.id.of.plugin"+id);
        return dto;
    }

    public static Service getEntity(long id) {
        Service entity = new Service();
        entity.setId(id);
        entity.setName("Plugin name");
        entity.setDescription("Some description");
        entity.setServiceClassName("the.qualified.id.of.plugin"+id);
        return entity;
    }

	public static CapabilityListType getCapabilityList() {
		CapabilityListType dto = new CapabilityListType();
		CapabilityType capability = new CapabilityType();
		capability.setType(CapabilityTypeType.POLLABLE);
		capability.setValue("TRUE");
		dto.getCapability().add(capability);
		return dto;
	}

	public static List<ServiceCapability> getEntityCapabilities(Service parent) {
		List<ServiceCapability> list = new ArrayList<>();
		ServiceCapability capability = new ServiceCapability();
		capability.setCapability(CapabilityTypeType.POLLABLE);
		capability.setService(parent);
		capability.setValue("TRUE");
		list.add(capability);
		return list;
	}
	
	public static SettingListType getSettingList() {
		SettingListType dto = new SettingListType();
		SettingType setting = new SettingType();
		setting.setKey(SETTING_KEY);
		setting.setValue("setting.value");
		dto.getSetting().add(setting);
		return dto;
	}
	
	public static List<ServiceSetting> getEntitySettings(Service parent) {
		List<ServiceSetting> list = new ArrayList<>();
		ServiceSetting setting = new ServiceSetting();
		setting.setService(parent);
		setting.setSetting(SETTING_KEY);
		setting.setValue("value");
		list.add(setting);
		return list;
	}

	public static List<ExchangeLog> getLogEntities(){
		ExchangeLog log1 = new ExchangeLog();
		log1.setType(LogType.RECEIVE_FA_QUERY_MSG);
		log1.setTypeRefGuid("SomeRefGuid_1AAA");
		log1.setTransferIncoming(false);
		log1.setGuid("AAA-BBB-CCC");
		log1.setTypeRefType(TypeRefType.FA_QUERY);
		ExchangeLog log2 = new ExchangeLog();
		log2.setType(LogType.RECEIVE_FA_QUERY_MSG);
		log2.setTypeRefGuid("SomeRefGuid_2BBB");
		log2.setTransferIncoming(true);
		log2.setGuid("CCC-DDD-EEE");
		log2.setTypeRefType(TypeRefType.FA_RESPONSE);
		return Arrays.asList(log1, log2);
	}
}