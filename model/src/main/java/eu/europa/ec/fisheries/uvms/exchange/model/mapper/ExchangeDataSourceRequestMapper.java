package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.wsdl.source.GetDataRequest;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;


public class ExchangeDataSourceRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapObjectToString(ModuleObject data) throws ExchangeModelMapperException {
        try {
            GetDataRequest request = new GetDataRequest();
            request.setId(data);
            return JAXBMarshaller.marshallJaxBObjectToString(request);
        } catch (Exception e) {
            LOG.error("[ Error when mapping Object to String ] {}", e.getMessage());
            throw new ExchangeModelMapperException("[ Error when mapping Object to String ]", e);
        }
    }

}
