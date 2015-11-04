package eu.europa.ec.fisheries.uvms.exchange.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;

@Local
public interface ExchangeLogService {

	public ExchangeLogType logAndCache(ExchangeLogType log, String pluginMessageId) throws ExchangeLogException;

	public ExchangeLogType log(ExchangeLogType log) throws ExchangeLogException;

	public ExchangeLogType updateStatus(String messageId, ExchangeLogStatusTypeType logStatus) throws ExchangeLogException;

	public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) throws ExchangeLogException;
	
	public List<UnsentMessageType> getUnsentMessageList() throws ExchangeLogException;

	public List<ExchangeLogStatusType> getExchangeStatusHistoryList(ExchangeLogStatusTypeType status, TypeRefType type, Date from, Date to) throws ExchangeLogException;

	public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException;

	public String createUnsentMessage(String senderReceiver, XMLGregorianCalendar timestamp, String recipient, String message) throws ExchangeLogException;
}
