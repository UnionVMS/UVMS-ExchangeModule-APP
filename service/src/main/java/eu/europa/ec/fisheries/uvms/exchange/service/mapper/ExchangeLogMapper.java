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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ReceiveMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendEmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendPollType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypeProperty;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypePropertyKey;
import eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

public class ExchangeLogMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeLogMapper.class);

    public static ExchangeLog getReceivedMovementExchangeLog(SetReportMovementType request, String typeRefGuid, String typeRefType,String username) throws ExchangeLogException {
        if (request == null) {
            throw new ExchangeLogException("No request");
        }
        ExchangeLog log = new ExchangeLog();
        log.setDateReceived(request.getTimestamp().toInstant());
        log.setType(LogType.PROCESSED_MOVEMENT);

        log.setTypeRefGuid(UUID.fromString(typeRefGuid));
        log.setTypeRefType(TypeRefType.MOVEMENT_RESPONSE);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setSenderReceiver(getSenderReceiver(request.getMovement(), request.getPluginType(), request.getPluginName(), username));
        if (request.getMovement().getSource() != null) {
            log.setSource(request.getMovement().getSource().name());
        } else {
            log.setSource(request.getPluginType().name());
        }
        log.setRecipient(getRecipient(request.getMovement(), request.getPluginType()));
        return log;
    }

    private static String getSenderReceiver(MovementBaseType movement, PluginType pluginType, String pluginName, String username) throws ExchangeLogException {
        if (movement == null) {
            throw new ExchangeLogException("No movement");
        }
        if (pluginType == null) {
            throw new ExchangeLogException("No plugin type");
        }
        String senderReceiver = null;
        switch (pluginType) {
            case MANUAL:
                senderReceiver = username!=null ? username : "Unknown";
                break;
            case SATELLITE_RECEIVER:
            case FLUX:
            case NAF:
                senderReceiver = pluginType.name();
                break;
            case EMAIL:
            case OTHER:
                senderReceiver = pluginName;
                break;
        }
        return senderReceiver;
    }

    private static String getSenderReceiver(AssetId assetId) throws ExchangeLogException {
        if (assetId == null) {
            throw new ExchangeLogException("No asset");
        }
        if (assetId.getAssetIdList() == null) {
            throw new ExchangeLogException("No asset id");
        }
        String senderReceiver = null;
        for (AssetIdList idList : assetId.getAssetIdList()) {
            switch (idList.getIdType()) {
                case IRCS:
                    senderReceiver = idList.getValue();
                    return senderReceiver;
                case CFR:
                case GUID:
                case ID:
                case IMO:
                case MMSI:
                default:
                    senderReceiver = idList.getValue();
            }
        }
        if (senderReceiver != null) {
            return senderReceiver;
        }
        throw new ExchangeLogException("No asset id value");
    }

    private static String getRecipientOfPoll(List<KeyValueType> pollReceiverList) throws ExchangeLogException {
        if (pollReceiverList == null || pollReceiverList.isEmpty()) {
            throw new ExchangeLogException("No poll receiver list");
        }
        String dnid = null;
        String memberNumber = null;
        String satelliteNumber = null;
        String les = null;
        for (KeyValueType pollReceiver : pollReceiverList) {
            if (IdType.DNID.name().equalsIgnoreCase(pollReceiver.getKey())) {
                dnid = pollReceiver.getValue();
            } else if (IdType.MEMBER_NUMBER.name().equalsIgnoreCase(pollReceiver.getKey())) {
                memberNumber = pollReceiver.getValue();
            } else if (IdType.SERIAL_NUMBER.name().equalsIgnoreCase(pollReceiver.getKey())) {
                satelliteNumber = pollReceiver.getValue();
            } else if (IdType.LES.name().equalsIgnoreCase(pollReceiver.getKey())) {
                les = pollReceiver.getValue();
            }
        }
        if (dnid != null && memberNumber != null) {
            return dnid + "." + memberNumber;
        } else if (satelliteNumber != null) {
            return satelliteNumber;
        } else if (les != null) {
            return les;
        }
        throw new ExchangeLogException("No receiver of poll");
    }

    public static String getSendMovementSenderReceiver(SendMovementToPluginType sendReport) {
        String senderReceiver = "";
        if (sendReport.getPluginName() != null && !sendReport.getPluginName().isEmpty()) {
            senderReceiver = sendReport.getPluginName();
        }
        if (sendReport.getPluginType() != null) {
            senderReceiver = sendReport.getPluginType().name();
        }
        if (sendReport.getMovement().getIrcs() != null) {
            senderReceiver = sendReport.getMovement().getIrcs();
        }
        if (sendReport.getIrcs() != null) {
            senderReceiver = sendReport.getIrcs();
        }
        try {
            senderReceiver = getSenderReceiver(sendReport.getMovement(), sendReport.getPluginType(), sendReport.getPluginName(), null);
        } catch (ExchangeLogException e) {
            LOG.debug("Report sent to plugin couldn't map to senderReceiver");
        }
        return senderReceiver;
    }

    public static ExchangeLog getSendMovementExchangeLog(SendMovementToPluginType sendReport) throws ExchangeLogException {
        if (sendReport == null) {
            throw new ExchangeLogException("No request");
        }
        ExchangeLog log = new ExchangeLog();
        log.setDateReceived(sendReport.getTimestamp().toInstant());
        log.setType(LogType.SEND_MOVEMENT);

        log.setTypeRefType(TypeRefType.MOVEMENT);
        log.setTypeRefGuid(UUID.fromString(sendReport.getMovement().getGuid()));
        String senderReceiver = getSendMovementSenderReceiver(sendReport);
        log.setSenderReceiver(senderReceiver);

        //TODO send fwdDate, fwdRule and recipient from Rules
        log.setFwdDate(sendReport.getFwdDate().toInstant());
        log.setFwdRule(sendReport.getFwdRule());
        log.setRecipient(sendReport.getRecipient());

        log.setUpdatedBy("SYSTEM");
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);

        return log;
    }

    public static ExchangeLog getSendCommandExchangeLog(CommandType command, String username) throws ExchangeLogException {
        if (command == null) {
            throw new ExchangeLogException("No command");
        }
        if (command.getCommand() == null) {
            throw new ExchangeLogException("No command type");
        }
        switch (command.getCommand()) {
            case EMAIL:
                return getSendEmailExchangeLog(command, username);
            case POLL:
                return getPollExchangeLog(command, username);
        }
        throw new ExchangeLogException("Not implemented command type");
    }

    private static ExchangeLog getSendEmailExchangeLog(CommandType command, String username) throws ExchangeLogException {
        if (command.getEmail() == null) {
            throw new ExchangeLogException("No email");
        }
        ExchangeLog log = new ExchangeLog();
        log.setType(LogType.SEND_EMAIL);
        log.setDateReceived(command.getTimestamp().toInstant());
        log.setSenderReceiver("SYSTEM");
        log.setRecipient(command.getEmail().getTo());
        log.setFwdRule(command.getFwdRule());
        log.setFwdDate(command.getTimestamp().toInstant());

        log.setUpdatedBy(username);
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);
        return addStatusHistory(log);
    }

    private static ExchangeLog getPollExchangeLog(CommandType command, String username) throws ExchangeLogException {
        if (command.getPoll() == null) {
            throw new ExchangeLogException("No poll");
        }

        //TODO fix in mobileterminal
        ExchangeLog log = new ExchangeLog();
        log.setType(LogType.SEND_POLL);
        log.setDateReceived(command.getTimestamp().toInstant());
        log.setRecipient(getRecipientOfPoll(command.getPoll().getPollReceiver()));
        log.setSenderReceiver("System");
        log.setFwdDate(command.getTimestamp().toInstant());
        log.setUpdatedBy(username);

        log.setTypeRefType(TypeRefType.POLL);
        log.setTypeRefGuid(UUID.fromString(command.getPoll().getPollId()));
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);
        return addStatusHistory(log);
    }

    public static ExchangeLog addStatusHistory(ExchangeLog log){
        List<ExchangeLogStatus> statusHistory = new ArrayList<>();
        ExchangeLogStatus statusLog = new ExchangeLogStatus();
        statusLog.setLog(log);
        statusLog.setStatus(log.getStatus() == null ? ExchangeLogStatusTypeType.ISSUED : log.getStatus());
        statusLog.setStatusTimestamp(Instant.now());
        statusLog.setUpdatedBy(log.getUpdatedBy());
        statusLog.setUpdateTime(Instant.now());
        statusHistory.add(statusLog);
        log.setStatusHistory(statusHistory);

        return log;
    }

    public static List<UnsentMessageTypeProperty> getUnsentMessageProperties(SendMovementToPluginType sendReport) {

        List<UnsentMessageTypeProperty> unsentMessageTypeProperties = new ArrayList<>();
        UnsentMessageTypeProperty propertyAssetName = new UnsentMessageTypeProperty();
        UnsentMessageTypeProperty propertyIrcs = new UnsentMessageTypeProperty();
        UnsentMessageTypeProperty propertyLong = new UnsentMessageTypeProperty();
        UnsentMessageTypeProperty propertyLat = new UnsentMessageTypeProperty();
        UnsentMessageTypeProperty propertyPositionTime = new UnsentMessageTypeProperty();

        propertyAssetName.setKey(UnsentMessageTypePropertyKey.ASSET_NAME);
        propertyAssetName.setValue(sendReport.getAssetName());
        propertyIrcs.setKey(UnsentMessageTypePropertyKey.IRCS);
        propertyIrcs.setValue(sendReport.getIrcs());
        propertyLong.setKey(UnsentMessageTypePropertyKey.LONGITUDE);
        propertyLong.setValue(sendReport.getMovement().getPosition().getLongitude().toString());
        propertyLat.setKey(UnsentMessageTypePropertyKey.LATITUDE);
        propertyLat.setValue(sendReport.getMovement().getPosition().getLatitude().toString());
        propertyPositionTime.setKey(UnsentMessageTypePropertyKey.POSITION_TIME);
        propertyPositionTime.setValue(sendReport.getMovement().getPositionTime().toString());

        unsentMessageTypeProperties.add(propertyAssetName);
        unsentMessageTypeProperties.add(propertyIrcs);
        unsentMessageTypeProperties.add(propertyLong);
        unsentMessageTypeProperties.add(propertyLat);
        unsentMessageTypeProperties.add(propertyPositionTime);
        return unsentMessageTypeProperties;
    }

    public static List<UnsentMessageTypeProperty> getPropertiesForPoll(PollType poll, String assetName) throws ExchangeLogException {
        List<UnsentMessageTypeProperty> unsentMessageTypeProperties = new ArrayList<>();
        UnsentMessageTypeProperty propertyAssetName = new UnsentMessageTypeProperty();
        propertyAssetName.setKey(UnsentMessageTypePropertyKey.ASSET_NAME);
        propertyAssetName.setValue(assetName);
        UnsentMessageTypeProperty pollType = new UnsentMessageTypeProperty();
        pollType.setKey(UnsentMessageTypePropertyKey.POLL_TYPE);
        pollType.setValue(poll.getPollTypeType().name());
        unsentMessageTypeProperties.add(propertyAssetName);
        unsentMessageTypeProperties.add(pollType);
        return unsentMessageTypeProperties;
    }

    public static List<UnsentMessageTypeProperty> getPropertiesForEmail(EmailType email) throws ExchangeLogException {
        List<UnsentMessageTypeProperty> unsentMessageTypeProperties = new ArrayList<>();
        UnsentMessageTypeProperty propertyEmail = new UnsentMessageTypeProperty();
        propertyEmail.setKey(UnsentMessageTypePropertyKey.EMAIL);
        propertyEmail.setValue(email.getTo());
        unsentMessageTypeProperties.add(propertyEmail);
        return unsentMessageTypeProperties;
    }

    public static String getConnectId(PollType poll) {
        List<KeyValueType> pollReceiver = poll.getPollReceiver();
        for (KeyValueType keyValueType : pollReceiver) {
            if ("CONNECT_ID".equalsIgnoreCase(keyValueType.getKey())) {
                return keyValueType.getValue();
            }
        }
        return null;
    }

    private static String getRecipient(MovementBaseType movementBaseType, PluginType pluginType) throws ExchangeLogException {
        if(movementBaseType== null){
            throw new ExchangeLogException("Movement is empty");
        }
        if(pluginType == null){
            throw new ExchangeLogException("PluginType is empty");
        }
        String recipient;
        switch (pluginType){
            case MANUAL:
            case FLUX:
            case NAF:
                recipient = movementBaseType.getFlagState();
                break;
            default:
                recipient = "UNKNOWN";
                break;
        }
        return recipient;
    }
}