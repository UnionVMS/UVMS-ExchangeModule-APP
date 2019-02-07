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
package eu.europa.ec.fisheries.uvms.exchange.dao.bean;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;
import eu.europa.ec.fisheries.uvms.exchange.dao.Dao;
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ServiceRegistryDaoBean extends Dao implements ServiceRegistryDao {

    private static final String SERVICE_CLASS_NAME_PARAMETER = "serviceClassName";
    private static final String SERVICE_MAP_NAME_PARAMETER = "mapName";

    private final static Logger LOG = LoggerFactory.getLogger(ServiceRegistryDaoBean.class);

    @Override
    public Service createEntity(Service entity) throws ExchangeDaoException {
        try {
            em.persist(entity);
            return entity;
        } catch (EntityExistsException e) {
            LOG.error("[ Error when creating. ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when creating. Service already exists. ] ");
        } catch (IllegalArgumentException e) {
            LOG.error("[ Error when creating. ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when creating. Illegal input. ]");
        } catch (Exception e) {
            LOG.error("[ Error when creating. ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when creating. ]");
        }
    }

    @Override
    public Service getEntityById(String id) throws ExchangeDaoException {
        try {
            return em.find(Service.class, new Long(id));
        } catch (Exception e) {
            LOG.error("[ Error when getting entity by ID. ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting entity by ID. ] " + id);
        }
    }

    @Override
    public Service updateService(Service entity) throws ExchangeDaoException {
        try {
            em.merge(entity);
            em.flush();
            return entity;
        } catch (Exception e) {
            LOG.error("[ Error when updating entity ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when updating entity ]");
        }
    }

    @Override
    public void deleteEntity(Long id) throws ExchangeDaoException {
        LOG.info("Delete Entity not implemented yet.");
        throw new ExchangeDaoException("Not implemented yet");
    }

    @Override
    public List<Service> getServices() throws ExchangeDaoException {
        try {
            TypedQuery<Service> query = em.createNamedQuery(ExchangeConstants.SERVICE_FIND_ALL, Service.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting service list ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting service list ] ");
        }
    }

    @Override
	public List<Service> getServicesByTypes(List<PluginType> pluginTypes) throws ExchangeDaoException {
        try {
            TypedQuery<Service> query = em.createNamedQuery(ExchangeConstants.SERVICE_FIND_BY_TYPES, Service.class);
            query.setParameter("types", pluginTypes);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            LOG.error("[ Error when getting service list by types ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting service list by types ] ");
        } catch (Exception e) {
            LOG.error("[ Error when getting service list by types ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting service list by types] ");
        }
	}
    
    @Override
    public List<ServiceCapability> getServiceCapabilities(String serviceClassName) throws ExchangeDaoException {
        try {
            TypedQuery<ServiceCapability> query = em.createNamedQuery(ExchangeConstants.CAPABILITY_FIND_BY_SERVICE, ServiceCapability.class);
            query.setParameter(SERVICE_CLASS_NAME_PARAMETER, serviceClassName);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting capabilities ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting capabilities ] ");
        }
    }

    @Override
    public List<ServiceSetting> getServiceSettings(String serviceClassName) throws ExchangeDaoException {
        try {
            TypedQuery<ServiceSetting> query = em.createNamedQuery(ExchangeConstants.SETTING_FIND_BY_SERVICE, ServiceSetting.class);
            query.setParameter("serviceClassName", serviceClassName);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting settings ] {}", e.getMessage());
            throw new ExchangeDaoException("[ Error when getting settings ] ");
        }
    }

    @Override
    public Service getServiceByServiceClassName(String serviceClassName) {
        try {
            TypedQuery<Service> query = em.createNamedQuery(ExchangeConstants.SERVICE_FIND_BY_SERVICE_CLASS_NAME, Service.class);
            query.setParameter(SERVICE_CLASS_NAME_PARAMETER, serviceClassName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Service getServiceByMappedServiceName(String mappedServiceName) {
        try {
            TypedQuery<Service> query = em.createNamedQuery(ExchangeConstants.SERVICE_FIND_BY_NAME, Service.class);
            query.setParameter(SERVICE_MAP_NAME_PARAMETER, mappedServiceName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}