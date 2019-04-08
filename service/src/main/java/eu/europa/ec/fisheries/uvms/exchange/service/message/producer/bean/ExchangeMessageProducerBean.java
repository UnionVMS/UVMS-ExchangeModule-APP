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
package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

import eu.europa.ec.fisheries.uvms.exchange.service.message.event.PluginErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.service.message.constants.MessageQueue;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.PluginErrorEventCarrier;
import eu.europa.ec.fisheries.uvms.exchange.service.message.producer.ExchangeMessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

@Stateless
public class ExchangeMessageProducerBean extends AbstractProducer implements ExchangeMessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageProducerBean.class);

    @Inject
    private ExchangeEventBusTopicProducer eventBusProducer;

    @Inject
    private ExchangeMovementProducer movementProducer;

    @Inject
    private ExchangeRulesProducer rulesProducer;

    @Inject
    private ExchangeAssetProducer assetProducer;

    @Inject
    private ExchangeSalesProducer salesProducer;

    @Inject
    private ExchangeEventProducer eventProducer;

    @Inject
    private ExchangeConfigProducer configProducer;

    @Override
    public String sendEventBusMessage(String text, String serviceName) {
        try {
            LOG.debug("Sending event bus message from Exchange module to recipient om JMS Topic to: {} ", serviceName);
            return eventBusProducer.sendEventBusMessage(text, serviceName, getPluginReplyTo());
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] ", e);
            throw new RuntimeException("[ Error when sending message. ]", e);
        }
    }

    @Override
    public String sendConfigMessage(String text) {
        try{
            return configProducer.sendModuleMessage(text, JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE));
        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }


    @Override
    public String sendRulesMessage(String text, String messageSelector) {
        try {
            Map<String, String> messageProperties = new HashMap<>();
            if (messageSelector != null) {
                messageProperties.put("messageSelector", messageSelector);
            }
            return rulesProducer.sendModuleMessageWithProps(text, getModuleReplyTo(), messageProperties);

        } catch (MessageException e) {
            LOG.error("[ Error when sending rules message. ] {}", e.getMessage());
            throw new RuntimeException("Error when sending rules message.");
        }
    }
    
    @Override
    public String sendMovementMessage(String text, String groupId) {
        try {
            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, "CREATE");
            properties.put(MessageConstants.JMS_MESSAGE_GROUP, groupId);
            return movementProducer.sendModuleMessageWithProps(text, getModuleReplyTo(), properties);
        } catch (MessageException e) {
            LOG.error("[ Error when sending movement message. ] {}", e);
            throw new RuntimeException("Error when sending movement message.");
        }
    }

    @Override
    public String forwardToAsset(String text, String function) {
        try {

            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, function);
            return assetProducer.sendModuleMessageWithProps(text, getModuleReplyTo(), properties);

        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    @Override
    public String sendExchangeEventMessage(String text, String function){
        try {

            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, function);
            return eventProducer.sendModuleMessageWithProps(text, getModuleReplyTo(), properties);

        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    @Override
    public String sendSalesMessage(String text){
        try{
            return salesProducer.sendModuleMessage(text, JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE));
        } catch (MessageException e) {
            LOG.error("[ Error when sending Asset info message. ] {}", e);
            throw new RuntimeException("Error when sending asset info message.", e);
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeErrorEvent message) {
        try {
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            this.sendResponseMessageToSender(message.getJmsMessage(), data);
        } catch (Exception e) {
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @Override
    public void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginErrorEventCarrier message) {
        try {
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            final String jmsMessageID = message.getJmsMessage().getJMSMessageID();
            final String serviceName = message.getServiceType() != null ? message.getServiceType() : "unknown";
            eventBusProducer.sendEventBusMessageWithSpecificIds(data, serviceName, null, null, jmsMessageID);
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Topic with correlationID: {} ", jmsMessageID);
        } catch (Exception e) {
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

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_EXCHANGE;
    }

    private Destination getPluginReplyTo(){
        return JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE_EVENT);
    }

    private Destination getModuleReplyTo(){
        return JMSUtils.lookupQueue(MessageConstants.QUEUE_EXCHANGE);
    }
}
