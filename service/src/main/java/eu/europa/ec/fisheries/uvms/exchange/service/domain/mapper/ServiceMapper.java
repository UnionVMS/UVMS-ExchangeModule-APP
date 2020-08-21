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
package eu.europa.ec.fisheries.uvms.exchange.service.domain.mapper;

import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceMapper {
    
    public static Service toServiceEntity(ServiceType model, CapabilityListType capabilityList, SettingListType settingList, String username) {
        Service entity = new Service();
        return toServiceEntity(entity, model, capabilityList, settingList, username);
    }
    
    public static Service toServiceEntity(Service entity, ServiceType model, CapabilityListType capabilityList, SettingListType settingList, String username) {
        entity.setName(model.getName());
        entity.setServiceClassName(model.getServiceClassName());
        entity.setServiceResponse(model.getServiceResponseMessageName());
        entity.setDescription(model.getDescription());
        entity.setType(model.getPluginType());
        entity.setSatelliteType(model.getSatelliteType());
        entity.setUpdated(DateUtils.nowUTC().toDate());
        entity.setUpdatedBy(username);
        entity.setServiceCapabilityList(toCapabilitiesEntities(entity, capabilityList, username));
        entity.setServiceSettingList(toSettingsEntities(entity, settingList, username));
        
        return entity;
    }
    
    public static ServiceResponseType toServiceModel(Service entity) {
        ServiceResponseType model = new ServiceResponseType();
        model.setDescription(entity.getDescription());
        model.setName(entity.getName());
        model.setServiceClassName(entity.getServiceClassName());
        CapabilityListType capabilityList = toCapabilityListModel(entity.getServiceCapabilityList());
        model.setCapabilityList(capabilityList);
        model.setServiceResponseMessageName(entity.getServiceResponse());
        model.setPluginType(entity.getType());
        model.setSatelliteType(entity.getSatelliteType());
        model.setStatus(mapStatus(entity.getStatus()));
        model.setActive(entity.getActive());
        SettingListType settingList = toSettingListModel(entity.getServiceSettingList());
        model.setSettingList(settingList);
        return model;
    }
    
    private static StatusType mapStatus(String status) {
        if (status != null) {
            try {
                return StatusType.valueOf(status);
            } catch (Exception e) {
                return StatusType.UNKNOWN;
            }
        }
        return StatusType.UNKNOWN;
    }
    
    private static CapabilityListType toCapabilityListModel(List<ServiceCapability> capabilityList) {
        CapabilityListType model = new CapabilityListType();
        if (capabilityList != null) {
            for (ServiceCapability capability : capabilityList) {
                model.getCapability().add(toCapabilityModel(capability));
            }
        }
        return model;
    }
    
    private static CapabilityType toCapabilityModel(ServiceCapability capability) {
        CapabilityType model = new CapabilityType();
        model.setType(capability.getCapability());
        model.setValue(capability.getValue());
        return model;
    }
    
    public static List<ServiceCapability> toCapabilitiesEntities(Service parent, CapabilityListType capabilityList, String username) {
        List<ServiceCapability> capabilityEntities = new ArrayList<>();
        for (CapabilityType capability : capabilityList.getCapability()) {
            capabilityEntities.add(toCapabilityEntity(parent, capability, username));
        }
        return capabilityEntities;
    }
    
    private static SettingListType toSettingListModel(List<ServiceSetting> serviceSettingList) {
        SettingListType model = new SettingListType();
        if (serviceSettingList != null) {
            for (ServiceSetting setting : serviceSettingList) {
                model.getSetting().add(toSettingModel(setting));
            }
        }
        return model;
    }
    
    private static SettingType toSettingModel(ServiceSetting setting) {
        SettingType model = new SettingType();
        model.setKey(setting.getSetting());
        model.setValue(setting.getValue());
        return model;
    }
    
    private static ServiceCapability toCapabilityEntity(Service parent, CapabilityType capability, String username) {
        ServiceCapability entity = new ServiceCapability();
        entity.setService(parent);
        entity.setCapability(capability.getType());
        entity.setUpdatedBy(username);
        entity.setUpdatedTime(DateUtils.nowUTC().toDate());
        entity.setValue(capability.getValue());
        return entity;
    }
    
    public static CapabilityType toModel(ServiceCapability entity) {
        CapabilityType type = new CapabilityType();
        type.setType(entity.getCapability());
        type.setValue(entity.getValue());
        return type;
    }
    
    public static List<ServiceSetting> mapSettingsList(Service parent, SettingListType settingList, String username) {
        List<ServiceSetting> newSettings = new ArrayList<>();
        
        List<ServiceSetting> currentSettings = parent.getServiceSettingList();
        Map<String, ServiceSetting> map = new HashMap<>();
        if (currentSettings != null) {
            for (ServiceSetting i : currentSettings) {
                map.put(i.getSetting(), i);
            }            
        }

        for (SettingType setting : settingList.getSetting()) {
            ServiceSetting currentSetting = map.get(setting.getKey());
            if (currentSetting == null) {
                ServiceSetting newSetting = toSettingEntity(parent, setting, username);
                newSetting.setService(parent);
                newSettings.add(newSetting);
            } else {
                if (!currentSetting.getValue().equalsIgnoreCase(setting.getValue())) {
                    currentSetting.setValue(setting.getValue());
                    currentSetting.setUpdatedTime(DateUtils.nowUTC().toDate());
                    currentSetting.setUser(username);
                }
                currentSetting.setService(parent);
                newSettings.add(currentSetting);
            }
        }
        return newSettings;
    }

    public static List<ServiceCapability> upsetCapabilityList(Service parent, CapabilityListType capabilityList, String username){
       List<ServiceCapability> newCapabilityList = new ArrayList<>();
        for(CapabilityType capabilityType : capabilityList.getCapability()){
            ServiceCapability newServiceCapability = toCapabilityEntity(parent, capabilityType, username);
            newCapabilityList.add(newServiceCapability);
        }

        return newCapabilityList;
    }

    public static List<ServiceSetting> toSettingsEntities(Service parent, SettingListType settingList, String username) {
        List<ServiceSetting> settingEntities = new ArrayList<>();
        for (SettingType setting : settingList.getSetting()) {
            settingEntities.add(toSettingEntity(parent, setting, username));
        }
        return settingEntities;
    }
    
    private static ServiceSetting toSettingEntity(Service parent, SettingType setting, String username) {
        ServiceSetting entity = new ServiceSetting();
        entity.setService(parent);
        entity.setSetting(setting.getKey());
        entity.setUpdatedTime(DateUtils.nowUTC().toDate());
        entity.setUser(username);
        entity.setValue(setting.getValue());
        return entity;
    }
    
    public static SettingType toModel(ServiceSetting entity) {
        SettingType type = new SettingType();
        type.setKey(entity.getSetting());
        type.setValue(entity.getValue());
        return type;
    }
}