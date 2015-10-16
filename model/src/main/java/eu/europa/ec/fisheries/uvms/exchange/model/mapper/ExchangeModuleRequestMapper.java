package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import javax.jms.TextMessage;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryMethod;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

public class ExchangeModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeModuleRequestMapper.class);

    public static String mapCreatePollRequest(CommandType command) throws ExchangeModelMarshallException {
        SetCommandRequest request = new SetCommandRequest();
        request.setMethod(ExchangeModuleMethod.SET_COMMAND);
        request.setCommand(command);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createRegisterServiceRequest(ServiceType serviceType, String messageSelector, CapabilityListType capabilityList, SettingListType settingList) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = new RegisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.REGISTER_SERVICE);
        request.setResponseTopicMessageSelector(messageSelector);
        request.setService(serviceType);
		request.setCapabilityList(capabilityList);
		request.setSettingList(settingList);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static String createUnregisterServiceRequest(ServiceType serviceType, String messageSelector) throws ExchangeModelMarshallException {
        UnregisterServiceRequest request = new UnregisterServiceRequest();
        request.setMethod(ExchangeRegistryMethod.UNREGISTER_SERVICE);
        request.setResponseTopicMessageSelector(messageSelector);
        request.setService(serviceType);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }

    public static ServiceType mapToServiceTypeFromRequest(TextMessage textMessage) throws ExchangeModelMarshallException {
        RegisterServiceRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
        return request.getService();
    }

    public static String createSetMovementReportRequest(SetReportMovementType reportType) throws ExchangeModelMarshallException {
    	SetMovementReportRequest request = new SetMovementReportRequest();
    	request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
		request.setRequest(reportType);
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String createSendReportToPlugin(PluginType type, MovementType payload) throws ExchangeModelMapperException {
    	SendMovementToPluginRequest request = new SendMovementToPluginRequest();
    	request.setMethod(ExchangeModuleMethod.SEND_REPORT_TO_PLUGIN);
		SendMovementToPluginType report = new SendMovementToPluginType();
    	try {
			report.setTimestamp(DateUtils.getCurrentDate());
		} catch (DatatypeConfigurationException e) {
			throw new ExchangeModelMapperException("Couldn't set current timestamp for message");
		}
    	report.setMovement(payload);
    	report.setPluginType(type);
		request.setReport(report);
		
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String createSetCommandSendPollRequest(String pluginName, PollType poll) throws ExchangeModelMapperException  {
    	SetCommandRequest request = createSetCommandRequest(pluginName, CommandTypeType.POLL);
    	request.getCommand().setPoll(poll);
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    public static String createSetCommandSendEmailRequest(String pluginName, EmailType email) throws ExchangeModelMapperException {
    	SetCommandRequest request = createSetCommandRequest(pluginName, CommandTypeType.EMAIL);
    	request.getCommand().setEmail(email);
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
    
    private static SetCommandRequest createSetCommandRequest(String pluginName, CommandTypeType type) throws ExchangeModelMapperException {
    	SetCommandRequest request = new SetCommandRequest();
    	request.setMethod(ExchangeModuleMethod.SET_COMMAND);
    	CommandType commandType = new CommandType();
    	try {
    		commandType.setTimestamp(DateUtils.getCurrentDate());
    	} catch (DatatypeConfigurationException e) {
    		throw new ExchangeModelMapperException("Couldn't set current timestamp for message");
    	}
    	commandType.setCommand(type);
    	commandType.setPluginName(pluginName);
    	
		request.setCommand(commandType);
    	return request;
    }

    public static String createGetServiceListRequest() throws ExchangeModelMapperException {
    	GetServiceListRequest request = new GetServiceListRequest();
    	request.setMethod(ExchangeModuleMethod.LIST_SERVICES);
    	return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
}
