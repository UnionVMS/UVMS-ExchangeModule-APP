
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MovementRefTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MovementRefTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MOVEMENT"/>
 *     &lt;enumeration value="ALARM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MovementRefTypeType")
@XmlEnum
public enum MovementRefTypeType {

    MOVEMENT,
    ALARM;

    public String value() {
        return name();
    }

    public static MovementRefTypeType fromValue(String v) {
        return valueOf(v);
    }

}
