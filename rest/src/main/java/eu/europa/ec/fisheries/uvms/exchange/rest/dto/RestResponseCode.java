package eu.europa.ec.fisheries.uvms.exchange.rest.dto;

public enum RestResponseCode {
	OK(200),
	
	EXCHANGE_ERROR(501),
	
	INPUT_ERROR(511),
	MAPPING_ERROR(512),
	
	SERVICE_ERROR(521),
	MODEL_ERROR(522),
	DOMAIN_ERROR(523),
	
	UNAUTHORIZED(401),
	
    UNDEFINED_ERROR(500);

    private int code;
    
    private RestResponseCode(int code) {
    	this.code = code;
    }
    
    public int getCode() {
    	return code;
    }
}
