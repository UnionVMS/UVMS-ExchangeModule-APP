/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.ExchangeLogModel;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.mapper.LogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeLogException;
import eu.europa.ec.fisheries.uvms.movement.model.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            throw new ExchangeLogException("Error when getting exchange log by GUID." + guid,e);
        }
    }

    public String getExchangeLogRawMessage(String guid) {
        String rawXml = null;
        ExchangeLog exchangeLogByGuid = exchangeLogDao.getExchangeLogByGuid(guid);
        if (exchangeLogByGuid != null){
            rawXml = exchangeLogByGuid.getTypeRefMessage();
        }
        return rawXml;
    }

    public GetLogListByQueryResponse getExchangeLogList(ExchangeListQuery query) {
        GetLogListByQueryResponse response = new GetLogListByQueryResponse();
        ExchangeListCriteria exchangeSearchCriteria = query.getExchangeSearchCriteria();
        List<ExchangeListCriteriaPair> criteria = exchangeSearchCriteria.getCriterias();
        HashMap<String, Object> paramsMap = initParamsMap(criteria);
        ExchangeListPagination pagination = query.getPagination();
        int page = pagination.getPage();
        int listSize = pagination.getListSize();
        Long count = exchangeLogDao.count(paramsMap);
        PageInfo pageInfo = calculatePagingInfo(count.intValue(),page,listSize);
        String sortingField = mapSortField(query);
        boolean isReversed = mapReversedField(query);
        List<ExchangeLog> list = exchangeLogDao.list(paramsMap, sortingField, isReversed,pageInfo.getQueryStartIndex(), listSize);
        List<ExchangeLogType> exchangeLogEntityList = new ArrayList<>();
        if (isNotEmpty(list)){
            for (ExchangeLog entity : list) {
                exchangeLogEntityList.add(LogMapper.toModel(entity));
            }
        }
        enrichDtosWithRelatedLogsInfo(exchangeLogEntityList);
        response.setCurrentPage(pageInfo.getCurrentPage());
        response.setTotalNumberOfPages(pageInfo.getTotalPages());
        response.getExchangeLog().addAll(exchangeLogEntityList);
        return response;
    }

    private PageInfo calculatePagingInfo(int total, int pageNr, int pageSize) {
        int startIdx = (pageNr * pageSize) - pageSize;

        int totalPages = ((total % pageSize) > 0) ? (total/pageSize +1) : total/pageSize;
        if (pageNr > totalPages) {
            startIdx=0;
            pageNr=1;
        }
        return new PageInfo(totalPages,startIdx,pageNr);
    }

    private void enrichDtosWithRelatedLogsInfo(List<ExchangeLogType> exchangeLogList) {
        List<String> guids = new ArrayList<>();
        for (ExchangeLogType log : exchangeLogList) {
            guids.add(log.getGuid());
        }
        List<ExchangeLog> relatedLogs = exchangeLogDao.getExchangeLogByRangeOfRefGuids(guids);
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

    private boolean mapReversedField(ExchangeListQuery query) {
        boolean isReversed = false;
        Sorting sorting = query.getSorting();
        if (sorting != null){
            isReversed = sorting.isReversed();
        }
        return isReversed;
    }

    private HashMap<String, Object> initParamsMap(List<ExchangeListCriteriaPair> criteria) {
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
        paramsMap.put("DATE_RECEIVED_TO", DateUtils.END_OF_TIME.toDate());
        paramsMap.put("DATE_RECEIVED_FROM", DateUtils.START_OF_TIME.toDate());
        paramsMap.put("INCOMING", false);
        paramsMap.put("OUTGOING", true);

        if (CollectionUtils.isNotEmpty(criteria)){
            for (ExchangeListCriteriaPair criterion : criteria) {
                if ("DATE_RECEIVED_FROM".equals(criterion.getKey().value())) {
                    paramsMap.put("DATE_RECEIVED_FROM", Date.from(DateUtil.parseToUTCDate(criterion.getValue())));
                }
                else if ("MESSAGE_DIRECTION".equals(criterion.getKey().value())) {
                    if ("OUTGOING".equals(criterion.getValue())){
                        paramsMap.put("OUTGOING", false);
                    }
                    else if ("INCOMING".equals(criterion.getValue())){
                        paramsMap.put("INCOMING", true);
                    }
                }
                else if ("DATE_RECEIVED_TO".equals(criterion.getKey().value())) {
                    paramsMap.put("DATE_RECEIVED_TO", Date.from(DateUtil.parseToUTCDate(criterion.getValue())));
                }
                else if ("SOURCE".equals(criterion.getKey().value())){
                    paramsMap.put("SOURCE", criterion.getValue());
                }
                else if ("RECIPIENT".equals(criterion.getKey().value())){
                    paramsMap.put("RECIPIENT", criterion.getValue());
                }
                else if ("STATUS".equals(criterion.getKey().value())){
                    paramsMap.put("STATUS", criterion.getValue());
                }
                else if ("SENDER_RECEIVER".equals(criterion.getKey().value())){
                    paramsMap.put("GUID", criterion.getValue());
                    paramsMap.put("ON", criterion.getValue());
                    paramsMap.put("TYPEREFGUID", criterion.getValue());
                    paramsMap.put("SENDER_RECEIVER", criterion.getValue());
                }
                else if ("TYPE".equals(criterion.getKey().value())){
                    LogTypeGroupEnum logType =  LogTypeGroupEnum.valueOf(criterion.getValue());
                    List<String> subTypes = logType.getSubTypesAsList();
                    paramsMap.put("TYPEREFTYPE", subTypes);
                }
            }
        }
        return paramsMap;
    }

    private static String mapSortField(ExchangeListQuery query) {
        String sortFields = "dateReceived";
        Sorting sorting = query.getSorting();
        if (sorting != null && sorting.getSortBy() != null){
            SortField key = sorting.getSortBy();
            if (key == null){
                return sortFields;
            }
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

    @Transactional
    public void remove(String messageUuid) {
        ExchangeLog exchangeLogByGuid = exchangeLogDao.getExchangeLogByGuid(messageUuid);
        if (exchangeLogByGuid == null){
            List<ExchangeLog> exchangeLogByRangeOfRef = exchangeLogDao.getExchangeLogByRangeOfRefGuids(Collections.singletonList(messageUuid));
            if (CollectionUtils.isNotEmpty(exchangeLogByRangeOfRef)){
                for (ExchangeLog exchangeLog : exchangeLogByRangeOfRef) {
                    exchangeLogDao.getEntityManager().remove(exchangeLog);
                }
            }
        } else {
            exchangeLogDao.getEntityManager().remove(exchangeLogByGuid);
        }
    }

    /**
     * Group relative types for search purposes
     */
    enum LogTypeGroupEnum {
        FA(TypeRefType.FA, TypeRefType.FA_QUERY, TypeRefType.FA_REPORT, TypeRefType.FA_RESPONSE) ,
        FA_QUERY(TypeRefType.FA_QUERY) ,
        FA_REPORT(TypeRefType.FA_REPORT) ,
        FA_RESPONSE(TypeRefType.FA_RESPONSE) ,
        MOVEMENT(TypeRefType.MOVEMENT, TypeRefType.MOVEMENT_REPORT, TypeRefType.MOVEMENT_RESPONSE),
        MOVEMENT_REPORT(TypeRefType.MOVEMENT, TypeRefType.MOVEMENT_REPORT),
        MOVEMENT_RESPONSE(TypeRefType.MOVEMENT_RESPONSE),
        ASSET(TypeRefType.ASSETS);

        private TypeRefType[] subTypes;

        LogTypeGroupEnum(TypeRefType... subTypes) {
            this.subTypes = subTypes;
        }

        List<String> getSubTypesAsList() {
            return Stream.of(this.subTypes)
                    .map(TypeRefType::value)
                    .collect(Collectors.toList());
        }
    }

    static class PageInfo {
        private int totalPages;
        private int queryStartIndex;
        private int currentPage;

        public PageInfo(int totalPages, int queryStartIndex, int currentPage) {
            this.totalPages = totalPages;
            this.queryStartIndex = queryStartIndex;
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getQueryStartIndex() {
            return queryStartIndex;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    }
}
