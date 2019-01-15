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
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.ServiceRegistryModel;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.bean.ExchangeAuditProducerBean;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeAuditRequestMapper;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

@Stateless
@Slf4j
public class ExchangeServiceBean implements ExchangeService {

    @EJB
    private ServiceRegistryModel serviceRegistryModel;

    @EJB
    private ExchangeAuditProducerBean auditProducer;

    @EJB
    private ExchangeConsumer exchangeConsumer;

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ServiceResponseType registerService(ServiceType data, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeServiceException {
        log.info("Register service invoked in service layer: {} {}",data,username);
        try {
            ServiceResponseType serviceResponseType = serviceRegistryModel.registerService(data, capabilityList, settingList, username);
            //sendAuditlogMessageForRegisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
            return serviceResponseType;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ServiceResponseType unregisterService(ServiceType data, String username) throws ExchangeServiceException {
        log.info("Unregister service invoked in service layer: {} {}",data,username);
        try {
            ServiceResponseType serviceResponseType = serviceRegistryModel.unregisterService(data, username);
            //sendAuditlogMessageForUnregisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
            return serviceResponseType;
        } catch (ExchangeModelException ex) {
            throw new ExchangeServiceException(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws ExchangeServiceException
     */
    @Override
    public List<ServiceResponseType> getServiceList(List<PluginType> pluginTypes) throws ExchangeServiceException {
        log.info("Get list invoked in service layer:{}",pluginTypes);
        try {
            List<ServiceResponseType> plugins = serviceRegistryModel.getPlugins(pluginTypes);
            return plugins;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ServiceResponseType upsertSettings(String serviceClassName, SettingListType settingListType, String username) throws ExchangeServiceException {
        log.info("Upsert settings in service layer: {} {} {}",serviceClassName,settingListType,username);
        try {
            ServiceResponseType updatedSettings = serviceRegistryModel.updatePluginSettings(serviceClassName, settingListType, username);
            //sendAuditlogMessageForUpdateService(compressServiceClassName(serviceClassName), username);
            return updatedSettings;
        } catch (ExchangeModelException  e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @return
     * @throws ExchangeServiceException
     */
    @Override
    public ServiceType getById(Long id) throws ExchangeServiceException {
        log.info("Get by id invoked in service layer:{}",id);
        throw new ExchangeServiceException("Get by id not implemented in service layer");
    }

    @Override
    public ServiceResponseType getService(String serviceId) throws ExchangeServiceException {
        try {
            ServiceResponseType plugin = serviceRegistryModel.getPlugin(serviceId);
            return plugin;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }


    @Override
    public ServiceResponseType updateServiceStatus(String serviceClassName, StatusType status, String username) throws ExchangeServiceException {
        log.info("Update service status invoked in service layer: {} {} {}",serviceClassName,status,username);
        try {
            ServiceResponseType updatedServiceStatus = serviceRegistryModel.updatePluginStatus(serviceClassName, status, username);
            //sendAuditlogMessageForUpdateServiceStatus(serviceClassName, status, username);
            return updatedServiceStatus;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForRegisterService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapRegisterService(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange registered service: " + serviceName );
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForUnregisterService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapUnregisterService(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange unregistered service: " + serviceName );
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForServiceStatusStopped(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStopped(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange stopped service: " + serviceName );
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForServiceStatusUnknown(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusUnknown(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange set service: " + serviceName +"status to unknown" );
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForServiceStatusStarted(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStarted(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void sendAuditlogMessageForUpdateService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapUpdateService(serviceName, username);
            auditProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
        } catch (AuditModelMarshallException | MessageException e) {
            log.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    private void sendAuditlogMessageForUpdateServiceStatus(String serviceName, StatusType status, String username){
        switch (status){
            case STARTED:
                //sendAuditlogMessageForServiceStatusStarted(compressServiceClassName(serviceName), username);
                break;
            case STOPPED:
                //sendAuditlogMessageForServiceStatusStopped(compressServiceClassName(serviceName), username);
                break;
            default:
                //sendAuditlogMessageForServiceStatusUnknown(compressServiceClassName(serviceName), username);
                break;
        }
    }
}