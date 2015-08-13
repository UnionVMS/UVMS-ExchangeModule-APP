
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalNumberOfPages" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="currentPage" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="exchangeLogs" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogType" maxOccurs="unbounded" minOccurs="0"/>
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
    "totalNumberOfPages",
    "currentPage",
    "exchangeLogs"
})
@XmlRootElement(name = "GetLogListByQueryResponse")
public class GetLogListByQueryResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected BigInteger totalNumberOfPages;
    @XmlElement(required = true)
    protected BigInteger currentPage;
    protected List<ExchangeLogType> exchangeLogs;

    /**
     * Gets the value of the totalNumberOfPages property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    /**
     * Sets the value of the totalNumberOfPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalNumberOfPages(BigInteger value) {
        this.totalNumberOfPages = value;
    }

    /**
     * Gets the value of the currentPage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the value of the currentPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCurrentPage(BigInteger value) {
        this.currentPage = value;
    }

    /**
     * Gets the value of the exchangeLogs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exchangeLogs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExchangeLogs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExchangeLogType }
     * 
     * 
     */
    public List<ExchangeLogType> getExchangeLogs() {
        if (exchangeLogs == null) {
            exchangeLogs = new ArrayList<ExchangeLogType>();
        }
        return this.exchangeLogs;
    }

}
