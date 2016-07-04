package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.exchange.message.event.MdrSyncRequestMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendCommandToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendFLUXFAResponseToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.SendReportToPluginEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;

import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface ExchangeEventOutgoingService {

	/**
	 * Send a report to a plugin
	 * @param message
	 */
	public void sendReportToPlugin(@Observes @SendReportToPluginEvent ExchangeMessageEvent message);
    
	/**
	 * Send a command to a plugin
	 * @param message
	 */
    public void sendCommandToPlugin(@Observes @SendCommandToPluginEvent ExchangeMessageEvent message);

	void forwardMdrSyncMessageToPlugin(@Observes @MdrSyncRequestMessageEvent ExchangeMessageEvent message);

	public void sendFLUXFAResponseToPlugin(@Observes @SendFLUXFAResponseToPluginEvent ExchangeMessageEvent message);
}
