/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	private static Logger LOG = LoggerFactory.getLogger(DateUtils.class);
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
