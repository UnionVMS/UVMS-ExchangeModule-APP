/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.exchange.rest.dto.exchange;

import eu.europa.ec.fisheries.uvms.exchange.rest.constants.PollStatus;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author jojoha
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeLog {

    @XmlElement(required = true, type = Date.class)
    private Date dateRecieved;
    @XmlElement(required = true, type = String.class)
    private String sentBy;
    @XmlElement(required = true, type = String.class)
    private String message;
    @XmlElement(required = true, type = String.class)
    private String fwdRule;
    @XmlElement(required = true, type = String.class)
    private String recipient;
    @XmlElement(required = true, type = Date.class)
    private Date dateFwd;
    @XmlElement(required = true, type = PollStatus.class)
    private PollStatus status;

    public Date getDateRecieved() {
        return dateRecieved;
    }

    public void setDateRecieved(Date dateRecieved) {
        this.dateRecieved = dateRecieved;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFwdRule() {
        return fwdRule;
    }

    public void setFwdRule(String fwdRule) {
        this.fwdRule = fwdRule;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Date getDateFwd() {
        return dateFwd;
    }

    public void setDateFwd(Date dateFwd) {
        this.dateFwd = dateFwd;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

}
