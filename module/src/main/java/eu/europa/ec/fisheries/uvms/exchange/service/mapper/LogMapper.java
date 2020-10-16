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
package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import org.slf4j.MDC;

import java.util.*;
import java.time.Instant;

public class LogMapper {

    private LogMapper(){
        // hide implicit constructor
    }

    public static ExchangeLog toNewEntity(ExchangeLogType log, String username) {
        ExchangeLog entity = new ExchangeLog();

        switch (log.getType()) {
            case RECEIVE_MOVEMENT:
                entity = toReceiveMovementEntity(log);
                break;
            case SEND_MOVEMENT:
                entity = toSendMovementEntity(log);
                break;
            case SEND_POLL:
                entity = toSendPollEntity(log);
                break;
            case SEND_EMAIL:
                entity = toSendEmailEntity(log);
                break;
            case RECEIVE_SALES_REPORT:
            case RECEIVE_SALES_QUERY:
            case RECEIVE_SALES_RESPONSE:
            case RECEIVE_FLUX_RESPONSE_MSG:
            case SEND_SALES_REPORT:
            case SEND_SALES_RESPONSE:
            case RCV_FLUX_FA_REPORT_MSG:
            case RECEIVE_FA_QUERY_MSG:
            case SEND_FA_QUERY_MSG:
            case SEND_FLUX_FA_REPORT_MSG:
            case SEND_FLUX_RESPONSE_MSG:
                entity.setSource(log.getSource());
                break;
            default:
                break;
        }

        if (username == null) {
            username = "SYSTEM";
        }

        entity.setOn(log.getOn());
        entity.setTodt(log.getTodt());
        entity.setTo(log.getTo());
        entity.setDf(log.getDf());
        //entity.setGuid(log.getGuid());    //should you really set a primary id somewhere other then the db/entity class?

        if (log.getTypeRef() != null) {
            entity.setTypeRefGuid( (log.getTypeRef().getRefGuid() == null) ? null : UUID.fromString(log.getTypeRef().getRefGuid()));
            entity.setTypeRefType(log.getTypeRef().getType());
            entity.setTypeRefMessage(log.getTypeRef().getMessage());
        }

        entity.setDateReceived(log.getDateRecieved().toInstant());
        entity.setSenderReceiver(log.getSenderReceiver());

        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.ISSUED;
        if (log.getStatus() != null) {
            status = log.getStatus();
        }

        List<ExchangeLogStatus> statusHistory = new ArrayList<>();
        ExchangeLogStatus statusLog = toNewStatusEntity(entity, status, username);
        statusHistory.add(statusLog);

        entity.setStatus(status);

        if (entity.getTransferIncoming() == null) {
            entity.setTransferIncoming(log.isIncoming());
        }

        entity.setStatusHistory(statusHistory);
        entity.setUpdatedBy(username);
        entity.setUpdateTime(Instant.now());
        entity.setType(log.getType());
        entity.setDestination(log.getDestination());
        entity.setMdcRequestId(MDC.get("requestId"));
        return entity;
    }

    private static ExchangeLog toReceiveMovementEntity(ExchangeLogType type) {
        ExchangeLog entity = new ExchangeLog();
        entity.setSource(type.getSource());
        entity.setTransferIncoming(true);
        return entity;
    }

    private static ExchangeLog toSendMovementEntity(ExchangeLogType log) {
        SendMovementType type = (SendMovementType) log;
        ExchangeLog entity = new ExchangeLog();
        entity.setFwdDate(type.getFwdDate().toInstant());
        entity.setFwdRule(type.getFwdRule());
        entity.setRecipient(type.getRecipient());
        entity.setTransferIncoming(false);
        return entity;
    }

    private static ExchangeLog toSendPollEntity(ExchangeLogType log) {
        SendPollType type = (SendPollType) log;
        ExchangeLog entity = new ExchangeLog();
        entity.setTransferIncoming(false);
        entity.setRecipient(type.getRecipient());
        entity.setFwdDate(type.getFwdDate().toInstant());
        return entity;
    }

    private static ExchangeLog toSendEmailEntity(ExchangeLogType log) {
        SendEmailType type = (SendEmailType) log;
        ExchangeLog entity = new ExchangeLog();
        entity.setTransferIncoming(false);
        entity.setFwdRule(type.getFwdRule());
        entity.setRecipient(type.getRecipient());
        entity.setFwdDate(type.getFwdDate().toInstant());
        return entity;
    }

