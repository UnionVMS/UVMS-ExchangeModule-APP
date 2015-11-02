package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;

public class StatusLog {

	private String timestamp;
	private ExchangeLogStatusTypeType status;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}
	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}
}
