package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.*;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.dao.UnsentMessageDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventLogCache;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ExchangeMessageConsumerBeanTest extends BuildExchangeServiceTestDeployment {

    JMSHelper jmsHelper;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    ServiceRegistryDao serviceRegistryDao;

    @Inject
    UnsentMessageDao unsentMessageDao;

    @Inject
    ExchangeEventLogCache exchangeEventLogCache;

    @Before
    public void cleanJMS() throws Exception {
        jmsHelper = new JMSHelper(connectionFactory);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void worldsBestAndMostUsefullArquillianTest(){
        assertTrue(true);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void pingExchangeTest() throws Exception{
        PingResponse response = jmsHelper.pingExchange();
        assertNotNull(response);
        assertEquals("pong", response.getResponse());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void listServiceTest() throws Exception{
        List<PluginType> inputList = new ArrayList<>();
        String request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList);  //Empty list ie get all

        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        TextMessage message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertEquals(1,response.getService().size());
        assertEquals("ManualMovement", response.getService().get(0).getServiceClassName());


        inputList.add(PluginType.SATELLITE_RECEIVER);

        request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList);         //have no SR in the db

        corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertTrue(response.getService().isEmpty());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailTest() throws Exception{
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createSetCommandSendEmailRequest(serviceClassName, email, null);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals(response.getCommand().getCommand(), CommandTypeType.EMAIL);
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());


    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailPluginNotStartedTest() throws Exception{
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);
        service.setStatus("STOPPED");
        service = serviceRegistryDao.updateService(service);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createSetCommandSendEmailRequest(serviceClassName, email, null);

        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");
        TextMessage message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        SetCommandResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandResponse.class);

        assertNotNull(response);
        assertEquals("Plugin to send command to is not started", response.getResponse().getMessage());
        assertEquals(AcknowledgeTypeType.NOK, response.getResponse().getType());

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());


    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandPollTest() throws Exception{
        String serviceName = "Poll Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.inmarsat";
        int sizeB4 = unsentMessageDao.getAll().size();
        int eventLogSizeB4 = exchangeEventLogCache.size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        PollType pollType = new PollType();
        pollType.setPollTypeType(PollTypeType.POLL);
        pollType.setMessage("TestMessage");
        pollType.setPollId("Test Poll ID");
        KeyValueType keyValueType = new KeyValueType();
        keyValueType.setKey("CONNECT_ID");
        keyValueType.setValue("TestConnectID");
        pollType.getPollReceiver().add(keyValueType);

        keyValueType = new KeyValueType();
        keyValueType.setKey("LES");
        keyValueType.setValue("TestLESID");
        pollType.getPollReceiver().add(keyValueType);

        String request = ExchangeModuleRequestMapper.createSetCommandSendPollRequest(serviceClassName, pollType, "Test User",null);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals( CommandTypeType.POLL, response.getCommand().getCommand());
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());
        assertEquals(eventLogSizeB4 + 1, exchangeEventLogCache.size());


        serviceRegistryDao.deleteEntity(service.getId());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendReportToPluginTest(){

    }



    private Service createAndPersistBasicService(String name, String serviceClassName, PluginType pluginType) throws Exception{
        Service s = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        if(s != null){
            return s;
        }

        s = new Service();
        s.setActive(true);
        s.setDescription("Test description");
        s.setName(name);
        s.setSatelliteType(null);
        s.setServiceClassName(serviceClassName);
        s.setServiceResponse(serviceClassName + "PLUGIN_RESPONSE");
        s.setStatus("STARTED");
        s.setType(pluginType);
        s.setUpdated(Instant.now());
        s.setUpdatedBy("Exchange Tests");

        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
        ServiceCapability serviceCapability = new ServiceCapability();
        serviceCapability.setService(s);
        serviceCapability.setUpdatedBy("Exchange Tests");
        serviceCapability.setUpdatedTime(Instant.now());
        serviceCapability.setCapability(CapabilityTypeType.POLLABLE);
        serviceCapability.setValue("TRUE");
        serviceCapabilityList.add(serviceCapability);
        s.setServiceCapabilityList(serviceCapabilityList);

        return serviceRegistryDao.createEntity(s);
    }
}
