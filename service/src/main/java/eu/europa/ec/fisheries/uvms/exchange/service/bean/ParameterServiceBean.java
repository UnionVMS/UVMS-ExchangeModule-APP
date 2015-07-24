package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.Parameter;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.InputArgumentException;

@Stateless
public class ParameterServiceBean implements ParameterService {

    /**
     * THIS BEAN NEEDS TO HAVE ITS NAME CHANGED SO IT DOES NOT CONFLICT WITH
     * OTHER COMPONENTS
     */
    final static Logger LOG = LoggerFactory.getLogger(ParameterServiceBean.class);

    @PersistenceContext(unitName = "modulePU")
    EntityManager em;

    @Override
    public String getStringValue(ParameterKey key) throws ExchangeServiceException {
        try {
            Query query = em.createNamedQuery(ServiceConstants.FIND_BY_NAME);
            query.setParameter("key", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return entity.getParamValue();
        } catch (Exception ex) {
            LOG.error("[ Error when getting String value ]", ex.getMessage());
            throw new ExchangeServiceException("[ Error when getting String value ]", ex);
        }
    }

    @Override
    public Boolean getBooleanValue(ParameterKey key) throws ExchangeServiceException {
        try {
            Query query = em.createNamedQuery(ServiceConstants.FIND_BY_NAME);
            query.setParameter("key", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return parseBooleanValue(entity.getParamValue());
        } catch (ExchangeServiceException ex) {
            LOG.error("[ Error when getting Boolean value ]", ex.getMessage());
            throw new ExchangeServiceException("[ Error when getting Boolean value ]", ex);
        }
    }

    private Boolean parseBooleanValue(String value) throws InputArgumentException, ExchangeServiceException {
        try {
            if (value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else {
                LOG.error("[ Error when parsing Boolean value from String, The String provided dows not equal 'TRUE' or 'FALSE'. The value is {} ]", value);
                throw new InputArgumentException("The String value provided does not equal boolean value, value provided = " + value);
            }
        } catch (Exception ex) {
            LOG.error("[ Error when parsing Boolean value from String ]", ex.getMessage());
            throw new ExchangeServiceException("[ Error when parsing Boolean value from String ]", ex);
        }
    }

}
