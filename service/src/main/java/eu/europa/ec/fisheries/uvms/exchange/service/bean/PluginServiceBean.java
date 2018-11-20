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

import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.config.service.UVMSConfigService;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.UpdatePluginSettingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.SettingTypeMapper;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Slf4j
public class PluginServiceBean implements PluginService {

    private static final String PARAMETER_DELIMETER = "\\.";

    @Inject
    @PluginErrorEvent
    private Event<PluginMessageEvent> errorEvent;

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ExchangeConsumer consumer;

    @EJB
    private ParameterService parameterService;

    @EJB
    private UVMSConfigService configService;

    private boolean checkPluginType(PluginType pluginType, String responseTopicMessageSelector, String messageId) throws ExchangeModelMarshallException, ExchangeMessageException {
        log.debug("[INFO] CheckPluginType " + pluginType.name());
        if (PluginType.EMAIL == pluginType || PluginType.NAF == pluginType) {
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
                    configService.pushSettingToConfig(SettingTypeMapper.map(setting.getKey(), setting.getValue(), description), false);
                }
            } catch (ConfigServiceException e) {
                log.error("Couldn't register plugin settings in config parameter table");
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
                log.info("Starting service {}", serviceClassName);
                start(serviceClassName);
            } else if (StatusType.STOPPED.equals(status)) {
                log.info("Stopping service {}", serviceClassName);
                stop(serviceClassName);
            } else {
                log.error("[ Status was null for service {} ]", serviceClassName);
            }
        }
    }

    @Override
    public void registerService(@Observes @RegisterServiceEvent PluginMessageEvent event) {
        TextMessage textMessage = event.getJmsMessage();
        RegisterServiceRequest register = null;
        try {
            register = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
            log.info("[INFO] Received @RegisterServiceEvent : {}" , register.getService());
            String messageId = textMessage.getJMSMessageID();
            boolean sendMessage;
            if (register.getService() != null) {
                sendMessage = checkPluginType(register.getService().getPluginType(), register.getService().getServiceResponseMessageName(), messageId);
                if (sendMessage) {
                    registerService(register, messageId);
                }
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException | JMSException e) {
            log.error("[ERROR] Register service exception {} {}",event, e.getMessage());
            errorEvent.fire(new PluginMessageEvent(textMessage, register.getService(), ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when register service")));
        }
    }

    private void overrideSettingsFromConfig(RegisterServiceRequest registerServiceRequest) {
        List<SettingType> currentRequestSettings = registerServiceRequest.getSettingList().getSetting();
        try {
            List<eu.europa.ec.fisheries.schema.config.types.v1.SettingType> configServiceSettings = configService.getSettings(registerServiceRequest.getService().getServiceClassName());
            Map<String, SettingType>configServiceSettingsMap = putConfigSettingsInAMap(configServiceSettings);
            if(!configServiceSettingsMap.isEmpty()){
                for(SettingType type : currentRequestSettings){
                    SettingType configSettingType = configServiceSettingsMap.get(type.getKey());
                    if(configSettingType!=null && !configSettingType.getValue().equalsIgnoreCase(type.getValue())){
                        type.setValue(configSettingType.getValue());
                    }
                }
            }

        } catch (ConfigServiceException e) {
            log.error("Register service exception, cannot read Exchange settings from Config {} {}",registerServiceRequest, e.getMessage());
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
        log.info("[INFO] Received @UnRegisterServiceEvent request : {}", event);
        TextMessage textMessage = event.getJmsMessage();
        ServiceResponseType service = null;
        try {
            UnregisterServiceRequest unregister = JAXBMarshaller.unmarshallTextMessage(textMessage, UnregisterServiceRequest.class);
            service = exchangeService.unregisterService(unregister.getService(), unregister.getService().getName());
            String serviceClassName = service.getServiceClassName();
            //NO ack back to plugin
            //TODO log to exchange log
        } catch (ExchangeModelMarshallException | ExchangeServiceException e) {
            log.error("Unregister service exception " + e.getMessage());
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
                log.info("ConfigModule updated parameter table with settings of plugins");
                try {
                    String key = settingEvent.getKey();
                    String value = parameterService.getStringValue(key);
                    String settingKey;
                    String[] splittedKey = key.split(PARAMETER_DELIMETER);
                    if (splittedKey.length > 2) {
                        settingKey = key;
                        String serviceClassName = "";
                        for (int i = 0; i < splittedKey.length - 2; i++) {
                            serviceClassName += splittedKey[i] + ".";
                        }
                        serviceClassName += splittedKey[splittedKey.length - 2];

                        SettingType settingType = new SettingType();
                        settingType.setKey(key);
                        settingType.setValue(value);
                        updatePluginSetting(serviceClassName, settingType, "UVMS");
                    } else {
                        log.error("No key or malformed key sent in settingEvent: key: {}, value: {}", key, value);
                    }
                } catch (ConfigServiceException e) {
                    log.error("Couldn't get updated parameter table value");
                } catch (ExchangeServiceException e) {
                    log.error("Couldn't upsert settings in exchange");
                } catch (ExchangeModelMarshallException e) {
                    log.error("Couldn't create plugin set config request");
                } catch (ExchangeMessageException e) {
                    log.error("Couldn't send message to plugin");
                }
                break;
            case DELETE:
                log.info("ConfigModule removed parameter setting");
                break;
        }
    }

    @Override
	public void updatePluginSetting(@Observes @UpdatePluginSettingEvent ExchangeMessageEvent settingEvent) {
		try {
			TextMessage jmsMessage = settingEvent.getJmsMessage();
			UpdatePluginSettingRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, UpdatePluginSettingRequest.class);
            log.info("Received @UpdatePluginSettingEvent from module queue:{}" , request.toString());
			updatePluginSetting(request.getServiceClassName(), request.getSetting(), request.getUsername());
			String text = ExchangeModuleResponseMapper.mapUpdateSettingResponse(ExchangeModuleResponseMapper.mapAcknowledgeTypeOK());
			producer.sendModuleResponseMessage(settingEvent.getJmsMessage(), text);
		} catch (ExchangeModelMarshallException | ExchangeServiceException | ExchangeMessageException | MessageException e) {
			log.error("Couldn't unmarshall update setting request");
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