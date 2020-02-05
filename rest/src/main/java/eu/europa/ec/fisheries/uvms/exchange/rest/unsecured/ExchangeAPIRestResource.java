package eu.europa.ec.fisheries.uvms.exchange.rest.unsecured;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.uvms.exchange.bean.ServiceRegistryModelBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventOutgoingServiceBean;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api")
@Stateless

@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class ExchangeAPIRestResource {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeAPIRestResource.class);

    @Inject
    private ServiceRegistryModelBean serviceRegistryModel;

    @EJB
    private ExchangeEventOutgoingServiceBean exchangeEventOutgoingService;

    @POST
    @Path("/serviceList")
    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        GetServiceListResponse getServiceListResponse = new GetServiceListResponse();
        try {
            List<Service> serviceList = serviceRegistryModel.getPlugins(request.getType());
            getServiceListResponse.getService().addAll(ServiceMapper.toServiceModelList(serviceList));
            return getServiceListResponse;
        } catch (Exception ex) {
            LOG.error("Call failed", ex);
        }
        return getServiceListResponse;
    }

    @POST
    @Path("/pluginCommand")
    public Response sendCommandToPlugin(SetCommandRequest request) {
        try {
            exchangeEventOutgoingService.sendCommandToPluginFromRest(request);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Response.status(500).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

}
