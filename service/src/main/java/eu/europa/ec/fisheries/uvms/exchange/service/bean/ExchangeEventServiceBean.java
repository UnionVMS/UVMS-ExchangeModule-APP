package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PingEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.PluginConfigEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SetMovementEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.EventService;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;

@Stateless
public class ExchangeEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeEventServiceBean.class);

    @Inject
    @ErrorEvent
    Event<ExchangeMessageEvent> errorEvent;

    @EJB
    MessageProducer producer;

    @EJB
    ExchangeService exchangeService;

    @Override
    public void getPluginConfig(@Observes @PluginConfigEvent ExchangeMessageEvent message) {
        LOG.info("Received MessageRecievedEvent");
        List<ServiceType> serviceList;
		try {
			serviceList = exchangeService.getServiceList();
			producer.sendModuleResponseMessage(message.getJmsMessage(), ExchangeModuleResponseMapper.mapServiceListResponse(serviceList));
		} catch (ExchangeException e) {
			LOG.error("[ Error when getting plugin list from source]");
			errorEvent.fire(new ExchangeMessageEvent(message.getJmsMessage(), ExchangeModuleResponseMapper.createFaultMessage(FaultCode.EXCHANGE_MESSAGE, "Excpetion when getting service list")));
		}
    }

	@Override
	public void processMovement(@Observes @SetMovementEvent ExchangeMessageEvent message) {
		LOG.info("Process movement");
		//TODO
		//PROCESS MOVEMENT
		//producer.sendModuleResponseMessage(message, text);
	}

	@Override
	public void ping(@Observes @PingEvent ExchangeMessageEvent message) {
		try {
			PingResponse response = new PingResponse();
			response.setResponse("pong");
			producer.sendModuleResponseMessage(message.getJmsMessage(), JAXBMarshaller.marshallJaxBObjectToString(response));
		} catch (ExchangeModelMarshallException e) {
			LOG.error("[ Error when marshalling ping response ]");
		}
	}

}
