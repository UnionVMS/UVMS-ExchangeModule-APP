package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

public class SettingTypeMapper {

    public static eu.europa.ec.fisheries.schema.config.types.v1.SettingType map(String key, String value) {
        eu.europa.ec.fisheries.schema.config.types.v1.SettingType ret = new eu.europa.ec.fisheries.schema.config.types.v1.SettingType();
        ret.setKey(key);
        ret.setValue(value);
        return ret;
    }

    public static eu.europa.ec.fisheries.schema.config.types.v1.SettingType map(String key, String value, String desc) {
        eu.europa.ec.fisheries.schema.config.types.v1.SettingType ret = new eu.europa.ec.fisheries.schema.config.types.v1.SettingType();
        ret.setKey(key);
        ret.setValue(value);
        ret.setDescription(desc);
        return ret;
    }

    public static eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType map(eu.europa.ec.fisheries.schema.config.types.v1.SettingType setting) {
        eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType ret = new eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType();
        ret.setKey(setting.getKey());
        ret.setValue(setting.getValue());
        return ret;
    }
}
