
package eu.europa.ec.fisheries.schema.exchange.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SendPollType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SendPollType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:exchange.schema.fisheries.ec.europa.eu:v1}ExchangeLogType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendPollType")
public class SendPollType
    extends ExchangeLogType
    implements Serializable
{

    private final static long serialVersionUID = 1L;

}
