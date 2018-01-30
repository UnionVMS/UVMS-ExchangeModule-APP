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
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeEventBusTopicProducer;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ExchangeMessageProducerBean extends AbstractProducer implements ExchangeMessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageProducerBean.class);

    @EJB
    private ExchangeEventBusTopicProducer eventBusProducer;

    @EJB
    private ExchangeMovementProducer movementProducer;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendMessageOnQueue(String text, MessageQueue queue) throws ExchangeMessageException {
        try {
            String replyToQueue = MessageConstants.QUEUE_EXCHANGE;
            String destination = null;
            switch (queue) {
                case EVENT:
                    destination = MessageConstants.QUEUE_EXCHANGE_EVENT;
                    break;
                case RULES:
                    destination = MessageConstants.QUEUE_MODULE_RULES;
                    break;
                case CONFIG:
                    destination = MessageConstants.QUEUE_CONFIG;
                    break;
                case VESSEL:
                    destination = MessageConstants.QUEUE_ASSET_EVENT;
                    break;
                case SALES:
                    destination = MessageConstants.QUEUE_SALES_EVENT;
                    break;
                case AUDIT:
                    destination = MessageConstants.QUEUE_AUDIT_EVENT;
                    break;
                case ACTIVITY_EVENT:
                    destination = MessageConstants.QUEUE_MODULE_ACTIVITY;
                    break;
                case MDR_EVENT:
                    destination = MessageConstants.QUEUE_MDR_EVENT;
                    break;
                case RULES_RESPONSE:
                    destination = MessageConstants.QUEUE_RULES;
                    break;
                default:
                    break;
            }
            if(StringUtils.isNotEmpty(destination)){
                return this.sendMessageToSpecificQueue(text, destination, replyToQueue);
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
            return eventBusProducer.sendEventBusMessage(text, serviceName, MessageConstants.QUEUE_EXCHANGE_EVENT);
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
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        try {
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            this.sendModuleResponseMessage(message.getJmsMessage(), data);
        } catch (ExchangeModelMapperException | JMSException e) {
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
        } catch (JMSException e) {
            LOG.error("[ Error when returning asynchronous module exchange response. ]");
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_EXCHANGE;
    }

}
