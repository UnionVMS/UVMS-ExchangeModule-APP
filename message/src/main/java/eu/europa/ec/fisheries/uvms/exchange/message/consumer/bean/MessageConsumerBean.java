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
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE_NAME),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = ExchangeModelConstants.CONNECTION_FACTORY)
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
    @ReceiveSalesReportEvent
    Event<ExchangeMessageEvent> receiveSalesReportEvent;

    @Inject
    @ReceiveSalesQueryEvent
    Event<ExchangeMessageEvent> receiveSalesQueryEvent;

    @Inject
    @ReceiveSalesResponseEvent
    Event<ExchangeMessageEvent> receiveSalesResponseEvent;

    @Inject
    @ReceiveInvalidSalesMessageEvent
    Event<ExchangeMessageEvent> receiveInvalidSalesMessageEvent;

    @Inject
    @SendSalesReportEvent
    Event<ExchangeMessageEvent> sendSalesReportEvent;

    @Inject
    @SendSalesResponseEvent
    Event<ExchangeMessageEvent> sendSalesResponseEvent;

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
    @MdrSyncRequestMessageEvent
    Event<ExchangeMessageEvent> mdrSyncRequestMessageEvent;

    @Inject
    @MdrSyncResponseMessageEvent
    Event<ExchangeMessageEvent> mdrSyncResponseMessageEvent;

    @Inject
    @SetFluxFAReportMessageEvent
    Event<ExchangeMessageEvent> processFLUXFAReportMessageEvent;

    @Inject
    @SendFLUXFAResponseToPluginEvent
    Event<ExchangeMessageEvent> processFLUXFAResponseMessageEvent;

    @Inject
    @UpdateLogStatusEvent
    Event<ExchangeMessageEvent> updateLogStatusEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        ExchangeBaseRequest request = tryConsumeExchangeBaseRequest(textMessage);
        LOG.info("ExchangeBaseRequest received in Exchange Message MDB:{}",request);
        if (request == null) {
            try {
                //Handle PingResponse from plugin
                JAXBMarshaller.unmarshallTextMessage(textMessage, PingResponse.class);
                updatePingStateEvent.fire(new ExchangeMessageEvent(textMessage));
            } catch (ExchangeModelMarshallException e) {
                AcknowledgeResponse type = tryConsumeAcknowledgeResponse(textMessage);
                if (type == null) {
                    LOG.error("[ Error when receiving message in exchange: {}]",message);
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Error when receiving message in exchange")));
                } else {
                    updateStateEvent.fire(new ExchangeMessageEvent(textMessage));
                }
            }
        } else if (!checkUsernameShouldBeProvided(request)) {
            LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
            errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
        } else{

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
                case RECEIVE_SALES_REPORT:
                    receiveSalesReportEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case RECEIVE_SALES_QUERY:
                    receiveSalesQueryEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case RECEIVE_SALES_RESPONSE:
                    receiveSalesResponseEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case RECEIVE_INVALID_SALES_MESSAGE:
                    receiveInvalidSalesMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SEND_SALES_RESPONSE:
                    sendSalesResponseEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SEND_SALES_REPORT:
                    sendSalesReportEvent.fire(new ExchangeMessageEvent(textMessage));
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
                case SET_MDR_SYNC_MESSAGE_REQUEST:
                    mdrSyncRequestMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_MDR_SYNC_MESSAGE_RESPONSE:
                    mdrSyncResponseMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_FLUX_FA_REPORT_MESSAGE:
                    processFLUXFAReportMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case SET_FLUX_FA_RESPONSE_MESSAGE:
                    processFLUXFAResponseMessageEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                case UPDATE_LOG_STATUS:
                    updateLogStatusEvent.fire(new ExchangeMessageEvent(textMessage));
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", request.getMethod());
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Method not implemented")));
            }
        }
    }

    private boolean checkUsernameShouldBeProvided(ExchangeBaseRequest request) {
        boolean usernameProvided = false;
        switch (request.getMethod()) {
            case SET_COMMAND:
            case SEND_REPORT_TO_PLUGIN:
            case SET_MOVEMENT_REPORT:
            case UPDATE_PLUGIN_SETTING:
            case PROCESSED_MOVEMENT:
                if (request.getUsername() != null) {
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
