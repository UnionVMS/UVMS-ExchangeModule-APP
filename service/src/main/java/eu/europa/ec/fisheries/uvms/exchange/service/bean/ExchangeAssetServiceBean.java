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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.message.consumer.ExchangeConsumer;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;

@Stateless
public class ExchangeAssetServiceBean {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeAssetServiceBean.class);
	
	@EJB
	ExchangeMessageProducer producer;
	
	@EJB
    ExchangeConsumer consumer;

	public Asset getAsset(String assetGuid) {
		try {
			String request = AssetModuleRequestMapper.createGetAssetModuleRequest(assetGuid, AssetIdType.GUID);
			String messageId = producer.forwardToAsset(request, AssetModuleMethod.GET_ASSET.value());
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			return AssetModuleResponseMapper.mapToAssetFromResponse(response, messageId);
		}catch (Exception e){
			throw new RuntimeException(e);		//convert various asset exceptions to runtime
		}
	}

}