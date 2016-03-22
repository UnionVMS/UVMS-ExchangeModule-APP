
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnsentMessageTypePropertyKey.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UnsentMessageTypePropertyKey">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EMAIL"/>
 *     &lt;enumeration value="ASSET_NAME"/>
 *     &lt;enumeration value="IRCS"/>
 *     &lt;enumeration value="LONGITUDE"/>
 *     &lt;enumeration value="LATITUDE"/>
 *     &lt;enumeration value="POLL_TYPE"/>
 *     &lt;enumeration value="POSITION_TIME"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UnsentMessageTypePropertyKey")
@XmlEnum
public enum UnsentMessageTypePropertyKey {

    EMAIL,
    ASSET_NAME,
    IRCS,
    LONGITUDE,
    LATITUDE,
    POLL_TYPE,
    POSITION_TIME;

    public String value() {
        return name();
    }

    public static UnsentMessageTypePropertyKey fromValue(String v) {
        return valueOf(v);
    }

}
