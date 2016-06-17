package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

import javax.ejb.Local;

@Local
public interface ExchangeRulesService {

    void sendMovementToRules(PluginType pluginType, RawMovementType movement, String username) throws ExchangeServiceException;

    void sendFLUXFAReportMessageToRules(PluginType pluginType, String  fluxFAReportMessage, String username) throws ExchangeServiceException;
}
