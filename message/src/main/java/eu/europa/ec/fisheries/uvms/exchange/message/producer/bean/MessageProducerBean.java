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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    @Resource(mappedName = ExchangeModelConstants.QUEUE_DATASOURCE_INTERNAL)
    private Queue localDbQueue;

    @Resource(mappedName = ExchangeModelConstants.EXCHANGE_RESPONSE_QUEUE)
    private Queue responseQueue;

    @Resource(mappedName = ExchangeModelConstants.EXCHANGE_MESSAGE_IN_QUEUE)
    private Queue eventQueue;

    @Resource(mappedName = ExchangeModelConstants.PLUGIN_EVENTBUS)
    private Topic eventBus;

    @Resource(mappedName = ExchangeModelConstants.QUEUE_INTEGRATION_RULES)
    private Queue rulesQueue;

    @Resource(mappedName = ConfigConstants.CONFIG_MESSAGE_IN_QUEUE)
    private Queue configQueue;

    @Resource(mappedName = ExchangeModelConstants.QUEUE_INTEGRATION_ASSET)
    private Queue vesselQueue;

    @Resource(mappedName = ExchangeModelConstants.QUEUE_INTEGRATION_AUDIT)
    private Queue auditQueue;

    @Resource(mappedName = ExchangeModelConstants.MOVEMENT_RESPONSE_QUEUE)
    private Queue movementResponseQueue;

    private static final int CONFIG_TTL = 30000;

    @EJB
    JMSConnectorBean connector;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException {
        try {
            Session session = connector.getNewSession();
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
                case INTERNAL:
                    getProducer(session, localDbQueue).send(message);
                    break;
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
                case AUDIT:
                    getProducer(session, auditQueue).send(message);
                default:
                    break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ]");
            throw new ExchangeMessageException("[ Error when sending message. ]");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException {
        try {
            LOG.debug("Sending event bus message from Exchange module to recipient om JMS Topic to: {} ", serviceName);
            Session session = connector.getNewSession();

            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, serviceName);
            message.setJMSReplyTo(eventQueue);

            getProducer(session, eventBus).send(message);

            return message.getJMSMessageID();
        } catch (Exception e) {
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
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        try {
            Session session = connector.getNewSession();

            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());

            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (ExchangeModelMapperException | JMSException e) {
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @Override
    public void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginMessageEvent message) {
        try {
            Session session = connector.getNewSession();
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
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @Override
    public void sendModuleResponseMessage(TextMessage message, String text) {
        try {
            LOG.info("Sending message back to recipient from ExchangeModule with correlationId {} on queue: {}", message.getJMSMessageID(), message.getJMSReplyTo());
            Session session = connector.getNewSession();
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            getProducer(session, message.getJMSReplyTo()).send(response);
        } catch (JMSException e) {
            LOG.error("[ Error when returning module exchange request. ]");
        }
    }

    @Override
    public void sendModuleAckMessage(String messageId, MessageQueue queue, String text) {
        try {
            LOG.info("Sending message asynchronous back to recipient from ExchangeModule with correlationId {} on queue: {}", messageId, queue);
            Session session = connector.getNewSession();
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
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination, long ttl) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(ttl);
        return producer;
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        return getProducer(session, destination, 60000L);
    }

}