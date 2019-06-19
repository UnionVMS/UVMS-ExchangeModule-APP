/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

@Stateless
@LocalBean
public class ExchangeRulesProducer extends AbstractProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRulesProducer.class);

    @Resource(mappedName =  "java:/" + MessageConstants.QUEUE_MODULE_RULES)
    private Queue destination;

    @Resource(mappedName = "java:/jms/queue/UVMSExchange")
    private Queue replyToQueue;

    public String sendRulesMessage(String text, String messageSelector) {
        try {
            Map<String, String> messageProperties = new HashMap<>();
            if (messageSelector != null) {
                messageProperties.put("messageSelector", messageSelector);
            }
            return sendModuleMessageWithProps(text, replyToQueue, messageProperties);

        } catch (JMSException e) {
            LOG.error("[ Error when sending rules message. ] {}", e.getMessage());
            throw new RuntimeException("Error when sending rules message.");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendSynchronousRulesMessage(String text, String messageSelector) {
        try {
            Map<String, String> messageProperties = new HashMap<>();
            if (messageSelector != null) {
                messageProperties.put("messageSelector", messageSelector);
            }
            return sendModuleMessageWithProps(text, replyToQueue, messageProperties);

        } catch (JMSException e) {
            LOG.error("[ Error when sending rules message. ] {}", e.getMessage());
            throw new RuntimeException("Error when sending rules message.");
        }
    }

    @Override
    public Destination getDestination() {
        return destination;
    }
}
