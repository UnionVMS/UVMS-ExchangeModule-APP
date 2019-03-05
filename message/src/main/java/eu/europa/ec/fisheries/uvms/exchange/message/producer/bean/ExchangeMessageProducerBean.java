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
package eu.europa.ec.fisheries.uvms.exchange.message.producer.bean;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

@Stateless
public class ExchangeMessageProducerBean extends AbstractProducer implements ExchangeMessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageProducerBean.class);

    @EJB
    private ExchangeEventBusTopicProducer eventBusProducer;

    @EJB
    private ExchangeMovementProducer movementProducer;

    @EJB
    private ExchangeRulesProducer rulesProducer;

    private Queue exchangeResponseQueue;
    private Queue exchangeEventQueue;
    private Topic eventBus;
    private Queue rulesQueue;
    private Queue configQueue;
    private Queue vesselQueue;
    private Queue auditQueue;
    private Queue movementResponseQueue;
    private Queue activityQueue;
    private Queue mdrQueue;
    private Queue salesQueue;
    private Queue rulesResponseQueue;

    @PostConstruct
    public void init() {
        exchangeResponseQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE);
        exchangeEventQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE_EVENT);
        rulesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MODULE_RULES);
        configQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_CONFIG);
        vesselQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_ASSET_EVENT);
        auditQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_AUDIT_EVENT);
        movementResponseQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MOVEMENT);
        eventBus = JMSUtils.lookupTopic(MessageConstants.EVENT_BUS_TOPIC);
        activityQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MODULE_ACTIVITY);
        mdrQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MDR_EVENT);
        salesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES_EVENT);
        rulesResponseQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_RULES);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException {
        try {
            Queue destination = getDestinationQueue(queue);
            if(destination != null){
                return this.sendMessageToSpecificQueue(text, destination, exchangeResponseQueue);
            }
            return null;
        } catch (MessageException e) {
            LOG.error("[ Error when sending message. ]");
            throw new ExchangeMessageException("[ Error when sending message. ]");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException {
        try {
            LOG.debug("Sending event bus message from Exchange module to recipient om JMS Topic to: {} ", serviceName);
            return eventBusProducer.sendEventBusMessage(text, serviceName, exchangeEventQueue);
        } catch (MessageException e) {
            LOG.error("[ Error when sending message. ] ", e);
            throw new ExchangeMessageException("[ Error when sending message. ]");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String text) throws ConfigMessageException {
        try {
            return sendMessageOnQueue(text, MessageQueue.CONFIG);
        } catch (ExchangeMessageException e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ConfigMessageException("Error when sending config message.");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendRulesMessage(String text) throws ConfigMessageException {
        try {
            return sendMessageOnQueue(text, MessageQueue.RULES);
        } catch (ExchangeMessageException e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ConfigMessageException("Error when sending config message.");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendRulesMessage(String text, String messageSelector) throws ExchangeMessageException {
        try {
            Map<String, String> messageProperties = new HashMap<>();
            if (messageSelector != null) {
                messageProperties.put("messageSelector", messageSelector);
            }
            return rulesProducer.sendModuleMessageWithProps(text, exchangeResponseQueue, messageProperties);

        } catch (MessageException e) {
            LOG.error("[ Error when sending rules message. ] {}", e.getMessage());
            throw new ExchangeMessageException("Error when sending rules message.");
        }
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendMovementMessage(String text, String groupId) throws ExchangeMessageException {
        try {
            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, "CREATE");
            properties.put(MessageConstants.JMS_MESSAGE_GROUP, groupId);
            return movementProducer.sendModuleMessageWithProps(text, exchangeResponseQueue, properties);
        } catch (MessageException e) {
            LOG.error("[ Error when sending movement message. ] {}", e);
            throw new ExchangeMessageException("Error when sending movement message.");
        }
    }

    @Override
    public String forwardToAsset(String text) throws ExchangeMessageException {
        try {
            Queue destination = getDestinationQueue(MessageQueue.VESSEL);
            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, "ASSET_INFORMATION");
            String s = "";
            if(destination != null) {
                s = this.sendMessageToSpecificQueueWithFunction(text, destination, null,"ASSET_INFORMATION",null);
            }
            return s;
        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new ExchangeMessageException("Error when sending asset info message.", e);
        }

    }


    @Override
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        try {
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            this.sendResponseMessageToSender(message.getJmsMessage(), data);
        } catch (ExchangeModelMapperException | JMSException | MessageException e) {
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @Override
    public void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginMessageEvent message) {
        try {
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            final String jmsMessageID = message.getJmsMessage().getJMSMessageID();
            final String serviceName = message.getServiceType() != null ? message.getServiceType().getServiceResponseMessageName() : "unknown";
            eventBusProducer.sendEventBusMessageWithSpecificIds(data, serviceName, null, null, jmsMessageID);
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Topic with correlationID: {} ", jmsMessageID);
        } catch (ExchangeModelMapperException | JMSException | MessageException e) {
            LOG.error("Error when returning Error message to recipient", e);
        }
    }

    @Override
    public void sendModuleAckMessage(String messageId, MessageQueue queue, String text) {
        try {
            LOG.debug("Sending message asynchronous back to recipient from ExchangeModule with text {} on queue: {}", text, queue);
            switch (queue) {
                case MOVEMENT_RESPONSE:
                    movementProducer.sendMessageWithSpecificIds(text, movementProducer.getDestination(), null, messageId, messageId);
                    break;
                default:
                    LOG.error("[ERROR] Module queue not implemented!");
                    break;
            }
        } catch (MessageException e) {
            LOG.error("[ Error when returning asynchronous module exchange response. ]");
        }
    }

    private Queue getDestinationQueue(MessageQueue queue) {
        Queue destination = null;
        switch (queue) {
            case EVENT:
                destination = exchangeEventQueue;
                break;
            case RULES:
                destination = rulesQueue;
                break;
            case CONFIG:
                destination = configQueue;
                break;
            case VESSEL:
                destination = vesselQueue;
                break;
            case SALES:
                destination = salesQueue;
                break;
            case AUDIT:
                destination = auditQueue;
                break;
            case ACTIVITY_EVENT:
                destination = activityQueue;
                break;
            case MDR_EVENT:
                destination = mdrQueue;
                break;
            case RULES_RESPONSE:
                destination = rulesResponseQueue;
                break;
            default:
                break;
        }
        return destination;
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_EXCHANGE;
    }

}
