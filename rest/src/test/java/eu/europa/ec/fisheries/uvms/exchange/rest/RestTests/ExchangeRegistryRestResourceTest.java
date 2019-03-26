package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.ExchangePluginMethod;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StartRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.StopRequest;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.JMSHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.Plugin;
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

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ExchangeRegistryRestResourceTest extends BuildExchangeRestTestDeployment {

    @Inject
    ServiceRegistryDaoBean serviceRegistryDao;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getServiceListTest() throws Exception {

        String stringResponse = getWebTarget()
                .path("plugin")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        assertNotNull(stringResponse);
        List<Plugin> response = RestHelper.readResponseDtoList(stringResponse, Plugin.class);
        assertFalse(response.isEmpty());
        assertEquals("STARTED", response.get(0).getStatus());
        assertEquals("ManualMovement", response.get(0).getName());
        assertEquals("ManualMovement", response.get(0).getServiceClassName());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void serviceStopTest() throws Exception {
        JMSHelper jmsHelper = new JMSHelper(connectionFactory);
        String serviceClassName = "Service Class Name " + UUID.randomUUID().toString();

        Service s = RestHelper.createBasicService("Test Service Name:" + UUID.randomUUID().toString(), serviceClassName, PluginType.OTHER);
        s = serviceRegistryDao.createEntity(s);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String stringResponse = getWebTarget()
                .path("plugin/stop")
                .path(serviceClassName)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(s), String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse.contains("true"));
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        StopRequest response = JAXBMarshaller.unmarshallTextMessage(message, StopRequest.class);
        assertEquals(ExchangePluginMethod.STOP, response.getMethod());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void serviceStartTest() throws Exception {
        JMSHelper jmsHelper = new JMSHelper(connectionFactory);
        String serviceClassName = "Service Class Name " + UUID.randomUUID().toString();

        Service s = RestHelper.createBasicService("Test Service Name:" + UUID.randomUUID().toString(), serviceClassName, PluginType.OTHER);
        s = serviceRegistryDao.createEntity(s);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String stringResponse = getWebTarget()
                .path("plugin/start")
                .path(serviceClassName)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(s), String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse.contains("true"));
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        StartRequest response = JAXBMarshaller.unmarshallTextMessage(message, StartRequest.class);
        assertEquals(ExchangePluginMethod.START, response.getMethod());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void serviceStopAndStartNonexistantServiceTest() throws Exception {
        String stringResponse = getWebTarget()
                .path("plugin/stop")
                .path("Non-valid service")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("test"), String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse.contains("Service with service class name: Non-valid service does not exist"));

        stringResponse = getWebTarget()
                .path("plugin/start")
                .path("Non-valid service")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("test"), String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse.contains("Service with service class name: Non-valid service does not exist"));
    }
}
