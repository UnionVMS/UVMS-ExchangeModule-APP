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
import eu.europa.ec.fisheries.uvms.exchange.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.PollQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.BusinessRuleComparator;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Path("/exchange")
@Stateless
public class ExchangeLogRestResource {

    private final static Logger LOG = LoggerFactory.getLogger(ExchangeLogRestResource.class);

    @EJB
    private ExchangeLogServiceBean serviceLayer;

    @Context
    private HttpServletRequest request;

    @Inject
    private ExchangeLogModelBean exchangeLogModel;

    @Inject
    private ExchangeLogDaoBean logDao;

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
        LOG.info("Get list invoked in rest layer.");
        try {
            //TODO query in swagger
            GetLogListByQueryResponse response = new GetLogListByQueryResponse();
            ListResponseDto exchangeLogList = exchangeLogModel.getExchangeLogListByQuery(query);
            response.setCurrentPage(exchangeLogList.getCurrentPage());
            response.setTotalNumberOfPages(exchangeLogList.getTotalNumberOfPages());
            response.getExchangeLog().addAll(exchangeLogList.getExchangeLogList());

            return new ResponseDto(ExchangeLogMapper.mapToQueryResponse(response), RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when geting log list. {} ] {} ", query, ex.getMessage());
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
            LOG.info("Get ExchangeLog status for Poll in rest layer:{}", query);
            Instant from = DateUtils.stringToDate(query.getStatusFromDate());
            Instant to = DateUtils.stringToDate(query.getStatusToDate());
            List<ExchangeLogStatusType> response = serviceLayer.getExchangeStatusHistoryList(query.getStatus(), TypeRefType.POLL, from, to);
            return new ResponseDto(response, RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. {}] {}", query, e.getMessage());
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
            LOG.info("Get ExchangeLog status for Poll by typeRefGuid : {}", typeRefGuid);
            if (typeRefGuid == null) {
                throw new IllegalArgumentException("Invalid id");
            }
            ExchangeLogStatusType response = exchangeLogModel.getExchangeLogStatusHistory(UUID.fromString(typeRefGuid), TypeRefType.POLL);
            return new ResponseDto(response, RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. {} ] {}", typeRefGuid, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/message/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogRawXMLByGuid(@PathParam("guid") String guid) {
        try {

            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(UUID.fromString(guid));
            return new ResponseDto(exchangeLog.getTypeRefMessage(), RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/validation/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogRawXMLAndValidationByGuid(@PathParam("guid") String guid) {
        try {
            ExchangeLogWithValidationResults results = serviceLayer.getExchangeLogRawMessageAndValidationByGuid(UUID.fromString(guid));
            if (results != null && CollectionUtils.isNotEmpty(results.getValidationList())) {
                Collections.sort(results.getValidationList(), new BusinessRuleComparator());
            }
            return new ResponseDto(results, RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/{guid}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto getExchangeLogByUUID(@PathParam("guid") String guid) {
        try {
            return new ResponseDto(exchangeLogModel.getExchangeLogByGuid(UUID.fromString(guid)), RestResponseCode.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

}