
package eu.europa.ec.fisheries.schema.exchange.service.v1;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.europa.ec.fisheries.schema.exchange.service.v1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.europa.ec.fisheries.schema.exchange.service.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SettingType }
     * 
     */
    public SettingType createSettingType() {
        return new SettingType();
    }

    /**
     * Create an instance of {@link SettingListType }
     * 
     */
    public SettingListType createSettingListType() {
        return new SettingListType();
    }

    /**
     * Create an instance of {@link ServiceResponseType }
     * 
     */
    public ServiceResponseType createServiceResponseType() {
        return new ServiceResponseType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link CapabilityListType }
     * 
     */
    public CapabilityListType createCapabilityListType() {
        return new CapabilityListType();
    }

    /**
     * Create an instance of {@link CapabilityType }
     * 
     */
    public CapabilityType createCapabilityType() {
        return new CapabilityType();
    }

}
