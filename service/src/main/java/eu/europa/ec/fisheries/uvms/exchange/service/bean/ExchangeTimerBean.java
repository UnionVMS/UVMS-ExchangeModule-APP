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
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Startup
@Singleton
public class ExchangeTimerBean {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeTimerBean.class);

    private static final int POLL_TIMEOUT_TIME_IN_MINUTES = 90;

    @Inject
    private ExchangeLogModelBean exchangeLogModelBean;

    @Inject
    private ExchangeLogModelBean exchangeLogModel;

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void pollResponseTimer() {
        try {
            Instant to = Instant.now();
            Instant from = to.minus( 1, ChronoUnit.DAYS);

            ExchangeHistoryListQuery query = new ExchangeHistoryListQuery();
            query.setTypeRefDateFrom(Date.from(from));
            query.setTypeRefDateTo(Date.from(to));
            query.getStatus().add(ExchangeLogStatusTypeType.PENDING);
            query.getType().add(TypeRefType.POLL);

            List<ExchangeLogStatus> logList = exchangeLogModel.getExchangeLogStatusHistoryByQuery(query);
            for (ExchangeLogStatus exchangeLogStatus : logList) {
                if(exchangeLogStatus.getStatusTimestamp().isBefore(Instant.now().minus(POLL_TIMEOUT_TIME_IN_MINUTES, ChronoUnit.MINUTES))) {

                    PollStatus pollStatus = new PollStatus();
                    pollStatus.setStatus(ExchangeLogStatusTypeType.TIMED_OUT);
                    pollStatus.setExchangeLogGuid(exchangeLogStatus.getLog().getId().toString());
                    pollStatus.setPollGuid(exchangeLogStatus.getLog().getTypeRefGuid().toString());

                    LOG.info("No response for poll {} for 90 minutes. Setting status timed out", exchangeLogStatus.getLog().getTypeRefGuid());
                    exchangeLogModelBean.setPollStatus(pollStatus, "Poll Response Timer", "No response to poll in 90 minutes, setting as timed out");
                }
            }

        } catch (Exception e) {
            LOG.error("[ Error when running pollResponseTimer. ] {}", e);
            throw e;
        }
    }

}
