/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.service.constants;

public enum ColumnType {

    NAME("name"),
    DESCRIPTION("description"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    END_POINT("endPoint"),
    CHANNEL("channel"),
    ORGANISATION("organisation"),
    ENABLED("enabled"),
    MESSAGE_TYPE("messageType"),
    SUBSCRIPTION_TYPE("subscriptionType"),
    TRIGGER_TYPE("triggerType"),
    SUB_ID("id");

    private String propertyName;

    ColumnType(String propertyName) {
        this.propertyName = propertyName;
    }

    public String propertyName() {
        return this.propertyName;
    }
}
