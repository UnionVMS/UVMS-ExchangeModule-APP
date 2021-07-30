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
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PluginBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.bean.ExchangeAuditProducerBean;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.bean.ExchangeEventBusTopicProducerBean;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.event.PollEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeToMdrRulesMapper;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType.BELGIAN_ACTIVITY;

@Stateless
@Slf4j
public class ExchangeEventOutgoingServiceBean implements ExchangeEventOutgoingService {

    @Inject
    @ErrorEvent
    private Event<ExchangeMessageEvent> exchangeErrorEvent;

    @Inject
    @PluginErrorEvent
    private Event<PluginMessageEvent> pluginErrorEvent;

    @Inject
    @PollEvent
    private Event<NotificationMessage> pollEvent;

    @EJB
    private ExchangeAuditProducerBean auditProducer;

    @EJB
    private ExchangeEventBusTopicProducerBean eventBusProducer;

    @EJB
    private ExchangeConsumer exchangeConsumer;

    @EJB
    private ExchangeLogService exchangeLogService;

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private ExchangeAssetService exchangeAssetService;

    @EJB
    private ExchangeEventOutgoingService exchangeEventOutgoingService;

    @Override
    public void sendSalesResponseToPlugin(SendSalesResponseRequest sendSalesResponseRequest, PluginType pluginType) throws ExchangeModelMarshallException, ExchangeMessageException {
        if (pluginType == null) {
            throw new IllegalArgumentException("No plugin provided to send the Sales response to.");
        }
        String marshalledRequest = JAXBMarshaller.marshallJaxBObjectToString(sendSalesResponseRequest);
        final String serviceName = pluginType == PluginType.BELGIAN_SALES ? ExchangeServiceConstants.BELGIAN_AUCTION_SALES_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_SALES_PLUGIN_SERVICE_NAME;
        sendEventBusMessage(marshalledRequest, serviceName);
    }

    @Override
    public void sendSalesReportToFLUX(SendSalesReportRequest sendSalesReportRequest) throws ExchangeModelMarshallException, ExchangeMessageException {
        String marshalledRequest = JAXBMarshaller.marshallJaxBObjectToString(sendSalesReportRequest);
        sendEventBusMessage(marshalledRequest, ExchangeServiceConstants.FLUX_SALES_PLUGIN_SERVICE_NAME);
    }

