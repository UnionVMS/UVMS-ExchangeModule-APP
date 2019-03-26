package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
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
    private ExchangeEventLogCache logCache;

    @Mock
    private Event<NotificationMessage> exchangeLogEvent; //yes this is used

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void updateStatusByLogGuidWhenSuccess() throws Exception {
        //data set
        ArgumentCaptor<ExchangeLogStatusType> captorForExchangeLogStatusType = ArgumentCaptor.forClass(ExchangeLogStatusType.class);
        ExchangeLogType expectedUpdatedLog = new ExchangeLogType();
        UUID logGuid = UUID.randomUUID();
        expectedUpdatedLog.setGuid(logGuid.toString());
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;

        //mock
        doReturn(logGuid).when(logCache).acknowledged(anyString());
        doReturn(expectedUpdatedLog).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatusType.class), eq("SYSTEM"));

        //execute
        ExchangeLogType actualUpdatedLog = exchangeLogService.updateStatus(logGuid.toString(), status, "SYSTEM");

        //verify and assert
        verify(exchangeLogModel).updateExchangeLogStatus(captorForExchangeLogStatusType.capture(), eq("SYSTEM"));

        assertSame(expectedUpdatedLog, actualUpdatedLog);

        ExchangeLogStatusType capturedExchangeLogStatusType = captorForExchangeLogStatusType.getValue();
        assertEquals(logGuid.toString(), capturedExchangeLogStatusType.getGuid());
        assertEquals(1, capturedExchangeLogStatusType.getHistory().size());
        assertEquals(status, capturedExchangeLogStatusType.getHistory().get(0).getStatus());
    }

    @Test
    public void updateStatusByLogGuidWhenFailure() throws Exception {
        expectedException.expect(ExchangeLogException.class);
        expectedException.expectMessage("Couldn't update status of exchange log");

        UUID id = UUID.randomUUID();

        //mock
        doReturn(id).when(logCache).acknowledged(anyString());
        doThrow(new ExchangeModelException("noooooooooooooooooooo!!!")).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatusType.class), eq("SYSTEM"));

        exchangeLogService.updateStatus(id.toString(), ExchangeLogStatusTypeType.FAILED, "SYSTEM");
    }

}