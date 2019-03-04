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
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SendMovementToPluginResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingResponse;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

import java.util.List;

public class ExchangeModuleResponseMapper {

    public static AcknowledgeType mapAcknowledgeTypeOK() {
    	AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setType(AcknowledgeTypeType.OK);
    	return ackType;
    }

    public static AcknowledgeType mapAcknowledgeTypeNOK(String messageId, String errorMessage) {
    	AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setMessage(errorMessage);
    	ackType.setMessageId(messageId);
    	ackType.setType(AcknowledgeTypeType.NOK);
    	return ackType;
    }
    
    public static String mapSetCommandResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
        SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static String mapSendMovementToPluginResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	SendMovementToPluginResponse response = new SendMovementToPluginResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
    
    public static String mapUpdateSettingResponse(AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	UpdatePluginSettingResponse response = new UpdatePluginSettingResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static ExchangeFault createFaultMessage(FaultCode code, String message) {
    	ExchangeFault fault = new ExchangeFault();
    	fault.setCode(code.getCode());
    	fault.setMessage(message);
    	return fault;
    }

	public static String mapServiceListResponse(List<ServiceResponseType> serviceList) throws ExchangeModelMarshallException {
		GetServiceListResponse response = new GetServiceListResponse();
		response.getService().addAll(serviceList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}

}