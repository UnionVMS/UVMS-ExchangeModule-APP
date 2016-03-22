
package eu.europa.ec.fisheries.schema.exchange.plugin.v1;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.europa.ec.fisheries.schema.exchange.plugin.v1 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.europa.ec.fisheries.schema.exchange.plugin.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetCommandRequest }
     * 
     */
    public SetCommandRequest createSetCommandRequest() {
        return new SetCommandRequest();
    }

    /**
     * Create an instance of {@link SetConfigRequest }
     * 
     */
    public SetConfigRequest createSetConfigRequest() {
        return new SetConfigRequest();
    }

    /**
     * Create an instance of {@link AcknowledgeResponse }
     * 
     */
    public AcknowledgeResponse createAcknowledgeResponse() {
        return new AcknowledgeResponse();
    }

    /**
     * Create an instance of {@link SetReportRequest }
     * 
     */
    public SetReportRequest createSetReportRequest() {
        return new SetReportRequest();
    }

    /**
     * Create an instance of {@link PingRequest }
     * 
     */
    public PingRequest createPingRequest() {
        return new PingRequest();
    }

    /**
     * Create an instance of {@link StopRequest }
     * 
     */
    public StopRequest createStopRequest() {
        return new StopRequest();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link StartRequest }
     * 
     */
    public StartRequest createStartRequest() {
        return new StartRequest();
    }

}
