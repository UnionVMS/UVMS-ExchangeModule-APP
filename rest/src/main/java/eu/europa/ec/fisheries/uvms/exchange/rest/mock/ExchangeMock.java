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
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingLog;

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
		log.setFrom("MOCK from");
		log.setId("MOCK id");
		log.setIncoming(false);
		log.setRecipient("MOCK recipient");
		log.setRule("MOCK rule");
		log.setSource("MOCK source");
		log.setStatus("MOCK status");
		return log;
	}

	public static String mockRawData(String id) {
		return "mock/rawData/" + id;
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
		log.setFrom("MOCK from");
		log.setId("MOCK id");
		return log;
	}

	public static boolean send(List<String> messageIdList) {
		return true;
	}

	public static Map<String, List> mockConfiguration() {
		Map<String, List> configuration = new HashMap<>();
		List<ExchangeLogStatusType> statusList = new ArrayList<>();
		statusList.addAll(Arrays.asList(ExchangeLogStatusType.values()));
		configuration.put("STATUS", statusList);
		List<String> recipientList = new ArrayList<>();
		recipientList.add("DNK");
		recipientList.add("FIN");
		configuration.put("RECIPIENT", recipientList);
		return configuration;
	}

}
