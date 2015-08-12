package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.module.v1.CreatePollRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeModuleRequestMapper {
    
    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);
    
    public static String mapCreatePollRequest(PollType poll) throws ExchangeModelMarshallException {
        CreatePollRequest request = new CreatePollRequest();
        request.setMethod(ExchangeModuleMethod.CREATE_POLL);
        request.setPoll(poll);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
}
