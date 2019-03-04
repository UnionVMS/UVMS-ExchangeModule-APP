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

import static eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod.QUERY_ASSET_INFORMATION;
import static eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod.RECEIVE_ASSET_INFORMATION;
import static eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod.SEND_ASSET_INFORMATION;

import javax.xml.bind.JAXBException;
import java.util.Date;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ProcessedMovementResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.QueryAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.RcvFLUXFaResponseMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveInvalidSalesMessage;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesQueryRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ReceiveSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendAssetInformationRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFAQueryMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAReportMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXFAResponseMessageRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageExchangeRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMDRSyncMessageExchangeResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetFLUXMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdateLogStatusRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.RecipientInfoType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
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
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExchangeModuleRequestMapper {

    private static final String FLUX_VESSEL_PLUGIN = "flux-vessel-plugin";

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

    public static String createSetMovementReportRequest(SetReportMovementType message, String username, String fluxDFValue, Date date,
                                                      String messageGuid, PluginType pluginType, String senderReceiver, String onValue) throws ExchangeModelMarshallException {
        SetMovementReportRequest request = new SetMovementReportRequest();
        request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
        request.setUsername(username);
        request.setRequest(message);
        request.setDate(date);
        populateBaseProperties(request, fluxDFValue, date, messageGuid, pluginType, senderReceiver, onValue, username, null, null, "");
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }


    public static String createReceiveAssetInformation(String assets, String username, PluginType pluginType) throws ExchangeModelMarshallException {
        ReceiveAssetInformationRequest request = new ReceiveAssetInformationRequest();
        request.setAssets(assets);
        request.setUsername(username);
        request.setMethod(RECEIVE_ASSET_INFORMATION);
        request.setSenderOrReceiver(FLUX_VESSEL_PLUGIN);
        request.setDate(new Date());
        request.setPluginType(pluginType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSendAssetInformation(String assets, String username) throws ExchangeModelMarshallException {
        SendAssetInformationRequest request = new SendAssetInformationRequest();
        request.setAssets(assets);
        request.setUsername(username);
        request.setMethod(SEND_ASSET_INFORMATION);
        request.setSenderOrReceiver(FLUX_VESSEL_PLUGIN);
        request.setDate(new Date());
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createQueryAssetInformation(String assets, String username) throws ExchangeModelMarshallException {
        QueryAssetInformationRequest request = new QueryAssetInformationRequest();
        request.setAssets(assets);
        request.setUsername(username);
        request.setMethod(QUERY_ASSET_INFORMATION);
        request.setSenderOrReceiver(FLUX_VESSEL_PLUGIN);
        request.setDate(new Date());
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createReceiveSalesReportRequest(String report, String reportGuid, String sender, String username, PluginType typeOfOriginatingPlugin, Date dateReceived, String on) throws ExchangeModelMarshallException {
        ReceiveSalesReportRequest request = new ReceiveSalesReportRequest();
        request.setReport(report);

        enrichBaseRequest(request, ExchangeModuleMethod.RECEIVE_SALES_REPORT, reportGuid, null, sender, dateReceived, username, typeOfOriginatingPlugin, on);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    @Deprecated
    public static String createReceiveSalesReportRequest(String report, String reportGuid, String sender, String username, PluginType typeOfOriginatingPlugin, Date dateReceived) throws ExchangeModelMarshallException {
        return createReceiveSalesReportRequest(report, reportGuid, sender, username, typeOfOriginatingPlugin, dateReceived, null);
    }

    public static String createReceiveSalesQueryRequest(String query, String queryGuid, String sender, Date receiveDate, String username, PluginType typeOfOriginatingPlugin, String on) throws ExchangeModelMarshallException {
        ReceiveSalesQueryRequest request = new ReceiveSalesQueryRequest();
        request.setQuery(query);
        enrichBaseRequest(request, ExchangeModuleMethod.RECEIVE_SALES_QUERY, queryGuid, null, sender, receiveDate, username, typeOfOriginatingPlugin, on);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    @Deprecated
    public static String createReceiveSalesQueryRequest(String query, String queryGuid, String sender, Date receiveDate, String username, PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        return createReceiveSalesQueryRequest(query, queryGuid, sender, receiveDate, username, typeOfOriginatingPlugin, null);
    }

    public static String createReceiveSalesResponseRequest(String response, String guid, String sender, Date date, String username, PluginType pluginType, String on) throws ExchangeModelMarshallException {
        ReceiveSalesResponseRequest receiveSalesResponseRequest = new ReceiveSalesResponseRequest();
        receiveSalesResponseRequest.setResponse(response);

        enrichBaseRequest(receiveSalesResponseRequest, ExchangeModuleMethod.RECEIVE_SALES_RESPONSE, guid, null, sender, date, username, pluginType, on);
        return JAXBMarshaller.marshallJaxBObjectToString(receiveSalesResponseRequest);
    }

    @Deprecated
    public static String createReceiveSalesResponseRequest(String response, String guid, String sender, Date date, String username, PluginType pluginType) throws ExchangeModelMarshallException {
        return createReceiveSalesResponseRequest(response, guid, sender, date, username, pluginType, null);
    }

    public static String createReceiveInvalidSalesMessage(String respondToInvalidMessageRequest, String guid, String sender, Date date, String username, PluginType pluginType) throws ExchangeModelMarshallException {
        return createReceiveInvalidSalesMessage(respondToInvalidMessageRequest, guid, sender,
                date, username, pluginType, null);
    }

    public static String createReceiveInvalidSalesMessage(String respondToInvalidMessageRequest, String guid, String sender, Date date, String username, PluginType pluginType, String originalMessage) throws ExchangeModelMarshallException {
        ReceiveInvalidSalesMessage receiveInvalidSalesMessage = new ReceiveInvalidSalesMessage();
        receiveInvalidSalesMessage.setRespondToInvalidMessageRequest(respondToInvalidMessageRequest);
        receiveInvalidSalesMessage.setOriginalMessage(originalMessage);
        enrichBaseRequest(receiveInvalidSalesMessage, ExchangeModuleMethod.RECEIVE_INVALID_SALES_MESSAGE, guid, null, sender, date, username, pluginType, null);

        return JAXBMarshaller.marshallJaxBObjectToString(receiveInvalidSalesMessage);
    }


    /**
     @deprecated use createSendSalesResponseRequest(String response,
     String guid, String dataFlow,
     String senderOrReceiver, Date date,
     ExchangeLogStatusTypeType validationStatus,
     PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException instead
     **/
    @Deprecated
    public static String createSendSalesResponseRequest(String response,
                                                        String guid, String dataFlow,
                                                        String receiver, Date date,
                                                        ExchangeLogStatusTypeType validationStatus) throws ExchangeModelMarshallException {
        return createSendSalesResponseRequest(response, guid, dataFlow, receiver, date, validationStatus, PluginType.FLUX);
    }

    public static String createSendSalesResponseRequest(String response,
                                                        String guid, String dataFlow,
                                                        String receiver, Date date,
                                                        ExchangeLogStatusTypeType validationStatus,
                                                        String typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        return createSendSalesResponseRequest(response, guid, dataFlow, receiver, date, validationStatus, PluginType.valueOf(typeOfOriginatingPlugin));
    }

    public static String createSendSalesResponseRequest(String response,
                                                        String guid, String dataFlow,
                                                        String receiver, Date date,
                                                        ExchangeLogStatusTypeType validationStatus,
                                                        PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        SendSalesResponseRequest sendSalesResponseRequest = new SendSalesResponseRequest();
        sendSalesResponseRequest.setResponse(checkNotNull(response));
        sendSalesResponseRequest.setValidationStatus(validationStatus);

        enrichBaseRequest(sendSalesResponseRequest, ExchangeModuleMethod.SEND_SALES_RESPONSE, guid, dataFlow, receiver, date, null, typeOfOriginatingPlugin, null);
       return JAXBMarshaller.marshallJaxBObjectToString(sendSalesResponseRequest);
    }

    /**
     @deprecated use createSendSalesReportRequest(String report,
     String guid, String dataFlow,
     String receiver, Date date,
     ExchangeLogStatusTypeType validationStatus,
     PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException instead
     **/
    @Deprecated
    public static String createSendSalesReportRequest(String report, String guid, String dataFlow, String receiver,
                                                      Date date, ExchangeLogStatusTypeType validationStatus) throws ExchangeModelMarshallException {
        return createSendSalesReportRequest(report, guid, dataFlow, receiver, date, validationStatus, PluginType.FLUX);
    }

    public static String createSendSalesReportRequest(String report, String guid, String dataFlow, String receiver,
                                                      Date date, ExchangeLogStatusTypeType validationStatus,
                                                      String typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        return createSendSalesReportRequest(report, guid, dataFlow, receiver, date, validationStatus, PluginType.valueOf(typeOfOriginatingPlugin));
    }

    public static String createSendSalesReportRequest(String report, String guid, String dataFlow, String receiver,
                                                      Date date, ExchangeLogStatusTypeType validationStatus,
                                                      PluginType typeOfOriginatingPlugin) throws ExchangeModelMarshallException {
        SendSalesReportRequest sendSalesReportRequest = new SendSalesReportRequest();
        sendSalesReportRequest.setReport(checkNotNull(report));
        sendSalesReportRequest.setValidationStatus(validationStatus);

        enrichBaseRequest(sendSalesReportRequest, ExchangeModuleMethod.SEND_SALES_REPORT, guid, dataFlow, receiver, date, null, typeOfOriginatingPlugin, null);
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

    public static String createFluxMdrSyncEntityRequest(String reportType, String username, String fr) throws ExchangeModelMarshallException {
        SetFLUXMDRSyncMessageExchangeRequest request = new SetFLUXMDRSyncMessageExchangeRequest();
        request.setMethod(ExchangeModuleMethod.SET_MDR_SYNC_MESSAGE_REQUEST);
        request.setUsername(username);
        request.setRequest(reportType);
        request.setFr(fr);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxMdrSyncEntityResponse(String reportType, String username) throws ExchangeModelMarshallException {
        SetFLUXMDRSyncMessageExchangeResponse request = new SetFLUXMDRSyncMessageExchangeResponse();
        request.setMethod(ExchangeModuleMethod.SET_MDR_SYNC_MESSAGE_RESPONSE);
        request.setUsername(username);
        request.setRequest(reportType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     * @deprecated  As of release 4.0.2, replaced by createActivityRequest(String message, String username, String fluxDFValue,Date date,
     *                                              String messageGuid, PluginType pluginType, String senderReceiver, String onValue)
     */
    @Deprecated
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

    public static String createFluxFAReportRequest(String message, String username, String fluxDFValue, Date date,
                                                   String messageGuid, PluginType pluginType, String senderReceiver, String onValue,
                                                   String todt, String to, String ad) throws ExchangeModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_REPORT_MESSAGE);
        request.setRequest(message);
        populateBaseProperties(request, fluxDFValue, date, messageGuid, pluginType, senderReceiver, onValue, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFARequestForUnknownType(String message, String username, String fluxDFValue, Date date,
                                                       String messageGuid, PluginType pluginType, String senderReceiver, String onValue,
                                                       String todt, String to, String ad) throws ExchangeModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ExchangeModuleMethod.UNKNOWN);
        request.setRequest(message);
        populateBaseProperties(request, fluxDFValue, date, messageGuid, pluginType, senderReceiver, onValue, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFaQueryRequest(String message, String username, String fluxDFValue, Date date,
                                              String messageGuid, PluginType pluginType, String senderReceiver, String onValue,
                                              String todt, String to, String ad) throws ExchangeModelMarshallException {
        SetFAQueryMessageRequest request = new SetFAQueryMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FA_QUERY_MESSAGE);
        request.setRequest(message);
        populateBaseProperties(request, fluxDFValue, date, messageGuid, pluginType, senderReceiver, onValue, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxResponseRequest(String message, String username, String dfValue, Date date,
                                                   String messageGuid, PluginType pluginType, String senderReceiver, String onValue,
                                                   String todt, String to, String ad) throws ExchangeModelMarshallException {
        RcvFLUXFaResponseMessageRequest request = new RcvFLUXFaResponseMessageRequest();
        request.setMethod(ExchangeModuleMethod.RCV_FLUX_FA_RESPONSE_MESSAGE);
        request.setRequest(message);
        request.setResponseMessageGuid(messageGuid);
        populateBaseProperties(request, dfValue, date, messageGuid, pluginType, senderReceiver, onValue, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSendFaQueryMessageRequest(String faQueryMessageStr, String username, String logId, String fluxDataFlow,
                                                         String senderOrReceiver, String todt, String to, String ad) throws ExchangeModelMarshallException {
        SetFAQueryMessageRequest request = new SetFAQueryMessageRequest();
        request.setMethod(ExchangeModuleMethod.SEND_FA_QUERY_MESSAGE);
        request.setRequest(faQueryMessageStr);
        populateBaseProperties(request, fluxDataFlow, DateUtils.nowUTC().toDate(), logId, null, senderOrReceiver, null, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSendFaReportMessageRequest(String faReportMessageStr, String username, String logId, String fluxDataFlow,
                                                         String senderOrReceiver, String onValue, String todt, String to, String ad) throws ExchangeModelMarshallException {
        SetFLUXFAReportMessageRequest request = new SetFLUXFAReportMessageRequest();
        request.setMethod(ExchangeModuleMethod.SEND_FLUX_FA_REPORT_MESSAGE);
        request.setRequest(faReportMessageStr);
        populateBaseProperties(request, fluxDataFlow, DateUtils.nowUTC().toDate(), logId, null, senderOrReceiver, onValue, username, todt, to, ad);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static void populateBaseProperties(ExchangeBaseRequest request, String fluxDFValue, Date date, String messageGuid, PluginType pluginType,
                                               String senderReceiver, String onValue, String username,  String todt, String to, String ad) {
        request.setUsername(username);
        request.setFluxDataFlow(fluxDFValue);
        request.setDate(date);
        request.setMessageGuid(messageGuid);
        request.setPluginType(pluginType);
        request.setTodt(todt);
        request.setTo(to);
        request.setSenderOrReceiver(senderReceiver);
        request.setOnValue(onValue);
        request.setAd(ad);
    }


    public static String createFluxFAResponseRequest(String response, String username, String df, String messageGuid, String fr, ExchangeLogStatusTypeType status, String destination) throws ExchangeModelMarshallException {
        return createFluxFAResponseRequest(response, username, df, messageGuid, fr, status, destination, PluginType.FLUX);
    }

    public static String createFluxFAResponseRequestWithOnValue(String response, String username, String df, String messageGuid, String fr,
                                                                String onVal, ExchangeLogStatusTypeType status, String destination, PluginType pluginType,
                                                                String responseGuid) throws ExchangeModelMarshallException {
        SetFLUXFAResponseMessageRequest request = new SetFLUXFAResponseMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_RESPONSE_MESSAGE);
        request.setUsername(username);
        request.setRequest(response);
        request.setFluxDataFlow(df);
        request.setMessageGuid(messageGuid);
        request.setDate(DateUtils.nowUTC().toDate());
        request.setPluginType(pluginType);
        request.setSenderOrReceiver(fr);
        request.setStatus(status);
        request.setDestination(destination);
        request.setOnValue(onVal);
        request.setResponseMessageGuid(responseGuid);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    /**
     *
     *@Deprecated Use the createFluxFAResponseRequestWithOnValue(...){} method instead
     */
    @Deprecated
    public static String createFluxFAResponseRequest(String response, String username, String df, String messageGuid, String fr, ExchangeLogStatusTypeType status, String destination, PluginType pluginType) throws ExchangeModelMarshallException {
        SetFLUXFAResponseMessageRequest request = new SetFLUXFAResponseMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_RESPONSE_MESSAGE);
        request.setUsername(username);
        request.setRequest(response);
        request.setFluxDataFlow(df);
        request.setMessageGuid(messageGuid);
        request.setDate(DateUtils.nowUTC().toDate());
        request.setPluginType(pluginType);
        request.setSenderOrReceiver(fr);
        request.setStatus(status);
        request.setDestination(destination);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createFluxFAResponseRequest(String response, String username, String df, String messageGuid, String fr,
                                                     ExchangeLogStatusTypeType status, String destination, PluginType pluginType, String todt,
                                                     String to, String onValue) throws ExchangeModelMarshallException {
        SetFLUXFAResponseMessageRequest request = new SetFLUXFAResponseMessageRequest();
        request.setMethod(ExchangeModuleMethod.SET_FLUX_FA_RESPONSE_MESSAGE);
        request.setUsername(username);
        request.setRequest(response);
        request.setFluxDataFlow(df);
        request.setMessageGuid(messageGuid);
        request.setDate(DateUtils.nowUTC().toDate());
        request.setTodt(todt);
        request.setTo(to);
        request.setOnValue(onValue);
        request.setPluginType(pluginType);
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

    public static String createUpdateLogStatusRequest(String logGuid, Exception e) throws ExchangeModelMarshallException {
        UpdateLogStatusRequest request = new UpdateLogStatusRequest();
        request.setMethod(ExchangeModuleMethod.UPDATE_LOG_BUSINESS_ERROR);
        request.setLogGuid(logGuid);
        if (e != null){
            request.setBusinessModuleExceptionMessage(ExceptionUtils.getMessage(e) + ":" + ExceptionUtils.getStackTrace(e));
        }
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static void enrichBaseRequest(ExchangeBaseRequest exchangeBaseRequest, ExchangeModuleMethod method, String guid, String dataFlow,
                                          String senderOrReceiver, Date date, String username, PluginType pluginType, String on) {
        exchangeBaseRequest.setMethod(checkNotNull(method));
        exchangeBaseRequest.setDate(checkNotNull(date));
        exchangeBaseRequest.setMessageGuid(checkNotNull(guid));
        exchangeBaseRequest.setFluxDataFlow(dataFlow);
        exchangeBaseRequest.setSenderOrReceiver(checkNotNull(senderOrReceiver));
        exchangeBaseRequest.setUsername(username);
        exchangeBaseRequest.setPluginType(pluginType);
        exchangeBaseRequest.setOnValue(on);
    }

    /**
     * Ensures that an object reference is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static String createSetFLUXMovementReportRequest(String message, String username, String fluxDFValue, Date date,
                                                            String messageGuid, PluginType pluginType, String senderReceiver, String onValue,
                                                            String guid, String registeredClassName, String ad, String to, String todt) throws JAXBException {
        SetFLUXMovementReportRequest request = new SetFLUXMovementReportRequest();
        request.setMethod(ExchangeModuleMethod.RECEIVE_MOVEMENT_REPORT_BATCH);
        request.setRequest(message);
        request.setMessageGuid(guid);
        populateBaseProperties(request, fluxDFValue, date, messageGuid, pluginType, senderReceiver, onValue,
                username, registeredClassName, ad, to, todt);
        return JAXBMarshaller.marshallJaxBObjectToString(request, "Unicode", true);
    }

    private static void populateBaseProperties(ExchangeBaseRequest request, String fluxDFValue, Date date, String messageGuid,
                                               PluginType pluginType, String senderReceiver, String onValue, String username,
                                               String registeredClassName, String ad, String to, String todt) {
        request.setUsername(username);
        request.setFluxDataFlow(fluxDFValue);
        request.setDf(fluxDFValue);
        request.setDate(date);
        request.setMessageGuid(messageGuid);
        request.setPluginType(pluginType);
        request.setSenderOrReceiver(senderReceiver);
        request.setOnValue(onValue);
        request.setRegisteredClassName(registeredClassName);
        request.setTo(to);
        request.setTodt(todt);
        request.setAd(ad);
    }
}
