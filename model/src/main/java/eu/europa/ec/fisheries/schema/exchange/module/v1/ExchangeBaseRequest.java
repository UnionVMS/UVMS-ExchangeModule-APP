
package eu.europa.ec.fisheries.schema.exchange.module.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeBaseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExchangeBaseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="method" type="{urn:module.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeModuleMethod"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeBaseRequest", propOrder = {
    "method"
})
@XmlSeeAlso({
    GetServiceListRequest.class,
    GetServiceSettingsRequest.class,
    UnregisterServiceRequest.class,
    GetServiceCapabilitiesRequest.class,
    RegisterServiceRequest.class,
    CreatePollRequest.class
})
public abstract class ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeModuleMethod method;

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeModuleMethod }
     *     
     */
    public ExchangeModuleMethod getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeModuleMethod }
     *     
     */
    public void setMethod(ExchangeModuleMethod value) {
        this.method = value;
    }

}
