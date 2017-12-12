package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.remote.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeLogServiceBeanTest {

    @InjectMocks
    private ExchangeLogServiceBean exchangeLogService;

    @Mock
    private ExchangeLogModel exchangeLogModel;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void updateStatusByLogGuidWhenSuccess() throws Exception {
        //data set
        ArgumentCaptor<ExchangeLogStatusType> captorForExchangeLogStatusType = ArgumentCaptor.forClass(ExchangeLogStatusType.class);
        ExchangeLogType expectedUpdatedLog = new ExchangeLogType();
        String logGuid = "123456";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;

        //mock
        doReturn(expectedUpdatedLog).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatusType.class), eq("SYSTEM"));

        //execute
        ExchangeLogType actualUpdatedLog = exchangeLogService.updateStatus(logGuid, status);

        //verify and assert
        verify(exchangeLogModel).updateExchangeLogStatus(captorForExchangeLogStatusType.capture(), eq("SYSTEM"));

        assertSame(expectedUpdatedLog, actualUpdatedLog);

        ExchangeLogStatusType capturedExchangeLogStatusType = captorForExchangeLogStatusType.getValue();
        assertEquals(logGuid, capturedExchangeLogStatusType.getGuid());
        assertEquals(1, capturedExchangeLogStatusType.getHistory().size());
        assertEquals(status, capturedExchangeLogStatusType.getHistory().get(0).getStatus());
    }

    @Test
    public void updateStatusByLogGuidWhenFailure() throws Exception {
        expectedException.expect(ExchangeLogException.class);
        expectedException.expectMessage("Couldn't update the status of the exchange log with guid 12345. The new status should be FAILED");

        doThrow(new ExchangeModelException("noooooooooooooooooooo!!!")).when(exchangeLogModel).updateExchangeLogStatus(isA(ExchangeLogStatusType.class), eq("SYSTEM"));

        exchangeLogService.updateStatus("12345", ExchangeLogStatusTypeType.FAILED);
    }

}