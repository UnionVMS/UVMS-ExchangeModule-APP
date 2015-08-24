package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.CreatePollRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeServiceRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapCreatePollRequest(PollType poll) throws ExchangeModelMarshallException {
        CreatePollRequest request = new CreatePollRequest();
        // TODO: Probably not ExchangeModuleMethod, but another specific for
        // plugin/service:
        request.setMethod(ExchangeModuleMethod.CREATE_POLL);
        request.setPoll(poll);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

}
