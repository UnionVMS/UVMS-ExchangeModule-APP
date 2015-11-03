/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ReceiveMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogData;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingLog;

/**
 *
 * @author jojoha
 */
public class ExchangeLogMapper {

    public static ListQueryResponse mapToQueryResponse(GetLogListByQueryResponse response) {

        ListQueryResponse dto = new ListQueryResponse();

        dto.setCurrentPage(response.getCurrentPage());
        dto.setTotalNumberOfPages(response.getTotalNumberOfPages());

        for (ExchangeLogType log : response.getExchangeLog()) {
            dto.getLogs().add(mapToExchangeLogDto(log));
        }

        return dto;

    }
    
    public static ExchangeLog mapToExchangeLogDto(ExchangeLogType log) {
    	ExchangeLog dto = new ExchangeLog();
    	switch(log.getType()) {
    	case RECEIVE_MOVEMENT:
    		dto.setSource(((ReceiveMovementType)log).getSource());
    		break;
    	case SEND_MOVEMENT:
    		SendMovementType sendLog = (SendMovementType)log;
    		Date dateFwd = sendLog.getFwdDate().toGregorianCalendar().getTime();
    		dto.setDateFwd(DateUtils.dateToString(dateFwd));
    		dto.setRule(sendLog.getFwdRule());
    		dto.setRecipient(sendLog.getRecipient());
    		break;
    	case SEND_EMAIL:
    	case SEND_POLL:
    	default:
    		break;
    	}
    	
    	Date dateReceived = log.getDateRecieved().toGregorianCalendar().getTime();
    	dto.setDateRecieved(DateUtils.dateToString(dateReceived));
    	dto.setId(log.getGuid());
    	dto.setIncoming(log.isIncoming());
    	if(log.getTypeRef() != null) {
    		ExchangeLogData logData = new ExchangeLogData();
    		logData.setGuid(log.getTypeRef().getRefGuid());
    		logData.setType(log.getTypeRef().getType());
    		dto.setLogData(logData);
    	}
    	dto.setSenderRecipient(log.getSenderReceiver());
    	dto.setStatus(log.getStatus().name());
        return dto;
    }

	public static List<SendingGroupLog> mapToSendingQueue(List<UnsentMessageType> unsentMessageList) {
		List<SendingGroupLog> sendingGroupList = new ArrayList<>();
		Map<String, List<UnsentMessageType>> groupMap = new HashMap<>();
		for(UnsentMessageType message : unsentMessageList) {
			List<UnsentMessageType> logList = groupMap.get(message.getRecipient());
			if(logList == null) {
				logList = new ArrayList<>();
			}
			logList.add(message);
			groupMap.put(message.getRecipient(), logList);
		}
		Iterator<String> itr = groupMap.keySet().iterator();
		while(itr.hasNext()) {
			SendingGroupLog groupLog = new SendingGroupLog();
			String recipient = itr.next();
			groupLog.setRecipient(recipient);
			groupLog.setSendingLogList(mapSendingLog(groupMap.get(recipient)));
			sendingGroupList.add(groupLog);
		}
		return sendingGroupList;
	}
	
	private static List<SendingLog> mapSendingLog(List<UnsentMessageType> messages) {
		List<SendingLog> sendingLog = new ArrayList<>();
		for(UnsentMessageType message : messages) {
			SendingLog log = new SendingLog();
			Date dateRecieved = message.getDateReceived().toGregorianCalendar().getTime();
			log.setDateRecieved(DateUtils.dateToString(dateRecieved));
			log.setMessageId(message.getMessageId());
			log.setSenderRecipient(message.getSenderReceiver());
			sendingLog.add(log);
		}
		return sendingLog;
	}
}
