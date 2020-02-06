package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLogStatus;
import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.PollQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ExchangeLogRestResourceTest extends BuildExchangeRestTestDeployment {

    @Inject
    ExchangeLogDaoBean exchangeLogDao;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void worldsBestAndMostUsefullArquillianTest(){
        assertTrue(true);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListByCriteriaTest() throws Exception {
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = new ExchangeListQuery();
        ExchangeListPagination exchangeListPagination = new ExchangeListPagination();
        exchangeListPagination.setListSize(10);
        exchangeListPagination.setPage(1);
        query.setPagination(exchangeListPagination);
        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        ExchangeListCriteriaPair exchangeListCriteriaPair = new ExchangeListCriteriaPair();
        exchangeListCriteriaPair.setKey(SearchField.TYPE);
        exchangeListCriteriaPair.setValue(TypeRefType.UNKNOWN.value());
        exchangeListCriteria.getCriterias().add(exchangeListCriteriaPair);
        query.setExchangeSearchCriteria(exchangeListCriteria);


        String stringResponse = getWebTarget()
                .path("exchange")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), String.class);

        assertNotNull(stringResponse);
        ListQueryResponse response = RestHelper.readResponseDto(stringResponse, ListQueryResponse.class);
        assertFalse(response.getLogList().isEmpty());
        assertEquals(exchangeLog.getId().toString(), response.getLogList().get(0).getId());
        assertEquals(DateUtils.parseInstantToString(exchangeLog.getDateReceived()), response.getLogList().get(0).getDateRecieved());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusTest() throws Exception {
        Instant now = Instant.now();
        PollQuery query = new PollQuery();
        query.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        query.setStatusFromDate(DateUtils.parseInstantToString(now));
        query.setStatusToDate(DateUtils.parseInstantToString(now.plusSeconds(5)));

        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        String stringResponse = getWebTarget()
                .path("exchange")
                .path("poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), String.class);

        assertNotNull(stringResponse);
        List<ExchangeLogStatusType> response = RestHelper.readResponseDtoList(stringResponse, ExchangeLogStatusType.class);
        assertFalse(response.isEmpty());
        ExchangeLogStatusType output = response.get(0);
        assertEquals(exchangeLog.getId().toString(), output.getGuid());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusByRefIdTest() throws Exception {
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        String stringResponse = getWebTarget()
                .path("exchange/poll")
                .path(exchangeLog.getTypeRefGuid().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        assertNotNull(stringResponse);
        ExchangeLogStatusType response = RestHelper.readResponseDto(stringResponse, ExchangeLogStatusType.class);
        assertEquals(exchangeLog.getId().toString(), response.getGuid());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getExchangeLogRawXMLByGuidTest() throws Exception {
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        String stringResponse = getWebTarget()
                .path("exchange/message")
                .path(exchangeLog.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        assertNotNull(stringResponse);
        assertTrue(stringResponse, stringResponse.contains(exchangeLog.getTypeRefMessage()));
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getExchangeLogRawXMLAndValidationByGuidTest() throws Exception {
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        exchangeLog.setTypeRefType(TypeRefType.FA_RESPONSE);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        String stringResponse = getWebTarget()
                .path("exchange/validation")
                .path(exchangeLog.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        assertNotNull(stringResponse);
        ExchangeLogWithValidationResults response = RestHelper.readResponseDto(stringResponse, ExchangeLogWithValidationResults.class);
        assertEquals(exchangeLog.getTypeRefMessage(), response.getMsg());
        assertEquals("Rules Mock Expression", response.getValidationList().get(0).getExpression());          //values from RulesModuleMock
        assertEquals("Rules Mock Message", response.getValidationList().get(0).getMessage());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getExchangeLogByUUIDTest() throws Exception {
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        exchangeLog.setTypeRefType(TypeRefType.FA_RESPONSE);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        String stringResponse = getWebTarget()
                .path("exchange/")
                .path(exchangeLog.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .get(String.class);

        assertNotNull(stringResponse);
        ExchangeLogType response = RestHelper.readResponseDto(stringResponse, ExchangeLogType.class);
        assertEquals(exchangeLog.getId().toString(), response.getGuid());
        assertEquals(exchangeLog.getSenderReceiver(), response.getSenderReceiver());
        assertEquals(exchangeLog.getSource(), response.getSource());
    }




    private ExchangeLog createBasicLog(){
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(LogType.PROCESSED_MOVEMENT);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.UNKNOWN);
        exchangeLog.setUpdatedBy("Tester");
        exchangeLog.setUpdateTime(Instant.now());
        exchangeLog.setDateReceived(Instant.now());
        exchangeLog.setSenderReceiver("Test sender/receiver");
        exchangeLog.setTransferIncoming(false);
        exchangeLog.setStatusHistory(new ArrayList<ExchangeLogStatus>());

        return exchangeLog;
    }

    private void addLogStatusToLog(ExchangeLog exchangeLog, ExchangeLogStatusTypeType statusType){
        ExchangeLogStatus status = new ExchangeLogStatus();
        status.setLog(exchangeLog);
        status.setStatus(statusType);
        status.setStatusTimestamp(Instant.now());
        status.setUpdatedBy("Status updater");
        status.setUpdateTime(Instant.now());

        exchangeLog.getStatusHistory().add(status);
    }
}
