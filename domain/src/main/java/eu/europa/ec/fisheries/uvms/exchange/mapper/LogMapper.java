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
package eu.europa.ec.fisheries.uvms.exchange.mapper;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusHistoryType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ReceiveMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendEmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendPollType;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import org.slf4j.MDC;

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

        if (log.getTypeRef() != null) {
            entity.setTypeRefGuid(log.getTypeRef().getRefGuid());
            entity.setTypeRefType(log.getTypeRef().getType());
            entity.setTypeRefMessage(log.getTypeRef().getMessage());
        }

        entity.setDateReceived(log.getDateRecieved());
        entity.setSenderReceiver(log.getSenderReceiver());

        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.ISSUED;
        if (log.getStatus() != null) {
            status = log.getStatus();
        }

        List<ExchangeLogStatus> statusHistory = new ArrayList<>();
        ExchangeLogStatus statusLog = new ExchangeLogStatus();
        statusLog.setLog(entity);
        statusLog.setStatus(status);
        statusLog.setStatusTimestamp(eu.europa.ec.fisheries.uvms.commons.date.DateUtils.nowUTC().toDate());
        statusLog.setUpdatedBy(username);
        statusLog.setUpdateTime(eu.europa.ec.fisheries.uvms.commons.date.DateUtils.nowUTC().toDate());
        statusHistory.add(statusLog);

        entity.setStatus(status);

        if (entity.getTransferIncoming() == null) {
            entity.setTransferIncoming(log.isIncoming());
        }

        entity.setStatusHistory(statusHistory);
        entity.setUpdatedBy(username);
        entity.setUpdateTime(eu.europa.ec.fisheries.uvms.commons.date.DateUtils.nowUTC().toDate());
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
        entity.setFwdDate(type.getFwdDate());
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
        entity.setFwdDate(type.getFwdDate());
        return entity;
    }

    private static ExchangeLog toSendEmailEntity(ExchangeLogType log) {
        SendEmailType type = (SendEmailType) log;
        ExchangeLog entity = new ExchangeLog();
        entity.setTransferIncoming(false);
        entity.setFwdRule(type.getFwdRule());
        entity.setRecipient(type.getRecipient());
        entity.setFwdDate(type.getFwdDate());
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
            type.setFwdDate(entity.getFwdDate());
            type.setFwdRule(entity.getFwdRule());
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_MOVEMENT;
            model = type;
        } else if (logType.equals(LogType.SEND_POLL)) {
            SendPollType type = new SendPollType();
            type.setFwdDate(entity.getFwdDate());
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_POLL;
            model = type;
        } else if (logType.equals(LogType.SEND_EMAIL)) {
            SendEmailType type = new SendEmailType();
            type.setFwdRule(entity.getFwdRule());
            type.setFwdDate(entity.getFwdDate());
            type.setRecipient(entity.getRecipient());
            logType = LogType.SEND_EMAIL;
            model = type;
        }

        model.setDateRecieved(entity.getDateReceived());
        model.setGuid(entity.getGuid());
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
        
        if (entity.getTypeRefType() != null) {
            LogRefType logRefType = new LogRefType();
            logRefType.setRefGuid(entity.getTypeRefGuid());
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
        entity.setStatusTimestamp(eu.europa.ec.fisheries.uvms.commons.date.DateUtils.nowUTC().toDate());
        entity.setUpdatedBy(username);
        entity.setUpdateTime(eu.europa.ec.fisheries.uvms.commons.date.DateUtils.nowUTC().toDate());
        return entity;
    }


    public static ExchangeLogStatusType toStatusModel(ExchangeLog exchangeLog) {
        ExchangeLogStatusType model = new ExchangeLogStatusType();
        if (exchangeLog.getType().equals(LogType.SEND_POLL)) {
            model.setIdentifier(exchangeLog.getRecipient());
        }
        model.setGuid(exchangeLog.getGuid());

        if (exchangeLog.getTypeRefType() != null) {
            LogRefType logRefType = new LogRefType();
            logRefType.setRefGuid(exchangeLog.getTypeRefGuid());
            logRefType.setType(exchangeLog.getTypeRefType());
            logRefType.setMessage(exchangeLog.getTypeRefMessage());
            model.setTypeRef(logRefType);
        }

        if (exchangeLog.getStatusHistory() != null) {
            List<ExchangeLogStatusHistoryType> historyModelList = new ArrayList<>();
            for (ExchangeLogStatus history : exchangeLog.getStatusHistory()) {
                ExchangeLogStatusHistoryType historyModel = new ExchangeLogStatusHistoryType();
                historyModel.setStatus(history.getStatus());
                historyModel.setTimestamp(history.getStatusTimestamp());
                historyModelList.add(historyModel);
            }
            model.getHistory().addAll(historyModelList);
        }
        return model;
    }

}