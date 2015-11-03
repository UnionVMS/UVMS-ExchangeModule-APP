package eu.europa.ec.fisheries.uvms.exchange.rest.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PollQuery {

	@XmlElement(required=true)
	private String statusFromDate;
	
	@XmlElement(required=true)
	private String statusToDate;

	@XmlElement(required=true)
	private ExchangeLogStatusTypeType status;
	
	public String getStatusFromDate() {
		return statusFromDate;
	}

	public void setStatusFromDate(String statusFromDate) {
		this.statusFromDate = statusFromDate;
	}

	public String getStatusToDate() {
		return statusToDate;
	}

	public void setStatusToDate(String statusToDate) {
		this.statusToDate = statusToDate;
	}

	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}

	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}
}
