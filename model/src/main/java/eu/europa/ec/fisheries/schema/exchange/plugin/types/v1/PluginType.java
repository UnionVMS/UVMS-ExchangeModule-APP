
package eu.europa.ec.fisheries.schema.exchange.plugin.types.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PluginType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PluginType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EMAIL"/>
 *     &lt;enumeration value="SATELLITE_RECEIVER"/>
 *     &lt;enumeration value="FLUX"/>
 *     &lt;enumeration value="MANUAL"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="NAF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PluginType")
@XmlEnum
public enum PluginType {

    EMAIL,
    SATELLITE_RECEIVER,
    FLUX,
    MANUAL,
    OTHER,
    NAF;

    public String value() {
        return name();
    }

    public static PluginType fromValue(String v) {
        return valueOf(v);
    }

}
