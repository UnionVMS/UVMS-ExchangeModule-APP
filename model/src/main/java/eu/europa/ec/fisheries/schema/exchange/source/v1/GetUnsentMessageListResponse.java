
package eu.europa.ec.fisheries.schema.exchange.source.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;


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
 *         &lt;element name="unsentMessage" type="{urn:exchange.schema.fisheries.ec.europa.eu:v1}UnsentMessageType" maxOccurs="unbounded" minOccurs="0"/>
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
    "unsentMessage"
})
@XmlRootElement(name = "GetUnsentMessageListResponse")
public class GetUnsentMessageListResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected List<UnsentMessageType> unsentMessage;

    /**
     * Gets the value of the unsentMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unsentMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnsentMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnsentMessageType }
     * 
     * 
     */
    public List<UnsentMessageType> getUnsentMessage() {
        if (unsentMessage == null) {
            unsentMessage = new ArrayList<UnsentMessageType>();
        }
        return this.unsentMessage;
    }

}
