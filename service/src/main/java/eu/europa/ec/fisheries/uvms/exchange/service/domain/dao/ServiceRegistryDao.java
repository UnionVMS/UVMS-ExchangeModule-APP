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
package eu.europa.ec.fisheries.uvms.exchange.service.domain.dao;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.ExchangeDaoException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ServiceRegistryDao {

    /**
     * Create entity in database
     *
     * @param service
     * @return
     * @throws
     * ExchangeDaoException
     */
    Service createEntity(Service service) throws ExchangeDaoException;

    /**
     * Get entity by internal entity id
     *
     * @param id
     * @return
     * @throws
     * ExchangeDaoException
     */
    Service getEntityById(String id) throws ExchangeDaoException;

    /**
     * Update entity in database
     *
     * @param service
     * @return
     * @throws
     * ExchangeDaoException
     */
    Service updateService(Service service) throws ExchangeDaoException;

    /**
     * Delete entity from database
     *
     * @param serviceId
     * @throws
     * ExchangeDaoException
     */
    void deleteEntity(Long serviceId) throws ExchangeDaoException;

    /**
     * Get all services (FIND_ALL)
     *
     * @return
     * @throws
     * ExchangeDaoException
     */
    List<Service> getServices() throws ExchangeDaoException;

    /**
     * Get services depending on plugin types
     * @return
     * @throws ExchangeDaoException
     */
    List<Service> getServicesByTypes(List<PluginType> pluginTypes) throws ExchangeDaoException;
    
    /**
     *
     * Gets all capabilities for a service
     *
     * @param serviceClassName
     * @return
     * @throws ExchangeDaoException
     */
    List<ServiceCapability> getServiceCapabilities(String serviceClassName) throws ExchangeDaoException;

    /**
     *
     * gets all settings for a service
     *
     * @param serviceClassName
     * @return
     * @throws ExchangeDaoException
     */
    List<ServiceSetting> getServiceSettings(String serviceClassName) throws ExchangeDaoException;

    /**
     * Get service by Service Class Name
     *
     * @param serviceClassName
     * @return
     */
    Service getServiceByServiceClassName(String serviceClassName);

    /**
     *
     * @param mappedServiceName
     * @return
     */
    Service getServiceByMappedServiceName(String mappedServiceName);

}