package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

@Local
public interface ParameterService {

    /**
     * Returns the parameter value as a string.
     * 
     * @param key a parameter key
     * @return a string representation of the parameter value
     * @throws ExchangeServiceException if unsuccessful
     */
    public String getStringValue(ParameterKey key) throws ExchangeServiceException;

    /**
     * Sets a value for the specified key.
     *
     * @param key a parameter key
     * @param value a value
     * @throws ExchangeServiceException if unsuccessful
     */
    public void setStringValue(ParameterKey key, String value) throws ExchangeServiceException;

    /**
     * Returns the parameter value as a boolean.
     * 
     * @param key a parameter key
     * @return a boolean representation of the parameter value
     * @throws ExchangeServiceException if unsuccessful
     */
    public Boolean getBooleanValue(ParameterKey key) throws ExchangeServiceException;

    /**
     * Removes any parameter with the specified key.
     * 
     * @param key a parameter key 
     * @throws ExchangeServiceException if unsuccessful
     */
    public void reset(ParameterKey key) throws ExchangeServiceException;

    /**
     * Removes all parameters.
     */
    public void clearAll() throws ExchangeServiceException;

}
