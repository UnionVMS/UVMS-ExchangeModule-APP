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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.ServiceRegistryModel;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeAuditRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeServiceBean.class);

    @EJB
    private ExchangeConsumer consumer;

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ServiceRegistryModel serviceRegistryModel;

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ServiceResponseType registerService(ServiceType data, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeServiceException {
        LOG.info("Register service invoked in service layer: {} {}",data,username);
        try {
            ServiceResponseType serviceResponseType = serviceRegistryModel.registerService(data, capabilityList, settingList, username);
            //sendAuditLogMessageForRegisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
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
        LOG.info("Unregister service invoked in service layer: {} {}",data,username);
        try {
            ServiceResponseType serviceResponseType = serviceRegistryModel.unregisterService(data, username);
            //sendAuditLogMessageForUnregisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
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
        LOG.info("Get list invoked in service layer:{}",pluginTypes);
        try {
            List<ServiceResponseType> plugins = serviceRegistryModel.getPlugins(pluginTypes);
            return plugins;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ServiceResponseType upsertSettings(String serviceClassName, SettingListType settingListType, String username) throws ExchangeServiceException {
        LOG.info("Upsert settings in service layer: {} {} {}",serviceClassName,settingListType,username);
        try {
            ServiceResponseType updatedSettings = serviceRegistryModel.updatePluginSettings(serviceClassName, settingListType, username);
            //sendAuditLogMessageForUpdateService(compressServiceClassName(serviceClassName), username);
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
        LOG.info("Get by id invoked in service layer:{}",id);
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
        LOG.info("Update service status invoked in service layer: {} {} {}",serviceClassName,status,username);
        try {
            ServiceResponseType updatedServiceStatus = serviceRegistryModel.updatePluginStatus(serviceClassName, status, username);
            //sendAuditLogMessageForUpdateServiceStatus(serviceClassName, status, username);
            return updatedServiceStatus;
        } catch (ExchangeModelException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    private void sendAuditLogMessageForRegisterService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapRegisterService(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange registered service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUnregisterService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapUnregisterService(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange unregistered service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForServiceStatusStopped(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStopped(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange stopped service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForServiceStatusUnknown(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusUnknown(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange set service: " + serviceName +"status to unknown" );
        }
    }

    private void sendAuditLogMessageForServiceStatusStarted(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStarted(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUpdateService(String serviceName, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapUpdateService(serviceName, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUpdateServiceStatus(String serviceName, StatusType status, String username){
        switch (status){
            case STARTED:
                //sendAuditLogMessageForServiceStatusStarted(compressServiceClassName(serviceName), username);
                break;
            case STOPPED:
                //sendAuditLogMessageForServiceStatusStopped(compressServiceClassName(serviceName), username);
                break;
            default:
                //sendAuditLogMessageForServiceStatusUnknown(compressServiceClassName(serviceName), username);
                break;
        }
    }
}