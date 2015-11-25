package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.source.v1.*;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeValidationException;

public class ExchangeDataSourceResponseMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeDataSourceRequestMapper.class);

    /**
     * Validates a response
     *
     * @param response
     * @param correlationId
     * @throws ExchangeModelMapperException
     * @throws JMSException
     */
    private static void validateResponse(TextMessage response, String correlationId) throws ExchangeValidationException, JMSException {

        if (response == null) {
            throw new ExchangeValidationException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new ExchangeValidationException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new ExchangeValidationException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: "
                    + response.getJMSCorrelationID());
        }

        try {
			ExchangeFault fault = JAXBMarshaller.unmarshallTextMessage(response, ExchangeFault.class);
	        //TODO use fault
			throw new ExchangeValidationException("Fault found when validate response " + fault.getMessage());
		} catch (ExchangeModelMarshallException e) {
			//everything went well
		}  

    }

    public static List<ServiceResponseType> mapToServiceTypeListFromResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
			validateResponse(message, correlationId);
			GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);
	        return response.getService();
		} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to list of service ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to list of service ] " + e.getMessage());
		}
    }

    public static ServiceResponseType mapToRegisterServiceResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);
            return response.getService();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to list of service ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to list of service ] " + e.getMessage());
		}
    }

    public static ServiceResponseType mapToUnregisterServiceResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		UnregisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, UnregisterServiceResponse.class);
            return response.getService();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to list of service ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to list of service ] " + e.getMessage());
		}
        
    }

    public static ServiceResponseType mapToServiceTypeFromGetServiceResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		GetServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceResponse.class);
            return response.getService();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to list of service ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to list of service ] " + e.getMessage());
		}
        
    }

    public static ServiceResponseType mapToServiceTypeFromSetSettingsResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		SetServiceSettingsResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetServiceSettingsResponse.class);
            return response.getService();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to list of service ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to list of service ] " + e.getMessage());
		}
	}
    
    public static ExchangeLogType mapToExchangeLogTypeFromCreateExchageLogResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		CreateLogResponse response = JAXBMarshaller.unmarshallTextMessage(message, CreateLogResponse.class);
            return response.getExchangeLog();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to create exchange log ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to create exchange log] " + e.getMessage());
		}
    }

    public static GetLogListByQueryResponse mapToGetLogListByQueryResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		GetLogListByQueryResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetLogListByQueryResponse.class);
            return response;
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
    		LOG.error("[ Error when mapping response to exchange log ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to exchange log] " + e.getMessage());
    	}
    }

    public static ServiceResponseType mapSetServiceResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		SetServiceStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetServiceStatusResponse.class);
    		return response.getService();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
    		LOG.error("[ Error when mapping response to set service response ]");
    		throw new ExchangeModelMapperException("[ Error when mapping response to set service response ] " + e.getMessage());
    	}
	}
    
    public static ExchangeLogType mapCreateExchangeLogResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
		try {
			validateResponse(message, correlationId);
			CreateLogResponse response = JAXBMarshaller.unmarshallTextMessage(message, CreateLogResponse.class);
			return response.getExchangeLog();
		} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to create log response ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to create log response ] " + e.getMessage());
		}
	}
    
    public static ExchangeLogType mapUpdateLogStatusResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		UpdateLogStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, UpdateLogStatusResponse.class);
    		return response.getExchangeLog();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
    		LOG.error("[ Error when mapping response to update log status response ]");
    		throw new ExchangeModelMapperException("[ Error when mapping response to update log status response ]" + e.getMessage());
    	}
	}
    
    public static List<UnsentMessageType> mapGetSendingQueueResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		GetUnsentMessageListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetUnsentMessageListResponse.class);
    		return response.getUnsentMessage();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
    		LOG.error("[ Error when mapping response to unsent message list response ]");
    		throw new ExchangeModelMapperException("[ Error when mapping response to unsent message list response ]" + e.getMessage());
    	}
	}

    public static List<ExchangeLogStatusType> mapGetLogStatusHistoryByQueryResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
    	try {
    		validateResponse(message, correlationId);
    		GetLogStatusHistoryByQueryResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetLogStatusHistoryByQueryResponse.class);
    		return response.getStatusLog();
    	} catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
    		LOG.error("[ Error when mapping response to log status history response ]");
    		throw new ExchangeModelMapperException("[ Error when mapping response to log status history response ]" + e.getMessage());
    	}
	}
    
    public static String createGetServiceSettingsResponse(List<SettingType> settings) throws ExchangeModelMarshallException {
    	GetServiceSettingsResponse response = new GetServiceSettingsResponse();
        SettingListType listType = new SettingListType();
        listType.getSetting().addAll(settings);
        response.setSettings(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceCapabilitiesResponse(List<CapabilityType> capabilities) throws ExchangeModelMarshallException {
        GetServiceCapabilitiesResponse response = new GetServiceCapabilitiesResponse();
        CapabilityListType listType = new CapabilityListType();
        listType.getCapability().addAll(capabilities);
        response.setCapabilities(listType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
        GetServiceResponse response = new GetServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetServiceListResponse(List<ServiceResponseType> services) throws ExchangeModelMarshallException {
        GetServiceListResponse response = new GetServiceListResponse();
        response.getService().addAll(services);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createCreateExchangeLogResponse(ExchangeLogType log) throws ExchangeModelMarshallException {
        CreateLogResponse response = new CreateLogResponse();
        response.setExchangeLog(log);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createGetExchangeListByQueryResponse(List<ExchangeLogType> logs, int currentPage, int totalNumberOfPages) throws ExchangeModelMarshallException {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();
        response.getExchangeLog().addAll(logs);
        response.setCurrentPage(currentPage);
        response.setTotalNumberOfPages(totalNumberOfPages);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createRegisterServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
        RegisterServiceResponse response = new RegisterServiceResponse();
        response.setService(service);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createUnregisterServiceResponse(ServiceResponseType service) throws ExchangeModelMarshallException {
    	UnregisterServiceResponse response = new UnregisterServiceResponse();
    	response.setService(service);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

	public static String createSetServiceSettingsResponse(ServiceResponseType updatedService) throws ExchangeModelMarshallException {
		SetServiceSettingsResponse response = new SetServiceSettingsResponse();
		response.setService(updatedService);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createSetServiceStatusResponse(ServiceResponseType statusService) throws ExchangeModelMarshallException {
		SetServiceStatusResponse response = new SetServiceStatusResponse();
		response.setService(statusService);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createUpdateLogStatusResponse(ExchangeLogType exchangeLog) throws ExchangeModelMarshallException {
		UpdateLogStatusResponse response = new UpdateLogStatusResponse();
		response.setExchangeLog(exchangeLog);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createGetLogStatusHistoryResponse(ExchangeLogStatusType statusType) throws ExchangeModelMarshallException {
		GetLogStatusHistoryResponse response = new GetLogStatusHistoryResponse();
		response.setStatus(statusType);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createCreateUnsentMessageResponse(String messageId) throws ExchangeModelMarshallException {
		CreateUnsentMessageResponse response = new CreateUnsentMessageResponse();
		response.setUnsentMessageId(messageId);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createGetUnsentMessageListResponse(List<UnsentMessageType> unsentMessageList) throws ExchangeModelMarshallException {
		GetUnsentMessageListResponse response = new GetUnsentMessageListResponse();
		response.getUnsentMessage().addAll(unsentMessageList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createResentMessageResponse(List<UnsentMessageType> messageList) throws ExchangeModelMarshallException {
		ResendMessageResponse response = new ResendMessageResponse();
		response.getResentMessage().addAll(messageList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createGetLogStatusHistoryByQueryResponse(List<ExchangeLogStatusType> statusHistoryList) throws ExchangeModelMarshallException {
		GetLogStatusHistoryByQueryResponse response = new GetLogStatusHistoryByQueryResponse();
		response.getStatusLog().addAll(statusHistoryList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

	public static String createSingleExchangeLogResponse(ExchangeLogType exchangeLog) throws ExchangeModelMarshallException {
	    SingleExchangeLogResponse response = new SingleExchangeLogResponse();
	    response.setExchangeLog(exchangeLog);
	    return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

    public static ExchangeLogType mapToExchangeLogTypeFromSingleExchageLogResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
        try {
            validateResponse(message, correlationId);
            SingleExchangeLogResponse singleExchangeLogResponse = JAXBMarshaller.unmarshallTextMessage(message, SingleExchangeLogResponse.class);
            return singleExchangeLogResponse.getExchangeLog();
        } catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when mapping response to single exchange log. ]");
            throw new ExchangeModelMapperException("[ Error when mapping response to single exchange log. ] " + e.getMessage());
        }
    }

	public static String mapCreateUnsentMessageResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
		try {
            validateResponse(message, correlationId);
            CreateUnsentMessageResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, CreateUnsentMessageResponse.class);
            return unmarshalledResponse.getUnsentMessageId();
        } catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when mapping response unsent message response. ]");
            throw new ExchangeModelMapperException("[ Error when mapping response unsent message response. ] " + e.getMessage());
        }
	}

	public static List<UnsentMessageType> mapResendMessageResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
		try {
            validateResponse(message, correlationId);
            ResendMessageResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, ResendMessageResponse.class);
            return unmarshalledResponse.getResentMessage();
        } catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when mapping to resend response. ]");
            throw new ExchangeModelMapperException("[ Error when mapping to resend response. ] " + e.getMessage());
        }
	}

	public static ExchangeLogStatusType mapGetLogStatusHistoryResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
		try {
            validateResponse(message, correlationId);
            GetLogStatusHistoryResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(message, GetLogStatusHistoryResponse.class);
            return unmarshalledResponse.getStatus();
        } catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when mapping to log status history response. ]");
            throw new ExchangeModelMapperException("[ Error when mapping to log status history response. ] " + e.getMessage());
        }
	}


    public static PollStatus mapSetPollStatusResponse(TextMessage message, String correlationId) throws ExchangeModelMapperException {
        try {
            validateResponse(message, correlationId);
            SetPollStatusResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetPollStatusResponse.class);
            return response.getExchangeLog();
        } catch (JMSException | ExchangeValidationException | ExchangeModelMarshallException e) {
            LOG.error("[ Error when mapping response to update log status response ]");
            throw new ExchangeModelMapperException("[ Error when mapping response to update log status response ]" + e.getMessage());
        }
    }

    public static String createSetPollStatusResponse(ExchangeLogType log) throws ExchangeModelMarshallException {
        SetPollStatusResponse response = new SetPollStatusResponse();
        PollStatus pollStatus = new PollStatus();
        pollStatus.setStatus(log.getStatus());
        pollStatus.setExchangeLogGuid(log.getGuid());
        pollStatus.setPollGuid(log.getTypeRef().getRefGuid());
        response.setExchangeLog(pollStatus);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
}
