package eu.europa.ec.fisheries.uvms.exchange.rest.unsecured;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@Stateless
public class ExchangeAPIResource {

    final static Logger LOG = LoggerFactory
            .getLogger(ExchangeAPIResource.class);


    @EJB
    private ExchangeService exchangeService;

    /**
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary Get a list of all registered and active services
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/serviceList")
    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        GetServiceListResponse getServiceListResponse = new GetServiceListResponse();
        try {
            List<ServiceResponseType> serviceList = exchangeService.getServiceList(request.getType());
            getServiceListResponse.getService().addAll(serviceList);
            return getServiceListResponse;
        } catch (ExchangeServiceException ex) {
            LOG.error("Call failed", ex);
        }
        return getServiceListResponse;
    }

}
