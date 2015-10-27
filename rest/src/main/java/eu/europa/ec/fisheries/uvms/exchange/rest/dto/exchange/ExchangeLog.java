package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeLog {

	@XmlElement(required = true)
	private String id;
	@XmlElement(required = true)
	private boolean incoming;
	@XmlElement(required = true)
	private String dateRecieved;
	@XmlElement(required = true)
	private String senderRecipient;
	@XmlElement(required = true)
	private String source;
	@XmlElement(required = true)
	private String rule;
	@XmlElement(required = true)
	private String recipient;
	@XmlElement(required = true)
	private String dateFwd;
	@XmlElement(required = true)
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isIncoming() {
		return incoming;
	}
	public void setIncoming(boolean incoming) {
		this.incoming = incoming;
	}
	public String getDateRecieved() {
		return dateRecieved;
	}
	public void setDateRecieved(String dateRecieved) {
		this.dateRecieved = dateRecieved;
	}
	public String getSenderRecipient() {
		return senderRecipient;
	}
	public void setSenderRecipient(String from) {
		this.senderRecipient = from;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getDateFwd() {
		return dateFwd;
	}
	public void setDateFwd(String dateFwd) {
		this.dateFwd = dateFwd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
