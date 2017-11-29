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

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LogMapper {

    final static Logger LOG = LoggerFactory.getLogger(LogMapper.class);

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
        }

        if (username == null) {
            username = "SYSTEM";
        }

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
        statusLog.setStatusTimestamp(DateUtils.nowUTC().toDate());
        statusLog.setUpdatedBy(username);
        statusLog.setUpdateTime(DateUtils.nowUTC().toDate());
        statusHistory.add(statusLog);

        entity.setStatus(status);

        if (entity.getTransferIncoming() == null) {
            entity.setTransferIncoming(log.isIncoming());
        }

        entity.setStatusHistory(statusHistory);
        entity.setUpdatedBy(username);
        entity.setUpdateTime(DateUtils.nowUTC().toDate());
        entity.setType(log.getType());
        entity.setDestination(log.getDestination());

        return entity;
    }

    private static ExchangeLog toReceiveMovementEntity(ExchangeLogType log) {
        ReceiveMovementType type = (ReceiveMovementType) log;
        ExchangeLog entity = new ExchangeLog();
        entity.setSource(type.getSource());
        entity.setTransferIncoming(true);
        entity.setRecipient(type.getRecipient());
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

        return model;
    }

    public static ExchangeLogStatus toNewStatusEntity(ExchangeLog parent, ExchangeLogStatusTypeType status, String username) {
        ExchangeLogStatus entity = new ExchangeLogStatus();
        entity.setLog(parent);
        entity.setStatus(status);
        entity.setStatusTimestamp(DateUtils.nowUTC().toDate());
        entity.setUpdatedBy(username);
        entity.setUpdateTime(DateUtils.nowUTC().toDate());
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