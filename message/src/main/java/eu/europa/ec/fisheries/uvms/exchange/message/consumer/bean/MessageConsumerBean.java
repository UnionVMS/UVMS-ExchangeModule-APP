package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginPingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

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
    @PluginPingEvent
    Event<ExchangeMessageEvent> updatePingStateEvent;

    @Inject
    @PingEvent
    Event<ExchangeMessageEvent> pingEvent;

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

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
        } else {
            LOG.debug("BaseRequest method " + request.getMethod());
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
                case PING:
                    pingEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", request.getMethod());
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Method not implemented")));
            }
        }
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
