package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.schema.exchange.v1.SourceType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ConfigResourceTest extends BuildExchangeRestTestDeployment {

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getConfigSearchFields() throws Exception {
        String stringResponse = getWebTarget()
                .path("config")
                .path("searchfields")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        for (SearchField s: SearchField.values()) {
            assertTrue(stringResponse.contains(s.value()));
        }
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getConfigurationTest() throws Exception {
        String stringResponse = getWebTarget()
                .path("config")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        for (ExchangeLogStatusTypeType e: ExchangeLogStatusTypeType.values()) {
            assertTrue(stringResponse.contains(e.value()));
        }
        for (TypeRefType e: TypeRefType.values()) {
            assertTrue(stringResponse.contains(e.value()));
        }
        for (SourceType e: SourceType.values()) {
            assertTrue(stringResponse.contains(e.value()));
        }
    }

}
