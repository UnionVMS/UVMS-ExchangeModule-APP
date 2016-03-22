
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PollStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PollStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pollGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exchangeLogGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogStatusTypeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PollStatus", propOrder = {
    "pollGuid",
    "exchangeLogGuid",
    "status"
})
public class PollStatus
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String pollGuid;
    @XmlElement(required = true)
    protected String exchangeLogGuid;
    @XmlElement(required = true)
    protected ExchangeLogStatusTypeType status;

    /**
     * Gets the value of the pollGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPollGuid() {
        return pollGuid;
    }

    /**
     * Sets the value of the pollGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPollGuid(String value) {
        this.pollGuid = value;
    }

    /**
     * Gets the value of the exchangeLogGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExchangeLogGuid() {
        return exchangeLogGuid;
    }

    /**
     * Sets the value of the exchangeLogGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExchangeLogGuid(String value) {
        this.exchangeLogGuid = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeLogStatusTypeType }
     *     
     */
    public ExchangeLogStatusTypeType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeLogStatusTypeType }
     *     
     */
    public void setStatus(ExchangeLogStatusTypeType value) {
        this.status = value;
    }

}
