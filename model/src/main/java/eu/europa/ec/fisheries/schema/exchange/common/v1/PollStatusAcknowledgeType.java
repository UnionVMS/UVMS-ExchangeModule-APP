
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;


/**
 * <p>Java class for PollStatusAcknowledgeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PollStatusAcknowledgeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogStatusTypeType"/>
 *         &lt;element name="pollId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PollStatusAcknowledgeType", propOrder = {
    "status",
    "pollId"
})
public class PollStatusAcknowledgeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeLogStatusTypeType status;
    @XmlElement(required = true)
    protected String pollId;

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

    /**
     * Gets the value of the pollId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPollId() {
        return pollId;
    }

    /**
     * Sets the value of the pollId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPollId(String value) {
        this.pollId = value;
    }

}
