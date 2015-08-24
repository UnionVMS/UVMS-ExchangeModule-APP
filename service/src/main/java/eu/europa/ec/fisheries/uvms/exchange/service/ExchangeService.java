package eu.europa.ec.fisheries.uvms.exchange.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
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

    /**
     *
     * Creates an ExchangeLog
     *
     * @param exchangeLog
     * @return
     * @throws ExchangeServiceException
     */
    public ExchangeLogType createExchangeLog(ExchangeLogType exchangeLog) throws ExchangeServiceException;

    /**
     *
     * Gets ExchangeLogs by a query
     *
     * @param query
     * @return
     * @throws ExchangeServiceException
     */
    public GetLogListByQueryResponse getExchangeLogByQuery(ExchangeListQuery query) throws ExchangeServiceException;

    /**
     *
     * Post poll request on EventBus
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public String sendPollToPlugin(PollType data) throws ExchangeServiceException;

}
