
package eu.europa.ec.fisheries.schema.exchange.service.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;


/**
 * <p>Java class for ServiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceResponseMessageName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="serviceClassName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pluginType" type="{urn:types.plugin.exchange.schema.fisheries.ec.europa.eu:v1}PluginType"/>
 *         &lt;element name="satelliteType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType", propOrder = {
    "serviceResponseMessageName",
    "serviceClassName",
    "name",
    "description",
    "pluginType",
    "satelliteType"
})
@XmlSeeAlso({
    ServiceResponseType.class
})
public class ServiceType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String serviceResponseMessageName;
    @XmlElement(required = true)
    protected String serviceClassName;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected PluginType pluginType;
    protected String satelliteType;

    /**
     * Gets the value of the serviceResponseMessageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceResponseMessageName() {
        return serviceResponseMessageName;
    }

    /**
     * Sets the value of the serviceResponseMessageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceResponseMessageName(String value) {
        this.serviceResponseMessageName = value;
    }

    /**
     * Gets the value of the serviceClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceClassName() {
        return serviceClassName;
    }

    /**
     * Sets the value of the serviceClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceClassName(String value) {
        this.serviceClassName = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the pluginType property.
     * 
     * @return
     *     possible object is
     *     {@link PluginType }
     *     
     */
    public PluginType getPluginType() {
        return pluginType;
    }

    /**
     * Sets the value of the pluginType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PluginType }
     *     
     */
    public void setPluginType(PluginType value) {
        this.pluginType = value;
    }

    /**
     * Gets the value of the satelliteType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSatelliteType() {
        return satelliteType;
    }

    /**
     * Sets the value of the satelliteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSatelliteType(String value) {
        this.satelliteType = value;
    }

}
