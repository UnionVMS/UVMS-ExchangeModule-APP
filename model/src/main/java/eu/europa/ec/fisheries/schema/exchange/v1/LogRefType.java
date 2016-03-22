
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LogRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LogRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="refGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}TypeRefType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogRefType", propOrder = {
    "refGuid",
    "type"
})
public class LogRefType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String refGuid;
    @XmlElement(required = true)
    protected TypeRefType type;

    /**
     * Gets the value of the refGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefGuid() {
        return refGuid;
    }

    /**
     * Sets the value of the refGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefGuid(String value) {
        this.refGuid = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TypeRefType }
     *     
     */
    public TypeRefType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeRefType }
     *     
     */
    public void setType(TypeRefType value) {
        this.type = value;
    }

}
