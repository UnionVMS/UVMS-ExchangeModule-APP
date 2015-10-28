package eu.europa.ec.fisheries.uvms.exchange.model.constant;

public enum FaultCode {
	EXCHANGE_MESSAGE(3700),
	EXCHANGE_TOPIC_MESSAGE(3701),
	EXCHANGE_EVENT_SERVICE(3201),
	
	EXCHANGE_MODEL_EXCEPTION(3800),
	EXCHANGE_MAPPER(3810),
	EXCHANGE_MARSHALL_EXCEPTION(3811),
	
	EXCHANGE_COMMAND_INVALID(3120),
	EXCHANGE_PLUGIN_INVALID(3205),
	
	//Exchange Plugin Fault Codes
	EXCHANGE_PLUGIN_EVENT(3200),
	PLUGIN_VALIDATION(3220);
	
	private final int code;
	
	private FaultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
