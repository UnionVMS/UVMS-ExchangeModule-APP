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

import static eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils.unMarshallMessage;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.xml.bind.JAXBException;

import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import org.apache.commons.collections.CollectionUtils;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRulesResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.exchange.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
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
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.MovementMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Stateless
public class ExchangeEventIncomingServiceBean implements ExchangeEventIncomingService {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeEventIncomingServiceBean.class);

    @Inject
    @ErrorEvent
    private Event<ExchangeErrorEvent> exchangeErrorEvent;

    @Inject
    @eu.europa.ec.fisheries.uvms.exchange.service.message.event.PluginErrorEvent
    private Event<PluginErrorEvent> pluginErrorEvent;

    @Inject
    @ExchangePluginStatusEvent
    private Event<NotificationMessage> pluginStatusEvent;

    @Inject
    @PollEvent
    private Event<NotificationMessage> pollEvent;

    @EJB
    private ExchangeLogService exchangeLog;

    @EJB
    private ExchangeLogModel exchangeLogModel;

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private ExchangeEventOutgoingService exchangeEventOutgoingService;

    private Jsonb jsonb = JsonbBuilder.create();
    
    @Override
    public void processFLUXFAReportMessage(TextMessage message) {
        try {
            SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAReportMessageRequest.class);
            String onValue = request.getOnValue();
            String username = request.getUsername();
            String fluxDataFlow = request.getFluxDataFlow();
            String senderOrReceiver = request.getSenderOrReceiver();
            LOG.debug("Got FLUXFAReportMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RCV_FLUX_FA_REPORT_MSG, ExchangeLogStatusTypeType.ISSUED
                    , extractFaType(request.getMethod()), request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createSetFLUXFAReportMessageRequest(extractPluginType(request), request.getRequest()
                    , username, extractLogId(message, exchangeLogType), fluxDataFlow, senderOrReceiver, onValue);
            forwardToRules(msg);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            LOG.error("Couldn't map to SetFLUXFAReportMessageRequest when processing FLUXFAReportMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            LOG.error("Couldn't log FAReportMessage received from plugin into database", e);
        }
    }

    @Override
    public void processFAQueryMessage(TextMessage message) {
        try {
            SetFAQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFAQueryMessageRequest.class);
            LOG.debug("Got FAQueryMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RECEIVE_FA_QUERY_MSG, ExchangeLogStatusTypeType.ISSUED
                    , TypeRefType.FA_QUERY, request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createSetFaQueryMessageRequest(extractPluginType(request), request.getRequest(),
                    request.getUsername(), extractLogId(message, exchangeLogType), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getOnValue());
            forwardToRules(msg);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            LOG.error("Couldn't map to SetFAQueryMessageRequest when processing FAQueryMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            LOG.error("Couldn't log FAQueryMessage received from plugin into database", e);
        }
    }

    @Override
    public void processFluxFAResponseMessage(TextMessage message) {
        try {
            RcvFLUXFaResponseMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message, RcvFLUXFaResponseMessageRequest.class);
            LOG.debug("Got FLUXResponseMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RECEIVE_FLUX_RESPONSE_MSG, ExchangeLogStatusTypeType.ISSUED
                    , TypeRefType.FA_RESPONSE, request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createRcvFluxFaResponseMessageRequest(extractPluginType(request)
                    , request.getRequest(), request.getUsername(), extractLogId(message, exchangeLogType), request.getFluxDataFlow()
                    , request.getSenderOrReceiver(), request.getOnValue());
            forwardToRules(msg);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            LOG.error("Couldn't map to RcvFLUXFaResponseMessageRequest when processing FLUXResponseMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            LOG.error("Couldn't log FLUXResponseMessage received from plugin into database", e);
        }
    }

    /*
     * Method for Observing the @MdrSyncMessageEvent, meaning a message from Activity MDR
     * module has arrived (synchronisation of the mdr).
     *
     */
    @Override
    public void sendResponseToRulesModule(TextMessage message) {           //and nothing to the exchange log?
        try {
            SetFLUXMDRSyncMessageExchangeResponse exchangeResponse = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXMDRSyncMessageExchangeResponse.class);
            LOG.debug("[INFO] Received @MdrSyncResponseMessageEvent. Going to send it to Rules now..");
            String strRequest = exchangeResponse.getRequest();
            SetFLUXMDRSyncMessageRulesResponse mdrResponse = new SetFLUXMDRSyncMessageRulesResponse();
            mdrResponse.setMethod(RulesModuleMethod.GET_FLUX_MDR_SYNC_RESPONSE);
            mdrResponse.setRequest(strRequest);
            String mdrStrReq = JAXBMarshaller.marshallJaxBObjectToString(mdrResponse);
            forwardToRules(mdrStrReq);
        } catch (Exception e) {
            LOG.error("[ERROR] Something strange happend during message conversion {} {}", message, e);         //if something happens, just log it and move on?????
        }
    }

    @Override
    public void getPluginListByTypes(TextMessage message) {
        try {
            GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListRequest.class);
            LOG.info("[INFO] Get plugin config LIST_SERVICE:{}", request.getType());
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            producer.sendModuleResponseMessage(message, ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
        } catch (ExchangeException | MessageException e) {
            LOG.error("[ Error when getting plugin list from source {}] {}", message, e);
            exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
        }
    }

    @Override
    public void processReceivedMovementBatch(TextMessage message) {
        try {
            // Log it.
            SetFLUXMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXMovementReportRequest.class);
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RECEIVE_MOVEMENT, ExchangeLogStatusTypeType.ISSUED, TypeRefType.MOVEMENT, request.getRequest(), true);

            // Send to rules.
            String onValue = request.getOnValue();
            String username = request.getUsername();
            String fluxDataFlow = request.getFluxDataFlow();
            String senderOrReceiver = request.getSenderOrReceiver();
            String registeredClassName = request.getRegisteredClassName();
            String ad = request.getAd();
            String to = request.getTo();
            String todt = request.getTodt();
            String msg = RulesModuleRequestMapper.createSetFLUXMovementReportRequest(extractPluginType(request), request.getRequest(),
                    username, extractLogId(message, exchangeLogType), fluxDataFlow, senderOrReceiver, onValue,
                    registeredClassName, ad, to, todt);
            forwardToRules(msg);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            LOG.error("Couldn't map to SetFLUXMovementReportRequest when processing FLUXMovementReport coming from movement-plugin!", e);
        } catch (ExchangeLogException e) {
            LOG.error("Couldn't log FLUXMovementReport received from plugin into database!", e);
        }
    }

    @Override
    public void processMovement(TextMessage message) {
        try {
            final TextMessage jmsMessage = message;
            SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetMovementReportRequest.class);
            if(request.getUsername() == null){
                LOG.error("[ Error when receiving message in exchange, username must be set in the request: ]");
                exchangeErrorEvent.fire(new ExchangeErrorEvent(message, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Username in the request must be set")));
                return;
            }
            LOG.debug("Processing Movement : {}", request.getRefGuid());
            String username;
            SetReportMovementType setRepMovType = request.getRequest();
            if (MovementSourceType.MANUAL.equals(setRepMovType.getMovement().getSource())) {// A person has created a position
                username = request.getUsername();
            } else {// A plugin has reported a position
                username = setRepMovType.getPluginType().name();
            }
            String pluginName = setRepMovType.getPluginName();
            PluginType pluginType = setRepMovType.getPluginType();
            if (validate(setRepMovType, pluginName, jmsMessage)) {
                MovementBaseType baseMovement = setRepMovType.getMovement();
                IncomingMovement incomingMovement = MovementMapper.mapMovementBaseTypeToRawMovementType(baseMovement);
                incomingMovement.setPluginType(pluginType.value());
//                incomingMovement.setPluginName(pluginName);
                incomingMovement.setDateReceived(setRepMovType.getTimestamp().toInstant());
                incomingMovement.setUpdatedBy(username);
                if (!baseMovement.getSource().equals(MovementSourceType.AIS)) {
                    LOG.debug("Logging received movement.");
                    ExchangeLogType createdLog = exchangeLog.log(request, LogType.RECEIVE_MOVEMENT, ExchangeLogStatusTypeType.ISSUED, TypeRefType.MOVEMENT,
                            message.getText(), true);
                    incomingMovement.setAckResponseMessageId(createdLog.getGuid());
                }
                String json = jsonb.toJson(incomingMovement);

                //combine all possible values into one big grouping string
                String groupId = incomingMovement.getAssetCFR() + incomingMovement.getAssetIMO() + incomingMovement.getAssetIRCS() + incomingMovement.getAssetMMSI() + incomingMovement.getAssetID() + incomingMovement.getAssetGuid() + incomingMovement.getMobileTerminalDNID() + incomingMovement.getMobileTerminalConnectId() + incomingMovement.getMobileTerminalGuid() + incomingMovement.getMobileTerminalLES() + incomingMovement.getMobileTerminalMemberNumber() + incomingMovement.getMobileTerminalSerialNumber() + "AllOtherThings";
                producer.sendMovementMessage(json, groupId);
                LOG.debug("Finished forwarding received movement to movement module.");
            } else {
                LOG.debug("Validation error. Event sent to plugin {}", message);
            }
        } catch (Exception e) {
            LOG.error("Could not process SetMovementReportRequest", e);
            throw new IllegalArgumentException("Could not process SetMovementReportRequest", e);
        } 
    }

    @Override
    public void receiveAssetInformation(TextMessage event) {
        try {
            ReceiveAssetInformationRequest request = JAXBMarshaller.unmarshallTextMessage(event, ReceiveAssetInformationRequest.class);
            String message = request.getAssets();
            forwardToAsset(message);
            exchangeLog.log(request, LogType.RECEIVE_ASSET_INFORMATION, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.ASSETS, message, true);
        } catch (ExchangeModelMarshallException e) {
            try {
                String errorMessage = "Couldn't map to ReceiveAssetInformationRequest when processing asset information from plugin. The event was " + event.getText();
                firePluginFault(event, errorMessage, e, null);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to ReceiveAssetInformationRequest when processing asset information from plugin.", e, null);
            }
        } catch (Exception e) {
            LOG.warn(e.toString(), e);

        }
    }

    @Override
    public void receiveSalesReport(TextMessage event) {
        try {
            ReceiveSalesReportRequest request = JAXBMarshaller.unmarshallTextMessage(event, ReceiveSalesReportRequest.class);
            LOG.debug("Receive sales report in Exchange module : {}", request.getReport());
            String report = request.getReport();
            PluginType plugin = request.getPluginType();
            String sender = request.getSenderOrReceiver();
            String messageGuid = request.getMessageGuid();
            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_REPORT, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_REPORT, report, true);

            String receiveSalesReportRequest = RulesModuleRequestMapper.createReceiveSalesReportRequest(report, messageGuid, plugin.name(), log.getGuid(), sender, request.getOnValue());
            String messageSelector = "ReceiveSalesReportRequest";
            forwardToRules(receiveSalesReportRequest, messageSelector);
        } catch (ExchangeModelMarshallException e) {
            try {
                firePluginFault(event, "Couldn't map to SetSalesReportRequest when processing sales report from plugin. The event was " + event.getText(), e, null);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to SetSalesReportRequest when processing sales report from plugin.", e, null);
            }
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales report.", e, null);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales report.", e, null);
        }
    }

    @Override
    public void receiveSalesQuery(TextMessage event) {
        try {
            ReceiveSalesQueryRequest request = JAXBMarshaller.unmarshallTextMessage(event, ReceiveSalesQueryRequest.class);
            LOG.info("Process sales query in Exchange module:{}", request);
            String query = request.getQuery();
            PluginType plugin = request.getPluginType();
            String sender = request.getSenderOrReceiver();
            String messageGuid = request.getMessageGuid();
            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_QUERY, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_QUERY, query, true);
            String receiveSalesQueryRequest = RulesModuleRequestMapper.createReceiveSalesQueryRequest(query, messageGuid, plugin.name(), log.getGuid(), sender, request.getOnValue());
            String messageSelector = "ReceiveSalesQueryRequest";
            forwardToRules(receiveSalesQueryRequest, messageSelector);
        } catch (ExchangeModelMarshallException e) {
            try {
                firePluginFault(event, "Couldn't map to SalesQueryRequest when processing sales query from plugin. The message was " + event.getText(), e, null);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to SalesQueryRequest when processing sales query from plugin.", e, null);
            }
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales query.", e, null);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales query.", e, null);
        }
    }

    @Override
    public void receiveSalesResponse(TextMessage event) {
        try {
            ReceiveSalesResponseRequest request = JAXBMarshaller.unmarshallTextMessage(event, ReceiveSalesResponseRequest.class);
            String response = request.getResponse();
            ExchangeLogType log = exchangeLog.log(request, LogType.RECEIVE_SALES_RESPONSE, ExchangeLogStatusTypeType.ISSUED, TypeRefType.SALES_RESPONSE, response, true);
            String receiveSalesResponseRequest = RulesModuleRequestMapper.createReceiveSalesResponseRequest(response, log.getGuid(), request.getSenderOrReceiver());
            String messageSelector = "ReceiveSalesResponseRequest";
            forwardToRules(receiveSalesResponseRequest, messageSelector);
        } catch (ExchangeModelMarshallException e) {
            firePluginFault(event, "Error when receiving a Sales response from FLUX", e, null);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales response.", e, null);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales response.", e, null);
        }
    }


    @Override
    public void receiveInvalidSalesMessage(TextMessage event) {
        try {
            ReceiveInvalidSalesMessage request = JAXBMarshaller.unmarshallTextMessage(event, ReceiveInvalidSalesMessage.class);
            exchangeLog.log(request, LogType.RECEIVE_SALES_REPORT, ExchangeLogStatusTypeType.FAILED, TypeRefType.SALES_REPORT, request.getOriginalMessage(), true);
            producer.sendMessageOnQueue(request.getRespondToInvalidMessageRequest(), MessageQueue.SALES);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming invalid sales message", e, null);
        } catch (ExchangeMessageException | ExchangeModelMarshallException e) {
            firePluginFault(event, "Error when receiving an invalid sales message from FLUX", e, null);
        }
    }

    @Override
    public void logRefIdByTypeExists(TextMessage event) {      //this one has the wierd behavour that it both returns the correct answer AND puts the initial message in DLQ for causing an exception AT THE SAME TIME if the input is an empty list..........
        try {
            LogRefIdByTypeExistsRequest request = unMarshallMessage(event.getText(), LogRefIdByTypeExistsRequest.class);
            UUID refGuid = UUID.fromString(request.getRefGuid());
            List<TypeRefType> refTypes = request.getRefTypes();
            List<ExchangeLogStatusType> exchangeStatusHistoryList = exchangeLogModel.getExchangeLogsStatusHistories(refGuid, refTypes);
            LogRefIdByTypeExistsResponse response = new LogRefIdByTypeExistsResponse();
            if (CollectionUtils.isNotEmpty(exchangeStatusHistoryList)) {
                response.setRefGuid(exchangeStatusHistoryList.get(0).getTypeRef().getRefGuid());
            }
            String responseAsString = JAXBUtils.marshallJaxBObjectToString(response);
            producer.sendModuleResponseMessage(event, responseAsString);
        } catch (ExchangeModelException | MessageException | JAXBException | JMSException e) {
            fireExchangeFault(event, "Could not un-marshall " + LogRefIdByTypeExistsRequest.class, e);
        }
    }

    @Override
    public void logIdByTypeExists(TextMessage event) {
        try {
            LogIdByTypeExistsRequest request = unMarshallMessage(event.getText(), LogIdByTypeExistsRequest.class);
            UUID messageGuid = UUID.fromString(request.getMessageGuid());
            TypeRefType refType = request.getRefType();
            ExchangeLogType exchangeLogByGuid = exchangeLogModel.getExchangeLogByGuidAndType(messageGuid, refType);
            LogIdByTypeExistsResponse response = new LogIdByTypeExistsResponse();
            if (exchangeLogByGuid != null) {
                response.setMessageGuid(exchangeLogByGuid.getGuid());
            }
            String responseAsString = JAXBUtils.marshallJaxBObjectToString(response);
            producer.sendModuleResponseMessage(event, responseAsString);

        } catch (ExchangeModelException | MessageException | JAXBException | JMSException e) {
            fireExchangeFault(event, "Could not un-marshall " + LogRefIdByTypeExistsRequest.class, e);
        }

    }


    @Override
    public void queryAssetInformation(TextMessage event) {
        try {
            QueryAssetInformationRequest incomingRequest = JAXBMarshaller.unmarshallTextMessage(event, QueryAssetInformationRequest.class);
            String message = incomingRequest.getAssets();
            String destination = incomingRequest.getDestination();
            String senderOrReceiver = incomingRequest.getSenderOrReceiver();

            eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendQueryAssetInformationRequest outgoingRequest = new eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendQueryAssetInformationRequest();
            outgoingRequest.setQuery(message);
            outgoingRequest.setDestination(destination);
            outgoingRequest.setSenderOrReceiver(senderOrReceiver);
            outgoingRequest.setMethod(ExchangePluginMethod.SEND_VESSEL_QUERY);

            exchangeEventOutgoingService.sendAssetInformationToFLUX(outgoingRequest);
            exchangeLog.log(incomingRequest, LogType.QUERY_ASSET_INFORMATION, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.ASSETS, message, false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException e) {
            fireExchangeFault(event, "Error when sending asset information query to FLUX", e);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the outgoing asset information query.", e, null);
        }
    }


    @Override
    public void ping(TextMessage message) {
        try {
            PingResponse response = new PingResponse();
            response.setResponse("pong");
            producer.sendModuleResponseMessage(message, JAXBMarshaller.marshallJaxBObjectToString(response));
        } catch (ExchangeModelMarshallException | MessageException e) {
            LOG.error("[ Error when marshalling ping response ]");
        }
    }

    @Override
    public void processPluginPing(TextMessage message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse response = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse.class);
            //TODO handle ping response from plugin, eg. no serviceClassName in response
            LOG.info("FIX ME handle ping response from plugin");
        } catch (ExchangeModelMarshallException e) {
            LOG.error("Couldn't process ping response from plugin {} {} ", message, e.getMessage());
        }
    }

    @Override
    public void processAcknowledge(TextMessage message) {
        try {
            AcknowledgeResponse response = JAXBMarshaller.unmarshallTextMessage(message, AcknowledgeResponse.class);
            AcknowledgeType acknowledge = response.getResponse();
            String serviceClassName = response.getServiceClassName();
            ExchangePluginMethod method = response.getMethod();
            LOG.info("[INFO] Process acknowledge : {}", method);
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
            LOG.error("Process acknowledge couldn't be marshalled {} {}", message, e);
            throw new IllegalStateException("Could not process acknowledge", e);
        } catch (ExchangeServiceException e) {
            //TODO Audit.log() couldn't process acknowledge in exchange service
            LOG.error("Couldn't process acknowledge in exchange service:{} {} ", message, e);
            throw new IllegalStateException("Could not process acknowledge", e);
        }
    }

    private void forwardToRules(String messageToForward) {
        forwardToRules(messageToForward, null);
    }


    /**
     * forwards serialized message to Rules module
     *
     * @param messageToForward
     */
    private void forwardToRules(String messageToForward, String messageSelector) {
        try {
            LOG.info("[INFO] Forwarding the msg to rules Module.");
            producer.sendRulesMessage(messageToForward, messageSelector);

        } catch (ExchangeMessageException e) {
            LOG.error("[ERROR] Failed to forward message to Rules: {} {}", messageToForward, e);
        }
    }

    /**
     * forwards serialized message to Asset module
     *
     * @param messageToForward
     */
    private void forwardToAsset(String messageToForward) {
        try {
            LOG.info("Forwarding the message to Asset.");
            String s = producer.forwardToAsset(messageToForward, "ASSET_INFORMATION");
        } catch (ExchangeMessageException e) {
            LOG.error("Failed to forward message to Asset: {} {}", messageToForward, e);
        }
    }


    private void firePluginFault(TextMessage messageEvent, String errorMessage, Throwable exception, String serviceClassName) {
        try {
            LOG.error(errorMessage, exception);
            ServiceResponseType service = ((serviceClassName == null) ? null : exchangeService.getService(serviceClassName));
            PluginFault fault = ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_PLUGIN_EVENT.getCode(), errorMessage);
            pluginErrorEvent.fire(new PluginErrorEvent(messageEvent, service, fault));
        } catch (ExchangeServiceException e) {
            LOG.error("Unable to send PluginError message due to: {}", e);
        }
    }

    private void fireExchangeFault(TextMessage messageEvent, String errorMessage, Throwable exception) {
        LOG.error(errorMessage, exception);
        eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault exchangeFault = ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, errorMessage);
        exchangeErrorEvent.fire(new ExchangeErrorEvent(messageEvent, exchangeFault));
    }


    private boolean validate(SetReportMovementType setReport, String service, TextMessage origin) {
        if (setReport == null) {
            String faultMessage = "No setReport request";
            firePluginFault(origin, faultMessage, new RuntimeException(), service);
            return false;
        } else if (setReport.getMovement() == null) {
            String faultMessage = "No movement in setReport request";
            firePluginFault(origin, faultMessage, new RuntimeException(), service);
            return false;
        } else if (setReport.getPluginType() == null) {
            String faultMessage = "No pluginType in setReport request";
            firePluginFault(origin, faultMessage, new RuntimeException(), service);
            return false;
        } else if (setReport.getPluginName() == null || setReport.getPluginName().isEmpty()) {
            String faultMessage = "No pluginName in setReport request";
            firePluginFault(origin, faultMessage, new RuntimeException(), service);
            return false;
        } else if (setReport.getTimestamp() == null) {
            String faultMessage = "No timestamp in setReport request";
            firePluginFault(origin, faultMessage, new RuntimeException(), service);
            return false;
        }
        return true;
    }

    private void handleUpdateExchangeLogAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {

        ExchangeLogStatusTypeType logStatus = ExchangeLogStatusTypeType.FAILED;
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.OK) {//TODO if(poll probably transmitted)
            logStatus = ExchangeLogStatusTypeType.SUCCESSFUL;
            removeUnsentMessage(serviceClassName, ack);

        } else if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {
            LOG.debug(method + " was NOK: " + ack.getMessage());

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
        ExchangeLogStatusTypeType exchangeLogStatus = ack.getPollStatus().getStatus();
        if (exchangeLogStatus.equals(ExchangeLogStatusTypeType.SUCCESSFUL) ||
                exchangeLogStatus.equals(ExchangeLogStatusTypeType.FAILED)) {
            removeUnsentMessage(serviceClassName, ack);
        }
        try {
            PollStatus updatedLog = exchangeLog.setPollStatus(ack.getMessageId(), UUID.fromString(ack.getPollStatus().getPollId()), exchangeLogStatus, serviceClassName);
            // Long polling
            pollEvent.fire(new NotificationMessage("guid", updatedLog.getPollGuid()));
        } catch (ExchangeLogException e) {
            LOG.error(e.getMessage());
        }
    }

    private void removeUnsentMessage(String serviceClassName, AcknowledgeType ack) {
        try {
            exchangeLog.removeUnsentMessage(ack.getUnsentMessageGuid(), serviceClassName);
        } catch (ExchangeLogException ex) {
            LOG.error(ex.getMessage());
        }
    }

    private void handleUpdateServiceAcknowledge(String serviceClassName, AcknowledgeType ack, StatusType status) throws ExchangeServiceException {
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.OK) {
            exchangeService.updateServiceStatus(serviceClassName, status, serviceClassName);

        } else if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {//TODO Audit.log()
            LOG.error("Couldn't start service " + serviceClassName);

        }
    }

    private void handleAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {
        LOG.debug(method + " was acknowledged in " + serviceClassName);
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {//TODO Audit.log()
            LOG.error(serviceClassName + " didn't like it. " + ack.getMessage());
        }
    }

    private NotificationMessage createNotificationMessage(String serviceClassName, boolean started) {
        NotificationMessage msg = new NotificationMessage("serviceClassName", serviceClassName);
        msg.setProperty("started", started);
        return msg;
    }

    private TypeRefType extractFaType(ExchangeModuleMethod method) {
        TypeRefType faType = null;
        switch (method){
            case SET_FLUX_FA_REPORT_MESSAGE:
                faType = TypeRefType.FA_REPORT;
                break;
            case UNKNOWN:
                faType = TypeRefType.UNKNOWN;
                break;
            default:
                LOG.error("[FATAL] FA Type could not be determined!!");
        }
        return faType;
    }

    private String extractLogId(TextMessage message, ExchangeLogType exchangeLogType) {
        String logId = null;
        if (exchangeLogType == null) {
            LOG.error("ExchangeLogType received is NULL while trying to save {}", message);
        } else {
            logId = exchangeLogType.getGuid();
            LOG.info("Logged to Exchange message with following GUID :" + logId);
        }
        return logId;
    }


    private eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType extractPluginType(ExchangeBaseRequest request) {
        eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType rulesPluginType;
        switch (request.getPluginType()) {
            case MANUAL:
                rulesPluginType = eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.MANUAL;
                break;
            case BELGIAN_ACTIVITY:
                rulesPluginType = eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.BELGIAN_ACTIVITY;
                break;
            default:
                rulesPluginType = eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.FLUX;
                break;
        }
        return rulesPluginType;
    }

}
