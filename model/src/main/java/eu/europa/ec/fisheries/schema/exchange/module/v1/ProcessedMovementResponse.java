
package eu.europa.ec.fisheries.schema.exchange.module.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:module.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeBaseRequest">
 *       &lt;sequence>
 *         &lt;element name="orgRequest" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}SetReportMovementType"/>
 *         &lt;element name="movementRefType" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementRefType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgRequest",
    "movementRefType"
})
@XmlRootElement(name = "ProcessedMovementResponse")
public class ProcessedMovementResponse
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected SetReportMovementType orgRequest;
    @XmlElement(required = true)
    protected MovementRefType movementRefType;

    /**
     * Gets the value of the orgRequest property.
     * 
     * @return
     *     possible object is
     *     {@link SetReportMovementType }
     *     
     */
    public SetReportMovementType getOrgRequest() {
        return orgRequest;
    }

    /**
     * Sets the value of the orgRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetReportMovementType }
     *     
     */
    public void setOrgRequest(SetReportMovementType value) {
        this.orgRequest = value;
    }

    /**
     * Gets the value of the movementRefType property.
     * 
     * @return
     *     possible object is
     *     {@link MovementRefType }
     *     
     */
    public MovementRefType getMovementRefType() {
        return movementRefType;
    }

    /**
     * Sets the value of the movementRefType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementRefType }
     *     
     */
    public void setMovementRefType(MovementRefType value) {
        this.movementRefType = value;
    }

}
