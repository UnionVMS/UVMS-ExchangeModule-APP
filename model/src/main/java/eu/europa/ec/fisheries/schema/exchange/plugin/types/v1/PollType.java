
package eu.europa.ec.fisheries.schema.exchange.plugin.types.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;


/**
 * <p>Java class for PollType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PollType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pollId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pollTypeType" type="{urn:types.plugin.exchange.schema.fisheries.ec.europa.eu:v1}PollTypeType"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pollReceiver" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}keyValueType" maxOccurs="unbounded"/>
 *         &lt;element name="pollPayload" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}keyValueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PollType", propOrder = {
    "pollId",
    "pollTypeType",
    "message",
    "pollReceiver",
    "pollPayload"
})
public class PollType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String pollId;
    @XmlElement(required = true)
    protected PollTypeType pollTypeType;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(required = true)
    protected List<KeyValueType> pollReceiver;
    protected List<KeyValueType> pollPayload;

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

    /**
     * Gets the value of the pollTypeType property.
     * 
     * @return
     *     possible object is
     *     {@link PollTypeType }
     *     
     */
    public PollTypeType getPollTypeType() {
        return pollTypeType;
    }

    /**
     * Sets the value of the pollTypeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PollTypeType }
     *     
     */
    public void setPollTypeType(PollTypeType value) {
        this.pollTypeType = value;
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
     * Gets the value of the pollReceiver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pollReceiver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPollReceiver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyValueType }
     * 
     * 
     */
    public List<KeyValueType> getPollReceiver() {
        if (pollReceiver == null) {
            pollReceiver = new ArrayList<KeyValueType>();
        }
        return this.pollReceiver;
    }

    /**
     * Gets the value of the pollPayload property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pollPayload property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPollPayload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyValueType }
     * 
     * 
     */
    public List<KeyValueType> getPollPayload() {
        if (pollPayload == null) {
            pollPayload = new ArrayList<KeyValueType>();
        }
        return this.pollPayload;
    }

}
