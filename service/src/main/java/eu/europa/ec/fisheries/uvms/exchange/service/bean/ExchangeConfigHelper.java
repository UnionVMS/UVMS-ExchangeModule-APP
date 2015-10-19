package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;

@Stateless
public class ExchangeConfigHelper implements ConfigHelper {

    @Override
    public List<String> getAllParameterKeys() {
        List<String> keys = new ArrayList<>();
        for (ParameterKey parameterKey : ParameterKey.values()) {
            keys.add(parameterKey.getKey());
        }

        return keys;
    }

    @Override
    public String getModuleName() {
        return ExchangeModelConstants.MODULE_NAME;
    }

}
