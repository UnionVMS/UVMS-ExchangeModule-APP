package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Local
public interface ExchangeLogService {

	public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId) throws ExchangeLogException;

	public ExchangeLogType log(ExchangeLogType log) throws ExchangeLogException;

	public ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException;

}
