package eu.europa.ec.fisheries.uvms.exchange.rest.RestTests;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.BuildExchangeRestTestDeployment;
import eu.europa.ec.fisheries.uvms.exchange.rest.RestHelper;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ExchangeLogRestResourceQueryTest extends BuildExchangeRestTestDeployment {

    @Inject
    ExchangeLogDaoBean exchangeLogDao;

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListByCriteriaTest() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.UNKNOWN.value()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertEquals(exchangeLog.getId().toString(), response.getLogList().get(0).getId());
        assertEquals(DateUtils.dateToEpochMilliseconds(exchangeLog.getDateReceived()), response.getLogList().get(0).getDateRecieved());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListTwoTypeCriteriaTest() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(false);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.UNKNOWN.value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.FA_QUERY.value()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertEquals(exchangeLog.getId().toString(), response.getLogList().get(0).getId());
        assertEquals(DateUtils.dateToEpochMilliseconds(exchangeLog.getDateReceived()), response.getLogList().get(0).getDateRecieved());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListTwoTypeCriteriaTwoLogs() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.FA_QUERY);
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.UNKNOWN.value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.FA_QUERY.value()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog1.getId().toString())));
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog2.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithTypeAndRecipient() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, TypeRefType.UNKNOWN.value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.RECIPIENT, createdLog.getRecipient()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithSenderReceiverAndStatus() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.SENDER_RECEIVER, createdLog.getSenderReceiver()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.STATUS, createdLog.getStatus().value()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithDateReceivedFromAndSource() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.DATE_RECEIVED_FROM, "" + createdLog.getDateReceived().toEpochMilli()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.SOURCE, createdLog.getSource()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithDateReceivedFromAndSourceTwoLogsGetOne() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.ALARM);
        exchangeLog.setSource("Another test source");
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.DATE_RECEIVED_FROM, "" + createdLog1.getDateReceived().toEpochMilli()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.SOURCE, createdLog1.getSource()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog1.getId().toString())));
        assertFalse(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog2.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithTypeOrSourceTwoLogsGetBoth() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.ALARM);
        exchangeLog.setSource("Another test source");
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(false);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, createdLog1.getTypeRefType().value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.SOURCE, createdLog2.getSource()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog1.getId().toString())));
        assertTrue(response.getLogList().stream().anyMatch(log -> log.getId().equals(createdLog2.getId().toString())));

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithDateAndSourceSortingSenderReceiver() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setSenderReceiver("C");
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setSenderReceiver("B");
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setSenderReceiver("A");
        ExchangeLog createdLog3 = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, createdLog1.getTypeRefType().value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.DATE_RECEIVED_FROM, "" + createdLog1.getDateReceived().toEpochMilli()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        Sorting sorting = new Sorting();
        sorting.setSortBy(SortField.SENDER_RECEIVER);
        query.setSorting(sorting);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertEquals(createdLog3.getId().toString(), response.getLogList().get(0).getId());
        assertEquals(createdLog2.getId().toString(), response.getLogList().get(1).getId());
        assertEquals(createdLog1.getId().toString(), response.getLogList().get(2).getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void getLogListWithDateAndSourceSortingRule() {
        ExchangeLog exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setFwdRule("B");
        ExchangeLog createdLog1 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setFwdRule("A");
        ExchangeLog createdLog2 = exchangeLogDao.createLog(exchangeLog);

        exchangeLog = RestHelper.createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog.setFwdRule("C");
        ExchangeLog createdLog3 = exchangeLogDao.createLog(exchangeLog);

        ExchangeListQuery query = getBasicQuery();

        ExchangeListCriteria exchangeListCriteria = new ExchangeListCriteria();
        exchangeListCriteria.setIsDynamic(true);
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.TYPE, createdLog1.getTypeRefType().value()));
        exchangeListCriteria.getCriterias().add(getCriteriaPair(SearchField.DATE_RECEIVED_FROM, "" + createdLog1.getDateReceived().toEpochMilli()));
        query.setExchangeSearchCriteria(exchangeListCriteria);

        Sorting sorting = new Sorting();
        sorting.setSortBy(SortField.RULE);
        query.setSorting(sorting);

        ListQueryResponse response = sendListQuery(query);

        assertFalse(response.getLogList().isEmpty());
        assertEquals(createdLog2.getId().toString(), response.getLogList().get(0).getId());
        assertEquals(createdLog1.getId().toString(), response.getLogList().get(1).getId());
        assertEquals(createdLog3.getId().toString(), response.getLogList().get(2).getId());

    }

    private ListQueryResponse sendListQuery(ExchangeListQuery query){
        Response response = getWebTarget()
                .path("exchange")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(query), Response.class);
        assertEquals(200, response.getStatus());

        return response.readEntity(ListQueryResponse.class);
    }

    private ExchangeListQuery getBasicQuery(){
        ExchangeListQuery query = new ExchangeListQuery();
        query.setPagination(getBasicPagination());

        return  query;
    }

    private ExchangeListPagination getBasicPagination(){
        ExchangeListPagination exchangeListPagination = new ExchangeListPagination();
        exchangeListPagination.setListSize(10);
        exchangeListPagination.setPage(1);

        return exchangeListPagination;
    }

    private ExchangeListCriteriaPair getCriteriaPair(SearchField key, String value){
        ExchangeListCriteriaPair exchangeListCriteriaPair = new ExchangeListCriteriaPair();
        exchangeListCriteriaPair.setKey(key);
        exchangeListCriteriaPair.setValue(value);

        return  exchangeListCriteriaPair;
    }
}
