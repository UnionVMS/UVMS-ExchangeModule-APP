package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SendingLog {

	@XmlElement(required = true)
	private String id;
	@XmlElement(required = true)
	private String dateRecieved;
	@XmlElement(required = true)
	private String from;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDateRecieved() {
		return dateRecieved;
	}
	public void setDateRecieved(String dateRecieved) {
		this.dateRecieved = dateRecieved;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
