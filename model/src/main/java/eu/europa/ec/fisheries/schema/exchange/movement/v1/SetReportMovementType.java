
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetReportMovementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetReportMovementType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}ReportMovementType">
 *       &lt;sequence>
 *         &lt;element name="movement" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementBaseType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetReportMovementType", propOrder = {
    "movement"
})
public class SetReportMovementType
    extends ReportMovementType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected MovementBaseType movement;

    /**
     * Gets the value of the movement property.
     * 
     * @return
     *     possible object is
     *     {@link MovementBaseType }
     *     
     */
    public MovementBaseType getMovement() {
        return movement;
    }

    /**
     * Sets the value of the movement property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementBaseType }
     *     
     */
    public void setMovement(MovementBaseType value) {
        this.movement = value;
    }

}
