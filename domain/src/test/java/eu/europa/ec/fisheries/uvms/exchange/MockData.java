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
package eu.europa.ec.fisheries.uvms.exchange;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;

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
}