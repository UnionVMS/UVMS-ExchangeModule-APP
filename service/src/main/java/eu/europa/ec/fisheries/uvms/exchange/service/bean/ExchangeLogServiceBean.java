package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Stateless
public class ExchangeLogServiceBean implements ExchangeLogService {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeLogServiceBean.class);
	
	@EJB
	MessageProducer producer;
	
	@EJB
	ExchangeMessageConsumer consumer;
	
	@EJB
    ExchangeEventLogCache logCache;
    
	@Override
	public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId) throws ExchangeLogException {
        ExchangeLogType createdLog = log(log);
        logCache.put(pluginMessageId, createdLog.getGuid());
        	
        return createdLog;
	}

	@Override
	public ExchangeLogType log(ExchangeLogType log) throws ExchangeLogException {
		try {
			String logText = ExchangeDataSourceRequestMapper.mapCreateExchangeLogToString(log);
			String messageId = producer.sendMessageOnQueue(logText, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			ExchangeLogType createdLog = ExchangeDataSourceResponseMapper.mapCreateExchangeLogResponse(response, messageId);
			return createdLog;
		}  catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't create log exchange log.");
		}
	}

	@Override
	public ExchangeLogType updateStatus(String pluginMessageId, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException {
		try {
			String logGuid = logCache.acknowledged(pluginMessageId);
			
			String text = ExchangeDataSourceRequestMapper.mapUpdateLogStatusRequest(logGuid, logStatus);
			String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			ExchangeLogType updatedLog = ExchangeDataSourceResponseMapper.mapUpdateLogStatusResponse(response, messageId);
			return updatedLog;
		} catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't update status of exchange log");
		}
	}

}
