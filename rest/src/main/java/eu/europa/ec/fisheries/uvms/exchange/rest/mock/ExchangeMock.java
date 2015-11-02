/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogData;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.StatusLog;

/**
 *
 * @author jojoha
 */
public class ExchangeMock {

	public static ListQueryResponse mockLogList(ExchangeListQuery query) {
		ListQueryResponse response = new ListQueryResponse();
		response.setCurrentPage(1);
		response.setTotalNumberOfPages(1);
		response.getLogs().addAll(mockExchangeLogList());
		return response;
	}

	private static List<ExchangeLog> mockExchangeLogList() {
		List<ExchangeLog> logList = new ArrayList<>();
		logList.add(mockExchangeLog());
		return logList;
	}

	public static ExchangeLog mockExchangeLog() {
		ExchangeLog log = new ExchangeLog();
		log.setDateFwd("MOCK DateFwd");
		log.setDateRecieved("MOCK dateReceived");
		log.setSenderRecipient("MOCK senderRecipient");
		log.setId("MOCK id");
		log.setIncoming(false);
		log.setRecipient("MOCK recipient");
		log.setRule("MOCK rule");
		log.setSource("MOCK source");
		log.setStatus("MOCK ExchangeLogStatusTypeType");
		log.setLogData(mockExchangeLogData());
		return log;
	}

	public static ExchangeLogData mockExchangeLogData() {
		ExchangeLogData logData = new ExchangeLogData();
		logData.setGuid("MOCK guid OF LogType");
		logData.setType(LogType.SEND_MOVEMENT);
		return logData;
	}

	public static List<SendingGroupLog> mockSendingList() {
		List<SendingGroupLog> groupLogList = new ArrayList<>();
		groupLogList.add(mockGroupLog("FIN"));
		groupLogList.add(mockGroupLog("DNK"));
		return groupLogList;
	}

	private static SendingGroupLog mockGroupLog(String recipient) {
		SendingGroupLog groupLog = new SendingGroupLog();
		groupLog.setRecipient(recipient);
		groupLog.setSendingLogList(mockSendingLogList(recipient));
		return groupLog;
	}

	private static List<SendingLog> mockSendingLogList(String recipient) {
		List<SendingLog> sendingLogList = new ArrayList<>();
		sendingLogList.add(mockSendingLog());
		sendingLogList.add(mockSendingLog());
		return sendingLogList;
	}

	private static SendingLog mockSendingLog() {
		SendingLog log = new SendingLog();
		log.setDateRecieved("MOCK dateReceived");
		log.setSenderRecipient("MOCK from");
		log.setMessageId("MOCK id");
		return log;
	}

	public static boolean send(List<String> messageIdList) {
		return true;
	}

	public static Map<String, List> mockConfiguration() {
		Map<String, List> configuration = new HashMap<>();
		List<ExchangeLogStatusTypeType> statusList = new ArrayList<>();
		statusList.addAll(Arrays.asList(ExchangeLogStatusTypeType.values()));
		configuration.put("STATUS", statusList);
		List<String> recipientList = new ArrayList<>();
		recipientList.add("DNK");
		recipientList.add("FIN");
		configuration.put("RECIPIENT", recipientList);
		return configuration;
	}

	public static List<ExchangeLogStatus> mockPollStatusList(ExchangeListQuery query) {
		List<ExchangeLogStatus> retList = new ArrayList<>();
		retList.add(mockExchangeLogStatus());
		return retList;
	}
	
	private static ExchangeLogStatus mockExchangeLogStatus() {
		ExchangeLogStatus logStatus = new ExchangeLogStatus();
		logStatus.setTypeRefGuid("Mock Poll GUID");
		List<StatusLog> statusLog = new ArrayList<>();
		StatusLog issued = new StatusLog();
		issued.setTimestamp("2015-02-02 15:00:00 +01:00");
		issued.setStatus(ExchangeLogStatusTypeType.ISSUED);
		statusLog.add(issued);
		StatusLog transmitted = new StatusLog();
		transmitted.setTimestamp("2015-02-02 15:30:00 +01:00");
		transmitted.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
		statusLog.add(transmitted);
		logStatus.setStatusList(statusLog);
		return logStatus;
	}

}
