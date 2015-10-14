package eu.europa.ec.fisheries.uvms.exchange.message.event.carrier;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;

public class PluginMessageEvent {

    private TextMessage jmsMessage;
    private String responseTopicMessageSelector;
    private PluginFault fault;

    public PluginMessageEvent(TextMessage jmsMessage, String responseTopicMessageSelector) {
        this.jmsMessage = jmsMessage;
        this.responseTopicMessageSelector = responseTopicMessageSelector;
    }

    public PluginMessageEvent(TextMessage jmsMessage, String responseTopicMessageSelector, PluginFault fault) {
        this.jmsMessage = jmsMessage;
        this.responseTopicMessageSelector = responseTopicMessageSelector;
        this.fault = fault;
    }

    public PluginFault getErrorFault() {
        return fault;
    }

    public void setErrorFault(PluginFault fault) {
        this.fault = fault;
    }

    public TextMessage getJmsMessage() {
        return jmsMessage;
    }

    public void setJmsMessage(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

	public String getResponseTopicMessageSelector() {
		return responseTopicMessageSelector;
	}

	public void setResponseTopicMessageSelector(String responseTopicMessageSelector) {
		this.responseTopicMessageSelector = responseTopicMessageSelector;
	}

}
