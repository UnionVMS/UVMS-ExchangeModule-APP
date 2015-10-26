package eu.europa.ec.fisheries.uvms.exchange.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.rest.constants.RestConstants;
import eu.europa.ec.fisheries.uvms.exchange.rest.service.ConfigResource;
import eu.europa.ec.fisheries.uvms.exchange.rest.service.ExchangeLogRestResource;
import eu.europa.ec.fisheries.uvms.exchange.rest.service.ExchangeRegistryResource;
import eu.europa.ec.fisheries.uvms.exchange.rest.service.ExchangeSendingQueueResource;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeatureFilter;

@ApplicationPath(RestConstants.MODULE_REST)
public class RestActivator extends Application {

    final static Logger LOG = LoggerFactory.getLogger(RestActivator.class);

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(ExchangeLogRestResource.class);
        set.add(ExchangeRegistryResource.class);
        set.add(ExchangeSendingQueueResource.class);
        set.add(ConfigResource.class);
        set.add(UnionVMSFeatureFilter.class);
        LOG.info(RestConstants.MODULE_NAME + " module starting up");
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
