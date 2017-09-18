package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExchangeModuleRequestMapperTest {

    @Test
    public void createUpdateLogStatusRequest() throws Exception {
        String logGuid = "abc";
        ExchangeLogStatusTypeType newStatus = ExchangeLogStatusTypeType.SUCCESSFUL;

        String result = ExchangeModuleRequestMapper.createUpdateLogStatusRequest(logGuid, newStatus);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:UpdateLogStatusRequest xmlns:ns2=\"urn:module.exchange.schema.fisheries.ec.europa.eu:v1\">\n" +
                "    <method>UPDATE_LOG_STATUS</method>\n" +
                "    <logGuid>abc</logGuid>\n" +
                "    <newStatus>SUCCESSFUL</newStatus>\n" +
                "</ns2:UpdateLogStatusRequest>\n", result);
    }

}