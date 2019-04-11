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
package eu.europa.ec.fisheries.uvms.exchange.model.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
*   @Notdeprecated --As of release 4.0.5 replaced by uvms-commons-date#DateUtils--
 * Back into action until we have had a chance to fix commons. Not to mention that we dont use commons in any of the other modules....
 */

public class DateUtils {
    final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    final static String DATE_FORMAT = "yyyy-MM-dd";
	


    public static Instant nowUTC() {
        return Instant.now();
    }

    public static String parseInstantToString(Instant time){
        return time.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }


    private static Instant parseToUTC(String format, String dateString) {
        ZonedDateTime zdt = ZonedDateTime.parse(dateString, java.time.format.DateTimeFormatter.ofPattern(format));
    	return zdt.toInstant();
    }
    
    public static Instant parseToUTCDateTime(String dateString) {
        return parseToUTC(DATE_TIME_FORMAT, dateString);
    }
}
