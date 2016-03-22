
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1}PluginBaseResponse">
 *       &lt;sequence>
 *         &lt;element name="serviceClassName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="response" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}AcknowledgeType"/>
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
    "serviceClassName",
    "response"
})
@XmlRootElement(name = "AcknowledgeResponse")
public class AcknowledgeResponse
    extends PluginBaseResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String serviceClassName;
    @XmlElement(required = true)
    protected AcknowledgeType response;

    /**
     * Gets the value of the serviceClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceClassName() {
        return serviceClassName;
    }

    /**
     * Sets the value of the serviceClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceClassName(String value) {
        this.serviceClassName = value;
    }

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link AcknowledgeType }
     *     
     */
    public AcknowledgeType getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcknowledgeType }
     *     
     */
    public void setResponse(AcknowledgeType value) {
        this.response = value;
    }

}
