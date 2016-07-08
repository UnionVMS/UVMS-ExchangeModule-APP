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
package eu.europa.ec.fisheries.uvms.exchange.service.util;

/**
 **/
public class StringUtil {
    
    
    public static String compressServiceClassName(String serviceClassName) {
        if (serviceClassName != null && serviceClassName.length() > 36) {
            String[] packages = serviceClassName.split("\\.");
            if (packages.length > 2) {
                StringBuilder compressed = new StringBuilder();
                for (int i = 0; i < packages.length - 2; i++) {
                    compressed.append(packages[i].charAt(0));
                    compressed.append(".");
                }

                compressed.append(packages[packages.length - 2]);
                compressed.append(".");
                compressed.append(packages[packages.length - 1]);
                return compressed.toString();
            } else {
                return serviceClassName.substring(serviceClassName.length() - 36, serviceClassName.length());
            }
        } else {
            return serviceClassName;
        }
    }
}