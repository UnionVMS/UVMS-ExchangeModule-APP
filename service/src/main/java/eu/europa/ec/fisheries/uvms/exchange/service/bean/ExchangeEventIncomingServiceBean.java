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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.LogIdByTypeExistsRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.LogIdByTypeExistsResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.LogRefIdByTypeExistsRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.LogRefIdByTypeExistsResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.QueryAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.RcvFLUXFaResponseMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveInvalidSalesMessage;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesQueryRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFAQueryMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageExchangeResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.AcknowledgeResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.movement.module.v1.ProcessedMovementAck;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.schema.rules.module.v1.RulesModuleMethod;
import eu.europa.ec.fisheries.schema.rules.module.v1.SetFLUXMDRSyncMessageRulesResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.exchange.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.LogIdByTypeExists;
import eu.europa.ec.fisheries.uvms.exchange.message.event.LogRefIdByTypeExists;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MdrSyncResponseMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginPingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.QueryAssetInformationEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceiveAssetInformationEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceiveInvalidSalesMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceiveSalesQueryEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceiveSalesReportEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceiveSalesResponseEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceivedFluxFaResponseMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ReceivedMovementBatchEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetFaQueryMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetFluxFAReportMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
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
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.PluginTypeMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.MovementModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.movementrules.model.exception.MovementRulesModelMapperException;
import eu.europa.ec.fisheries.uvms.movementrules.model.mapper.MovementRulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import lombok.extern.slf4j.Slf4j;

@Stateless
@Slf4j
public class ExchangeEventIncomingServiceBean implements ExchangeEventIncomingService {

    @Inject
    @ErrorEvent
    private Event<ExchangeMessageEvent> exchangeErrorEvent;

    @Inject
    @PluginErrorEvent
    private Event<PluginMessageEvent> pluginErrorEvent;

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

