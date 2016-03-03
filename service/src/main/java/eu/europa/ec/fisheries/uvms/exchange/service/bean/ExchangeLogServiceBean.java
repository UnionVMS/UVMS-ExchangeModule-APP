package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeValidationException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeAuditRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
            sendAuditLogMessageForCreateExchangeLog(createdLog.getGuid());
            exchangeLogEvent.fire(new NotificationMessage("guid", createdLog.getGuid()));
            return createdLog;
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
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
            sendAuditLogMessageForUpdateExchangeLog(updatedLog.getGuid());

            // For long polling
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
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
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
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeLogException("Couldn't get unsent message list.");
        }
    }

    @Override
    public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException {
        LOG.info("Get pollstatus list in service layer");
        try {
            List<ExchangeLogStatusTypeType> statusList = new ArrayList<>();
            if (status != null) {
                statusList.add(status);
            }
            List<TypeRefType> typeList = new ArrayList<>();
            if (type != null) {
                typeList.add(type);
            }
            String text = ExchangeDataSourceRequestMapper.mapGetLogStatusHistoryByQueryRequest(from, to, statusList, typeList);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            List<ExchangeLogStatusType> pollStatusList = ExchangeDataSourceResponseMapper.mapGetLogStatusHistoryByQueryResponse(response, messageId);
            return pollStatusList;
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeLogException("Couldn't get exchange status history list.");
        }
    }

    @Override
    public ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid) throws ExchangeLogException {
        LOG.info("Get poll status history in service layer");
        if (typeRefGuid == null || typeRefGuid.isEmpty()) {
            throw new ExchangeLogException("Invalid id");
        }
        try {
            String text = ExchangeDataSourceRequestMapper.mapGetLogStatusHistoryRequest(typeRefGuid, type);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            ExchangeLogStatusType pollStatus = ExchangeDataSourceResponseMapper.mapGetLogStatusHistoryResponse(response, messageId);
            return pollStatus;
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
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
    public String createUnsentMessage(String senderReceiver, XMLGregorianCalendar timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties) throws ExchangeLogException {
        LOG.debug("createUnsentMessage in service layer");
        try {
            String text = ExchangeDataSourceRequestMapper.mapCreateUnsentMessage(timestamp, senderReceiver, recipient, message, properties);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            String unsentMessageId = ExchangeDataSourceResponseMapper.mapCreateUnsentMessageResponse(response, messageId);
            List<String> unsentMessageIds = Arrays.asList(unsentMessageId);
            sendAuditLogMessageForCreateUnsentMessage(unsentMessageId);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", unsentMessageIds));
            return unsentMessageId;
        } catch (ExchangeMessageException | ExchangeModelMapperException e) {
            LOG.error("Couldn't add message to unsent list");
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void removeUnsentMessage(String unsentMessageId) throws ExchangeLogException {
        LOG.debug("removeUnsentMessage in service layer");
        try {
            String text = ExchangeDataSourceRequestMapper.mapRemoveUnsentMessage(unsentMessageId);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            String removedUnsentMessageId = ExchangeDataSourceResponseMapper.mapRemoveUnsentMessageResponse(response, messageId);
            List<String> removedMessageIds = Arrays.asList(removedUnsentMessageId);
            sendAuditLogMessageForRemoveUnsentMessage(removedUnsentMessageId);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", removedMessageIds));
        } catch (ExchangeMessageException | ExchangeModelMapperException e) {
            LOG.error("Couldn't add message to unsent list");
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void resend(List<String> messageIdList) throws ExchangeLogException {
        LOG.debug("resend in service layer");
        try {
            String text = ExchangeDataSourceRequestMapper.mapResendMessage(messageIdList);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            List<UnsentMessageType> unsentMessageList = ExchangeDataSourceResponseMapper.mapResendMessageResponse(response, messageId);
            sendAuditLogMessageForResendUnsentMessage(messageIdList.toString());
            if (unsentMessageList != null && !unsentMessageList.isEmpty()) {
                sendingQueueEvent.fire(new NotificationMessage("messageIds", messageIdList));

                for (UnsentMessageType unsentMessage : unsentMessageList) {

                    String unsentMessageId = producer.sendMessageOnQueue(unsentMessage.getMessage(), MessageQueue.EVENT);
                    TextMessage unsentResponse = consumer.getMessage(unsentMessageId, TextMessage.class);
                    sendAuditLogMessageForCreateUnsentMessage(unsentMessageId);
                    try {
                        ExchangeModuleResponseMapper.validateResponse(unsentResponse, unsentMessageId);
                    } catch (JMSException | ExchangeValidationException e) {
                        //TODO handle unsent message
                        LOG.error("Couldn't resend message " + unsentMessage.getMessageId() + " : " + e.getMessage());
                    }
                }
            }
        } catch (ExchangeMessageException | ExchangeModelMapperException e) {
            LOG.error("Couldn't add message to unsent list");
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public PollStatus setPollStatus(String jmsCorrelationId, String pollId, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException {
        try {
            // Remove the message from cache, because legancy implementation
            logCache.acknowledged(jmsCorrelationId);

            String text = ExchangeDataSourceRequestMapper.mapSetPollStatusRequest(pollId, logStatus);
            String messageId = producer.sendMessageOnQueue(text, MessageQueue.INTERNAL);
            TextMessage response = consumer.getMessage(messageId, TextMessage.class);
            sendAuditLogMessageForUpdatePollStatus(pollId);
            PollStatus pollStatusResponse = ExchangeDataSourceResponseMapper.mapSetPollStatusResponse(response, messageId);

            // For long polling
            exchangeLogEvent.fire(new NotificationMessage("guid", pollStatusResponse.getExchangeLogGuid()));
            return pollStatusResponse;
        } catch (ExchangeModelMapperException | ExchangeMessageException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        }
    }

    private void sendAuditLogMessageForCreateUnsentMessage(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapCreateUnsentMessage(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Unsent message was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForRemoveUnsentMessage(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapRemoveUnsentMessage(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Unsent message was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForUpdateExchangeLog(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapUpdateExchangeLog(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log with guid: " + guid + " is updated");
        }
    }

    private void sendAuditLogMessageForResendUnsentMessage(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapResendSendingQueue(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Resend sending queue with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForCreateExchangeLog(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapCreateExchangeLog(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForUpdatePollStatus(String guid) {
        try {
            String request = ExchangeAuditRequestMapper.mapUpdatePoll(guid);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange poll with guid: " + guid + " is updated");
        }
    }
}
