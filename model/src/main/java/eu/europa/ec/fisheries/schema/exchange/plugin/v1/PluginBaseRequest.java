
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PluginBaseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PluginBaseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="method" type="{urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1}ExchangePluginMethod"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PluginBaseRequest", propOrder = {
    "method"
})
@XmlSeeAlso({
    StartRequest.class,
    PingResponse.class,
    StopRequest.class,
    PingRequest.class,
    SetReportRequest.class,
    SetConfigRequest.class,
    SetCommandRequest.class
})
public abstract class PluginBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangePluginMethod method;

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangePluginMethod }
     *     
     */
    public ExchangePluginMethod getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangePluginMethod }
     *     
     */
    public void setMethod(ExchangePluginMethod value) {
        this.method = value;
    }

}
