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
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventIncomingServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventOutgoingServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.PluginServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

//@formatter:off
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_LOOKUP_STR, propertyValue = MessageConstants.QUEUE_EXCHANGE_EVENT),
})
//@formatter:on
public class ExchangeMessageConsumerBean implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeMessageConsumerBean.class);

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
            LOG.trace("Request body : {}", textMessage.getText());
            String function = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            exchangeMethod = (function != null) ? ExchangeModuleMethod.valueOf(function) : tryConsumeExchangeBaseRequest(textMessage).getMethod();
            LOG.debug("Going to process following message type [ {} ] : ", exchangeMethod);
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

                /* ELECTRONIC FISHING REPORTING, EFR */

                case EFR_SAVE_ACTIVITY:
                    incomingServiceBean.processEfrSaveActivity(textMessage);
                    break;
                case EFR_ACTIVITY_SAVED:
                    outgoingServiceBean.sendEfrActivitySavedToPlugin(textMessage);
                    break;

                default:
                    LOG.error("[ Not implemented method consumed: {} ] ", exchangeMethod);
                    errorEvent.fire(new ExchangeErrorEvent(textMessage, "Method not implemented"));
            }
        } catch (Exception e) {
            try {
                LOG.error("Error in exchange call to {} : Incoming message is {} . END", exchangeMethod, textMessage.getText(), e);
                throw new RuntimeException("Error in exchange call to " + exchangeMethod + " : Incoming message is " + textMessage.getText() + " .END", e);
            } catch (JMSException jmse) {
                LOG.error("Can not get text from text message in exchange message consumer bean", jmse);
            }
        }
    }

    private ExchangeBaseRequest tryConsumeExchangeBaseRequest(TextMessage textMessage) {
        try {
            if (textMessage.getText().startsWith("<ns2:AcknowledgeResponse xmlns:ns2=\"urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1\">")) {
                LOG.debug("Received deprecated plugin Ack response with message " + textMessage.getText());
                ExchangeBaseRequest plugin = new SetCommandRequest();
                // They all go to the same place so this does not matter, also this is really ugly ;(
                plugin.setMethod(ExchangeModuleMethod.PLUGIN_SET_COMMAND_ACK);
                return plugin;
            }
            ExchangeBaseRequest retVal = JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeBaseRequest.class);
            LOG.debug("Using deprecated way to get incoming method call in message from: " + retVal.getUsername());
            return retVal;
        } catch (Exception e) {
            LOG.error("Error when consuming ExchangeBaseRequest", e);
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
