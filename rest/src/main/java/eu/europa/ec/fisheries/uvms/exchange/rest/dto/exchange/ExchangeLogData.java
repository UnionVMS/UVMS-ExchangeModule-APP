package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import eu.europa.ec.fisheries.schema.exchange.v1.LogType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeLogData {

	@XmlElement(required = true)
	private String guid;
	
	@XmlElement(required = true)
	private LogType type;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}
}
