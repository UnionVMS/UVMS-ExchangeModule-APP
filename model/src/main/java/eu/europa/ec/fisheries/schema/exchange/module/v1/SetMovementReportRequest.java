
package eu.europa.ec.fisheries.schema.exchange.module.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element name="request" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}SetReportMovementType"/>
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
    "request"
})
@XmlRootElement(name = "SetMovementReportRequest")
public class SetMovementReportRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected SetReportMovementType request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link SetReportMovementType }
     *     
     */
    public SetReportMovementType getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetReportMovementType }
     *     
     */
    public void setRequest(SetReportMovementType value) {
        this.request = value;
    }

}
