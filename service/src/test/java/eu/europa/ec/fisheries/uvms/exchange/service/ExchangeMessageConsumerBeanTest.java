package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.common.v1.*;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.UpdatePluginSettingResponse;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.EmailType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetConfigRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.v1.SetReportRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.exchange.dao.ExchangeLogDao;
import eu.europa.ec.fisheries.uvms.exchange.dao.ServiceRegistryDao;
import eu.europa.ec.fisheries.uvms.exchange.dao.UnsentMessageDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceCapability;
import eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry.ServiceSetting;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.exchange.service.bean.ExchangeEventLogCache;
import eu.europa.ec.fisheries.uvms.exchange.service.model.IncomingMovement;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ExchangeMessageConsumerBeanTest extends BuildExchangeServiceTestDeployment {

    JMSHelper jmsHelper;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    ServiceRegistryDao serviceRegistryDao;

    @Inject
    UnsentMessageDao unsentMessageDao;

    @Inject
    ExchangeEventLogCache exchangeEventLogCache;

    @Inject
    ExchangeLogDao exchangeLogDao;

    private Jsonb jsonb;

    @Before
    public void initialize() throws Exception {
        jmsHelper = new JMSHelper(connectionFactory);
        jsonb = JsonbBuilder.create();
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void worldsBestAndMostUsefullArquillianTest(){
        assertTrue(true);
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void pingExchangeTest() throws Exception{
        PingResponse response = jmsHelper.pingExchange();
        assertNotNull(response);
        assertEquals("pong", response.getResponse());
    }

    /* -- PLUGINS/SERVICES -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void listServiceTest() throws Exception{
        List<PluginType> inputList = new ArrayList<>();
        String request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList);  //Empty list ie get all

        String corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        TextMessage message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        GetServiceListResponse response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertEquals(1,response.getService().size());
        assertEquals("ManualMovement", response.getService().get(0).getServiceClassName());


        inputList.add(PluginType.SATELLITE_RECEIVER);

        request = ExchangeModuleRequestMapper.createGetServiceListRequest(inputList);         //have no SR in the db

        corrID = jmsHelper.sendExchangeMessage(request, null, "LIST_SERVICES");

        message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        response = JAXBMarshaller.unmarshallTextMessage(message, GetServiceListResponse.class);

        assertNotNull(response);
        assertTrue(response.getService().isEmpty());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailTest() throws Exception{
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createSetCommandSendEmailRequest(serviceClassName, email, null);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals(response.getCommand().getCommand(), CommandTypeType.EMAIL);
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());


    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandEmailPluginNotStartedTest() throws Exception{
        String serviceName = "Email Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.sweagencyemail";
        int sizeB4 = unsentMessageDao.getAll().size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.EMAIL);
        service.setStatus("STOPPED");
        service = serviceRegistryDao.updateService(service);

        EmailType email = new EmailType();
        email.setBody("Test body");
        email.setFrom("ExchangeTests@exchange.uvms");
        email.setTo("TestExecuter@exchange.uvms");
        email.setSubject("Test subject");

        String request = ExchangeModuleRequestMapper.createSetCommandSendEmailRequest(serviceClassName, email, null);

        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        SetCommandResponse response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandResponse.class);

        assertNotNull(response);
        assertEquals("Plugin to send command to is not started", response.getResponse().getMessage());
        assertEquals(AcknowledgeTypeType.NOK, response.getResponse().getType());

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());

        serviceRegistryDao.deleteEntity(service.getId());


    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setCommandPollTest() throws Exception{
        String serviceName = "Poll Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.inmarsat";
        int sizeB4 = unsentMessageDao.getAll().size();
        int eventLogSizeB4 = exchangeEventLogCache.size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        PollType pollType = new PollType();
        pollType.setPollTypeType(PollTypeType.POLL);
        pollType.setMessage("TestMessage");
        pollType.setPollId("Test Poll ID");
        KeyValueType keyValueType = new KeyValueType();
        keyValueType.setKey("CONNECT_ID");
        keyValueType.setValue("TestConnectID");
        pollType.getPollReceiver().add(keyValueType);

        keyValueType = new KeyValueType();
        keyValueType.setKey("LES");
        keyValueType.setValue("TestLESID");
        pollType.getPollReceiver().add(keyValueType);

        String request = ExchangeModuleRequestMapper.createSetCommandSendPollRequest(serviceClassName, pollType, "Test User",null);

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_COMMAND");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetCommandRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetCommandRequest.class);

        assertNotNull(response);
        assertEquals( CommandTypeType.POLL, response.getCommand().getCommand());
        assertEquals(response.getCommand().getPluginName(), serviceClassName);

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());
        assertEquals(eventLogSizeB4 + 1, exchangeEventLogCache.size());


        serviceRegistryDao.deleteEntity(service.getId());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void sendReportToPluginTest() throws Exception{
        String serviceName = "Flux Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.fluxus";
        String recipient = "To whom it may concern";
        int sizeB4 = unsentMessageDao.getAll().size();
        int eventLogSizeB4 = exchangeEventLogCache.size();
        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.FLUX);
        MovementType movementType = createMovementType();
        List<RecipientInfoType> recipientInfoTypeList = new ArrayList<>();

        String request = ExchangeModuleRequestMapper.createSendReportToPlugin(serviceClassName, PluginType.FLUX, Instant.now(), null, recipient, movementType, recipientInfoTypeList, movementType.getAssetName(),movementType.getIrcs(), movementType.getMmsi(), movementType.getExternalMarking(), movementType.getFlagState());

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "SEND_REPORT_TO_PLUGIN");
        TextMessage message = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetReportRequest response = JAXBMarshaller.unmarshallTextMessage(message, SetReportRequest.class);

         assertEquals(ReportTypeType.MOVEMENT, response.getReport().getType());
         assertEquals(movementType.getWkt(), response.getReport().getMovement().getWkt());
         assertEquals(recipient, response.getReport().getRecipient());

        Thread.sleep(1000);     //to allow the db to sync up
        assertEquals(sizeB4 + 1, unsentMessageDao.getAll().size());
        assertEquals(eventLogSizeB4 + 1, exchangeEventLogCache.size());


        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void updatePluginSettingTest() throws Exception{
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

        String request = ExchangeModuleRequestMapper.createUpdatePluginSettingRequest(serviceClassName, settingName,"Belgium", "Test User");

        jmsHelper.registerSubscriber("ServiceName = '" + serviceClassName + "'");
        String corrID = jmsHelper.sendExchangeMessage(request, null, "UPDATE_PLUGIN_SETTING");
        TextMessage topicMessage = (TextMessage)jmsHelper.listenOnEventBus("ServiceName = '" + serviceClassName + "'", 5000l);
        SetConfigRequest response = JAXBMarshaller.unmarshallTextMessage(topicMessage, SetConfigRequest.class);

        assertEquals(1, response.getConfigurations().getSetting().size());
        assertEquals(settingName, response.getConfigurations().getSetting().get(0).getKey());
        assertEquals("Belgium", response.getConfigurations().getSetting().get(0).getValue());

        TextMessage queueMessage = (TextMessage)jmsHelper.listenForResponseOnStandardQueue(corrID);
        UpdatePluginSettingResponse setCommandResponse = JAXBMarshaller.unmarshallTextMessage(queueMessage, UpdatePluginSettingResponse.class);

        assertEquals(AcknowledgeTypeType.OK, setCommandResponse.getResponse().getType());



        serviceRegistryDao.deleteEntity(service.getId());

    }

    /* -- MOVEMENT -- */

    @Test
    @OperateOnDeployment("exchangeservice")
    public void setMovementReportTest() throws Exception{
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";


        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String request = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType, "Test User", null, Instant.now(), null, PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(request, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage)jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);

        assertEquals("CREATE",message.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY));
        assertTrue(message.getStringProperty(MessageConstants.JMS_MESSAGE_GROUP).contains(movementType.getMmsi()));
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);
        assertEquals(movementType.getMmsi(), output.getAssetMMSI());
        assertEquals(movementType.getPosition().getLongitude(), output.getLongitude(),0);
        assertEquals(movementType.getFlagState(),output.getFlagState());


        Thread.sleep(1000);     //to allow the db to sync up
        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(output.getAckResponseMessageId());
        assertNotNull(exchangeLog);
        assertEquals(ExchangeLogStatusTypeType.ISSUED, exchangeLog.getStatus());
        assertEquals(TypeRefType.MOVEMENT,exchangeLog.getTypeRefType());
        serviceRegistryDao.deleteEntity(service.getId());
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    @Ignore
    public void receiveMovementReportBatchTest(){
        //Not part of swe uvms flow (since it sends stuff to rules instead of movement), ignoring
    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processedMovementAlarmTest() throws Exception{
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";


        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String setupRequest = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType, "Test User", null, Instant.now(), null, PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(setupRequest, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage)jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);

        MovementRefType movementRefType = new MovementRefType();
        movementRefType.setAckResponseMessageID(output.getAckResponseMessageId());
        movementRefType.setType(MovementRefTypeType.ALARM);
        String request = ExchangeModuleRequestMapper.mapToProcessedMovementResponse("Test username", movementRefType);

        corrID = jmsHelper.sendExchangeMessage(request, null, "PROCESSED_MOVEMENT");
        Thread.sleep(1000); //to let it work

        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(output.getAckResponseMessageId());
        assertEquals(ExchangeLogStatusTypeType.FAILED, exchangeLog.getStatus());

        serviceRegistryDao.deleteEntity(service.getId());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    public void processedMovementSuccessTest() throws Exception{
        String serviceName = "Iridium Test Service";
        String serviceClassName = "eu.europa.ec.fisheries.uvms.plugins.Iridium";


        Service service = createAndPersistBasicService(serviceName, serviceClassName, PluginType.SATELLITE_RECEIVER);

        MovementType movementType = createMovementType();
        SetReportMovementType setReportMovementType = new SetReportMovementType();
        setReportMovementType.setMovement(movementType);
        setReportMovementType.setTimestamp(Date.from(Instant.now()));
        setReportMovementType.setPluginType(PluginType.SATELLITE_RECEIVER);
        setReportMovementType.setPluginName(serviceClassName);
        String setupRequest = ExchangeModuleRequestMapper.createSetMovementReportRequest(setReportMovementType, "Test User", null, Instant.now(), null, PluginType.OTHER, "IRIDIUM", "OnValue?");

        String corrID = jmsHelper.sendExchangeMessage(setupRequest, null, "SET_MOVEMENT_REPORT");
        TextMessage message = (TextMessage)jmsHelper.listenOnQueue(MessageConstants.COMPONENT_MESSAGE_IN_QUEUE_NAME);
        IncomingMovement output = jsonb.fromJson(message.getText(), IncomingMovement.class);

        MovementRefType movementRefType = new MovementRefType();
        movementRefType.setAckResponseMessageID(output.getAckResponseMessageId());
        movementRefType.setType(MovementRefTypeType.MOVEMENT);
        String request = ExchangeModuleRequestMapper.mapToProcessedMovementResponse("Test username", movementRefType);

        corrID = jmsHelper.sendExchangeMessage(request, null, "PROCESSED_MOVEMENT");
        Thread.sleep(1000); //to let it work

        ExchangeLog exchangeLog = exchangeLogDao.getExchangeLogByGuid(output.getAckResponseMessageId());
        assertEquals(ExchangeLogStatusTypeType.SUCCESSFUL, exchangeLog.getStatus());

        serviceRegistryDao.deleteEntity(service.getId());

    }

    @Test
    @OperateOnDeployment("exchangeservice")
    @Ignore
    public void processedMovementBatchTest() {
        //Not part of swe uvms flow (since it sends stuff to rules instead of movement), ignoring
    }


    private Service createAndPersistBasicService(String name, String serviceClassName, PluginType pluginType) throws Exception{
        Service s = serviceRegistryDao.getServiceByServiceClassName(serviceClassName);
        if(s != null){
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
        s.setStatus("STARTED");
        s.setType(pluginType);
        s.setUpdated(Instant.now());
        s.setUpdatedBy("Exchange Tests");

        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
        ServiceCapability serviceCapability = new ServiceCapability();
        serviceCapability.setService(s);
        serviceCapability.setUpdatedBy("Exchange Tests");
        serviceCapability.setUpdatedTime(Instant.now());
        serviceCapability.setCapability(CapabilityTypeType.POLLABLE);
        serviceCapability.setValue("TRUE");
        serviceCapabilityList.add(serviceCapability);
        s.setServiceCapabilityList(serviceCapabilityList);

        s.setServiceSettingList(new ArrayList<>());

        return serviceRegistryDao.createEntity(s);
    }

    private MovementType createMovementType(){
        MovementType movementType = new MovementType();
        movementType.setCalculatedCourse(0.0);
        movementType.setCalculatedSpeed(5.5);
        movementType.setConnectId("TestConnectID");
        movementType.setGuid("TestMovementGuid");
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
        assetIdList.setValue("AssetGuid");
        assetId.getAssetIdList().add(assetIdList);

        assetIdList = new AssetIdList();
        assetIdList.setIdType(AssetIdType.MMSI);
        assetIdList.setValue(movementType.getMmsi());
        assetId.getAssetIdList().add(assetIdList);
        movementType.setAssetId(assetId);



        movementType.setSource(MovementSourceType.IRIDIUM);


        return movementType;
    }
}
