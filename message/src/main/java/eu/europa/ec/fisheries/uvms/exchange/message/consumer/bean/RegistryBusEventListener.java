/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.ExchangeRegistryBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceRequest;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.RegisterServiceResponse;
import eu.europa.ec.fisheries.schema.exchange.registry.v1.UnregisterServiceRequest;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.PluginErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.RegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.registry.UnRegisterServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import java.util.logging.Level;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jojoha
 */
@MessageDriven(mappedName = ExchangeModelConstants.EVENTBUS, activationConfig = {
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = ExchangeModelConstants.SERVICE_NAME + " = '" + ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE + "'"),
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = ExchangeModelConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = ExchangeModelConstants.DESTINATION_TYPE_TOPIC),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = ExchangeModelConstants.EVENTBUS_NAME),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE)
})
public class RegistryBusEventListener implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(RegistryBusEventListener.class);

    @Inject
    @RegisterServiceEvent
    Event<ExchangeMessageEvent> pluginConfigEvent;

    @Inject
    @UnRegisterServiceEvent
    Event<ExchangeMessageEvent> processMovementEvent;

    @Inject
    @PluginErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

    @EJB
    MessageProducer messageProducer;

    @Override
    public void onMessage(Message message) {

        LOG.info("Eventbus listener for Exchange Registry (ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE): {}", ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE);

        TextMessage textMessage = (TextMessage) message;

        try {

            ExchangeRegistryBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, ExchangeRegistryBaseRequest.class);

            String responseMessage = null;
            String serviceName = null;

            switch (request.getMethod()) {
                case REGISTER_SERVICE:
                    RegisterServiceRequest register = JAXBMarshaller.unmarshallTextMessage(textMessage, RegisterServiceRequest.class);
                    serviceName = register.getResponseTopicMessageSelector();
                    responseMessage = ExchangePluginResponseMapper.mapToRegisterServiceResponse(AcknowledgeTypeType.OK, register.getService(), null);
                    LOG.info("GOT REGISTER CALL FROM " + register.getService().getName());
                    break;
                case UNREGISTER_SERVICE:
                    UnregisterServiceRequest unregister = JAXBMarshaller.unmarshallTextMessage(textMessage, UnregisterServiceRequest.class);
                    LOG.info("GOT UNREGISTER CALL FROM " + unregister.getService().getName());
                    break;
                default:
                    String faultResponse = ExchangePluginResponseMapper.mapToPluginFaultResponse(1, "Method not recognized in plugin");
                    ExchangeFault fault = new ExchangeFault();
                    fault.setCode(1);
                    fault.setMessage(faultResponse);
                    errorEvent.fire(new ExchangeMessageEvent(textMessage, fault));
                    break;
            }

            messageProducer.sendEventBusMessage(responseMessage, serviceName);

        } catch (ExchangeMessageException | ExchangeModelMarshallException | NullPointerException e) {
            LOG.error("[ Error when receiving message in exchange: ]", e);
            LOG.info("[ Trying to send Error Message backs to the requesting queue : ]", e);
        }
    }

}
