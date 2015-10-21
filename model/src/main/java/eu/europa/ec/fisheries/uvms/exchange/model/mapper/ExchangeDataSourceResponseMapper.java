package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.math.BigInteger;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.CreateLogResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceCapabilitiesResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceSettingsResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.RegisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.UnregisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeDataSourceResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    /**
     * Validates a response
     *
     * @param response
     * @param correlationId
     * @throws ExchangeModelMapperException
     * @throws JMSException
     */
    private static void validateResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException, JMSException {

        if (response == null) {
            LOG.error("[ Error when validating response in ResponseMapper: Response is Null ]");
            throw new ExchangeModelMapperException("[ Error when validating response in ResponseMapper: Response is Null ]");
        }

        if (response.getJMSCorrelationID() == null) {
            LOG.error("[ No corelationId in response.] Expected was: {} ", correlationId);
            throw new ExchangeModelMapperException("[ No corelationId in response (Null) ] . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            LOG.error("[ Wrong corelationId in response. Expected was {0} But actual was: {1} ]", correlationId, response.getJMSCorrelationID());
            throw new ExchangeModelMapperException("[ Wrong corelationId in response. ] Expected was: " + correlationId + "But actual was: "
                    + response.getJMSCorrelationID());
        }

    }

    public static List<ServiceResponseType> mapToServiceTypeListFromResponse(TextMessage message) throws ExchangeModelMapperException {
        GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);
        return response.getService();
    }

    public static ServiceResponseType mapToRegisterServiceResponse(TextMessage message) throws ExchangeModelMapperException {
        RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);
        return response.getService();
    }

    public static ServiceResponseType mapToUnregisterServiceResponse(TextMessage message) throws ExchangeModelMapperException {
        UnregisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, UnregisterServiceResponse.class);
        return response.getService();
    }

    public static ServiceResponseType mapToServiceTypeFromGetServiceResponse(TextMessage message) throws ExchangeModelMapperException {
        GetServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceResponse.class);
        return response.getService();
    }

    public static ExchangeLogType mapToExchangeLogTypeFromCreateExchageLogResponse(TextMessage message) throws ExchangeModelMapperException {
        CreateLogResponse response = JAXBMarshaller.unmarshallTextMessage(message, CreateLogResponse.class);
        return response.getExchangeLog();
    }

    public static GetLogListByQueryResponse mapToGetLogListByQueryResponse(TextMessage message) throws ExchangeModelMapperException {
        GetLogListByQueryResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetLogListByQueryResponse.class);
        return response;
    }

    public static String mapServiceTypeListToStringFromResponse(List<ServiceResponseType> services) throws ExchangeModelMapperException {
        GetServiceListResponse response = new GetServiceListResponse();
        response.getService().addAll(services);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceSettingsResponse(List<SettingType> settings) throws ExchangeModelMarshallException {
    	GetServiceSettingsResponse response = new GetServiceSettingsResponse();
        SettingListType listType = new SettingListType();
        listType.getSetting().addAll(settings);
        response.setSettings(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceCapabilitiesResponse(List<CapabilityType> capabilities) throws ExchangeModelMarshallException {
        GetServiceCapabilitiesResponse response = new GetServiceCapabilitiesResponse();
        CapabilityListType listType = new CapabilityListType();
        listType.getCapability().addAll(capabilities);
        response.setCapabilities(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
        GetServiceResponse response = new GetServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceListResponse(List<ServiceResponseType> services) throws ExchangeModelMarshallException {
        GetServiceListResponse response = new GetServiceListResponse();
        response.getService().addAll(services);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createCreateExchangeLogResponse(ExchangeLogType log) throws ExchangeModelMarshallException {
        CreateLogResponse response = new CreateLogResponse();
        response.setExchangeLog(log);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetExchangeListByQueryResponse(List<ExchangeLogType> logs, Long currentPage, Long totalNumberOfPages) throws ExchangeModelMarshallException {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();
        response.getExchangeLogs().addAll(logs);
        response.setCurrentPage(BigInteger.valueOf(currentPage));
        response.setTotalNumberOfPages(BigInteger.valueOf(totalNumberOfPages));
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createRegisterServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
        RegisterServiceResponse response = new RegisterServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createUnregisterServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
    	UnregisterServiceResponse response = new UnregisterServiceResponse();
    	response.setService(service);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
}
