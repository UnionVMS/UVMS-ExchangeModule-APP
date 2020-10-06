package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;

import java.time.Instant;
import java.util.ArrayList;

public class TestHelper {

    public static ExchangeLog createBasicLog(){
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(LogType.PROCESSED_MOVEMENT);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.UNKNOWN);
        exchangeLog.setUpdatedBy("Tester");
        exchangeLog.setUpdateTime(Instant.now());
        exchangeLog.setDateReceived(Instant.now());
        exchangeLog.setFwdDate(Instant.now());
        exchangeLog.setSenderReceiver("Test sender/receiver");
        exchangeLog.setTransferIncoming(false);
        exchangeLog.setStatusHistory(new ArrayList<ExchangeLogStatus>());

        return exchangeLog;
    }

    public static void addLogStatusToLog(ExchangeLog exchangeLog, ExchangeLogStatusTypeType statusType){
        ExchangeLogStatus status = new ExchangeLogStatus();
        status.setLog(exchangeLog);
        status.setStatus(statusType);
        status.setStatusTimestamp(Instant.now());
        status.setUpdatedBy("Status updater");
        status.setUpdateTime(Instant.now());

        exchangeLog.getStatusHistory().add(status);
    }
}
