package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.LogTypeLabel;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange.ExchangeLogDto;
import org.junit.Test;


import java.sql.Date;
import java.time.Instant;

import static org.junit.Assert.*;

public class ExchangeLogMapperTest {


    @Test
    public void mapToExchangeLogDtoWhenReceiveMovement() {
        //data set
        String guid = "logGuid";
        boolean incoming = true;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.MOVEMENT;
        String senderReceiver = "FRA";


        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z


        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        String source = "BEL";
        String recipient = "FRA";

        ReceiveMovementType log = new ReceiveMovementType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.RECEIVE_MOVEMENT);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);
        log.setSource(source);
        log.setRecipient(recipient);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.RECEIVED_MOVEMENT.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
        assertEquals(source, dto.getSource());
        assertEquals(recipient, dto.getRecipient());
    }

    @Test
    public void mapToExchangeLogDtoWhenSendMovement() {
        //data set
        String guid = "logGuid";
        boolean incoming = false;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.MOVEMENT;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        Instant fwdDate = Instant.now();
        String fwdRule = "rule 1";
        String recipient = "FRA";

        SendMovementType log = new SendMovementType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.SEND_MOVEMENT);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);
        log.setFwdDate(Date.from(fwdDate));
        log.setFwdRule(fwdRule);
        log.setRecipient(recipient);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.SENT_MOVEMENT.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
        assertEquals(fwdRule, dto.getRule());
        assertEquals(recipient, dto.getRecipient());
        assertNotNull(dto.getDateFwd());
    }

    @Test
    public void mapToExchangeLogDtoWhenSendEmail() {
        //data set
        String guid = "logGuid";
        boolean incoming = false;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.ALARM;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        Instant fwdDate = Instant.now();
        String fwdRule = "rule 1";
        String recipient = "FRA";

        SendEmailType log = new SendEmailType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.SEND_EMAIL);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);
        log.setFwdDate(Date.from(fwdDate));
        log.setFwdRule(fwdRule);
        log.setRecipient(recipient);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.SENT_EMAIL.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
        assertEquals(fwdRule, dto.getRule());
        assertEquals(recipient, dto.getRecipient());
        assertNotNull(dto.getDateFwd());
    }

    @Test
    public void mapToExchangeLogDtoWhenSendPoll() {
        //data set
        String guid = "logGuid";
        boolean incoming = false;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.POLL;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        String fwdRule = "rule 1";
        String recipient = "FRA";

        SendPollType log = new SendPollType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.SEND_POLL);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);
        log.setFwdRule(fwdRule);
        log.setRecipient(recipient);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.SENT_POLL.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
        assertEquals(fwdRule, dto.getRule());
        assertEquals(recipient, dto.getRecipient());
    }

    @Test
    public void mapToExchangeLogDtoWhenSendSalesReport() {
        //data set
        String guid = "logGuid";
        boolean incoming = false;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.SALES_REPORT;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.SEND_SALES_REPORT);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.SENT_SALES_REPORT.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
    }

    @Test
    public void mapToExchangeLogDtoWhenSendSalesResponse() {
        //data set
        String guid = "logGuid";
        boolean incoming = false;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.SALES_RESPONSE;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.SEND_SALES_RESPONSE);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.SENT_SALES_RESPONSE.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
    }

    @Test
    public void mapToExchangeLogDtoWhenReceiveSalesQuery() {
        //data set
        String guid = "logGuid";
        boolean incoming = true;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.SALES_QUERY;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.RECEIVE_SALES_QUERY);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.RECEIVED_SALES_QUERY.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
    }

    @Test
    public void mapToExchangeLogDtoWhenReceiveSalesReport() {
        //data set
        String guid = "logGuid";
        boolean incoming = true;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.SALES_REPORT;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.RECEIVE_SALES_REPORT);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.RECEIVED_SALES_REPORT.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
    }

    @Test
    public void mapToExchangeLogDtoWhenReceiveSalesResponseAndTypeRefIsNotNull() {
        //data set
        String guid = "logGuid";
        boolean incoming = true;
        String refGuid = "refGuid";
        TypeRefType refType = TypeRefType.SALES_RESPONSE;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z

        LogRefType ref = new LogRefType();
        ref.setType(refType);
        ref.setRefGuid(refGuid);

        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.RECEIVE_SALES_RESPONSE);
        log.setTypeRef(ref);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.RECEIVED_SALES_RESPONSE.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(refGuid, dto.getLogData().getGuid());
        assertEquals(refType, dto.getLogData().getType());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
    }

    @Test
    public void mapToExchangeLogDtoWhenReceiveSalesResponseAndTypeRefIsNull() {
        //data set
        String guid = "logGuid";
        boolean incoming = true;
        String senderReceiver = "FRA";

        Instant dateReceived = Instant.parse("2017-05-10T13:24:56.00Z");   //2007-12-03T10:15:30.00Z


        ExchangeLogType log = new ExchangeLogType();
        log.setGuid(guid);
        log.setDateRecieved(Date.from(dateReceived));
        log.setIncoming(incoming);
        log.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        log.setType(LogType.RECEIVE_SALES_RESPONSE);
        log.setSenderReceiver(senderReceiver);

        //execute
        ExchangeLogDto dto = ExchangeLogMapper.mapToExchangeLogDto(log);

        //assert
        assertEquals(LogTypeLabel.RECEIVED_SALES_RESPONSE.name(), dto.getType());
        assertNotNull(dto.getDateRecieved());
        assertEquals(guid, dto.getId());
        assertEquals(incoming, dto.isIncoming());
        assertEquals(senderReceiver, dto.getSenderRecipient());
        assertEquals("SUCCESSFUL", dto.getStatus());
        assertNull(dto.getLogData());
    }
}