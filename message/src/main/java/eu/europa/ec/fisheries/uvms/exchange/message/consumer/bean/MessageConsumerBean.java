package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

//@formatter:off
@MessageDriven(mappedName = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE_NAME)
})
//@formatter:on
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @PluginConfigEvent
    Event<ExchangeMessageEvent> pluginConfigEvent;

    @Inject
    @SetMovementEvent
    Event<ExchangeMessageEvent> processMovementEvent;

    @Inject
    @SendReportToPluginEvent
    Event<ExchangeMessageEvent> sendMessageToPluginEvent;

    @Inject
    @SendCommandToPluginEvent
    Event<ExchangeMessageEvent> sendCommandToPluginEvent;

    @Inject
    @ExchangeLogEvent
    Event<ExchangeMessageEvent> updateStateEvent;

    @Inject
    @UpdatePluginSettingEvent
    Event<ExchangeMessageEvent> updatePluginSettingEvent;
    
    @Inject
    @PluginPingEvent
    Event<ExchangeMessageEvent> updatePingStateEvent;

    @Inject
    @PingEvent
    Event<ExchangeMessageEvent> pingEvent;

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

    @Inject
    @HandleProcessedMovementEvent
    Event<ExchangeMessageEvent> processedMovementEvent;

    @Inject
    @SetFluxFAReportMessageEvent
    Event<ExchangeMessageEvent> processFLUXFAReportMessageEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        LOG.info("Message received in Exchange Message MDB");

        TextMessage textMessage = (TextMessage) message;
        ExchangeBaseRequest request = tryConsumeExchangeBaseRequest(textMessage);
        if (request == null) {
            try {
                //Handle PingResponse from plugin
                JAXBMarshaller.unmarshallTextMessage(textMessage, PingResponse.class);
                updatePingStateEvent.fire(new ExchangeMessageEvent(textMessage));
            } catch (ExchangeModelMarshallException e) {
                AcknowledgeResponse type = tryConsumeAcknowledgeResponse(textMessage);
                if (type == null) {
                    LOG.error("[ Error when receiving message in exchange: ]");
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Error when receiving message in exchange")));
                } else {
                    updateStateEvent.fire(new ExchangeMessageEvent(textMessage));
                }
            }
        } else if(!checkUsernameShouldBeProvided(request)) {
            LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
            errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
        } else{

            LOG.debug("BaseRequest method {}", request.getMethod());
            switch (request.getMethod()) {
                case LIST_SERVICES:
                    pluginConfigEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_COMMAND:
                    sendCommandToPluginEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SEND_REPORT_TO_PLUGIN:
                    sendMessageToPluginEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_MOVEMENT_REPORT:
                    processMovementEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case UPDATE_PLUGIN_SETTING:
                    updatePluginSettingEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case PING:
                    pingEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case PROCESSED_MOVEMENT:
                    processedMovementEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_FLUX_FA_REPORT_MESSAGE:
                    LOG.debug("inside SET_FLUX_FA_REPORT_MESSAGE case");
                    processFLUXFAReportMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", request.getMethod());
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Method not implemented")));
            }
        }
    }

    private boolean checkUsernameShouldBeProvided(ExchangeBaseRequest request){
        boolean usernameProvided = false;
        switch (request.getMethod()){
            case SET_COMMAND:
            case SEND_REPORT_TO_PLUGIN:
            case SET_MOVEMENT_REPORT:
            case UPDATE_PLUGIN_SETTING:
            case PROCESSED_MOVEMENT:
                if(request.getUsername()!=null){
                    usernameProvided = true;
                }
                break;
            default:
                usernameProvided = true;
                break;

        }
        return usernameProvided;
    }

    private ExchangeBaseRequest tryConsumeExchangeBaseRequest(TextMessage textMessage) {
        try {
            return JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeBaseRequest.class);
        } catch (ExchangeModelMarshallException e) {
            return null;
        }
    }

    private AcknowledgeResponse tryConsumeAcknowledgeResponse(TextMessage textMessage) {
        try {
            return JAXBMarshaller.unmarshallTextMessage(textMessage, AcknowledgeResponse.class);
        } catch (ExchangeModelMarshallException e) {
            return null;
        }
    }

}
