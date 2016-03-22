
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ExchangeHistoryListQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExchangeHistoryListQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}TypeRefType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogStatusTypeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="typeRefDateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="typeRefDateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeHistoryListQuery", propOrder = {
    "type",
    "status",
    "typeRefDateFrom",
    "typeRefDateTo"
})
public class ExchangeHistoryListQuery
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected List<TypeRefType> type;
    protected List<ExchangeLogStatusTypeType> status;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar typeRefDateFrom;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar typeRefDateTo;

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeRefType }
     * 
     * 
     */
    public List<TypeRefType> getType() {
        if (type == null) {
            type = new ArrayList<TypeRefType>();
        }
        return this.type;
    }

    /**
     * Gets the value of the status property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the status property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExchangeLogStatusTypeType }
     * 
     * 
     */
    public List<ExchangeLogStatusTypeType> getStatus() {
        if (status == null) {
            status = new ArrayList<ExchangeLogStatusTypeType>();
        }
        return this.status;
    }

    /**
     * Gets the value of the typeRefDateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTypeRefDateFrom() {
        return typeRefDateFrom;
    }

    /**
     * Sets the value of the typeRefDateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTypeRefDateFrom(XMLGregorianCalendar value) {
        this.typeRefDateFrom = value;
    }

    /**
     * Gets the value of the typeRefDateTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTypeRefDateTo() {
        return typeRefDateTo;
    }

    /**
     * Sets the value of the typeRefDateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTypeRefDateTo(XMLGregorianCalendar value) {
        this.typeRefDateTo = value;
    }

}
