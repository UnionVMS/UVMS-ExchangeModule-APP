package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.JMSHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ExchangeAPIRestResourceTest extends BuildExchangeRestTestDeployment {

    @Inject
    ServiceRegistryDaoBean serviceRegistryDao;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getServiceListTest() throws Exception {
        GetServiceListRequest request = new GetServiceListRequest();
        request.getType().add(PluginType.OTHER);
        Client client = ClientBuilder.newClient();

        GetServiceListResponse response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("serviceList")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), GetServiceListResponse.class);

        assertNotNull(response);
        List<ServiceResponseType> responseList = response.getService();
        assertFalse(responseList.isEmpty());
        assertEquals("STARTED", responseList.get(0).getStatus().value());
        assertEquals("ManualMovement", responseList.get(0).getName());
        assertEquals("ManualMovement", responseList.get(0).getServiceClassName());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getServiceListTestFromTheSecurePart() throws Exception {
        GetServiceListRequest request = new GetServiceListRequest();
        request.getType().add(PluginType.OTHER);

        Client client = ClientBuilder.newClient();
        GetServiceListResponse response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("serviceList")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), GetServiceListResponse.class);

        assertNotNull(response);
        List<ServiceResponseType> responseList = response.getService();
        assertFalse(responseList.isEmpty());
        assertEquals("STARTED", responseList.get(0).getStatus().value());
        assertEquals("ManualMovement", responseList.get(0).getName());
        assertEquals("ManualMovement", responseList.get(0).getServiceClassName());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void insertServiceAndGetServiceListTest() throws Exception {
        String serviceClassName = "Service Class Name " + UUID.randomUUID().toString();
        GetServiceListRequest request = new GetServiceListRequest();
        request.getType().add(PluginType.OTHER);
        request.getType().add(PluginType.BELGIAN_ACTIVITY);

        Client client = ClientBuilder.newClient();
        GetServiceListResponse response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("serviceList")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), GetServiceListResponse.class);

        assertNotNull(response);
        int nbrOfPlugins = response.getService().size();
        Service s = RestHelper.createBasicService("Test Service Name:" + UUID.randomUUID().toString(), serviceClassName, PluginType.BELGIAN_ACTIVITY);
        s = serviceRegistryDao.createEntity(s);

        response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("serviceList")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), GetServiceListResponse.class);

        assertNotNull(response);
        assertFalse(response.getService().isEmpty());
        assertEquals(nbrOfPlugins + 1, response.getService().size());
        assertEquals(serviceClassName, response.getService().get(nbrOfPlugins).getServiceClassName());


        serviceRegistryDao.deleteEntity(s.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendCommandToEmailPluginTest() throws Exception {
        String serviceClassName = "Service Class Name " + UUID.randomUUID().toString();
        Service s = RestHelper.createBasicService("Test Service Name:" + UUID.randomUUID().toString(), serviceClassName, PluginType.EMAIL);
        s = serviceRegistryDao.createEntity(s);
        JMSHelper jmsHelper = new JMSHelper(connectionFactory);

        SetCommandRequest request = new SetCommandRequest();
        CommandType commandType = new CommandType();
        commandType.setCommand(CommandTypeType.EMAIL);

        EmailType emailType = new EmailType();
        emailType.setTo("TestEmailTo@test.test");
        emailType.setFrom("TestEmailFrom@test.test");
        emailType.setBody("Test EMail Body");
        emailType.setSubject("Test Email Subject");
        commandType.setEmail(emailType);

        commandType.setPluginName(serviceClassName);
        commandType.setTimestamp(Date.from(Instant.now()));
        commandType.setFwdRule("Test forward rule");
        request.setCommand(commandType);
        request.setDate(new java.util.Date());
        request.setUsername("sendCommandToEmailPluginTest Username");

        Client client = ClientBuilder.newClient();
        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        Response response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("pluginCommand")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertEquals(200, response.getStatus());
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest emailCommand = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest.class);
        assertEquals(serviceClassName, emailCommand.getCommand().getPluginName());
        assertEquals(emailType.getTo(), emailCommand.getCommand().getEmail().getTo());
        assertEquals(ExchangePluginMethod.SET_COMMAND, emailCommand.getMethod());

        serviceRegistryDao.deleteEntity(s.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendCommandToPollPluginTest() throws Exception {
        String serviceClassName = "Service Class Name " + UUID.randomUUID().toString();
        String guid = UUID.randomUUID().toString();
        Service s = RestHelper.createBasicService("Test Service Name:" + UUID.randomUUID().toString(), serviceClassName, PluginType.SATELLITE_RECEIVER);
        s = serviceRegistryDao.createEntity(s);
        JMSHelper jmsHelper = new JMSHelper(connectionFactory);

        SetCommandRequest request = new SetCommandRequest();
        CommandType commandType = new CommandType();
        commandType.setCommand(CommandTypeType.POLL);

        PollType pollType = new PollType();
        pollType.setPollId(guid);
        pollType.setMessage("Test Poll Message");
        pollType.setPollTypeType(PollTypeType.POLL);
        KeyValueType connectId = new KeyValueType();
        connectId.setKey("CONNECT_ID");
        connectId.setValue("dummy connect id");
        pollType.getPollReceiver().add(connectId);
        commandType.setPoll(pollType);

        KeyValueType lesValue = new KeyValueType();
        lesValue.setKey("LES");
        lesValue.setValue("TestLESID");
        pollType.getPollReceiver().add(lesValue);

        commandType.setPluginName(serviceClassName);
        commandType.setTimestamp(Date.from(Instant.now()));
        commandType.setFwdRule("Test forward rule");
        request.setCommand(commandType);
        request.setDate(new Date());
        request.setUsername("sendCommandToPollPluginTest Username");

        Client client = ClientBuilder.newClient();
        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        Response response = client.target("http://localhost:8080/exchangerest/unsecured/rest")
                .path("api")
                .path("pluginCommand")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertEquals(200, response.getStatus());
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest pollCommand = JAXBMarshaller.unmarshallTextMessage(message, eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest.class);
        assertEquals(serviceClassName, pollCommand.getCommand().getPluginName());
        assertEquals(pollType.getPollId(), pollCommand.getCommand().getPoll().getPollId());
        assertEquals(ExchangePluginMethod.SET_COMMAND, pollCommand.getMethod());

        serviceRegistryDao.deleteEntity(s.getId());
    }
}
