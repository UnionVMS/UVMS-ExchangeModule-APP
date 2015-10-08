package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.ReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryMethod;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;

import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    public static String mapCreatePollRequest(CommandType command) throws ExchangeModelMarshallException {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        request.setCommand(command);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createRegisterServiceRequest(ServiceType serviceType, String messageSelector) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = new RegisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.REGISTER_SERVICE);
        request.setResponseTopicMessageSelector(messageSelector);
        request.setService(serviceType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUnregisterServiceRequest(ServiceType serviceType) throws ExchangeModelMarshallException {
        UnregisterServiceRequest request = new UnregisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.UNREGISTER_SERVICE);
        request.setService(serviceType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static ServiceType mapToServiceTypeFromRequest(TextMessage textMessage) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
        return request.getService();
    }

    public static String createSetMovementReportRequest(ReportMovementType reportType) throws ExchangeModelMarshallException {
    	SetMovementReportRequest request = new SetMovementReportRequest();
    	request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
		request.setRequest(reportType);
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
}
