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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.time.Instant;
import java.util.*;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.UnsentMessageDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessageProperty;
import eu.europa.ec.fisheries.uvms.exchange.service.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeLogServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogServiceBean.class);

    @EJB
    private ExchangeMessageProducer producer;

    @EJB
    private ExchangeEventLogCache logCache;

    @EJB
    private ExchangeToRulesSyncMsgBean exchangeToRulesSyncMsgBean;

    @Inject
    @ExchangeLogEvent
    private Event<NotificationMessage> exchangeLogEvent;

    @Inject
    @ExchangeSendingQueueEvent
    private Event<NotificationMessage> sendingQueueEvent;

    @EJB
    private ExchangeLogModelBean exchangeLogModel;

    @EJB
    private ExchangeLogDaoBean exchangeLogDao;

    @Inject
    private UnsentMessageDaoBean unsentMessageDao;


    public ExchangeLog logAndCache(ExchangeLog log, String pluginMessageId) {
        ExchangeLog createdLog = log(log);
        logCache.put(pluginMessageId, createdLog.getId());

        return createdLog;
    }

    public ExchangeLog log(ExchangeLog log) {
        ExchangeLog exchangeLog = exchangeLogModel.createExchangeLog(log);
        String guid = exchangeLog.getId().toString();
        exchangeLogEvent.fire(new NotificationMessage("guid", guid));
        LOG.debug("[INFO] Logging message with guid : [ "+guid+" ] was successful.");
        return exchangeLog;
    }

    /**
     * Create a new log entry.
     * @param request the incoming exchange request
     * @param logType the type of the log
     * @param status the status of the message (does it needs to be validated, is it valid, ...)
     * @param messageType the type of the message
     * @param messageText XML representation of the incoming/outgoing message
     * @param incoming is this an incoming message (then true) or an outgoing message (then false)?
     * @return the created log entry
     */
    public ExchangeLog log(ExchangeBaseRequest request, LogType logType, ExchangeLogStatusTypeType status, TypeRefType messageType, String messageText, boolean incoming) {
        ExchangeLog log = new ExchangeLog();
        log.setTypeRefType(messageType);
        log.setTypeRefMessage(messageText);
        log.setTypeRefGuid( (request.getMessageGuid() == null) ? null : UUID.fromString(request.getMessageGuid()));

        log.setSenderReceiver(request.getSenderOrReceiver());
        log.setDateReceived(request.getDate().toInstant());
        log.setType(logType);
        log.setStatus(status);
        log.setTransferIncoming(incoming);
        log.setDestination(request.getDestination());
        log.setSource(request.getPluginType().toString());
        log.setOn(request.getOnValue());
        log.setTo(request.getTo());
        log.setTodt(request.getTodt());
        log.setDf(request.getFluxDataFlow());
        log.setUpdatedBy("SYSTEM");

        log = ExchangeLogMapper.addStatusHistory(log);

        return log(log);
    }

    public ExchangeLog updateStatus(String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) {
        UUID logGuid = logCache.acknowledged(pluginMessageId);
        ExchangeLogStatus exchangeLogStatus = createExchangeLogStatus(logStatus);
        ExchangeLog updatedLog = exchangeLogModel.updateExchangeLogStatus(exchangeLogStatus, username, logGuid);
        // For long polling
        exchangeLogEvent.fire(new NotificationMessage("guid", updatedLog.getId()));
        return updatedLog;
    }

    private ExchangeLogStatus createExchangeLogStatus(ExchangeLogStatusTypeType logStatus) {
        ExchangeLogStatus exchangeLogStatus = new ExchangeLogStatus();
        exchangeLogStatus.setStatus(logStatus);
        exchangeLogStatus.setUpdatedBy("SYSTEM");
        exchangeLogStatus.setStatusTimestamp(Instant.now());
        return exchangeLogStatus;
    }

    private ExchangeLogStatusType createExchangeLogBusinessError(String logGuid,String businessMessageError) {
        ExchangeLogStatusType exchangeLogStatusType = new ExchangeLogStatusType();
        exchangeLogStatusType.setGuid(logGuid);
        exchangeLogStatusType.setBusinessModuleExceptionMessage(businessMessageError);
        return exchangeLogStatusType;
    }

    /**
     * Adds a new log status to a log with the specified log guid.
     *
     * Since the guid is not something that an end user will have to, this method is assumed to be used by the system.
     * Therefore, the logged username will be "SYSTEM".
     *
     * @param logGuid guid of the log. Notice that this is NOT the internal id.
     * @param logStatus the next status
     * @return the updated log
     * @when something goes wrong
     */
    public ExchangeLog updateStatus(UUID logGuid, ExchangeLogStatusTypeType logStatus) {
        ExchangeLogStatus exchangeLogStatus = createExchangeLogStatus(logStatus);
        return exchangeLogModel.updateExchangeLogStatus(exchangeLogStatus, "SYSTEM", logGuid);
    }

    public ExchangeLogType updateExchangeLogBusinessError(UUID logGuid, String errorMessage) {
        ExchangeLogStatusType exchangeLogStatusType = createExchangeLogBusinessError(logGuid.toString(), errorMessage);
        return  exchangeLogModel.updateExchangeLogBusinessError(exchangeLogStatusType, errorMessage);
    }

    public List<UnsentMessage> getUnsentMessageList() {
        LOG.info("Get unsent message list in service layer");
        return unsentMessageDao.getAll();
    }

    public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Instant from, Instant to) {
        LOG.info("Get pollstatus list in service layer:{}",status);
        List<ExchangeLogStatusTypeType> statusList = new ArrayList<>();
        if (status != null) {
            statusList.add(status);
        }
        List<TypeRefType> typeList = new ArrayList<>();
        if (type != null) {
            typeList.add(type);
        }
        ExchangeHistoryListQuery query = new ExchangeHistoryListQuery();
        query.setTypeRefDateFrom(Date.from(from));
        query.setTypeRefDateTo(Date.from(to));
        query.getStatus().addAll(statusList);
        query.getType().addAll(typeList);

        return  exchangeLogModel.getExchangeLogStatusHistoryByQuery(query);
    }

    public ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, UUID typeRefGuid) {
        LOG.info("Get poll status history in service layer:{}",type);
        if (typeRefGuid == null) {
            throw new IllegalArgumentException("Invalid id");
        }
        return exchangeLogModel.getExchangeLogStatusHistory(typeRefGuid, type);
    }

    public String createUnsentMessage(String senderReceiver, Instant timestamp, String recipient, String message, List<UnsentMessageProperty> properties, String username) {
        LOG.debug("[INFO] CreateUnsentMessage in service layer:{}",message);
        UnsentMessage unsentMessage = new UnsentMessage();
        unsentMessage.setDateReceived(timestamp);
        unsentMessage.setSenderReceiver(senderReceiver);
        unsentMessage.setRecipient(recipient);
        unsentMessage.setMessage(message);
        unsentMessage.setUpdatedBy(username);
        unsentMessage.setProperties(new ArrayList<>());
        unsentMessage.getProperties().addAll(properties);

        String createdUnsentMessageId = unsentMessageDao.create(unsentMessage).getGuid().toString();

        List<String> unsentMessageIds = Collections.singletonList(createdUnsentMessageId);
        sendingQueueEvent.fire(new NotificationMessage("messageIds", unsentMessageIds));
        return createdUnsentMessageId;
    }

    public void removeUnsentMessage(String unsentMessageId) {
        LOG.trace("removeUnsentMessage in service layer:{}",unsentMessageId);
        if (unsentMessageId == null) {
            throw new IllegalArgumentException("No message to remove");
        }

        String removeMessageId = null;
        UnsentMessage entity = unsentMessageDao.getByGuid(UUID.fromString(unsentMessageId));
        if (entity != null) {
            removeMessageId = entity.getGuid().toString();
            unsentMessageDao.remove(entity);
        } else {
            LOG.error("[ No message with id {} to remove ]", unsentMessageId);
            throw new IllegalArgumentException("[ No message with id " + unsentMessageId + " to remove ]");
        }

        List<String> removedMessageIds = Collections.singletonList(removeMessageId);
        sendingQueueEvent.fire(new NotificationMessage("messageIds", removedMessageIds));
    }

    public void updateTypeRef(ExchangeLog exchangeLogStatus, MovementRefType movementRefType){
        exchangeLogStatus.setTypeRefType(TypeRefType.valueOf(movementRefType.getType().value()));
        exchangeLogStatus.setTypeRefGuid(UUID.fromString(movementRefType.getMovementRefGuid()));
    }

    public ExchangeLogWithValidationResults getExchangeLogRawMessageAndValidationByGuid(UUID guid) {
        ExchangeLog log = exchangeLogDao.getExchangeLogByGuid(guid);
        ExchangeLogWithValidationResults validationFromRules = new ExchangeLogWithValidationResults();
        if (log.getTypeRefType() != null){
            if (TypeRefType.FA_RESPONSE.equals(log.getTypeRefType())){
                guid = log.getTypeRefGuid();
            }
            validationFromRules = exchangeToRulesSyncMsgBean.getValidationFromRules(guid.toString(), log.getTypeRefType());
            validationFromRules.setMsg(log.getTypeRefMessage() != null ? log.getTypeRefMessage() : StringUtils.EMPTY);
        }
        return validationFromRules;
    }

    public void resend(List<String> messageIdList, String username) {
        LOG.debug("resend in service layer:{} {}",messageIdList,username);
        List<UnsentMessage> unsentMessageList;
        unsentMessageList = getAndRemoveUnsentMessagesFromDB(messageIdList);
        if (unsentMessageList != null && !unsentMessageList.isEmpty()) {
            sendingQueueEvent.fire(new NotificationMessage("messageIds", messageIdList));

            for (UnsentMessage unsentMessage : unsentMessageList) {
                try {
                    String unsentMessageId = producer.sendMessageOnQueue(unsentMessage.getMessage(), MessageQueue.EVENT);
                    //TextMessage unsentResponse = consumer.getMessage(unsentMessageId, TextMessage.class);
                    //ExchangeModuleResponseMapper.validateResponse(unsentResponse, unsentMessageId);
                } catch (Exception e) {
                    LOG.error("Error when sending/receiving message {} {}",messageIdList, e);
                }
            }
        }
    }

    public PollStatus setPollStatus(String jmsCorrelationId, UUID pollId, ExchangeLogStatusTypeType logStatus, String username) {
        // Remove the message from cache, because legancy implementation
        logCache.acknowledged(jmsCorrelationId);
        PollStatus pollStatus = new PollStatus();
        pollStatus.setPollGuid(pollId.toString());
        pollStatus.setStatus(logStatus);

        ExchangeLogType exchangeLogType = exchangeLogModel.setPollStatus(pollStatus, username);
        pollStatus.setExchangeLogGuid(exchangeLogType.getGuid());
        // For long polling
        exchangeLogEvent.fire(new NotificationMessage("guid", pollStatus.getExchangeLogGuid()));
        return pollStatus;
    }

    private List<UnsentMessage> getAndRemoveUnsentMessagesFromDB(List<String> unsentMessageId) {
        if (unsentMessageId == null) {
            throw new IllegalArgumentException("No messageList to resend");
        }

        List<UnsentMessage> unsentMessageList = new ArrayList<>();
        for (String messageId : unsentMessageId) {
            try {
                UnsentMessage message = unsentMessageDao.getByGuid(UUID.fromString(messageId));
                UnsentMessage removedMessage = unsentMessageDao.remove(message);
                unsentMessageList.add(removedMessage);
            } catch (NoResultException e) {
                LOG.error("Couldn't find message to resend with guid: " + messageId);
            }
        }
        return unsentMessageList;
    }
}