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

import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
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
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.EventService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.MovementMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;

@Stateless
public class ExchangeEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventServiceBean.class);

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

    @EJB
    MessageProducer producer;

    @EJB
    ExchangeService exchangeService;
    
    @EJB
    ParameterService parameterService;

    @Override
    public void getPluginConfig(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        LOG.info("Received MessageRecievedEvent");
        try {
        	TextMessage jmsMessage = message.getJmsMessage();
        	GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, GetServiceListRequest.class);
        	List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
			producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
		} catch (ExchangeException e) {
			LOG.error("[ Error when getting plugin list from source]");
            errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
		}
    }

    @Override
	public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
		LOG.info("Process movement");
		//TODO process movement
		try {
			SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetMovementReportRequest.class);
			
			//TODO log to exchange log (received message)
			//reportType.getFrom()
			//reportType.getTimestamp()
			MovementBaseType baseMovement = request.getRequest().getMovement();
			RawMovementType rawMovement = MovementMapper.getMapper().map(baseMovement, RawMovementType.class);
			if(rawMovement.getAssetId() != null && rawMovement.getAssetId().getAssetIdList() != null) {
				rawMovement.getAssetId().getAssetIdList().addAll(MovementMapper.mapAssetIdList(baseMovement.getAssetId().getAssetIdList()));
			}
			if(rawMovement.getMobileTerminal() != null && rawMovement.getMobileTerminal().getMobileTerminalIdList() != null) {
				rawMovement.getMobileTerminal().getMobileTerminalIdList().addAll(MovementMapper.mapMobileTerminalIdList(baseMovement.getMobileTerminalId().getMobileTerminalIdList()));
			}
			String movement = RulesModuleRequestMapper.createSetMovementReportRequest(rawMovement);
			producer.sendMessageOnQueue(movement, DataSourceQueue.RULES);
		} catch (ExchangeModelMarshallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExchangeMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RulesModelMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@Override
	public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message) {
		LOG.info("Send report to plugin");
		
		try {
			SendMovementToPluginRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendMovementToPluginRequest.class);
			SendMovementToPluginType sendReport = request.getReport();
		
			List<PluginType> type = new ArrayList<>();
			type.add(sendReport.getPluginType());
			List<ServiceResponseType> services = exchangeService.getServiceList(type);
			if(!services.isEmpty()) {
				//TODO do some validation logic
				//check so request.getReport().getTo() exists
				//check so the type of service has type request.getReport().getPlugin()
				//check so the plugin is started
				//otherwise answer to sender (rules) so rules can do something about it (tickets)
				
				String serviceName = services.get(0).getServiceClassName(); //Use first and only
				ReportType report = new ReportType();
				report.setTimestamp(sendReport.getTimestamp());
				//when elog is supported add logic
				report.setMovement(sendReport.getMovement());
				report.setType(ReportTypeType.MOVEMENT);
				
				String text = ExchangePluginRequestMapper.createSetReportRequest(report);
				producer.sendEventBusMessage(text, serviceName);
				
				//TODO log to exchange logs
				
			} else {
				errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending message to plugin ")));
			}
		} catch (ExchangeException e) {
			LOG.error("[ Error when sending report to plugin ]");
			errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending message to plugin ")));
		}
	}

	@Override
	public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message) {
		LOG.info("Process acknowledge");
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
			errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending command to plugin ")));
		}
	}

}
