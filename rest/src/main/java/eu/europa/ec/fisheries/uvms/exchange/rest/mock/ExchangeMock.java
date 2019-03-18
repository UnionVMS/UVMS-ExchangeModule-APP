/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mock;

import eu.europa.ec.fisheries.schema.exchange.v1.SourceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.*;

/**
 **/
public class ExchangeMock {

	public static ListQueryResponse mockLogList(ExchangeListQuery query) {
		ListQueryResponse response = new ListQueryResponse();
		response.setCurrentPage(1);
		response.setTotalNumberOfPages(1);
		response.getLogList().addAll(mockExchangeLogList());
		return response;
	}

	private static List<ExchangeLogDto> mockExchangeLogList() {
		List<ExchangeLogDto> logList = new ArrayList<>();
		logList.add(mockExchangeLog());
		return logList;
	}

	public static ExchangeLogDto mockExchangeLog() {
		ExchangeLogDto log = new ExchangeLogDto();
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
		logData.setType(TypeRefType.POLL);
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
        List<PluginType> pluginTypeList = new ArrayList<>();
		groupLog.setRecipient(recipient);
        PluginType pluginType = new PluginType();
        pluginType.setName("PluginMock");
        pluginType.setSendingLogList(mockSendingLogList(recipient));
        pluginTypeList.add(pluginType);
        groupLog.setPluginList(pluginTypeList);
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

		List<TypeRefType> refTypes = new ArrayList<>();
		refTypes.addAll(Arrays.asList(TypeRefType.values()));
		configuration.put("TYPE", refTypes);

		List<SourceType> sources = new ArrayList<>();
		sources.addAll(Arrays.asList(SourceType.values()));
		configuration.put("SOURCE", sources);

		return configuration;
	}
}