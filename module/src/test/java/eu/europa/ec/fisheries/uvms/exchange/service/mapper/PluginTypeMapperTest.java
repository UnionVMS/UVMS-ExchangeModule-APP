package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluginTypeMapperTest {

    @Test
    public void mapWhenEmail() {
        assertMapping(PluginType.EMAIL, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType.EMAIL);
    }

    @Test
    public void mapWhenFLUX() {
        assertMapping(PluginType.FLUX, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType.FLUX);
    }

    @Test
    public void mapWhenSatelliteReceiver() {
        assertMapping(PluginType.SATELLITE_RECEIVER, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType.SATELLITE_RECEIVER);
    }

    @Test
    public void mapWhenNAF() {
        assertMapping(PluginType.NAF, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType.NAF);
    }

    @Test
    public void mapWhenOther() {
        assertMapping(PluginType.OTHER, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType.OTHER);
    }

    @Test(expected = NullPointerException.class)
    public void mapWhenNull() {
        PluginTypeMapper.map(null);
    }

    private void assertMapping(PluginType input, eu.europa.ec.fisheries.schema.movementrules.exchange.v1.PluginType expectedOutput) {
        assertEquals(expectedOutput, PluginTypeMapper.map(input));
    }

}
