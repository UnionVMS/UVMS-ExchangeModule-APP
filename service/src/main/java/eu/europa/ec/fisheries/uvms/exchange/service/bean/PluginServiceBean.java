package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.config.service.UVMSConfigService;
import eu.europa.ec.fisheries.uvms.exchange.message.event.UpdatePluginSettingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.SettingTypeMapper;

@Stateless
public class PluginServiceBean implements PluginService {

    final static Logger LOG = LoggerFactory.getLogger(PluginServiceBean.class);

    private static final String PARAMETER_DELIMETER = "/";

    @Inject
    @PluginErrorEvent
    Event<PluginMessageEvent> errorEvent;

    @EJB
    ExchangeService exchangeService;

    @EJB
    MessageProducer producer;

    @EJB
    ExchangeMessageConsumer consumer;

    @Inject
    ParameterService parameterService;

    @Inject
    UVMSConfigService configService;

    private boolean checkPluginType(PluginType pluginType, String responseTopicMessageSelector, String messageId) throws ExchangeModelMarshallException, ExchangeMessageException {
        LOG.debug("checkPluginType " + pluginType.name());
        if (PluginType.EMAIL == pluginType || PluginType.FLUX == pluginType || PluginType.NAF == pluginType) {
            //Check if type already exists
            List<PluginType> type = new ArrayList<>();
            type.add(pluginType);
            try {
                List<ServiceResponseType> services = exchangeService.getServiceList(type);
                if (!services.isEmpty()) {
                    for(ServiceResponseType service : services){
                        if(service.isActive()){
                            //TODO log to audit log
                            String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Plugin of " + pluginType + " already registered. Only one is allowed.");
                            producer.sendEventBusMessage(response, responseTopicMessageSelector);
                            return false;
                        }
                    }
                }
            } catch (ExchangeServiceException e) {
                String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Exchange service exception when registering plugin [ " + e.getMessage() + " ]");
                producer.sendEventBusMessage(response, responseTopicMessageSelector);
                return false;
            }
        }
        return true;
    }

    private void registerService(RegisterServiceRequest register, String messageId) throws ExchangeModelMarshallException, ExchangeMessageException {
        try {
            overrideSettingsFromConfig(register);
            ServiceResponseType service = exchangeService.registerService(register.getService(), register.getCapabilityList(), register.getSettingList(), register.getService().getName());
            //push to config module
            try {
                String serviceClassName = register.getService().getServiceClassName();
                SettingListType settings = register.getSettingList();
                for (SettingType setting : settings.getSetting()) {
                    String description = "Plugin " + serviceClassName + " " + setting.getKey() + " setting";
                    String key = serviceClassName + PARAMETER_DELIMETER + setting.getKey();
                    configService.pushSettingToConfig(SettingTypeMapper.map(key, setting.getValue(), description), false);
                }
            } catch (ConfigServiceException e) {
                LOG.error("Couldn't register plugin settings in config parameter table");
            }

  
            //TODO log to exchange log
            String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseOK(messageId, service);
            producer.sendEventBusMessage(response, register.getService().getServiceResponseMessageName());

            setServiceStatusOnRegister(register.getService().getServiceClassName());

        } catch (ExchangeServiceException | ExchangeModelMapperException e) {
            String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Exchange service exception when registering plugin [ " + e.getMessage() + " ]");
            producer.sendEventBusMessage(response, register.getService().getServiceResponseMessageName());
        }
    }

    private void setServiceStatusOnRegister(String serviceClassName) throws ExchangeModelMapperException, ExchangeMessageException, ExchangeServiceException {
        ServiceResponseType service = exchangeService.getService(serviceClassName);
        if (service != null) {
            StatusType status = service.getStatus();
            if (StatusType.STARTED.equals(status)) {
                LOG.info("Starting service {}", serviceClassName);
                start(serviceClassName);
            } else if (StatusType.STOPPED.equals(status)) {
                LOG.info("Stopping service {}", serviceClassName);
                stop(serviceClassName);
            } else {
                LOG.error("[ Status was null for service {} ]", serviceClassName);
            }
        }
    }

