package eu.europa.ec.fisheries.uvms.exchange.client;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchFields;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ExchangeClientTest extends AbstractClientTest {

    @Inject
    ExchangeRestClient exchangeRestClient;
    
    @Before
    public void before() throws NamingException{
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/exchange_endpoint", "http://localhost:8080/exchange/rest");
    }

    @Test
    @OperateOnDeployment("normal")
    public void getServiceListTest() throws Exception{
        GetServiceListRequest request = new GetServiceListRequest();
        request.getType().add(PluginType.OTHER);
        request.getType().add(PluginType.BELGIAN_ACTIVITY);

        GetServiceListResponse serviceList = exchangeRestClient.getServiceList(request);
        assertNotNull(serviceList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void sendEmailTest() throws Exception{
        exchangeRestClient.sendEmail(new EmailType());  //just testing that we reach the endpoint

    }

    @Test
    @OperateOnDeployment("normal")
    public void sendCommandTest() throws Exception{
        try {
            exchangeRestClient.sendCommandToPlugin(new SetCommandRequest());  //just testing that we reach the endpoint

        }catch (RuntimeException e){
            assertTrue(e.getMessage().startsWith("java.lang.RuntimeException: Errormessage from exchange:"));
        }
    }

}
