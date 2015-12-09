package eu.europa.ec.fisheries.uvms.exchange.model.constant;

public enum AuditTypeEnum {

    EXCHANGE_LOG("Exchange log"), EXCHANGE_PLUGIN("Exchange plugin"), EXCHANGE_SENDINGQUEUE("Exchange sending queue"), EXCHANGE_UNSENT_MESSAGE("Exchange unsent message"), EXCHANGE_POLL("Exchange poll");

    private String value;

    AuditTypeEnum(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
