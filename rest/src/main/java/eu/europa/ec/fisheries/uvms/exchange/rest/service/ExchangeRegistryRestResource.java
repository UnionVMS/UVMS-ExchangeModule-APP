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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.RestResponseCode;
import eu.europa.ec.fisheries.uvms.exchange.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.exchange.rest.mapper.ServiceMapper;
import eu.europa.ec.fisheries.uvms.exchange.service.ExchangeService;
import eu.europa.ec.fisheries.uvms.exchange.service.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/plugin")
@Stateless
public class ExchangeRegistryRestResource {

	final static Logger LOG = LoggerFactory
			.getLogger(ExchangeRegistryRestResource.class);

	@EJB
	ExchangeService serviceLayer;

	@EJB
	PluginService pluginService;
	
	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Get a list of all registered and active services
	 * 
	 */
	@GET
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/list")
	@RequiresFeature(UnionVMSFeature.viewExchange)
	public ResponseDto getList() {
		LOG.info("Get list invoked in rest layer");
		try {
			return new ResponseDto(ServiceMapper.map(serviceLayer.getServiceList(null)), RestResponseCode.OK);
		} catch (ExchangeServiceException | NullPointerException ex) {
			LOG.error("[ Error when geting list. ] {} ", ex.getMessage());
			return ErrorHandler.getFault(ex);
		}
	}

	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Start a service
	 * 
	 */
	@PUT
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/start/{serviceClassName}")
	@RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
	public ResponseDto startService(@PathParam(value="serviceClassName") String serviceClassName) {			//why is this a put????		And this returns true or an exception???
		LOG.info("Start service invoked in rest layer:{}",serviceClassName);
		try {
			return new ResponseDto(pluginService.start(serviceClassName), RestResponseCode.OK);
		} catch (ExchangeServiceException ex) {
			LOG.error("[ Error when starting service {}] {}",serviceClassName,ex);
			return ErrorHandler.getFault(ex);
		}
	}

	/**
	 * 
	 * @responseMessage 200 [Success]
	 * @responseMessage 500 [Error]
	 * 
	 * @summary Stop a service
	 * 
	 */
	@PUT
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/stop/{serviceClassName}")
	@RequiresFeature(UnionVMSFeature.manageExchangeTransmissionStatuses)
	public ResponseDto stopService(@PathParam(value="serviceClassName") String serviceClassName) {		//why is this a put????
		LOG.info("Stop service invoked in rest layer:{}",serviceClassName);
		try {
			return new ResponseDto(pluginService.stop(serviceClassName), RestResponseCode.OK);
		} catch (ExchangeServiceException | NullPointerException ex) {
			LOG.error("[ Error when stopping service {} ] {} ",serviceClassName, ex.getMessage());
			return ErrorHandler.getFault(ex);
		}
	}
}