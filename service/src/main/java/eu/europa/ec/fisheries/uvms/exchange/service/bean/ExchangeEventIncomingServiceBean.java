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
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.schema.movement.module.v1.ProcessedMovementAck;
import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRulesResponse;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.*;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventIncomingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangePluginStatusEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.PollEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.MovementMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.PluginTypeMapper;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.MovementModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
public class ExchangeEventIncomingServiceBean implements ExchangeEventIncomingService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventIncomingServiceBean.class);

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> exchangeErrorEvent;

    @Inject
    @PluginErrorEvent
    Event<PluginMessageEvent> pluginErrorEvent;

    @EJB
    ExchangeLogService exchangeLog;

    @EJB
    MessageProducer producer;

    @EJB
    ExchangeService exchangeService;

    @EJB
    ExchangeEventOutgoingService exchangeEventOutgoingService;

    @Inject
    @ExchangePluginStatusEvent
    Event<NotificationMessage> pluginStatusEvent;

    @Inject
    @PollEvent
    Event<NotificationMessage> pollEvent;

    @Override
    public void processFLUXFAReportMessage(@Observes @SetFluxFAReportMessageEvent ExchangeMessageEvent message) {
        LOG.info("Process FLUXFAReportMessage:{}",message);
        try {
            SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            PluginType exchangePluginType = request.getPluginType();
            eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType rulesPluginType =
                    exchangePluginType == PluginType.MANUAL
                            ? eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.MANUAL
                            : eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.FLUX;
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RCV_FLUX_FA_REPORT_MSG, ExchangeLogStatusTypeType.ISSUED, TypeRefType.FA_REPORT, request.getRequest(), true);
            String logId = null;
            if (exchangeLogType == null) {
                LOG.error("ExchangeLogType received is NULL while trying to save RECEIVE_FLUX_FA_REPORT_MSG {}",message);
            } else {
                logId = exchangeLogType.getGuid();
                LOG.info("SetFLUXFAReportMessageRequest Logged to Exchange:" + logId);
            }


            String msg = RulesModuleRequestMapper.createSetFLUXFAReportMessageRequest(rulesPluginType, request.getRequest(), request.getUsername(), logId, request.getFluxDataFlow(), request.getSenderOrReceiver());

            //Improvement that could be done is to pass the service. For this purpose
            //we need first to set the plugin name, which requires BaseExchangeRequest XSD modification, as well as in ActivityPlugin
            forwardToRules(msg, message, null);

        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            LOG.error("Couldn't map to SetFLUXFAReportMessageRequest when processing FLUXFAReportMessage from plugin", e);
        } catch (ExchangeLogException e) {
            LOG.error("Couldn't log FAReportMessage received from plugin into database", e);
        }

    }

    /*
     * Method for Observing the @MdrSyncMessageEvent, meaning a message from Activity MDR
	 * module has arrived (synchronisation of the mdr).
	 *
	 */
    @Override
    public void sendResponseToRulesModule(@Observes @MdrSyncResponseMessageEvent ExchangeMessageEvent message) {
        LOG.info("Received @MdrSyncResponseMessageEvent.:{}",message);

        TextMessage requestMessage = message.getJmsMessage();
        try {
            SetFLUXMDRSyncMessageExchangeResponse exchangeResponse = JAXBMarshaller.unmarshallTextMessage(requestMessage, SetFLUXMDRSyncMessageExchangeResponse.class);
            String strRequest = exchangeResponse.getRequest();
            SetFLUXMDRSyncMessageRulesResponse mdrResponse = new SetFLUXMDRSyncMessageRulesResponse();
            mdrResponse.setMethod(RulesModuleMethod.GET_FLUX_MDR_SYNC_RESPONSE);
            mdrResponse.setRequest(strRequest);
            String mdrStrReq = JAXBMarshaller.marshallJaxBObjectToString(mdrResponse);

            forwardToRules(mdrStrReq, null, null);

        } catch (Exception e) {
            LOG.error("Something strange happend during message conversion {} {}",message,e);
        }
    }

    @Override
    public void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        LOG.info("Get plugin config LIST_SERVICE:{}",message);
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, GetServiceListRequest.class);
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
        } catch (ExchangeException e) {
            LOG.error("[ Error when getting plugin list from source {}] {}",message,e);
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
        }
    }

    @Override
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
        LOG.info("Process movement:{}",message);
        try {
            SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetMovementReportRequest.class);
            String username;

            // A person has created a position
            if (MovementSourceType.MANUAL.equals(request.getRequest().getMovement().getSource())) {
                username = request.getUsername();

                // Send some response to Movement, if it originated from there (manual movement)
                ProcessedMovementAck response = MovementModuleResponseMapper.mapProcessedMovementAck(eu.europa.ec.fisheries.schema.movement.common.v1.AcknowledgeTypeType.OK, message.getJmsMessage().getJMSMessageID(), "Movement successfully processed");
                producer.sendModuleAckMessage(message.getJmsMessage().getJMSMessageID(), MessageQueue.MOVEMENT_RESPONSE, JAXBMarshaller.marshallJaxBObjectToString(response));
            } // A plugin has reported a position
            else {
                username = request.getRequest().getPluginType().name();
            }

            String pluginName = request.getRequest().getPluginName();
            ServiceResponseType service = exchangeService.getService(pluginName);

            PluginType pluginType = request.getRequest().getPluginType();


            if (validate(request.getRequest(), service, message.getJmsMessage())) {
                MovementBaseType baseMovement = request.getRequest().getMovement();
                RawMovementType rawMovement = MovementMapper.getInstance().getMapper().map(baseMovement, RawMovementType.class);
                if (rawMovement.getAssetId() != null && rawMovement.getAssetId().getAssetIdList() != null) {
                    rawMovement.getAssetId().getAssetIdList().addAll(MovementMapper.mapAssetIdList(baseMovement.getAssetId().getAssetIdList()));
                }
                if (baseMovement.getMobileTerminalId() != null && baseMovement.getMobileTerminalId().getMobileTerminalIdList() != null) {
                    rawMovement.getMobileTerminal().getMobileTerminalIdList().addAll(MovementMapper.mapMobileTerminalIdList(baseMovement.getMobileTerminalId().getMobileTerminalIdList()));
                }

                rawMovement.setPluginType(pluginType.value());
                rawMovement.setPluginName(pluginName);
                rawMovement.setDateRecieved(request.getRequest().getTimestamp());
                // TODO: Temporary - probably better to change corr id to have the same though the entire flow; then we can use this to send response to original caller from anywhere needed
                rawMovement.setAckResponseMessageID(message.getJmsMessage().getJMSMessageID());

                String msg = RulesModuleRequestMapper.createSetMovementReportRequest(PluginTypeMapper.map(pluginType), rawMovement, username);
                forwardToRules(msg, message, service);
            } else {
                LOG.debug("Validation error. Event sent to plugin {}",message);
            }

        } catch (ExchangeServiceException e) {
            //TODO send back to plugin
        } catch (ExchangeModelMarshallException e) {
            //Cannot send back fault to unknown sender
            LOG.error("Couldn't map to SetMovementReportRequest when processing movement from plugin:{} {}",message,e);
        } catch (JMSException e) {
            LOG.error("Failed to get response queue:{} {}",message,e);
        } catch (RulesModelMapperException e) {
            LOG.error("Failed to build Rules momvent request:{} {}",message,e);;
        }
    }

    private void forwardToRules(String messageToForward) {
        forwardToRules(messageToForward, null, null);
    }

    /**
     * forwards serialized message to Rules module
     *
     * @param messageToForward
     * @param exchangeMessageEvent is optional
     * @param service              is optional
     */
    private void forwardToRules(String messageToForward, ExchangeMessageEvent exchangeMessageEvent, ServiceResponseType service) {
        try {
            producer.sendMessageOnQueue(messageToForward, MessageQueue.RULES);
        } catch (ExchangeMessageException e) {
            LOG.error("Failed to forward message to Rules: {} {}",messageToForward, e);

      /*      if (service!= null && exchangeMessageEvent != null) {
                PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), "Message cannot be sent to Rules module [ " + e.getMessage() + " ]");
                pluginErrorEvent.fire(new PluginMessageEvent(exchangeMessageEvent.getJmsMessage(), service, fault));
            } */
        }
    }

    @Override
    public void receiveSalesReport(@Observes @ReceiveSalesReportEvent ExchangeMessageEvent event) {
        LOG.info("Receive sales report in Exchange module:{}",event);

        try {
            ReceiveSalesReportRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveSalesReportRequest.class);
            String report = request.getReport();
            PluginType plugin = request.getPluginType();
            String sender = request.getSenderOrReceiver();
            String messageGuid = request.getMessageGuid();

            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_REPORT, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_REPORT, report, true);

            forwardToRules(RulesModuleRequestMapper.createReceiveSalesReportRequest(report, messageGuid, plugin.name(), log.getGuid(), sender));
        } catch (ExchangeModelMarshallException e) {
            try {
                String errorMessage = "Couldn't map to SetSalesReportRequest when processing sales report from plugin. The event was " + event.getJmsMessage().getText();
                firePluginFault(event, errorMessage, e);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to SetSalesReportRequest when processing sales report from plugin.", e);
            }
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales report.", e);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales report.", e);
        }
    }

    @Override
    public void receiveSalesQuery(@Observes @ReceiveSalesQueryEvent ExchangeMessageEvent event) {
        LOG.info("Process sales query in Exchange module:{}",event);

        try {
            ReceiveSalesQueryRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveSalesQueryRequest.class);
            String query = request.getQuery();
            PluginType plugin = request.getPluginType();
            String sender = request.getSenderOrReceiver();
            String messageGuid = request.getMessageGuid();

            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_QUERY, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_QUERY, query, true);

            forwardToRules(RulesModuleRequestMapper.createReceiveSalesQueryRequest(query, messageGuid, plugin.name(), log.getGuid(), sender));

        } catch (ExchangeModelMarshallException e) {
            try {
                firePluginFault(event, "Couldn't map to SalesQueryRequest when processing sales query from plugin. The message was " + event.getJmsMessage().getText(), e);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to SalesQueryRequest when processing sales query from plugin.", e);
            }
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales query.", e);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales query.", e);
        }
    }

    @Override
    public void receiveSalesResponse(@Observes @ReceiveSalesResponseEvent ExchangeMessageEvent event) {
        try {
            ReceiveSalesResponseRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveSalesResponseRequest.class);
            String response = request.getResponse();

            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_RESPONSE, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_RESPONSE, response, true);

            forwardToRules(RulesModuleRequestMapper.createReceiveSalesResponseRequest(response, log.getGuid()));
        } catch (ExchangeModelMarshallException e) {
            firePluginFault(event, "Error when receiving a Sales response from FLUX", e);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales response.", e);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales response.", e);
        }
    }

    @Override
    public void sendSalesResponse(@Observes @SendSalesResponseEvent ExchangeMessageEvent message) {
        try {
            SendSalesResponseRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendSalesResponseRequest.class);

            eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest pluginRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest();
            pluginRequest.setRecipient(request.getSenderOrReceiver());
            pluginRequest.setResponse(request.getResponse());
            pluginRequest.setMethod(ExchangePluginMethod.SEND_SALES_RESPONSE);

            exchangeLog.log(request, LogType.SEND_SALES_RESPONSE, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.SALES_RESPONSE, request.getResponse(), false);

            exchangeEventOutgoingService.sendSalesResponseToFLUX(pluginRequest);
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales response.", e);
        }
    }

    @Override
    public void sendSalesReport(@Observes @SendSalesReportEvent ExchangeMessageEvent message) {
        try {
            SendSalesReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendSalesReportRequest.class);

            eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest pluginRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest();
            pluginRequest.setRecipient(request.getSenderOrReceiver());
            pluginRequest.setReport(request.getReport());
            pluginRequest.setMethod(ExchangePluginMethod.SEND_SALES_RESPONSE);

            exchangeLog.log(request, LogType.SEND_SALES_REPORT, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.SALES_REPORT, request.getReport(), false);

            exchangeEventOutgoingService.sendSalesReportToFLUX(pluginRequest);
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(message, "Error when sending a Sales response to FLUX", e);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not log the outgoing sales report.", e);
        }
    }

    @Override
    public void updateLogStatus(@Observes @UpdateLogStatusEvent ExchangeMessageEvent message) {
        try {
            UpdateLogStatusRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), UpdateLogStatusRequest.class);
            String logGuid = request.getLogGuid();
            ExchangeLogStatusTypeType status = request.getNewStatus();

            exchangeLog.updateStatus(logGuid, status);
        } catch (ExchangeLogException e) {
            fireExchangeFault(message, "Could not update the status of a message log.", e);
        } catch (ExchangeModelMarshallException e) {
            fireExchangeFault(message, "Could not unmarshall the incoming UpdateLogStatus message", e);
        }
    }

    // Async response handler for processed movements
    @Override
    public void handleProcessedMovement(@Observes @HandleProcessedMovementEvent ExchangeMessageEvent message) {
        LOG.debug("Received processed movement from Rules:{}",message);
        try {
            ProcessedMovementResponse request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), ProcessedMovementResponse.class);
            String username;
            MovementRefType movementRefType = request.getMovementRefType();
            SetReportMovementType orgRequest = request.getOrgRequest();

            if (PluginType.MANUAL.equals(orgRequest.getPluginType())) {
                username = request.getUsername();
            } else {
                username = orgRequest.getPluginName();
            }

            ExchangeLogType log = ExchangeLogMapper.getReceivedMovementExchangeLog(orgRequest, movementRefType.getMovementRefGuid(), movementRefType.getType().value(), username);
            ExchangeLogType createdLog = exchangeLog.log(log, username);

            LogRefType logTypeRef = createdLog.getTypeRef();
            if (logTypeRef != null && logTypeRef.getType() == TypeRefType.POLL) {
                String pollGuid = logTypeRef.getRefGuid();
                pollEvent.fire(new NotificationMessage("guid", pollGuid));
            }
        } catch (ExchangeLogException | ExchangeModelMarshallException e) {
            LOG.error(e.getMessage());
        }
    }


    private void firePluginFault(ExchangeMessageEvent messageEvent, String errorMessage, Throwable exception) {
        LOG.error(errorMessage, exception);
        PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), errorMessage);
        pluginErrorEvent.fire(new PluginMessageEvent(messageEvent.getJmsMessage(), null, fault));
    }

    private void fireExchangeFault(ExchangeMessageEvent messageEvent, String errorMessage, Throwable exception) {
        LOG.error(errorMessage, exception);
        eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault exchangeFault = ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, errorMessage);
        exchangeErrorEvent.fire(new ExchangeMessageEvent(messageEvent.getJmsMessage(), exchangeFault));
    }


    private boolean validate(SetReportMovementType setReport, ServiceResponseType service, TextMessage origin) {
        if (setReport == null) {
            String faultMessage = "No setReport request";
            pluginErrorEvent.fire(new PluginMessageEvent(origin, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
            return false;
        } else if (setReport.getMovement() == null) {
            String faultMessage = "No movement in setReport request";
            pluginErrorEvent.fire(new PluginMessageEvent(origin, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
            return false;
        } else if (setReport.getPluginType() == null) {
            String faultMessage = "No pluginType in setReport request";
            pluginErrorEvent.fire(new PluginMessageEvent(origin, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
            return false;
        } else if (setReport.getPluginName() == null || setReport.getPluginName().isEmpty()) {
            String faultMessage = "No pluginName in setReport request";
            pluginErrorEvent.fire(new PluginMessageEvent(origin, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
            return false;
        } else if (setReport.getTimestamp() == null) {
            String faultMessage = "No timestamp in setReport request";
            pluginErrorEvent.fire(new PluginMessageEvent(origin, service, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.PLUGIN_VALIDATION.getCode(), faultMessage)));
            return false;
        }
        return true;
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
    public void processPluginPing(@Observes @PluginPingEvent ExchangeMessageEvent message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse response = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse.class);
            //TODO handle ping response from plugin, eg. no serviceClassName in response
            LOG.info("FIX ME handle ping response from plugin");
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't process ping response from plugin {} {} ",message, e.getMessage());
        }
    }

    @Override
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message) {
        LOG.info("Process acknowledge:{}",message);

        try {
            AcknowledgeResponse response = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), AcknowledgeResponse.class);
            AcknowledgeType acknowledge = response.getResponse();
            String serviceClassName = response.getServiceClassName();
            ExchangePluginMethod method = response.getMethod();
            switch (method) {
                case SET_COMMAND:
                    // Only Acknowledge for poll should have a poll status set
                    if (acknowledge.getPollStatus() != null && acknowledge.getPollStatus().getPollId() != null) {
                        handleSetPollStatusAcknowledge(method, serviceClassName, acknowledge);
                    } else {
                        handleUpdateExchangeLogAcknowledge(method, serviceClassName, acknowledge);
                    }
                    break;
                case SET_REPORT:
                    handleUpdateExchangeLogAcknowledge(method, serviceClassName, acknowledge);
                    break;
                case START:
                    handleUpdateServiceAcknowledge(serviceClassName, acknowledge, StatusType.STARTED);
                    pluginStatusEvent.fire(createNotificationMessage(serviceClassName, true));
                    break;
                case STOP:
                    handleUpdateServiceAcknowledge(serviceClassName, acknowledge, StatusType.STOPPED);
                    pluginStatusEvent.fire(createNotificationMessage(serviceClassName, false));
                    break;
                case SET_CONFIG:
                default:
                    handleAcknowledge(method, serviceClassName, acknowledge);
                    break;
            }
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Process acknowledge couldn't be marshalled {} {}",message,e);
        } catch (ExchangeServiceException e) {
            //TODO Audit.log() couldn't process acknowledge in exchange service
            LOG.error("Couldn't process acknowledge in exchange service:{} {} ",message,  e.getMessage());
        }
    }

    private void handleUpdateExchangeLogAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {

        ExchangeLogStatusTypeType logStatus = ExchangeLogStatusTypeType.FAILED;
        switch (ack.getType()) {
            case OK:
                //TODO if(poll probably transmitted)
                logStatus = ExchangeLogStatusTypeType.SUCCESSFUL;
                try {
                    exchangeLog.removeUnsentMessage(ack.getUnsentMessageGuid(), serviceClassName);
                } catch (ExchangeLogException ex) {
                    LOG.error(ex.getMessage());
                }
                break;
            case NOK:
                LOG.debug(method + " was NOK: " + ack.getMessage());
                break;
        }

        try {
            ExchangeLogType updatedLog = exchangeLog.updateStatus(ack.getMessageId(), logStatus, serviceClassName);

            // Long polling
            LogRefType typeRef = updatedLog.getTypeRef();
            if (typeRef != null && typeRef.getType() == TypeRefType.POLL) {
                String pollGuid = typeRef.getRefGuid();
                pollEvent.fire(new NotificationMessage("guid", pollGuid));
            }
        } catch (ExchangeLogException e) {
            LOG.error(e.getMessage());
        }
    }

    private void handleSetPollStatusAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {
        LOG.debug(method + " was acknowledged in " + serviceClassName);
        try {
            PollStatus updatedLog = exchangeLog.setPollStatus(ack.getMessageId(), ack.getPollStatus().getPollId(), ack.getPollStatus().getStatus(), serviceClassName);

            // Long polling
            pollEvent.fire(new NotificationMessage("guid", updatedLog.getPollGuid()));
        } catch (ExchangeLogException e) {
            LOG.error(e.getMessage());
        }
    }

    private void handleUpdateServiceAcknowledge(String serviceClassName, AcknowledgeType ack, StatusType status) throws ExchangeServiceException {
        switch (ack.getType()) {
            case OK:
                exchangeService.updateServiceStatus(serviceClassName, status, serviceClassName);
                break;
            case NOK:
                //TODO Audit.log()
                LOG.error("Couldn't start service " + serviceClassName);
                break;
        }
    }

    private void handleAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {
        LOG.debug(method + " was acknowledged in " + serviceClassName);
        switch (ack.getType()) {
            case OK:
                break;
            case NOK:
                //TODO Audit.log()
                LOG.error(serviceClassName + " didn't like it. " + ack.getMessage());
                break;
        }
    }

    private NotificationMessage createNotificationMessage(String serviceClassName, boolean started) {
        NotificationMessage msg = new NotificationMessage("serviceClassName", serviceClassName);
        msg.setProperty("started", started);
        return msg;
    }

}