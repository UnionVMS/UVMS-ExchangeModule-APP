package eu.europa.ec.fisheries.uvms.exchange.model.remote;
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

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ServiceRegistryModel {
    public ServiceResponseType registerService(ServiceType model, CapabilityListType capabilityList, SettingListType settingList, String username) throws ExchangeModelException;

    public ServiceResponseType getPlugin(String pluginId) throws ExchangeModelException;

    public ServiceResponseType unregisterService(ServiceType serviceType, String username) throws ExchangeModelException;

    public List<ServiceResponseType> getPlugins(List<PluginType> pluginType) throws ExchangeModelException;

    public ServiceResponseType updatePluginSettings(String serviceClassName, SettingListType settings, String username) throws ExchangeModelException;

    public List<SettingType> getPluginSettings(String serviceClassName) throws ExchangeModelException;

    public List<CapabilityType> getPluginCapabilities(String pluginId) throws ExchangeModelException;

    public ServiceResponseType updatePluginStatus(String serviceName, StatusType status, String username) throws ExchangeModelException;
}