    @Override
    public void sendAssetInformationToFLUX(PluginBaseRequest request) throws ExchangeModelMarshallException, ExchangeMessageException {
        String marshalledRequest = JAXBMarshaller.marshallJaxBObjectToString(request);
        sendEventBusMessage(marshalledRequest, ExchangeServiceConstants.FLUX_VESSEL_PLUGIN_SERVICE_NAME);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message) {
        try {
            SendMovementToPluginRequest request = (SendMovementToPluginRequest) message.getExchangeBaseRequest();
            log.info("Send report to plugin: {}",request);
            SendMovementToPluginType sendReport = request.getReport();

            List<PluginType> type = new ArrayList<>();
            type.add(sendReport.getPluginType());

            List<ServiceResponseType> services = exchangeService.getServiceList(type);
            if (services == null || services.isEmpty()) {
                String faultMessage = "No plugins of type " + sendReport.getPluginType() + " found";
                log.debug(faultMessage);

            } else {
                ServiceResponseType service = services.get(0);

                if (validate(service, sendReport, message.getJmsMessage(), request.getUsername())) {
                    List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                    String unsentMessageGuid = exchangeLogService.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), message.getJmsMessage().getText(), unsentMessageProperties, request.getUsername());
                    String text = ExchangePluginRequestMapper.createSetReportRequest(sendReport.getTimestamp(), sendReport, unsentMessageGuid);
                    String pluginMessageId = sendEventBusMessage(text, service.getServiceClassName());
                    try {
                        ExchangeLogType log = ExchangeLogMapper.getSendMovementExchangeLog(sendReport);
                        exchangeLogService.logAndCache(log, pluginMessageId, request.getUsername());
                    } catch (ExchangeLogException e) {
                        log.error(e.getMessage(),e);
                    }
                    AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeOK();
                    String moduleResponse = ExchangeModuleResponseMapper.mapSendMovementToPluginResponse(ackType);
                    auditProducer.sendResponseMessageToSender(message.getJmsMessage(), moduleResponse);
                } else {
                    log.debug("Cannot send to plugin. Response sent to caller:{}",message);
                }
            }
        } catch (ExchangeException  | MessageException e) {
            log.error("Error when sending report to plugin " + message,e);

        } catch (JMSException ex) {
            log.error("Error when creating unsent movement " + message,ex);
        }
    }


    /*
	 * Method for Observing the @MdrSyncRequestMessageEvent, meaning a message from Activity MDR
	 * module has arrived (synchronisation of a MDR Entity) which needs to be sent to EventBus Topic
	 * so that it gets intercepted by MDR Plugin Registered Subscriber and sent to Flux.
	 *
	 */
    @Override
    public void forwardMdrSyncMessageToPlugin(@Observes @MdrSyncRequestMessageEvent ExchangeMessageEvent message) {
        TextMessage requestMessage = message.getJmsMessage();
        try {
            log.info("[INFO] Received MdrSyncMessageEvent. Going to send to the Plugin now..");
            String marshalledReq = ExchangeToMdrRulesMapper.mapExchangeToMdrPluginRequest(requestMessage);
            sendEventBusMessage(marshalledReq, ExchangeServiceConstants.MDR_PLUGIN_SERVICE_NAME);
        } catch (Exception e) {
            log.error("[ERROR] Something strange happend during message conversion " + message,e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private boolean validate(ServiceResponseType service, SendMovementToPluginType sendReport, TextMessage origin, String username) {
        String serviceName = service.getServiceClassName(); //Use first and only
        if (serviceName == null || serviceName.isEmpty()) {
            String faultMessage = "First plugin of type " + sendReport.getPluginType() + " is invalid. Missing serviceClassName";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
            try {
                List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                exchangeLogService.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), origin.getText(), unsentMessageProperties, username);
            } catch (ExchangeLogException | JMSException e) {
                log.error("Couldn't create unsent message " + e.getMessage(),e);
            }
            return false;
        } else if (!sendReport.getPluginType().equals(service.getPluginType())) {
            String faultMessage = "First plugin of type " + sendReport.getPluginType() + " does not match plugin type of " + serviceName + ". Current type is " + service.getPluginType();
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
            return false;
        } else if (sendReport.getPluginName() != null && !serviceName.equalsIgnoreCase(sendReport.getPluginName())) {
            String faultMessage = "First plugin of type " + sendReport.getPluginType() + " does not matching input of " + sendReport.getPluginName();
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
            return false;
        } else if (!StatusType.STARTED.equals(service.getStatus())) {
            try {
                List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                exchangeLogService.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), origin.getText(), unsentMessageProperties, username);
            } catch (ExchangeLogException | JMSException e) {
                log.error("Couldn't create unsent message " + e.getMessage(),e);
            }

            try {
                AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeNOK(origin.getJMSMessageID(), "Plugin to send movement is not started");
                String moduleResponse = ExchangeModuleResponseMapper.mapSendMovementToPluginResponse(ackType);
                auditProducer.sendResponseMessageToSender(origin, moduleResponse);
            } catch (JMSException | ExchangeModelMarshallException | MessageException e) {
                log.error("Plugin not started, couldn't send module response: " + e.getMessage(),e);
            }
            return false;
        }
        return true;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message) {
        SetCommandRequest request = new SetCommandRequest();
        try {
            request = (SetCommandRequest) message.getExchangeBaseRequest();
            log.info("Send command to plugin:{}",request);
            String pluginName = request.getCommand().getPluginName();
            CommandType commandType = request.getCommand();
            ServiceResponseType service = exchangeService.getService(pluginName);

            if (validate(request.getCommand(), message.getJmsMessage(), service, commandType, request.getUsername())) {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                String unsentMessageGuid = exchangeLogService.createUnsentMessage(service.getName(), request.getCommand().getTimestamp(), request.getCommand().getCommand().name(), message.getJmsMessage().getText(), setUnsentMessageTypePropertiesForPoll, request.getUsername());

                request.getCommand().setUnsentMessageGuid(unsentMessageGuid);
                String text = ExchangePluginRequestMapper.createSetCommandRequest(request.getCommand());
                String pluginMessageId = sendEventBusMessage(text, pluginName);

                try {
                    ExchangeLogType log = ExchangeLogMapper.getSendCommandExchangeLog(request.getCommand());
                    exchangeLogService.logAndCache(log, pluginMessageId, request.getUsername());
                } catch (ExchangeLogException e) {
                    log.error(e.getMessage(),e);
                }
                CommandTypeType x = request.getCommand().getCommand();

                //response back to MobileTerminal (not to rules when email)
                if (request.getCommand().getCommand() != CommandTypeType.EMAIL) {
                    AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeOK();
                    String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
                    auditProducer.sendResponseMessageToSender(message.getJmsMessage(), moduleResponse);
                }
            } else {
                log.debug("Can not send to plugin. Response sent to caller.");
            }

        } catch (NullPointerException | ExchangeException | MessageException e) {
            if (request.getCommand().getCommand() != CommandTypeType.EMAIL) {
                log.error("Error when sending command to plugin " + e.getMessage(),e);
                exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Exception when sending command to plugin")));
            }
        } catch (JMSException ex) {
            log.error("Error when creating unsent message " + ex.getMessage(),ex);
        }
    }


    @Override
    public void sendFLUXFAResponseToPlugin(@Observes @SendFLUXFAResponseToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFLUXFAResponseMessageRequest request = (SetFLUXFAResponseMessageRequest) message.getExchangeBaseRequest();
            log.debug("[INFO] Got FLUXFAResponse in exchange with destination :" + request.getDestination());
            String text = ExchangePluginRequestMapper.createSetFLUXFAResponseRequestWithOn(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getOnValue(),request.getResponseMessageGuid());
            request.setOnValue(null); //a value should be assigned later from bridge for response messages
            final ExchangeLogType logType = exchangeLogService.log(request, LogType.SEND_FLUX_RESPONSE_MSG, request.getStatus(), TypeRefType.FA_RESPONSE, request.getRequest(), false);
            if(!logType.getStatus().equals(ExchangeLogStatusTypeType.FAILED) && !logType.getStatus().equals(ExchangeLogStatusTypeType.BLOCKED)){ // Send response only if it is NOT FAILED
                log.debug("[START] Sending FLUXFAResponse to Flux Activity Plugin..");
                String pluginMessageId = sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                        ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
                log.debug("[END] FLUXFAResponse sent to Flux Activity Plugin {}" + pluginMessageId);
            } else {
                log.info("[WARN] FLUXFAResponse is FAILED so won't be sent to Flux Activity Plugin..");
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            log.error("Unable to send FLUXFAResponse to plugin.", e);
        }
    }

    @Override
    public void sendFLUXFAQueryToPlugin(@Observes @SendFaQueryToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFAQueryMessageRequest request = (SetFAQueryMessageRequest) message.getExchangeBaseRequest();
            log.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text;
            if(request.getResponseMessageGuid() == null){
               text = ExchangePluginRequestMapper.createSendFLUXFAQueryRequest(
                        request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(),request.getMessageGuid());
            } else {
                text = ExchangePluginRequestMapper.createSendFLUXFAQueryRequest(
                        request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getResponseMessageGuid());
            }
            log.debug("Message to plugin {}", text);
            String pluginMessageId = sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            log.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            request.setOnValue(null); //a value should be assigned later from bridge for response messages
            exchangeLogService.newLog(request, LogType.SEND_FA_QUERY_MSG, request.getValidation(), TypeRefType.FA_QUERY, request.getRequest(), false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            log.error("Unable to send FLUXFAQuery to plugin.", e);
        }
    }

    @Override
    public void sendFLUXFAReportToPlugin(@Observes @SendFaReportToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFLUXFAReportMessageRequest request = (SetFLUXFAReportMessageRequest) message.getExchangeBaseRequest();
            log.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text;
            if(request.getResponseMessageGuid() == null){
                text = ExchangePluginRequestMapper.createSendFLUXFAReportRequest(
                        request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(),request.getMessageGuid());
            } else {
                text = ExchangePluginRequestMapper.createSendFLUXFAReportRequest(
                        request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(),request.getResponseMessageGuid());
            }
            log.debug("Message to plugin {}", text);
            String pluginMessageId = sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            log.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            request.setOnValue(null); //a value should be assigned later from bridge for response messages
            exchangeLogService.newLog(request, LogType.SEND_FLUX_FA_REPORT_MSG, request.getValidation(), TypeRefType.FA_REPORT, request.getRequest(), false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            log.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }

    @Override
    public void sendSalesResponse(@Observes @SendSalesResponseEvent ExchangeMessageEvent message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesResponseRequest request = (eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesResponseRequest) message.getExchangeBaseRequest();
            ExchangeLogStatusTypeType validationStatus = request.getValidationStatus();
            exchangeLogService.log(request, LogType.SEND_SALES_RESPONSE, validationStatus, TypeRefType.SALES_RESPONSE, request.getResponse(), false);
            if (validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL || validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS) {
                eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest pluginRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest();
                pluginRequest.setRecipient(request.getSenderOrReceiver());
                pluginRequest.setResponse(request.getResponse());
                pluginRequest.setMethod(ExchangePluginMethod.SEND_SALES_RESPONSE);
                exchangeEventOutgoingService.sendSalesResponseToPlugin(pluginRequest, request.getPluginType());
            } else {
                log.error("Received invalid response from the Sales module: " + request.getResponse());
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales response.", e);
        }
    }

    @Override
    public void sendSalesReport(@Observes @SendSalesReportEvent ExchangeMessageEvent message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest request = (eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest) message.getExchangeBaseRequest();
            ExchangeLogStatusTypeType validationStatus = request.getValidationStatus();
            exchangeLogService.log(request, LogType.SEND_SALES_REPORT, validationStatus, TypeRefType.SALES_REPORT, request.getReport(), false);
            if (validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL || validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS) {
                eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest pluginRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest();
                pluginRequest.setRecipient(request.getSenderOrReceiver());
                pluginRequest.setReport(request.getReport());
                if (request.getSenderOrReceiver() != null) {
                    pluginRequest.setSenderOrReceiver(request.getSenderOrReceiver());
                }
                pluginRequest.setMethod(ExchangePluginMethod.SEND_SALES_REPORT);
                exchangeEventOutgoingService.sendSalesReportToFLUX(pluginRequest);
            } else {
                log.error("Received invalid report from the Sales module: " + request.getReport());
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales report.", e);
        }
    }

    @Override
    public void sendAssetInformation(@Observes @SendAssetInformationEvent ExchangeMessageEvent event) {
        try {
            SendAssetInformationRequest incomingRequest = (SendAssetInformationRequest) event.getExchangeBaseRequest();
            String message = incomingRequest.getAssets();
            String destination = incomingRequest.getDestination();
            String senderOrReceiver = incomingRequest.getSenderOrReceiver();

            eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendAssetInformationRequest outgoingRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendAssetInformationRequest();
            outgoingRequest.setRequest(message);
            outgoingRequest.setDestination(destination);
            outgoingRequest.setSenderOrReceiver(senderOrReceiver);
            outgoingRequest.setMethod(ExchangePluginMethod.SEND_VESSEL_INFORMATION);

            exchangeEventOutgoingService.sendAssetInformationToFLUX(outgoingRequest);
            exchangeLogService.log(incomingRequest, LogType.SEND_ASSET_INFORMATION, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.ASSETS, message, false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(event, "Error when sending asset information to FLUX", e);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the outgoing asset information.", e);
        }
    }

    @Override
    public void updateLogStatus(@Observes @UpdateLogStatusEvent ExchangeMessageEvent message) {
        try {
            UpdateLogStatusRequest request = (UpdateLogStatusRequest) message.getExchangeBaseRequest();
            String logGuid = request.getLogGuid();
            ExchangeLogStatusTypeType status = request.getNewStatus();
            exchangeLogService.updateStatus(logGuid, status);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not update the status of a message log.", e);
        }
    }

    @Override
    public void updateLogBusinessError(@Observes @UpdateLogBusinessErrorEvent ExchangeMessageEvent message) {
        try {
            UpdateLogStatusRequest request = (UpdateLogStatusRequest)message.getExchangeBaseRequest();
            String exchangeLogGuid = request.getLogGuid();
            String businessModuleExceptionMessage = request.getBusinessModuleExceptionMessage();
            exchangeLogService.updateExchangeLogBusinessError(exchangeLogGuid, businessModuleExceptionMessage);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not unmarshall the incoming UpdateLogStatus message", e);
        }
    }

    @Override
    public void updateOnValue(@Observes @UpdateOnResponseValueEvent ExchangeMessageEvent message){
        try {
            UpdateOnMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), UpdateOnMessageRequest.class);
            String onValue = request.getOnValue();
            String responseGuid = request.getResponseMessageGuid();
            exchangeLogService.updateExchangeResponseOnMessage(onValue,responseGuid);
        } catch ( ExchangeModelException e) {
            fireExchangeFault(message, "Could not update the UpdateOnMessageRequest entity ", e);
        }

    }

    @Override
    public void handleProcessedMovement(@Observes @HandleProcessedMovementEvent ExchangeMessageEvent message) {
        try {
            ProcessedMovementResponse request = (ProcessedMovementResponse) message.getExchangeBaseRequest();
            log.debug("Received processed movement from Rules:{}", request);
            String username;
            MovementRefType movementRefType = request.getMovementRefType();
            SetReportMovementType orgRequest = request.getOrgRequest();
            if (PluginType.MANUAL.equals(orgRequest.getPluginType())) {
                username = request.getUsername();
            } else {
                username = orgRequest.getPluginName();
            }
            ExchangeLogType log = ExchangeLogMapper.getReceivedMovementExchangeLog(orgRequest, movementRefType.getMovementRefGuid(), movementRefType.getType().value(), username,message);
            ExchangeLogType createdLog = exchangeLogService.log(log, username);
            LogRefType logTypeRef = createdLog.getTypeRef();
            if (logTypeRef != null && logTypeRef.getType() == TypeRefType.POLL) {
                String pollGuid = logTypeRef.getRefGuid();
                pollEvent.fire(new NotificationMessage("guid", pollGuid));
            }
        } catch (ExchangeLogException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void handleProcessedMovementBatch(@Observes @ProcessedMovementBatch ExchangeMessageEvent message) {
        try {
            ProcessedMovementResponseBatch request = (ProcessedMovementResponseBatch) message.getExchangeBaseRequest();
            log.debug("Received processed movement from Rules:{}", request);
            String username;
            MovementRefType movementRefType = request.getMovementRefType();
            List<SetReportMovementType> reportTypeList = request.getOrgRequest();
            SetReportMovementType setReportMovementType;
            if(CollectionUtils.isNotEmpty(reportTypeList)){
                setReportMovementType = reportTypeList.get(0);
            } else {
                setReportMovementType = new SetReportMovementType();
            }
            username = request.getUsername();
            ExchangeLogType log = ExchangeLogMapper.getReceivedMovementExchangeLog(setReportMovementType, movementRefType.getMovementRefGuid(), movementRefType.getType().value(), username,message);
            exchangeLogService.log(log, username);

            String pluginRequest = ExchangePluginRequestMapper.createSendFLUXMovementRequest(
                    JAXBMarshaller.marshallJaxBObjectToString(request),
                    request.getDestination(),
                    request.getSenderOrReceiver(),
                    request.getFluxDataFlow(),
                    log.getGuid(),
                    request.getAd());
            sendMovementReportToFLUX(pluginRequest, ExchangeServiceConstants.MOVEMENT_PLUGIN_SERVICE_NAME);
        } catch (ExchangeLogException | ExchangeMessageException | ExchangeModelMarshallException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void sendMovementReportToFLUX(String marshalledRequest, String serviceName) throws ExchangeMessageException {
        sendEventBusMessage(marshalledRequest, serviceName); // todo check the transactional on private method works on EJB/
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private String sendEventBusMessage(String marshalledRequest, String serviceName) throws ExchangeMessageException {
        try {
            log.debug("Sending event bus message from Exchange module to recipient om JMS Topic to: {} ", serviceName);
            return eventBusProducer.sendEventBusMessage(marshalledRequest, serviceName);
        } catch (MessageException e) {
            log.error("Error when sending message. ", e);
            throw new ExchangeMessageException("[ Error when sending message. ]", e);
        }
    }

    private void firePluginFault(ExchangeMessageEvent messageEvent, String errorMessage, Throwable exception) {
        log.error(errorMessage, exception);
        PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), errorMessage);
        pluginErrorEvent.fire(new PluginMessageEvent(messageEvent.getJmsMessage(), null, fault));
    }

    private void fireExchangeFault(ExchangeMessageEvent messageEvent, String errorMessage, Throwable exception) {
        log.error(errorMessage, exception);
        eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault exchangeFault = ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, errorMessage);
        exchangeErrorEvent.fire(new ExchangeMessageEvent(messageEvent.getJmsMessage(), exchangeFault));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private boolean validate(CommandType command, TextMessage origin, ServiceResponseType service, CommandType commandType, String username) {
        if (command == null) {
            String faultMessage = "No command";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (command.getCommand() == null) {
            String faultMessage = "No command type";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (command.getPluginName() == null) {
            String faultMessage = "No plugin to send to";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (service == null || service.getServiceClassName() == null || !service.getServiceClassName().equalsIgnoreCase(command.getPluginName())) {
            String faultMessage = "No plugin receiver available";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                log.error("Couldn't create unsentMessage " + e.getMessage(),e);
            }
            return false;
        } else if (command.getTimestamp() == null) {
            String faultMessage = "No timestamp";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (!StatusType.STARTED.equals(service.getStatus())) {
            log.info("Plugin to send report to is not started:{}",service);
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                log.error("Couldn't create unsentMessage " + e.getMessage(),e);
            }

            try {
                AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeNOK(origin.getJMSMessageID(), "Plugin to send command to is not started");
                String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
                auditProducer.sendResponseMessageToSender(origin, moduleResponse);
            } catch (JMSException | ExchangeModelMarshallException | MessageException e) {
                log.error("Plugin not started, couldn't send module response: " + e.getMessage(),e);
            }

            return false;
        }
        return true;
    }

    private Asset getAsset(String connectId) throws ExchangeLogException {
        Asset asset;
        try {
            asset = exchangeAssetService.getAsset(connectId);
        } catch (ExchangeServiceException e) {
            throw new ExchangeLogException("Couldn't create unsentMessage " + e.getMessage(),e);
        }
        return asset;
    }

    private List<UnsentMessageTypeProperty> getSetUnsentMessageTypePropertiesForPoll(CommandType commandType) throws ExchangeLogException {
        List<UnsentMessageTypeProperty> properties = new ArrayList<>();
        if (commandType.getPoll() != null) {
            String connectId = ExchangeLogMapper.getConnectId(commandType.getPoll());
            Asset asset = getAsset(connectId);
            properties = ExchangeLogMapper.getPropertiesForPoll(commandType.getPoll(), asset.getName());

        } else if (commandType.getEmail() != null) {
            properties = ExchangeLogMapper.getPropertiesForEmail(commandType.getEmail());

        }
        return properties;
    }

}
