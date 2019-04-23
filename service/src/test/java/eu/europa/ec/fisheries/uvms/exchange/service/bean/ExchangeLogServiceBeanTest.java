package eu.europa.ec.fisheries.uvms.exchange.service.bean;

//@RunWith(MockitoJUnitRunner.class)
// TODO :  AuditModelMarshallException is compiled with java 8!!!!!! FIX this so that this tests can work!!!
public class ExchangeLogServiceBeanTest {

 /*   @InjectMocks
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
        ExchangeLogType actualUpdatedLog = exchangeLogService.updateStatus(logGuid, status, false);

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

        exchangeLogService.updateStatus("12345", ExchangeLogStatusTypeType.FAILED, false);
    }*/

}