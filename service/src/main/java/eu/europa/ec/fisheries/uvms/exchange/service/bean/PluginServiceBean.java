package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
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
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

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
    
	@Override
	public void registerService(@Observes @RegisterServiceEvent PluginMessageEvent event) {
		LOG.info("register service");
		TextMessage textMessage = event.getJmsMessage();
		String responseTopicMessageSelector = null;
		try {
			RegisterServiceRequest register = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
	        responseTopicMessageSelector = register.getResponseTopicMessageSelector();
	        boolean sendMessage = true;
	        
	        if(register.getService() != null) {
	        	PluginType pluginType = register.getService().getPluginType();
		        if(PluginType.EMAIL == pluginType || PluginType.FLUX == pluginType) {
		        	//Check if type already exists
		        	List<PluginType> type = new ArrayList<>();
		        	type.add(pluginType);
			        List<ServiceResponseType> services = exchangeService.getServiceList(type);
			        if(!services.isEmpty()) {
			        	
			        	//TODO log to exchange log
			        	//TODO better response message
			        	String response = ExchangePluginResponseMapper.mapToRegisterServiceResponse(AcknowledgeTypeType.NOK, services.get(0), null);
			        	producer.sendEventBusMessage(response, responseTopicMessageSelector);
			        	sendMessage = false;
			        }
		        }
		        
		        if(sendMessage) {
		        	
		        	ServiceResponseType service = exchangeService.registerService(register.getService(), register.getCapabilityList(), register.getSettingList());
		        	
		        	//set settings to parameter table, push to config module
		        	try {
		        		String serviceClassName = register.getService().getServiceClassName();
		        		SettingListType settings = register.getSettingList();
		        		for(SettingType setting : settings.getSetting()) {
		        			parameterService.setStringValue(serviceClassName+PARAMETER_DELIMETER+setting.getKey(), setting.getValue(), "Plugin " + serviceClassName + " " + setting.getKey() + " setting");
		        		}
						configService.pushAllModuleSettings(); //TODO change when implemented in config
					} catch (ConfigServiceException e) {
						LOG.error("Couldn't register plugin settings in config parameter table");
					}

		        	//TODO log to exchange log
		        
		        	SettingListType settings = service.getSettingList();
		        	String response = ExchangePluginResponseMapper.mapToRegisterServiceResponse(AcknowledgeTypeType.OK, service, settings);
		        	producer.sendEventBusMessage(response, responseTopicMessageSelector);
		        }
	        }

		} catch (ExchangeModelMarshallException | ExchangeServiceException | ExchangeMessageException e) {
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
			
			try {
				ServiceResponseType service = exchangeService.unregisterService(unregister.getService());
				String serviceClassName = service.getServiceClassName();
				
				SettingListType settingList = service.getSettingList();
				for(SettingType setting : settingList.getSetting()) {
					boolean removed = parameterService.removeParameter(serviceClassName+PARAMETER_DELIMETER+setting.getKey());
				}
				configService.pushAllModuleSettings(); //TODO only push changed settings
			} catch (ConfigServiceException e) {
				LOG.error("Couldn't remove settings from config parameter table");
			}
			
	        //TODO log to exchange log
	        
		} catch (ExchangeModelMarshallException | ExchangeServiceException e) {
			LOG.error("Unregister service exception " + e.getMessage());
			errorEvent.fire(new PluginMessageEvent(textMessage, responseTopicMessageSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Exception when unregister service")));
		}
	}

	@Override
	public void setConfig(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent) {
		switch(settingEvent.getEvent()) {
		case STORE:
			//ConfigModule and/or Exchange module deployed
			break;
		case UPDATE:
			//ConfigModule updated parameter table (settings of plugin, etc)
			LOG.info("ConfigModule updated parameter table with settings of plugins");
			//exchangeService.getServiceList(pluginTypes)
			//parameterService.getSettings(keys)
		case DELETE:
			LOG.info("ConfigModule removed parameter setting");
			break;
		}
	}
}
