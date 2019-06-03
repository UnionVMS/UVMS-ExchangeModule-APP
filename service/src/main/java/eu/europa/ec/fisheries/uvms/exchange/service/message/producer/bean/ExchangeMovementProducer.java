/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.exchange.service.message.producer.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;

@Stateless
public class ExchangeMovementProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeMovementProducer.class);

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/jms/queue/UVMSMovementEvent")
    private Queue movementQueue;

    public void sendMovementMessage(String text, String groupId) {
        try (Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(movementQueue)) {
            TextMessage message = session.createTextMessage();
            message.setStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY, "CREATE");
            message.setStringProperty(MessageConstants.JMS_MESSAGE_GROUP, groupId);
            MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(message);
            message.setText(text);
            producer.send(message);
        } catch (JMSException e) {
            LOG.error("[ Error when sending movement message. ] {}", e);
            throw new IllegalStateException("Error when sending movement message.", e);
        }
    }
}
