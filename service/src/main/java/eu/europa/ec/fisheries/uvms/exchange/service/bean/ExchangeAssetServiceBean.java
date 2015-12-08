package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeRulesService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.ModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.vessel.model.exception.VesselModelMapperException;
import eu.europa.ec.fisheries.uvms.vessel.model.mapper.VesselModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.vessel.model.mapper.VesselModuleResponseMapper;
import eu.europa.ec.fisheries.wsdl.vessel.types.Vessel;
import eu.europa.ec.fisheries.wsdl.vessel.types.VesselIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

@Stateless
public class ExchangeAssetServiceBean implements ExchangeAssetService {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeAssetServiceBean.class);
	
	@EJB
	MessageProducer producer;
	
	@EJB
	ExchangeMessageConsumer consumer;

	@Override
	public Vessel getAsset(String assetGuid) throws ExchangeServiceException {
		try {
            String request = VesselModuleRequestMapper.createGetVesselModuleRequest(assetGuid, VesselIdType.GUID);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.VESSEL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return VesselModuleResponseMapper.mapToVesselFromResponse(response, messageId);

		} catch (ExchangeMessageException e) {
			LOG.error("Couldn't send message to vessel module ");
			throw new ExchangeServiceException("Couldn't send message to vessel module");
		} catch (VesselModelMapperException e) {
            LOG.error("Couldn't map asset object by guid:  " + assetGuid);
            throw new ExchangeServiceException("Couldn't map asset object by guid:  " + assetGuid);
        }
    }

}
