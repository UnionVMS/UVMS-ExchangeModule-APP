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

import eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeRulesService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ExchangeRulesServiceBean implements ExchangeRulesService {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeRulesServiceBean.class);
	
	@EJB
	MessageProducer producer;
	
	@EJB
	ExchangeMessageConsumer consumer;
	
	@Override
	public void sendMovementToRules(PluginType pluginType, RawMovementType movement, String username) throws ExchangeServiceException {
		try {
            String request = RulesModuleRequestMapper.createSetMovementReportRequest(pluginType, movement, username);
            producer.sendMessageOnQueue(request, MessageQueue.RULES);
        } catch (RulesModelMapperException | ExchangeMessageException e) {
			LOG.error("Couldn't send message to rules ");
			throw new ExchangeServiceException("Couldn't send message to rules");
		}
	}

	@Override
	public void sendFLUXFAReportMessageToRules(PluginType pluginType, String  fluxFAReportMessage, String username) throws ExchangeServiceException {
		try {
			LOG.info("send  FLUXFAReportMessageToRules");
			String request = RulesModuleRequestMapper.createSetFLUXFAReportMessageRequest(pluginType,fluxFAReportMessage,username);
			LOG.info("  FLUXFAReportMessageToRules sent");
		    producer.sendMessageOnQueue(request, MessageQueue.RULES);
		} catch (RulesModelMapperException | ExchangeMessageException  e) {
			LOG.error("Couldn't send message to rules ");
			throw new ExchangeServiceException("Couldn't send message to rules");
		}

	}

}