package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeModuleResponseMapper {

    private static void validateResponse(TextMessage response, String correlationId) throws ExchangeModelMapperException, JMSException {

        if (response == null) {
            throw new ExchangeModelMapperException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new ExchangeModelMapperException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new ExchangeModelMapperException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: "
                    + response.getJMSCorrelationID());
        }

    }

    public static String mapCreatePollResponseToString(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static ExchangeFault createFaultMessage(FaultCode code, String message) {
    	ExchangeFault fault = new ExchangeFault();
    	fault.setCode(code.getCode());
    	fault.setMessage(message);
    	return fault;
    }

	public static String mapServiceListResponse(List<ServiceType> serviceList) throws ExchangeModelMarshallException {
		GetServiceListResponse response = new GetServiceListResponse();
		response.getService().addAll(serviceList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
}
