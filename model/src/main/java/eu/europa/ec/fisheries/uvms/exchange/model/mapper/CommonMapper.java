/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.model.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author jojoha
 */
public class CommonMapper {

    public static ExchangeLogType mapCommandTypeToExchangeLogType(CommandType command) {

        //TODO FIX EXCHANGE LOG
        ExchangeLogType type = new ExchangeLogType();
        type.setAssetId("FAKE ASSET");
        type.setDateFwd(parseTimestamp(new Date()));
        type.setDateRecieved(parseTimestamp(new Date()));
        type.setFwdRule("FAKE RULE");
        //type.setMessage(poll.getMessage());
        //type.setPollTrackId(poll.getPollTrackId());
        type.setRecipient("FAKE RECIPIENT");
        type.setSentBy("FAKE SENDER");
        type.setStatus(ExchangeLogStatusType.PENDING);
        //type.setTerminalId(poll.getTerminalId());
        //type.setTerminalType(poll.getServiceId());
        return type;
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
