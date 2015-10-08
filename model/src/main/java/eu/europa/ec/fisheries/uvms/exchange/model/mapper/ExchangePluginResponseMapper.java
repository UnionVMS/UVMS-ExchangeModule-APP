/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StartResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StopResponse;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryMethod;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 *
 * @author jojoha
 */
public class ExchangePluginResponseMapper {

    private static void validateResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException, JMSException {

        if (response == null) {
            throw new ExchangeModelMapperException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new ExchangeModelMapperException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new ExchangeModelMapperException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: "
                    + response.getJMSCorrelationID());
        }

    }

    public static String mapToRegisterServiceResponse(AcknowledgeTypeType ackType, ServiceType service, List<SettingType> settings) throws ExchangeModelMarshallException {
        RegisterServiceResponse response = new RegisterServiceResponse();
        response.setAck(ackType);
        response.setService(service);
        SettingListType settingsList = new SettingListType();
        if (settings != null) {
            settingsList.getSetting().addAll(settings);
        }
        response.setSettings(settingsList);
        response.setMethod(ExchangeRegistryMethod.REGISTER_SERVICE);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static AcknowledgeType mapToAcknowlageType(String id, AcknowledgeTypeType ackType) {
        AcknowledgeType type = new AcknowledgeType();
        type.setMessageId(id);
        type.setType(ackType);
        return type;
    }

    public static String mapToPingResponse(boolean registered, boolean enabled) throws ExchangeModelMarshallException {
        PingResponse response = new PingResponse();
        response.setMethod(ExchangePluginMethod.PING);
        response.setResponse("pong");
        response.setRegistered(registered);
        response.setEnabled(enabled);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToStopResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        StopResponse response = new StopResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToStartResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        StartResponse response = new StartResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToSetCommandResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToSetConfigResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetConfigResponse response = new SetConfigResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToSetReportResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetReportResponse response = new SetReportResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String mapToPluginFaultResponse(int code, String message) throws ExchangeModelMarshallException {
        PluginFault response = new PluginFault();
        response.setCode(code);
        response.setMessage(message);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

}
