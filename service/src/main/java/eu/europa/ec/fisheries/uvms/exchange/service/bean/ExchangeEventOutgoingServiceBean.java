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

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFAQueryMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAResponseMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
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
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.MdrSyncRequestMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendFLUXFAResponseToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendFaQueryToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendFaReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeEventOutgoingService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeToMdrRulesMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeEventOutgoingServiceBean implements ExchangeEventOutgoingService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventOutgoingServiceBean.class);

    @Inject
    @ErrorEvent
    private Event<ExchangeMessageEvent> exchangeErrorEvent;

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ExchangeConsumer consumer;

    @EJB
    private ExchangeLogService exchangeLog;

    @EJB
    private ExchangeService exchangeService;

    @EJB
    private ExchangeAssetService exchangeAssetService;

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
    public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message) {

        try {
            SendMovementToPluginRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SendMovementToPluginRequest.class);
            LOG.info("Send report to plugin: {}",request);
            SendMovementToPluginType sendReport = request.getReport();

            List<PluginType> type = new ArrayList<>();
            type.add(sendReport.getPluginType());

            List<ServiceResponseType> services = exchangeService.getServiceList(type);
            if (services == null || services.isEmpty()) {
                String faultMessage = "No plugins of type " + sendReport.getPluginType() + " found";
                LOG.debug(faultMessage);

            } else {
                ServiceResponseType service = services.get(0);
                String serviceName = service.getServiceClassName();

                if (validate(service, sendReport, message.getJmsMessage(), request.getUsername())) {
                    List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                    String unsentMessageGuid = exchangeLog.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), message.getJmsMessage().getText(), unsentMessageProperties, request.getUsername());

                    String text = ExchangePluginRequestMapper.createSetReportRequest(sendReport.getTimestamp(), sendReport, unsentMessageGuid);
                    String pluginMessageId = producer.sendEventBusMessage(text, serviceName);

                    //System.out.println("SendReport: PluginMessageId: " + pluginMessageId);
                    try {
                        ExchangeLogType log = ExchangeLogMapper.getSendMovementExchangeLog(sendReport);
                        exchangeLog.logAndCache(log, pluginMessageId, request.getUsername());
                    } catch (ExchangeLogException e) {
                        LOG.error(e.getMessage());
                    }

                    AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeOK();
                    String moduleResponse = ExchangeModuleResponseMapper.mapSendMovementToPluginResponse(ackType);
                    producer.sendModuleResponseMessage(message.getJmsMessage(), moduleResponse);

                } else {
                    LOG.debug("Cannot send to plugin. Response sent to caller:{}",message);
                }
            }
        } catch (ExchangeException e) {
            LOG.error("[ Error when sending report to plugin {} ] {}",message,e);

        } catch (JMSException ex) {
            LOG.error("[ Error when creating unsent movement {}] {}",message,ex);
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
            LOG.info("[INFO] Received MdrSyncMessageEvent. Going to send to the Plugin now..");
            String marshalledReq = ExchangeToMdrRulesMapper.mapExchangeToMdrPluginRequest(requestMessage);
            producer.sendEventBusMessage(marshalledReq, ExchangeServiceConstants.MDR_PLUGIN_SERVICE_NAME);
        } catch (Exception e) {
            LOG.error("[ERROR] Something strange happend during message conversion {} {}",message,e);
        }
    }


    private boolean validate(ServiceResponseType service, SendMovementToPluginType sendReport, TextMessage origin, String username) {
        String serviceName = service.getServiceClassName(); //Use first and only
        if (serviceName == null || serviceName.isEmpty()) {
            String faultMessage = "First plugin of type " + sendReport.getPluginType() + " is invalid. Missing serviceClassName";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_PLUGIN_INVALID, faultMessage)));
            try {
                List<UnsentMessageTypeProperty> unsentMessageProperties = ExchangeLogMapper.getUnsentMessageProperties(sendReport);
                exchangeLog.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), origin.getText(), unsentMessageProperties, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsent message " + e.getMessage());
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
                exchangeLog.createUnsentMessage(sendReport.getRecipient(), sendReport.getTimestamp(), ExchangeLogMapper.getSendMovementSenderReceiver(sendReport), origin.getText(), unsentMessageProperties, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsent message " + e.getMessage());
            }

            try {
                AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeNOK(origin.getJMSMessageID(), "Plugin to send movement is not started");
                String moduleResponse = ExchangeModuleResponseMapper.mapSendMovementToPluginResponse(ackType);
                producer.sendModuleResponseMessage(origin, moduleResponse);
            } catch (JMSException | ExchangeModelMarshallException e) {
                LOG.error("Plugin not started, couldn't send module response: " + e.getMessage());
            }
            return false;
        }
        return true;
    }

    @Override
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message) {
        SetCommandRequest request = new SetCommandRequest();
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetCommandRequest.class);
            LOG.info("Send command to plugin:{}",request);
            String pluginName = request.getCommand().getPluginName();
            CommandType commandType = request.getCommand();
            ServiceResponseType service = exchangeService.getService(pluginName);

            if (validate(request.getCommand(), message.getJmsMessage(), service, commandType, request.getUsername())) {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                String unsentMessageGuid = exchangeLog.createUnsentMessage(service.getName(), request.getCommand().getTimestamp(), request.getCommand().getCommand().name(), message.getJmsMessage().getText(), setUnsentMessageTypePropertiesForPoll, request.getUsername());

                request.getCommand().setUnsentMessageGuid(unsentMessageGuid);
                String text = ExchangePluginRequestMapper.createSetCommandRequest(request.getCommand());
                String pluginMessageId = producer.sendEventBusMessage(text, pluginName);

                try {
                    ExchangeLogType log = ExchangeLogMapper.getSendCommandExchangeLog(request.getCommand());
                    exchangeLog.logAndCache(log, pluginMessageId, request.getUsername());
                } catch (ExchangeLogException e) {
                    LOG.error(e.getMessage());
                }
                CommandTypeType x = request.getCommand().getCommand();

                //response back to MobileTerminal (not to rules when email)
                if (request.getCommand().getCommand() != CommandTypeType.EMAIL) {
                    AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeOK();
                    String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
                    producer.sendModuleResponseMessage(message.getJmsMessage(), moduleResponse);
                }
            } else {
                LOG.debug("Can not send to plugin. Response sent to caller.");
            }

        } catch (NullPointerException | ExchangeException e) {
            if (request.getCommand().getCommand() != CommandTypeType.EMAIL) {
                LOG.error("[ Error when sending command to plugin {} ]", e.getMessage());
                exchangeErrorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_EVENT_SERVICE, "Exception when sending command to plugin")));
            }
        } catch (JMSException ex) {
            LOG.error("[ Error when creating unsent message {} ]", ex.getMessage());
        }
    }


    @Override
    public void sendFLUXFAResponseToPlugin(@Observes @SendFLUXFAResponseToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFLUXFAResponseMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAResponseMessageRequest.class);
            LOG.debug("[INFO] Got FLUXFAResponse in exchange with destination :" + request.getDestination());
            String text = ExchangePluginRequestMapper.createSetFLUXFAResponseRequestWithOn(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver(), request.getOnValue());
            final ExchangeLogType log = exchangeLog.log(request, LogType.SEND_FLUX_RESPONSE_MSG, request.getStatus(), TypeRefType.FA_RESPONSE, request.getRequest(), false);
            if(!log.getStatus().equals(ExchangeLogStatusTypeType.FAILED)){ // Send response only if it is NOT FAILED
                LOG.debug("[START] Sending FLUXFAResponse to Flux Activity Plugin..");
                String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                        ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
                LOG.debug("[END] FLUXFAResponse sent to Flux Activity Plugin {}" + pluginMessageId);
            } else {
                LOG.info("[WARN] FLUXFAResponse is FAILED so won't be sent to Flux Activity Plugin..");
            }
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }

    @Override
    public void sendFLUXFAQueryToPlugin(@Observes @SendFaQueryToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFAQueryMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFAQueryMessageRequest.class);
            LOG.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text = ExchangePluginRequestMapper.createSendFLUXFAQueryRequest(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver());
            LOG.debug("Message to plugin {}", text);
            String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            LOG.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            exchangeLog.log(request, LogType.SEND_FA_QUERY_MSG, ExchangeLogStatusTypeType.SENT, TypeRefType.FA_QUERY, request.getRequest(), false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }


    @Override
    public void sendFLUXFAReportToPlugin(@Observes @SendFaReportToPluginEvent ExchangeMessageEvent message) {
        try {
            SetFLUXFAReportMessageRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), SetFLUXFAReportMessageRequest.class);
            LOG.debug("Got SetFAQueryMessageRequest in exchange : " + request.getRequest());
            String text = ExchangePluginRequestMapper.createSendFLUXFAReportRequest(
                    request.getRequest(), request.getDestination(), request.getFluxDataFlow(), request.getSenderOrReceiver());
            LOG.debug("Message to plugin {}", text);
            String pluginMessageId = producer.sendEventBusMessage(text, ((request.getPluginType() == BELGIAN_ACTIVITY)
                    ? ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME : ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME));
            LOG.info("Message sent to Flux ERS Plugin :" + pluginMessageId);
            exchangeLog.log(request, LogType.SEND_FLUX_FA_REPORT_MSG, ExchangeLogStatusTypeType.SENT, TypeRefType.FA_REPORT, request.getRequest(), false);
        } catch (ExchangeModelMarshallException | ExchangeMessageException | ExchangeLogException e) {
            LOG.error("Unable to send FLUX FA Report to plugin.", e);
        }
    }

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
                exchangeLog.createUnsentMessage(service.getName(), command.getTimestamp(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }
            return false;
        } else if (command.getTimestamp() == null) {
            String faultMessage = "No timestamp";
            exchangeErrorEvent.fire(new ExchangeMessageEvent(origin, ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_COMMAND_INVALID, faultMessage)));
            return false;
        } else if (!StatusType.STARTED.equals(service.getStatus())) {
            LOG.info("Plugin to send report to is not started:{}",service);
            try {
                List<UnsentMessageTypeProperty> setUnsentMessageTypePropertiesForPoll = getSetUnsentMessageTypePropertiesForPoll(commandType);
                exchangeLog.createUnsentMessage(service.getName(), command.getTimestamp(), command.getCommand().name(), origin.getText(), setUnsentMessageTypePropertiesForPoll, username);
            } catch (ExchangeLogException | JMSException e) {
                LOG.error("Couldn't create unsentMessage " + e.getMessage());
            }

            try {
                AcknowledgeType ackType = ExchangeModuleResponseMapper.mapAcknowledgeTypeNOK(origin.getJMSMessageID(), "Plugin to send command to is not started");
                String moduleResponse = ExchangeModuleResponseMapper.mapSetCommandResponse(ackType);
                producer.sendModuleResponseMessage(origin, moduleResponse);
            } catch (JMSException | ExchangeModelMarshallException e) {
                LOG.error("Plugin not started, couldn't send module response: " + e.getMessage());
            }

            return false;
        }
        return true;
    }

    private Asset getAsset(String connectId) throws ExchangeLogException {
        Asset asset = null;
        try {
            asset = exchangeAssetService.getAsset(connectId);
        } catch (ExchangeServiceException e) {
            LOG.error("Couldn't create unsentMessage " + e.getMessage());
            throw new ExchangeLogException(e.getMessage());
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
