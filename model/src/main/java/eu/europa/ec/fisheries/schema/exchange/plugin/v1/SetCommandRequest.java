
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;


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
 *         &lt;element name="command" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}CommandType"/>
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
    "command"
})
@XmlRootElement(name = "SetCommandRequest")
public class SetCommandRequest
    extends PluginBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected CommandType command;

    /**
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link CommandType }
     *     
     */
    public CommandType getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommandType }
     *     
     */
    public void setCommand(CommandType value) {
        this.command = value;
    }

}