    @Override
    public void processFLUXFAReportMessage(@Observes @SetFluxFAReportMessageEvent ExchangeMessageEvent message) {
        try {
            SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            String onValue = request.getOnValue();
            String username = request.getUsername();
            String fluxDataFlow = request.getFluxDataFlow();
            String senderOrReceiver = request.getSenderOrReceiver();
            log.debug("Got FLUXFAReportMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RCV_FLUX_FA_REPORT_MSG, ExchangeLogStatusTypeType.ISSUED
                    , extractFaType(request.getMethod()), request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createSetFLUXFAReportMessageRequest(extractPluginType(request), request.getRequest()
                    , username, extractLogId(message, exchangeLogType), fluxDataFlow, senderOrReceiver, onValue);
            forwardToRules(msg, message, null);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            log.error("Couldn't map to SetFLUXFAReportMessageRequest when processing FLUXFAReportMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            log.error("Couldn't log FAReportMessage received from plugin into database", e);
        }
    }

    @Override
    public void processFAQueryMessage(@Observes @SetFaQueryMessageEvent ExchangeMessageEvent message) {
        try {
            SetFAQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFAQueryMessageRequest.class);
            log.debug("Got FAQueryMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RECEIVE_FA_QUERY_MSG, ExchangeLogStatusTypeType.ISSUED
                    , TypeRefType.FA_QUERY, request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createSetFaQueryMessageRequest(extractPluginType(request), request.getRequest(),
                    request.getUsername(), extractLogId(message, exchangeLogType), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getOnValue());
            forwardToRules(msg, message, null);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            log.error("Couldn't map to SetFAQueryMessageRequest when processing FAQueryMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            log.error("Couldn't log FAQueryMessage received from plugin into database", e);
        }
    }

    @Override
    public void processFluxFAResponseMessage(@Observes @ReceivedFluxFaResponseMessageEvent ExchangeMessageEvent message) {
        try {
            RcvFLUXFaResponseMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), RcvFLUXFaResponseMessageRequest.class);
            log.debug("Got FLUXResponseMessage in exchange :" + request.getRequest());
            ExchangeLogType exchangeLogType = exchangeLog.log(request, LogType.RECEIVE_FLUX_RESPONSE_MSG, ExchangeLogStatusTypeType.ISSUED
                    , TypeRefType.FA_RESPONSE, request.getRequest(), true);
            String msg = RulesModuleRequestMapper.createRcvFluxFaResponseMessageRequest(extractPluginType(request)
                    , request.getRequest(), request.getUsername(), extractLogId(message, exchangeLogType), request.getFluxDataFlow()
                    , request.getSenderOrReceiver(), request.getOnValue());
            forwardToRules(msg, message, null);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            log.error("Couldn't map to RcvFLUXFaResponseMessageRequest when processing FLUXResponseMessage coming from fa-plugin!", e);
        } catch (ExchangeLogException e) {
            log.error("Couldn't log FLUXResponseMessage received from plugin into database", e);
        }
    }

    /*
     * Method for Observing the @MdrSyncMessageEvent, meaning a message from Activity MDR
     * module has arrived (synchronisation of the mdr).
     *
     */
    @Override
    public void sendResponseToRulesModule(@Observes @MdrSyncResponseMessageEvent ExchangeMessageEvent message) {
        TextMessage requestMessage = message.getJmsMessage();
        try {
            SetFLUXMDRSyncMessageExchangeResponse exchangeResponse = JAXBMarshaller.unmarshallTextMessage(requestMessage, SetFLUXMDRSyncMessageExchangeResponse.class);
            log.debug("[INFO] Received @MdrSyncResponseMessageEvent. Going to send it to Rules now..");
            String strRequest = exchangeResponse.getRequest();
            SetFLUXMDRSyncMessageRulesResponse mdrResponse = new SetFLUXMDRSyncMessageRulesResponse();
            mdrResponse.setMethod(RulesModuleMethod.GET_FLUX_MDR_SYNC_RESPONSE);
            mdrResponse.setRequest(strRequest);
            String mdrStrReq = JAXBMarshaller.marshallJaxBObjectToString(mdrResponse);
            forwardToRules(mdrStrReq, null, null);
        } catch (Exception e) {
            log.error("[ERROR] Something strange happend during message conversion {} {}", message, e);
        }
    }

    @Override
    public void getPluginListByTypes(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        try {
            TextMessage jmsMessage = message.getJmsMessage();
            GetServiceListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, GetServiceListRequest.class);
            log.info("[INFO] Get plugin config LIST_SERVICE:{}", request.getType());
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
        } catch (ExchangeException | MessageException e) {
            log.error("[ Error when getting plugin list from source {}] {}", message, e);
            exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(
                    FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
        }
    }

    @Override
    public void processReceivedMovementBatch(@Observes @ReceivedMovementBatchEvent ExchangeMessageEvent message) {
        try {
            // Log it.
            SetFLUXMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXMovementReportRequest.class);
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
            forwardToRules(msg, message, null);
        } catch (RulesModelMapperException | ExchangeModelMarshallException e) {
            log.error("Couldn't map to SetFLUXMovementReportRequest when processing FLUXMovementReport coming from movement-plugin!", e);
        } catch (ExchangeLogException e) {
            log.error("Couldn't log FLUXMovementReport received from plugin into database!", e);
        }
    }

