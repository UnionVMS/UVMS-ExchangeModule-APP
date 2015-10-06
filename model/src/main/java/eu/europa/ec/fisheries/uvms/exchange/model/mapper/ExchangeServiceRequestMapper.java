package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeServiceRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapCreatePollRequest(CommandType command) throws ExchangeModelMarshallException {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        request.setCommand(command);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

}
