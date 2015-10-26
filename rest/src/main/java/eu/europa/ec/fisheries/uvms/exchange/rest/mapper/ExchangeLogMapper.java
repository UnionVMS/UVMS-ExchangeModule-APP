/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;

/**
 *
 * @author jojoha
 */
public class ExchangeLogMapper {

    public static ListQueryResponse mapToQueryResponse(GetLogListByQueryResponse response) {

        ListQueryResponse dto = new ListQueryResponse();

        dto.setCurrentPage(response.getCurrentPage().intValue());
        dto.setTotalNumberOfPages(response.getTotalNumberOfPages().intValue());

        for (ExchangeLogType log : response.getExchangeLog()) {
            dto.getLogs().add(mapToExchangeLogDto(log));
        }

        return dto;

    }

    public static ExchangeLog mapToExchangeLogDto(ExchangeLogType log) {
        ExchangeLog dto = new ExchangeLog();
        //TODO map exchangelog
        return dto;
    }
}
