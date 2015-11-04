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
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.CreateUnsentMessageResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.notifications.NotificationMessage;

@Stateless
public class ExchangeEventOutgoingServiceBean implements ExchangeEventOutgoingService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventOutgoingServiceBean.class);

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> exchangeErrorEvent;
    
    @EJB
    MessageProducer producer;

    @EJB
    ExchangeMessageConsumer consumer;

    @EJB
    ExchangeLogService exchangeLog;
    
    @EJB
    ExchangeService exchangeService;

    @Inject
    @ExchangeSendingQueueEvent
    Event<NotificationMessage> sendingQueueEvent;

	@Override
    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message) {
        LOG.info("Send report to plugin");

        try {
            SendMovementToPluginRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendMovementToPluginRequest.class);
            SendMovementToPluginType sendReport = request.getReport();
            
            List<PluginType> type = new ArrayList<>();
            type.add(sendReport.getPluginType());
            
            List<ServiceResponseType> services = exchangeService.getServiceList(type);
            if(services.isEmpty()) {
                String faultMessage = "No plugins of type " + sendReport.getPluginType() + " found";
                LOG.debug(faultMessage);
				exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, faultMessage)));
            } else {
            	ServiceResponseType service = services.get(0);
            	String serviceName = service.getServiceClassName();
            	String text = ExchangePluginRequestMapper.createSetReportRequest(sendReport.getTimestamp(), sendReport.getMovement());
            	
                if(validate(service, sendReport, text, message.getJmsMessage())) {
                	String pluginMessageId = producer.sendEventBusMessage(text, serviceName);
                	
                	//System.out.println("SendReport: PluginMessageId: " + pluginMessageId);
                	
                	try {
                		ExchangeLogType log = ExchangeLogMapper.getSendMovementExchangeLog(sendReport);
                		exchangeLog.logAndCache(log, pluginMessageId);
                	} catch (ExchangeLogException e) {
                		LOG.error(e.getMessage());
                	}
                	
                	//TODO
                	//producer.sendMessageOnQueue(arg0, arg1)
                } else {
                	LOG.debug("Validation error. Event sent to caller.");
                }
            }
        } catch (ExchangeException e) {
            LOG.error("[ Error when sending report to plugin ]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending message to plugin ")));
        }
    }

	private boolean validate(ServiceResponseType service, SendMovementToPluginType sendReport, String reportText, TextMessage origin) {
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
        	LOG.info("Plugin to send report to is not started");
        	try {
				String text = ExchangeDataSourceRequestMapper.mapCreateUnsentMessage(sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), sendReport.getRecipient(), reportText);
				String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
				TextMessage response = consumer.getMessage(messageId, TextMessage.class);
				CreateUnsentMessageResponse createUnsentMessageResponse = JAXBMarshaller.unmarshallTextMessage(response, CreateUnsentMessageResponse.class);
				sendingQueueEvent.fire(new NotificationMessage("messageId", createUnsentMessageResponse.getUnsentMessageId()));
			} catch (ExchangeModelMarshallException | ExchangeMessageException e) {
				LOG.error("Couldn't add message to unsent list");
			}
        }
        return true;
    }
	
	@Override
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message) {
        LOG.info("Send command to plugin");

        try {
            SetCommandRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetCommandRequest.class);
            String pluginName = request.getCommand().getPluginName();

            if(validate(request.getCommand(), message.getJmsMessage())) {
            	String text = ExchangePluginRequestMapper.createSetCommandRequest(request.getCommand());
            	String pluginMessageId = producer.sendEventBusMessage(text, pluginName);
            	
            	try {
            		ExchangeLogType log = ExchangeLogMapper.getSendCommandExchangeLog(request.getCommand());
            		exchangeLog.logAndCache(log, pluginMessageId);
            	} catch (ExchangeLogException e) {
            		LOG.error(e.getMessage());
            	}
            	
				//response back to Rules or MobileTerminal
            	AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeOK();
				String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
            	producer.sendModuleResponseMessage(message.getJmsMessage(), moduleResponse);
            } else {
            	LOG.debug("Validation error. Event sent to caller.");
            }
            
        } catch (NullPointerException | ExchangeException e) {
            LOG.error("[ Error when sending command to plugin ]");
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending command to plugin ")));
        }
    }
	
	private boolean validate(CommandType command, TextMessage origin) {
        if(command == null) {
        	String faultMessage = "No command";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
			return false;
        } else if(command.getCommand() == null) {
        	String faultMessage = "No command type";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
			return false;
        } else if(command.getPluginName() == null) {
        	String faultMessage = "No plugin to send to";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
			return false;
        } else if(command.getTimestamp() == null) {
        	String faultMessage = "No timestamp";
			exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
			return false;
        } //TODO if service is started, else sendingQueue
        return true;
	}
}
