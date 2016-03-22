
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcknowledgeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AcknowledgeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}AcknowledgeTypeType"/>
 *         &lt;element name="pollStatus" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}PollStatusAcknowledgeType" minOccurs="0"/>
 *         &lt;element name="unsentMessageGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcknowledgeType", propOrder = {
    "messageId",
    "message",
    "type",
    "pollStatus",
    "unsentMessageGuid"
})
public class AcknowledgeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String messageId;
    protected String message;
    @XmlElement(required = true)
    protected AcknowledgeTypeType type;
    protected PollStatusAcknowledgeType pollStatus;
    @XmlElement(required = true)
    protected String unsentMessageGuid;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AcknowledgeTypeType }
     *     
     */
    public AcknowledgeTypeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcknowledgeTypeType }
     *     
     */
    public void setType(AcknowledgeTypeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the pollStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PollStatusAcknowledgeType }
     *     
     */
    public PollStatusAcknowledgeType getPollStatus() {
        return pollStatus;
    }

    /**
     * Sets the value of the pollStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PollStatusAcknowledgeType }
     *     
     */
    public void setPollStatus(PollStatusAcknowledgeType value) {
        this.pollStatus = value;
    }

    /**
     * Gets the value of the unsentMessageGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnsentMessageGuid() {
        return unsentMessageGuid;
    }

    /**
     * Sets the value of the unsentMessageGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsentMessageGuid(String value) {
        this.unsentMessageGuid = value;
    }

}
