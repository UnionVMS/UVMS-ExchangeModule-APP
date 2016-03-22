
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
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
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="senderReceiver" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateRecieved" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="type" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}LogType"/>
 *         &lt;element name="incoming" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="typeRef" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}LogRefType"/>
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
@XmlType(name = "ExchangeLogType", propOrder = {
    "guid",
    "senderReceiver",
    "dateRecieved",
    "type",
    "incoming",
    "typeRef",
    "status"
})
@XmlSeeAlso({
    SendPollType.class,
    SendMovementType.class,
    ReceiveMovementType.class,
    SendEmailType.class
})
public abstract class ExchangeLogType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String guid;
    @XmlElement(required = true)
    protected String senderReceiver;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateRecieved;
    @XmlElement(required = true)
    protected LogType type;
    protected boolean incoming;
    @XmlElement(required = true)
    protected LogRefType typeRef;
    @XmlElement(required = true)
    protected ExchangeLogStatusTypeType status;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the senderReceiver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderReceiver() {
        return senderReceiver;
    }

    /**
     * Sets the value of the senderReceiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderReceiver(String value) {
        this.senderReceiver = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link LogType }
     *     
     */
    public LogType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link LogType }
     *     
     */
    public void setType(LogType value) {
        this.type = value;
    }

    /**
     * Gets the value of the incoming property.
     * 
     */
    public boolean isIncoming() {
        return incoming;
    }

    /**
     * Sets the value of the incoming property.
     * 
     */
    public void setIncoming(boolean value) {
        this.incoming = value;
    }

    /**
     * Gets the value of the typeRef property.
     * 
     * @return
     *     possible object is
     *     {@link LogRefType }
     *     
     */
    public LogRefType getTypeRef() {
        return typeRef;
    }

    /**
     * Sets the value of the typeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link LogRefType }
     *     
     */
    public void setTypeRef(LogRefType value) {
        this.typeRef = value;
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
