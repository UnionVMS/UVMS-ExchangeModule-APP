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

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.remote.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.model.remote.UnsentModel;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeAuditRequestMapper;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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

    @EJB(lookup = ExchangeServiceConstants.GLOBAL_DB_ACCESS_EXCHANGE_LOG)
    private ExchangeLogModel exchangeLogModel;

    @EJB(lookup = ExchangeServiceConstants.GLOBAL_DB_ACCESS_UNSENT_MESSAGE)
    private UnsentModel unsentModel;


    @Override
    public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId, String username) throws ExchangeLogException {
        ExchangeLogType createdLog = log(log, username);
        logCache.put(pluginMessageId, createdLog.getGuid());

        return createdLog;
    }

    @Override
    public ExchangeLogType log(ExchangeLogType log, String username) throws ExchangeLogException {
        try {

            ExchangeLogType exchangeLog = exchangeLogModel.createExchangeLog(log, username);
            sendAuditLogMessageForCreateExchangeLog(exchangeLog.getGuid(), username);
            exchangeLogEvent.fire(new NotificationMessage("guid", exchangeLog.getGuid()));
            return exchangeLog;
        } catch (ExchangeModelMapperException e) {
            throw new ExchangeLogException("Couldn't create log exchange log.");
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't create log exchange log.");
        }
    }

    @Override
    public ExchangeLogType updateStatus(String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException {
        try {
            String logGuid = logCache.acknowledged(pluginMessageId);

            ExchangeLogStatusType exchangeLogStatusType = new ExchangeLogStatusType();
            exchangeLogStatusType.setGuid(logGuid);
            ArrayList statusHistoryList = new ArrayList();
            ExchangeLogStatusHistoryType statusHistory = new ExchangeLogStatusHistoryType();
            statusHistory.setStatus(logStatus);
            statusHistoryList.add(statusHistory);
            exchangeLogStatusType.getHistory().addAll(statusHistoryList);
            ExchangeLogType updatedLog = exchangeLogModel.updateExchangeLogStatus(exchangeLogStatusType, username);

            sendAuditLogMessageForUpdateExchangeLog(updatedLog.getGuid(), username);
            // For long polling
            exchangeLogEvent.fire(new NotificationMessage("guid", updatedLog.getGuid()));
            return updatedLog;
        } catch (ExchangeModelMapperException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        }
    }

    @Override
    public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException {
        try {
            ListResponseDto exchangeLogList = exchangeLogModel.getExchangeLogListByQuery(query);
            GetLogListByQueryResponse response = new GetLogListByQueryResponse();
            response.setCurrentPage(exchangeLogList.getCurrentPage());
            response.setTotalNumberOfPages(exchangeLogList.getTotalNumberOfPages());
            response.getExchangeLog().addAll(exchangeLogList.getExchangeLogList());
            return response;
        } catch (ExchangeModelMapperException e) {
            throw new ExchangeLogException("Couldn't get exchange log list.");
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange log list.");
        }
    }

    @Override
    public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException {
        LOG.info("Get unsent message list in service layer");
        try {
            List<UnsentMessageType> unsentMessageList = unsentModel.getMessageList();
            return unsentMessageList;
        } catch (ExchangeModelException e) {
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

            ExchangeHistoryListQuery query = new ExchangeHistoryListQuery();
            query.setTypeRefDateFrom(from);
            query.setTypeRefDateTo(to);
            query.getStatus().addAll(statusList);
            query.getType().addAll(typeList);

            List<ExchangeLogStatusType> pollStatusList = exchangeLogModel.getExchangeLogStatusHistoryByQuery(query);
            return pollStatusList;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange status history list.");
        }
    }

    @Override
    public ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid, String userName) throws ExchangeLogException {
        LOG.info("Get poll status history in service layer");
        if (typeRefGuid == null || typeRefGuid.isEmpty()) {
            throw new ExchangeLogException("Invalid id");
        }
        try {
            ExchangeLogStatusType pollStatus = exchangeLogModel.getExchangeLogStatusHistory(typeRefGuid, type);
            return pollStatus;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange status history list.");
        }
    }

    @Override
    public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException {
        try {
            ExchangeLogType exchangeLogByGuid = exchangeLogModel.getExchangeLogByGuid(guid);
            return exchangeLogByGuid;
        } catch (ExchangeModelException e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            throw new ExchangeLogException("Error when getting exchange log by GUID.");
        }
    }

    @Override
    public String createUnsentMessage(String senderReceiver, Date timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties, String username) throws ExchangeLogException {
        LOG.debug("createUnsentMessage in service layer");
        try {
            UnsentMessageType unsentMessage = new UnsentMessageType();
            unsentMessage.setDateReceived(timestamp);
            unsentMessage.setSenderReceiver(senderReceiver);
            unsentMessage.setRecipient(recipient);
            unsentMessage.setMessage(message);
            unsentMessage.getProperties().addAll(properties);
            String createdUnsentMessageId = unsentModel.createMessage(unsentMessage, username);

            List<String> unsentMessageIds = Arrays.asList(createdUnsentMessageId);
            sendAuditLogMessageForCreateUnsentMessage(createdUnsentMessageId, username);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", unsentMessageIds));
            return createdUnsentMessageId;
        } catch (ExchangeModelException e) {
            LOG.error("Couldn't add message to unsent list");
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void removeUnsentMessage(String unsentMessageId, String username) throws ExchangeLogException {
        LOG.debug("removeUnsentMessage in service layer");
        try {
            String removeMessageId = unsentModel.removeMessage(unsentMessageId);
            List<String> removedMessageIds = Arrays.asList(removeMessageId);
            sendAuditLogMessageForRemoveUnsentMessage(removeMessageId, username);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", removedMessageIds));
        } catch (ExchangeModelException e) {
            LOG.error("Couldn't add message to unsent list");
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void resend(List<String> messageIdList, String username) throws ExchangeLogException {
        LOG.debug("resend in service layer");
        List<UnsentMessageType> unsentMessageList;
        try {
            unsentMessageList = unsentModel.resend(messageIdList);
            sendAuditLogMessageForResendUnsentMessage(messageIdList.toString(), username);
        } catch (ExchangeModelException e) {
            LOG.error("Couldn't read unsent messages", e);
            throw new ExchangeLogException("Couldn't read unsent messages");
        }
        if (unsentMessageList != null && !unsentMessageList.isEmpty()) {
            sendingQueueEvent.fire(new NotificationMessage("messageIds", messageIdList));

            for (UnsentMessageType unsentMessage : unsentMessageList) {
                try {
                    String unsentMessageId = producer.sendMessageOnQueue(unsentMessage.getMessage(), MessageQueue.EVENT);
                    //TextMessage unsentResponse = consumer.getMessage(unsentMessageId, TextMessage.class);
                    sendAuditLogMessageForCreateUnsentMessage(unsentMessageId, username);
                    //ExchangeModuleResponseMapper.validateResponse(unsentResponse, unsentMessageId);
                } catch (ExchangeMessageException e) {
                    LOG.error("Error when sending/receiving message", e);
                }
            }
        }
    }

    @Override
    public PollStatus setPollStatus(String jmsCorrelationId, String pollId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException {
        try {
            // Remove the message from cache, because legancy implementation
            logCache.acknowledged(jmsCorrelationId);
            PollStatus pollStatus = new PollStatus();
            pollStatus.setPollGuid(pollId);
            pollStatus.setStatus(logStatus);

            ExchangeLogType exchangeLogType = exchangeLogModel.setPollStatus(pollStatus, username);
            pollStatus.setExchangeLogGuid(exchangeLogType.getGuid());
            sendAuditLogMessageForUpdatePollStatus(pollId, username);
            // For long polling
            exchangeLogEvent.fire(new NotificationMessage("guid", pollStatus.getExchangeLogGuid()));
            return pollStatus;
        } catch (ExchangeModelMapperException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        }
    }

    private void sendAuditLogMessageForCreateUnsentMessage(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapCreateUnsentMessage(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Unsent message was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForRemoveUnsentMessage(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapRemoveUnsentMessage(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Unsent message was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForUpdateExchangeLog(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapUpdateExchangeLog(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log with guid: " + guid + " is updated");
        }
    }

    private void sendAuditLogMessageForResendUnsentMessage(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapResendSendingQueue(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Resend sending queue with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForCreateExchangeLog(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapCreateExchangeLog(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange log was created with guid: " + guid);
        }
    }

    private void sendAuditLogMessageForUpdatePollStatus(String guid, String username) {
        try {
            String request = ExchangeAuditRequestMapper.mapUpdatePoll(guid, username);
            producer.sendMessageOnQueue(request, MessageQueue.AUDIT);
        } catch (AuditModelMarshallException | ExchangeMessageException e) {
            LOG.error("Could not send audit log message. Exchange poll with guid: " + guid + " is updated");
        }
    }
}