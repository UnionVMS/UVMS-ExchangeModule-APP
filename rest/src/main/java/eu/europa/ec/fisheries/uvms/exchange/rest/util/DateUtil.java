package eu.europa.ec.fisheries.uvms.exchange.rest.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;

public class DateUtil {

    final static org.slf4j.Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    final static String FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    public static java.sql.Timestamp getDateFromString(String inDate) throws ParseException {
        Date date = parseToUTCDate(inDate);
        return new java.sql.Timestamp(date.getTime());
    }

    public static Date parseToUTCDate(String dateString) throws IllegalArgumentException {
        try {
            if (dateString != null) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT).withOffsetParsed();
                DateTime dateTime = formatter.withZoneUTC().parseDateTime(dateString);
                GregorianCalendar cal = dateTime.toGregorianCalendar();
                return cal.getTime();
            } else
                return null;
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public static String parseUTCDateToString(Date date) {
        String dateString = null;
        if (date != null) {
            DateFormat df = new SimpleDateFormat(FORMAT);
            dateString = df.format(date);
        }
        return dateString;
    }

    public static Date parseTimestamp(XMLGregorianCalendar timestamp) {
        if (timestamp != null) {
            return timestamp.toGregorianCalendar().getTime();
        }
        return null;
    }

    public static XMLGregorianCalendar parseTimestamp(Date timestamp) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(timestamp);
        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException ex) {
        }
        return xmlCalendar;
    }
}
