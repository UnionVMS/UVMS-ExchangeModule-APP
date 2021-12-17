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

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.json.bind.Jsonb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;

@RequestScoped
public class EventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(EventProducer.class);

    private Jsonb jsonb = new JsonBConfigurator().getContext(null);

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Topic destination;

    public void sendServiceRegisteredEvent(ServiceResponseType service) {
        sendServiceEvent(service, "Service Registered");
    }

    public void sendServiceUnregisteredEvent(ServiceResponseType service) {
        sendServiceEvent(service, "Service Unregistered");
    }

    public void sendServiceEvent(ServiceResponseType service, String eventType) {
        try {
            String message = jsonb.toJson(service);
            sendMessageOnEventStream(message, eventType);
        } catch (JMSException e) {
            LOG.error("Could not send \"{}\" event for {}", eventType, service.getName(), e);
        }
    }

    public void sendMessageOnEventStream(String outgoingJson, String eventName) throws JMSException {
        TextMessage message = context.createTextMessage(outgoingJson);
        message.setStringProperty(MessageConstants.EVENT_STREAM_EVENT, eventName);
        message.setStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST, null);
        MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(message);

        context.createProducer()
            .setDeliveryMode(DeliveryMode.PERSISTENT)
            .send(destination, message);
    }
}