    public static ExchangeLogType toModel(ExchangeLog entity) {
        ExchangeLogType model = new ExchangeLogType();
        LogType logType = entity.getType();


        if (logType.equals(LogType.RECEIVE_MOVEMENT)) {
            ReceiveMovementType type = new ReceiveMovementType();
            type.setSource(entity.getSource());
            type.setRecipient(entity.getRecipient());
            logType = LogType.RECEIVE_MOVEMENT;
            model = type;
        } else if (logType.equals(LogType.SEND_MOVEMENT)) {
            SendMovementType type = new SendMovementType();
            type.setFwdDate(Date.from(entity.getFwdDate()));
            type.setFwdRule(entity.getFwdRule());
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_MOVEMENT;
            model = type;
        } else if (logType.equals(LogType.SEND_POLL)) {
            SendPollType type = new SendPollType();
            type.setFwdDate(Date.from(entity.getFwdDate()));
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_POLL;
            model = type;
        } else if (logType.equals(LogType.SEND_EMAIL)) {
            SendEmailType type = new SendEmailType();
            type.setFwdRule(entity.getFwdRule());
            type.setFwdDate(Date.from(entity.getFwdDate()));
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_EMAIL;
            model = type;
        }

        model.setDateRecieved(Date.from(entity.getDateReceived()));
        model.setGuid(entity.getId().toString());
        model.setSenderReceiver(entity.getSenderReceiver());
        model.setIncoming(entity.getTransferIncoming());
        model.setStatus(entity.getStatus());
        model.setDestination(entity.getDestination());
        model.setType(logType);
        model.setSource(entity.getSource());
        model.setTypeRefType(entity.getTypeRefType());
        model.setDf(entity.getDf());
        model.setTodt(entity.getTodt());
        model.setTo(entity.getTo());
        model.setOn(entity.getOn());
        model.setBusinessModuleExceptionMessage(entity.getBusinessError());

        if (entity.getTypeRefType() != null) {
            LogRefType logRefType = new LogRefType();
            logRefType.setRefGuid( (entity.getTypeRefGuid() == null ? null : entity.getTypeRefGuid().toString()) );
            logRefType.setType(entity.getTypeRefType());
            logRefType.setMessage(entity.getTypeRefMessage());
            model.setTypeRef(logRefType);
        }

        return model;
    }

    public static ExchangeLogStatus toNewStatusEntity(ExchangeLog parent, ExchangeLogStatusTypeType status, String username) {
        ExchangeLogStatus entity = new ExchangeLogStatus();
        entity.setLog(parent);
        entity.setStatus(status);
        entity.setStatusTimestamp(Instant.now());
        entity.setUpdatedBy(username);
        entity.setUpdateTime(Instant.now());
        return entity;
    }


    public static ExchangeLogStatusType toStatusModel(ExchangeLog exchangeLog) {
        if(exchangeLog == null){
            return null;
        }
        ExchangeLogStatusType model = new ExchangeLogStatusType();
        if (exchangeLog.getType().equals(LogType.SEND_POLL)) {
            model.setIdentifier(exchangeLog.getRecipient());
            model.setRefMessage(exchangeLog.getTypeRefMessage());
        }
        model.setGuid(exchangeLog.getId().toString());

        if (exchangeLog.getTypeRefType() != null) {
            LogRefType logRefType = new LogRefType();
            logRefType.setRefGuid( (exchangeLog.getTypeRefGuid() == null) ? null : exchangeLog.getTypeRefGuid().toString());
            logRefType.setType(exchangeLog.getTypeRefType());
            logRefType.setMessage(exchangeLog.getTypeRefMessage());
            model.setTypeRef(logRefType);
        }

        if (exchangeLog.getRelatedRefType() != null) {
            LogRefType relatedRefType = new LogRefType();
            relatedRefType.setRefGuid(exchangeLog.getRelatedRefGuid().toString());
            relatedRefType.setType(exchangeLog.getRelatedRefType());
            model.setRelatedLogData(relatedRefType);
        }

        if (exchangeLog.getStatusHistory() != null) {
            List<ExchangeLogStatusHistoryType> historyModelList = new ArrayList<>();
            for (ExchangeLogStatus history : exchangeLog.getStatusHistory()) {
                ExchangeLogStatusHistoryType historyModel = new ExchangeLogStatusHistoryType();
                historyModel.setStatus(history.getStatus());
                historyModel.setTimestamp(Date.from(history.getStatusTimestamp()));
                historyModelList.add(historyModel);
            }
            historyModelList.sort(Comparator.comparing(ExchangeLogStatusHistoryType::getTimestamp).reversed());
            model.getHistory().addAll(historyModelList);
        }
        return model;
    }

}