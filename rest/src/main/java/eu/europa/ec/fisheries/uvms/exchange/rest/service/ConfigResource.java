package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.v1.SearchField;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mock.ExchangeMock;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/config")
@Stateless
@RequiresFeature(UnionVMSFeature.viewExchange)
public class ConfigResource {

    final static Logger LOG = LoggerFactory.getLogger(ConfigResource.class);
    
    //@EJB
    //ConfigService configService;

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/searchfields")
    public ResponseDto getConfigSearchFields() {
        try {
            return new ResponseDto(SearchField.values(), RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. ]", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/")
    public ResponseDto getConfiguration() {
        try {
        	//List<Config> configuration = configService.getConfiguration();
        	Map<String, List> configuration = ExchangeMock.mockConfiguration();
            return new ResponseDto(configuration, RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config configuration. ] ", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }
}
