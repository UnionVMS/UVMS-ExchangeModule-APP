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
package eu.europa.ec.fisheries.uvms.exchange.service.timer;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Startup
@Singleton
public class ExchangePollResponseTimerBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangePollResponseTimerBean.class);

    private static final int POLL_TIMEOUT_TIME_IN_MINUTES = 90;

    private ExchangeListQuery query;

    private ExchangeListCriteriaPair dateCriteria;

    @Inject
    private ExchangeLogModelBean exchangeLogModelBean;

    @Inject
    private ExchangeLogModelBean exchangeLogModel;

    @EJB
    private ExchangeLogDaoBean logDao;

    @PostConstruct
    public void init(){

        ExchangeListPagination pagination = new ExchangeListPagination();
        pagination.setListSize(1000);
        pagination.setPage(1);

        query = new ExchangeListQuery();
        query.setPagination(pagination);


        ExchangeListCriteria criteria = new ExchangeListCriteria();
        criteria.setIsDynamic(true);
        ExchangeListCriteriaPair pair = new ExchangeListCriteriaPair();
        pair.setKey(SearchField.STATUS);
        pair.setValue(ExchangeLogStatusTypeType.PENDING.value());
        criteria.getCriterias().add(pair);

        pair = new ExchangeListCriteriaPair();
        pair.setKey(SearchField.TYPE);
        //pair.setValue(LogType.SEND_POLL.value());   //what is known as 'send poll' in one place is 'poll' in another.....
        pair.setValue("POLL");
        criteria.getCriterias().add(pair);

        pair = new ExchangeListCriteriaPair();
        pair.setKey(SearchField.DATE_RECEIVED_FROM);
        pair.setValue(DateUtils.dateToEpochMilliseconds(Instant.now()));
        criteria.getCriterias().add(pair);

        query.setExchangeSearchCriteria(criteria);
        dateCriteria = pair;
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void pollResponseTimer() {
        try {
            Instant from = Instant.now().minus( 1, ChronoUnit.DAYS);
            dateCriteria.setValue(DateUtils.dateToEpochMilliseconds(from));

            ListResponseDto logList = exchangeLogModel.getExchangeLogListByQuery(query);
            for (ExchangeLogType exchangeLog : logList.getExchangeLogList()) {
                if(exchangeLog.getDateRecieved().toInstant().isBefore(Instant.now().minus(POLL_TIMEOUT_TIME_IN_MINUTES, ChronoUnit.MINUTES))) {

                    PollStatus pollStatus = new PollStatus();
                    pollStatus.setStatus(ExchangeLogStatusTypeType.TIMED_OUT);
                    pollStatus.setExchangeLogGuid(exchangeLog.getGuid());
                    pollStatus.setPollGuid(exchangeLog.getTypeRef().getRefGuid());

                    ExchangeLog exchangeLogByGuid = logDao.getExchangeLogByGuid(UUID.fromString(exchangeLog.getGuid()));
                    exchangeLogByGuid.setStatus(ExchangeLogStatusTypeType.TIMED_OUT);
                    logDao.updateLog(exchangeLogByGuid);

                    LOG.info("No response for poll {} for 90 minutes. Setting status timed out", exchangeLog.getTypeRef().getRefGuid());
                    exchangeLogModelBean.setPollStatus(pollStatus, "Poll Response Timer", "No response to poll in 90 minutes, setting as timed out");
                }
            }

        } catch (Exception e) {
            LOG.error("[ Error when running pollResponseTimer. ] {}", e);
            throw e;
        }
    }

}
