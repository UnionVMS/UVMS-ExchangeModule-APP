package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.types.ModuleObject;

@Path("/exchange")
@Stateless
public class ExchangeRestResource {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeRestResource.class);

    /**
     * TODO Rename this class so the name is YOUR_COMPNENT_NAME Resource instead
     * of RestResource
     */
    @EJB
    ExchangeService serviceLayer;

    /**
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary [Description]
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("list")
    public ResponseDto getList() {
        LOG.info("Get list invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.getList(), ResponseCode.OK);
        } catch (ExchangeServiceException | NullPointerException ex) {
            LOG.error("[ Error when geting list. ] {} ", ex.getStackTrace());
            return new ResponseDto(ex.getMessage(), ResponseCode.ERROR);
        }
    }

    /**
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary [Description]
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(value = "{id}")
    public ResponseDto getById(@PathParam(value = "id") final Long id) {
        LOG.info("Get by id invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.getById(id), ResponseCode.OK);
        } catch (ExchangeServiceException | NullPointerException ex) {
            LOG.error("[ Error when geting by id. ] {} ", ex.getStackTrace());
            return new ResponseDto(ex.getMessage(), ResponseCode.ERROR);
        }
    }

    /**
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary [Description]
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public ResponseDto create(final ModuleObject data) {
        LOG.info("Create invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.create(data), ResponseCode.OK);
        } catch (ExchangeServiceException | NullPointerException ex) {
            LOG.error("[ Error when creating. ] {} ", ex.getStackTrace());
            return new ResponseDto(ResponseCode.ERROR);
        }
    }

    /**
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary [Description]
     *
     */
    @PUT
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public ResponseDto update(final ModuleObject data) {
        LOG.info("Update invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.update(data), ResponseCode.OK);
        } catch (ExchangeServiceException | NullPointerException ex) {
            LOG.error("[ Error when updating. ] {} ", ex.getStackTrace());
            return new ResponseDto(ResponseCode.ERROR);
        }
    }

}
