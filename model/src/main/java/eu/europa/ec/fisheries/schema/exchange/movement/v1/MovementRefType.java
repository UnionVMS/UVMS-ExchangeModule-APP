
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MovementRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MovementRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="movementRefGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementRefTypeType"/>
 *         &lt;element name="ackResponseMessageID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovementRefType", propOrder = {
    "movementRefGuid",
    "type",
    "ackResponseMessageID"
})
public class MovementRefType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String movementRefGuid;
    @XmlElement(required = true)
    protected MovementRefTypeType type;
    @XmlElement(required = true)
    protected String ackResponseMessageID;

    /**
     * Gets the value of the movementRefGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMovementRefGuid() {
        return movementRefGuid;
    }

    /**
     * Sets the value of the movementRefGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMovementRefGuid(String value) {
        this.movementRefGuid = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link MovementRefTypeType }
     *     
     */
    public MovementRefTypeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementRefTypeType }
     *     
     */
    public void setType(MovementRefTypeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the ackResponseMessageID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAckResponseMessageID() {
        return ackResponseMessageID;
    }

    /**
     * Sets the value of the ackResponseMessageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAckResponseMessageID(String value) {
        this.ackResponseMessageID = value;
    }

}
