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

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.bean.ServiceRegistryModelBean;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.PluginServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Stateless
@Path("/plugin")
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class ExchangeRegistryRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRegistryRestResource.class);

    @Inject
    private ServiceRegistryModelBean serviceRegistryModel;

    @EJB
    PluginServiceBean pluginService;

    @GET
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public ResponseDto<?> getList() {
        LOG.info("Get list invoked in rest layer");
        try {
            return new ResponseDto<>(ServiceMapper.map(serviceRegistryModel.getPlugins(null)), RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when geting list. ] {} ", ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/capability/{capabilityType}")
    @RequiresFeature(UnionVMSFeature.viewExchange)
    public Response getPluginsByCapability(@PathParam(value = "capabilityType") String capabilityType) {
        LOG.info("Get list invoked in rest layer");
        try {
            CapabilityTypeType capability = CapabilityTypeType.fromValue(capabilityType.toUpperCase());
            List<Service> plugins = serviceRegistryModel.getPluginsByCapability(capability);
            return Response.ok(ServiceMapper.map(plugins)).build();
        } catch (Exception e) {
            LOG.error("Error when getting plugins by capability", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @PUT
    @Path("/start/{serviceClassName}")
    @RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
    public ResponseDto<?> startService(@PathParam(value = "serviceClassName") String serviceClassName) { // Why is this a put? And this returns true or an exception?
        LOG.info("Start service invoked in rest layer:{}", serviceClassName);
        try {
            return new ResponseDto<>(pluginService.start(serviceClassName), RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when starting service {}] {}", serviceClassName, ex);
            return ErrorHandler.getFault(ex);
        }
    }

    @PUT
    @Path("/stop/{serviceClassName}")
    @RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
    public ResponseDto<?> stopService(@PathParam(value = "serviceClassName") String serviceClassName) { // Why is this a put?
        LOG.info("Stop service invoked in rest layer:{}", serviceClassName);
        try {
            return new ResponseDto<>(pluginService.stop(serviceClassName), RestResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when stopping service {} ] {} ", serviceClassName, ex.getMessage());
            return ErrorHandler.getFault(ex);
        }
    }
}