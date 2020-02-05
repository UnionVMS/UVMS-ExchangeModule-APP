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

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.dao.bean.UnsentMessageDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.mapper.UnsentMessageMapper;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.SendingGroupLog;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ExchangeLogMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeLogServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sendingqueue")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class ExchangeSendingQueueRestResource {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeSendingQueueRestResource.class);

    @EJB
    ExchangeLogServiceBean serviceLayer;

    @Inject
    private UnsentMessageDaoBean unsentMessageDao;

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto<?> getSendingQueue() {
        LOG.info("Get list invoked in rest layer");
        try {
            List<UnsentMessage> unsentMessageList = unsentMessageDao.getAll();
            List<UnsentMessageType> unsentMessageTypeList = UnsentMessageMapper.toModel(unsentMessageList);
            List<SendingGroupLog> sendingQueue = ExchangeLogMapper.mapToSendingQueue(unsentMessageTypeList);
            return new ResponseDto<>(sendingQueue, RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting log list. ] {} ", ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }

    @PUT
    @Path("/send")
    @RequiresFeature(UnionVMSFeature.manageExchangeSendingQueue)
    public ResponseDto<?> send(final List<String> messageIdList) {
        LOG.info("Get list invoked in rest layer:{}", messageIdList);
        try {
            //TODO swaggerize messageIdList
            serviceLayer.resend(messageIdList, request.getRemoteUser());
            return new ResponseDto<>(true, RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting log list. {} ] {} ", messageIdList, ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }
}
