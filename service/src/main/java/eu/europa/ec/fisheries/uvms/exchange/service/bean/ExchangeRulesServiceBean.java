package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.rules.movement.v1.RawMovementType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeRulesService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMapperException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.ModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;

@Stateless
public class ExchangeRulesServiceBean implements ExchangeRulesService {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeRulesServiceBean.class);
	
	@EJB
	MessageProducer producer;
	
	@EJB
	ExchangeMessageConsumer consumer;
	
	@Override
	public MovementRefType sendMovementToRules(PluginType pluginType, RawMovementType movement) throws ExchangeServiceException {
		try {
            String request = RulesModuleRequestMapper.createSetMovementReportRequest(pluginType, movement);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.RULES);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ModuleResponseMapper.mapSetMovementReportResponse(response, messageId);
		} catch (RulesModelMapperException | ExchangeMessageException e) {
			LOG.error("Couldn't send message to rules ");
			throw new ExchangeServiceException("Couldn't send message to rules");
		}
	}

}
