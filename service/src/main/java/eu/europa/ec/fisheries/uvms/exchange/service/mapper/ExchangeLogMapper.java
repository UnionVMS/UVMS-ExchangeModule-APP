package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SendMovementToPluginType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ReceiveMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendEmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendPollType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

public class ExchangeLogMapper {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeLogMapper.class);

    public static ExchangeLogType getReceivedMovementExchangeLog(SetReportMovementType request, String typeRefGuid, String typeRefType) throws ExchangeLogException {
        if (request == null) {
            throw new ExchangeLogException("No request");
        }
        ReceiveMovementType log = new ReceiveMovementType();
        log.setDateRecieved(request.getTimestamp());
        log.setType(LogType.RECEIVE_MOVEMENT);

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        TypeRefType refType = TypeRefType.UNKNOWN;
        try {
            refType = TypeRefType.fromValue(typeRefType);
        } catch (IllegalArgumentException e) {
            LOG.error("Non existing typeRefType: " + typeRefType);

        }
        logRefType.setType(refType);
        log.setTypeRef(logRefType);

        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);

        log.setSenderReceiver(getSenderReceiver(request.getMovement(), request.getPluginType(), request.getPluginName()));
        if (request.getMovement().getSource() != null) {
            log.setSource(request.getMovement().getSource().name());
        } else {
            log.setSource(request.getPluginType().name());
        }
        return log;
    }

    private static String getSenderReceiver(MovementBaseType movement, PluginType pluginType, String pluginName) throws ExchangeLogException {
        if (movement == null) {
            throw new ExchangeLogException("No movement");
        }
        if (pluginType == null) {
            throw new ExchangeLogException("No plugin type");
        }

        String senderReceiver = null;

        switch (pluginType) {
            case SATELLITE_RECEIVER:
                senderReceiver = getSenderReceiverOfMovement(movement.getSource(), movement.getMobileTerminalId());
                break;
            case FLUX:
                senderReceiver = getSenderReceiver(movement.getAssetId());
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
        for (AssetIdList idList : assetId.getAssetIdList()) {
            switch (idList.getIdType()) {
                case CFR:
                case GUID:
                case ID:
                case IMO:
                case IRCS:
                case MMSI:
                default:
                    return idList.getValue();
            }
        }
        throw new ExchangeLogException("No asset id value");
    }

    private static String getSenderReceiverOfMovement(MovementSourceType source, MobileTerminalId terminalId) throws ExchangeLogException {
        if (source == null) {
            throw new ExchangeLogException("No source of movement type");
        }
        if (terminalId == null) {
            throw new ExchangeLogException("No mobile terminal id");
        }

        String dnid = null;
        String memberNumber = null;
        String serialNumber = null;
        String les = null;

        List<IdList> idList = terminalId.getMobileTerminalIdList();
        for (IdList id : idList) {
            if (IdType.DNID.equals(id.getType())) {
                dnid = id.getValue();
            } else if (IdType.MEMBER_NUMBER.equals(id.getType())) {
                memberNumber = id.getValue();
            } else if (IdType.SERIAL_NUMBER.equals(id.getType())) {
                serialNumber = id.getValue();
            } else if (IdType.LES.equals(id.getType())) {
                les = id.getValue();
            }
        }

        switch (source) {
            case INMARSAT_C:
                return dnid + memberNumber;
            case IRIDIUM:
                return serialNumber;
            case AIS:
                return serialNumber;
        }
        throw new ExchangeLogException("No id of mobile terminal value");
    }

    private static String getSenderReciverOfPoll(List<KeyValueType> pollRecieverList) throws ExchangeLogException {
        if (pollRecieverList == null || pollRecieverList.isEmpty()) {
            throw new ExchangeLogException("No poll receiver list");
        }
        String dnid = null;
        String memberNumber = null;
        String satelliteNumber = null;
        String les = null;

        for (KeyValueType pollReceiver : pollRecieverList) {
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
            return dnid + memberNumber;
        } else if (satelliteNumber != null) {
            return satelliteNumber;
        } else if (les != null) {
            return les;
        }
        throw new ExchangeLogException("No receiver of poll");
    }

    public static String getSendMovementSenderReceiver(SendMovementToPluginType sendReport) {
        String senderReceiver = sendReport.getPluginType().name();
        if (sendReport.getPluginName() != null && !sendReport.getPluginName().isEmpty()) {
            senderReceiver = sendReport.getPluginName();
        }
        try {
            senderReceiver = getSenderReceiver(sendReport.getMovement(), sendReport.getPluginType(), sendReport.getPluginName());
        } catch (ExchangeLogException e) {
            LOG.debug("Report sent to plugin couldn't map to senderReceiver");
        }
        return senderReceiver;
    }

    public static ExchangeLogType getSendMovementExchangeLog(SendMovementToPluginType sendReport) throws ExchangeLogException {
        if (sendReport == null) {
            throw new ExchangeLogException("No request");
        }
        SendMovementType log = new SendMovementType();
        log.setDateRecieved(sendReport.getTimestamp());
        log.setType(LogType.SEND_MOVEMENT);
        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(sendReport.getMovement().getGuid());
        logRefType.setType(TypeRefType.MOVEMENT);
        log.setTypeRef(logRefType);
        String senderReceiver = getSendMovementSenderReceiver(sendReport);
        log.setSenderReceiver(senderReceiver);

        //TODO send fwdDate, fwdRule and recipient from Rules
        log.setFwdDate(sendReport.getFwdDate());
        log.setFwdRule(sendReport.getFwdRule());
        log.setRecipient(sendReport.getRecipient());

        return log;
    }

    public static ExchangeLogType getSendCommandExchangeLog(CommandType command) throws ExchangeLogException {
        if (command == null) {
            throw new ExchangeLogException("No command");
        }
        if (command.getCommand() == null) {
            throw new ExchangeLogException("No command type");
        }
        switch (command.getCommand()) {
            case EMAIL:
                return getSendEmailExchangeLog(command);
            case POLL:
                return getPollExchangeLog(command);
        }
        throw new ExchangeLogException("Not implemented command type");
    }

    private static ExchangeLogType getSendEmailExchangeLog(CommandType command) throws ExchangeLogException {
        if (command.getEmail() == null) {
            throw new ExchangeLogException("No email");
        }
        SendEmailType log = new SendEmailType();
        log.setType(LogType.SEND_EMAIL);
        log.setDateRecieved(command.getTimestamp());
        log.setSenderReceiver(command.getEmail().getTo());
        return log;
    }

    private static ExchangeLogType getPollExchangeLog(CommandType command) throws ExchangeLogException {
        if (command.getPoll() == null) {
            throw new ExchangeLogException("No poll");
        }

        //TODO fix in mobileterminal
        SendPollType log = new SendPollType();
        log.setType(LogType.SEND_POLL);
        log.setDateRecieved(command.getTimestamp());

        String senderReceiver = command.getPluginName();
        try {
            senderReceiver = getSenderReciverOfPoll(command.getPoll().getPollReceiver());
        } catch (ExchangeLogException e) {
            LOG.error(e.getMessage());
        }
        log.setSenderReceiver(senderReceiver);
        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(command.getPoll().getPollId());
        logRefType.setType(TypeRefType.POLL);
        log.setTypeRef(logRefType);
        return log;
    }
}
