package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;

public class PluginTypeMapperTest {

    @Test
    public void mapWhenEmail() throws Exception {
        assertMapping(PluginType.EMAIL, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.EMAIL);
    }

    @Test
    public void mapWhenFLUX() throws Exception {
        assertMapping(PluginType.FLUX, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.FLUX);
    }

    @Test
    public void mapWhenSatelliteReceiver() throws Exception {
        assertMapping(PluginType.SATELLITE_RECEIVER, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.SATELLITE_RECEIVER);
    }

    @Test
    public void mapWhenNAF() throws Exception {
        assertMapping(PluginType.NAF, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.NAF);
    }

    @Test
    public void mapWhenOther() throws Exception {
        assertMapping(PluginType.OTHER, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.OTHER);
    }

    @Test(expected = NullPointerException.class)
    public void mapWhenNull() {
        PluginTypeMapper.map(null);
    }

    private void assertMapping(PluginType input, eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType expectedOutput) {
        assertEquals(expectedOutput, PluginTypeMapper.map(input));
    }

}