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

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@formatter:off
@MessageDriven(mappedName = MessageConstants.QUEUE_EXCHANGE_EVENT, activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_EXCHANGE_EVENT_NAME),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_JNDI_NAME, propertyValue = MessageConstants.QUEUE_EXCHANGE_EVENT),
        @ActivationConfigProperty(propertyName = MessageConstants.CONNECTION_FACTORY_JNDI_NAME, propertyValue = MessageConstants.CONNECTION_FACTORY),
        @ActivationConfigProperty(propertyName = "maxMessagesPerSessions", propertyValue = "100"),
        @ActivationConfigProperty(propertyName = "maximumRedeliveries", propertyValue = MessageConstants.JMS_MAX_REDELIVERIES + ""),
        @ActivationConfigProperty(propertyName = "maxSessions", propertyValue = "50")
})
//@formatter:on
public class ExchangeMessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageConsumerBean.class);

    @Inject
    @PluginConfigEvent
    private Event<ExchangeMessageEvent> pluginConfigEvent;

    @Inject
    @SetMovementEvent
    private Event<ExchangeMessageEvent> processMovementEvent;

    @Inject
    @ReceivedMovementBatchEvent
    private Event<ExchangeMessageEvent> receiveMovementBatchEvent;

    @Inject
    @ReceiveSalesReportEvent
    private Event<ExchangeMessageEvent> receiveSalesReportEvent;

    @Inject
    @ReceiveSalesQueryEvent
    private Event<ExchangeMessageEvent> receiveSalesQueryEvent;

    @Inject
    @ReceiveSalesResponseEvent
    private Event<ExchangeMessageEvent> receiveSalesResponseEvent;

    @Inject
    @ReceiveInvalidSalesMessageEvent
    private Event<ExchangeMessageEvent> receiveInvalidSalesMessageEvent;

    @Inject
    @SendSalesReportEvent
    private Event<ExchangeMessageEvent> sendSalesReportEvent;

    @Inject
    @SendSalesResponseEvent
    private Event<ExchangeMessageEvent> sendSalesResponseEvent;

    @Inject
    @SendReportToPluginEvent
    private Event<ExchangeMessageEvent> sendMessageToPluginEvent;

    @Inject
    @SendCommandToPluginEvent
    private Event<ExchangeMessageEvent> sendCommandToPluginEvent;

    @Inject
    @ExchangeLogEvent
    private Event<ExchangeMessageEvent> updateStateEvent;

    @Inject
    @UpdatePluginSettingEvent
    private Event<ExchangeMessageEvent> updatePluginSettingEvent;

    @Inject
    @PluginPingEvent
    private Event<ExchangeMessageEvent> updatePingStateEvent;

    @Inject
    @PingEvent
    private Event<ExchangeMessageEvent> pingEvent;

    @Inject
    @ErrorEvent
    private Event<ExchangeMessageEvent> errorEvent;

    @Inject
    @HandleProcessedMovementEvent
    private Event<ExchangeMessageEvent> processedMovementEvent;

    @Inject
    @ProcessedMovementBatch
    private Event<ExchangeMessageEvent> processedMovementBatch;

    @Inject
    @MdrSyncRequestMessageEvent
    private Event<ExchangeMessageEvent> mdrSyncRequestMessageEvent;

    @Inject
    @MdrSyncResponseMessageEvent
    private Event<ExchangeMessageEvent> mdrSyncResponseMessageEvent;

    @Inject
    @SetFluxFAReportMessageEvent
    private Event<ExchangeMessageEvent> processFLUXFAReportMessageEvent;

    @Inject
    @SendFaReportToPluginEvent
    private Event<ExchangeMessageEvent> sendFaReportToPluginMessageEvent;

    @Inject
    @SetFaQueryMessageEvent
    private Event<ExchangeMessageEvent> receivedFaQueryFromFlux;

    @Inject
    @SendFaQueryToPluginEvent
    private Event<ExchangeMessageEvent> sendFaQueryToPluginEvent;

    @Inject
    @SendFLUXFAResponseToPluginEvent
    private Event<ExchangeMessageEvent> processFLUXFAResponseMessageEvent;

    @Inject
    @ReceivedFluxFaResponseMessageEvent
    private Event<ExchangeMessageEvent> receivedFLUXFAResponseMessageEvent;

    @Inject
    @UpdateLogStatusEvent
    private Event<ExchangeMessageEvent> updateLogStatusEvent;

    @Inject
    @UpdateLogBusinessErrorEvent
    private Event<ExchangeMessageEvent> updateLogBusinessErrorEvent;

    @Inject
    @LogRefIdByTypeExists
    private Event<ExchangeMessageEvent> logRefIdByTyeExists;

    @Inject
    @LogIdByTypeExists
    private Event<ExchangeMessageEvent> logIdByTyeExists;

    @Inject
    @ReceiveAssetInformationEvent
    private Event<ExchangeMessageEvent> assetInformationEvent;


    @Override
    public void onMessage(Message message) {
        
        TextMessage textMessage = (TextMessage) message;
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        ExchangeBaseRequest request = tryConsumeExchangeBaseRequest(textMessage);
        LOG.debug("Message received in Exchange Message MDB. Times redelivered: " + getTimesRedelivered(message));
        LOG.debug("Request body : ", request);
        final ExchangeMessageEvent messageEventWrapper = new ExchangeMessageEvent(textMessage);
        if (request == null) {
            LOG.warn("[ERROR] ExchangeBaseRequest is null!! Check the message sent...");
            try {
                //Handle PingResponse from plugin
                JAXBMarshaller.unmarshallTextMessage(textMessage, PingResponse.class);
                updatePingStateEvent.fire(messageEventWrapper);
            } catch (ExchangeModelMarshallException e) {
                AcknowledgeResponse type = tryConsumeAcknowledgeResponse(textMessage);
                if (type == null) {
                    LOG.error("[ Error when receiving message in exchange: {}]", message);
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Error when receiving message in exchange")));
                } else {
                    updateStateEvent.fire(messageEventWrapper);
                }
            }
        } else if (!checkUsernameShouldBeProvided(request)) {
            LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
            errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
        } else {
            ExchangeModuleMethod exchangeMethod = request.getMethod();
            LOG.info("[INFO] Going to process following message type [ {} ] : ", exchangeMethod);
            switch (exchangeMethod) {
                case LIST_SERVICES:
                    pluginConfigEvent.fire(messageEventWrapper);
                    break;
                case SET_COMMAND:
                    sendCommandToPluginEvent.fire(messageEventWrapper);
                    break;
                case SEND_REPORT_TO_PLUGIN:
                    sendMessageToPluginEvent.fire(messageEventWrapper);
                    break;
                case SET_MOVEMENT_REPORT: // @Deprecated TODO To be removed when ready..
                    processMovementEvent.fire(messageEventWrapper);
                    break;
                case RECEIVE_MOVEMENT_REPORT_BATCH:
                    receiveMovementBatchEvent.fire(messageEventWrapper);
                    break;
                case RECEIVE_SALES_REPORT:
                    receiveSalesReportEvent.fire(messageEventWrapper);
                    break;
                case RECEIVE_SALES_QUERY:
                    receiveSalesQueryEvent.fire(messageEventWrapper);
                    break;
                case RECEIVE_SALES_RESPONSE:
                    receiveSalesResponseEvent.fire(messageEventWrapper);
                    break;
                case RECEIVE_INVALID_SALES_MESSAGE:
                    receiveInvalidSalesMessageEvent.fire(messageEventWrapper);
                    break;
                case SEND_SALES_RESPONSE:
                    sendSalesResponseEvent.fire(messageEventWrapper);
                    break;
                case SEND_SALES_REPORT:
                    sendSalesReportEvent.fire(messageEventWrapper);
                    break;
                case UPDATE_PLUGIN_SETTING:
                    updatePluginSettingEvent.fire(messageEventWrapper);
                    break;
                case PING:
                    pingEvent.fire(messageEventWrapper);
                    break;
                case PROCESSED_MOVEMENT:
                    processedMovementEvent.fire(messageEventWrapper);
                    break;
                case PROCESSED_MOVEMENT_BATCH:
                    processedMovementBatch.fire(messageEventWrapper);
                    break;
                case SET_MDR_SYNC_MESSAGE_REQUEST:
                    mdrSyncRequestMessageEvent.fire(messageEventWrapper);
                    break;
                case SET_MDR_SYNC_MESSAGE_RESPONSE:
                    mdrSyncResponseMessageEvent.fire(messageEventWrapper);
                    break;
                case SET_FLUX_FA_REPORT_MESSAGE:
                case UNKNOWN :
                    processFLUXFAReportMessageEvent.fire(messageEventWrapper);
                    break;
                case SEND_FLUX_FA_REPORT_MESSAGE:
                    sendFaReportToPluginMessageEvent.fire(messageEventWrapper);
                    break;
                case SET_FA_QUERY_MESSAGE:
                    receivedFaQueryFromFlux.fire(messageEventWrapper);
                    break;
                case SEND_FA_QUERY_MESSAGE:
                    sendFaQueryToPluginEvent.fire(messageEventWrapper);
                    break;
                case SET_FLUX_FA_RESPONSE_MESSAGE:
                    processFLUXFAResponseMessageEvent.fire(messageEventWrapper);
                    break;
                case RCV_FLUX_FA_RESPONSE_MESSAGE:
                    receivedFLUXFAResponseMessageEvent.fire(messageEventWrapper);
                    break;
                case UPDATE_LOG_STATUS:
                    updateLogStatusEvent.fire(messageEventWrapper);
                    break;
                case UPDATE_LOG_BUSINESS_ERROR:
                    updateLogBusinessErrorEvent.fire(messageEventWrapper);
                    break;
                case LOG_REF_ID_BY_TYPE_EXISTS:
                    logRefIdByTyeExists.fire(messageEventWrapper);
                    break;
                case LOG_ID_BY_TYPE_EXISTS:
                    logIdByTyeExists.fire(messageEventWrapper);
                    break;
                case RECEIVE_ASSET_INFORMATION:
                    assetInformationEvent.fire(messageEventWrapper);
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", exchangeMethod);
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

    private int getTimesRedelivered(Message message) {
        try {
            return (message.getIntProperty("JMSXDeliveryCount") - 1);

        } catch (Exception e) {
            return 0;
        }
    }
}
