package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;

@Singleton
@Startup
@DependsOn(value = { "ExchangeServiceBean" })
public class ConfigInitializer {

    @EJB
    ExchangeService exchangeService;

    final static Logger LOG = LoggerFactory.getLogger(ConfigInitializer.class);

    @PostConstruct
    protected void startup() {
        try {
            exchangeService.syncSettingsWithConfig();
        }
        catch (ExchangeServiceException e) {
            LOG.error("[ Error when synchronizing settings with Config at startup. ]", e);
        }
    }
}
