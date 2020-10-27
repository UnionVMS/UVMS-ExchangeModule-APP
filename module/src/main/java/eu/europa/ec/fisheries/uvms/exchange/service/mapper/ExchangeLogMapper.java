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

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypePropertyKey;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.unsent.UnsentMessageProperty;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExchangeLogMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeLogMapper.class);

    public static ExchangeLog getReceivedMovementExchangeLog(SetReportMovementType request, String typeRefGuid, String typeRefType, String username) {
        if (request == null) {
            throw new IllegalArgumentException("No request");
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

    private static String getSenderReceiver(MovementBaseType movement, PluginType pluginType, String pluginName, String username) {
        if (movement == null) {
            throw new IllegalArgumentException("No movement");
        }
        if (pluginType == null) {
            throw new IllegalArgumentException("No plugin type");
        }
        String senderReceiver = null;
        switch (pluginType) {
            case MANUAL:
                senderReceiver = username != null ? username : "Unknown";
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

    private static String getRecipientOfPoll(List<KeyValueType> pollReceiverList) {
        if (pollReceiverList == null || pollReceiverList.isEmpty()) {
            throw new IllegalArgumentException("No poll receiver list");
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
        throw new IllegalArgumentException("No receiver of poll");
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
        } catch (Exception e) {
            LOG.debug("Report sent to plugin couldn't map to senderReceiver");
        }
        return senderReceiver;
    }

    public static ExchangeLog getSendMovementExchangeLog(SendMovementToPluginType sendReport) {
        if (sendReport == null) {
            throw new IllegalArgumentException("No request");
        }
        ExchangeLog log = new ExchangeLog();
        log.setDateReceived(sendReport.getTimestamp().toInstant());
        log.setType(LogType.SEND_MOVEMENT);

        log.setTypeRefType(TypeRefType.MOVEMENT);
        log.setTypeRefGuid(UUID.fromString(sendReport.getMovement().getGuid()));
        String senderReceiver = getSendMovementSenderReceiver(sendReport);
        log.setSenderReceiver(senderReceiver);
        log.setTransferIncoming(false);

        log.setFwdDate(sendReport.getFwdDate().toInstant());
        log.setFwdRule(sendReport.getFwdRule());
        log.setRecipient(sendReport.getRecipient());

        log.setUpdatedBy("SYSTEM");
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);

        return log;
    }

    public static ExchangeLog getSendCommandExchangeLog(CommandType command, String username) {
        if (command == null) {
            throw new IllegalArgumentException("No command");
        }
        if (command.getCommand() == null) {
            throw new IllegalArgumentException("No command type");
        }
        switch (command.getCommand()) {
            case EMAIL:
                return getSendEmailExchangeLog(command, username);
            case POLL:
                return getPollExchangeLog(command, username);
        }
        throw new IllegalArgumentException("Not implemented command type");
    }

    private static ExchangeLog getSendEmailExchangeLog(CommandType command, String username) {
        if (command.getEmail() == null) {
            throw new IllegalArgumentException("No email");
        }
        ExchangeLog log = new ExchangeLog();
        log.setType(LogType.SEND_EMAIL);
        log.setDateReceived(command.getTimestamp().toInstant());
        log.setSenderReceiver("SYSTEM");
        log.setRecipient(command.getEmail().getTo());
        log.setFwdRule(command.getFwdRule());
        log.setFwdDate(command.getTimestamp().toInstant());
        log.setTransferIncoming(false);

        log.setUpdatedBy(username);
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);
        return addStatusHistory(log);
    }

    private static ExchangeLog getPollExchangeLog(CommandType command, String username) {
        if (command.getPoll() == null) {
            throw new IllegalArgumentException("No poll");
        }

        //TODO fix in MobileTerminal
        ExchangeLog log = new ExchangeLog();
        log.setType(LogType.SEND_POLL);
        log.setDateReceived(command.getTimestamp().toInstant());
        log.setRecipient(getRecipientOfPoll(command.getPoll().getPollReceiver()));
        log.setSenderReceiver("System");
        log.setFwdDate(command.getTimestamp().toInstant());
        log.setUpdatedBy(username);
        log.setTransferIncoming(false);

        log.setTypeRefType(TypeRefType.POLL);
        log.setTypeRefGuid(UUID.fromString(command.getPoll().getPollId()));
        log.setStatus(ExchangeLogStatusTypeType.ISSUED);
        return addStatusHistory(log);
    }

    public static ExchangeLog addStatusHistory(ExchangeLog log) {
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

    public static List<UnsentMessageProperty> getUnsentMessageProperties(SendMovementToPluginType sendReport) {
        List<UnsentMessageProperty> unsentMessageProperties = new ArrayList<>();
        UnsentMessageProperty propertyAssetName = new UnsentMessageProperty();
        UnsentMessageProperty propertyIrcs = new UnsentMessageProperty();
        UnsentMessageProperty propertyLong = new UnsentMessageProperty();
        UnsentMessageProperty propertyLat = new UnsentMessageProperty();
        UnsentMessageProperty propertyPositionTime = new UnsentMessageProperty();

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

        unsentMessageProperties.add(propertyAssetName);
        unsentMessageProperties.add(propertyIrcs);
        unsentMessageProperties.add(propertyLong);
        unsentMessageProperties.add(propertyLat);
        unsentMessageProperties.add(propertyPositionTime);
        return unsentMessageProperties;
    }

    public static List<UnsentMessageProperty> getPropertiesForPoll(PollType poll, String assetName) {
        List<UnsentMessageProperty> unsentMessageProperties = new ArrayList<>();
        UnsentMessageProperty propertyAssetName = new UnsentMessageProperty();
        propertyAssetName.setKey(UnsentMessageTypePropertyKey.ASSET_NAME);
        propertyAssetName.setValue(assetName);
        UnsentMessageProperty pollType = new UnsentMessageProperty();
        pollType.setKey(UnsentMessageTypePropertyKey.POLL_TYPE);
        pollType.setValue(poll.getPollTypeType().name());
        unsentMessageProperties.add(propertyAssetName);
        unsentMessageProperties.add(pollType);
        return unsentMessageProperties;
    }

    public static List<UnsentMessageProperty> getPropertiesForEmail(EmailType email) {
        List<UnsentMessageProperty> unsentMessageProperties = new ArrayList<>();
        UnsentMessageProperty propertyEmail = new UnsentMessageProperty();
        propertyEmail.setKey(UnsentMessageTypePropertyKey.EMAIL);
        propertyEmail.setValue(email.getTo());
        unsentMessageProperties.add(propertyEmail);
        return unsentMessageProperties;
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

    private static String getRecipient(MovementBaseType movementBaseType, PluginType pluginType) {
        if (movementBaseType == null) {
            throw new IllegalArgumentException("Movement is empty");
        }
        if (pluginType == null) {
            throw new IllegalArgumentException("PluginType is empty");
        }
        String recipient;
        switch (pluginType) {
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
