
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeListQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExchangeListQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pagination" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeListPagination"/>
 *         &lt;element name="exchangeSearchCriteria" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeListCriteria"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeListQuery", propOrder = {
    "pagination",
    "exchangeSearchCriteria"
})
public class ExchangeListQuery
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ExchangeListPagination pagination;
    @XmlElement(required = true)
    protected ExchangeListCriteria exchangeSearchCriteria;

    /**
     * Gets the value of the pagination property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeListPagination }
     *     
     */
    public ExchangeListPagination getPagination() {
        return pagination;
    }

    /**
     * Sets the value of the pagination property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeListPagination }
     *     
     */
    public void setPagination(ExchangeListPagination value) {
        this.pagination = value;
    }

    /**
     * Gets the value of the exchangeSearchCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link ExchangeListCriteria }
     *     
     */
    public ExchangeListCriteria getExchangeSearchCriteria() {
        return exchangeSearchCriteria;
    }

    /**
     * Sets the value of the exchangeSearchCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExchangeListCriteria }
     *     
     */
    public void setExchangeSearchCriteria(ExchangeListCriteria value) {
        this.exchangeSearchCriteria = value;
    }

}
