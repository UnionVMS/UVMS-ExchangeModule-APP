package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.CreatePollRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapCreatePollRequest(PollType poll) throws ExchangeModelMarshallException {
        CreatePollRequest request = new CreatePollRequest();
        request.setMethod(ExchangeModuleMethod.CREATE_POLL);
        request.setPoll(poll);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createRegisterServiceRequest(ServiceType serviceType) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = new RegisterServiceRequest();
        request.setMethod(ExchangeModuleMethod.REGISTER_SERVICE);
        request.setService(serviceType);

        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUnregisterServiceRequest(ServiceType serviceType) throws ExchangeModelMarshallException {
        UnregisterServiceRequest request = new UnregisterServiceRequest();
        request.setMethod(ExchangeModuleMethod.UNREGISTER_SERVICE);
        request.setService(serviceType);

        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static ServiceType mapToServiceTypeFromRequest(TextMessage textMessage) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
        return request.getService();
    }

}
