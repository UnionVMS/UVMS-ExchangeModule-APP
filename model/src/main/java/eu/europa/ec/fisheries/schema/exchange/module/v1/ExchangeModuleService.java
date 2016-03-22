package eu.europa.ec.fisheries.schema.exchange.module.v1;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.6
 * 2016-03-22T18:03:46.823+01:00
 * Generated source version: 2.7.6
 * 
 */
@WebServiceClient(name = "ExchangeModuleService", 
                  wsdlLocation = "file:/C:/projects/unionvms/UNIONVMS/branches/dev/Modules/Exchange/APP/model/src/main/resources/contract/ExchangeModuleService.wsdl",
                  targetNamespace = "urn:module.exchange.schema.fisheries.ec.europa.eu:v1") 
public class ExchangeModuleService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("urn:module.exchange.schema.fisheries.ec.europa.eu:v1", "ExchangeModuleService");
    public final static QName ExchangeModulePortType = new QName("urn:module.exchange.schema.fisheries.ec.europa.eu:v1", "ExchangeModulePortType");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/projects/unionvms/UNIONVMS/branches/dev/Modules/Exchange/APP/model/src/main/resources/contract/ExchangeModuleService.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ExchangeModuleService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/C:/projects/unionvms/UNIONVMS/branches/dev/Modules/Exchange/APP/model/src/main/resources/contract/ExchangeModuleService.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ExchangeModuleService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ExchangeModuleService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ExchangeModuleService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ExchangeModuleService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ExchangeModuleService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public ExchangeModuleService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns ExchangeModulePortType
     */
    @WebEndpoint(name = "ExchangeModulePortType")
    public ExchangeModulePortType getExchangeModulePortType() {
        return super.getPort(ExchangeModulePortType, ExchangeModulePortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ExchangeModulePortType
     */
    @WebEndpoint(name = "ExchangeModulePortType")
    public ExchangeModulePortType getExchangeModulePortType(WebServiceFeature... features) {
        return super.getPort(ExchangeModulePortType, ExchangeModulePortType.class, features);
    }

}
