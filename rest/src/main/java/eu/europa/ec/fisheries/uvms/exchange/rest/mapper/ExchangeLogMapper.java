/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.LogTypeLabel;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.*;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.*;

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
        Date dateFwd = null;
    	switch(log.getType()) {
    	case RECEIVE_MOVEMENT:
    		dto.setType(LogTypeLabel.RECEIVED_MOVEMENT);
    		dto.setSource(((ReceiveMovementType)log).getSource());
			dto.setRecipient(((ReceiveMovementType)log).getRecipient());
    		break;
    	case SEND_MOVEMENT:
    		dto.setType(LogTypeLabel.SENT_MOVEMENT);
    		SendMovementType sendLog = (SendMovementType)log;
    		dateFwd = sendLog.getFwdDate().toGregorianCalendar().getTime();
    		dto.setDateFwd(DateUtils.dateToString(dateFwd));
    		dto.setRule(sendLog.getFwdRule());
    		dto.setRecipient(sendLog.getRecipient());
    		break;
    	case SEND_EMAIL:
			SendEmailType sendEmail = (SendEmailType)log;
            dateFwd = sendEmail.getFwdDate().toGregorianCalendar().getTime();
            dto.setType(LogTypeLabel.SENT_EMAIL);
			dto.setRecipient(sendEmail.getRecipient());
			dto.setRule(sendEmail.getFwdRule());
            dto.setDateFwd(DateUtils.dateToString(dateFwd));
    		break;
    	case SEND_POLL:
			SendPollType sendPoll = (SendPollType)log;
    		dto.setType(LogTypeLabel.SENT_POLL);
			dto.setRule(sendPoll.getFwdRule());
			dto.setRecipient(sendPoll.getRecipient());
    		break;
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
            groupLog.setPluginList(mapPluginTypeList(groupMap.get(recipient)));
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
            log.setProperties(mapProperties(message.getProperties()));
			sendingLog.add(log);
		}
		return sendingLog;
	}

    private static Map<String, String> mapProperties(List<UnsentMessageTypeProperty> properties) {
        Map<String, String> map = new HashMap<>();
        for (UnsentMessageTypeProperty property : properties){
            map.put(property.getKey().name(), property.getValue());
        }
        return map;
    }

    private static List<PluginType> mapPluginTypeList(List<UnsentMessageType> unsentMessageList){
        Map<String, List<UnsentMessageType>> groupMap = new HashMap<>();
        List<PluginType> pluginTypeList = new ArrayList<>();
        for(UnsentMessageType message : unsentMessageList) {
            List<UnsentMessageType> logList = groupMap.get(message.getSenderReceiver());
            if(logList == null) {
                logList = new ArrayList<>();
            }
            logList.add(message);
            groupMap.put(message.getSenderReceiver(), logList);
        }

        Iterator<String> iterator = groupMap.keySet().iterator();
        while (iterator.hasNext()){
            PluginType pluginType = new PluginType();
            String senderReceiver = iterator.next();
            pluginType.setName(senderReceiver);
            pluginType.setSendingLogList(mapSendingLog(groupMap.get(senderReceiver)));
            pluginTypeList.add(pluginType);
        }
        return pluginTypeList;
    }



}
