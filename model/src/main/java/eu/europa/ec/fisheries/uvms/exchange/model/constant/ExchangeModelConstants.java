package eu.europa.ec.fisheries.uvms.exchange.model.constant;

public class ExchangeModelConstants {

    public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String PLUGIN_EVENTBUS = "java:/jms/topic/EventBus";
    public static final String EVENTBUS_NAME = "EventBus";
    
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";
    public static final String DESTINATION_TYPE_TOPIC = "javax.jms.Topic";

    public static final String EXCHANGE_MESSAGE_IN_QUEUE = "java:/jms/queue/UVMSExchangeEvent";
    public static final String EXCHANGE_MESSAGE_IN_QUEUE_NAME = "UVMSExchangeEvent";
    public static final String EXCHANGE_RESPONSE_QUEUE = "java:/jms/queue/UVMSExchange";
    public static final String QUEUE_DATASOURCE_INTERNAL = "java:/jms/queue/UVMSExchangeModel";

    public static final String EXCHANGE_REGISTER_SERVICE = "EXCHANGE_REGISTRY";
    public static final String SERVICE_NAME = "ServiceName";
    public static final String RESPONSE_TOPIC_ADDON_NAME = "PLUGIN_RESPONSE";

    public static final String MODULE_NAME = "exchange";
    
    public static final String QUEUE_INTEGRATION_RULES = "java:/jms/queue/UVMSRulesEvent";
}
