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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.uvms.exchange.bean.ServiceRegistryModelBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeServiceBean{

    final static Logger LOG = LoggerFactory.getLogger(ExchangeServiceBean.class);

    @EJB
    private ServiceRegistryModelBean serviceRegistryModel;

    /**
     * Register a service
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public Service registerService(Service data, String username) {
        LOG.info("Register service invoked in service layer: {} {}",data,username);
        Service service = serviceRegistryModel.registerService(data, username);
        return service;
    }

    /**
     * Unregister a service
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public Service unregisterService(ServiceType data, String username) {
        LOG.info("Unregister service invoked in service layer: {} {}",data,username);
        Service service = serviceRegistryModel.unregisterService(data, username);
        return service;
    }

    /**
     * Get a list with plugins depending on plugin types
     *
     * @return
     * @throws ExchangeServiceException
     */
    public List<Service> getServiceList(List<PluginType> pluginTypes) {
        LOG.info("Get list invoked in service layer:{}",pluginTypes);
        List<Service> plugins = serviceRegistryModel.getPlugins(pluginTypes);
        return plugins;
    }

    /**
     * Upsert Service with settings
     * @param serviceClassName - name of service to upsert
     * @param settingList - list of settings to upsert
     * @return updated service
     * @throws ExchangeServiceException
     */
    public Service upsertSettings(String serviceClassName, List<ServiceSetting> settingList, String username) {
        LOG.info("Upsert settings in service layer: {} {} {}",serviceClassName, settingList,username);
        Service updatedSettings = serviceRegistryModel.updatePluginSettings(serviceClassName, settingList, username);
        return updatedSettings;
    }

    /**
     *
     * @param serviceId
     * @return
     * @throws ExchangeServiceException
     */
    public Service getService(String serviceId) {
        Service plugin = serviceRegistryModel.getPlugin(serviceId);
        return plugin;
    }


    /**
     *
     * @param serviceClassName
     * @param status
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceResponseType updateServiceStatus(String serviceClassName, StatusType status, String username) {
        LOG.info("Update service status invoked in service layer: {} {} {}",serviceClassName,status,username);
        ServiceResponseType updatedServiceStatus = serviceRegistryModel.updatePluginStatus(serviceClassName, status, username);
        return updatedServiceStatus;
    }
}