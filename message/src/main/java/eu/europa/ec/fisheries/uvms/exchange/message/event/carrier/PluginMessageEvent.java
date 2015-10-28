package eu.europa.ec.fisheries.uvms.exchange.message.event.carrier;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginFault;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceType;

public class PluginMessageEvent {

    private TextMessage jmsMessage;
    private ServiceType serviceType;
    private PluginFault fault;

    public PluginMessageEvent(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

    public PluginMessageEvent(TextMessage jmsMessage, ServiceType type, PluginFault fault) {
        this.jmsMessage = jmsMessage;
        this.serviceType = type;
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

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public PluginFault getFault() {
        return fault;
    }

    public void setFault(PluginFault fault) {
        this.fault = fault;
    }

}
