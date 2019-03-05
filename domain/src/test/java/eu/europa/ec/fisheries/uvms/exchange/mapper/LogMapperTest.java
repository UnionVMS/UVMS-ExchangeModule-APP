package eu.europa.ec.fisheries.uvms.exchange.mapper;

import eu.europa.ec.fisheries.schema.exchange.v1.*;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import org.junit.Test;


import java.util.Date;  //leave be
import java.time.Instant;

import static org.junit.Assert.*;

public class LogMapperTest {

    @Test
    public void toNewEntityWhenLogTypeIsReceiveMovement() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.MOVEMENT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;

        String source = "FLUX";
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ReceiveMovementType input = new ReceiveMovementType();
        input.setType(LogType.RECEIVE_MOVEMENT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertEquals(source, result.getSource());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_MOVEMENT, result.getType());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendMovement() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.MOVEMENT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Instant fwdDate = Instant.now();
        String fwdRule = "fantastic rules and where to find them";
        String recipient = "potter@wb.com";

        SendMovementType input = new SendMovementType();
        input.setType(LogType.SEND_MOVEMENT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(Date.from(fwdDate));
        input.setFwdRule(fwdRule);
        input.setRecipient(recipient);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertEquals(fwdDate, result.getFwdDate());
        assertEquals(fwdRule, result.getFwdRule());
        assertEquals(recipient, result.getRecipient());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_MOVEMENT, result.getType());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendPoll() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.POLL;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Instant fwdDate = Instant.now();
        String recipient = "potter@wb.com";

