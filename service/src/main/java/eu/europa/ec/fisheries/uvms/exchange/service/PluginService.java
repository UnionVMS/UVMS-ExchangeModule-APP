package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;

@Local
public interface PluginService {

    public void registerService(@Observes @RegisterServiceEvent PluginMessageEvent message);
    public void unregisterService(@Observes @UnRegisterServiceEvent PluginMessageEvent message);
    
    public void setConfig(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent);
}
