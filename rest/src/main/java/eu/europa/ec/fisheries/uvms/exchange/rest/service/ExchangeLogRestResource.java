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
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeListQuery;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogWithValidationResults;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.PollQuery;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.BusinessRuleComparator;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogModelBean;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogServiceBean;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Stateless
@Path("/exchange")
@RequiresFeature(UnionVMSFeature.viewExchange)
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
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

    @POST
    @Path("/list")
    public Response getLogListByCriteria(final ExchangeListQuery query) {
        LOG.info("Get list invoked in rest layer.");
        try {
            //TODO query in swagger
            GetLogListByQueryResponse response = new GetLogListByQueryResponse();
            ListResponseDto exchangeLogList = exchangeLogModel.getExchangeLogListByQuery(query);
            response.setCurrentPage(exchangeLogList.getCurrentPage());
            response.setTotalNumberOfPages(exchangeLogList.getTotalNumberOfPages());
            response.getExchangeLog().addAll(exchangeLogList.getExchangeLogList());

            return Response.ok(ExchangeLogMapper.mapToQueryResponse(response)).build();
        } catch (Exception ex) {
            LOG.error("[ Error when geting log list. {} ] {} ", query, ex.getMessage());
            throw ex;
        }
    }

    @POST
    @Path(value = "/poll")
    public Response getPollStatus(PollQuery query) {
        try {
            LOG.info("Get ExchangeLog status for Poll in rest layer:{}", query);
            Instant from = DateUtils.stringToDate(query.getStatusFromDate());
            Instant to = DateUtils.stringToDate(query.getStatusToDate());
            List<ExchangeLogStatusType> response = serviceLayer.getExchangeStatusHistoryList(query.getStatus(), TypeRefType.POLL, from, to);
            return Response.ok(response).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. {}] {}", query, e.getMessage());
            throw e;
        }
    }

    @GET
    @Path(value = "/poll/{typeRefGuid}")
    public Response getPollStatus(@PathParam("typeRefGuid") String typeRefGuid) {
        try {
            LOG.info("Get ExchangeLog status for Poll by typeRefGuid : {}", typeRefGuid);
            if (typeRefGuid == null) {
                throw new IllegalArgumentException("Invalid id");
            }
            ExchangeLogStatusType response = exchangeLogModel.getExchangeLogStatusHistory(UUID.fromString(typeRefGuid), TypeRefType.POLL);
            return Response.ok(response).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. {} ] {}", typeRefGuid, e.getMessage());
            throw e;
        }
    }

    @GET
    @Path("/message/{guid}")
    public Response getExchangeLogRawXMLByGuid(@PathParam("guid") String guid) {
        try {

            ExchangeLog exchangeLog = logDao.getExchangeLogByGuid(UUID.fromString(guid));
            return Response.ok(exchangeLog.getTypeRefMessage()).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            throw e;
        }
    }

    @GET
    @Path("/validation/{guid}")
    public Response getExchangeLogRawXMLAndValidationByGuid(@PathParam("guid") String guid) {
        try {
            ExchangeLogWithValidationResults results = serviceLayer.getExchangeLogRawMessageAndValidationByGuid(UUID.fromString(guid));
            if (results != null && CollectionUtils.isNotEmpty(results.getValidationList())) {
                results.getValidationList().sort(new BusinessRuleComparator());
            }
            return Response.ok(results).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            throw e;
        }
    }

    @GET
    @Path("/{guid}")
    public Response getExchangeLogByUUID(@PathParam("guid") String guid) {
        try {
            return Response.ok(exchangeLogModel.getExchangeLogByGuid(UUID.fromString(guid))).build();
        } catch (Exception e) {
            LOG.error("[ Error when getting exchange log by GUID. ] {}", e.getMessage());
            throw e;
        }
    }

}
