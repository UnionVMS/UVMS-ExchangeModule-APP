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
package eu.europa.ec.fisheries.uvms.exchange.model.constant;

public enum FaultCode {
	EXCHANGE_MESSAGE(3700),
	EXCHANGE_TOPIC_MESSAGE(3701),
	EXCHANGE_EVENT_SERVICE(3201),
	
	EXCHANGE_MODEL_EXCEPTION(3800),
	EXCHANGE_MAPPER(3810),
	EXCHANGE_MARSHALL_EXCEPTION(3811),
	
	EXCHANGE_COMMAND_INVALID(3120),
	EXCHANGE_PLUGIN_INVALID(3205),
	
	//Exchange Plugin Fault Codes
	EXCHANGE_PLUGIN_EVENT(3200),
	PLUGIN_VALIDATION(3220);
	
	private final int code;
	
	private FaultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}