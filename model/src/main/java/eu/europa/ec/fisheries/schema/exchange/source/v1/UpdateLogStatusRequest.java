
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:source.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeBaseRequest">
 *       &lt;sequence>
 *         &lt;element name="status" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogStatusType"/>
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
    "status"
})
@XmlRootElement(name = "UpdateLogStatusRequest")
public class UpdateLogStatusRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeLogStatusType status;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeLogStatusType }
     *     
     */
    public ExchangeLogStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeLogStatusType }
     *     
     */
    public void setStatus(ExchangeLogStatusType value) {
        this.status = value;
    }

}
