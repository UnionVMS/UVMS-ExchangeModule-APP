
package eu.europa.ec.fisheries.schema.exchange.module.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.poll.v1.PollType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:module.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeBaseRequest">
 *       &lt;sequence>
 *         &lt;element name="poll" type="{urn:poll.exchange.schema.fisheries.ec.europa.eu:v1}PollType"/>
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
    "poll"
})
@XmlRootElement(name = "CreatePollRequest")
public class CreatePollRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected PollType poll;

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

}
