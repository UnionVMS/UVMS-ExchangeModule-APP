/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.mapper.LogMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.movement.model.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Stateless
@LocalBean
@Slf4j
public class ExchangeLogRestServiceBean {

    @EJB
    private ExchangeLogModel exchangeLogModel;

    @PersistenceContext(unitName = "exchangePU")
    protected EntityManager em;

    private ExchangeLogDao exchangeLogDao;

    @PostConstruct
    public void init(){
        exchangeLogDao = new ExchangeLogDao(em);
    }

    public ExchangeLogType getExchangeLogByGuid(String guid) throws ExchangeLogException {
        try {
            return exchangeLogModel.getExchangeLogByGuid(guid);
        } catch (ExchangeModelException e) {
            log.error("[ Error when getting exchange log by GUID. {}] {}",guid, e.getMessage());
            throw new ExchangeLogException("Error when getting exchange log by GUID.");
        }
    }

    public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();


        ExchangeListCriteria exchangeSearchCriteria = query.getExchangeSearchCriteria();

        List<ExchangeListCriteriaPair> criterias = exchangeSearchCriteria.getCriterias();
        HashMap<String, Object> paramsMap = new HashMap<>();

        paramsMap.put("TYPEREFTYPE", null);
        paramsMap.put("GUID", null);
        paramsMap.put("TYPEREFGUID", null);
        paramsMap.put("SENDER_RECEIVER", null);
        paramsMap.put("STATUS", null);
        paramsMap.put("SOURCE", null);
        paramsMap.put("RECIPIENT", null);
        paramsMap.put("DF", null);
        paramsMap.put("ON", null);
        paramsMap.put("TODT", null);
        paramsMap.put("AD", null);
        paramsMap.put("DATE_RECEIVED_FROM", DateUtils.START_OF_TIME.toDate());
        paramsMap.put("DATE_RECEIVED_TO", DateUtils.END_OF_TIME.toDate());
        paramsMap.put("INCOMING", false);
        paramsMap.put("OUTGOING", true);

        for (ExchangeListCriteriaPair criteria : criterias) {
            if ("DATE_RECEIVED_FROM".equals(criteria.getKey().value())) {
                paramsMap.put("DATE_RECEIVED_FROM", DateUtil.parseToUTCDate(criteria.getValue()));
            }
            else if ("MESSAGE_DIRECTION".equals(criteria.getKey().value())) {
                if ("OUTGOING".equals(criteria.getValue())){
                    paramsMap.put("OUTGOING", false);
                }
                else if ("INCOMING".equals(criteria.getValue())){
                    paramsMap.put("INCOMING", true);
                }
            }
            else if ("DATE_RECEIVED_TO".equals(criteria.getKey().value())) {
                paramsMap.put("DATE_RECEIVED_TO", DateUtil.parseToUTCDate(criteria.getValue()));
            }
            else if ("SOURCE".equals(criteria.getKey().value())){
                paramsMap.put("SOURCE", criteria.getValue());
            }
            else if ("RECIPIENT".equals(criteria.getKey().value())){
                paramsMap.put("RECIPIENT", criteria.getValue());
            }
            else if ("STATUS".equals(criteria.getKey().value())){
                paramsMap.put("STATUS", criteria.getValue());
            }
            else if ("SENDER_RECEIVER".equals(criteria.getKey().value())){
                paramsMap.put("GUID", criteria.getValue());
                paramsMap.put("TYPEREFGUID", criteria.getValue());
                paramsMap.put("SENDER_RECEIVER", criteria.getValue());
            }
            else if ("TYPE".equals(criteria.getKey().value())){
                paramsMap.put("TYPEREFTYPE", criteria.getValue());
            }
        }
        ExchangeListPagination pagination = query.getPagination();
        int page = pagination.getPage();
        int listSize = pagination.getListSize();
        Long count = exchangeLogDao.count(paramsMap);

        String sortingField = mapSortField(query.getSorting().getSortBy());

        List<ExchangeLog> list = exchangeLogDao.list(paramsMap, sortingField, query.getSorting().isReversed(),(page * listSize) - listSize, listSize -1);
        List<ExchangeLogType> exchangeLogEntityList = new ArrayList<>();

        enrichDtosWithRelatedLogsInfo(exchangeLogEntityList);

        if (isNotEmpty(list)){
            for (ExchangeLog entity : list) {
                exchangeLogEntityList.add(LogMapper.toModel(entity));
            }
        }

        // FIXME UGLY
        enrichDtosWithRelatedLogsInfo(exchangeLogEntityList);

        response.setCurrentPage(page);
        int totalNumberOfPages = (count.intValue() / listSize);
        if (CollectionUtils.isNotEmpty(exchangeLogEntityList)){
            response.setTotalNumberOfPages(totalNumberOfPages + 1);
        }
        else {
            response.setTotalNumberOfPages(totalNumberOfPages);
        }

        response.getExchangeLog().addAll(exchangeLogEntityList);
        return response;
    }

    public LogWithRawMsgAndType getExchangeLogRawMessage(String guid) {
        return exchangeLogModel.getExchangeLogRawXmlByGuid(guid);
    }

    private static String mapSortField(SortField key) {
        String sortFields = "dateReceived";
        if (key != null){
            switch (key) {
                case SOURCE:
                    sortFields = "source";
                    break;
                case TYPE:
                    sortFields = "typeRefType";
                    break;
                case SENDER_RECEIVER:
                    sortFields = "senderReceiver";
                    break;
                case RULE:
                    sortFields = "fwdRule";
                    break;
                case RECEPIENT:
                    sortFields = "recipient";
                    break;
                case STATUS:
                    sortFields = "status";
                    break;
                case DATE_FORWARDED:
                    sortFields = "status";
                    break;
                default:
                    sortFields = "dateReceived";
            }
        }
        return sortFields;
    }

    private void enrichDtosWithRelatedLogsInfo(List< ExchangeLogType > exchangeLogList) {
        List<String> guids = new ArrayList<>();
        for (ExchangeLogType log : exchangeLogList) {
            guids.add(log.getGuid());
        }
        List<ExchangeLog> relatedLogs = getExchangeLogByRangeOfRefGuids(guids);
        if (CollectionUtils.isNotEmpty(relatedLogs)) {
            for (ExchangeLog logEntity : relatedLogs) {
                RelatedLogInfo refLogInfo = new RelatedLogInfo();
                refLogInfo.setGuid(logEntity.getGuid());
                refLogInfo.setType(logEntity.getTypeRefType().toString());
                for (ExchangeLogType logType : exchangeLogList) {
                    if (StringUtils.equals(logEntity.getTypeRefGuid(), logType.getGuid())) {
                        logType.getRelatedLogData().add(refLogInfo);
                    }
                }
            }
        }
    }

    private List<ExchangeLog> getExchangeLogByRangeOfRefGuids(List<String> logGuids) {
        if(CollectionUtils.isEmpty(logGuids)){
            return new ArrayList<>();
        }
        TypedQuery<ExchangeLog> query = em.createNamedQuery(ExchangeLog.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, ExchangeLog.class);
        query.setParameter("refGuids", logGuids);
        return query.getResultList();
    }
}
