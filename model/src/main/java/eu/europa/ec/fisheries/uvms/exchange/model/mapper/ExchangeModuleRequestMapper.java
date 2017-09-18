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
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryMethod;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExchangeModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeModuleRequestMapper.class);

    public static String createRegisterServiceRequest(ServiceType serviceType, CapabilityListType capabilityList, SettingListType settingList) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = new RegisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.REGISTER_SERVICE);
        request.setService(serviceType);
        request.setCapabilityList(capabilityList);
        request.setSettingList(settingList);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUnregisterServiceRequest(ServiceType serviceType) throws ExchangeModelMarshallException {
        UnregisterServiceRequest request = new UnregisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.UNREGISTER_SERVICE);
        request.setService(serviceType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSetMovementReportRequest(SetReportMovementType reportType, String username) throws ExchangeModelMarshallException {
        SetMovementReportRequest request = new SetMovementReportRequest();
        request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
        request.setUsername(username);
        request.setRequest(reportType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createReceiveSalesReportRequest(String report, String reportGuid, String sender, String username, PluginType typeOfOriginatingPlugin, Date dateReceived) throws ExchangeModelMarshallException {
        ReceiveSalesReportRequest request = new ReceiveSalesReportRequest();
        request.setMethod(ExchangeModuleMethod.RECEIVE_SALES_REPORT);
        request.setUsername(username);
        request.setReport(report);
        request.setPluginType(typeOfOriginatingPlugin);
        request.setSenderOrReceiver(sender);
        request.setMessageGuid(reportGuid);
        request.setDate(dateReceived);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createReceiveSalesQueryRequest(String query, String queryGuid, String sender, Date receiveDate, String username, PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        ReceiveSalesQueryRequest request = new ReceiveSalesQueryRequest();
        request.setQuery(query);
        enrichBaseRequest(request, ExchangeModuleMethod.RECEIVE_SALES_QUERY, queryGuid, null, sender, receiveDate, username, typeOfOriginatingPlugin);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createReceiveSalesResponseRequest(String response, String guid, String sender, Date date, String username, PluginType pluginType) throws ExchangeModelMarshallException {
        ReceiveSalesResponseRequest receiveSalesResponseRequest = new ReceiveSalesResponseRequest();
        receiveSalesResponseRequest.setResponse(response);

        enrichBaseRequest(receiveSalesResponseRequest, ExchangeModuleMethod.RECEIVE_SALES_RESPONSE, guid, null, sender, date, username, pluginType);
        return JAXBMarshaller.marshallJaxBObjectToString(receiveSalesResponseRequest);
    }

    public static String createSendSalesResponseRequest(String response,
                                                 String guid, String dataFlow,
                                                 String senderOrReceiver, Date date,
                                                 ExchangeLogStatusTypeType validationStatus) throws ExchangeModelMarshallException {
        SendSalesResponseRequest sendSalesResponseRequest = new SendSalesResponseRequest();
        sendSalesResponseRequest.setResponse(checkNotNull(response));
        sendSalesResponseRequest.setValidationStatus(validationStatus);

        enrichBaseRequest(sendSalesResponseRequest, ExchangeModuleMethod.SEND_SALES_RESPONSE, guid, dataFlow, senderOrReceiver, date, null, null);
       return JAXBMarshaller.marshallJaxBObjectToString(sendSalesResponseRequest);
    }

    public static String createSendSalesReportRequest(String report,
                                               String guid, String dataFlow,
                                               String senderOrReceiver, Date date,
                                               ExchangeLogStatusTypeType validationStatus) throws ExchangeModelMarshallException {
        SendSalesReportRequest sendSalesReportRequest = new SendSalesReportRequest();
        sendSalesReportRequest.setReport(checkNotNull(report));
        sendSalesReportRequest.setValidationStatus(validationStatus);

        enrichBaseRequest(sendSalesReportRequest, ExchangeModuleMethod.SEND_SALES_REPORT, guid, dataFlow, senderOrReceiver, date, null, null);
        return JAXBMarshaller.marshallJaxBObjectToString(sendSalesReportRequest);
    }


    public static String createSendReportToPlugin(String pluginName, PluginType type, Date fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginRequest request = createSendReportToPluginRequest(pluginName, type, fwdDate, fwdRule, recipient, payload, recipientInfoList, assetName, ircs, mmsi, externalMarking, flagState);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static SendMovementToPluginRequest createSendReportToPluginRequest(String pluginName, PluginType type, Date fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginRequest request = new SendMovementToPluginRequest();
        request.setMethod(ExchangeModuleMethod.SEND_REPORT_TO_PLUGIN);
        SendMovementToPluginType sendMovementToPluginType = createSendMovementToPluginType(pluginName, type, fwdDate, fwdRule, recipient, payload, recipientInfoList, assetName, ircs, mmsi, externalMarking, flagState);
        request.setReport(sendMovementToPluginType);
        request.setUsername("UVMS");
        return request;
    }

    public static SendMovementToPluginType createSendMovementToPluginType(String pluginName, PluginType type, Date fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginType report = new SendMovementToPluginType();
        mapToMovementType(payload, ircs, mmsi, externalMarking, flagState, assetName);
        report.setTimestamp(DateUtils.nowUTC().toDate());
        report.setFwdDate(fwdDate);
        report.setFwdRule(fwdRule);
        report.setRecipient(recipient);
        report.getRecipientInfo().addAll(recipientInfoList);
        report.setAssetName(assetName);
        report.setMovement(payload);
        report.setPluginType(type);
        report.setPluginName(pluginName);
        report.setIrcs(payload.getIrcs());
        return report;
    }

    private static void mapToMovementType(MovementType movementType, String ircs, String mmsi, String externalMarking, String flagState, String assetName) {
        movementType.setMmsi(mmsi);
        movementType.setExternalMarking(externalMarking);
        movementType.setIrcs(ircs);
        movementType.setFlagState(flagState);
        movementType.setAssetName(assetName);
    }

    public static String createSetCommandSendPollRequest(String pluginName, PollType poll, String username, String fwdRule) throws ExchangeModelMapperException {
        SetCommandRequest request = createSetCommandRequest(pluginName, CommandTypeType.POLL, username, fwdRule);
        request.getCommand().setPoll(poll);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSetCommandSendEmailRequest(String pluginName, EmailType email, String fwdRule) throws ExchangeModelMapperException {
        SetCommandRequest request = createSetCommandRequest(pluginName, CommandTypeType.EMAIL, "UVMS", fwdRule);
        request.getCommand().setEmail(email);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static SetCommandRequest createSetCommandRequest(String pluginName, CommandTypeType type, String username, String fwdRule) throws ExchangeModelMapperException {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        CommandType commandType = new CommandType();
        commandType.setTimestamp(DateUtils.nowUTC().toDate());
        commandType.setCommand(type);
        commandType.setPluginName(pluginName);
        commandType.setFwdRule(fwdRule);
        request.setUsername(username);
        request.setCommand(commandType);
        return request;
    }

    public static String createGetServiceListRequest(List<PluginType> pluginTypes) throws ExchangeModelMapperException {
        GetServiceListRequest request = new GetServiceListRequest();
        request.setMethod(ExchangeModuleMethod.LIST_SERVICES);
        request.getType().addAll(pluginTypes);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUpdatePluginSettingRequest(String serviceClassName, String settingKey, String settingValue) throws ExchangeModelMarshallException {
        UpdatePluginSettingRequest request = new UpdatePluginSettingRequest();
        request.setMethod(ExchangeModuleMethod.UPDATE_PLUGIN_SETTING);
        request.setServiceClassName(serviceClassName);
        SettingType setting = new SettingType();
        setting.setKey(settingKey);
        setting.setValue(settingValue);
        request.setSetting(setting);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    // Asynch processed movement response
    public static String mapToProcessedMovementResponse(SetReportMovementType orgRequest, MovementRefType movementRef) throws ExchangeModelMapperException {
        ProcessedMovementResponse response = new ProcessedMovementResponse();
        response.setMethod(ExchangeModuleMethod.PROCESSED_MOVEMENT);
        response.setOrgRequest(orgRequest);
        response.setMovementRefType(movementRef);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createFluxMdrSyncEntityRequest(String reportType, String username) throws ExchangeModelMarshallException {
        SetFLUXMDRSyncMessageExchangeRequest request = new SetFLUXMDRSyncMessageExchangeRequest();
        request.setMethod(ExchangeModuleMethod.SET_MDR_SYNC_MESSAGE_REQUEST);
        request.setUsername(username);
        request.setRequest(reportType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxMdrSyncEntityResponse(String reportType, String username) throws ExchangeModelMarshallException {
        SetFLUXMDRSyncMessageExchangeResponse request = new SetFLUXMDRSyncMessageExchangeResponse();
        request.setMethod(ExchangeModuleMethod.SET_MDR_SYNC_MESSAGE_RESPONSE);
        request.setUsername(username);
        request.setRequest(reportType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }


    public static String createFluxFAReportRequest(String message, String username, String fluxDFValue,Date date, String messageGuid,PluginType pluginType,String senderReceiver) throws ExchangeModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_REPORT_MESSAGE);
        request.setUsername(username);
        request.setRequest(message);
        request.setFluxDataFlow(fluxDFValue);
        request.setDate(date);
        request.setMessageGuid(messageGuid);
        request.setPluginType(pluginType);
        request.setSenderOrReceiver(senderReceiver);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxFAResponseRequest(String response, String username, String df, String messageGuid, String fr, ExchangeLogStatusTypeType status, String destination) throws ExchangeModelMarshallException {
        SetFLUXFAResponseMessageRequest request = new SetFLUXFAResponseMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_RESPONSE_MESSAGE);
        request.setUsername(username);
        request.setRequest(response);
        request.setFluxDataFlow(df);
        request.setMessageGuid(messageGuid);
        request.setDate(DateUtils.nowUTC().toDate());
        request.setSenderOrReceiver(fr);
        request.setStatus(status);
        request.setDestination(destination);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxFAManualResponseRequest(String reportType, String username) throws ExchangeModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_REPORT_MESSAGE);
        request.setUsername(username);
        request.setRequest(reportType);
        request.setPluginType(PluginType.MANUAL);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUpdateLogStatusRequest(String logGuid, ExchangeLogStatusTypeType newStatus) throws ExchangeModelMarshallException {
        UpdateLogStatusRequest request = new UpdateLogStatusRequest();
        request.setMethod(ExchangeModuleMethod.UPDATE_LOG_STATUS);
        request.setLogGuid(logGuid);
        request.setNewStatus(newStatus);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static void enrichBaseRequest(ExchangeBaseRequest exchangeBaseRequest, ExchangeModuleMethod method, String guid, String dataFlow, String senderOrReceiver, Date date, String username, PluginType pluginType) {
        exchangeBaseRequest.setMethod(checkNotNull(method));
        exchangeBaseRequest.setDate(checkNotNull(date));
        exchangeBaseRequest.setMessageGuid(checkNotNull(guid));
        exchangeBaseRequest.setFluxDataFlow(dataFlow);
        exchangeBaseRequest.setSenderOrReceiver(checkNotNull(senderOrReceiver));
        exchangeBaseRequest.setUsername(username);
        exchangeBaseRequest.setPluginType(pluginType);
    }

}