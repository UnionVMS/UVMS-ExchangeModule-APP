package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ExchangeAPIResourceUnsecuredTest extends BuildExchangeRestTestDeployment {

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
}
