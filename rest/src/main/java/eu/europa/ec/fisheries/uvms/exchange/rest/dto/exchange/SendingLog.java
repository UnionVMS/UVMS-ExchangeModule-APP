package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SendingLog {

	@XmlElement(required = true)
	private String messageId;
	@XmlElement(required = true)
	private String dateRecieved;
	@XmlElement(required = true)
	private String senderRecipient;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String id) {
		this.messageId = id;
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
	public void setSenderRecipient(String senderRecipient) {
		this.senderRecipient = senderRecipient;
	}
}