    @Override
    public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
        try {
            final TextMessage jmsMessage = message.getJmsMessage();
            final String jmsMessageID = jmsMessage.getJMSMessageID();
            SetMovementReportRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, SetMovementReportRequest.class);
            log.info("[INFO] Processing Movement : {}", request.getRefGuid());
            String username;
            SetReportMovementType setRepMovType = request.getRequest();
            if (MovementSourceType.MANUAL.equals(setRepMovType.getMovement().getSource())) {// A person has created a position
                username = request.getUsername();
                // Send some response to Movement, if it originated from there (manual movement)
                ProcessedMovementAck response = MovementModuleResponseMapper.mapProcessedMovementAck(eu.europa.ec.fisheries.schema.movement.common.v1.AcknowledgeTypeType.OK,
                        jmsMessageID, "Movement successfully processed");
                producer.sendModuleAckMessage(jmsMessageID, MessageQueue.MOVEMENT_RESPONSE, JAXBMarshaller.marshallJaxBObjectToString(response));
            } else {// A plugin has reported a position
                username = setRepMovType.getPluginType().name();
            }
            String pluginName = setRepMovType.getPluginName();
            ServiceResponseType service = exchangeService.getService(pluginName);
            PluginType pluginType = setRepMovType.getPluginType();
            if (validate(setRepMovType, service, jmsMessage)) {
                MovementBaseType baseMovement = setRepMovType.getMovement();
                IncomingMovement incomingMovement = MovementMapper.mapMovementBaseTypeToRawMovementType(baseMovement);
                incomingMovement.setPluginType(pluginType.value());
//                incomingMovement.setPluginName(pluginName);
                incomingMovement.setDateReceived(setRepMovType.getTimestamp());
                incomingMovement.setUpdatedBy(username);
                // TODO : Temporary - probably better to change corr id to have the same though the entire flow;
                // TODO : then we can use this to send response to original caller from anywhere needed
                incomingMovement.setAckResponseMessageId(jmsMessageID);
                log.info("[INFO] Logging received movement.");
                exchangeLog.log(request, LogType.RECEIVE_MOVEMENT, ExchangeLogStatusTypeType.ISSUED, TypeRefType.MOVEMENT,
                        JAXBMarshaller.marshallJaxBObjectToString(request), true);
                String json = new ObjectMapper().writeValueAsString(incomingMovement);
                // TODO find a better group id
                producer.sendMovementMessage(json, incomingMovement.getAssetCFR());
                log.info("[INFO] Finished forwarding received movement to rules module.");
            } else {
                log.debug("[ERROR] Validation error. Event sent to plugin {}", message);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not process SetMovementReportRequest", e);
        } 
    }

    @Override
    public void receiveAssetInformation(@Observes @ReceiveAssetInformationEvent ExchangeMessageEvent event) {
        try {
            ReceiveAssetInformationRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveAssetInformationRequest.class);
            String message = request.getAssets();
            forwardToAsset(message);
            exchangeLog.log(request, LogType.RECEIVE_ASSET_INFORMATION, ExchangeLogStatusTypeType.SUCCESSFUL, TypeRefType.ASSETS, message, true);
        } catch (ExchangeModelMarshallException e) {
            try {
                String errorMessage = "Couldn't map to ReceiveAssetInformationRequest when processing asset information from plugin. The event was " + event.getJmsMessage().getText();
                firePluginFault(event, errorMessage, e);
            } catch (JMSException e1) {
                firePluginFault(event, "Couldn't map to ReceiveAssetInformationRequest when processing asset information from plugin.", e);
            }
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming asset information.", e);
        }
    }

    @Override
    public void receiveSalesReport(@Observes @ReceiveSalesReportEvent ExchangeMessageEvent event) {
        try {
            ReceiveSalesReportRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveSalesReportRequest.class);
            log.debug("Receive sales report in Exchange module : {}", request.getReport());
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
                firePluginFault(event, "Couldn't map to SetSalesReportRequest when processing sales report from plugin. The event was " + event.getJmsMessage().getText(), e);
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
        try {
            ReceiveSalesQueryRequest request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveSalesQueryRequest.class);
            log.info("Process sales query in Exchange module:{}", request);
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
            String receiveSalesResponseRequest = RulesModuleRequestMapper.createReceiveSalesResponseRequest(response, log.getGuid(), request.getSenderOrReceiver());
            String messageSelector = "ReceiveSalesResponseRequest";
            forwardToRules(receiveSalesResponseRequest, messageSelector);
        } catch (ExchangeModelMarshallException e) {
            firePluginFault(event, "Error when receiving a Sales response from FLUX", e);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming sales response.", e);
        } catch (RulesModelMarshallException e) {
            firePluginFault(event, "Could not create a request for the Rules module for an incoming sales response.", e);
        }
    }


    @Override
    public void receiveInvalidSalesMessage(@Observes @ReceiveInvalidSalesMessageEvent ExchangeMessageEvent event) {
        try {
            ReceiveInvalidSalesMessage request = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), ReceiveInvalidSalesMessage.class);
            exchangeLog.log(request, LogType.RECEIVE_SALES_REPORT, ExchangeLogStatusTypeType.FAILED, TypeRefType.SALES_REPORT, request.getOriginalMessage(), true);
            producer.sendMessageOnQueue(request.getRespondToInvalidMessageRequest(), MessageQueue.SALES);
        } catch (ExchangeLogException e) {
            firePluginFault(event, "Could not log the incoming invalid sales message", e);
        } catch (ExchangeMessageException | ExchangeModelMarshallException e) {
            firePluginFault(event, "Error when receiving an invalid sales message from FLUX", e);
        }
    }

    @Override
    public void logRefIdByTypeExists(@Observes @LogRefIdByTypeExists ExchangeMessageEvent event) {
        try {
            LogRefIdByTypeExistsRequest request = unMarshallMessage(event.getJmsMessage().getText(), LogRefIdByTypeExistsRequest.class);
            String refGuid = request.getRefGuid();
            List<TypeRefType> refTypes = request.getRefTypes();
            List<ExchangeLogStatusType> exchangeStatusHistoryList = exchangeLogModel.getExchangeLogsStatusHistories(refGuid, refTypes);
            LogRefIdByTypeExistsResponse response = new LogRefIdByTypeExistsResponse();
            if (CollectionUtils.isNotEmpty(exchangeStatusHistoryList)) {
                response.setRefGuid(exchangeStatusHistoryList.get(0).getTypeRef().getRefGuid());
            }
            String responseAsString = JAXBUtils.marshallJaxBObjectToString(response);
            producer.sendModuleResponseMessage(event.getJmsMessage(), responseAsString);
        } catch (ExchangeModelException | MessageException | JAXBException | JMSException e) {
            fireExchangeFault(event, "Could not un-marshall " + LogRefIdByTypeExistsRequest.class, e);
        }
    }

    @Override
    public void logIdByTypeExists(@Observes @LogIdByTypeExists ExchangeMessageEvent event) {
        try {
            LogIdByTypeExistsRequest request = unMarshallMessage(event.getJmsMessage().getText(), LogIdByTypeExistsRequest.class);
            String messageGuid = request.getMessageGuid();
            TypeRefType refType = request.getRefType();
            ExchangeLogType exchangeLogByGuid = exchangeLogModel.getExchangeLogByGuidAndType(messageGuid, refType);
            LogIdByTypeExistsResponse response = new LogIdByTypeExistsResponse();
            if (exchangeLogByGuid != null) {
                response.setMessageGuid(exchangeLogByGuid.getGuid());
            }
            String responseAsString = JAXBUtils.marshallJaxBObjectToString(response);
            producer.sendModuleResponseMessage(event.getJmsMessage(), responseAsString);

        } catch (ExchangeModelException | MessageException | JAXBException | JMSException e) {
            fireExchangeFault(event, "Could not un-marshall " + LogRefIdByTypeExistsRequest.class, e);
        }

    }


    @Override
    public void queryAssetInformation(@Observes @QueryAssetInformationEvent ExchangeMessageEvent event) {
        try {
            QueryAssetInformationRequest incomingRequest = JAXBMarshaller.unmarshallTextMessage(event.getJmsMessage(), QueryAssetInformationRequest.class);
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
            firePluginFault(event, "Could not log the outgoing asset information query.", e);
        }
    }


    @Override
    public void ping(@Observes @PingEvent ExchangeMessageEvent message) {
        try {
            PingResponse response = new PingResponse();
            response.setResponse("pong");
            producer.sendModuleResponseMessage(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(response));
        } catch (ExchangeModelMarshallException | MessageException e) {
            log.error("[ Error when marshalling ping response ]");
        }
    }

    @Override
    public void processPluginPing(@Observes @PluginPingEvent ExchangeMessageEvent message) {
        try {
            eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse response = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse.class);
            //TODO handle ping response from plugin, eg. no serviceClassName in response
            log.info("FIX ME handle ping response from plugin");
        } catch (ExchangeModelMarshallException e) {
            log.error("Couldn't process ping response from plugin {} {} ", message, e.getMessage());
        }
    }

    @Override
    public void processAcknowledge(@Observes @ExchangeLogEvent ExchangeMessageEvent message) {
        try {
            AcknowledgeResponse response = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), AcknowledgeResponse.class);
            AcknowledgeType acknowledge = response.getResponse();
            String serviceClassName = response.getServiceClassName();
            ExchangePluginMethod method = response.getMethod();
            log.info("[INFO] Process acknowledge : {}", method);
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
            log.error("Process acknowledge couldn't be marshalled {} {}", message, e);
        } catch (ExchangeServiceException e) {
            //TODO Audit.log() couldn't process acknowledge in exchange service
            log.error("Couldn't process acknowledge in exchange service:{} {} ", message, e.getMessage());
        }
    }

    private void forwardToRules(String messageToForward, ExchangeMessageEvent exchangeMessageEvent, ServiceResponseType service) {
        forwardToRules(messageToForward, exchangeMessageEvent, service, null);
    }

    private void forwardToRules(String messageToForward, String messageSelector) {
        forwardToRules(messageToForward, null, null, messageSelector);
    }

    /**
     * forwards serialized message to Rules module
     *
     * @param messageToForward
     * @param exchangeMessageEvent is optional
     * @param service              is optional
     */
    private void forwardToRules(String messageToForward, ExchangeMessageEvent exchangeMessageEvent, ServiceResponseType service, String messageSelector) {
        try {
            log.info("[INFO] Forwarding the msg to rules Module.");
            producer.sendRulesMessage(messageToForward, messageSelector);

        } catch (ExchangeMessageException e) {
            log.error("[ERROR] Failed to forward message to Rules: {} {}", messageToForward, e);
        }
    }

    /**
     * forwards serialized message to Asset module
     *
     * @param messageToForward
     */
    private void forwardToAsset(String messageToForward) {
        try {
            log.info("Forwarding the message to Asset.");
            producer.sendMessageOnQueue(messageToForward, MessageQueue.VESSEL);
        } catch (ExchangeMessageException e) {
            log.error("Failed to forward message to Asset: {} {}", messageToForward, e);
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

    private void handleUpdateExchangeLogAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {

        ExchangeLogStatusTypeType logStatus = ExchangeLogStatusTypeType.FAILED;
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.OK) {//TODO if(poll probably transmitted)
            logStatus = ExchangeLogStatusTypeType.SUCCESSFUL;
            try {
                exchangeLog.removeUnsentMessage(ack.getUnsentMessageGuid(), serviceClassName);
            } catch (ExchangeLogException ex) {
                log.error(ex.getMessage());
            }

        } else if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {
            log.debug(method + " was NOK: " + ack.getMessage());

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
            log.error(e.getMessage());
        }
    }

    private void handleSetPollStatusAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {
        log.debug(method + " was acknowledged in " + serviceClassName);
        try {
            PollStatus updatedLog = exchangeLog.setPollStatus(ack.getMessageId(), ack.getPollStatus().getPollId(), ack.getPollStatus().getStatus(), serviceClassName);
            // Long polling
            pollEvent.fire(new NotificationMessage("guid", updatedLog.getPollGuid()));
        } catch (ExchangeLogException e) {
            log.error(e.getMessage());
        }
    }

    private void handleUpdateServiceAcknowledge(String serviceClassName, AcknowledgeType ack, StatusType status) throws ExchangeServiceException {
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.OK) {
            exchangeService.updateServiceStatus(serviceClassName, status, serviceClassName);

        } else if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {//TODO Audit.log()
            log.error("Couldn't start service " + serviceClassName);

        }
    }

    private void handleAcknowledge(ExchangePluginMethod method, String serviceClassName, AcknowledgeType ack) {
        log.debug(method + " was acknowledged in " + serviceClassName);
        if (ack.getType() == eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType.NOK) {//TODO Audit.log()
            log.error(serviceClassName + " didn't like it. " + ack.getMessage());
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
                log.error("[FATAL] FA Type could not be determined!!");
        }
        return faType;
    }

    private String extractLogId(ExchangeMessageEvent message, ExchangeLogType exchangeLogType) {
        String logId = null;
        if (exchangeLogType == null) {
            log.error("ExchangeLogType received is NULL while trying to save {}", message);
        } else {
            logId = exchangeLogType.getGuid();
            log.info("Logged to Exchange message with following GUID :" + logId);
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
