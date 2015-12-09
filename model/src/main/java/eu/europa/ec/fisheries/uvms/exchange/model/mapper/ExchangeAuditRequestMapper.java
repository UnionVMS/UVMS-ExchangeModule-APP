package eu.europa.ec.fisheries.uvms.exchange.model.mapper;


import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.AuditTypeEnum;

public class ExchangeAuditRequestMapper {

    public static String mapCreateExchangeLog(String guid) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_LOG.getValue(), AuditOperationEnum.CREATE.getValue(), guid);
    }

    public static String mapUpdateExchangeLog(String guid) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_LOG.getValue(), AuditOperationEnum.UPDATE.getValue(), guid);
    }

    public static String mapResendSendingQueue(String guid) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_SENDINGQUEUE.getValue(), AuditOperationEnum.RESEND.getValue(), guid);
    }

    public static String mapServiceStatusStarted(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.START.getValue(), serviceName);
    }

    public static String mapServiceStatusUnknown(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UNKNOWN.getValue(), serviceName);
    }

    public static String mapServiceStatusStopped(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.STOP.getValue(), serviceName);
    }

    public static String mapRegisterService(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.REGISTER_SERVICE.getValue(), serviceName);
    }

    public static String mapUnregisterService(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UNREGISTER_SERVICE.getValue(), serviceName);
    }

    public static String mapUpdateService(String serviceName) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_PLUGIN.getValue(), AuditOperationEnum.UPDATE.getValue(), serviceName);
    }

    public static String mapCreateUnsentMessage(String guid) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_UNSENT_MESSAGE.getValue(), AuditOperationEnum.CREATE.getValue(), guid);
    }

    public static String mapUpdatePoll(String guid) throws AuditModelMarshallException {
        return mapToAuditLog(AuditTypeEnum.EXCHANGE_POLL.getValue(), AuditOperationEnum.UPDATE.getValue(), guid);
    }

    private static String mapToAuditLog(String objectType, String operation, String affectedObject) throws AuditModelMarshallException {
        return AuditLogMapper.mapToAuditLog(objectType, operation, affectedObject);
    }

}
