
package eu.europa.ec.fisheries.schema.exchange.source.v1;

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
 *         &lt;element name="method" type="{urn:source.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeDataSourceMethod"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "method",
    "username"
})
@XmlSeeAlso({
    GetServiceRequest.class,
    CreateLogRequest.class,
    UnregisterServiceRequest.class,
    GetServiceCapabilitiesRequest.class,
    RegisterServiceRequest.class,
    GetUnsentMessageListRequest.class,
    PingRequest.class,
    GetLogStatusHistoryRequest.class,
    GetLogStatusHistoryByQueryRequest.class,
    UpdateLogStatusRequest.class,
    GetExchangeLogRequest.class,
    SetServiceSettingsRequest.class,
    GetServiceListRequest.class,
    GetServiceSettingsRequest.class,
    ResendMessageRequest.class,
    CreateUnsentMessageRequest.class,
    GetLogListByQueryRequest.class,
    SetPollStatusRequest.class,
    SetServiceStatusRequest.class,
    RemoveUnsentMessageRequest.class
})
public abstract class ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeDataSourceMethod method;
    @XmlElement(required = true)
    protected String username;

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeDataSourceMethod }
     *     
     */
    public ExchangeDataSourceMethod getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeDataSourceMethod }
     *     
     */
    public void setMethod(ExchangeDataSourceMethod value) {
        this.method = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
