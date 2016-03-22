
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:source.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeBaseRequest">
 *       &lt;sequence>
 *         &lt;element name="service" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}ServiceType"/>
 *         &lt;element name="settingList" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}SettingListType"/>
 *         &lt;element name="capabilityList" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}CapabilityListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "settingList",
    "capabilityList"
})
@XmlRootElement(name = "RegisterServiceRequest")
public class RegisterServiceRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ServiceType service;
    @XmlElement(required = true)
    protected SettingListType settingList;
    @XmlElement(required = true)
    protected CapabilityListType capabilityList;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceType }
     *     
     */
    public ServiceType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceType }
     *     
     */
    public void setService(ServiceType value) {
        this.service = value;
    }

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

}
