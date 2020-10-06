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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.config.ParameterKey;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.UnsentMessageDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.unsent.UnsentMessage;

@Startup
@Singleton
public class UnsentMessageTimerBean {

    private static final Logger LOG = LoggerFactory.getLogger(UnsentMessageTimerBean.class);

    @Inject
    private UnsentMessageDaoBean unsentMessageDao;

    @Inject
    private ExchangeLogServiceBean serviceLayer;

    @EJB
    private ParameterService parameterService;

    @Schedule(minute = "*/10", hour = "*", persistent = false)
    public void resendUnsentMessages() {
        LOG.debug("Starting unsent message timer bean..");
        Integer threshold = getUnsentMessageThreshold();
        if (threshold == null || threshold == 0) {
            LOG.debug("Execution cancelled, threshold is {}", threshold);
            return;
        }
        List<UnsentMessage> unsentMessageList = unsentMessageDao.getAll();
        for (UnsentMessage unsentMessage : unsentMessageList) {
            if (unsentMessage.getUpdateTime().isBefore(Instant.now().minus(threshold, ChronoUnit.MINUTES))) {
                LOG.info("Resending unsent message {} to {}", unsentMessage.getGuid(), unsentMessage.getRecipient());
                serviceLayer.resend(Arrays.asList(unsentMessage.getGuid().toString()), "System");
            }
        }
    }

    private Integer getUnsentMessageThreshold() {
        try {
            return Integer.valueOf(parameterService.getStringValue(ParameterKey.UNSENT_MESSAGE_THRESHOLD.getKey()));
        } catch (NumberFormatException | ConfigServiceException e) {
            LOG.error("Could not get parameter: {}", ParameterKey.UNSENT_MESSAGE_THRESHOLD.getKey());
            return null;
        }
    }
}
