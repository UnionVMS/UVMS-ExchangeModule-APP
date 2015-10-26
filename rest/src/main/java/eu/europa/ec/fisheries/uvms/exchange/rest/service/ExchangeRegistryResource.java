package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/plugin")
@Stateless
public class ExchangeRegistryResource {

	final static Logger LOG = LoggerFactory
			.getLogger(ExchangeRegistryResource.class);

	@EJB
	ExchangeService serviceLayer;

	@EJB
	PluginService pluginService;
	
	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Get a list of all registered and active services
	 * 
	 */
	@GET
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/list")
	@RequiresFeature(UnionVMSFeature.viewExchange)
	public ResponseDto getList() {
		LOG.info("Get list invoked in rest layer");
		try {
			return new ResponseDto(ServiceMapper.map(serviceLayer.getServiceList(null)), RestResponseCode.OK);
		} catch (ExchangeServiceException | NullPointerException ex) {
			LOG.error("[ Error when geting list. ] {} ", ex.getMessage());
			return ErrorHandler.getFault(ex);
		}
	}

	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Start a service
	 * 
	 */
	@PUT
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/start/{serviceClassName}")
	@RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
	public ResponseDto startService(@PathParam(value="serviceClassName") String serviceClassName) {
		LOG.info("Start service invoked in rest layer");
		try {
			return new ResponseDto(pluginService.start(serviceClassName), RestResponseCode.OK);
		} catch (ExchangeServiceException ex) {
			LOG.error("[ Error when starting service ]");
			return ErrorHandler.getFault(ex);
		}
	}

	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Stop a service
	 * 
	 */
	@PUT
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/stop/{serviceClassName}")
	@RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
	public ResponseDto stopService(@PathParam(value="serviceClassName") String serviceClassName) {
		LOG.info("Stop service invoked in rest layer");
		try {
			return new ResponseDto(pluginService.stop(serviceClassName), RestResponseCode.OK);
		} catch (ExchangeServiceException | NullPointerException ex) {
			LOG.error("[ Error when stopping service ] ", ex.getMessage());
			return ErrorHandler.getFault(ex);
		}
	}
}
