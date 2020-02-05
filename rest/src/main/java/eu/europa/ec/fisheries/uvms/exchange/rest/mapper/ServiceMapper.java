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
package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ServiceMapper {

    public static List<Plugin> map(List<Service> serviceList) {
        List<Plugin> plugins = new ArrayList<>();
        if (serviceList != null) {
            for (Service service : serviceList) {
                Plugin plugin = new Plugin();
                plugin.setName(service.getName());
                plugin.setServiceClassName(service.getServiceClassName());
                plugin.setType(service.getType().name());
                plugin.setStatus(service.getStatus() ? "STARTED" : "STOPPED");
                plugins.add(plugin);
            }
        }
        return plugins;
    }
}
