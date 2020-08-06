package eu.europa.ec.fisheries.uvms.exchange.rest.unsecured;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventOutgoingServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ServiceRegistryModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ServiceMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class ExchangeAPIRestResource {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeAPIRestResource.class);

    @Inject
    private ServiceRegistryModelBean serviceRegistryModel;

    @Inject
    private ExchangeLogModelBean exchangeLogModel;

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
    @Path("/sendEmail")
    public Response sendEmail(EmailType email) {
        try {
            SetCommandRequest sendEmailCommand = ExchangeModuleRequestMapper.createSetCommandSendEmailRequest(null, email, email.getFrom());
            return sendCommandToPlugin(sendEmailCommand);
        } catch (Exception e) {
            LOG.error("Error while creating a send email command {} ", e.getMessage(), e);
            return Response.status(500).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @POST
    @Path("/pluginCommand")
    public Response sendCommandToPlugin(SetCommandRequest request) {
        try {
            exchangeEventOutgoingService.sendCommandToPluginFromRest(request);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Response.status(500).entity(ExceptionUtils.getRootCause(e).getMessage()).build();
        }
    }


    @GET
    @Path(value = "/poll/{typeRefGuid}")
    public Response getPollStatus(@PathParam("typeRefGuid") String typeRefGuid) {
        try {
            LOG.info("Get ExchangeLog status for Poll by typeRefGuid : {}", typeRefGuid);
            if (typeRefGuid == null) {
                throw new IllegalArgumentException("Invalid id");
            }
            ExchangeLogStatusType response = exchangeLogModel.getExchangeLogStatusHistory(UUID.fromString(typeRefGuid), TypeRefType.POLL);
            return Response.ok(response).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. {} ] {}", typeRefGuid, e.getMessage());
            return Response.status(500).entity(ExceptionUtils.getRootCause(e).getMessage()).build();
        }
    }

}
