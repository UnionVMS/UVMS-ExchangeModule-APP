package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;
import java.util.List;

@Local
public interface ExchangeService {

    /**
     * Create/Insert data into database
     *
     * @param data
     * @return
     * @throws ExchangeServiceException
     */
    public ModuleObject create(ModuleObject data) throws ExchangeServiceException;

    /**
     * Get a list with data
     *
     * @return
     * @throws ExchangeServiceException
     */
    public List<ModuleObject> getList() throws ExchangeServiceException;

    /**
     * Get an object by id
     *
     * @param id
     * @return
     * @throws ExchangeServiceException
     */
    public ModuleObject getById(Long id) throws ExchangeServiceException;

    /**
     * Update an object
     *
     * @param data
     * @throws ExchangeServiceException
     */
    public ModuleObject update(ModuleObject data) throws ExchangeServiceException;

}
