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

import static eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType.BELGIAN_ACTIVITY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import org.apache.commons.collections.CollectionUtils;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ProcessedMovementResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ProcessedMovementResponseBatch;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFAQueryMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAResponseMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdateLogStatusRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefTypeType;
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
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypeProperty;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.event.PollEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeToMdrRulesMapper;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeEventOutgoingServiceBean implements ExchangeEventOutgoingService {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeEventOutgoingServiceBean.class);

    @Inject
    @ErrorEvent
    private Event<ExchangeErrorEvent> exchangeErrorEvent;

    @Inject
    @eu.europa.ec.fisheries.uvms.exchange.service.message.event.PluginErrorEvent
    private Event<PluginErrorEvent> pluginErrorEvent;

    @Inject
    @PollEvent
    private Event<NotificationMessage> pollEvent;

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ExchangeConsumer consumer;

    @EJB
    private ExchangeLogServiceBean exchangeLogService;

    @EJB
    private ExchangeServiceBean exchangeService;

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
        producer.sendEventBusMessage(marshalledRequest, serviceName);
    }

    @Override
    public void sendSalesReportToFLUX(SendSalesReportRequest sendSalesReportRequest) throws ExchangeModelMarshallException, ExchangeMessageException {
        String marshalledRequest = JAXBMarshaller.marshallJaxBObjectToString(sendSalesReportRequest);
        producer.sendEventBusMessage(marshalledRequest, ExchangeServiceConstants.FLUX_SALES_PLUGIN_SERVICE_NAME);
    }

    @Override
    public void sendAssetInformationToFLUX(PluginBaseRequest request) throws ExchangeModelMarshallException, ExchangeMessageException {
        String marshalledRequest = JAXBMarshaller.marshallJaxBObjectToString(request);
        producer.sendEventBusMessage(marshalledRequest, ExchangeServiceConstants.FLUX_VESSEL_PLUGIN_SERVICE_NAME);
    }

    @Override
    public void sendReportToPlugin(TextMessage message) {
        try {
            SendMovementToPluginRequest request = JAXBMarshaller.unmarshallTextMessage(message, SendMovementToPluginRequest.class);
            if(request.getUsername() == null){
                LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
                exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
                return;
            }
            LOG.info("Send report to plugin: {}",request);
            SendMovementToPluginType sendReport = request.getReport();

            String unsentMessageGuid;
            try {
                List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                unsentMessageGuid = exchangeLogService.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp().toInstant(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), message.getText(), unsentMessageProperties, request.getUsername());
            } catch (Exception e) {
                throw new IllegalStateException("Could not create unsent message ", e);
            }

            ServiceResponseType service = null;
            List<ServiceResponseType> services = exchangeService.getServiceList(Arrays.asList(sendReport.getPluginType()));
            for (ServiceResponseType serviceResponseType : services) {
                if (StatusType.STARTED.equals(serviceResponseType.getStatus())) {
                    service = serviceResponseType;
                }
            }
            
            if (service != null) {
                String serviceName = service.getServiceClassName();
                
                String text = ExchangePluginRequestMapper.createSetReportRequest(sendReport.getTimestamp().toInstant(), sendReport, unsentMessageGuid);
                String pluginMessageId = producer.sendEventBusMessage(text, serviceName);
                try {
                    ExchangeLogType log = ExchangeLogMapper.getSendMovementExchangeLog(sendReport);
                    exchangeLogService.logAndCache(log, pluginMessageId, request.getUsername());
                } catch (ExchangeLogException e) {
                    LOG.error("Could not create log", e);
                }
            } else {
                LOG.error("No report sent, no plugin of type " + sendReport.getPluginType() + " found");
            }
        } catch (ExchangeModelMarshallException | ExchangeServiceException | ExchangeMessageException e) {
            LOG.error("Could not send report to plugin", e);
            throw new IllegalStateException("Could not send report to plugin", e);
        }
    }


    /*
	 * Method for Observing the @MdrSyncRequestMessageEvent, meaning a message from Activity MDR
	 * module has arrived (synchronisation of a MDR Entity) which needs to be sent to EventBus Topic
	 * so that it gets intercepted by MDR Plugin Registered Subscriber and sent to Flux.
	 *
	 */
    @Override
    public void forwardMdrSyncMessageToPlugin(TextMessage message) {               //not adding anything to the exchange log????
        try {
            LOG.info("[INFO] Received MdrSyncMessageEvent. Going to send to the Plugin now..");
            String marshalledReq = ExchangeToMdrRulesMapper.mapExchangeToMdrPluginRequest(message);
            producer.sendEventBusMessage(marshalledReq, ExchangeServiceConstants.MDR_PLUGIN_SERVICE_NAME);
        } catch (Exception e) {
            LOG.error("[ERROR] Something strange happend during message conversion {} {}",message,e);       //so, if we dont update the mdr plugin bc of an exception, we just ignore the entire message?
        }
    }

    @Override
    public void sendCommandToPlugin(TextMessage message) {
        SetCommandRequest request = new SetCommandRequest();
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);
            if(request.getUsername() == null){
                LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
                exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
                return;
            }

            LOG.info("Send command to plugin:{}",request);
            String pluginName = request.getCommand().getPluginName();

            ServiceResponseType service = exchangeService.getService(pluginName);

            if (validate(request.getCommand(), message, service, request.getCommand(), request.getUsername())) {
                sendCommandToPlugin(request, service.getName(), message.getText());
            } else {
            LOG.debug("Can not send to plugin. Response sent to caller.");
            }
        } catch (NullPointerException | ExchangeException e) {
            if (request.getCommand().getCommand() != CommandTypeType.EMAIL) {
                LOG.error("[ Error when sending command to plugin {} ]", e);
                if (getTimesRedelivered(message) > MessageConstants.JMS_MAX_REDELIVERIES) {
                    exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Exception when sending command to plugin")));
                }
            }
            throw new IllegalStateException("Error when sending command to plugin", e);
        } catch (JMSException ex) {
            LOG.error("[ Error when creating unsent message {} ]", ex);
            throw new IllegalStateException("Error when sending command to plugin", ex);
        }
    }

    @Override
    public String sendCommandToPluginFromRest(SetCommandRequest request){
        try {
            ServiceResponseType service = exchangeService.getService(request.getCommand().getPluginName());
            CommandType command = request.getCommand();
            String validationResult = validateRestCommand(request, service, command);
            if (validationResult.equals("OK")) {
                String marshalled = JAXBMarshaller.marshallJaxBObjectToString(request);
                sendCommandToPlugin(request, service.getName(), marshalled);
            }
            return validationResult;
        }catch (ExchangeMessageException | ExchangeModelMarshallException | ExchangeLogException | ExchangeServiceException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void sendFLUXFAResponseToPlugin(TextMessage message) {
        try {
            SetFLUXFAResponseMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAResponseMessageRequest.class);
            LOG.debug("[INFO] Got FLUXFAResponse in exchange with destination :" + request.getDestination());
            String text = ExchangePluginRequestMapper.createSetFLUXFAResponseRequestWithOn(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getOnValue());
            final ExchangeLogType logType = exchangeLogService.log(request, LogType.SEND_FLUX_RESPONSE_MSG, request.getStatus(), TypeRefType.FA_RESPONSE, request.getRequest(), false);
            if(!logType.getStatus().equals(ExchangeLogStatusTypeType.FAILED)){ // Send response only if it is NOT FAILED
                LOG.debug("[START] Sending FLUXFAResponse to Flux Activity Plugin..");
                String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                        ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
                LOG.debug("[END] FLUXFAResponse sent to Flux Activity Plugin {}" + pluginMessageId);
            } else {
                LOG.info("[WARN] FLUXFAResponse is FAILED so won't be sent to Flux Activity Plugin..");
            }
        } catch (Exception e /*ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e*/) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }

    @Override
    public void sendFLUXFAQueryToPlugin(TextMessage message) {
        try {
            SetFAQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFAQueryMessageRequest.class);
            LOG.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text = ExchangePluginRequestMapper.createSendFLUXFAQueryRequest(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver());
            LOG.debug("Message to plugin {}", text);
            String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            LOG.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            exchangeLogService.log(request, LogType.SEND_FA_QUERY_MSG, ExchangeLogStatusTypeType.SENT, TypeRefType.FA_QUERY, request.getRequest(), false);
        } catch (Exception e /*ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e*/) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }


    @Override
    public void sendFLUXFAReportToPlugin(TextMessage message) {
        try {
            SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAReportMessageRequest.class);
            LOG.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text = ExchangePluginRequestMapper.createSendFLUXFAReportRequest(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver());
            LOG.debug("Message to plugin {}", text);
            String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            LOG.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            exchangeLogService.log(request, LogType.SEND_FLUX_FA_REPORT_MSG, ExchangeLogStatusTypeType.SENT, TypeRefType.FA_REPORT, request.getRequest(), false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);   //well you might have, since you first send and then log so if something goes wrong on the log it is already sent......
        }
    }

    @Override
    public void sendSalesResponse(TextMessage message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesResponseRequest request = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesResponseRequest.class);
            ExchangeLogStatusTypeType validationStatus = request.getValidationStatus();
            exchangeLogService.log(request, LogType.SEND_SALES_RESPONSE, validationStatus, TypeRefType.SALES_RESPONSE, request.getResponse(), false);
            if (validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL || validationStatus == ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS) {
                eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest pluginRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest();
                pluginRequest.setRecipient(request.getSenderOrReceiver());
                pluginRequest.setResponse(request.getResponse());
                pluginRequest.setMethod(ExchangePluginMethod.SEND_SALES_RESPONSE);
                exchangeEventOutgoingService.sendSalesResponseToPlugin(pluginRequest, request.getPluginType());
            } else {
                LOG.error("Received invalid response from the Sales module: " + request.getResponse());
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales response.", e);
        }
    }

    @Override
    public void sendSalesReport(TextMessage message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest request = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest.class);
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
                LOG.error("Received invalid report from the Sales module: " + request.getReport());
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales report.", e);
        }
    }

    @Override
    public void sendAssetInformation(TextMessage event) {
        try {
            SendAssetInformationRequest incomingRequest = JAXBMarshaller.unmarshallTextMessage(event, SendAssetInformationRequest.class);
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
    public void updateLogStatus(TextMessage message) {
        try {
            UpdateLogStatusRequest request = JAXBMarshaller.unmarshallTextMessage(message, UpdateLogStatusRequest.class);
            UUID logGuid = UUID.fromString(request.getLogGuid());
            ExchangeLogStatusTypeType status = request.getNewStatus();
            exchangeLogService.updateStatus(logGuid, status);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not update the status of a message log.", e);
        } catch (ExchangeModelMarshallException e) {
            fireExchangeFault(message, "Could not unmarshall the incoming UpdateLogStatus message", e);
        }
    }

    @Override
    public void updateLogBusinessError(TextMessage message) {  //should this chain not set a log status or something?
        try {
            UpdateLogStatusRequest request = JAXBMarshaller.unmarshallTextMessage(message, UpdateLogStatusRequest.class);
            UUID exchangeLogGuid = UUID.fromString(request.getLogGuid());
            String businessModuleExceptionMessage = request.getBusinessModuleExceptionMessage();
            exchangeLogService.updateExchangeLogBusinessError(exchangeLogGuid, businessModuleExceptionMessage);
        } catch (ExchangeLogException | ExchangeModelMarshallException e) {
            fireExchangeFault(message, "Could not unmarshall the incoming UpdateLogStatus message", e);
        }
    }

    @Override
    public void handleProcessedMovement(TextMessage message) {
        try {
            ProcessedMovementResponse response = JAXBMarshaller.unmarshallTextMessage(message, ProcessedMovementResponse.class);
            if(response.getUsername() == null){
                LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
                exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
                return;
            }
            LOG.debug("Received processed movement from Movement:{}", response);
            MovementRefType movementRefType = response.getMovementRefType();
            if (movementRefType.getAckResponseMessageID() == null) {
                return;
            }
            ExchangeLogStatusTypeType statusType;
            if (movementRefType.getType().equals(MovementRefTypeType.ALARM)) { 
                statusType = ExchangeLogStatusTypeType.FAILED;
            } else {
                statusType = ExchangeLogStatusTypeType.SUCCESSFUL;
            }
            ExchangeLogType updatedLog = exchangeLogService.updateStatus(UUID.fromString(movementRefType.getAckResponseMessageID()), statusType);
            exchangeLogService.updateTypeRef(updatedLog, movementRefType);

        } catch (ExchangeLogException | ExchangeModelException e) {
            LOG.error("Could not handle processed movement", e);
            throw new IllegalArgumentException("Could not handle processed movement", e);
        }
    }

    @Override
    public void handleProcessedMovementBatch(TextMessage message) {
        try {
            ProcessedMovementResponseBatch request = JAXBMarshaller.unmarshallTextMessage(message, ProcessedMovementResponseBatch.class);
            LOG.debug("Received processed movement from Rules:{}", request);
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
            ExchangeLogType log = ExchangeLogMapper.getReceivedMovementExchangeLog(setReportMovementType, movementRefType.getMovementRefGuid(), movementRefType.getType().value(), username);
            exchangeLogService.log(log, username);
        } catch (ExchangeLogException | ExchangeModelMarshallException e) {
            LOG.error(e.getMessage());
        }
    }

    private void firePluginFault(TextMessage messageEvent, String errorMessage, Throwable exception) {
        LOG.error(errorMessage, exception);
        PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), errorMessage);
        pluginErrorEvent.fire(new PluginErrorEvent(messageEvent, null, fault));
    }

    private void fireExchangeFault(TextMessage messageEvent, String errorMessage, Throwable exception) {
        LOG.error(errorMessage, exception);
        eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault exchangeFault = ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, errorMessage);
        exchangeErrorEvent.fire(new ExchangeErrorEvent(messageEvent, exchangeFault));
    }


    private boolean validate(CommandType command, TextMessage origin, ServiceResponseType service, CommandType commandType, String username) {
        if (command == null) {
            String faultMessage = "No command";
            exchangeErrorEvent.fire(new ExchangeErrorEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (command.getCommand() == null) {
            String faultMessage = "No command type";
            exchangeErrorEvent.fire(new ExchangeErrorEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (command.getPluginName() == null) {
            String faultMessage = "No plugin to send to";
            exchangeErrorEvent.fire(new ExchangeErrorEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (service == null || service.getServiceClassName() == null || !service.getServiceClassName().equalsIgnoreCase(command.getPluginName())) {                  //this can never happen since a nullpointer is thrown in the remapping process
            String faultMessage = "No plugin receiver available";
            exchangeErrorEvent.fire(new ExchangeErrorEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp().toInstant(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }
            return false;
        } else if (command.getTimestamp() == null) {
            String faultMessage = "No timestamp";
            exchangeErrorEvent.fire(new ExchangeErrorEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (!StatusType.STARTED.equals(service.getStatus())) {
            LOG.info("Plugin to send report to is not started:{}",service);
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp().toInstant(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }

            try {
                AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeNOK(origin.getJMSMessageID(), "Plugin to send command to is not started");
                String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
                producer.sendModuleResponseMessage(origin, moduleResponse);
            } catch (JMSException | ExchangeModelMarshallException | MessageException e) {
                LOG.error("Plugin not started, couldn't send module response: " + e.getMessage());
            }

            return false;
        }
        return true;
    }

    private String validateRestCommand(SetCommandRequest request, ServiceResponseType service, CommandType command) throws ExchangeServiceException {
        String faultMessage = "OK";
        if (command == null) {
            faultMessage = "No command";
        } else if (command.getCommand() == null) {
            faultMessage = "No command type";
        } else if (command.getPluginName() == null) {
            faultMessage = "No plugin to send to";
        } else if (service == null || service.getServiceClassName() == null || !service.getServiceClassName().equalsIgnoreCase(command.getPluginName())) {
            faultMessage = "No plugin receiver available";
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(request.getCommand());
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp().toInstant(), command.getCommand().name(), request.toString(), setUnsentMessageTypePropertiesForPoll, request.getUsername());
            } catch (ExchangeLogException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }
        } else if (command.getTimestamp() == null) {
            faultMessage = "No timestamp";
        } else if (!StatusType.STARTED.equals(service.getStatus())) {
            LOG.info("Plugin to send report to is not started:{}",service);
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(request.getCommand());
                exchangeLogService.createUnsentMessage(service.getName(), command.getTimestamp().toInstant(), command.getCommand().name(), request.toString(), setUnsentMessageTypePropertiesForPoll, request.getUsername());
            } catch (ExchangeLogException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }
        }
        return faultMessage;
    }


    private void sendCommandToPlugin(SetCommandRequest request, String serviceName, String originalJMSText) throws ExchangeMessageException, ExchangeModelMarshallException, ExchangeLogException {

        CommandType commandType = request.getCommand();

        List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
        String unsentMessageGuid = exchangeLogService.createUnsentMessage(serviceName, request.getCommand().getTimestamp().toInstant(), request.getCommand().getCommand().name(), originalJMSText, setUnsentMessageTypePropertiesForPoll, request.getUsername());

        request.getCommand().setUnsentMessageGuid(unsentMessageGuid);
        String text = ExchangePluginRequestMapper.createSetCommandRequest(request.getCommand());
        String pluginMessageId = producer.sendEventBusMessage(text, request.getCommand().getPluginName());

        try {
            ExchangeLogType log = ExchangeLogMapper.getSendCommandExchangeLog(request.getCommand());
            exchangeLogService.logAndCache(log, pluginMessageId, request.getUsername());
        } catch (ExchangeLogException e) {
            LOG.error("Could not create log", e);
        }
    }

    private Asset getAsset(String connectId) throws ExchangeLogException {
        Asset asset;
        try {
            asset = exchangeAssetService.getAsset(connectId);
        } catch (ExchangeServiceException e) {
            LOG.error("Couldn't create unsentMessage " + e.getMessage());
            throw new ExchangeLogException(e.getMessage(), e);
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

    private int getTimesRedelivered(TextMessage message) {
        try {
            return (message.getIntProperty("JMSXDeliveryCount") - 1);

        } catch (Exception e) {
            return 0;
        }
    }
}
