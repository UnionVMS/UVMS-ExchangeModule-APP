
package eu.europa.ec.fisheries.schema.exchange.v1;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.europa.ec.fisheries.schema.exchange.v1 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.europa.ec.fisheries.schema.exchange.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExchangeListQuery }
     * 
     */
    public ExchangeListQuery createExchangeListQuery() {
        return new ExchangeListQuery();
    }

    /**
     * Create an instance of {@link ExchangeListCriteria }
     * 
     */
    public ExchangeListCriteria createExchangeListCriteria() {
        return new ExchangeListCriteria();
    }

    /**
     * Create an instance of {@link ExchangeListCriteriaPair }
     * 
     */
    public ExchangeListCriteriaPair createExchangeListCriteriaPair() {
        return new ExchangeListCriteriaPair();
    }

    /**
     * Create an instance of {@link ExchangeLogType }
     * 
     */
    public ExchangeLogType createExchangeLogType() {
        return new ExchangeLogType();
    }

    /**
     * Create an instance of {@link ExchangeListPagination }
     * 
     */
    public ExchangeListPagination createExchangeListPagination() {
        return new ExchangeListPagination();
    }

}
