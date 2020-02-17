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
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ServiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.Instant;
import java.util.List;

@Stateless
public class ServiceRegistryModelBean {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryModelBean.class);

    @EJB
    private ServiceRegistryDaoBean dao;

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

    public Service unregisterService(String serviceClassName, String username) {
        // Look for existing service
        Service service = dao.getServiceByServiceClassName(serviceClassName);
        if (service != null) {
            service.setActive(false);
            service.setStatus(false);
            service.getServiceCapabilityList().clear();
            service.getServiceSettingList().clear();
            service.setUpdatedBy(username);
            return dao.updateService(service);
        }
        //TODO handle unable to unregister
        throw new IllegalArgumentException("[ No service to unregister ]");
    }

    public List<Service> getPlugins(List<PluginType> pluginTypes) {
        List<Service> entityList;
        if (pluginTypes == null || pluginTypes.isEmpty()) {
            entityList = dao.getServices();
        } else {
            entityList = dao.getServicesByTypes(pluginTypes);
        }
        return entityList;
    }

    public List<Service> getPluginsByCapability(CapabilityTypeType capabilityType) {
        return dao.getServicesByCapability(capabilityType);
    }

    public Service updatePluginSettings(String serviceClassName, ServiceSetting newSetting, String username) {
        LOG.info("Update plugin settings for " + serviceClassName);
        Service service = dao.getServiceByServiceClassName(serviceClassName);
        if (service != null) {
            for (ServiceSetting setting : service.getServiceSettingList()) {
                if (setting.getSetting().equals(newSetting.getSetting()) && !setting.getValue().equalsIgnoreCase(newSetting.getValue())) {
                    setting.setValue(newSetting.getValue());
                    setting.setUpdatedTime(Instant.now());
                    setting.setUser(username);
                }
            }
            dao.updateService(service);
            return service;
        }
        throw new IllegalArgumentException("No plugin found when update plugin settings for plugin: " + serviceClassName);
    }

    public Service getPluginByName(String pluginName) {
        return dao.getServiceByName(pluginName);
    }

    public Service getServiceByServiceClassName(String serviceClassName) {
        return dao.getServiceByServiceClassName(serviceClassName);
    }

    public Service getPlugin(String serviceClassName) {
        return dao.getServiceByServiceClassName(serviceClassName);
    }

    public ServiceResponseType updatePluginStatus(String serviceName, StatusType status, String username) {
        Service service = dao.getServiceByServiceClassName(serviceName);
        if (service != null) {
            service.setStatus(StatusType.STARTED.equals(status));
            service.setUpdatedBy(username);
            service.setUpdated(Instant.now());
            return ServiceMapper.toServiceModel(dao.updateService(service));
        }
        throw new IllegalArgumentException("[ Error when update plugin " + serviceName + " with status " + status.name());
    }
}
