
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn:source.exchange.schema.fisheries.ec.europa.eu:v1}ExchangeBaseRequest">
 *       &lt;element name="exchangeLog" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogType"/>
 *     &lt;/extension>
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
@XmlRootElement(name = "CreateLogRequest")
public class CreateLogRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeLogType exchangeLog;

    /**
     * Gets the value of the exchangeLog property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeLogType }
     *     
     */
    public ExchangeLogType getExchangeLog() {
        return exchangeLog;
    }

    /**
     * Sets the value of the exchangeLog property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeLogType }
     *     
     */
    public void setExchangeLog(ExchangeLogType value) {
        this.exchangeLog = value;
    }

}
