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
package eu.europa.ec.fisheries.uvms.exchange.bean;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ServiceRegistryModelBean {

    final static Logger LOG = LoggerFactory.getLogger(ServiceRegistryModelBean.class);

    @EJB
    ServiceRegistryDaoBean dao;

    public Service registerService(Service newService, String username) {
        // Look for existing service
        Service existingService = dao.getServiceByServiceClassName(newService.getServiceClassName());
        if (existingService == null) {
        	//create
            newService.setActive(true);
            newService.setStatus(true);
            existingService = dao.createEntity(newService);
        } else {
            existingService.setActive(true);
            existingService.setStatus(true);
            List<ServiceSetting> newSettings = ServiceMapper.mapSettingsList(existingService, newService.getServiceSettingList(), username);
            List<ServiceCapability> serviceCapabilityList = ServiceMapper.upsetCapabilityList(existingService, newService.getServiceCapabilityList(), username);

            existingService.getServiceCapabilityList().clear();
            existingService.getServiceCapabilityList().addAll(serviceCapabilityList);
            existingService.getServiceSettingList().clear();
            existingService.getServiceSettingList().addAll(newSettings);
            existingService.setDescription(newService.getDescription());
            existingService.setName(newService.getName());
            existingService.setUpdated(Instant.now());
            existingService.setUpdatedBy(username);
            dao.updateService(existingService);
        }
        return existingService;
    }

    public Service unregisterService(ServiceType serviceType, String username) {
        // Look for existing service
        Service service = dao.getServiceByServiceClassName(serviceType.getServiceClassName());
        if (service != null) {
            service.setActive(false);
            service.setStatus(false);
            service.getServiceCapabilityList().clear();
            service.getServiceSettingList().clear();
            service.setUpdatedBy(username);
            Service updateService = dao.updateService(service);
            return updateService;
        }

        //TODO handle unable to unregister
        throw new IllegalArgumentException("[ No service to unregister ]"); 
    }

    public List<Service> getPlugins(List<PluginType> pluginTypes) {

       	List<Service> entityList = new ArrayList<>();
       	if(pluginTypes == null || pluginTypes.isEmpty()) {
       		entityList = dao.getServices();
       	} else {
       		entityList = dao.getServicesByTypes(pluginTypes);
       	}

        return entityList;
    }

	public Service updatePluginSettings(String serviceClassName, List<ServiceSetting> settings, String username) {
    	LOG.info("Update plugin settings for " + serviceClassName);
    	Service service = dao.getServiceByServiceClassName(serviceClassName);
    	if(service != null) {
    		List<ServiceSetting> newSettings = ServiceMapper.mapSettingsList(service, settings, username);
    		service.getServiceSettingList().clear();
    		service.getServiceSettingList().addAll(newSettings);
    		dao.updateService(service);
    		return service;
    	}
    	throw new IllegalArgumentException("No plugin found when update plugin settings");
	}
    
    public List<SettingType> getPluginSettings(String serviceClassName) {
        LOG.info("Get plugin settings:{}",serviceClassName);

        List<SettingType> settings = new ArrayList<>();
        List<ServiceSetting> entityList = dao.getServiceSettings(serviceClassName);
        for (ServiceSetting entity : entityList) {
            settings.add(ServiceMapper.toModel(entity));
        }


        return settings;

    }

    public List<CapabilityType> getPluginCapabilities(String serviceClassName) {
        LOG.info("Get plugin capabilities:{}",serviceClassName);

        List<CapabilityType> capabilities = new ArrayList<>();
        List<ServiceCapability> entityList = dao.getServiceCapabilities(serviceClassName);
        for (ServiceCapability entity : entityList) {
            capabilities.add(ServiceMapper.toModel(entity));
        }

            

        return capabilities;
    }

    public Service getPlugin(String serviceClassName) {
            Service service = dao.getServiceByServiceClassName(serviceClassName);
            return service;

    }

	public ServiceResponseType updatePluginStatus(String serviceName, StatusType status, String username) {
		Service service = dao.getServiceByServiceClassName(serviceName);
		if(service != null) {
			service.setStatus(StatusType.STARTED.equals(status));
            service.setUpdatedBy(username);
            service.setUpdated(Instant.now());
			return ServiceMapper.toServiceModel(dao.updateService(service));
		}
		throw new IllegalArgumentException("[ Error when update plugin " + serviceName + " with status " + status.name());
	}
}