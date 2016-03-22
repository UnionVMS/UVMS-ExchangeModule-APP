
package eu.europa.ec.fisheries.schema.exchange.movement.v1;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.europa.ec.fisheries.schema.exchange.movement.v1 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.europa.ec.fisheries.schema.exchange.movement.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MovementType }
     * 
     */
    public MovementType createMovementType() {
        return new MovementType();
    }

    /**
     * Create an instance of {@link SendMovementToPluginType }
     * 
     */
    public SendMovementToPluginType createSendMovementToPluginType() {
        return new SendMovementToPluginType();
    }

    /**
     * Create an instance of {@link SetReportMovementType }
     * 
     */
    public SetReportMovementType createSetReportMovementType() {
        return new SetReportMovementType();
    }

    /**
     * Create an instance of {@link MovementBaseType }
     * 
     */
    public MovementBaseType createMovementBaseType() {
        return new MovementBaseType();
    }

    /**
     * Create an instance of {@link MovementRefType }
     * 
     */
    public MovementRefType createMovementRefType() {
        return new MovementRefType();
    }

    /**
     * Create an instance of {@link RecipientInfoType }
     * 
     */
    public RecipientInfoType createRecipientInfoType() {
        return new RecipientInfoType();
    }

    /**
     * Create an instance of {@link MovementActivityType }
     * 
     */
    public MovementActivityType createMovementActivityType() {
        return new MovementActivityType();
    }

    /**
     * Create an instance of {@link MovementPoint }
     * 
     */
    public MovementPoint createMovementPoint() {
        return new MovementPoint();
    }

    /**
     * Create an instance of {@link MovementMetaData }
     * 
     */
    public MovementMetaData createMovementMetaData() {
        return new MovementMetaData();
    }

}
