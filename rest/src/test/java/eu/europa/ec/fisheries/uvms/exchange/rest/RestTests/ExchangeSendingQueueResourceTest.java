package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;


import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.dao.UnsentMessageDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.JMSHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ExchangeSendingQueueResourceTest extends BuildExchangeRestTestDeployment {

    @Inject
    ExchangeLogService exchangeLogService;

    @Inject
    UnsentMessageDao unsentMessageDao;

    @Inject
    ServiceRegistryDao serviceRegistryDao;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getSendingQueueTest() throws Exception {
        String unsentMessageId = exchangeLogService.createUnsentMessage("Sending queue test senderReceiver", Instant.now(), "Sending queue test recipient", "Sending queue test message", new ArrayList<>(), "Sending queue test username");

        String stringResponse = getWebTarget()
                .path("sendingqueue")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse.contains(unsentMessageId));
        List<SendingGroupLog> response = RestHelper.readResponseDtoList(stringResponse, SendingGroupLog.class);
        assertFalse(response.isEmpty());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendTest() throws Exception {
        JMSHelper jmsHelper = new JMSHelper(connectionFactory);
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
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals(response.getCommand().getCommand(), CommandTypeType.EMAIL);
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000);     //to allow the db to sync up
        List<UnsentMessage> unsentMessageList = unsentMessageDao.getAll();
        assertEquals(sizeB4 + 1, unsentMessageList.size());

        List<String> unsentMessagesIdList = new ArrayList<>();
        for (UnsentMessage u: unsentMessageList) {
            unsentMessagesIdList.add(u.getGuid().toString());
        }



        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String stringResponse = getWebTarget()
                .path("sendingqueue")
                .path("send")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(unsentMessagesIdList), String.class);

        TextMessage resentMessage = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest resentResponse = JAXBMarshaller.unmarshallTextMessage(resentMessage, SetCommandRequest.class);

        assertNotNull(resentResponse);
        assertEquals(resentResponse.getCommand().getCommand(), CommandTypeType.EMAIL);
        assertEquals(resentResponse.getCommand().getPluginName(), serviceClassName);

        serviceRegistryDao.deleteEntity(service.getId());

    }


    private Service createAndPersistBasicService(String name, String serviceClassName, PluginType pluginType) throws Exception{
        Service s = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        if(s != null){
            serviceRegistryDao.deleteEntity(s.getId());
            Thread.sleep(1000);
        }

        s = new Service();
        s.setActive(true);
        s.setDescription("Test description");
        s.setName(name);
        s.setSatelliteType(null);
        s.setServiceClassName(serviceClassName);
        s.setServiceResponse(serviceClassName + "PLUGIN_RESPONSE");
        s.setStatus(true);
        s.setType(pluginType);
        s.setUpdated(Instant.now());
        s.setUpdatedBy("Exchange Tests");

        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
        ServiceCapability serviceCapability = new ServiceCapability();
        serviceCapability.setService(s);
        serviceCapability.setUpdatedBy("Exchange Tests");
        serviceCapability.setUpdatedTime(Instant.now());
        serviceCapability.setCapability(CapabilityTypeType.POLLABLE);
        serviceCapability.setValue(true);
        serviceCapabilityList.add(serviceCapability);
        s.setServiceCapabilityList(serviceCapabilityList);

        s.setServiceSettingList(new ArrayList<>());

        return serviceRegistryDao.createEntity(s);
    }
}
