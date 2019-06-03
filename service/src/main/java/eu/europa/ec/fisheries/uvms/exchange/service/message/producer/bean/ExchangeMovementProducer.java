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
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

@Stateless
@LocalBean
public class ExchangeMovementProducer extends AbstractProducer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMovementProducer.class);

    @Resource(mappedName = "java:/jms/queue/UVMSExchange")
    private Queue replyToQueue;

    public String sendMovementMessage(String text, String groupId) {
        try {
            Map<String, String> properties = new HashMap<>();
            properties.put(MessageConstants.JMS_FUNCTION_PROPERTY, "CREATE");
            properties.put(MessageConstants.JMS_MESSAGE_GROUP, groupId);
            return sendModuleMessageWithProps(text, replyToQueue, properties);
        } catch (MessageException e) {
            LOG.error("[ Error when sending movement message. ] {}", e);
            throw new RuntimeException("Error when sending movement message.", e);
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_MODULE_MOVEMENT;
    }
}
