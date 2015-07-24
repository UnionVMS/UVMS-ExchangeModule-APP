package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import javax.ejb.Local;

@Local
public interface ParameterService {

    public String getStringValue(ParameterKey key) throws ExchangeServiceException;

    public Boolean getBooleanValue(ParameterKey key) throws ExchangeServiceException;

}
