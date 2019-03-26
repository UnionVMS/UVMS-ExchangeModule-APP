package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RegistryBusEventListenerTest extends BuildExchangeServiceTestDeployment {

    JMSHelper jmsHelper;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    ServiceRegistryDaoBean serviceRegistryDao;

    private Jsonb jsonb;

    @Before
    public void initialize() throws Exception {
        jmsHelper = new JMSHelper(connectionFactory);
        jsonb = JsonbBuilder.create();
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void registerEmailServiceTest() throws Exception{
        ServiceType service = createBasicService(PluginType.EMAIL);

        String request = ExchangeModuleRequestMapper.createRegisterServiceRequest(service, createBasicCapabilityList(), createBasicSettingsList());

        jmsHelper.sendMessageOnEventQueue(request);

        Thread.sleep(1000); //to let it work

        List<Service> serviceList = serviceRegistryDao.getServices();
        Service newService = serviceList.get(serviceList.size() - 1);
        assertTrue(newService.getActive());
        assertEquals(true, newService.getStatus());
        assertEquals(PluginType.EMAIL.value(), newService.getType().value());
        assertEquals(service.getServiceClassName(), newService.getServiceClassName());

        assertEquals(5, serviceRegistryDao.getServiceCapabilities(service.getServiceClassName()).size());
        assertEquals(4, serviceRegistryDao.getServiceSettings(service.getServiceClassName()).size());

    }


    @Test
    @OperateOnDeployment("exchangeservice")
    public void registerManualServiceTest() throws Exception{
        ServiceType service = createBasicService(PluginType.MANUAL);

        String request = ExchangeModuleRequestMapper.createRegisterServiceRequest(service, createBasicCapabilityList(), createBasicSettingsList());

        jmsHelper.registerSubscriber("ServiceName = '" + service.getServiceResponseMessageName() + "'");
        jmsHelper.sendMessageOnEventQueue(request);
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + service.getServiceResponseMessageName() + "'", 5000l);
        RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);

        assertEquals(AcknowledgeTypeType.OK, response.getAck().getType());
        assertEquals(service.getServiceClassName(), response.getService().getServiceClassName());

        Thread.sleep(1000); //to let it work

        List<Service> serviceList = serviceRegistryDao.getServices();
        Service newService = serviceList.get(serviceList.size() - 1);
        assertTrue(newService.getActive());
        assertEquals(true, newService.getStatus());
        assertEquals(PluginType.MANUAL.value(), newService.getType().value());
        assertEquals(service.getServiceClassName(), newService.getServiceClassName());

        assertEquals(5, serviceRegistryDao.getServiceCapabilities(service.getServiceClassName()).size());
        assertEquals(4, serviceRegistryDao.getServiceSettings(service.getServiceClassName()).size());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void registerAndUnregisterManualServiceTest() throws Exception{
        ServiceType service = createBasicService(PluginType.MANUAL);

        String registerRequest = ExchangeModuleRequestMapper.createRegisterServiceRequest(service, createBasicCapabilityList(), createBasicSettingsList());

        jmsHelper.registerSubscriber("ServiceName = '" + service.getServiceResponseMessageName() + "'");
        jmsHelper.sendMessageOnEventQueue(registerRequest);
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + service.getServiceResponseMessageName() + "'", 5000l);
        RegisterServiceResponse response = JAXBMarshaller.unmarshallTextMessage(message, RegisterServiceResponse.class);

        assertEquals(AcknowledgeTypeType.OK, response.getAck().getType());
        assertEquals(service.getServiceClassName(), response.getService().getServiceClassName());

        Thread.sleep(500);      //removed a requires new transaction on the register service part so this is needed to allow the transaction go commit
        String unregisterRequest = ExchangeModuleRequestMapper.createUnregisterServiceRequest(service);
        jmsHelper.sendMessageOnEventQueue(unregisterRequest);

        Thread.sleep(1000);
        Service unregistredService = serviceRegistryDao.getServiceByServiceClassName(service.getServiceClassName());
        assertEquals(service.getServiceClassName(), unregistredService.getServiceClassName());
        assertFalse(unregistredService.getActive());
        assertEquals(false, unregistredService.getStatus());
        assertEquals(PluginType.MANUAL.value(), unregistredService.getType().value());
    }

    private ServiceType createBasicService(PluginType pluginType) {

        ServiceType service = new ServiceType();
        UUID random = UUID.randomUUID();
        service.setDescription("Register service test service " + pluginType.value());
        String name = "Register service test service name " + pluginType.value() + random.toString();
        service.setName(name.substring(0, 60));     //username can only be 60 long
        service.setPluginType(pluginType);
        service.setServiceClassName("Register service test service class name " + pluginType.value() + random.toString());
        service.setServiceResponseMessageName("Register service test response message " + pluginType.value() + random.toString());

        return service;
    }

    private CapabilityListType createBasicCapabilityList(){
        CapabilityListType capabilityList = new CapabilityListType();
        CapabilityType capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.CONFIGURABLE);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);

        capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.MULTIPLE_OCEAN);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);

        capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.ONLY_SINGLE_OCEAN);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);

        capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.POLLABLE);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);

        capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.SAMPLING);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);

        return capabilityList;
    }

    private SettingListType createBasicSettingsList(){
        SettingListType settingListType = new SettingListType();
        SettingType settingType = new SettingType();
        settingType.setKey("Password");
        settingType.setValue("Test password");
        settingListType.getSetting().add(settingType);

        settingType = new SettingType();
        settingType.setKey("Username");
        settingType.setValue("Test username");
        settingListType.getSetting().add(settingType);

        settingType = new SettingType();
        settingType.setKey("Port");
        settingType.setValue("Test port");
        settingListType.getSetting().add(settingType);

        settingType = new SettingType();
        settingType.setKey("Host");
        settingType.setValue("Test host");
        settingListType.getSetting().add(settingType);

        return settingListType;
    }
}
