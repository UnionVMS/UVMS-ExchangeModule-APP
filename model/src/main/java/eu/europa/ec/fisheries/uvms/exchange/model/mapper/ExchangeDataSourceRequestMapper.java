package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.source.v1.ExchangeDataSourceMethod;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.source.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.v1.ServiceType;
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

    public static String mapRegisterServiceToString(ServiceType service)
            throws ExchangeModelMapperException {
        try {
            RegisterServiceRequest request = new RegisterServiceRequest();
            request.setService(service);
            request.setMethod(ExchangeDataSourceMethod.REGISTER_SERVICE);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping RegisterServiceRequest to String ] {}",
                    e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping RegisterServiceRequest to String ]",
                    e);
        }
    }

}
