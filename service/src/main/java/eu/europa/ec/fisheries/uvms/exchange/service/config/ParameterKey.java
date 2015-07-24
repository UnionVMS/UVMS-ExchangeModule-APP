package eu.europa.ec.fisheries.uvms.exchange.service.config;

public enum ParameterKey {

    KEY("exchange.key.attribute");

    private final String key;

    private ParameterKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
