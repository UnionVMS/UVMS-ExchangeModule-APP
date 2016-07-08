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
package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

public class SettingTypeMapper {

    public static eu.europa.ec.fisheries.schema.config.types.v1.SettingType map(String key, String value) {
        eu.europa.ec.fisheries.schema.config.types.v1.SettingType ret = new eu.europa.ec.fisheries.schema.config.types.v1.SettingType();
        ret.setKey(key);
        ret.setValue(value);
        return ret;
    }

    public static eu.europa.ec.fisheries.schema.config.types.v1.SettingType map(String key, String value, String desc) {
        eu.europa.ec.fisheries.schema.config.types.v1.SettingType ret = new eu.europa.ec.fisheries.schema.config.types.v1.SettingType();
        ret.setKey(key);
        ret.setValue(value);
        ret.setDescription(desc);
        return ret;
    }

    public static eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType map(eu.europa.ec.fisheries.schema.config.types.v1.SettingType setting) {
        eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType ret = new eu.europa.ec.fisheries.schema.exchange.service.v1.SettingType();
        ret.setKey(setting.getKey());
        ret.setValue(setting.getValue());
        return ret;
    }
}