        SendPollType input = new SendPollType();
        input.setType(LogType.SEND_POLL);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(Date.from(fwdDate));
        input.setRecipient(recipient);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertEquals(fwdDate, result.getFwdDate());
        assertEquals(recipient, result.getRecipient());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_POLL, result.getType());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendEmail() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.UNKNOWN;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Instant fwdDate = Instant.now();
        String fwdRule = "fantastic rules and where to find them";
        String recipient = "potter@wb.com";

        SendEmailType input = new SendEmailType();
        input.setType(LogType.SEND_EMAIL);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(Date.from(fwdDate));
        input.setFwdRule(fwdRule);
        input.setRecipient(recipient);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertEquals(fwdDate, result.getFwdDate());
        assertEquals(fwdRule, result.getFwdRule());
        assertEquals(recipient, result.getRecipient());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_EMAIL, result.getType());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndTypeRefAndStatusAreFilledIn() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_REPORT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_REPORT, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndTypeRefIsNotFilledIn() throws Exception {
        //data set
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String destination = "destination";
        String source = "FLUX";

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_REPORT, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndStatusIsNotFilledIn() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_REPORT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "BELGIAN_AUCTION_PLUGIN";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(ExchangeLogStatusTypeType.ISSUED, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(ExchangeLogStatusTypeType.ISSUED, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_REPORT, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }


    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndUsernameIsNull() throws Exception {
        //data set
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String destination = "destination";
        String source = "yo mama";

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, null);

        //assert
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals("SYSTEM", result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals("SYSTEM", result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_REPORT, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesResponse() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_RESPONSE;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "test";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_RESPONSE);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_RESPONSE, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesQuery() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_QUERY;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_QUERY);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertTrue(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_SALES_QUERY, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendSalesReport() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_REPORT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_SALES_REPORT, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendSalesResponse() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_RESPONSE;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "BELGIAN_AUCTION_PLUGIN";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_SALES_RESPONSE);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_SALES_RESPONSE, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toModelWhenEntityIsReceiveMovementLog() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String source = "Coldplay";
        String recipient = "Viva la vida";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setSource(source);
        entity.setRecipient(recipient);
        entity.setType(LogType.RECEIVE_MOVEMENT);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.RECEIVE_MOVEMENT, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(source, ((ReceiveMovementType) model).getSource());
        assertEquals(recipient, ((ReceiveMovementType) model).getRecipient());
    }

    @Test
    public void toModelWhenEntityIsSendMovementLog() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String fwdRule = "Coldplay";
        Instant fwdDate = Instant.now();
        String recipient = "Viva la vida";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setFwdRule(fwdRule);
        entity.setFwdDate(fwdDate);
        entity.setRecipient(recipient);
        entity.setType(LogType.SEND_MOVEMENT);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.SEND_MOVEMENT, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(fwdRule, ((SendMovementType) model).getFwdRule());
        assertEquals(fwdDate, ((SendMovementType) model).getFwdDate());
        assertEquals(recipient, ((SendMovementType) model).getRecipient());
    }

    @Test
    public void toModelWhenEntityIsPollLog() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        Instant fwdDate = Instant.now();
        String recipient = "Viva la vida";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setFwdDate(fwdDate);
        entity.setRecipient(recipient);
        entity.setType(LogType.SEND_POLL);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.SEND_POLL, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(fwdDate, ((SendPollType) model).getFwdDate());
        assertEquals(recipient, ((SendPollType) model).getRecipient());
    }

    @Test
    public void toModelWhenEntityIsEmailLog() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String fwdRule = "Coldplay";
        Instant fwdDate = Instant.now();
        String recipient = "Viva la vida";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setFwdRule(fwdRule);
        entity.setFwdDate(fwdDate);
        entity.setRecipient(recipient);
        entity.setType(LogType.SEND_EMAIL);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.SEND_EMAIL, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(fwdRule, ((SendEmailType) model).getFwdRule());
        assertEquals(fwdDate, ((SendEmailType) model).getFwdDate());
        assertEquals(recipient, ((SendEmailType) model).getRecipient());
    }

    @Test
    public void toModelWhenEntityIsReceiveSalesQuery() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String destination = "destination";
        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setType(LogType.RECEIVE_SALES_QUERY);
        entity.setDestination(destination);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.RECEIVE_SALES_QUERY, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(destination, model.getDestination());
    }

    @Test
    public void toModelWhenEntityIsReceiveSalesReport() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String destination = "destination";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setType(LogType.RECEIVE_SALES_REPORT);
        entity.setDestination(destination);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.RECEIVE_SALES_REPORT, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(destination, model.getDestination());
    }

    @Test
    public void toModelWhenEntityIsReceiveSalesResponse() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String destination = "destination";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setType(LogType.RECEIVE_SALES_RESPONSE);
        entity.setDestination(destination);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.RECEIVE_SALES_RESPONSE, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(destination, model.getDestination());
    }

    @Test
    public void toModelWhenEntityIsSendSalesReport() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String destination = "destination";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setType(LogType.SEND_SALES_REPORT);
        entity.setDestination(destination);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.SEND_SALES_REPORT, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(destination, model.getDestination());
    }

    @Test
    public void toModelWhenEntityIsSendSalesResponse() {
        Instant dateReceived = Instant.now();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String destination = "destination";

        ExchangeLog entity = new ExchangeLog();
        entity.setDateReceived(dateReceived);
        entity.setGuid(guid);
        entity.setSenderReceiver(senderReceiver);
        entity.setTransferIncoming(incoming);
        entity.setStatus(status);
        entity.setType(LogType.SEND_SALES_RESPONSE);
        entity.setDestination(destination);

        ExchangeLogType model = LogMapper.toModel(entity);

        assertEquals(LogType.SEND_SALES_RESPONSE, model.getType());
        assertEquals(dateReceived, model.getDateRecieved());
        assertEquals(guid, model.getGuid());
        assertEquals(senderReceiver, model.getSenderReceiver());
        assertEquals(incoming, model.isIncoming());
        assertEquals(status, model.getStatus());
        assertEquals(destination, model.getDestination());
    }

    @Test
    public void toNewEntityWhenLogTypeIsRcvFluxFAReportMsg() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.FA_REPORT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RCV_FLUX_FA_REPORT_MSG);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.RCV_FLUX_FA_REPORT_MSG, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveFAQueryMsg() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.FA_QUERY;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_FA_QUERY_MSG);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.RECEIVE_FA_QUERY_MSG, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendFAQueryMsg() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.FA_QUERY;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_FA_QUERY_MSG);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_FA_QUERY_MSG, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendFluxFAReportMsg() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.FA_REPORT;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_FLUX_FA_REPORT_MSG);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_FLUX_FA_REPORT_MSG, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendFluxResponseMsg() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.FA_RESPONSE;
        Instant dateReceived = Instant.now();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";
        String source = "FLUX";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_FLUX_RESPONSE_MSG);
        input.setTypeRef(logRefType);
        input.setDateRecieved(Date.from(dateReceived));
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);
        input.setSource(source);

        String username = "stainii";

        //execute
        ExchangeLog result = LogMapper.toNewEntity(input, username);

        //assert
        assertEquals(typeRefGuid, result.getTypeRefGuid());
        assertEquals(typeRefType, result.getTypeRefType());
        assertEquals(dateReceived, result.getDateReceived());
        assertEquals(senderOrReceiver, result.getSenderReceiver());
        assertEquals(status, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals(result, result.getStatusHistory().get(0).getLog());
        assertEquals(status, result.getStatusHistory().get(0).getStatus());
        assertNotNull(result.getStatusHistory().get(0).getStatusTimestamp());
        assertEquals(username, result.getStatusHistory().get(0).getUpdatedBy());
        assertNotNull(result.getStatusHistory().get(0).getUpdateTime());
        assertEquals(username, result.getUpdatedBy());
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getTransferIncoming());
        assertEquals(LogType.SEND_FLUX_RESPONSE_MSG, result.getType());
        assertEquals(destination, result.getDestination());
        assertEquals(source, result.getSource());
    }
}