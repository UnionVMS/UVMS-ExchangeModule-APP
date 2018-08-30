/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.service;

import eu.europa.ec.fisheries.schema.exchange.source.v1.GetLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.PollQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.BusinessRuleComparator;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ListQueryResponse;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeLogService;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogRestServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("/exchange")
@Stateless
@Slf4j
public class ExchangeLogRestResource {

    @EJB
    private ExchangeLogService serviceLayer;

    @EJB
    private ExchangeLogRestServiceBean logRestServiceBean;

    @Context
    private HttpServletRequest request;

    /**
     * @responseMessage 200 [Success]
     * @responseMessage 500 [Error]
     * @summary Get a list of all exchangeLogs by search criterias
     */
    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getLogListByCriteria(final ExchangeListQuery query) {
        log.info("Get list invoked in rest layer.");
        try {
            //TODO query in swagger
            GetLogListByQueryResponse response = logRestServiceBean.getExchangeLogList(query);
            ListQueryResponse exchangeLogList = ExchangeLogMapper.mapToQueryResponse(response);
            return new ResponseDto(exchangeLogList, RestResponseCode.OK);
        } catch (Exception ex) {
            log.error("[ Error when geting log list. {} ] {} ", query, ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/poll")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getPollStatus(PollQuery query) {
        try {
            log.info("Get ExchangeLog status for Poll in rest layer:{}", query);
            Date from = DateUtils.stringToDate(query.getStatusFromDate());
            Date to = DateUtils.stringToDate(query.getStatusToDate());
            List<ExchangeLogStatusType> response = serviceLayer.getExchangeStatusHistoryList(query.getStatus(), TypeRefType.POLL, from, to);
            return new ResponseDto(response, RestResponseCode.OK);
        } catch (Exception e) {
            log.error("[ Error when getting config search fields. {}] {}", query, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/poll/{typeRefGuid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getPollStatus(@PathParam("typeRefGuid") String typeRefGuid) {
        try {
            log.info("Get ExchangeLog status for Poll by typeRefGuid : {}", typeRefGuid);
            ExchangeLogStatusType response = serviceLayer.getExchangeStatusHistory(TypeRefType.POLL, typeRefGuid, request.getRemoteUser());
            return new ResponseDto(response, RestResponseCode.OK);
        } catch (Exception e) {
            log.error("[ Error when getting config search fields. {} ] {}", typeRefGuid, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/message/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogRawXMLByGuid(@PathParam("guid") String guid) {
        try {
            LogWithRawMsgAndType exchangeLogRawMessageByGuid = logRestServiceBean.getExchangeLogRawMessage(guid);
            return new ResponseDto(exchangeLogRawMessageByGuid.getRawMsg(), RestResponseCode.OK);
        } catch (Exception e) {
            log.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/validation/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogRawXMLAndValidationByGuid(@PathParam("guid") String guid) {
        try {
            ExchangeLogWithValidationResults results = serviceLayer.getExchangeLogRawMessageAndValidationByGuid(guid);
            if (results != null && CollectionUtils.isNotEmpty(results.getValidationList())) {
                Collections.sort(results.getValidationList(), new BusinessRuleComparator());
            }
            return new ResponseDto(results, RestResponseCode.OK);
        } catch (Exception e) {
            log.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogByUUID(@PathParam("guid") String guid) {
        try {
            return new ResponseDto(logRestServiceBean.getExchangeLogByGuid(guid), RestResponseCode.OK);
        } catch (Exception e) {
            log.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

}