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
import eu.europa.ec.fisheries.schema.exchange.v1.SendEmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendPollType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypeProperty;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.LogTypeLabel;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogData;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingLog;

/**
 **/
public class ExchangeLogMapper {

    public static ListQueryResponse mapToQueryResponse(GetLogListByQueryResponse response) {

        ListQueryResponse dto = new ListQueryResponse();

        dto.setCurrentPage(response.getCurrentPage());
        dto.setTotalNumberOfPages(response.getTotalNumberOfPages());

        for (ExchangeLogType log : response.getExchangeLog()) {
            dto.getLogList().add(mapToExchangeLogDto(log));
        }

        return dto;

    }

    public static ExchangeLogDto mapToExchangeLogDto(ExchangeLogType log) {
    	ExchangeLogDto dto = new ExchangeLogDto();
        Date dateFwd;
    	switch(log.getType()) {
            case RECEIVE_MOVEMENT:
                dto.setType(LogTypeLabel.RECEIVED_MOVEMENT.toString());
                dto.setSource(log.getSource());
                dto.setRecipient(((ReceiveMovementType)log).getRecipient());
                break;
            case SEND_MOVEMENT:
                dto.setType(LogTypeLabel.SENT_MOVEMENT.toString());
                SendMovementType sendLog = (SendMovementType)log;
                dateFwd = sendLog.getFwdDate();
                dto.setDateFwd(DateUtils.dateToString(dateFwd));
                dto.setRule(sendLog.getFwdRule());
                dto.setRecipient(sendLog.getRecipient());
                break;
            case SEND_EMAIL:
                SendEmailType sendEmail = (SendEmailType)log;
                dateFwd = sendEmail.getFwdDate();
                dto.setType(LogTypeLabel.SENT_EMAIL.toString());
                dto.setRecipient(sendEmail.getRecipient());
                dto.setRule(sendEmail.getFwdRule());
                dto.setDateFwd(DateUtils.dateToString(dateFwd));
                break;
            case SEND_POLL:
                SendPollType sendPoll = (SendPollType)log;
                dto.setType(LogTypeLabel.SENT_POLL.toString());
                dto.setRule(sendPoll.getFwdRule());
                dto.setRecipient(sendPoll.getRecipient());
                break;
            case SEND_SALES_REPORT:
                dto.setType(LogTypeLabel.SENT_SALES_REPORT.toString());
                break;
            case SEND_SALES_RESPONSE:
                dto.setType(LogTypeLabel.SENT_SALES_RESPONSE.toString());
                break;
            case RECEIVE_SALES_QUERY:
                dto.setType(LogTypeLabel.RECEIVED_SALES_QUERY.toString());
                break;
            case RECEIVE_SALES_REPORT:
                dto.setType(LogTypeLabel.RECEIVED_SALES_REPORT.toString());
                break;
            case RECEIVE_SALES_RESPONSE:
                dto.setType(LogTypeLabel.RECEIVED_SALES_RESPONSE.toString());
                break;
            default:
                if(log.getType() !=null) {
                    dto.setType(log.getType().toString());
                }
                break;
    	}
    	
    	Date dateReceived = log.getDateRecieved();
    	dto.setDateRecieved(DateUtils.dateToString(dateReceived));
    	dto.setId(log.getGuid());
        dto.setDf(log.getDf());

        dto.setIncoming(log.isIncoming());
        if(log.getTypeRefType()!=null) {
            dto.setTypeRefType(log.getTypeRefType().toString());
        }

        if(dto.getSource()==null){
            dto.setSource(log.getSource());
        }

    	if(log.getTypeRef() != null) {
    		ExchangeLogData logData = new ExchangeLogData();
    		logData.setGuid(log.getTypeRef().getRefGuid());
    		logData.setType(log.getTypeRef().getType());
            logData.setRefMessage(log.getTypeRef().getMessage());
            dto.setLogData(logData);
    	}
    	dto.setSenderRecipient(log.getSenderReceiver());
    	dto.setStatus(log.getStatus().name());
    	dto.setResponseStatus(log.getResponseStatus() !=null ? log.getResponseStatus().name() : null);
        dto.setRelatedLogData(log.getRelatedLogData());
        dto.setDf(log.getDf());
        dto.setTo(log.getTo());
        dto.setOn(log.getOn());

        dto.setTodt(log.getTodt());
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
			Date dateRecieved = message.getDateReceived();
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