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
package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

/**
 **/
@MessageDriven(mappedName = ExchangeModelConstants.PLUGIN_EVENTBUS, activationConfig = {
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = ExchangeModelConstants.SERVICE_NAME + " = '" + ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE + "'"),
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_TOPIC),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EVENTBUS_NAME),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = ExchangeModelConstants.PLUGIN_EVENTBUS),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = ExchangeModelConstants.CONNECTION_FACTORY)
})
public class RegistryBusEventListener implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(RegistryBusEventListener.class);

    @Inject
    @RegisterServiceEvent
    Event<PluginMessageEvent> registerServiceEvent;

    @Inject
    @UnRegisterServiceEvent
    Event<PluginMessageEvent> unregisterServiceEvent;

    @Inject
    @PluginErrorEvent
    Event<PluginMessageEvent> errorEvent;

    @Override
    public void onMessage(Message message) {

        LOG.info("Eventbus listener for Exchange Registry (ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE): {}", ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE);

        TextMessage textMessage = (TextMessage) message;
        ServiceType settings = null;

        try {
            ExchangeRegistryBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeRegistryBaseRequest.class);
            switch (request.getMethod()) {
                case REGISTER_SERVICE:
                    RegisterServiceRequest regReq = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
                    settings = regReq.getService();
                    registerServiceEvent.fire(new PluginMessageEvent(textMessage));
                    break;
                case UNREGISTER_SERVICE:
                    UnregisterServiceRequest unRegReq = JAXBMarshaller.unmarshallTextMessage(textMessage, UnregisterServiceRequest.class);
                    settings = unRegReq.getService();
                    unregisterServiceEvent.fire(new PluginMessageEvent(textMessage));
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ]", request.getMethod());
                    throw new ExchangeMessageException("[ Not implemented method consumed: " + request.getMethod() + " ]");
            }
        } catch (ExchangeMessageException | ExchangeModelMarshallException | NullPointerException e) {
            LOG.error("[ Error when receiving message on topic in exchange: ]");
            errorEvent.fire(new PluginMessageEvent(textMessage, settings, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_TOPIC_MESSAGE.getCode(), "Error when receiving message in exchange " + e.getMessage())));
        }
    }

}