    @Override
    public void registerService(@Observes @RegisterServiceEvent PluginMessageEvent event) {
        LOG.info("register service");
        TextMessage textMessage = event.getJmsMessage();
        RegisterServiceRequest register = null;
        try {
            register = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
            String messageId = textMessage.getJMSMessageID();
            boolean sendMessage = true;

            if (register.getService() != null) {
                sendMessage = checkPluginType(register.getService().getPluginType(), register.getService().getServiceResponseMessageName(), messageId);

                if (sendMessage) {
                    registerService(register, messageId);
                }
            }

        } catch (ExchangeModelMarshallException | ExchangeMessageException | JMSException e) {
            LOG.error("Register service exception " + e.getMessage());
            errorEvent.fire(new PluginMessageEvent(textMessage, register.getService(), ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when register service")));
        }
    }

    private void overrideSettingsFromConfig(RegisterServiceRequest registerServiceRequest) {
        List<SettingType> currentRequestSettings = registerServiceRequest.getSettingList().getSetting();
        try {
            List<eu.europa.ec.fisheries.schema.config.types.v1.SettingType> configServiceSettings = configService.getSettings(registerServiceRequest.getService().getServiceClassName());
            Map<String, SettingType>configServiceSettingsMap = putConfigSettingsInAMap(configServiceSettings);
            if(!configServiceSettingsMap.isEmpty()){
                String serviceClassName = registerServiceRequest.getService().getServiceClassName();
                for(SettingType type : currentRequestSettings){
                    String key = serviceClassName + PARAMETER_DELIMETER + type.getKey();
                    SettingType configSettingType = configServiceSettingsMap.get(key);
                    if(configSettingType!=null && !configSettingType.getValue().equalsIgnoreCase(type.getValue())){
                        type.setValue(configSettingType.getValue());
                    }
                }
            }

        } catch (ConfigServiceException e) {
            LOG.error("Register service exception, cannot read Exchange settings from Config " + e.getMessage());
            // Ignore when we can't get the settings from Config. It is possible there is no Config module setup.
        }
    }

    private Map<String, SettingType> putConfigSettingsInAMap(List<eu.europa.ec.fisheries.schema.config.types.v1.SettingType> settings){
        Map<String, SettingType> settingTypeMap = new HashMap<>();
        if(settings!=null && !settings.isEmpty()) {
            for (eu.europa.ec.fisheries.schema.config.types.v1.SettingType configSettingType : settings) {
                SettingType type = new SettingType();
                type.setKey(configSettingType.getKey());
                type.setValue(configSettingType.getValue());
                settingTypeMap.put(configSettingType.getKey(), type);
            }
        }
        return  settingTypeMap;
    }

    @Override
    public void unregisterService(@Observes @UnRegisterServiceEvent PluginMessageEvent event) {
        LOG.info("unregister service");
        TextMessage textMessage = event.getJmsMessage();
        ServiceResponseType service = null;
        try {
            UnregisterServiceRequest unregister = JAXBMarshaller.unmarshallTextMessage(textMessage, UnregisterServiceRequest.class);
            service = exchangeService.unregisterService(unregister.getService(), unregister.getService().getName());
            String serviceClassName = service.getServiceClassName();

            //NO ack back to plugin
            //TODO log to exchange log
        } catch (ExchangeModelMarshallException | ExchangeServiceException e) {
            LOG.error("Unregister service exception " + e.getMessage());
            errorEvent.fire(new PluginMessageEvent(textMessage, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when unregister service")));
        }
    }
    
    private void updatePluginSetting(String serviceClassName, SettingType updatedSetting, String username) throws ExchangeServiceException, ExchangeModelMarshallException, ExchangeMessageException {
    	SettingListType settingListType = new SettingListType();
    	settingListType.getSetting().add(updatedSetting);

    	ServiceResponseType service = exchangeService.upsertSettings(serviceClassName, settingListType, username);

        // Send the plugin settings to the topic where all plugins should listen to
    	String text = ExchangePluginRequestMapper.createSetConfigRequest(service.getSettingList());
    	producer.sendEventBusMessage(text, serviceClassName);
    }
    
    @Override
    public void setConfig(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent) {
        switch (settingEvent.getType()) {
            case STORE:
                //ConfigModule and/or Exchange module deployed
                break;
            case UPDATE:
                LOG.info("ConfigModule updated parameter table with settings of plugins");
                try {
                    String key = settingEvent.getKey();
                    String value = parameterService.getStringValue(key);
                    String settingKey;
                    String[] splittedKey = key.split(PARAMETER_DELIMETER);
                    String serviceClassName = splittedKey[0];
                    if(splittedKey.length>1){
                        settingKey = splittedKey[1];

                    }else{
                        //TODO: Investigate if the key should begin with module name +/+ key or only key ???
                        settingKey = splittedKey[0];
                    }
                    SettingType settingType = new SettingType();
                    settingType.setKey(settingKey);
                    settingType.setValue(value);
                    updatePluginSetting(serviceClassName, settingType, "UVMS");
                } catch (ConfigServiceException e) {
                    LOG.error("Couldn't get updated parameter table value");
                } catch (ExchangeServiceException e) {
                    LOG.error("Couldn't upsert settings in exchange");
                } catch (ExchangeModelMarshallException e) {
                    LOG.error("Couldn't create plugin set config request");
                } catch (ExchangeMessageException e) {
                    LOG.error("Couldn't send message to plugin");
                }
                break;
            case DELETE:
                LOG.info("ConfigModule removed parameter setting");
                break;
        }
    }

    @Override
	public void updatePluginSetting(@Observes @UpdatePluginSettingEvent ExchangeMessageEvent settingEvent) {
		LOG.info("update plugin setting from module queue");
		try {
			TextMessage jmsMessage = settingEvent.getJmsMessage();
			UpdatePluginSettingRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, UpdatePluginSettingRequest.class);
			updatePluginSetting(request.getServiceClassName(), request.getSetting(), request.getUsername());
			
			String text = ExchangeModuleResponseMapper.mapUpdateSettingResponse(ExchangeModuleResponseMapper.mapAcknowledgeTypeOK());
			producer.sendModuleResponseMessage(settingEvent.getJmsMessage(), text);
		} catch (ExchangeModelMarshallException | ExchangeServiceException | ExchangeMessageException e) {
			LOG.error("Couldn't unmarshall update setting request");
			settingEvent.setErrorFault(ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Couldn't update plugin setting"));
			producer.sendModuleErrorResponseMessage(settingEvent);
		}
		
		
	}
    
    @Override
    public boolean start(String serviceClassName) throws ExchangeServiceException {
        if (serviceClassName == null) {
            throw new InputArgumentException("No service to start");
        }
        try {
            if (isServiceRegistered(serviceClassName)){
                String text = ExchangePluginRequestMapper.createStartRequest();
                producer.sendEventBusMessage(text, serviceClassName);
                return true;
            }else{
                throw new ExchangeServiceException("Service with service class name: "+ serviceClassName + " does not exist");
            }
        } catch (ExchangeModelMarshallException e) {
            throw new ExchangeServiceException("[ Couldn't map start request for " + serviceClassName + " ]");
        } catch (ExchangeMessageException e) {
            throw new ExchangeServiceException("[ Couldn't send start request for " + serviceClassName + " ]");
        }
    }

    private boolean isServiceRegistered(String serviceClassName) throws ExchangeServiceException {
        List<ServiceResponseType> serviceList = exchangeService.getServiceList(null);
        for (ServiceResponseType serviceResponseType : serviceList){
            if(serviceResponseType.getServiceClassName().equalsIgnoreCase(serviceClassName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean stop(String serviceClassName) throws ExchangeServiceException {
        if (serviceClassName == null) {
            throw new InputArgumentException("No service to stop");
        }
        try {
            if(isServiceRegistered(serviceClassName)) {
                String text = ExchangePluginRequestMapper.createStopRequest();
                producer.sendEventBusMessage(text, serviceClassName);
                return true;
            }else{
                throw new ExchangeServiceException("Service with service class name: "+ serviceClassName + " does not exist");
            }
        } catch (ExchangeModelMarshallException e) {
            throw new ExchangeServiceException("[ Couldn't map stop request for " + serviceClassName + " ]");
        } catch (ExchangeMessageException e) {
            throw new ExchangeServiceException("[ Couldn't send stop request for " + serviceClassName + " ]");
        }
    }

    @Override
    public boolean ping(String serviceClassName) throws ExchangeServiceException {
        if (serviceClassName == null) {
            throw new InputArgumentException("No service to ping");
        }
        try {
            String text = ExchangePluginRequestMapper.createPingRequest();
            producer.sendEventBusMessage(text, serviceClassName);
            return true;
        } catch (ExchangeModelMarshallException e) {
            throw new ExchangeServiceException("[ Couldn't map ping request for " + serviceClassName + " ]");
        } catch (ExchangeMessageException e) {
            throw new ExchangeServiceException("[ Couldn't send ping request for " + serviceClassName + " ]");
        }

    }
}
