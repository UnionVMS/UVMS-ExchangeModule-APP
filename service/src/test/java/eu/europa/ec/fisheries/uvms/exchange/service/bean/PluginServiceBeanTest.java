package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import static org.junit.Assert.assertThat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.schema.config.module.v1.SettingEventType;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.config.service.UVMSConfigService;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.BuildExchangeServiceTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.service.JMSHelper;

@RunWith(Arquillian.class)
public class PluginServiceBeanTest extends BuildExchangeServiceTestDeployment {

    JMSHelper jmsHelper;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    PluginServiceBean pluginService;
    
    @Inject
    UVMSConfigService uvmsConfigService;

    @Inject
    ServiceRegistryDaoBean serviceRegistryDao;
    
    @Inject
    ParameterService parameterService;
    
    @Before
    public void initialize() throws Exception {
        jmsHelper = new JMSHelper(connectionFactory);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setConfigTest() throws Exception{
        String serviceName = "Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.test";
        String key = "setting";
        String value = "APA";
        
        ServiceSetting setting = new ServiceSetting();
        setting.setSetting(serviceClassName + "." + key);
        setting.setValue(value);
        setting.setUpdatedTime(Instant.now());
        setting.setUser("Test");
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.OTHER, Arrays.asList(setting));
        
        // Simulate sync with config
        parameterService.setStringValue(serviceClassName + "." + key, value, "");
        
        SettingType settingType = new SettingType();
        settingType.setKey(serviceClassName + "." + key);
        String newValue = "BEPA";
        settingType.setValue(newValue);
        settingType.setDescription("Test");
        
        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        
        uvmsConfigService.updateSetting(settingType, SettingEventType.SET);
        
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000l);
        SetConfigRequest configRequest = JAXBMarshaller.unmarshallTextMessage(message, SetConfigRequest.class);
        
        assertThat(configRequest.getConfigurations().getSetting().size(), CoreMatchers.is(1));
        assertThat(configRequest.getConfigurations().getSetting().get(0).getKey(), CoreMatchers.is(key));
        assertThat(configRequest.getConfigurations().getSetting().get(0).getValue(), CoreMatchers.is(newValue));
        
        serviceRegistryDao.deleteEntity(service.getId());
    }
    
    private Service createAndPersistBasicService(String name, String serviceClassName, PluginType pluginType, List<ServiceSetting> settings) throws Exception{
        Service service = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        if(service != null){
            serviceRegistryDao.deleteEntity(service.getId());
        }
        service = new Service();
        service.setActive(true);
        service.setDescription("Test description");
        service.setName(name);
        service.setSatelliteType(null);
        service.setServiceClassName(serviceClassName);
        service.setServiceResponse(serviceClassName + "PLUGIN_RESPONSE");
        service.setStatus(true);
        service.setType(pluginType);
        service.setUpdated(Instant.now());
        service.setUpdatedBy("Exchange Tests");

        for (ServiceSetting serviceSetting : settings) {
            serviceSetting.setService(service);
        }
        service.setServiceSettingList(settings);

        return serviceRegistryDao.createEntity(service);
    }
}
