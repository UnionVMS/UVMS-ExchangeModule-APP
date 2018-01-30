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

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
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

@MessageDriven(mappedName = MessageConstants.EVENT_BUS_TOPIC, activationConfig = {
    @ActivationConfigProperty(propertyName = MessageConstants.MESSAGE_SELECTOR_STR, propertyValue = ExchangeModelConstants.SERVICE_NAME + " = '" + ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE + "'"),
    @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = MessageConstants.SUBSCRIPTION_DURABILITY_STR, propertyValue = MessageConstants.DURABLE_CONNECTION),
    @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_TOPIC),
    @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.EVENT_BUS_TOPIC),
    @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_JNDI_NAME, propertyValue = MessageConstants.EVENT_BUS_TOPIC),
    @ActivationConfigProperty(propertyName = MessageConstants.SUBSCRIPTION_NAME_STR, propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
    @ActivationConfigProperty(propertyName = MessageConstants.CLIENT_ID_STR, propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
    @ActivationConfigProperty(propertyName = MessageConstants.CONNECTION_FACTORY_JNDI_NAME, propertyValue = MessageConstants.CONNECTION_FACTORY)
})
public class RegistryBusEventListener implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(RegistryBusEventListener.class);

    @Inject
    @RegisterServiceEvent
    private Event<PluginMessageEvent> registerServiceEvent;

    @Inject
    @UnRegisterServiceEvent
    private Event<PluginMessageEvent> unregisterServiceEvent;

    @Inject
    @PluginErrorEvent
    private Event<PluginMessageEvent> errorEvent;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        ServiceType settings = null;
        try {
            ExchangeRegistryBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeRegistryBaseRequest.class);
            LOG.info("Eventbus listener for Exchange Registry (ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE): {} {}", ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE,request);
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
            LOG.error("[ Error when receiving message on topic in exchange: {}] {}",message,e);
            errorEvent.fire(new PluginMessageEvent(textMessage, settings, ExchangePluginResponseMapper.mapToPluginFaultResponse(FaultCode.EXCHANGE_TOPIC_MESSAGE.getCode(), "Error when receiving message in exchange " + e.getMessage())));
        }
    }

}