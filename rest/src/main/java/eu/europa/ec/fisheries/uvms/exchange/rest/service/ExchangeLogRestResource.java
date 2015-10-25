package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/exchange")
@Stateless
public class ExchangeLogRestResource {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeLogRestResource.class);

    @EJB
    ExchangeService serviceLayer;

    /**
     *
     * @responseType
     * java.util.List<eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse>
     *
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     *
     * @summary Get a list of all exchangeLogs by search criterias
     *
     */
    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/log")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getLogListByCriteria(ExchangeListQuery query) {
        LOG.info("Get list invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.getExchangeLogByQuery(query), RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when geting log list. ] {} ", ex.getMessage());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/{id}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getById(@PathParam(value = "id") final Long id) {
        LOG.info("Get by id invoked in rest layer");
        try {
            return new ResponseDto(serviceLayer.getById(id), RestResponseCode.OK);
        } catch (ExchangeServiceException | NullPointerException ex) {
            LOG.error("[ Error when geting by id. ] {} ", ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }
}
