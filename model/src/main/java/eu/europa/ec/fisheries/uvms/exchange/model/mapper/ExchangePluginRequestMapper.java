/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.PingRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StartRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StopRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

/**
 *
 * @author jojoha
 */
public class ExchangePluginRequestMapper {

	public static String createSetReportRequest(ReportType reportType) throws ExchangeModelMarshallException {
		SetReportRequest request = new SetReportRequest();
		request.setMethod(ExchangePluginMethod.SET_REPORT);
		request.setReport(reportType);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}
	
	public static String createSetCommandRequest(CommandType commandType) throws ExchangeModelMarshallException {
		SetCommandRequest request = new SetCommandRequest();
		request.setMethod(ExchangePluginMethod.SET_COMMAND);
		request.setCommand(commandType);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}

	public static String createSetConfigRequest(SettingListType settingList) throws ExchangeModelMarshallException {
		SetConfigRequest request = new SetConfigRequest();
		request.setMethod(ExchangePluginMethod.SET_CONFIG);
		request.setConfigurations(settingList);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}

	public static String createPingRequest() throws ExchangeModelMarshallException {
		PingRequest request = new PingRequest();
		request.setMethod(ExchangePluginMethod.PING);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}

	public static String createStartRequest() throws ExchangeModelMarshallException {
		StartRequest request = new StartRequest();
		request.setMethod(ExchangePluginMethod.START);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}
	
	public static String createStopRequest() throws ExchangeModelMarshallException {
		StopRequest request = new StopRequest();
		request.setMethod(ExchangePluginMethod.STOP);
		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}
}
