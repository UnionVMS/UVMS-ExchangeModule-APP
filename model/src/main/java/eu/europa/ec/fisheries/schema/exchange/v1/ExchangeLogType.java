
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ExchangeLogType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExchangeLogType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="terminalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="terminalType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="assetId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pollTrackId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateRecieved" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="sentBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fwdRule" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recipient" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateFwd" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="status" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogStatusType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeLogType", propOrder = {
    "terminalId",
    "terminalType",
    "assetId",
    "pollTrackId",
    "dateRecieved",
    "sentBy",
    "message",
    "fwdRule",
    "recipient",
    "dateFwd",
    "status"
})
public class ExchangeLogType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String terminalId;
    @XmlElement(required = true)
    protected String terminalType;
    @XmlElement(required = true)
    protected String assetId;
    @XmlElement(required = true)
    protected String pollTrackId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateRecieved;
    @XmlElement(required = true)
    protected String sentBy;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(required = true)
    protected String fwdRule;
    @XmlElement(required = true)
    protected String recipient;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateFwd;
    @XmlElement(required = true)
    protected ExchangeLogStatusType status;

    /**
     * Gets the value of the terminalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * Sets the value of the terminalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalId(String value) {
        this.terminalId = value;
    }

    /**
     * Gets the value of the terminalType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * Sets the value of the terminalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTerminalType(String value) {
        this.terminalType = value;
    }

    /**
     * Gets the value of the assetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * Sets the value of the assetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetId(String value) {
        this.assetId = value;
    }

    /**
     * Gets the value of the pollTrackId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPollTrackId() {
        return pollTrackId;
    }

    /**
     * Sets the value of the pollTrackId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPollTrackId(String value) {
        this.pollTrackId = value;
    }

    /**
     * Gets the value of the dateRecieved property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateRecieved() {
        return dateRecieved;
    }

    /**
     * Sets the value of the dateRecieved property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateRecieved(XMLGregorianCalendar value) {
        this.dateRecieved = value;
    }

    /**
     * Gets the value of the sentBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSentBy() {
        return sentBy;
    }

    /**
     * Sets the value of the sentBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSentBy(String value) {
        this.sentBy = value;
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
     * Gets the value of the dateFwd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFwd() {
        return dateFwd;
    }

    /**
     * Sets the value of the dateFwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateFwd(XMLGregorianCalendar value) {
        this.dateFwd = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeLogStatusType }
     *     
     */
    public ExchangeLogStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeLogStatusType }
     *     
     */
    public void setStatus(ExchangeLogStatusType value) {
        this.status = value;
    }

}
