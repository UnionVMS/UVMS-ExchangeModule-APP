
package eu.europa.ec.fisheries.schema.exchange.service.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}ServiceType">
 *       &lt;sequence>
 *         &lt;element name="settingList" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}SettingListType" minOccurs="0"/>
 *         &lt;element name="capabilityList" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}CapabilityListType" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}StatusType" minOccurs="0"/>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceResponseType", propOrder = {
    "settingList",
    "capabilityList",
    "status",
    "active"
})
public class ServiceResponseType
    extends ServiceType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected SettingListType settingList;
    protected CapabilityListType capabilityList;
    protected StatusType status;
    protected Boolean active;

    /**
     * Gets the value of the settingList property.
     * 
     * @return
     *     possible object is
     *     {@link SettingListType }
     *     
     */
    public SettingListType getSettingList() {
        return settingList;
    }

    /**
     * Sets the value of the settingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettingListType }
     *     
     */
    public void setSettingList(SettingListType value) {
        this.settingList = value;
    }

    /**
     * Gets the value of the capabilityList property.
     * 
     * @return
     *     possible object is
     *     {@link CapabilityListType }
     *     
     */
    public CapabilityListType getCapabilityList() {
        return capabilityList;
    }

    /**
     * Sets the value of the capabilityList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapabilityListType }
     *     
     */
    public void setCapabilityList(CapabilityListType value) {
        this.capabilityList = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

}
