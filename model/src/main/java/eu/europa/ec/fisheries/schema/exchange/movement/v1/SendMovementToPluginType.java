
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for SendMovementToPluginType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SendMovementToPluginType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}ReportMovementType">
 *       &lt;sequence>
 *         &lt;element name="fwdRule" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fwdDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="recipient" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recipientInfo" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}RecipientInfoType" maxOccurs="unbounded"/>
 *         &lt;element name="movement" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementType"/>
 *         &lt;element name="assetName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ircs" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendMovementToPluginType", propOrder = {
    "fwdRule",
    "fwdDate",
    "recipient",
    "recipientInfo",
    "movement",
    "assetName",
    "ircs"
})
public class SendMovementToPluginType
    extends ReportMovementType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String fwdRule;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fwdDate;
    @XmlElement(required = true)
    protected String recipient;
    @XmlElement(required = true)
    protected List<RecipientInfoType> recipientInfo;
    @XmlElement(required = true)
    protected MovementType movement;
    @XmlElement(required = true)
    protected String assetName;
    @XmlElement(required = true)
    protected String ircs;

    /**
     * Gets the value of the fwdRule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFwdRule() {
        return fwdRule;
    }

    /**
     * Sets the value of the fwdRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFwdRule(String value) {
        this.fwdRule = value;
    }

    /**
     * Gets the value of the fwdDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFwdDate() {
        return fwdDate;
    }

    /**
     * Sets the value of the fwdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFwdDate(XMLGregorianCalendar value) {
        this.fwdDate = value;
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
     * Gets the value of the assetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Sets the value of the assetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetName(String value) {
        this.assetName = value;
    }

    /**
     * Gets the value of the ircs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrcs() {
        return ircs;
    }

    /**
     * Sets the value of the ircs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrcs(String value) {
        this.ircs = value;
    }

}
