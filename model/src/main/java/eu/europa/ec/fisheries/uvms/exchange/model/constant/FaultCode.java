package eu.europa.ec.fisheries.uvms.exchange.model.constant;

public enum FaultCode {
	EXCHANGE_MESSAGE(3700);
	
	private final int code;
	
	private FaultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
