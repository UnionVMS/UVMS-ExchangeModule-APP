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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;  //leave be
import java.util.List;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.UnsentModel;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeLogEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.ExchangeSendingQueueEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Stateless
@Slf4j
public class ExchangeLogServiceBean implements ExchangeLogService {

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
    private ExchangeLogModel exchangeLogModel;

    @EJB
    private UnsentModel unsentModel;

    @EJB
    private ExchangeLogDao exchangeLogDao;

    @Override
    public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId, String username) throws ExchangeLogException {
        ExchangeLogType createdLog = log(log, username);
        logCache.put(pluginMessageId, createdLog.getGuid());

        return createdLog;
    }

    @Override
    public ExchangeLogType log(ExchangeLogType logType, String username) throws ExchangeLogException {
        try {
            ExchangeLogType exchangeLog = exchangeLogModel.createExchangeLog(logType, username);
            String guid = exchangeLog.getGuid();
            exchangeLogEvent.fire(new NotificationMessage("guid", guid));
            log.debug("[INFO] Logging message with guid : [ "+guid+" ] was successful.");
            return exchangeLog;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't create log exchange log.", e);
        }
    }

    @Override
    public ExchangeLogType log(ExchangeBaseRequest request, LogType logType, ExchangeLogStatusTypeType status, TypeRefType messageType, String messageText, boolean incoming) throws ExchangeLogException {
        LogRefType ref = new LogRefType();
        ref.setMessage(messageText);
        ref.setRefGuid(request.getMessageGuid());
        ref.setType(messageType);

        ExchangeLogType log = new ExchangeLogType();
        log.setSenderReceiver(request.getSenderOrReceiver());
        log.setDateRecieved(request.getDate());
        log.setType(logType);
        log.setStatus(status);
        log.setIncoming(incoming);
        log.setTypeRef(ref);
        log.setDestination(request.getDestination());
        log.setSource(request.getPluginType().toString());
        log.setOn(request.getOnValue());
        log.setTo(request.getTo());
        log.setTodt(request.getTodt());
        log.setDf(request.getFluxDataFlow());
        log.setGuid(request.getResponseMessageGuid());

        return log(log, request.getUsername());
    }

    @Override
    public ExchangeLogType updateStatus(String pluginMessageId, ExchangeLogStatusTypeType logStatus, String username) throws ExchangeLogException {
        try {
            String logGuid = logCache.acknowledged(pluginMessageId);
            ExchangeLogStatusType exchangeLogStatusType = createExchangeLogStatusType(logStatus, logGuid);
            ExchangeLogType updatedLog = exchangeLogModel.updateExchangeLogStatus(exchangeLogStatusType, username);
            // For long polling
            exchangeLogEvent.fire(new NotificationMessage("guid", updatedLog.getGuid()));
            return updatedLog;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log", e);
        }
    }

    private ExchangeLogStatusType createExchangeLogStatusType(ExchangeLogStatusTypeType logStatus, String logGuid) {
        ExchangeLogStatusType exchangeLogStatusType = new ExchangeLogStatusType();
        exchangeLogStatusType.setGuid(logGuid);
        ArrayList statusHistoryList = new ArrayList();
        ExchangeLogStatusHistoryType statusHistory = new ExchangeLogStatusHistoryType();
        statusHistory.setStatus(logStatus);
        statusHistoryList.add(statusHistory);
        exchangeLogStatusType.getHistory().addAll(statusHistoryList);
        return exchangeLogStatusType;
    }

    private ExchangeLogStatusType createExchangeLogBusinessError(String logGuid,String businessMessageError) {
        ExchangeLogStatusType exchangeLogStatusType = new ExchangeLogStatusType();
        exchangeLogStatusType.setGuid(logGuid);
        exchangeLogStatusType.setBusinessModuleExceptionMessage(businessMessageError);
        return exchangeLogStatusType;
    }

    @Override
    public ExchangeLogType updateStatus(String logGuid, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException {
        try {
            ExchangeLogStatusType exchangeLogStatusType = createExchangeLogStatusType(logStatus, logGuid);
            return exchangeLogModel.updateExchangeLogStatus(exchangeLogStatusType, "SYSTEM");
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update the status of the exchange log with guid " + logGuid + ". The new status should be " + logStatus, e);
        }
    }

    @Override
    public ExchangeLogType updateExchangeLogBusinessError(String logGuid, String errorMessage) throws ExchangeLogException {
        try {
            ExchangeLogStatusType exchangeLogStatusType = createExchangeLogBusinessError(logGuid, errorMessage);
        return exchangeLogModel.updateExchangeLogBusinessError(exchangeLogStatusType, errorMessage);
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update the status of the exchange log with guid " + logGuid, e);
        }
    }

    @Override
    public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException {
        log.info("Get unsent message list in service layer");
        try {
            return unsentModel.getMessageList();
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get unsent message list.");
        }
    }

    @Override
    public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Instant from, Instant to) throws ExchangeLogException {
        log.info("Get pollstatus list in service layer:{}",status);
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
            query.setTypeRefDateFrom(Date.from(from));
            query.setTypeRefDateTo(Date.from(to));
            query.getStatus().addAll(statusList);
            query.getType().addAll(typeList);
            return  exchangeLogModel.getExchangeLogStatusHistoryByQuery(query);
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange status history list.");
        }
    }

    @Override
    public ExchangeLogStatusType getExchangeStatusHistory(TypeRefType type, String typeRefGuid, String userName) throws ExchangeLogException {
        log.info("Get poll status history in service layer:{}",type);
        if (typeRefGuid == null || typeRefGuid.isEmpty()) {
            throw new ExchangeLogException("Invalid id");
        }
        try {
            return exchangeLogModel.getExchangeLogStatusHistory(typeRefGuid, type);
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't get exchange status history list.");
        }
    }

    @Override
    public String createUnsentMessage(String senderReceiver, Instant timestamp, String recipient, String message, List<UnsentMessageTypeProperty> properties, String username) throws ExchangeLogException {
        log.debug("[INFO] CreateUnsentMessage in service layer:{}",message);
        try {
            UnsentMessageType unsentMessage = new UnsentMessageType();
            unsentMessage.setDateReceived(Date.from(timestamp));
            unsentMessage.setSenderReceiver(senderReceiver);
            unsentMessage.setRecipient(recipient);
            unsentMessage.setMessage(message);
            unsentMessage.getProperties().addAll(properties);
            String createdUnsentMessageId = unsentModel.createMessage(unsentMessage, username);

            List<String> unsentMessageIds = Collections.singletonList(createdUnsentMessageId);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", unsentMessageIds));
            return createdUnsentMessageId;
        } catch (ExchangeModelException e) {
            log.error("Couldn't add message to unsent list: {} {}",message,e);
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void removeUnsentMessage(String unsentMessageId, String username) throws ExchangeLogException {
        log.debug("removeUnsentMessage in service layer:{}",unsentMessageId);
        try {
            String removeMessageId = unsentModel.removeMessage(unsentMessageId);
            List<String> removedMessageIds = Collections.singletonList(removeMessageId);
            sendingQueueEvent.fire(new NotificationMessage("messageIds", removedMessageIds));
        } catch (ExchangeModelException e) {
            log.error("Couldn't add message to unsent list {} {}",unsentMessageId,e);
            throw new ExchangeLogException("Couldn't add message to unsent list");
        }
    }

    @Override
    public void updateTypeRef(ExchangeLogType exchangeLogStatusType, MovementRefType movementRefType) throws ExchangeModelException {
        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(exchangeLogStatusType.getGuid());
        exchangeLog.setTypeRefType(TypeRefType.valueOf(movementRefType.getType().value()));
        exchangeLog.setTypeRefGuid(movementRefType.getMovementRefGuid());
    }

    @Override
    public ExchangeLogWithValidationResults getExchangeLogRawMessageAndValidationByGuid(String guid) {
        LogWithRawMsgAndType rawMsg = exchangeLogModel.getExchangeLogRawXmlByGuid(guid);
        ExchangeLogWithValidationResults validationFromRules = new ExchangeLogWithValidationResults();
        if (rawMsg.getType() != null){
            if (TypeRefType.FA_RESPONSE.equals(rawMsg.getType())){
                guid = rawMsg.getRefGuid();
            }
            validationFromRules = exchangeToRulesSyncMsgBean.getValidationFromRules(guid, rawMsg.getType());
            validationFromRules.setMsg(rawMsg.getRawMsg() != null ? rawMsg.getRawMsg() : StringUtils.EMPTY);
        }
        return validationFromRules;
    }

    @Override
    public void resend(List<String> messageIdList, String username) throws ExchangeLogException {
        log.debug("resend in service layer:{} {}",messageIdList,username);
        List<UnsentMessageType> unsentMessageList;
        try {
            unsentMessageList = unsentModel.resend(messageIdList);
        } catch (ExchangeModelException e) {
            log.error("Couldn't read unsent messages", e);
            throw new ExchangeLogException("Couldn't read unsent messages");
        }
        if (unsentMessageList != null && !unsentMessageList.isEmpty()) {
            sendingQueueEvent.fire(new NotificationMessage("messageIds", messageIdList));

            for (UnsentMessageType unsentMessage : unsentMessageList) {
                try {
                    String unsentMessageId = producer.sendMessageOnQueue(unsentMessage.getMessage(), MessageQueue.EVENT);
                    //TextMessage unsentResponse = consumer.getMessage(unsentMessageId, TextMessage.class);
                    //ExchangeModuleResponseMapper.validateResponse(unsentResponse, unsentMessageId);
                } catch (ExchangeMessageException e) {
                    log.error("Error when sending/receiving message {} {}",messageIdList, e);
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
            // For long polling
            exchangeLogEvent.fire(new NotificationMessage("guid", pollStatus.getExchangeLogGuid()));
            return pollStatus;
        } catch (ExchangeModelException e) {
            throw new ExchangeLogException("Couldn't update status of exchange log");
        }
    }
}