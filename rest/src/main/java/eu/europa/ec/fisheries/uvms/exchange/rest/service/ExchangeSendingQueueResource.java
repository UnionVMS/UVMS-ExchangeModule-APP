package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/sendingqueue")
@Stateless
public class ExchangeSendingQueueResource {

	final static Logger LOG = LoggerFactory.getLogger(ExchangeSendingQueueResource.class);

	@EJB
	ExchangeLogService serviceLayer;

	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Get a list of all exchangeLogs by search criterias
	 * 
	 */
	@GET
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/list")
	@RequiresFeature(UnionVMSFeature.viewExchange)
	public ResponseDto getSendingQueue() {
		LOG.info("Get list invoked in rest layer");
		try {
			List<UnsentMessageType> unsentMessageList = serviceLayer.getUnsentMessageList();
			List<SendingGroupLog> sendingQueue = ExchangeLogMapper.mapToSendingQueue(unsentMessageList);
			return new ResponseDto(sendingQueue, RestResponseCode.OK);
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
	 * @summary Get a list of all exchangeLogs by search criterias
	 * 
	 */
	@PUT
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/send")
	@RequiresFeature(UnionVMSFeature.manageExchangeSendingQueue)
	public ResponseDto send(final List<String> messageIdList) {
		LOG.info("Get list invoked in rest layer");
		try {
			//TODO swaggerize messageIdList
			//boolean send = ExchangeMock.send(messageIdList);
			serviceLayer.resend(messageIdList);
			return new ResponseDto(true, RestResponseCode.OK);
		} catch (Exception ex) {
			LOG.error("[ Error when geting log list. ] {} ", ex.getMessage());
			return ErrorHandler.getFault(ex);
		}
	}
}
