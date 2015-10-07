package eu.europa.ec.fisheries.uvms.exchange.message.event.carrier;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;

public class ExchangeMessageEvent {

    private TextMessage jmsMessage;
    private ExchangeFault fault;

    public ExchangeMessageEvent(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

    public ExchangeMessageEvent(TextMessage jmsMessage, ExchangeFault fault) {
        this.jmsMessage = jmsMessage;
        this.fault = fault;
    }

    public ExchangeFault getErrorFault() {
        return fault;
    }

    public void setErrorFault(ExchangeFault fault) {
        this.fault = fault;
    }

    public TextMessage getJmsMessage() {
        return jmsMessage;
    }

    public void setJmsMessage(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

}
