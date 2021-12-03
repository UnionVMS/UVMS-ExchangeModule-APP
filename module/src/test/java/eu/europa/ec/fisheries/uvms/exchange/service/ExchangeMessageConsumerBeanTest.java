package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.*;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesReportRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SendSalesResponseRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.*;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangePluginResponseMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ExchangeLogDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.ServiceRegistryDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.dao.UnsentMessageDaoBean;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.service.model.IncomingMovement;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ExchangeMessageConsumerBeanTest extends BuildExchangeServiceTestDeployment {

    private JMSHelper jmsHelper;
    private Jsonb jsonb;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Inject
    private ServiceRegistryDaoBean serviceRegistryDao;
    @Inject
    private UnsentMessageDaoBean unsentMessageDao;
    @Inject
    private ExchangeLogDaoBean exchangeLogDao;

    @Before
    public void initialize() throws Exception {
        jmsHelper = new JMSHelper(connectionFactory);
        jmsHelper.clearQueue(MessageConstants.RULES_MESSAGE_IN_QUEUE_NAME);
        jmsHelper.clearQueue("UVMSAssetEvent");
        jmsHelper.clearQueue(JMSHelper.RESPONSE_QUEUE);
        jmsHelper.clearQueue(JMSHelper.EXCHANGE_QUEUE);
        jsonb =  new JsonBConfigurator().getContext(null);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void pingExchangeTest() throws Exception {
        PingResponse response = jmsHelper.pingExchange();
        assertNotNull(response);
        assertEquals("pong", response.getResponse());
    }

    /* -- PLUGINS/SERVICES -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void listServiceTest() throws Exception {
        List<PluginType> inputList = new ArrayList<>();
        String request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList); // Empty list ie get all

        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        TextMessage message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertFalse(response.getService().isEmpty());
        assertEquals("ManualMovement", response.getService().get(0).getServiceClassName());

        inputList.add(PluginType.BELGIAN_SALES);

        request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList); // Have no BELGIAN_SALES in the db

        corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertTrue(response.getService().isEmpty());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailTest() throws Exception {
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createMarshalledSetCommandSendEmailRequest(serviceClassName, email, null);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals(response.getCommand().getCommand(), CommandTypeType.EMAIL);
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000); // To allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailPluginNotStartedTest() throws Exception {
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        ExchangeLog latestLogB4 = exchangeLogDao.getLatestLog();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);
        service.setStatus(false);
        service = serviceRegistryDao.updateService(service);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createMarshalledSetCommandSendEmailRequest(serviceClassName, email, null);

        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        Thread.sleep(1000); // To allow the db to sync up

        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());
        assertEquals(latestLogB4.getId(), exchangeLogDao.getLatestLog().getId()); // No log should have been created

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandPollTest() throws Exception {
        String serviceName = "Poll Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.inmarsat";
        System.out.println("Now");
        String guid = UUID.randomUUID().toString();
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        PollType pollType = new PollType();
        pollType.setPollTypeType(PollTypeType.POLL);
        pollType.setMessage("TestMessage");
        pollType.setPollId(guid);
        KeyValueType keyValueType = new KeyValueType();
        keyValueType.setKey("CONNECT_ID");
        keyValueType.setValue("TestConnectID");
        pollType.getPollReceiver().add(keyValueType);

        keyValueType = new KeyValueType();
        keyValueType.setKey("LES");
        keyValueType.setValue("TestLESID");
        pollType.getPollReceiver().add(keyValueType);

        String request = ExchangeModuleRequestMapper.createSetCommandSendPollRequest(serviceClassName, pollType, "Test User", null);


        //Thread.sleep(5 * 60 * 1000);
        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals(CommandTypeType.POLL, response.getCommand().getCommand());
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000); // To allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendReportToPluginTest() throws Exception {
        String serviceName = "Flux Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.fluxus";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.FLUX);
        MovementType movementType = createMovementType();
        List<RecipientInfoType> recipientInfoTypeList = new ArrayList<>();

        String request = ExchangeModuleRequestMapper.createSendReportToPlugin(serviceClassName, PluginType.FLUX,
                Instant.now(), null, "recipient", movementType, recipientInfoTypeList,
                movementType.getAssetName(), movementType.getIrcs(), movementType.getMmsi(),
                movementType.getExternalMarking(), movementType.getFlagState());

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_REPORT_TO_PLUGIN");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);
        SetReportRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);

        String expectedRecipient = "UNK"; // From UserRestMock
        assertEquals(ReportTypeType.MOVEMENT, response.getReport().getType());
        assertEquals(movementType.getWkt(), response.getReport().getMovement().getWkt());
        assertEquals(expectedRecipient, response.getReport().getRecipient());

        Thread.sleep(1000); // To allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        List<ExchangeLog> logs = exchangeLogDao.getExchangeLogByTypesRefAndGuid(UUID.fromString(movementType.getGuid()),
                Collections.singletonList(TypeRefType.MOVEMENT));
        assertThat(logs.size(), CoreMatchers.is(1));
        ExchangeLog log = logs.get(0);
        assertThat(log.getTransferIncoming(), CoreMatchers.is(false));

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void updatePluginSettingTest() throws Exception {
        String serviceName = "Alien Activity Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Aliens";
        String settingName = "HomePlanet";

        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.BELGIAN_ACTIVITY);
        ServiceSetting serviceSetting = new ServiceSetting();
        serviceSetting.setUpdatedTime(Instant.now());
        serviceSetting.setUser("Aliens");
        serviceSetting.setService(service);
        serviceSetting.setSetting(settingName);
        serviceSetting.setValue("OuterSpace");
        service.getServiceSettingList().add(serviceSetting);
        serviceRegistryDao.updateService(service);

        String request = ExchangeModuleRequestMapper.createUpdatePluginSettingRequest(
                serviceClassName, settingName, "Belgium", "Test User");

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "UPDATE_PLUGIN_SETTING");
        TextMessage topicMessage = (TextMessage) jmsHelper.listenOnEventBus(5000L);
        SetConfigRequest response = JAXBMarshaller.unmarshallTextMessage(topicMessage, SetConfigRequest.class);

        assertEquals(1, response.getConfigurations().getSetting().size());
        assertEquals(settingName, response.getConfigurations().getSetting().get(0).getKey());
        assertEquals("Belgium", response.getConfigurations().getSetting().get(0).getValue());

        TextMessage queueMessage = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        UpdatePluginSettingResponse setCommandResponse = JAXBMarshaller.unmarshallTextMessage(queueMessage, UpdatePluginSettingResponse.class);

        assertEquals(AcknowledgeTypeType.OK, setCommandResponse.getResponse().getType());

        serviceRegistryDao.deleteEntity(service.getId());
    }

    /* -- Ack Messages -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processStopAcknowledgeTest() throws Exception {
        String serviceName = "Stop Ack Test Service";
        String serviceClassName = "Stop Ack Test Service";
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.MANUAL);
        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        String request = ExchangePluginResponseMapper.mapToStopResponse(serviceClassName, ackType);
        String corrID = jmsHelper.sendExchangeMessage(request, null, ExchangeModuleMethod.PLUGIN_STOP_ACK.value());

        Thread.sleep(1000);
        Service updatedService = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        assertFalse(updatedService.getStatus());

        serviceRegistryDao.deleteEntity(updatedService.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processStartAcknowledgeTest() throws Exception {
        String serviceName = "Start Ack Test Service";
        String serviceClassName = "Start Ack Test Service";
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.MANUAL);
        service.setStatus(false);
        serviceRegistryDao.updateService(service);
        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        String request = ExchangePluginResponseMapper.mapToStartResponse(serviceClassName, ackType);
        String corrID = jmsHelper.sendExchangeMessage(request, null, ExchangeModuleMethod.PLUGIN_START_ACK.value());

        Thread.sleep(1000);
        Service updatedService = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        assertTrue(serviceClassName, updatedService.getStatus());

        serviceRegistryDao.deleteEntity(updatedService.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processSetReportAcknowledgeTest() throws Exception {
        UnsentMessage unsent = new UnsentMessage();
        unsent.setDateReceived(Instant.now());
        unsent.setMessage("processSetReportAcknowledgeTest");
        unsent.setRecipient("processSetReportAcknowledgeTest recipient");
        unsent.setSenderReceiver("processSetReportAcknowledgeTest senderReciever");
        unsent.setUpdatedBy("processSetReportAcknowledgeTest tester");
        unsent.setUpdateTime(Instant.now());
        unsent = unsentMessageDao.create(unsent);

        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PENDING);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        ackType.setUnsentMessageGuid(unsent.getGuid().toString());
        ackType.setLogId(exchangeLog.getId().toString());
        ackType.setMessage("processSetReportAcknowledgeTest message");

        String request = ExchangePluginResponseMapper.mapToSetReportResponse("Fake service class name", ackType);
        String corrID = jmsHelper.sendExchangeMessage(request, null, ExchangeModuleMethod.PLUGIN_SET_REPORT_ACK.value());

        Thread.sleep(1000);

        UnsentMessage shouldBeNull = unsentMessageDao.getByGuid(unsent.getGuid());
        assertNull("The guid on the line above should not exist in the db", shouldBeNull);

        ExchangeLog updatedExchangeLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, updatedExchangeLog.getStatus());
        assertEquals(1, updatedExchangeLog.getStatusHistory().size());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processSetCommandAcknowledgeNotPollTest() throws Exception {
        UnsentMessage unsent = new UnsentMessage();
        unsent.setDateReceived(Instant.now());
        unsent.setMessage("processSetCommandAcknowledgeTest");
        unsent.setRecipient("processSetCommandAcknowledgeTest recipient");
        unsent.setSenderReceiver("processSetCommandAcknowledgeTest senderReciever");
        unsent.setUpdatedBy("processSetCommandAcknowledgeTest tester");
        unsent.setUpdateTime(Instant.now());
        unsent = unsentMessageDao.create(unsent);

        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PENDING);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        ackType.setUnsentMessageGuid(unsent.getGuid().toString());
        ackType.setLogId(exchangeLog.getId().toString());
        ackType.setMessage("processSetCommandAcknowledgeTest message");

        String request = ExchangePluginResponseMapper.mapToSetCommandResponse("Fake service class name", ackType);
        String corrID = jmsHelper.sendExchangeMessage(request, null, ExchangeModuleMethod.PLUGIN_SET_COMMAND_ACK.value());

        Thread.sleep(1000);

        UnsentMessage shouldBeNull = unsentMessageDao.getByGuid(unsent.getGuid());
        assertNull("The guid on the line above should not exist in the db", shouldBeNull);

        ExchangeLog updatedExchangeLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, updatedExchangeLog.getStatus());
        assertEquals(1, updatedExchangeLog.getStatusHistory().size());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processSetCommandAcknowledgePollTest() throws Exception {
        UnsentMessage unsent = new UnsentMessage();
        unsent.setDateReceived(Instant.now());
        unsent.setMessage("processSetCommandAcknowledgeTest");
        unsent.setRecipient("processSetCommandAcknowledgeTest recipient");
        unsent.setSenderReceiver("processSetCommandAcknowledgeTest senderReciever");
        unsent.setUpdatedBy("processSetCommandAcknowledgeTest tester");
        unsent.setUpdateTime(Instant.now());
        unsent = unsentMessageDao.create(unsent);

        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setStatus(ExchangeLogStatusTypeType.PENDING);
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog.setTypeRefGuid(UUID.randomUUID());
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        ackType.setUnsentMessageGuid(unsent.getGuid().toString());
        ackType.setLogId(exchangeLog.getId().toString());
        ackType.setMessage("processSetCommandAcknowledgeTest message");
        PollStatusAcknowledgeType pollStatusAcknowledgeType = new PollStatusAcknowledgeType();
        pollStatusAcknowledgeType.setStatus(ExchangeLogStatusTypeType.SUCCESSFUL);
        pollStatusAcknowledgeType.setPollId(exchangeLog.getTypeRefGuid().toString());
        ackType.setPollStatus(pollStatusAcknowledgeType);

        String request = ExchangePluginResponseMapper.mapToSetCommandResponse("Fake service class name", ackType);
        String corrID = jmsHelper.sendExchangeMessage(request, null, ExchangeModuleMethod.PLUGIN_SET_COMMAND_ACK.value());

        Thread.sleep(1000);

        UnsentMessage shouldBeNull = unsentMessageDao.getByGuid(unsent.getGuid());
        assertNull("The guid on the line above should not exist in the db", shouldBeNull);

        ExchangeLog updatedExchangeLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, updatedExchangeLog.getStatus());
        assertEquals(1, updatedExchangeLog.getStatusHistory().size());
        assertEquals(ackType.getMessage(), updatedExchangeLog.getTypeRefMessage());
    }

    /* -- MOVEMENT -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setMovementReportTest() throws Exception {
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";
        String guid = UUID.randomUUID().toString();

        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        movementType.setSourceSatelliteId(42);
        movementType.setAisPositionAccuracy((short)42);
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String request = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType,
                "Test User", null, Instant.now(), PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);

        assertEquals("CREATE", message.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY));
        assertTrue(message.getStringProperty(MessageConstants.JMS_MESSAGE_GROUP).contains(movementType.getMmsi()));
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);
        assertEquals(movementType.getMmsi(), output.getAssetMMSI());
        assertEquals(movementType.getPosition().getLongitude(), output.getLongitude(), 0);
        assertEquals(movementType.getFlagState(), output.getFlagState());
        assertEquals(movementType.getSourceSatelliteId().shortValue(), output.getSourceSatelliteId().shortValue());
        assertEquals(movementType.getAisPositionAccuracy(), output.getAisPositionAccuracy());


        Thread.sleep(1000); // To allow the db to sync up
        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(UUID.fromString(output.getAckResponseMessageId()));
        assertNotNull(exchangeLog);
        assertEquals(ExchangeLogStatusTypeType.ISSUED, exchangeLog.getStatus());
        assertEquals(TypeRefType.MOVEMENT, exchangeLog.getTypeRefType());
        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processedMovementAlarmTest() throws Exception {
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";

        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String setupRequest = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType,
                "Test User", null, Instant.now(), PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(setupRequest, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);
        String logGuid = output.getAckResponseMessageId();

        MovementRefType movementRefType = new MovementRefType();
        movementRefType.setAckResponseMessageID(output.getAckResponseMessageId());
        movementRefType.setType(MovementRefTypeType.ALARM);
        String request = ExchangeModuleRequestMapper.mapToProcessedMovementResponse("Test username", movementRefType);

        corrID = jmsHelper.sendExchangeMessage(request, null, "PROCESSED_MOVEMENT");
        Thread.sleep(1000); //to let it work

        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(UUID.fromString(output.getAckResponseMessageId()));
        assertNotNull(exchangeLog);
        assertEquals(ExchangeLogStatusTypeType.FAILED, exchangeLog.getStatus());

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processedMovementSuccessTest() throws Exception {
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";

        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String setupRequest = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType,
                "Test User", null, Instant.now(), PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(setupRequest, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);

        MovementRefType movementRefType = new MovementRefType();
        movementRefType.setAckResponseMessageID(output.getAckResponseMessageId());
        movementRefType.setType(MovementRefTypeType.MOVEMENT);
        movementRefType.setMovementRefGuid(output.getAckResponseMessageId());
        String request = ExchangeModuleRequestMapper.mapToProcessedMovementResponse("Test username", movementRefType);

        corrID = jmsHelper.sendExchangeMessage(request, null, "PROCESSED_MOVEMENT");
        Thread.sleep(1000); //to let it work

        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(UUID.fromString(output.getAckResponseMessageId()));
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, exchangeLog.getStatus());

        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processMovementPollRequest() throws Exception {
        UUID pollRefId = UUID.randomUUID();

        // Create poll log
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setType(LogType.SEND_POLL);
        exchangeLog.setTypeRefGuid(pollRefId);
        exchangeLog.setTypeRefType(TypeRefType.POLL);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);

        // Create incoming position with reference to poll
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";

        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        setReportMovementType.setPollRef(pollRefId.toString());
        String setupRequest = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType,
                "Test User", null, Instant.now(), PluginType.OTHER, "IRIDIUM", "OnValue?");

        jmsHelper.sendExchangeMessage(setupRequest, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);

        // Send processed movement request
        UUID movementId = UUID.randomUUID();
        MovementRefType movementRefType = new MovementRefType();
        movementRefType.setAckResponseMessageID(output.getAckResponseMessageId());
        movementRefType.setType(MovementRefTypeType.MOVEMENT);
        movementRefType.setMovementRefGuid(movementId.toString());
        String request = ExchangeModuleRequestMapper.mapToProcessedMovementResponse("Test username", movementRefType);

        jmsHelper.sendExchangeMessage(request, null, "PROCESSED_MOVEMENT");
        Thread.sleep(1000); //to let it work

        // Poll log should be referencing the position
        ExchangeLog log = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertThat(log.getRelatedRefType(), CoreMatchers.is(TypeRefType.MOVEMENT));
        assertThat(log.getRelatedRefGuid(), CoreMatchers.is(movementId));

        serviceRegistryDao.deleteEntity(service.getId());
    }

    /* -- SALES -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void receiveInvalidSalesMessageTest() throws Exception {
        List<TypeRefType> list = new ArrayList<>();
        list.add(TypeRefType.SALES_REPORT);
        UUID guid = UUID.randomUUID();
        int salesLogB4 = exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size();

        String request = ExchangeModuleRequestMapper.createReceiveInvalidSalesMessage("Response invalid message",
                guid.toString(), "Invalid sender", Instant.now(), "Invalid username", PluginType.BELGIAN_SALES, "Invalid original message");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "RECEIVE_INVALID_SALES_MESSAGE");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue("UVMSSalesEvent");
        assertEquals("Response invalid message", message.getText()); // Kinda wondering how the hell this works on the sales side.

        Thread.sleep(1000);
        assertEquals(salesLogB4 + 1, exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendSalesResponseTest() throws Exception {
        List<TypeRefType> list = new ArrayList<>();
        list.add(TypeRefType.SALES_RESPONSE);
        UUID guid = UUID.randomUUID();
        int salesLogB4 = exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size();
        String serviceClassName = ExchangeServiceConstants.BELGIAN_AUCTION_SALES_PLUGIN_SERVICE_NAME;
        String request = ExchangeModuleRequestMapper.createSendSalesResponseRequest("Send sales response",
                guid.toString(), "Send sales response dataFlow", "Send sales response receiver",
                Instant.now(), ExchangeLogStatusTypeType.SUCCESSFUL_WITH_WARNINGS, PluginType.BELGIAN_SALES);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_SALES_RESPONSE");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SendSalesResponseRequest output = JAXBMarshaller.unmarshallTextMessage(message, SendSalesResponseRequest.class);

        assertEquals(ExchangePluginMethod.SEND_SALES_RESPONSE, output.getMethod());
        assertEquals("Send sales response", output.getResponse());
        assertEquals("Send sales response receiver", output.getRecipient());

        Thread.sleep(1000);
        assertEquals(salesLogB4 + 1, exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendSalesReportTest() throws Exception {
        List<TypeRefType> list = new ArrayList<>();
        list.add(TypeRefType.SALES_REPORT);
        UUID guid = UUID.randomUUID();
        int salesLogB4 = exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size();
        String serviceClassName = ExchangeServiceConstants.FLUX_SALES_PLUGIN_SERVICE_NAME;
        String request = ExchangeModuleRequestMapper.createSendSalesReportRequest("Sales report", guid.toString(),
                "Sales report dataFlow", "Send sales report receiver", Instant.now(), ExchangeLogStatusTypeType.SUCCESSFUL, PluginType.FLUX);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_SALES_REPORT");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SendSalesReportRequest output = JAXBMarshaller.unmarshallTextMessage(message, SendSalesReportRequest.class);

        assertEquals(ExchangePluginMethod.SEND_SALES_REPORT, output.getMethod());
        assertEquals("Sales report", output.getReport());
        assertEquals("Send sales report receiver", output.getRecipient());

        Thread.sleep(1000);
        assertEquals(salesLogB4 + 1, exchangeLogDao.getExchangeLogByTypesRefAndGuid(guid, list).size());
    }

    /* -- MDR -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setMDRSyncMessageRequestTest() throws Exception { // SET_MDR_SYNC_MESSAGE_REQUEST
        String request = ExchangeModuleRequestMapper.createFluxMdrSyncEntityRequest("MDR ReportType", "MDR username", "MDR from?");

        jmsHelper.registerSubscriber("ServiceName = '" + ExchangeServiceConstants.MDR_PLUGIN_SERVICE_NAME + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_MDR_SYNC_MESSAGE_REQUEST");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SetMdrPluginRequest output = JAXBMarshaller.unmarshallTextMessage(message, SetMdrPluginRequest.class);
        assertEquals("MDR ReportType", output.getRequest());
        assertEquals(ExchangePluginMethod.SET_MDR_REQUEST, output.getMethod());
    }

    /* -- FLUX -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendFluxFaReportMessageTest() throws Exception { // SEND_FLUX_FA_REPORT_MESSAGE
        String fluxMessage = "Send flux FA Report faReportMessageStr";
        String guid = UUID.randomUUID().toString();
        String request = ExchangeModuleRequestMapper.createSendFaReportMessageRequest(fluxMessage, "Send flux FA Report username",
                guid, "Send flux FA Report fluxDataFlow", "Send flux FA Report senderOrReciver",
                "Send flux FA Report onValue", "Send flux FA Report todt", "Send flux FA Report to", "Send flux FA Report ad", PluginType.BELGIAN_ACTIVITY);

        jmsHelper.registerSubscriber("ServiceName = '" + ExchangeServiceConstants.BELGIAN_ACTIVITY_PLUGIN_SERVICE_NAME + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_FLUX_FA_REPORT_MESSAGE");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SetFLUXFAReportRequest output = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAReportRequest.class);
        assertEquals(fluxMessage, output.getResponse());
        assertEquals(ExchangePluginMethod.SEND_FA_REPORT, output.getMethod());

        Thread.sleep(1000);
        ExchangeLog exchangeLog = exchangeLogDao.getLatestLog();
        assertEquals(exchangeLog.getDateReceived().toString(), LogType.SEND_FLUX_FA_REPORT_MSG, exchangeLog.getType());
        assertEquals(ExchangeLogStatusTypeType.SENT, exchangeLog.getStatus());
        assertEquals(TypeRefType.FA_REPORT, exchangeLog.getTypeRefType());
        assertEquals(fluxMessage, exchangeLog.getTypeRefMessage());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendFAQueryMessageTest() throws Exception { // SEND_FA_QUERY_MESSAGE
        String queryMessage = "Send FA Query Message12";
        String guid = UUID.randomUUID().toString();
        String request = ExchangeModuleRequestMapper.createSendFaQueryMessageRequest(queryMessage, "Send FA Query Message username",
                guid, "Send FA Query Message fluxDataFlow", "Send FA Query Message senderOrReciver",
                "Send FA Query Message todt", "Send FA Query Message to", "Send FA Query Message ad", PluginType.FLUX);

        jmsHelper.registerSubscriber("ServiceName = '" + ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_FA_QUERY_MESSAGE");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SetFLUXFAQueryRequest output = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAQueryRequest.class);
        assertEquals(queryMessage, output.getResponse());
        assertEquals(ExchangePluginMethod.SEND_FA_QUERY, output.getMethod());
        assertEquals("Send FA Query Message fluxDataFlow", output.getFluxDataFlow());

        Thread.sleep(1000);
        ExchangeLog exchangeLog = exchangeLogDao.getLatestLog();
        assertEquals(exchangeLog.getDateReceived().toString(), LogType.SEND_FA_QUERY_MSG, exchangeLog.getType());
        assertEquals(ExchangeLogStatusTypeType.SENT, exchangeLog.getStatus());
        assertEquals(TypeRefType.FA_QUERY, exchangeLog.getTypeRefType());
        assertEquals(queryMessage, exchangeLog.getTypeRefMessage());
        assertFalse(exchangeLog.getTransferIncoming());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setFluxFAResponseMessageTest() throws Exception { // SET_FLUX_FA_RESPONSE_MESSAGE
        String responseMessage = "Set Flux FA Response Message";
        String guid = UUID.randomUUID().toString();
        String request = ExchangeModuleRequestMapper.createFluxFAResponseRequestWithOnValue(responseMessage,
                "Set Flux FA Response Message username", "Set Flux FA Response Message df", guid,
                "Set Flux FA Response Message fr", "Set Flux FA Response Message onVal", ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED,
                "Set Flux FA Response Message destination", PluginType.FLUX, UUID.randomUUID().toString());

        jmsHelper.registerSubscriber("ServiceName = '" + ExchangeServiceConstants.FLUX_ACTIVITY_PLUGIN_SERVICE_NAME + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_FLUX_FA_RESPONSE_MESSAGE");
        TextMessage message = (TextMessage) jmsHelper.listenOnEventBus(5000L);

        SetFLUXFAResponseRequest output = JAXBMarshaller.unmarshallTextMessage(message, SetFLUXFAResponseRequest.class);
        assertEquals(responseMessage, output.getResponse());
        assertEquals(ExchangePluginMethod.SET_FLUX_RESPONSE, output.getMethod());
        assertEquals("Set Flux FA Response Message onVal", output.getOnValue());

        Thread.sleep(1000);
        ExchangeLog exchangeLog = exchangeLogDao.getLatestLog();
        assertEquals(exchangeLog.getTypeRefGuid().toString(), LogType.SEND_FLUX_RESPONSE_MSG, exchangeLog.getType());
        assertEquals(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED, exchangeLog.getStatus());
        assertEquals(TypeRefType.FA_RESPONSE, exchangeLog.getTypeRefType());
        assertEquals(responseMessage, exchangeLog.getTypeRefMessage());
        assertFalse(exchangeLog.getTransferIncoming());
    }

    /* -- LOG -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void updateLogStatusTest() throws Exception { // UPDATE_LOG_STATUS
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog = exchangeLogDao.createLog(exchangeLog);
        String request = ExchangeModuleRequestMapper.createUpdateLogStatusRequest(exchangeLog.getId().toString(), ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED);
        String corrID = jmsHelper.sendExchangeMessage(request, null, "UPDATE_LOG_STATUS");
        Thread.sleep(1000); //to let it work

        ExchangeLog latestLog = exchangeLogDao.getLatestLog();
        assertEquals(exchangeLog.getId(), latestLog.getId());

        ExchangeLog updatedLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertEquals(exchangeLog.getId(), updatedLog.getId());
        assertEquals(ExchangeLogStatusTypeType.PROBABLY_TRANSMITTED, updatedLog.getStatus());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void updateLogBusinessErrorTest() throws Exception { // UPDATE_LOG_BUSINESS_ERROR
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog = exchangeLogDao.createLog(exchangeLog);
        Exception e = new RuntimeException("Bankruptcy");
        String request = ExchangeModuleRequestMapper.createUpdateLogStatusRequest(exchangeLog.getId().toString(), e);
        String corrID = jmsHelper.sendExchangeMessage(request, null, "UPDATE_LOG_BUSINESS_ERROR");
        Thread.sleep(1000); //to let it work

        ExchangeLog updatedLog = exchangeLogDao.getExchangeLogByGuid(exchangeLog.getId());
        assertEquals(ExceptionUtils.getMessage(e) + ":" + ExceptionUtils.getStackTrace(e), updatedLog.getBusinessError());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void logRefIdByTypeExistsTest() throws Exception { // LOG_REF_ID_BY_TYPE_EXISTS
        ExchangeLog exchangeLog = createBasicLog();
        UUID guid = UUID.randomUUID();
        exchangeLog.setTypeRefGuid(guid);
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);
        String request = ExchangeModuleRequestMapper.createLogRefIdByTypeExistsRequest(exchangeLog.getTypeRefGuid().toString(), Collections.singletonList(TypeRefType.ALARM));
        String corrID = jmsHelper.sendExchangeMessage(request, null, "LOG_REF_ID_BY_TYPE_EXISTS");
        TextMessage message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        LogRefIdByTypeExistsResponse response = JAXBMarshaller.unmarshallTextMessage(message, LogRefIdByTypeExistsResponse.class);

        assertNull(response.getRefGuid());

        List<TypeRefType> inputList = new ArrayList<>();
        inputList.add(exchangeLog.getTypeRefType());
        request = ExchangeModuleRequestMapper.createLogRefIdByTypeExistsRequest(exchangeLog.getTypeRefGuid().toString(), inputList);
        corrID = jmsHelper.sendExchangeMessage(request, null, "LOG_REF_ID_BY_TYPE_EXISTS");
        message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        response = JAXBMarshaller.unmarshallTextMessage(message, LogRefIdByTypeExistsResponse.class);

        assertNotNull(response.getRefGuid());
        assertEquals(exchangeLog.getTypeRefGuid().toString(), response.getRefGuid());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void logIdByTypeExistsTest() throws Exception { // LOG_ID_BY_TYPE_EXISTS
        ExchangeLog exchangeLog = createBasicLog();
        exchangeLog.setTypeRefType(TypeRefType.UNKNOWN);
        exchangeLog = exchangeLogDao.createLog(exchangeLog);
        String request = ExchangeModuleRequestMapper.createLogIdByTypeExistsRequest(exchangeLog.getId().toString(), null);
        String corrID = jmsHelper.sendExchangeMessage(request, null, "LOG_ID_BY_TYPE_EXISTS");
        TextMessage message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        LogIdByTypeExistsResponse response = JAXBMarshaller.unmarshallTextMessage(message, LogIdByTypeExistsResponse.class);

        assertNotNull(response.getMessageGuid()); // This class is better written then the one above and can handle an empty refType as input. Or something
        assertEquals(exchangeLog.getId().toString(), response.getMessageGuid());

        request = ExchangeModuleRequestMapper.createLogIdByTypeExistsRequest(exchangeLog.getId().toString(), exchangeLog.getTypeRefType());
        corrID = jmsHelper.sendExchangeMessage(request, null, "LOG_ID_BY_TYPE_EXISTS");
        message = (TextMessage) jmsHelper.listenForResponseOnStandardQueue(corrID);
        response = JAXBMarshaller.unmarshallTextMessage(message, LogIdByTypeExistsResponse.class);

        assertNotNull(response.getMessageGuid());
        assertEquals(exchangeLog.getId().toString(), response.getMessageGuid());
    }

    /* -- Assets -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void receiveAssetInformationTest() throws Exception { // RECEIVE_ASSET_INFORMATION
        String assets = "ReceiveAssetInformation assets";
        String guid = UUID.randomUUID().toString();
        String request = ExchangeModuleRequestMapper.createReceiveAssetInformation(assets, "ReceiveAssetInformation assets username", PluginType.OTHER, "ais");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "RECEIVE_ASSET_INFORMATION");
        TextMessage message = (TextMessage) jmsHelper.listenOnQueue("UVMSAssetEvent");
        String response = message.getText();

        assertEquals(assets, response);
        assertEquals("ASSET_INFORMATION", message.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY));

        Thread.sleep(1000);
        ExchangeLog exchangeLog = exchangeLogDao.getLatestLog();
        assertEquals(assets, exchangeLog.getTypeRefMessage());
        assertEquals(LogType.RECEIVE_ASSET_INFORMATION, exchangeLog.getType());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, exchangeLog.getStatus());
        assertEquals(TypeRefType.ASSETS, exchangeLog.getTypeRefType());
        assertEquals("ais", exchangeLog.getSenderReceiver());
    }

    private Service createAndPersistBasicService(String name, String serviceClassName, PluginType pluginType) throws Exception {
        Service s = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        if (s != null) {
            serviceRegistryDao.deleteEntity(s.getId());
            Thread.sleep(1000);
        }

        s = new Service();
        s.setActive(true);
        s.setDescription("Test description");
        s.setName(name);
        s.setSatelliteType(null);
        s.setServiceClassName(serviceClassName);
        s.setServiceResponse(serviceClassName + "PLUGIN_RESPONSE");
        s.setStatus(true);
        s.setType(pluginType);
        s.setUpdated(Instant.now());
        s.setUpdatedBy("Exchange Tests");

        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
        ServiceCapability serviceCapability = new ServiceCapability();
        serviceCapability.setService(s);
        serviceCapability.setUpdatedBy("Exchange Tests");
        serviceCapability.setUpdatedTime(Instant.now());
        serviceCapability.setCapability(CapabilityTypeType.POLLABLE);
        serviceCapability.setValue(true);
        serviceCapabilityList.add(serviceCapability);
        s.setServiceCapabilityList(serviceCapabilityList);

        s.setServiceSettingList(new ArrayList<>());

        return serviceRegistryDao.createEntity(s);
    }

    private MovementType createMovementType() {
        MovementType movementType = new MovementType();
        movementType.setCalculatedCourse(0.0);
        movementType.setCalculatedSpeed(5.5);
        movementType.setConnectId("TestConnectID");
        movementType.setGuid(UUID.randomUUID().toString());
        movementType.setMeasuredSpeed(5.6);
        movementType.setWkt("POINT(11.999335 57.738125)");
        MovementPoint movementPoint = new MovementPoint();
        movementPoint.setAltitude(42.42);
        movementPoint.setLatitude(57.738125);
        movementPoint.setLongitude(11.999335);
        movementType.setPosition(movementPoint);
        movementType.setPositionTime(Date.from(Instant.now()));

        MovementMetaData movementMetaData = new MovementMetaData();
        movementMetaData.setClosestCountryCoast("Closest EEZ");
        movementMetaData.setClosestPort("Closest port");
        movementMetaData.setDistanceToClosestPort(55.5);
        movementMetaData.setDistanceToCountryCoast(01.0);
        movementMetaData.getAreas();    //makes it an empty area
        movementType.setMetaData(movementMetaData);

        movementType.setAssetName("AssetName");
        movementType.setFlagState("Flagstate");
        movementType.setMovementType(MovementTypeType.EXI);
        movementType.setExternalMarking("ExternalMarking");
        movementType.setMmsi("TestMMSI");
        movementType.setIrcs("TestIRCS");

        AssetId assetId = new AssetId();
        assetId.setAssetType(AssetType.AIR);
        AssetIdList assetIdList = new AssetIdList();
        assetIdList.setIdType(AssetIdType.GUID);
        assetIdList.setValue(UUID.randomUUID().toString());
        assetId.getAssetIdList().add(assetIdList);

        assetIdList = new AssetIdList();
        assetIdList.setIdType(AssetIdType.MMSI);
        assetIdList.setValue(movementType.getMmsi());
        assetId.getAssetIdList().add(assetIdList);
        movementType.setAssetId(assetId);
        movementType.setSource(MovementSourceType.IRIDIUM);

        return movementType;
    }

    private ExchangeLog createBasicLog() {
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(LogType.PROCESSED_MOVEMENT);
        exchangeLog.setStatus(ExchangeLogStatusTypeType.UNKNOWN);
        exchangeLog.setUpdatedBy("Tester");
        exchangeLog.setUpdateTime(Instant.now());
        exchangeLog.setDateReceived(Instant.now());
        exchangeLog.setSenderReceiver("Test sender/receiver");
        exchangeLog.setTransferIncoming(false);

        return exchangeLog;
    }
}
