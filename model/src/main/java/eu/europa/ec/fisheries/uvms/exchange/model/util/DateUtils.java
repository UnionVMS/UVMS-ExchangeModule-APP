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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
/**
 *
* @deprecated As of release 4.0.5 replaced by uvms-commons-date#DateUtils
 */
@Deprecated
public class DateUtils {
    final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    final static String DATE_FORMAT = "yyyy-MM-dd";
	
    public static XMLGregorianCalendar dateToXmlGregorian(Date timestamp) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(timestamp);
        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException ex) {
        }
        return xmlCalendar;
    }

    public static DateTime nowUTC() throws IllegalArgumentException {
        return new DateTime(DateTimeZone.UTC);
    }

    public static Date parseTimestamp(XMLGregorianCalendar timestamp) {
        if (timestamp != null) {
            return timestamp.toGregorianCalendar().getTime();
        }
        return null;
    }

    private static Date parseToUTC(String format, String dateString) {
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(format).withOffsetParsed();
    	DateTime dateTime = formatter.withZoneUTC().parseDateTime(dateString);
    	GregorianCalendar cal = dateTime.toGregorianCalendar();
    	return cal.getTime();
    }
    
    public static Date parseToUTCDateTime(String dateString) {
        return parseToUTC(DATE_TIME_FORMAT, dateString);
    }
}
