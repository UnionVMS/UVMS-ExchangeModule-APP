package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
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
            Query query = em.createNamedQuery(ExchangeServiceConstants.FIND_BY_NAME);
            query.setParameter("parameterDescription", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return entity.getParamValue();
        } catch (Exception ex) {
            LOG.error("[ Error when getting String value ]", ex.getMessage());
            throw new ExchangeServiceException("[ Error when getting String value ]");
        }
    }

    @Override
    public void setStringValue(ParameterKey key, String value) throws ExchangeServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(ExchangeServiceConstants.FIND_BY_NAME, Parameter.class);
            query.setParameter("parameterDescription", key.getKey());
            List<Parameter> parameters = query.getResultList();

            if (parameters.size() == 1) {
                // Update existing parameter
                parameters.get(0).setParamValue(value);
                em.flush();
            }
            else {
                if (!parameters.isEmpty()) {
                    // Remove all parameters occurring more than once
                    for (Parameter parameter : parameters) {
                        em.remove(parameter);
                    }
                }

                // Create new parameter
                Parameter parameter = new Parameter();
                parameter.setParamId(UUID.randomUUID().toString());
                parameter.setParamDescription(key.getKey());
                parameter.setParamValue(value);
                em.persist(parameter);
            }
        }
        catch (Exception e) {
            LOG.error("[ Error when setting String value. ] {}", e.getMessage());
            throw new ExchangeServiceException("[ Error when setting String value. ]");
        }
    }

    @Override
    public Boolean getBooleanValue(ParameterKey key) throws ExchangeServiceException {
        try {
            Query query = em.createNamedQuery(ExchangeServiceConstants.FIND_BY_NAME);
            query.setParameter("key", key.getKey());
            Parameter entity = (Parameter) query.getSingleResult();
            return parseBooleanValue(entity.getParamValue());
        } catch (ExchangeServiceException ex) {
            LOG.error("[ Error when getting Boolean value ]", ex.getMessage());
            throw new ExchangeServiceException("[ Error when getting Boolean value ]");
        }
    }

    @Override
    public void reset(ParameterKey key) throws ExchangeServiceException {
        TypedQuery<Parameter> query = em.createNamedQuery(ExchangeServiceConstants.FIND_BY_NAME, Parameter.class);
        query.setParameter("parameterDescription", key.getKey());
        for (Parameter parameter : query.getResultList()) {
            em.remove(parameter);
        }
    }

    @Override
    public void clearAll() throws ExchangeServiceException {
        try {
            TypedQuery<Parameter> query = em.createNamedQuery(ExchangeServiceConstants.LIST_ALL, Parameter.class);
            List<Parameter> parameters = query.getResultList();
            for (Parameter parameter : parameters) {
                em.remove(parameter);
            }
        }
        catch (Exception e) {
            LOG.error("[ Error when clearing all settings. ] {}", e.getMessage());
            throw new ExchangeServiceException("[ Error when clearing all settings. ]");
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
            throw new ExchangeServiceException("[ Error when parsing Boolean value from String ]");
        }
    }

}
