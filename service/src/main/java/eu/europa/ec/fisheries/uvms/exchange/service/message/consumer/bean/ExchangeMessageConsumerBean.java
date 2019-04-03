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
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventIncomingServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventOutgoingServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.PluginServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
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
    private ExchangeEventIncomingServiceBean incomingServiceBean;

    @Inject
    private ExchangeEventOutgoingServiceBean outgoingServiceBean;

    @Inject
    private PluginServiceBean pluginServiceBean;

    @Inject
    @ErrorEvent
    private Event<ExchangeErrorEvent> errorEvent;


    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        LOG.debug("Message received in Exchange Message MDB. Times redelivered: " + getTimesRedelivered(message));


        ExchangeModuleMethod exchangeMethod = null;
        try {
            LOG.trace("Request body : ", textMessage.getText());
            String function = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            exchangeMethod = (function != null) ? ExchangeModuleMethod.valueOf(function) : tryConsumeExchangeBaseRequest(textMessage).getMethod();
            LOG.info("[INFO] Going to process following message type [ {} ] : ", exchangeMethod);
            switch (exchangeMethod) {

                    /* PLUGIN */

                case LIST_SERVICES:
                    incomingServiceBean.getPluginListByTypes(textMessage);
                    break;
                case SET_COMMAND:
                    outgoingServiceBean.sendCommandToPlugin(textMessage);
                    break;
                case SEND_REPORT_TO_PLUGIN:
                    outgoingServiceBean.sendReportToPlugin(textMessage);
                    break;
                case UPDATE_PLUGIN_SETTING:
                    pluginServiceBean.updatePluginSetting(textMessage);
                    break;
                case PLUGIN_SET_CONFIG_ACK:
                case PLUGIN_SET_COMMAND_ACK:
                case PLUGIN_SET_REPORT_ACK:
                case PLUGIN_START_ACK:
                case PLUGIN_STOP_ACK:
                case PLUGIN_PING_ACK:
                case PLUGIN_SET_MDR_REQUEST_ACK:
                case PLUGIN_SEND_FA_REPORT_ACK:
                case PLUGIN_SEND_FA_QUERY_ACK:
                case PLUGIN_SET_FLUX_RESPONSE_ACK:
                case PLUGIN_SEND_SALES_REPORT_ACK:
                case PLUGIN_SEND_SALES_RESPONSE_ACK:
                case PLUGIN_SEND_VESSEL_INFORMATION_ACK:
                case PLUGIN_SEND_VESSEL_QUERY_ACK:
                    incomingServiceBean.processAcknowledge(textMessage);
                    break;
                case PLUGIN_PING_RESPONSE:
                    incomingServiceBean.processPluginPing(textMessage);
                    break;

                    /* MOVEMENTS */

                case SET_MOVEMENT_REPORT:
                    incomingServiceBean.processMovement(textMessage);
                    break;
                case RECEIVE_MOVEMENT_REPORT_BATCH:
                    incomingServiceBean.processReceivedMovementBatch(textMessage);
                    break;
                case PROCESSED_MOVEMENT:
                    outgoingServiceBean.handleProcessedMovement(textMessage);
                    break;
                case PROCESSED_MOVEMENT_BATCH:
                    outgoingServiceBean.handleProcessedMovementBatch(textMessage);
                    break;

                    /* SALES */

                case RECEIVE_SALES_REPORT:
                    incomingServiceBean.receiveSalesReport(textMessage);
                    break;
                case RECEIVE_SALES_QUERY:
                    incomingServiceBean.receiveSalesQuery(textMessage);
                    break;
                case RECEIVE_SALES_RESPONSE:
                    incomingServiceBean.receiveSalesResponse(textMessage);
                    break;
                case RECEIVE_INVALID_SALES_MESSAGE:
                    incomingServiceBean.receiveInvalidSalesMessage(textMessage);
                    break;
                case SEND_SALES_RESPONSE:
                    outgoingServiceBean.sendSalesResponse(textMessage);
                    break;
                case SEND_SALES_REPORT:
                    outgoingServiceBean.sendSalesReport(textMessage);
                    break;
                case PING:
                    incomingServiceBean.ping(textMessage);
                    break;

                    /* MDR */

                case SET_MDR_SYNC_MESSAGE_REQUEST:
                    outgoingServiceBean.forwardMdrSyncMessageToPlugin(textMessage);
                    break;
                case SET_MDR_SYNC_MESSAGE_RESPONSE:
                    incomingServiceBean.sendResponseToRulesModule(textMessage);
                    break;

                    /* FLUX */

                case SET_FLUX_FA_REPORT_MESSAGE:
                case UNKNOWN:
                    incomingServiceBean.processFLUXFAReportMessage(textMessage);
                    break;
                case SEND_FLUX_FA_REPORT_MESSAGE:
                    outgoingServiceBean.sendFLUXFAReportToPlugin(textMessage);
                    break;
                case SET_FA_QUERY_MESSAGE:
                    incomingServiceBean.processFAQueryMessage(textMessage);
                    break;
                case SEND_FA_QUERY_MESSAGE:
                    outgoingServiceBean.sendFLUXFAQueryToPlugin(textMessage);
                    break;
                case SET_FLUX_FA_RESPONSE_MESSAGE:
                    outgoingServiceBean.sendFLUXFAResponseToPlugin(textMessage);
                    break;
                case RCV_FLUX_FA_RESPONSE_MESSAGE:
                    incomingServiceBean.processFluxFAResponseMessage(textMessage);
                    break;

                    /* LOG */

                case UPDATE_LOG_STATUS:
                    outgoingServiceBean.updateLogStatus(textMessage);
                    break;
                case UPDATE_LOG_BUSINESS_ERROR:
                    outgoingServiceBean.updateLogBusinessError(textMessage);
                    break;
                case LOG_REF_ID_BY_TYPE_EXISTS:
                    incomingServiceBean.logRefIdByTypeExists(textMessage);
                    break;
                case LOG_ID_BY_TYPE_EXISTS:
                    incomingServiceBean.logIdByTypeExists(textMessage);
                    break;

                    /* ASSET */

                case RECEIVE_ASSET_INFORMATION:
                        incomingServiceBean.receiveAssetInformation(textMessage);
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", exchangeMethod);
                    errorEvent.fire(new ExchangeErrorEvent(textMessage, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Method not implemented")));
            }
        } catch (Exception e){
            LOG.error("Error in exchange call to {} : ", exchangeMethod, e);
            throw new RuntimeException("Error in exchange call to " + exchangeMethod + " : ", e);
        }
    }


    private ExchangeBaseRequest tryConsumeExchangeBaseRequest(TextMessage textMessage) {
        try {
            return JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeBaseRequest.class);
        } catch (RuntimeException e) {
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
