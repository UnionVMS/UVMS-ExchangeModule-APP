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
package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.message.event.carrier.PluginMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.exception.ExchangeServiceException;
import javax.ejb.Local;
import javax.enterprise.event.Observes;

@Local
public interface PluginService {

    void registerService(PluginMessageEvent message);
    void unregisterService(PluginMessageEvent message);
    void setConfig(ConfigSettingEvent settingEvent);
    void updatePluginSetting(ExchangeMessageEvent settingEvent);
    boolean ping(String serviceClassName) throws ExchangeServiceException;
	boolean start(String serviceClassName) throws ExchangeServiceException;
	boolean stop(String serviceClassName) throws ExchangeServiceException;
    
}