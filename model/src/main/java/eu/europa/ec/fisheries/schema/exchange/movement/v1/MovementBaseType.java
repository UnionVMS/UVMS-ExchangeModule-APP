
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;


/**
 * <p>Java class for MovementBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MovementBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetId" type="{urn:asset.movement.exchange.schema.fisheries.ec.europa.eu:v1}AssetId"/>
 *         &lt;element name="mobileTerminalId" type="{urn:mobileterminal.movement.exchange.schema.fisheries.ec.europa.eu:v1}MobileTerminalId"/>
 *         &lt;element name="comChannelType" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementComChannelType"/>
 *         &lt;element name="source" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementSourceType"/>
 *         &lt;element name="position" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementPoint"/>
 *         &lt;element name="positionTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reportedSpeed" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="reportedCourse" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="movementType" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementTypeType"/>
 *         &lt;element name="activity" type="{urn:movement.exchange.schema.fisheries.ec.europa.eu:v1}MovementActivityType"/>
 *         &lt;element name="assetName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flagState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="externalMarking" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ircs" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mmsi" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tripNumber" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="internalReferenceNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovementBaseType", propOrder = {
    "assetId",
    "mobileTerminalId",
    "comChannelType",
    "source",
    "position",
    "positionTime",
    "status",
    "reportedSpeed",
    "reportedCourse",
    "movementType",
    "activity",
    "assetName",
    "flagState",
    "externalMarking",
    "ircs",
    "mmsi",
    "tripNumber",
    "internalReferenceNumber"
})
@XmlSeeAlso({
    MovementType.class
})
public class MovementBaseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected AssetId assetId;
    @XmlElement(required = true)
    protected MobileTerminalId mobileTerminalId;
    @XmlElement(required = true)
    protected MovementComChannelType comChannelType;
    @XmlElement(required = true)
    protected MovementSourceType source;
    @XmlElement(required = true)
    protected MovementPoint position;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar positionTime;
    @XmlElement(required = true)
    protected String status;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double reportedSpeed;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double reportedCourse;
    @XmlElement(required = true)
    protected MovementTypeType movementType;
    @XmlElement(required = true)
    protected MovementActivityType activity;
    @XmlElement(required = true)
    protected String assetName;
    @XmlElement(required = true)
    protected String flagState;
    @XmlElement(required = true)
    protected String externalMarking;
    @XmlElement(required = true)
    protected String ircs;
    @XmlElement(required = true)
    protected String mmsi;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double tripNumber;
    @XmlElement(required = true)
    protected String internalReferenceNumber;

    /**
     * Gets the value of the assetId property.
     * 
     * @return
     *     possible object is
     *     {@link AssetId }
     *     
     */
    public AssetId getAssetId() {
        return assetId;
    }

    /**
     * Sets the value of the assetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetId }
     *     
     */
    public void setAssetId(AssetId value) {
        this.assetId = value;
    }

    /**
     * Gets the value of the mobileTerminalId property.
     * 
     * @return
     *     possible object is
     *     {@link MobileTerminalId }
     *     
     */
    public MobileTerminalId getMobileTerminalId() {
        return mobileTerminalId;
    }

    /**
     * Sets the value of the mobileTerminalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileTerminalId }
     *     
     */
    public void setMobileTerminalId(MobileTerminalId value) {
        this.mobileTerminalId = value;
    }

    /**
     * Gets the value of the comChannelType property.
     * 
     * @return
     *     possible object is
     *     {@link MovementComChannelType }
     *     
     */
    public MovementComChannelType getComChannelType() {
        return comChannelType;
    }

    /**
     * Sets the value of the comChannelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementComChannelType }
     *     
     */
    public void setComChannelType(MovementComChannelType value) {
        this.comChannelType = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link MovementSourceType }
     *     
     */
    public MovementSourceType getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementSourceType }
     *     
     */
    public void setSource(MovementSourceType value) {
        this.source = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link MovementPoint }
     *     
     */
    public MovementPoint getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementPoint }
     *     
     */
    public void setPosition(MovementPoint value) {
        this.position = value;
    }

    /**
     * Gets the value of the positionTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPositionTime() {
        return positionTime;
    }

    /**
     * Sets the value of the positionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPositionTime(XMLGregorianCalendar value) {
        this.positionTime = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the reportedSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getReportedSpeed() {
        return reportedSpeed;
    }

    /**
     * Sets the value of the reportedSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setReportedSpeed(Double value) {
        this.reportedSpeed = value;
    }

    /**
     * Gets the value of the reportedCourse property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getReportedCourse() {
        return reportedCourse;
    }

    /**
     * Sets the value of the reportedCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setReportedCourse(Double value) {
        this.reportedCourse = value;
    }

    /**
     * Gets the value of the movementType property.
     * 
     * @return
     *     possible object is
     *     {@link MovementTypeType }
     *     
     */
    public MovementTypeType getMovementType() {
        return movementType;
    }

    /**
     * Sets the value of the movementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementTypeType }
     *     
     */
    public void setMovementType(MovementTypeType value) {
        this.movementType = value;
    }

    /**
     * Gets the value of the activity property.
     * 
     * @return
     *     possible object is
     *     {@link MovementActivityType }
     *     
     */
    public MovementActivityType getActivity() {
        return activity;
    }

    /**
     * Sets the value of the activity property.
     * 
     * @param value
     *     allowed object is
     *     {@link MovementActivityType }
     *     
     */
    public void setActivity(MovementActivityType value) {
        this.activity = value;
    }

    /**
     * Gets the value of the assetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Sets the value of the assetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetName(String value) {
        this.assetName = value;
    }

    /**
     * Gets the value of the flagState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagState() {
        return flagState;
    }

    /**
     * Sets the value of the flagState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagState(String value) {
        this.flagState = value;
    }

    /**
     * Gets the value of the externalMarking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalMarking() {
        return externalMarking;
    }

    /**
     * Sets the value of the externalMarking property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalMarking(String value) {
        this.externalMarking = value;
    }

    /**
     * Gets the value of the ircs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrcs() {
        return ircs;
    }

    /**
     * Sets the value of the ircs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrcs(String value) {
        this.ircs = value;
    }

    /**
     * Gets the value of the mmsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMmsi() {
        return mmsi;
    }

    /**
     * Sets the value of the mmsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMmsi(String value) {
        this.mmsi = value;
    }

    /**
     * Gets the value of the tripNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTripNumber() {
        return tripNumber;
    }

    /**
     * Sets the value of the tripNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTripNumber(Double value) {
        this.tripNumber = value;
    }

    /**
     * Gets the value of the internalReferenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalReferenceNumber() {
        return internalReferenceNumber;
    }

    /**
     * Sets the value of the internalReferenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalReferenceNumber(String value) {
        this.internalReferenceNumber = value;
    }

}
