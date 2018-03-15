package eu.europa.ec.fisheries.uvms.exchange.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogRefType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.ReceiveMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendEmailType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendMovementType;
import eu.europa.ec.fisheries.schema.exchange.v1.SendPollType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import java.util.Date;
import org.junit.Test;

public class LogMapperTest {

    @Test
    public void toNewEntityWhenLogTypeIsReceiveMovement() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.MOVEMENT;
        Date dateReceived = new Date();
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
        input.setDateRecieved(dateReceived);
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
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Date fwdDate = new Date();
        String fwdRule = "fantastic rules and where to find them";
        String recipient = "potter@wb.com";

        SendMovementType input = new SendMovementType();
        input.setType(LogType.SEND_MOVEMENT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(fwdDate);
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
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Date fwdDate = new Date();
        String recipient = "potter@wb.com";

        SendPollType input = new SendPollType();
        input.setType(LogType.SEND_POLL);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(fwdDate);
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
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        Date fwdDate = new Date();
        String fwdRule = "fantastic rules and where to find them";
        String recipient = "potter@wb.com";

        SendEmailType input = new SendEmailType();
        input.setType(LogType.SEND_EMAIL);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setFwdDate(fwdDate);
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
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndTypeRefIsNotFilledIn() throws Exception {
        //data set
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String destination = "destination";

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndStatusIsNotFilledIn() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_REPORT;
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }


    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesReportAndUsernameIsNull() throws Exception {
        //data set
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String destination = "destination";

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_REPORT);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesResponse() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_RESPONSE;
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_RESPONSE);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsReceiveSalesQuery() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_QUERY;
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.RECEIVE_SALES_QUERY);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(true);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendSalesReport() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_REPORT;
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_SALES_REPORT);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);

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
    }

    @Test
    public void toNewEntityWhenLogTypeIsSendSalesResponse() throws Exception {
        //data set
        String typeRefGuid = "trg";
        TypeRefType typeRefType = TypeRefType.SALES_RESPONSE;
        Date dateReceived = new Date();
        String senderOrReceiver = "BEL";
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.SUCCESSFUL;
        String message = "<xml></xml>";
        String destination = "destination";

        LogRefType logRefType = new LogRefType();
        logRefType.setRefGuid(typeRefGuid);
        logRefType.setType(typeRefType);
        logRefType.setMessage(message);

        ExchangeLogType input = new ExchangeLogType();
        input.setType(LogType.SEND_SALES_RESPONSE);
        input.setTypeRef(logRefType);
        input.setDateRecieved(dateReceived);
        input.setSenderReceiver(senderOrReceiver);
        input.setStatus(status);
        input.setIncoming(false);
        input.setDestination(destination);

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
    }

    @Test
    public void toModelWhenEntityIsReceiveMovementLog() {
        Date dateReceived = new Date();
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
        Date dateReceived = new Date();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String fwdRule = "Coldplay";
        Date fwdDate = new Date();
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
        Date dateReceived = new Date();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        Date fwdDate = new Date();
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
        Date dateReceived = new Date();
        String guid = "Paradise";
        String senderReceiver = "Chris Martin";
        boolean incoming = true;
        ExchangeLogStatusTypeType status = ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED;
        String fwdRule = "Coldplay";
        Date fwdDate = new Date();
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
        Date dateReceived = new Date();
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
        Date dateReceived = new Date();
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
        Date dateReceived = new Date();
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
        Date dateReceived = new Date();
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
        Date dateReceived = new Date();
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

}