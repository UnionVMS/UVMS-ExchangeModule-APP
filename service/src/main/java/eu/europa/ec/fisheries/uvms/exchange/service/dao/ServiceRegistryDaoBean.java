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
package eu.europa.ec.fisheries.uvms.exchange.service.dao;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
public class ServiceRegistryDaoBean extends AbstractDao {

    private static final String SERVICE_CLASS_NAME_PARAMETER = "serviceClassName";

    final static Logger LOG = LoggerFactory.getLogger(ServiceRegistryDaoBean.class);

    // registerService
    public Service createEntity(Service entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    // getService
    public Service getEntityById(String id) {
        return em.find(Service.class, UUID.fromString(id));
    }

    // updateService
    public Service updateService(Service entity) {
        em.merge(entity);
        em.flush();
        return entity;
    }

    // deactivateService
    public void deleteEntity(UUID id ) {
        Service s = em.find(Service.class, id);
        em.remove(s);
    }

    public List<Service> getServices() {
        try {
            TypedQuery<Service> query = em.createNamedQuery(Service.SERVICE_FIND_ALL, Service.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when updating entity ] {}", e.getMessage());
            throw new RuntimeException("[ Error when getting service list ] ", e);
        }
    }

	public List<Service> getServicesByTypes(List<PluginType> pluginTypes) {
            TypedQuery<Service> query = em.createNamedQuery(Service.SERVICE_FIND_BY_TYPES, Service.class);
            query.setParameter("types", pluginTypes);
            return query.getResultList();
	}

    public List<Service> getServicesByCapability(CapabilityTypeType capability) {
            TypedQuery<Service> query = em.createNamedQuery(Service.SERVICE_FIND_BY_CAPABILITY, Service.class);
            query.setParameter("capability", capability);
            return query.getResultList();
    }

    public List<ServiceCapability> getServiceCapabilities(String serviceClassName) {
            TypedQuery<ServiceCapability> query = em.createNamedQuery(ServiceCapability.CAPABILITY_FIND_BY_SERVICE, ServiceCapability.class);
            query.setParameter(SERVICE_CLASS_NAME_PARAMETER, serviceClassName);
            return query.getResultList();
    }

    public List<ServiceSetting> getServiceSettings(String serviceClassName) {
            TypedQuery<ServiceSetting> query = em.createNamedQuery(ServiceSetting.SETTING_FIND_BY_SERVICE, ServiceSetting.class);
            query.setParameter("serviceClassName", serviceClassName);
            return query.getResultList();
    }

    public Service getServiceByName(String serviceName) {
        try {
            TypedQuery<Service> query = em.createNamedQuery(Service.SERVICE_FIND_BY_NAME, Service.class);
            query.setParameter("name", serviceName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Service getServiceByServiceClassName(String serviceClassName) {
        try {
            TypedQuery<Service> query = em.createNamedQuery(Service.SERVICE_FIND_BY_SERVICE_CLASS_NAME, Service.class);
            query.setParameter(SERVICE_CLASS_NAME_PARAMETER, serviceClassName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
