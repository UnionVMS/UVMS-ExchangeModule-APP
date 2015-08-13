/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.rest.constants.PollStatus;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.util.DateUtil;

/**
 *
 * @author jojoha
 */
public class ExchangeLogMapper {

    public static ListQueryResponse mapToQueryResponse(GetLogListByQueryResponse response) {

        ListQueryResponse dto = new ListQueryResponse();

        dto.setCurrentPage(response.getCurrentPage().intValue());
        dto.setTotalNumberOfPages(response.getTotalNumberOfPages().intValue());

        for (ExchangeLogType log : response.getExchangeLogs()) {
            dto.getLogs().add(mapToExchangeLogDto(log));
        }

        return dto;

    }

    public static ExchangeLog mapToExchangeLogDto(ExchangeLogType log) {
        ExchangeLog dto = new ExchangeLog();

        dto.setDateFwd(DateUtil.parseTimestamp(log.getDateFwd()));
        dto.setDateRecieved(DateUtil.parseTimestamp(log.getDateRecieved()));
        dto.setFwdRule(log.getFwdRule());
        dto.setMessage(log.getMessage());
        dto.setSentBy(log.getSentBy());
        dto.setRecipient(log.getRecipient());
        dto.setStatus(mapExchangeLogStatus(log.getStatus()));

        return dto;
    }

    public static PollStatus mapExchangeLogStatus(ExchangeLogStatusType status) {
        switch (status) {
            case SUCCESSFUL:
                return PollStatus.SUCCESSFULL;
            case PENDING:
                return PollStatus.PENDING;
            case FAILED:
                return PollStatus.FAILED;
            default:
                return null;
        }
    }

}
