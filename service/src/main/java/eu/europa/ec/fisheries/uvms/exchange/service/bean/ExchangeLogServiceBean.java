package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;

@Stateless
public class ExchangeLogServiceBean implements ExchangeLogService {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeLogServiceBean.class);
	
	@EJB
	MessageProducer producer;
	
	@EJB
	ExchangeMessageConsumer consumer;
	
	@EJB
    ExchangeEventLogCache logCache;

    @Inject
    @ExchangeLogEvent
    Event<NotificationMessage> exchangeLogEvent;

    @Inject
    @ExchangeSendingQueueEvent
    Event<NotificationMessage> sendingQueueEvent;
    
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
			exchangeLogEvent.fire(new NotificationMessage("guid", createdLog.getGuid()));
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
			exchangeLogEvent.fire(new NotificationMessage("guid", updatedLog.getGuid()));
			return updatedLog;
		} catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't update status of exchange log");
		}
	}

	@Override
	public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException {
		try {
			String text = ExchangeDataSourceRequestMapper.mapGetExchageLogListByQueryToString(query);
			String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			GetLogListByQueryResponse logList = ExchangeDataSourceResponseMapper.mapToGetLogListByQueryResponse(response, messageId);
			return logList;
		}  catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't get exchange log list.");
		}
	}

	@Override
	public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException {
		LOG.info("Get unsent message list in service layer");
		try {
			String text = ExchangeDataSourceRequestMapper.mapGetUnsentMessageList();
			String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			List<UnsentMessageType> unsentMessageList = ExchangeDataSourceResponseMapper.mapGetSendingQueueResponse(response, messageId);
			return unsentMessageList;
		}  catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't get unsent message list.");
		}
	}

	@Override
	public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException {
		LOG.info("Get pollstatus list in service layer");
		try {
			List<ExchangeLogStatusTypeType> statusList = new ArrayList<>();
			statusList.add(status);
			List<TypeRefType> typeList = new ArrayList<>();
			typeList.add(type);
			String text = ExchangeDataSourceRequestMapper.mapGetLogStatusHistoryByQueryRequest(from, to, statusList, typeList);
			String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			List<ExchangeLogStatusType> pollStatusList = ExchangeDataSourceResponseMapper.mapGetLogStatusHistoryByQueryResponse(response, messageId);
			return pollStatusList;
		}  catch (ExchangeModelMapperException | ExchangeMessageException e) {
			throw new ExchangeLogException("Couldn't get exchange status history list.");
		}
	}

    @Override
    public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException {
        try {
            String request = ExchangeDataSourceRequestMapper.mapGetExchangeLogRequest(guid);
            String messageId = producer.sendMessageOnQueue(request, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            return ExchangeDataSourceResponseMapper.mapToExchangeLogTypeFromSingleExchageLogResponse(response, messageId);
        } catch (ExchangeMessageException | ExchangeModelMapperException e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            throw new ExchangeLogException("Error when getting exchange log by GUID.");
        }
    }

	@Override
	public String createUnsentMessage(String senderReceiver, XMLGregorianCalendar timestamp, String recipient, String message) throws ExchangeLogException {
		LOG.debug("createUnsentMessage in service layer");
		try {
			String text = ExchangeDataSourceRequestMapper.mapCreateUnsentMessage(timestamp, senderReceiver, recipient, message);
			LOG.debug(text);
			String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
			TextMessage response = consumer.getMessage(messageId, TextMessage.class);
			String unsentMessageId = ExchangeDataSourceResponseMapper.mapCreateUnsentMessageResponse(response, messageId);
			sendingQueueEvent.fire(new NotificationMessage("messageId", unsentMessageId));
			return unsentMessageId;
		} catch (ExchangeMessageException | ExchangeModelMapperException e) {
			LOG.error("Couldn't add message to unsent list");
			throw new ExchangeLogException("Couldn't add message to unsent list");
		}
	}

}
