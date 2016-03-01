package eu.europa.ec.fisheries.uvms.exchange.model.constant;


public enum  AuditOperationEnum {
    CREATE("Create"), UPDATE("Update"), START("Start") ,STOP("Stop"), UNKNOWN("Unknown") ,RESEND("resend"), REGISTER_SERVICE ("Register service"), UNREGISTER_SERVICE("Unregister service"), REMOVE("Remove");

    private String value;

    AuditOperationEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
