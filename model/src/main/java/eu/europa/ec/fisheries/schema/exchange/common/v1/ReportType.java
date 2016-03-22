
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.RecipientInfoType;


/**
 * <p>Java class for ReportType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReportType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}ReportTypeType"/>
 *         &lt;element name="recipient" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recipientInfo" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}RecipientInfoType" maxOccurs="unbounded"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;choice>
 *           &lt;element name="movement" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementType"/>
 *         &lt;/choice>
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
@XmlType(name = "ReportType", propOrder = {
    "type",
    "recipient",
    "recipientInfo",
    "timestamp",
    "movement",
    "unsentMessageGuid"
})
public class ReportType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ReportTypeType type;
    @XmlElement(required = true)
    protected String recipient;
    @XmlElement(required = true)
    protected List<RecipientInfoType> recipientInfo;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    protected MovementType movement;
    @XmlElement(required = true)
    protected String unsentMessageGuid;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTypeType }
     *     
     */
    public ReportTypeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTypeType }
     *     
     */
    public void setType(ReportTypeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the recipient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the value of the recipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipient(String value) {
        this.recipient = value;
    }

    /**
     * Gets the value of the recipientInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recipientInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecipientInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecipientInfoType }
     * 
     * 
     */
    public List<RecipientInfoType> getRecipientInfo() {
        if (recipientInfo == null) {
            recipientInfo = new ArrayList<RecipientInfoType>();
        }
        return this.recipientInfo;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the movement property.
     * 
     * @return
     *     possible object is
     *     {@link MovementType }
     *     
     */
    public MovementType getMovement() {
        return movement;
    }

    /**
     * Sets the value of the movement property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementType }
     *     
     */
    public void setMovement(MovementType value) {
        this.movement = value;
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
