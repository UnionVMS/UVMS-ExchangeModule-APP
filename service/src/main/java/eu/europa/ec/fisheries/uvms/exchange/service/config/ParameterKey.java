package eu.europa.ec.fisheries.uvms.exchange.service.config;

public enum ParameterKey {

    KEY("exchange.key.attribute"),
    KEY2("exchange.key.otherSetting"),
    KEY3("aGlobalSetting");

    private final String key;

    private ParameterKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ParameterKey valueOfKey(String keyString) {
        for (ParameterKey key : ParameterKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }

        throw new IllegalArgumentException("No enum value with key = " + keyString);
    }
}
