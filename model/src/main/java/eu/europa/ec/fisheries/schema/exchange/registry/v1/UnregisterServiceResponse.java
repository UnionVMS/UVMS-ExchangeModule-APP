
package eu.europa.ec.fisheries.schema.exchange.registry.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:registry.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeRegistryBaseRequest">
 *       &lt;sequence>
 *         &lt;element name="ack" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}AcknowledgeType"/>
 *         &lt;element name="service" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}ServiceType"/>
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
    "ack",
    "service"
})
@XmlRootElement(name = "UnregisterServiceResponse")
public class UnregisterServiceResponse
    extends ExchangeRegistryBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected AcknowledgeType ack;
    @XmlElement(required = true)
    protected ServiceType service;

    /**
     * Gets the value of the ack property.
     * 
     * @return
     *     possible object is
     *     {@link AcknowledgeType }
     *     
     */
    public AcknowledgeType getAck() {
        return ack;
    }

    /**
     * Sets the value of the ack property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcknowledgeType }
     *     
     */
    public void setAck(AcknowledgeType value) {
        this.ack = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceType }
     *     
     */
    public ServiceType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceType }
     *     
     */
    public void setService(ServiceType value) {
        this.service = value;
    }

}
