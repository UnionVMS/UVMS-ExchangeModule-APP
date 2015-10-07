package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeServiceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.notifications.NotificationEvent;
import eu.europa.ec.fisheries.uvms.notifications.NotificationMessage;
import eu.europa.ec.fisheries.wsdl.module.v1.PullSettingsResponse;
import eu.europa.ec.fisheries.wsdl.module.v1.PushSettingsResponse;
import eu.europa.ec.fisheries.wsdl.types.v1.PullSettingsStatus;
import eu.europa.ec.fisheries.wsdl.types.v1.SettingType;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeServiceBean.class);

    @EJB
    ParameterService parameterService;

    @EJB
    ExchangeMessageConsumer consumer;

    @EJB
    MessageProducer producer;

    @Inject
    @NotificationEvent
    Event<NotificationMessage> notification;

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ServiceType registerService(ServiceType data) throws ExchangeServiceException {
        LOG.info("Register service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapRegisterServiceToString(data);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToRegisterServiceResponse(response);
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
    public ServiceType unregisterService(ServiceType data) throws ExchangeServiceException {
        LOG.info("Unregister service invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapUnregisterServiceToString(data);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToUnregisterServiceResponse(response);
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
    public List<ServiceType> getServiceList() throws ExchangeServiceException {
        LOG.info("Get list invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetServiceListToString();
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToServiceTypeListFromResponse(response);
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

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws ExchangeServiceException
     */
    @Override
    public ServiceType update(ServiceType data) throws ExchangeServiceException {
        LOG.info("Update invoked in service layer");
        throw new ExchangeServiceException("Update not implemented in service layer");
    }

    @Override
    public ServiceType getService(String serviceId) throws ExchangeServiceException {
        LOG.info("Get list invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetServiceToString(serviceId);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToServiceTypeFromGetServiceResponse(response);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public ExchangeLogType createExchangeLog(ExchangeLogType exchangeLog) throws ExchangeServiceException {
        LOG.info("Create Exchange log invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapCreateExchangeLogToString(exchangeLog);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToExchangeLogTypeFromCreateExchageLogResponse(response);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public GetLogListByQueryResponse getExchangeLogByQuery(ExchangeListQuery query) throws ExchangeServiceException {
        LOG.info("Create Exchange log invoked in service layer");
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetExchageLogListByQueryToString(query);
            String messageId = producer.sendDataSourceMessage(request, DataSourceQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToGetLogListByQueryResponse(response);
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public String setPluginReport(CommandType data) throws ExchangeServiceException {
        LOG.info("Send poll to plugin method invoked in service layer");
        try {
            String request = ExchangeServiceRequestMapper.mapCreatePollRequest(data);
            notification.fire(new NotificationMessage("pollTrackId", data));
            producer.sendEventBusMessage(request, data.getTo());
            return "Message sent!";
        } catch (ExchangeMessageException | ExchangeModelMapperException e) {
            throw new ExchangeServiceException(e.getMessage());
        }
    }

    @Override
    public void syncSettingsWithConfig() throws ExchangeServiceException {
        try {
            boolean pullSuccess = pullSettingsFromConfig();
            if (!pullSuccess) {
                boolean pushSuccess = pushSettingsToConfig();
                if (!pushSuccess) {
                    throw new ExchangeMessageException("Failed to push missing settings to Config.");
                }
            }
        } catch (ModelMarshallException | ExchangeMessageException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when synchronizing settings with Config module. ] {}", e.getMessage());
            throw new ExchangeServiceException("Error when synchronizing settings with Config module.", e);
        }
    }

    /**
     * @return true if settings were pulled successful, or false if they are missing in the Config module
     * @throws ModelMarshallException
     * @throws ExchangeMessageException
     * @throws ExchangeModelMarshallException
     * @throws ExchangeServiceException 
     */
    private boolean pullSettingsFromConfig() throws ModelMarshallException, ExchangeMessageException, ExchangeModelMarshallException, ExchangeServiceException {
        String request = ModuleRequestMapper.toPullSettingsRequest(ExchangeModelConstants.MODULE_NAME);
        String messageId = producer.sendConfigMessage(request);
        TextMessage response = consumer.getMessage(messageId, TextMessage.class);
        PullSettingsResponse pullResponse = JAXBMarshaller.unmarshallTextMessage(response, PullSettingsResponse.class);
        if (pullResponse.getStatus() == PullSettingsStatus.MISSING) {
            return false;
        }

        storeSettings(pullResponse.getSettings());
        return true;
    }

    /**
     * @return true if settings were pushed successfully
     * @throws ExchangeServiceException
     * @throws ExchangeModelMarshallException
     * @throws ExchangeMessageException
     * @throws ModelMarshallException
     */
    private boolean pushSettingsToConfig() throws ExchangeServiceException, ExchangeModelMarshallException, ExchangeMessageException, ModelMarshallException {
        String request = ModuleRequestMapper.toPushSettingsRequest(ExchangeModelConstants.MODULE_NAME, getSettings());
        String messageId = producer.sendConfigMessage(request);
        TextMessage response = consumer.getMessage(messageId, TextMessage.class);
        PushSettingsResponse pushResponse = JAXBMarshaller.unmarshallTextMessage(response, PushSettingsResponse.class);

        if (pushResponse.getStatus() != PullSettingsStatus.OK) {
            return false;
        }

        storeSettings(pushResponse.getSettings());
        return true;
    }

    private List<SettingType> getSettings() {
        List<SettingType> settings = new ArrayList<>();
        for (ParameterKey key : ParameterKey.values()) {
            try {
                SettingType setting = new SettingType();
                setting.setKey(key.getKey());
                setting.setValue(parameterService.getStringValue(key));
                settings.add(setting);
            }
            catch (ExchangeServiceException e) {
                LOG.error("[ Error when getting settings. ] {}", e.getMessage());
            }
        }

        return settings;
    }

    private void storeSettings(List<SettingType> settings) throws ExchangeServiceException {
        parameterService.clearAll();
        for (SettingType setting: settings) {
            try {
                ParameterKey key = ParameterKey.valueOfKey(setting.getKey());
                parameterService.setStringValue(key, setting.getValue());
            }
            catch (IllegalArgumentException | NullPointerException e) {
                LOG.warn("[ Setting with key " + setting.getKey() + " is not recognized by this module. ]");
            }
            catch (ExchangeServiceException e) {
                LOG.error("[ Error when storing setting. ]", e);
            }
        }
    }
}
