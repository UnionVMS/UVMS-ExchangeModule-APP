
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;


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
 *         &lt;element name="query" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeListQuery"/>
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
    "query"
})
@XmlRootElement(name = "GetLogListByQueryRequest")
public class GetLogListByQueryRequest
    extends ExchangeBaseRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeListQuery query;

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeListQuery }
     *     
     */
    public ExchangeListQuery getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeListQuery }
     *     
     */
    public void setQuery(ExchangeListQuery value) {
        this.query = value;
    }

}
