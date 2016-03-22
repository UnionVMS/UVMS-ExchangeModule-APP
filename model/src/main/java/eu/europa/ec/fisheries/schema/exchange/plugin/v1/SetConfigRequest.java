
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:plugin.exchange.schema.fisheries.ec.europa.eu:v1}PluginBaseRequest">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="configurations" type="{urn:service.exchange.schema.fisheries.ec.europa.eu:v1}SettingListType"/>
 *         &lt;/sequence>
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
    "configurations"
})
@XmlRootElement(name = "SetConfigRequest")
public class SetConfigRequest
    extends PluginBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected SettingListType configurations;

    /**
     * Gets the value of the configurations property.
     * 
     * @return
     *     possible object is
     *     {@link SettingListType }
     *     
     */
    public SettingListType getConfigurations() {
        return configurations;
    }

    /**
     * Sets the value of the configurations property.
     * 
     * @param value
     *     allowed object is
     *     {@link SettingListType }
     *     
     */
    public void setConfigurations(SettingListType value) {
        this.configurations = value;
    }

}
