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
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeAuditRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import static eu.europa.ec.fisheries.uvms.exchange.service.util.StringUtil.compressServiceClassName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeServiceBean.class);

    @EJB
    ParameterService parameterService;

    @EJB
    ExchangeMessageConsumer consumer;

    @EJB
    MessageProducer producer;

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ServiceResponseType registerService(ServiceType data, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeServiceException {
        LOG.info("Register service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapRegisterServiceToString(data, capabilityList, settingList, username);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ServiceResponseType serviceResponseType = ExchangeDataSourceResponseMapper.mapToRegisterServiceResponse(response, messageId);
            sendAuditLogMessageForRegisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
            return serviceResponseType;
        } catch (ExchangeModelMapperException | ExchangeMessageException ex) {
            throw new ExchangeServiceException(ex.getMessage());
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
        LOG.info("Unregister service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapUnregisterServiceToString(data, username);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ServiceResponseType serviceResponseType = ExchangeDataSourceResponseMapper.mapToUnregisterServiceResponse(response, messageId);
            sendAuditLogMessageForUnregisterService(compressServiceClassName(serviceResponseType.getServiceClassName()), username);
            return serviceResponseType;
        } catch (ExchangeModelMapperException | ExchangeMessageException ex) {
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
        LOG.info("Get list invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetServiceListToString(pluginTypes);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToServiceTypeListFromResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ServiceResponseType upsertSettings(String serviceClassName, SettingListType settingListType, String username) throws ExchangeServiceException {
        LOG.info("Upsert settings in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapSetSettingsToString(serviceClassName, settingListType, username);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            sendAuditLogMessageForUpdateService(compressServiceClassName(serviceClassName), username);
            return ExchangeDataSourceResponseMapper.mapToServiceTypeFromSetSettingsResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
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
        LOG.info("Get by id invoked in service layer");
        throw new ExchangeServiceException("Get by id not implemented in service layer");
    }

    @Override
    public ServiceResponseType getService(String serviceId) throws ExchangeServiceException {
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetServiceToString(serviceId);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToServiceTypeFromGetServiceResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ExchangeLogType createExchangeLog(ExchangeLogType exchangeLog, String username) throws ExchangeServiceException {
        LOG.info("Create Exchange log invoked in service layer");
        //TODO: Do we use this method??
        try {
            String request = ExchangeDataSourceRequestMapper.mapCreateExchangeLogToString(exchangeLog, username);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ExchangeLogType exchangeLogType = ExchangeDataSourceResponseMapper.mapToExchangeLogTypeFromCreateExchageLogResponse(response, messageId);
            sendAuditLogMessageForCreateExchangeLog(exchangeLog.getGuid(), username);
            return exchangeLog;
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public GetLogListByQueryResponse getExchangeLogByQuery(ExchangeListQuery query) throws ExchangeServiceException {
        LOG.info("Create Exchange log invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetExchageLogListByQueryToString(query);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToGetLogListByQueryResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ServiceResponseType updateServiceStatus(String serviceClassName, StatusType status, String username) throws ExchangeServiceException {
        LOG.info("Update service status invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapSetServiceStatus(serviceClassName, status, username);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            sendAuditLogMessageForUpdateServiceStatus(serviceClassName, status, username);
            return ExchangeDataSourceResponseMapper.mapSetServiceResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
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
                sendAuditLogMessageForServiceStatusStarted(compressServiceClassName(serviceName), username);
            case STOPPED:
                sendAuditLogMessageForServiceStatusStopped(compressServiceClassName(serviceName), username);
            default:
                sendAuditLogMessageForServiceStatusUnknown(compressServiceClassName(serviceName), username);
        }
    }

    private void sendAuditLogMessageForCreateExchangeLog(String guid, String username){
        try {
            String request = ExchangeAuditRequestMapper.mapCreateExchangeLog(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log was created with guid: " + guid);
        }
    }
}