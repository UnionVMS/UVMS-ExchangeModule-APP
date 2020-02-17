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
package eu.europa.ec.fisheries.uvms.exchange.rest.dto;


public enum RestResponseCode {
	OK(200),
	
	EXCHANGE_ERROR(501),
	
	INPUT_ERROR(511),
	MAPPING_ERROR(512),
	
	SERVICE_ERROR(521),
	MODEL_ERROR(522),
	DOMAIN_ERROR(523),
	
	UNAUTHORIZED(401),
	
    UNDEFINED_ERROR(500);

    private int code;
    
    RestResponseCode(int code) {
    	this.code = code;
    }
    
    public int getCode() {
    	return code;
    }

    public RestResponseCode fromInt(int code){
		for (RestResponseCode value : RestResponseCode.values()) {
			if(value.code == code) {
				return value;
			}
		}
		return null;
	}

	public RestResponseCode fromString(String code){
		return fromInt(Integer.parseInt(code));
	}

}