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
    public ServiceResponseType registerService(ServiceType data, CapabilityListType capabilityList, SettingListType settingList) throws ExchangeServiceException {
        LOG.info("Register service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapRegisterServiceToString(data, capabilityList, settingList);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ServiceResponseType serviceResponseType = ExchangeDataSourceResponseMapper.mapToRegisterServiceResponse(response, messageId);
            sendAuditLogMessageForRegisterService(compressServiceClassName(serviceResponseType.getServiceClassName()));
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
    public ServiceResponseType unregisterService(ServiceType data) throws ExchangeServiceException {
        LOG.info("Unregister service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapUnregisterServiceToString(data);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ServiceResponseType serviceResponseType = ExchangeDataSourceResponseMapper.mapToUnregisterServiceResponse(response, messageId);
            sendAuditLogMessageForUnregisterService(compressServiceClassName(serviceResponseType.getServiceClassName()));
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
    public ServiceResponseType upsertSettings(String serviceClassName, SettingListType settingListType) throws ExchangeServiceException {
        LOG.info("Upsert settings in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapSetSettingsToString(serviceClassName, settingListType);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            sendAuditLogMessageForUpdateService(compressServiceClassName(serviceClassName));
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
    public ExchangeLogType createExchangeLog(ExchangeLogType exchangeLog) throws ExchangeServiceException {
        LOG.info("Create Exchange log invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapCreateExchangeLogToString(exchangeLog);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ExchangeLogType exchangeLogType = ExchangeDataSourceResponseMapper.mapToExchangeLogTypeFromCreateExchageLogResponse(response, messageId);
            sendAuditLogMessageForCreateExchangeLog(exchangeLog.getGuid());
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
    public ServiceResponseType updateServiceStatus(String serviceClassName, StatusType status) throws ExchangeServiceException {
        LOG.info("Update service status invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapSetServiceStatus(serviceClassName, status);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            sendAuditLogMessageForUpdateServiceStatus(serviceClassName, status);
            return ExchangeDataSourceResponseMapper.mapSetServiceResponse(response, messageId);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    private void sendAuditLogMessageForRegisterService(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapRegisterService(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange registered service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUnregisterService(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapUnregisterService(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange unregistered service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForServiceStatusStopped(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStopped(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange stopped service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForServiceStatusUnknown(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusUnknown(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange set service: " + serviceName +"status to unknown" );
        }
    }

    private void sendAuditLogMessageForServiceStatusStarted(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapServiceStatusStarted(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUpdateService(String serviceName){
        try {
            String request = ExchangeAuditRequestMapper.mapUpdateService(serviceName);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange started service: " + serviceName );
        }
    }

    private void sendAuditLogMessageForUpdateServiceStatus(String serviceName, StatusType status){
        switch (status){
            case STARTED:
                sendAuditLogMessageForServiceStatusStarted(compressServiceClassName(serviceName));
            case STOPPED:
                sendAuditLogMessageForServiceStatusStopped(compressServiceClassName(serviceName));
            default:
                sendAuditLogMessageForServiceStatusUnknown(compressServiceClassName(serviceName));
        }
    }

    private void sendAuditLogMessageForCreateExchangeLog(String guid){
        try {
            String request = ExchangeAuditRequestMapper.mapCreateExchangeLog(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log was created with guid: " + guid);
        }
    }
}
