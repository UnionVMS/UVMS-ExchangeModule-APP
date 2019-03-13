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

package eu.europa.ec.fisheries.uvms.exchange.constant;

public class ExchangeConstants {

    private ExchangeConstants() {
    }

    public static final String SERVICE_FIND_ALL = "Service.findAll";
    public static final String SERVICE_FIND_BY_TYPES = "Service.findByTypes";
    public static final String SERVICE_FIND_BY_SERVICE_CLASS_NAME = "Service.findByServiceClassName";
    public static final String SERVICE_FIND_BY_NAME = "Service.findByServiceMappedName";
    public static final String CAPABILITY_FIND_BY_SERVICE = "ServiceCapability.findByServiceId";
    public static final String SETTING_FIND_BY_SERVICE = "ServiceSetting.findByServiceId";
    public static final String UNSENT_FIND_ALL = "UnsentMessage.findAll";
    public static final String UNSENT_BY_GUID = "UnsentMessage.findByGuid";
    public static final String LOG_BY_GUID = "Log.findByGuid";
    public static final String LOG_BY_TYPE_REF_AND_GUID = "Log.findByTypeRefGuid";
    public static final String LOG_BY_TYPE_RANGE_OF_REF_GUIDS = "Log.findByRangeOfRefGuids";
    public static final String LATEST_LOG = "Log.latestLog";

}