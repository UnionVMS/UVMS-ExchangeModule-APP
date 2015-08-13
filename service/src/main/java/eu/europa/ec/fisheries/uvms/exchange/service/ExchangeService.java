package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

@Local
public interface ExchangeService {

    /**
     * Register a service
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType registerService(ServiceType data) throws ExchangeServiceException;

    /**
     * Unregister a service
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType unregisterService(ServiceType data) throws ExchangeServiceException;

    /**
     * Get a list with data
     *
     * @return
     * @throws ExchangeServiceException
     */
    public List<ServiceType> getServiceList() throws ExchangeServiceException;

    /**
     * Get an object by id
     *
     * @param id
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType getById(Long id) throws ExchangeServiceException;

    /**
     * Update an object
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType update(ServiceType data) throws ExchangeServiceException;

    /**
     *
     * @param serviceId
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType getService(String serviceId) throws ExchangeServiceException;

}
