/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.bean.ExchangeAssetProducerBean;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeAssetService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.TextMessage;

@Stateless
@Slf4j
public class ExchangeAssetServiceBean implements ExchangeAssetService {

	@EJB
    private ExchangeConsumer exchangeConsumer;

	@EJB
	private ExchangeAssetProducerBean assetProducer;

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Asset getAsset(String assetGuid) throws ExchangeServiceException {
		try {
            String request = AssetModuleRequestMapper.createGetAssetModuleRequest(assetGuid, AssetIdType.GUID);
            String messageId = assetProducer.sendModuleMessage(request, exchangeConsumer.getDestination());
            TextMessage response = exchangeConsumer.getMessage(messageId, TextMessage.class);
            return AssetModuleResponseMapper.mapToAssetFromResponse(response, messageId);
		} catch (MessageException e) {
			log.error("Couldn't send message to vessel module");
			throw new ExchangeServiceException("Couldn't send message to vessel module");
		} catch (AssetModelMapperException e) {
			log.error("Couldn't map asset object by guid:  {}", assetGuid);
            throw new ExchangeServiceException("Couldn't map asset object by guid:  " + assetGuid);
        }
	}

}