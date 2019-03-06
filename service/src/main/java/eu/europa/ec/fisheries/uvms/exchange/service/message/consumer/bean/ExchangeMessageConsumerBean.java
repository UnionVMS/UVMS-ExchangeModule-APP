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
package eu.europa.ec.fisheries.uvms.exchange.service.message.consumer.bean;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventIncomingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
    private ExchangeEventIncomingService incomingServiceBean;

    @Inject
    private ExchangeEventOutgoingService outgoingServiceBean;

    @Inject
    private PluginService pluginServiceBean;

    @Inject
    @ErrorEvent
    private Event<ExchangeMessageEvent> errorEvent;


    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        ExchangeBaseRequest request = tryConsumeExchangeBaseRequest(textMessage);
        LOG.debug("Message received in Exchange Message MDB. Times redelivered: " + getTimesRedelivered(message));
        LOG.trace("Request body : ", request);
        final ExchangeMessageEvent messageEventWrapper = new ExchangeMessageEvent(textMessage);
        if (request == null) {
            LOG.warn("[ERROR] ExchangeBaseRequest is null!! Check the message sent...");
            try {
                //Handle PingResponse from plugin
                JAXBMarshaller.unmarshallTextMessage(textMessage, PingResponse.class);
                incomingServiceBean.processPluginPing(messageEventWrapper);
            } catch (ExchangeModelMarshallException e) {
                AcknowledgeResponse type = tryConsumeAcknowledgeResponse(textMessage);
                if (type == null) {
                    LOG.error("[ Error when receiving message in exchange: {}]", message);
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Error when receiving message in exchange")));
                } else {
                    incomingServiceBean.processAcknowledge(messageEventWrapper);
                }
            }
        } else if (!checkUsernameShouldBeProvided(request)) {
            LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
            errorEvent.fire(new ExchangeMessageEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
        } else {
            ExchangeModuleMethod exchangeMethod = request.getMethod();
            LOG.info("[INFO] Going to process following message type [ {} ] : ", exchangeMethod);
            switch (exchangeMethod) {

                    /* PLUGIN */

                case LIST_SERVICES:
                    incomingServiceBean.getPluginListByTypes(messageEventWrapper);
                    break;
                case SET_COMMAND:
                    outgoingServiceBean.sendCommandToPlugin(messageEventWrapper);
                    break;
                case SEND_REPORT_TO_PLUGIN:
                    outgoingServiceBean.sendReportToPlugin(messageEventWrapper);
                    break;
                case UPDATE_PLUGIN_SETTING:
                    pluginServiceBean.updatePluginSetting(messageEventWrapper);
                    break;

                    /* MOVEMENTS */

                case SET_MOVEMENT_REPORT:
                    incomingServiceBean.processMovement(messageEventWrapper);
                    break;
                case RECEIVE_MOVEMENT_REPORT_BATCH:
                    incomingServiceBean.processReceivedMovementBatch(messageEventWrapper);
                    break;
                case PROCESSED_MOVEMENT:
                    outgoingServiceBean.handleProcessedMovement(messageEventWrapper);
                    break;
                case PROCESSED_MOVEMENT_BATCH:
                    outgoingServiceBean.handleProcessedMovementBatch(messageEventWrapper);
                    break;

                    /* SALES */

                case RECEIVE_SALES_REPORT:
                    incomingServiceBean.receiveSalesReport(messageEventWrapper);
                    break;
                case RECEIVE_SALES_QUERY:
                    incomingServiceBean.receiveSalesQuery(messageEventWrapper);
                    break;
                case RECEIVE_SALES_RESPONSE:
                    incomingServiceBean.receiveSalesResponse(messageEventWrapper);
                    break;
                case RECEIVE_INVALID_SALES_MESSAGE:
                    incomingServiceBean.receiveInvalidSalesMessage(messageEventWrapper);
                    break;
                case SEND_SALES_RESPONSE:
                    outgoingServiceBean.sendSalesResponse(messageEventWrapper);
                    break;
                case SEND_SALES_REPORT:
                    outgoingServiceBean.sendSalesReport(messageEventWrapper);
                    break;
                case PING:
                    incomingServiceBean.ping(messageEventWrapper);
                    break;

                    /* MDR */

                case SET_MDR_SYNC_MESSAGE_REQUEST:
                    outgoingServiceBean.forwardMdrSyncMessageToPlugin(messageEventWrapper);
                    break;
                case SET_MDR_SYNC_MESSAGE_RESPONSE:
                    incomingServiceBean.sendResponseToRulesModule(messageEventWrapper);
                    break;

                    /* FLUX */

                case SET_FLUX_FA_REPORT_MESSAGE:
                case UNKNOWN:
                    incomingServiceBean.processFLUXFAReportMessage(messageEventWrapper);
                    break;
                case SEND_FLUX_FA_REPORT_MESSAGE:
                    outgoingServiceBean.sendFLUXFAReportToPlugin(messageEventWrapper);
                    break;
                case SET_FA_QUERY_MESSAGE:
                    incomingServiceBean.processFAQueryMessage(messageEventWrapper);
                    break;
                case SEND_FA_QUERY_MESSAGE:
                    outgoingServiceBean.sendFLUXFAQueryToPlugin(messageEventWrapper);
                    break;
                case SET_FLUX_FA_RESPONSE_MESSAGE:
                    outgoingServiceBean.sendFLUXFAResponseToPlugin(messageEventWrapper);
                    break;
                case RCV_FLUX_FA_RESPONSE_MESSAGE:
                    incomingServiceBean.processFluxFAResponseMessage(messageEventWrapper);
                    break;

                    /* LOG */

                case UPDATE_LOG_STATUS:
                    outgoingServiceBean.updateLogStatus(messageEventWrapper);
                    break;
                case UPDATE_LOG_BUSINESS_ERROR:
                    outgoingServiceBean.updateLogBusinessError(messageEventWrapper);
                    break;
                case LOG_REF_ID_BY_TYPE_EXISTS:
                    incomingServiceBean.logRefIdByTypeExists(messageEventWrapper);
                    break;
                case LOG_ID_BY_TYPE_EXISTS:
                    incomingServiceBean.logIdByTypeExists(messageEventWrapper);
                    break;

                    /* ASSET */

                case RECEIVE_ASSET_INFORMATION:
                        incomingServiceBean.receiveAssetInformation(messageEventWrapper);
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
