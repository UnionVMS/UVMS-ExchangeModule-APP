package eu.europa.ec.fisheries.uvms.exchange.service.timer;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.service.TestHelper;
import eu.europa.ec.fisheries.uvms.exchange.service.TransactionalTests;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.service.timer.ExchangePollResponseTimerBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ExchangePollResponseTimerBeanTest extends TransactionalTests {

    @Inject
    ExchangeLogDaoBean exchangeLogDao;

    @Inject
    ExchangePollResponseTimerBean exchangeTimerBean;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void pollResponseTimerTest() {
        ExchangeLog exchangeLog = TestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PENDING);
        exchangeLog.setDateReceived(Instant.now().minus(2, ChronoUnit.HOURS));
        exchangeLog.setTypeRefMessage("poll response timer test");

        TestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PENDING);
        ExchangeLogStatus exchangeLogStatus = exchangeLog.getStatusHistory().get(0);
        exchangeLogStatus.setStatusTimestamp(Instant.now());
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        exchangeTimerBean.pollResponseTimer();

        ExchangeLog updatedExchangeLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());

        assertEquals(ExchangeLogStatusTypeType.FAILED, updatedExchangeLog.getStatus());
        assertTrue(updatedExchangeLog.getStatusHistory().size() == 2);
        assertTrue(updatedExchangeLog.getStatusHistory().stream().anyMatch(log -> log.getStatus().equals(ExchangeLogStatusTypeType.FAILED)));
        
    }

}
