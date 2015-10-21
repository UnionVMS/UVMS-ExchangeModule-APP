package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.CreateLogRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.ExchangeDataSourceMethod;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceCapabilitiesRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceSettingsRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.SetServiceSettingsRequest;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeDataSourceRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapCreateExchangeLogToString(ExchangeLogType log) throws ExchangeModelMapperException {
        try {
            CreateLogRequest request = new CreateLogRequest();
            request.setMethod(ExchangeDataSourceMethod.CREATE_LOG);
            request.setExchangeLog(log);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceListRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]");
        }
    }

    public static String mapGetExchageLogListByQueryToString(ExchangeListQuery query) throws ExchangeModelMapperException {
        try {
            GetLogListByQueryRequest request = new GetLogListByQueryRequest();
            request.setMethod(ExchangeDataSourceMethod.GET_LOG_BY_QUERY);
            request.setQuery(query);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceListRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]");
        }
    }

    public static String mapGetServiceListToString(List<PluginType> pluginTypes) throws ExchangeModelMapperException {
        try {
            GetServiceListRequest request = new GetServiceListRequest();
            request.setMethod(ExchangeDataSourceMethod.LIST_SERVICES);
            request.getType().addAll(pluginTypes);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping GetServiceListRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]");
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
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceListRequest to String ]");
        }
    }

    public static String mapRegisterServiceToString(ServiceType service, CapabilityListType capabilityList, SettingListType settingList) throws ExchangeModelMapperException {
        try {
            RegisterServiceRequest request = new RegisterServiceRequest();
            request.setMethod(ExchangeDataSourceMethod.REGISTER_SERVICE);
            request.setService(service);
            request.setCapabilityList(capabilityList);
            request.setSettingList(settingList);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping RegisterServiceRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping RegisterServiceRequest to String ]");
        }
    }

    public static String mapUnregisterServiceToString(ServiceType service) throws ExchangeModelMapperException {
        try {
            UnregisterServiceRequest request = new UnregisterServiceRequest();
            request.setService(service);
            request.setMethod(ExchangeDataSourceMethod.UNREGISTER_SERVICE);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping RegisterServiceRequest to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping RegisterServiceRequest to String ]");
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
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceSettingsRequest to String ]");
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
            throw new ExchangeModelMapperException("[ Error when mapping GetServiceCapabilitiesRequest to String ]");
        }
    }

	public static String mapSetSettingsToString(String serviceClassName, SettingListType settingListType) throws ExchangeModelMapperException {
		try {
			SetServiceSettingsRequest request = new SetServiceSettingsRequest();
			request.setMethod(ExchangeDataSourceMethod.SET_SETTINGS);
			request.setServiceName(serviceClassName);
			request.setSettings(settingListType);
			return JAXBMarshaller.marshallJaxBObjectToString(request);
		} catch (ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping SetServiceSettingsRequest ] ");
			throw new ExchangeModelMapperException("[ Error when mapping SetServiceSettingsRequest ]");
		}
	}

}
