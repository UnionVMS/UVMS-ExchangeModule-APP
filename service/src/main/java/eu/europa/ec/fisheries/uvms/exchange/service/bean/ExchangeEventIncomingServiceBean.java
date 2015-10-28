package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventIncomingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.MovementMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;

@Stateless
public class ExchangeEventIncomingServiceBean implements ExchangeEventIncomingService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventIncomingServiceBean.class);

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> exchangeErrorEvent;

    @Inject
    @PluginErrorEvent
    Event<PluginMessageEvent> pluginErrorEvent;
    
    @EJB
    MessageProducer producer;
    
    @EJB
    ExchangeService exchangeService;

    @Override
    public void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        LOG.info("Get plugin config LIST_SERVICE");
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, GetServiceListRequest.class);
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
        } catch (ExchangeException e) {
            LOG.error("[ Error when getting plugin list from source ]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
        }
    }

    @Override
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
        LOG.info("Process movement");
        try {
            SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetMovementReportRequest.class);
            
            String pluginName = request.getRequest().getPluginName();
            PluginType pluginType = request.getRequest().getPluginType();
            String responseMessageTopicSelector = pluginName + ExchangeModelConstants.RESPONSE_TOPIC_ADDON_NAME;
            LOG.debug("Process movement from " + pluginName + " of " + pluginType + " type");
            
            if(validate(request.getRequest(), responseMessageTopicSelector, message.getJmsMessage())) {
            	//Send to exchange log
                try {
                	ExchangeLogType log = ExchangeLogMapper.getReceiveMovementExchangeLog(request.getRequest());
                	String text = ExchangeDataSourceRequestMapper.mapCreateExchangeLogToString(log);
                	producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
                } catch (ExchangeModelMapperException | ExchangeMessageException | ExchangeLogException e) {
                	LOG.error("Couldn't log movement to exchange log. " + e.getMessage());
            	}
            	
            	MovementBaseType baseMovement = request.getRequest().getMovement();
            	RawMovementType rawMovement = MovementMapper.getInstance().getMapper().map(baseMovement, RawMovementType.class);
            	if(rawMovement.getAssetId() != null && rawMovement.getAssetId().getAssetIdList() != null) {
                	rawMovement.getAssetId().getAssetIdList().addAll(MovementMapper.mapAssetIdList(baseMovement.getAssetId().getAssetIdList()));
            	}
            	if(baseMovement.getMobileTerminalId() != null && baseMovement.getMobileTerminalId().getMobileTerminalIdList() != null) {
                	rawMovement.getMobileTerminal().getMobileTerminalIdList().addAll(MovementMapper.mapMobileTerminalIdList(baseMovement.getMobileTerminalId().getMobileTerminalIdList()));
            	}
            
            	try {
            		String movement = RulesModuleRequestMapper.createSetMovementReportRequest(MovementMapper.mapPluginType(pluginType), rawMovement);
            		producer.sendMessageOnQueue(movement, MessageQueue.RULES);
            		
            		//TODO send back ack to plugin?
            	} catch (RulesModelMapperException | ExchangeMessageException e) {
            		PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Movement sent cannot be sent to Rules module [ " + e.getMessage() +" ]");
            		pluginErrorEvent.fire(new PluginMessageEvent(message.getJmsMessage(), responseMessageTopicSelector, fault));
            	}
            } else {
            	LOG.debug("Validation error. Event sent to plugin");
            }
        } catch (ExchangeModelMarshallException e) {
        	//Cannot send back fault to unknown sender
        	LOG.error("Couldn't map to SetMovementReportRequest when processing movement from plugin");
        }
    }

	private boolean validate(SetReportMovementType setReport, String responseMessageTopicSelector, TextMessage origin) {
		if(setReport == null) {
        	String faultMessage = "No setReport request";
			pluginErrorEvent.fire(new PluginMessageEvent(origin, responseMessageTopicSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
			return false;
        } else if(setReport.getMovement() == null) {
        	String faultMessage = "No movement in setReport request";
        	pluginErrorEvent.fire(new PluginMessageEvent(origin, responseMessageTopicSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
			return false;
        } else if(setReport.getPluginType() == null) {
        	String faultMessage = "No pluginType in setReport request";
        	pluginErrorEvent.fire(new PluginMessageEvent(origin, responseMessageTopicSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
			return false;
        } else if(setReport.getPluginName() == null || setReport.getPluginName().isEmpty()) {
        	String faultMessage = "No pluginName in setReport request";
        	pluginErrorEvent.fire(new PluginMessageEvent(origin, responseMessageTopicSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
			return false;
        } else if(setReport.getTimestamp() == null) {
        	String faultMessage = "No timestamp in setReport request";
        	pluginErrorEvent.fire(new PluginMessageEvent(origin, responseMessageTopicSelector, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
			return false;
        }
        return true;
    }
    
    @Override
    public void ping(@Observes @PingEvent ExchangeMessageEvent message) {
        try {
            PingResponse response = new PingResponse();
            response.setResponse("pong");
            producer.sendModuleResponseMessage(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(response));
        } catch (ExchangeModelMarshallException e) {
            LOG.error("[ Error when marshalling ping response ]");
        }
    }

    @Override
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message) {
        LOG.info("Process acknowledge");
        
        try {
			AcknowledgeResponse response = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), AcknowledgeResponse.class);
			AcknowledgeType acknowledge = response.getResponse();
			String serviceClassName = response.getServiceClassName();
			ExchangePluginMethod method = response.getMethod();
			switch(method) {
			case SET_COMMAND:
				break;
			case SET_CONFIG:
				break;
			case SET_REPORT:
				break;
			case PING:
				LOG.info(serviceClassName + " answered on ping: " + acknowledge.getType() + ": " + acknowledge.getMessage());
				break;
			case START:
				handleStatusAcknowledge(serviceClassName, acknowledge, StatusType.STARTED);
				break;
			case STOP:
				handleStatusAcknowledge(serviceClassName, acknowledge, StatusType.STOPPED);
				break;
			default:
				LOG.error("Received unknown acknowledge: " + method);
				break;
			}
		} catch (ExchangeModelMarshallException e) {
			LOG.error("Process acknowledge couldn't be marshalled");
		} catch (ExchangeServiceException e) {
			//TODO couldn't save acknowledge in exchange service
			LOG.error("Couldn't process acknowledge in exchange service: " + e.getMessage());
		}
    }

    private void handleStatusAcknowledge(String serviceClassName, AcknowledgeType ack, StatusType status) throws ExchangeServiceException {
    	switch(ack.getType()) {
    	case OK:
    		exchangeService.updateServiceStatus(serviceClassName, status);
    		break;
    	case NOK:
    		//TODO
    		LOG.error("Couldn't start service");
    		break;
    	}
    }
}
