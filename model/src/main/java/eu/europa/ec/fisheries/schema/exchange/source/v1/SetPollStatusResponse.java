
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.PollStatus;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exchangeLog" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}PollStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "exchangeLog"
})
@XmlRootElement(name = "SetPollStatusResponse")
public class SetPollStatusResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected PollStatus exchangeLog;

    /**
     * Gets the value of the exchangeLog property.
     * 
     * @return
     *     possible object is
     *     {@link PollStatus }
     *     
     */
    public PollStatus getExchangeLog() {
        return exchangeLog;
    }

    /**
     * Sets the value of the exchangeLog property.
     * 
     * @param value
     *     allowed object is
     *     {@link PollStatus }
     *     
     */
    public void setExchangeLog(PollStatus value) {
        this.exchangeLog = value;
    }

}
