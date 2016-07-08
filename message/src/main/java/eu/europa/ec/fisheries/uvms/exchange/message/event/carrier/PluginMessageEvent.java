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