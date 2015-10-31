package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

@Local
public interface ExchangeRulesService {
	
    public MovementRefType sendMovementToRules(PluginType pluginType, RawMovementType movement) throws ExchangeServiceException;

}
