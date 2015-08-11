package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceCapabilitiesResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceSettingsResponse;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.source.v1.RegisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.v1.SettingType;
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

    public static List<ServiceType> mapToServiceTypeListFromResponse(TextMessage message) throws ExchangeModelMapperException {
        GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);
        return response.getService();
    }

    public static ServiceType mapToServiceTypeFromResponse(TextMessage message) throws ExchangeModelMapperException {
        RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);
        return response.getService();
    }

    public static String mapServiceTypeListToStringFromResponse(List<ServiceType> services) throws ExchangeModelMapperException {
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

    public static String createGetServiceListResponse(List<ServiceType> services) throws ExchangeModelMarshallException {
        GetServiceListResponse response = new GetServiceListResponse();
        response.getService().addAll(services);
        response.setCurrentPage(null);
        response.setTotalNumberOfPages(null);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createRegisterServiceResponse(ServiceType service) throws ExchangeModelMarshallException {
        RegisterServiceResponse response = new RegisterServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

}
