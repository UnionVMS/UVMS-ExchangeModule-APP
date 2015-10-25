package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.config.service.UVMSConfigService;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
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

    @Inject
    ParameterService parameterService;
    
    @Inject
    UVMSConfigService configService;
    
    private boolean checkPluginType(PluginType pluginType, String responseTopicMessageSelector, String messageId) throws ExchangeModelMarshallException, ExchangeMessageException {
    	LOG.debug("checkPluginType " + pluginType.name());
        if(PluginType.EMAIL == pluginType || PluginType.FLUX == pluginType) {
        	//Check if type already exists
        	List<PluginType> type = new ArrayList<>();
        	type.add(pluginType);
			try {
				List<ServiceResponseType> services = exchangeService.getServiceList(type);
				if(!services.isEmpty()) {
		        	
		        	//TODO log to audit log
		        	
		        	String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Plugin of " + pluginType + " already registered. Only one is allowed.");
		        	producer.sendEventBusMessage(response, responseTopicMessageSelector);
		        	return false;
		        }
			} catch (ExchangeServiceException e) {
				String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Exchange service exception when registering plugin [ " + e.getMessage() + " ]");
				producer.sendEventBusMessage(response, responseTopicMessageSelector);
				return false;
			}
        }
        return true;
    }
    
    private void registerService(RegisterServiceRequest register, String responseTopicMessageSelector, String messageId) throws ExchangeModelMarshallException, ExchangeMessageException {
    	try {
    		ServiceResponseType service = exchangeService.registerService(register.getService(), register.getCapabilityList(), register.getSettingList());
    		
    		//push to config module
    		try {
    			String serviceClassName = register.getService().getServiceClassName();
    			SettingListType settings = register.getSettingList();
    			for(SettingType setting : settings.getSetting()) {
    				String description = "Plugin " + serviceClassName + " " + setting.getKey() + " setting";
    				String key = serviceClassName+PARAMETER_DELIMETER+setting.getKey();
    				configService.pushSettingToConfig(SettingTypeMapper.map(key, setting.getValue(), description), false);
    			}
    		} catch (ConfigServiceException e) {
    			LOG.error("Couldn't register plugin settings in config parameter table");
    		}
    		
        	//TODO log to exchange log
        	String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseOK(messageId, service);
        	producer.sendEventBusMessage(response, responseTopicMessageSelector);
		} catch (ExchangeServiceException e) {
			String response = ExchangePluginResponseMapper.mapToRegisterServiceResponseNOK(messageId, "Exchange service exception when registering plugin [ " + e.getMessage() + " ]");
			producer.sendEventBusMessage(response, responseTopicMessageSelector);
		}
    }
    
	@Override
	public void registerService(@Observes @RegisterServiceEvent PluginMessageEvent event) {
		LOG.info("register service");
		TextMessage textMessage = event.getJmsMessage();
		String responseTopicMessageSelector = null;
		try {
			RegisterServiceRequest register = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
	        responseTopicMessageSelector = register.getResponseTopicMessageSelector();
	        String messageId = textMessage.getJMSMessageID();
	        boolean sendMessage = true;

	        if(register.getService() != null) {
	        	sendMessage = checkPluginType(register.getService().getPluginType(), responseTopicMessageSelector, messageId);
	        	
		        if(sendMessage) {
		        	registerService(register, responseTopicMessageSelector, messageId);
		        }
	        }
		} catch (ExchangeModelMarshallException | ExchangeMessageException | JMSException e) {
			LOG.error("Register service exception " + e.getMessage());
			errorEvent.fire(new PluginMessageEvent(textMessage, responseTopicMessageSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when register service")));
		}
	}

	@Override
	public void unregisterService(@Observes @UnRegisterServiceEvent PluginMessageEvent event) {
		LOG.info("unregister service");
		TextMessage textMessage = event.getJmsMessage();
		String responseTopicMessageSelector = null;
		try {
			UnregisterServiceRequest unregister = JAXBMarshaller.unmarshallTextMessage(textMessage, UnregisterServiceRequest.class);
			responseTopicMessageSelector = unregister.getResponseTopicMessageSelector();
			
			ServiceResponseType service = exchangeService.unregisterService(unregister.getService());
			String serviceClassName = service.getServiceClassName();
			
			try {
				SettingListType settingList = service.getSettingList();
				for(SettingType setting : settingList.getSetting()) {
					String key = serviceClassName+PARAMETER_DELIMETER+setting.getKey();
					configService.pushSettingToConfig(SettingTypeMapper.map(key, setting.getValue()), true);
				}
			} catch (ConfigServiceException e) {
				LOG.error("Couldn't unregister plugin settings in config parameter table");
			}
			
			//NO ack back to plugin
	        //TODO log to exchange log
		} catch (ExchangeModelMarshallException | ExchangeServiceException e) {
			LOG.error("Unregister service exception " + e.getMessage());
			errorEvent.fire(new PluginMessageEvent(textMessage, responseTopicMessageSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when unregister service")));
		}
	}

	@Override
	public void setConfig(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent) {
		switch(settingEvent.getType()) {
		case STORE:
			//ConfigModule and/or Exchange module deployed
			break;
		case UPDATE:
			LOG.info("ConfigModule updated parameter table with settings of plugins");
			try {
				String key = settingEvent.getKey();
				String value = parameterService.getStringValue(key);
			
				String[] splittedKey = key.split(PARAMETER_DELIMETER);
				String serviceClassName = splittedKey[0];
				String settingKey = splittedKey[1];
				
				SettingListType settingListType = new SettingListType();
				SettingType settingType = new SettingType();
				settingType.setKey(settingKey);
				settingType.setValue(value);
				settingListType.getSetting().add(settingType);

				ServiceResponseType service = exchangeService.upsertSettings(serviceClassName, settingListType);
				
				String text = ExchangePluginRequestMapper.createSetConfigRequest(service.getSettingList());
				producer.sendEventBusMessage(text, serviceClassName);
				
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
	public boolean start(String serviceClassName) throws ExchangeServiceException {
		if(serviceClassName == null) {
			throw new InputArgumentException("No service to start");
		}
		try {
			String text = ExchangePluginRequestMapper.createStartRequest();
			producer.sendEventBusMessage(text, serviceClassName);
			return true;
		} catch (ExchangeModelMarshallException e) {
			throw new ExchangeServiceException("[ Couldn't map start request for " + serviceClassName + " ]");
		} catch (ExchangeMessageException e) {
			throw new ExchangeServiceException("[ Couldn't send start request for " + serviceClassName + " ]");
		}
	}

	@Override
	public boolean stop(String serviceClassName) throws ExchangeServiceException {
		if(serviceClassName == null) {
			throw new InputArgumentException("No service to stop");
		}
		try {
			String text = ExchangePluginRequestMapper.createStopRequest();
			producer.sendEventBusMessage(text, serviceClassName);
			return true;
		} catch (ExchangeModelMarshallException e){
			throw new ExchangeServiceException("[ Couldn't map stop request for " + serviceClassName + " ]");
		} catch (ExchangeMessageException e) {
			throw new ExchangeServiceException("[ Couldn't send stop request for " + serviceClassName + " ]");
		}
	}

	@Override
	public boolean ping(String serviceClassName) throws ExchangeServiceException {
		if(serviceClassName == null) {
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
