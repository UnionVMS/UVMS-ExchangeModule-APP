/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mock;

import eu.europa.ec.fisheries.uvms.exchange.rest.constants.PollStatus;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jojoha
 */
public class ExchangeMock {

    public static List<ExchangeLog> getLogs() {

        List<ExchangeLog> logs = new ArrayList<>();
        logs.add(getLogMock(PollStatus.SUCCESSFULL, "POL"));
        logs.add(getLogMock(PollStatus.PENDING, "SWE"));
        logs.add(getLogMock(PollStatus.FAILED, "ENG"));
        return logs;

    }

    private static ExchangeLog getLogMock(PollStatus status, String countryCode) {
        ExchangeLog log = new ExchangeLog();
        log.setDateFwd(new Date());
        log.setDateRecieved(new Date());
        log.setFwdRule("MS Reports");
        log.setMessage("//SR//AD//SWE//TM//PO//SR//AD//SWE//TM//PO");
        log.setSentBy("97762274-df81-eb96-aed5-9fecd91e7ef6");
        log.setRecipient(countryCode);
        log.setStatus(status);
        return log;
    }

}
