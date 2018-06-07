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
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.ServiceRegistryModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ServiceRegistryModelBean implements ServiceRegistryModel {

    final static Logger LOG = LoggerFactory.getLogger(ServiceRegistryModelBean.class);

    @EJB
    ServiceRegistryDao dao;

    @Override
    public ServiceResponseType registerService(ServiceType serviceType, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeModelException {
        // Look for existing service
        Service service = dao.getServiceByServiceClassName(serviceType.getServiceClassName());
        if (service == null) {
        	//create
        	service = ServiceMapper.toServiceEntity(serviceType, capabilityList, settingList, username);
        	service.setActive(true);
        	service.setStatus(StatusType.STARTED.name());
            dao.createEntity(service);
        } else {
            service.setActive(true);
            service.setStatus(StatusType.STARTED.name());
            List<ServiceSetting> newSettings = ServiceMapper.mapSettingsList(service, settingList, username);
            List<ServiceCapability> serviceCapabilityList = ServiceMapper.upsetCapabilityList(service, capabilityList, username);

            service.getServiceCapabilityList().clear();
            service.getServiceCapabilityList().addAll(serviceCapabilityList);
            service.getServiceSettingList().clear();
            service.getServiceSettingList().addAll(newSettings);
            service.setDescription(serviceType.getDescription());
            service.setName(serviceType.getName());
            service.setUpdated(new DateTime(DateTimeZone.UTC).toDate());
            service.setUpdatedBy(username);
            dao.updateService(service);
        }
        return ServiceMapper.toServiceModel(service);        
    }

    @Override
    public ServiceResponseType unregisterService(ServiceType serviceType, String username) throws ExchangeModelException {
        // Look for existing service
        Service service = dao.getServiceByServiceClassName(serviceType.getServiceClassName());
        ServiceResponseType response = null;
        if (service != null) {
            service.setActive(false);
            service.setStatus(StatusType.STOPPED.name());
            service.getServiceCapabilityList().clear();
            service.getServiceSettingList().clear();
            service.setUpdatedBy(username);
            Service updateService = dao.updateService(service);
            response = ServiceMapper.toServiceModel(updateService);
        }

        if(response != null) {
        	return response;
        }
        
        //TODO handle unable to unregister
        throw new ExchangeDaoException("[ No service to unregister ]"); 
    }

    @Override
    public List<ServiceResponseType> getPlugins(List<PluginType> pluginTypes) throws ExchangeModelException {
        List<ServiceResponseType> services = new ArrayList<>();

       	List<Service> entityList = new ArrayList<>();
       	if(pluginTypes == null || pluginTypes.isEmpty()) {
       		entityList = dao.getServices();
       	} else {
       		entityList = dao.getServicesByTypes(pluginTypes);
       	}
        for (Service entity : entityList) {
            services.add(ServiceMapper.toServiceModel(entity));
        }
        return services;
    }

    @Override
	public ServiceResponseType updatePluginSettings(String serviceClassName, SettingListType settings, String username) throws ExchangeModelException {
    	LOG.info("Update plugin settings for " + serviceClassName);
    	Service service = dao.getServiceByServiceClassName(serviceClassName);
    	if(service != null) {
    		List<ServiceSetting> newSettings = ServiceMapper.mapSettingsList(service, settings, username);
    		service.getServiceSettingList().clear();
    		service.getServiceSettingList().addAll(newSettings);
    		dao.updateService(service);
    		return ServiceMapper.toServiceModel(service);
    	}
    	throw new ExchangeDaoException("No plugin found when update plugin settings");
	}
    
    @Override
    public List<SettingType> getPluginSettings(String serviceClassName) throws ExchangeModelException {
        LOG.info("Get plugin settings:{}",serviceClassName);

        List<SettingType> settings = new ArrayList<>();
        try {
            List<ServiceSetting> entityList = dao.getServiceSettings(serviceClassName);
            for (ServiceSetting entity : entityList) {
                settings.add(ServiceMapper.toModel(entity));
            }

        } catch (ExchangeDaoException e) {
            LOG.error("[ Error when getting list. {}] {}", serviceClassName, e.getMessage());
            throw new ExchangeModelException("[ Error when getting list. ]");
        }

        return settings;

    }

    @Override
    public List<CapabilityType> getPluginCapabilities(String serviceClassName) throws ExchangeModelException {
        LOG.info("Get plugin capabilities:{}",serviceClassName);

        List<CapabilityType> capabilities = new ArrayList<>();
        try {
            List<ServiceCapability> entityList = dao.getServiceCapabilities(serviceClassName);
            for (ServiceCapability entity : entityList) {
                capabilities.add(ServiceMapper.toModel(entity));
            }

            

        } catch (ExchangeDaoException e) {
            LOG.error("[ Error when getting list.{} ] {}",serviceClassName, e.getMessage());
            throw new ExchangeModelException("[ Error when getting list. ]");
        }
        return capabilities;
    }

    @Override
    public ServiceResponseType getPlugin(String serviceClassName) throws ExchangeModelException {
        try {
            Service service = dao.getServiceByServiceClassName(serviceClassName);
            return ServiceMapper.toServiceModel(service);

        } catch (NullPointerException e) {
            LOG.error("[ Error when getting Service. {} ] {}",serviceClassName,  e.getMessage());
            throw new ExchangeModelException("[ Error when getting service. ]");
        }
    }

	@Override
	public ServiceResponseType updatePluginStatus(String serviceName, StatusType status, String username) throws ExchangeModelException {
		Service service = dao.getServiceByServiceClassName(serviceName);
		if(service != null) {
			service.setStatus(status.name());
            service.setUpdatedBy(username);
            service.setUpdated(new Date());
			return ServiceMapper.toServiceModel(dao.updateService(service));
		}
		throw new ExchangeModelException("[ Error when update plugin " + serviceName + " with status " + status.name());
	}
}