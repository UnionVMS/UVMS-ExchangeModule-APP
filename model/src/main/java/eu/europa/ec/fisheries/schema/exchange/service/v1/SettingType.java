
package eu.europa.ec.fisheries.schema.exchange.service.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;


/**
 * <p>Java class for SettingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SettingType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:common.exchange.schema.fisheries.ec.europa.eu:v1}keyValueType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SettingType")
public class SettingType
    extends KeyValueType
    implements Serializable
{

    private final static long serialVersionUID = 1L;

}
