package eu.europa.ec.fisheries.uvms.exchange.model.mapper;


import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.AuditTypeEnum;

public class ExchangeAuditRequestMapper {

    public static String mapCreateExchangeLog(String guid, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_LOG.getValue(), AuditOperationEnum.CREATE.getValue(), guid, username);
    }

    public static String mapUpdateExchangeLog(String guid, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_LOG.getValue(), AuditOperationEnum.UPDATE.getValue(), guid, username);
    }

    public static String mapResendSendingQueue(String guid, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_SENDINGQUEUE.getValue(), AuditOperationEnum.RESEND.getValue(), guid, username);
    }

    public static String mapServiceStatusStarted(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.START.getValue(), serviceName, username);
    }

    public static String mapServiceStatusUnknown(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UNKNOWN.getValue(), serviceName, username);
    }

    public static String mapServiceStatusStopped(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.STOP.getValue(), serviceName, username);
    }

    public static String mapRegisterService(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.REGISTER_SERVICE.getValue(), serviceName, username);
    }

    public static String mapUnregisterService(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UNREGISTER_SERVICE.getValue(), serviceName, username);
    }

    public static String mapUpdateService(String serviceName, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UPDATE.getValue(), serviceName, username);
    }

    public static String mapCreateUnsentMessage(String guid, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_UNSENT_MESSAGE.getValue(), AuditOperationEnum.CREATE.getValue(), guid, username);
    }

    public static String mapUpdatePoll(String guid, String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_POLL.getValue(), AuditOperationEnum.UPDATE.getValue(), guid, username);
    }

    private static String mapToAuditLog(String objectType, String operation, String affectedObject, String username) throws AuditModelMarshallException {
        return AuditLogMapper.mapToAuditLog(objectType, operation, affectedObject, username);
    }

    public static String mapRemoveUnsentMessage(String guid , String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_UNSENT_MESSAGE.getValue(), AuditOperationEnum.REMOVE.getValue(), guid, username);
    }

}
