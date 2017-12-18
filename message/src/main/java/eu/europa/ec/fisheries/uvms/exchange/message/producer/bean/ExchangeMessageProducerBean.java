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

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.*;

@Stateless
public class ExchangeMessageProducerBean implements ExchangeMessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageProducerBean.class);

    private Queue responseQueue;
    private Queue eventQueue;
    private Topic eventBus;
    private Queue rulesQueue;
    private Queue configQueue;
    private Queue vesselQueue;
    private Queue auditQueue;
    private Queue movementResponseQueue;
    private Queue activityQueue;
    private Queue mdrQueue;
    private Queue salesQueue;
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        responseQueue = JMSUtils.lookupQueue(ExchangeModelConstants.EXCHANGE_RESPONSE_QUEUE);
        eventQueue = JMSUtils.lookupQueue(ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE);
        rulesQueue = JMSUtils.lookupQueue(ExchangeModelConstants.QUEUE_INTEGRATION_RULES);
        configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
        vesselQueue = JMSUtils.lookupQueue(ExchangeModelConstants.QUEUE_INTEGRATION_ASSET);
        auditQueue = JMSUtils.lookupQueue(ExchangeModelConstants.QUEUE_INTEGRATION_AUDIT);
        movementResponseQueue = JMSUtils.lookupQueue(ExchangeModelConstants.MOVEMENT_RESPONSE_QUEUE);
        eventBus = JMSUtils.lookupTopic(ExchangeModelConstants.PLUGIN_EVENTBUS);
        activityQueue = JMSUtils.lookupQueue(ExchangeModelConstants.ACTIVITY_EVENT_QUEUE);
        mdrQueue = JMSUtils.lookupQueue(ExchangeModelConstants.MDR_EVENT_QUEUE);
        salesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES_EVENT);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException {

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
                case EVENT:
                    getProducer(session, eventQueue).send(message);
                    break;
                case RULES:
                    getProducer(session, rulesQueue, 0L).send(message);
                    break;
                case CONFIG:
                    getProducer(session, configQueue).send(message);
                    break;
                case VESSEL:
                    getProducer(session, vesselQueue).send(message);
                    break;
                case SALES:
                    getProducer(session, salesQueue).send(message);
                    break;
                case AUDIT:
                    getProducer(session, auditQueue).send(message);
                    break;
                case ACTIVITY_EVENT:
                    getProducer(session, activityQueue).send(message);
                    break;
                case MDR_EVENT:
                    getProducer(session, mdrQueue).send(message);
                    break;
                default:
                    break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ]");
            throw new ExchangeMessageException("[ Error when sending message. ]");
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException {
        Connection connection = null;
        try {
            LOG.debug("Sending event bus message from Exchange module to recipient om JMS Topic to: {} ", serviceName);
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, serviceName);
            message.setJMSReplyTo(eventQueue);

            getProducer(session, eventBus).send(message);

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] ", e);
            throw new ExchangeMessageException("[ Error when sending message. ]");
        } finally {
            JMSUtils.disconnectQueue(connection);
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
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());

            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (ExchangeModelMapperException | JMSException e) {
            LOG.error("Error when returning Error message to recipient");
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    public void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginMessageEvent message) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Topic with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            TextMessage response = session.createTextMessage(data);
            if (message.getServiceType() != null) {
                response.setStringProperty(ExchangeModelConstants.SERVICE_NAME, message.getServiceType().getServiceResponseMessageName());
            } else {
                response.setStringProperty(ExchangeModelConstants.SERVICE_NAME, "unknown");
            }
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, eventBus).send(response);

        } catch (ExchangeModelMapperException | JMSException e) {
            LOG.error("Error when returning Error message to recipient", e);
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    public void sendModuleResponseMessage(TextMessage message, String text) {
        Connection connection = null;
        try {
            LOG.info("Sending message back to recipient from ExchangeModule with text {} on queue: {}", text, message.getJMSReplyTo());
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            getProducer(session, message.getJMSReplyTo()).send(response);
        } catch (JMSException e) {
            LOG.error("[ Error when returning module exchange request. ]");
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    public void sendModuleAckMessage(String messageId, MessageQueue queue, String text) {
        Connection connection = null;

        try {
            LOG.info("Sending message asynchronous back to recipient from ExchangeModule with text {} on queue: {}", text, queue);
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(messageId);

            switch (queue) {
                case MOVEMENT_RESPONSE:
                    getProducer(session, movementResponseQueue).send(response);
                    break;
                default:
                    break;
            }

        } catch (JMSException e) {
            LOG.error("[ Error when returning asynchronous module exchange response. ]");
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination, long ttl) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.setTimeToLive(ttl);
        return producer;
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        return getProducer(session, destination, 60000L);
    }

}
