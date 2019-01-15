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

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.enterprise.event.Observes;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Stateless
@LocalBean
public class ExchangeMessageProducerBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeMessageProducerBean.class);

    @EJB
    private ExchangeEventBusTopicProducerBean eventBusProducer;

    @EJB
    private ExchangeToExchangeProducerBean exchangeProdcerBean;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        try {
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            exchangeProdcerBean.sendResponseMessageToSender(message.getJmsMessage(), data, 600000, DeliveryMode.NON_PERSISTENT);
        } catch (ExchangeModelMapperException | JMSException | MessageException e) {
            LOG.error("Error when returning Error message to recipient");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendPluginErrorResponseMessage(@Observes @PluginErrorEvent PluginMessageEvent message) {
        try {
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());
            final String jmsMessageID = message.getJmsMessage().getJMSMessageID();
            final String serviceName = message.getServiceType() != null ? message.getServiceType().getServiceResponseMessageName() : "unknown";
            eventBusProducer.sendEventBusMessageWithSpecificIds(data, serviceName, null, null, jmsMessageID, 60000, DeliveryMode.NON_PERSISTENT);
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Topic with correlationID: {} ", jmsMessageID);
        } catch (ExchangeModelMapperException | JMSException | MessageException e) {
            LOG.error("Error when returning Error message to recipient", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendResponseMessageToSender(TextMessage jmsMessage, String text) {
        try {
            exchangeProdcerBean.sendResponseMessageToSender(jmsMessage, text);
        } catch (MessageException e) {
            LOG.error("Error when returning message to recipient!", e);
        }
    }
}
