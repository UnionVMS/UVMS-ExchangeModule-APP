package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
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
	public Asset getAsset(String assetGuid) throws ExchangeServiceException {
		try {
            String request = AssetModuleRequestMapper.createGetAssetModuleRequest(assetGuid, AssetIdType.GUID);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.VESSEL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return AssetModuleResponseMapper.mapToAssetFromResponse(response, messageId);

		} catch (ExchangeMessageException e) {
			LOG.error("Couldn't send message to vessel module");
			throw new ExchangeServiceException("Couldn't send message to vessel module");
		} catch (AssetModelMapperException e) {
            LOG.error("Couldn't map asset object by guid:  {}", assetGuid);
            throw new ExchangeServiceException("Couldn't map asset object by guid:  " + assetGuid);
        }
    }

}
