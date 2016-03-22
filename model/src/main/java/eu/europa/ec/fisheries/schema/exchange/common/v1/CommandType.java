
package eu.europa.ec.fisheries.schema.exchange.common.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;


/**
 * <p>Java class for CommandType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommandType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pluginName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="command" type="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}CommandTypeType"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;choice>
 *           &lt;element name="poll" type="{urn:types.plugin.exchange.schema.fisheries.ec.europa.eu:v1}PollType"/>
 *           &lt;element name="email" type="{urn:types.plugin.exchange.schema.fisheries.ec.europa.eu:v1}EmailType"/>
 *         &lt;/choice>
 *         &lt;element name="unsentMessageGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommandType", propOrder = {
    "pluginName",
    "command",
    "timestamp",
    "poll",
    "email",
    "unsentMessageGuid"
})
public class CommandType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String pluginName;
    @XmlElement(required = true)
    protected CommandTypeType command;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    protected PollType poll;
    protected EmailType email;
    @XmlElement(required = true)
    protected String unsentMessageGuid;

    /**
     * Gets the value of the pluginName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Sets the value of the pluginName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPluginName(String value) {
        this.pluginName = value;
    }

    /**
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link CommandTypeType }
     *     
     */
    public CommandTypeType getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommandTypeType }
     *     
     */
    public void setCommand(CommandTypeType value) {
        this.command = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the poll property.
     * 
     * @return
     *     possible object is
     *     {@link PollType }
     *     
     */
    public PollType getPoll() {
        return poll;
    }

    /**
     * Sets the value of the poll property.
     * 
     * @param value
     *     allowed object is
     *     {@link PollType }
     *     
     */
    public void setPoll(PollType value) {
        this.poll = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link EmailType }
     *     
     */
    public EmailType getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailType }
     *     
     */
    public void setEmail(EmailType value) {
        this.email = value;
    }

    /**
     * Gets the value of the unsentMessageGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnsentMessageGuid() {
        return unsentMessageGuid;
    }

    /**
     * Sets the value of the unsentMessageGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsentMessageGuid(String value) {
        this.unsentMessageGuid = value;
    }

}
