package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.util.List;

import javax.jms.TextMessage;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
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
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;

public class ExchangeModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeModuleRequestMapper.class);

    public static String mapCreatePollRequest(CommandType command) throws ExchangeModelMarshallException {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        request.setCommand(command);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

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

    public static ServiceType mapToServiceTypeFromRequest(TextMessage textMessage) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
        return request.getService();
    }

    public static String createSetMovementReportRequest(SetReportMovementType reportType, String username) throws ExchangeModelMarshallException {
        SetMovementReportRequest request = new SetMovementReportRequest();
        request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
        request.setUsername(username);
        request.setRequest(reportType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createSendReportToPlugin(String pluginName, PluginType type, XMLGregorianCalendar fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginRequest request = createSendReportToPluginRequest(pluginName, type, fwdDate, fwdRule, recipient, payload, recipientInfoList, assetName, ircs, mmsi, externalMarking, flagState);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    private static SendMovementToPluginRequest createSendReportToPluginRequest(String pluginName, PluginType type, XMLGregorianCalendar fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginRequest request = new SendMovementToPluginRequest();
        request.setMethod(ExchangeModuleMethod.SEND_REPORT_TO_PLUGIN);
        SendMovementToPluginType sendMovementToPluginType = createSendMovementToPluginType(pluginName, type, fwdDate, fwdRule, recipient, payload, recipientInfoList, assetName, ircs, mmsi, externalMarking, flagState);
        request.setReport(sendMovementToPluginType);
        request.setUsername("UVMS");
        return request;
    }

    public static SendMovementToPluginType createSendMovementToPluginType(String pluginName, PluginType type, XMLGregorianCalendar fwdDate, String fwdRule, String recipient, MovementType payload, List<RecipientInfoType> recipientInfoList, String assetName, String ircs, String mmsi, String externalMarking, String flagState) throws ExchangeModelMapperException {
        SendMovementToPluginType report = new SendMovementToPluginType();
        mapToMovementType(payload, ircs, mmsi, externalMarking, flagState, assetName);
        report.setTimestamp(DateUtils.dateToXmlGregorian(DateUtils.nowUTC().toDate()));
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

    private static void mapToMovementType(MovementType movementType, String ircs, String mmsi, String externalMarking, String flagState, String assetName){
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
        commandType.setTimestamp(DateUtils.dateToXmlGregorian(DateUtils.nowUTC().toDate()));
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

}