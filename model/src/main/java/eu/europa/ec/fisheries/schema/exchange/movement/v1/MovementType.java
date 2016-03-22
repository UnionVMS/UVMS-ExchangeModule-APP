
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MovementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MovementType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementBaseType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="connectId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="calculatedCourse" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="measuredSpeed" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="calculatedSpeed" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="segmentIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="2"/>
 *         &lt;element name="metaData" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementMetaData"/>
 *         &lt;element name="wkt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovementType", propOrder = {
    "guid",
    "connectId",
    "calculatedCourse",
    "measuredSpeed",
    "calculatedSpeed",
    "segmentIds",
    "metaData",
    "wkt"
})
public class MovementType
    extends MovementBaseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String guid;
    @XmlElement(required = true)
    protected String connectId;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double calculatedCourse;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double measuredSpeed;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double calculatedSpeed;
    @XmlElement(required = true)
    protected List<String> segmentIds;
    @XmlElement(required = true)
    protected MovementMetaData metaData;
    @XmlElement(required = true)
    protected String wkt;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the connectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectId() {
        return connectId;
    }

    /**
     * Sets the value of the connectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectId(String value) {
        this.connectId = value;
    }

    /**
     * Gets the value of the calculatedCourse property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCalculatedCourse() {
        return calculatedCourse;
    }

    /**
     * Sets the value of the calculatedCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCalculatedCourse(Double value) {
        this.calculatedCourse = value;
    }

    /**
     * Gets the value of the measuredSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMeasuredSpeed() {
        return measuredSpeed;
    }

    /**
     * Sets the value of the measuredSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMeasuredSpeed(Double value) {
        this.measuredSpeed = value;
    }

    /**
     * Gets the value of the calculatedSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCalculatedSpeed() {
        return calculatedSpeed;
    }

    /**
     * Sets the value of the calculatedSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCalculatedSpeed(Double value) {
        this.calculatedSpeed = value;
    }

    /**
     * Gets the value of the segmentIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the segmentIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSegmentIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSegmentIds() {
        if (segmentIds == null) {
            segmentIds = new ArrayList<String>();
        }
        return this.segmentIds;
    }

    /**
     * Gets the value of the metaData property.
     * 
     * @return
     *     possible object is
     *     {@link MovementMetaData }
     *     
     */
    public MovementMetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the value of the metaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementMetaData }
     *     
     */
    public void setMetaData(MovementMetaData value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the wkt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWkt() {
        return wkt;
    }

    /**
     * Sets the value of the wkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWkt(String value) {
        this.wkt = value;
    }

}
