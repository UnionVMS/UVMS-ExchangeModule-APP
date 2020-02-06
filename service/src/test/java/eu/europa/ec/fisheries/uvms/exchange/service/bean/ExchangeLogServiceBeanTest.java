package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.longpolling.notifications.NotificationMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.enterprise.event.Event;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeLogServiceBeanTest {

    @InjectMocks
    private ExchangeLogServiceBean exchangeLogService;

    @Mock
    private ExchangeLogModelBean exchangeLogModel;

    @Mock
    private Event<NotificationMessage> exchangeLogEvent; //yes this is used

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void updateStatusByLogGuidWhenSuccess() throws Exception {
        //data set
        ArgumentCaptor<ExchangeLogStatus> captorForExchangeLogStatus = ArgumentCaptor.forClass(ExchangeLogStatus.class);
        ExchangeLog expectedUpdatedLog = new ExchangeLog();
        UUID logGuid = UUID.randomUUID();
        expectedUpdatedLog.setId(logGuid);
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;

        //mock
        doReturn(expectedUpdatedLog).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatus.class), eq("SYSTEM"), isA(UUID.class));

        //execute
        ExchangeLog actualUpdatedLog = exchangeLogService.updateStatus(logGuid.toString(), status, "SYSTEM");

        //verify and assert
        verify(exchangeLogModel).updateExchangeLogStatus(captorForExchangeLogStatus.capture(), eq("SYSTEM"), isA(UUID.class));

        assertSame(expectedUpdatedLog, actualUpdatedLog);

        ExchangeLogStatus capturedExchangeLogStatus = captorForExchangeLogStatus.getValue();
        assertEquals("SYSTEM", capturedExchangeLogStatus.getUpdatedBy());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, capturedExchangeLogStatus.getStatus());
    }

    @Test
    public void updateStatusByLogGuidWhenFailure() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("noooooooooooooooooooo!!!");

        UUID id = UUID.randomUUID();

        //mock
        doThrow(new RuntimeException("noooooooooooooooooooo!!!")).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatus.class), eq("SYSTEM"), isA(UUID.class));

        exchangeLogService.updateStatus(id.toString(), ExchangeLogStatusTypeType.FAILED, "SYSTEM");
    }

}