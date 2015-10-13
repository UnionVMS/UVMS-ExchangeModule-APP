package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.config.module.v1.ConfigTopicBaseRequest;
import eu.europa.ec.fisheries.schema.config.module.v1.PushModuleSettingMessage;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.ObjectFactory;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ConfigMessageRecievedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.EventService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
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
        List<ServiceType> serviceList;
		try {
			serviceList = exchangeService.getServiceList();
			producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
		} catch (ExchangeException e) {
			LOG.error("[ Error when getting plugin list from source]");
            errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
		}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void receiveConfigMessageEvent(@Observes @ConfigMessageRecievedEvent ExchangeMessageEvent message) {
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            ConfigTopicBaseRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(jmsMessage, ConfigTopicBaseRequest.class);
            switch (baseRequest.getStatus()) {
            case DEPLOYED:
                exchangeService.syncSettingsWithConfig();
                break;
            case SETTING_CHANGED:
                updateParameter((PushModuleSettingMessage) JAXBMarshaller.unmarshallTextMessage(jmsMessage, PushModuleSettingMessage.class));
                break;
            default:
                break;
            }
        } catch (ExchangeServiceException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when synchronizing settings with Config. ] {}", e.getMessage());
        }
    }

    private void updateParameter(PushModuleSettingMessage message) throws ExchangeServiceException {
        if (message.getSetting().getModule() == null || message.getSetting().getModule().equals(ExchangeModelConstants.MODULE_NAME)) {
            SettingType setting = message.getSetting();
            ParameterKey key;
            try {
                key = ParameterKey.valueOfKey(setting.getKey());
            } catch (IllegalArgumentException e) {
                LOG.error("[ Received setting with unknown key: " + setting.getKey() + " ]");
                return;
            }

            switch (message.getAction()) {
            case SET:
                parameterService.setStringValue(key, setting.getValue());
                break;
            case RESET:
                parameterService.reset(key);
                break;
            default:
                break;
            }
        }
    }

    @Override
	public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
		LOG.info("Process movement");
		//TODO
		//PROCESS MOVEMENT
		try {
			SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetMovementReportRequest.class);
			//TODO log to exchange log (received message)
			//reportType.getFrom()
			//reportType.getTimestamp()
			
			String movement = RulesModuleRequestMapper.createSetMovementReportRequest(MovementMapper.getMapper().map(request.getRequest().getMovement(), RawMovementType.class));
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
			String serviceName = sendReport.getPluginName();
		
			//TODO do some validation logic
			//check so request.getReport().getTo() exists
			//check so the type of service has type request.getReport().getPlugin()
			//check so the plugin is started
			//otherwise answer to sender (rules) so rules can do something about it (tickets)
			
			ReportType report = new ReportType();
			report.setTo(serviceName);
			report.setTimestamp(sendReport.getTimestamp());
			
			//when elog is supported add logic
			report.setMovement(sendReport.getMovement());
			report.setType(ReportTypeType.MOVEMENT);
			
			String text = ExchangePluginRequestMapper.createSetReportRequest(report);
			producer.sendEventBusMessage(text, serviceName);
		} catch (ExchangeException e) {
			LOG.error("[ Error when sending report to plugin ]");
			errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Excpetion when sending message to plugin ")));
		}
	}

	@Override
	public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message) {
		LOG.info("Process acknowledge");
	}

}
