package eu.europa.ec.fisheries.uvms.exchange.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.StatusType;
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
     * @param capabilityList
     * @param settingList
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceResponseType registerService(ServiceType data, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeServiceException;

    /**
     * Unregister a service
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceResponseType unregisterService(ServiceType data, String username) throws ExchangeServiceException;

    /**
     * Get a list with plugins depending on plugin types
     *
     * @return
     * @throws ExchangeServiceException
     */
    public List<ServiceResponseType> getServiceList(List<PluginType> pluginTypes) throws ExchangeServiceException;
    
    /**
     * Get an object by id
     *
     * @param id
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceType getById(Long id) throws ExchangeServiceException;

    /**
     * Upsert Service with settings
     * @param serviceClassName - name of service to upsert
     * @param settingList - list of settings to upsert
     * @return updated service
     * @throws ExchangeServiceException
     */
    public ServiceResponseType upsertSettings(String serviceClassName, SettingListType settingList, String username) throws ExchangeServiceException;

    /**
     * 
     * @param serviceClassName
     * @param status
     * @return
     * @throws ExchangeServiceException
     */
	public ServiceResponseType updateServiceStatus(String serviceClassName, StatusType status, String username) throws ExchangeServiceException;
    
    /**
     *
     * @param serviceId
     * @return
     * @throws ExchangeServiceException
     */
    public ServiceResponseType getService(String serviceId) throws ExchangeServiceException;

    /**
     *
     * Creates an ExchangeLog
     *
     * @param exchangeLog
     * @return
     * @throws ExchangeServiceException
     */
    public ExchangeLogType createExchangeLog(ExchangeLogType exchangeLog, String username) throws ExchangeServiceException;

    /**
     *
     * Gets ExchangeLogs by a query
     *
     * @param query
     * @return
     * @throws ExchangeServiceException
     */
    public GetLogListByQueryResponse getExchangeLogByQuery(ExchangeListQuery query) throws ExchangeServiceException;

}
