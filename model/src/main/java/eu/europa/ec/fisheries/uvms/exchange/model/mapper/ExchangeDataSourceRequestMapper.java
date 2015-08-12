package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.source.v1.ExchangeDataSourceMethod;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceCapabilitiesRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceSettingsRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;

public class ExchangeDataSourceRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapGetServiceListToString() throws ExchangeModelMapperException {
        try {
            GetServiceListRequest request = new GetServiceListRequest();
            request.setMethod(ExchangeDataSourceMethod.LIST_SERVICES);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceListRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]", e);
        }
    }

    public static String mapGetServiceToString(String serviceId) throws ExchangeModelMapperException {
        try {
            GetServiceRequest request = new GetServiceRequest();
            request.setMethod(ExchangeDataSourceMethod.GET_SERVICE);
            request.setServiceId(serviceId);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceListRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]", e);
        }
    }

    public static String mapRegisterServiceToString(ServiceType service) throws ExchangeModelMapperException {
        try {
            RegisterServiceRequest request = new RegisterServiceRequest();
            request.setService(service);
            request.setMethod(ExchangeDataSourceMethod.REGISTER_SERVICE);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping RegisterServiceRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping RegisterServiceRequest to String ]", e);
        }
    }

    public static String mapGetServiceSettingsToString(String serviceClassName) throws ExchangeModelMapperException {
        try {
            GetServiceSettingsRequest request = new GetServiceSettingsRequest();
            request.setServiceName(serviceClassName);
            request.setMethod(ExchangeDataSourceMethod.GET_SETTINGS);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceSettingsRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceSettingsRequest to String ]", e);
        }
    }

    public static String mapGetServiceCapabilitiesToString(String serviceClassName) throws ExchangeModelMapperException {
        try {
            GetServiceCapabilitiesRequest request = new GetServiceCapabilitiesRequest();
            request.setServiceName(serviceClassName);
            request.setMethod(ExchangeDataSourceMethod.GET_CAPABILITIES);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceCapabilitiesRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceCapabilitiesRequest to String ]", e);
        }
    }

}
