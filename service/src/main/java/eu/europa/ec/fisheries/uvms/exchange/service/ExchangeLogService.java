package eu.europa.ec.fisheries.uvms.exchange.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Local
public interface ExchangeLogService {

	public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId) throws ExchangeLogException;

	public ExchangeLogType log(ExchangeLogType log) throws ExchangeLogException;

	public ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException;

	public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException;
	
	public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException;
	
}
