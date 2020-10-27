package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.TestExchangeLogWithValidationResults;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.PollQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.TestExchangeLogStatusType;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
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
    public void getPollStatusTest() {
        Instant now = Instant.now();
        PollQuery query = new PollQuery();
        query.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        query.setStatusFromDate(DateUtils.dateToEpochMilliseconds(now));
        query.setStatusToDate(DateUtils.dateToEpochMilliseconds(now.plusSeconds(5)));

        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog = exchangeLogDao.createLog(exchangeLog);

        List<TestExchangeLogStatusType> responseDto = getWebTarget()
                .path("exchange")
                .path("poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), new GenericType<List<TestExchangeLogStatusType>>() {});

        assertNotNull(responseDto);
        assertFalse(responseDto.isEmpty());
        assertEquals(1, responseDto.size());
        assertTrue(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog.getId().toString())));
        assertTrue(responseDto.stream().anyMatch(logStatusType -> createdLog.getTypeRefMessage().equals(logStatusType.getRefMessage())));
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusTwoLogsOnlyGetOne() {
        Instant now = Instant.now();
        PollQuery query = new PollQuery();
        query.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        query.setStatusFromDate(DateUtils.dateToEpochMilliseconds(now));
        query.setStatusToDate(DateUtils.dateToEpochMilliseconds(now.plusSeconds(5)));

        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS);
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        List<TestExchangeLogStatusType> responseDto = getWebTarget()
                .path("exchange")
                .path("poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), new GenericType<List<TestExchangeLogStatusType>>() {});

        assertNotNull(responseDto);
        assertFalse(responseDto.isEmpty());
        assertEquals(1, responseDto.size());
        assertTrue(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog1.getId().toString())));
        assertTrue(responseDto.stream().anyMatch(logStatusType -> createdLog1.getTypeRefMessage().equals(logStatusType.getRefMessage())));

        assertFalse(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog2.getId().toString())));
        assertFalse(responseDto.stream().anyMatch(logStatusType -> createdLog2.getTypeRefMessage().equals(logStatusType.getRefMessage())));
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusTwoLogsOnlyOneIsPoll() {
        Instant now = Instant.now();
        PollQuery query = new PollQuery();
        query.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        query.setStatusFromDate(DateUtils.dateToEpochMilliseconds(now));
        query.setStatusToDate(DateUtils.dateToEpochMilliseconds(now.plusSeconds(5)));

        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.SALES_RESPONSE);
        exchangeLog.setType(LogType.RECEIVE_SALES_RESPONSE);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        List<TestExchangeLogStatusType> responseDto = getWebTarget()
                .path("exchange")
                .path("poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), new GenericType<List<TestExchangeLogStatusType>>() {});

        assertNotNull(responseDto);
        assertFalse(responseDto.isEmpty());
        assertEquals(1, responseDto.size());
        assertTrue(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog1.getId().toString())));
        assertTrue(responseDto.stream().anyMatch(logStatusType -> createdLog1.getTypeRefMessage().equals(logStatusType.getRefMessage())));

        assertFalse(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog2.getId().toString())));
        assertFalse(responseDto.stream().anyMatch(logStatusType -> createdLog2.getTypeRefMessage().equals(logStatusType.getRefMessage())));
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusTwoLogsOnlyGetOneOneLogIsToEarly() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        Instant now = Instant.now();
        PollQuery query = new PollQuery();
        query.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        query.setStatusFromDate(DateUtils.dateToEpochMilliseconds(now));
        query.setStatusToDate(DateUtils.dateToEpochMilliseconds(now.plusSeconds(5)));

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        List<TestExchangeLogStatusType> responseDto = getWebTarget()
                .path("exchange")
                .path("poll")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), new GenericType<List<TestExchangeLogStatusType>>() {});

        assertNotNull(responseDto);
        assertFalse(responseDto.isEmpty());
        assertEquals(1, responseDto.size());
        assertFalse(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog1.getId().toString())));
        assertFalse(responseDto.stream().anyMatch(logStatusType -> createdLog1.getTypeRefMessage().equals(logStatusType.getRefMessage())));

        assertTrue(responseDto.stream().anyMatch(logStatusType -> logStatusType.getGuid().equals(createdLog2.getId().toString())));
        assertTrue(responseDto.stream().anyMatch(logStatusType -> createdLog2.getTypeRefMessage().equals(logStatusType.getRefMessage())));
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getPollStatusByRefIdTest() throws Exception {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
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
    public void getExchangeLogRawXMLByGuidTest() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
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
    public void getExchangeLogByUUIDTest() throws Exception {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog.setTypeRefMessage(UUID.randomUUID().toString());
        exchangeLog.setTypeRefType(TypeRefType.FA_RESPONSE);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        RestHelper.addLogStatusToLog(exchangeLog,ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
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



}