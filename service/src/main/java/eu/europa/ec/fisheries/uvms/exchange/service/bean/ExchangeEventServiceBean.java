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

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.MovementMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;

@Stateless
public class ExchangeEventServiceBean implements ExchangeEventService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventServiceBean.class);

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

    @EJB
    ParameterService parameterService;

    @Override
    public void getPluginConfig(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        LOG.info("Get plugin config LIST_SERVICE");
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, GetServiceListRequest.class);
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
        } catch (ExchangeException e) {
            LOG.error("[ Error when getting plugin list from source]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
        }
    }

    @Override
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
        LOG.info("Process movement");
        try {
            SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetMovementReportRequest.class);
            
            //TODO log to exchange log (received message)
            String pluginName = request.getRequest().getPluginName();
            PluginType pluginType = request.getRequest().getPluginType();
            String responseMessageTopicSelector = pluginName + ExchangeModelConstants.RESPONSE_TOPIC_ADDON_NAME;
            //TODO validate
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
            	producer.sendMessageOnQueue(movement, DataSourceQueue.RULES);
            } catch (RulesModelMapperException | ExchangeMessageException e) {
            	PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Movement sent cannot be sent to Rules module [ " + e.getMessage() +" ]");
            	pluginErrorEvent.fire(new PluginMessageEvent(message.getJmsMessage(), responseMessageTopicSelector, fault));
            }
        } catch (ExchangeModelMarshallException e) {
        	LOG.error("Couldn't map to SetMovementReportRequest when processing movement from plugin");
//TODO send error
        }
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

    private boolean validate(ServiceResponseType service, SendMovementToPluginType sendReport, TextMessage origin) {
    	String serviceName = service.getServiceClassName(); //Use first and only
        if(serviceName == null || serviceName.isEmpty()) {
        	String faultMessage = "First plugin of type " + sendReport.getPluginType() + " is invalid. Missing serviceClassName";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
			return false;
        } else if(!sendReport.getPluginType().equals(service.getPluginType())) {
        	String faultMessage = "First plugin of type " + sendReport.getPluginType() + " does not match plugin type of " + serviceName + ". Current type is " + service.getPluginType();
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
			return false;
        } else if(sendReport.getPluginName() != null && !serviceName.equalsIgnoreCase(sendReport.getPluginName())) {
        	String faultMessage = "First plugin of type " + sendReport.getPluginType() + " does not matching input of " + sendReport.getPluginName();
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
			return false;
        } else if(!StatusType.STARTED.equals(service.getStatus())) {
//TODO exchange log to sendingQueue
/*
        	String faultMessage = "First plugin of type " + sendReport.getPluginType() + " is not started.";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
			return false;*/
        }
        return true;
    }
    
    @Override
    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message) {
        LOG.info("Send report to plugin");

        try {
            SendMovementToPluginRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendMovementToPluginRequest.class);
            SendMovementToPluginType sendReport = request.getReport();
            
            boolean sendMessage = true;
            List<PluginType> type = new ArrayList<>();
            type.add(sendReport.getPluginType());
            List<ServiceResponseType> services = exchangeService.getServiceList(type);
            if(services.isEmpty()) {
                 String faultMessage = "No plugins of type " + sendReport.getPluginType() + " found";
				exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, faultMessage)));
				sendMessage = false;
            } else {
            	ServiceResponseType service = services.get(0);
            	String serviceName = service.getServiceClassName();
                sendMessage = validate(service, sendReport, message.getJmsMessage());
                
                if(sendMessage) {
                	ReportType report = new ReportType();
                	report.setTimestamp(sendReport.getTimestamp());
                	//when elog is supported add logic
                	report.setMovement(sendReport.getMovement());
                	report.setType(ReportTypeType.MOVEMENT);

                	String text = ExchangePluginRequestMapper.createSetReportRequest(report);
                	producer.sendEventBusMessage(text, serviceName);
                }

                //TODO log to exchange logs

            }
        } catch (ExchangeException e) {
            LOG.error("[ Error when sending report to plugin ]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending message to plugin ")));
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
    
    @Override
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message) {
        LOG.info("Send command to plugin");

        try {
            SetCommandRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetCommandRequest.class);
            String pluginName = request.getCommand().getPluginName();

            String text = ExchangePluginRequestMapper.createSetCommandRequest(request.getCommand());
            producer.sendEventBusMessage(text, pluginName);

            //TODO log to exchange logs

        } catch (NullPointerException | ExchangeException e) {
            LOG.error("[ Error when sending command to plugin ]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending command to plugin ")));
        }
    }

}
