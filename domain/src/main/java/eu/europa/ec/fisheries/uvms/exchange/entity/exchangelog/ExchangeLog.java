/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="log")
//@formatter:off
@NamedQueries({
  @NamedQuery(name = ExchangeConstants.LOG_BY_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.guid = :guid AND ((:typeRefType = null) OR (log.typeRefType = :typeRefType))"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, query = "SELECT DISTINCT log FROM ExchangeLog log WHERE log.typeRefGuid IN (:refGuids)"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_REF_AND_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.typeRefGuid = :typeRefGuid AND log.typeRefType in (:typeRefTypes)")
})
//@formatter:on
public class ExchangeLog {

	@Id
	@Column(name="log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="log_type")
	@Enumerated(EnumType.STRING)
	private LogType type;
	
	@Column(name="log_type_ref_guid")
	private String typeRefGuid;
	
	@Enumerated(EnumType.STRING)
	@Column(name="log_type_ref_type")
	private TypeRefType typeRefType;

	@Column(name = "log_type_ref_message")
	private String typeRefMessage;

	@Column(name = "log_to")
	private String to;

    @Column(name = "log_df")
    private String df;

    @Column(name = "log_todt")
	private String todt;

	@Column(name = "log_on")
	private String on;

	@NotNull(message = "The Guid for the log cannot be empty!")
	@Size(max=100)
	@Column(name = "log_guid", unique=true)
	private String guid;
	
	@Column(name = "log_transfer_incoming")
	private Boolean transferIncoming;

	@NotNull
	@Column(name = "log_senderreceiver")
	@Size(max=100)
	private String senderReceiver;

	@NotNull(message = "The dateReceived for the log cannot be empty!")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_daterecieved")
	private Date dateReceived;

	@NotNull(message = "The log_status field for the log cannot be empty!")
	@Enumerated(EnumType.STRING)
	@Column(name = "log_status")
	private ExchangeLogStatusTypeType status;

	@NotNull(message = "The log_updattim field for the log cannot be empty!")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_updattim")
	private Date updateTime;

	@NotNull(message = "The updatedBy field for the log cannot be empty!")
	@Size(max=100)
	@Column(name = "log_upuser")
	private String updatedBy;

	@OneToMany(mappedBy="log", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ExchangeLogStatus> statusHistory;

	@Size(max=100)
	@Column(name="log_recipient")
	private String recipient;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="log_fwddate")
	private Date fwdDate;

	@Size(max=100)
	@Column(name="log_fwdrule")
	private String fwdRule;

	@Size(max=100)
	@Column(name="log_source")
	private String source;

	@Size(max=100)
	@Column(name="log_destination")
	private String destination;

	@Size(max=36)
	@Column(name="log_mdc_request_id")
	private String mdcRequestId;

	@Column(name = "log_business_error")
	private String businessError;

	@PrePersist
	public void prepersist() {
		if(StringUtils.isEmpty(guid)){
			setGuid(UUID.randomUUID().toString());
		}
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getSenderReceiver() {
		return senderReceiver;
	}

	public void setSenderReceiver(String senderReceiver) {
		this.senderReceiver = senderReceiver;
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}

	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Boolean getTransferIncoming() {
		return transferIncoming;
	}

	public void setTransferIncoming(Boolean transferIncoming) {
		this.transferIncoming = transferIncoming;
	}

	public List<ExchangeLogStatus> getStatusHistory() {
		return statusHistory;
	}

	public void setStatusHistory(List<ExchangeLogStatus> statusHistory) {
		this.statusHistory = statusHistory;
	}

	public String getTypeRefGuid() {
		return typeRefGuid;
	}

	public void setTypeRefGuid(String typeRefGuid) {
		this.typeRefGuid = typeRefGuid;
	}

	public TypeRefType getTypeRefType() {
		return typeRefType;
	}

	public void setTypeRefType(TypeRefType typeRefType) {
		this.typeRefType = typeRefType;
	}

	public String getTypeRefMessage() {
		return typeRefMessage;
	}

	public void setTypeRefMessage(String typeRefMessage) {
		this.typeRefMessage = typeRefMessage;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Date getFwdDate() {
		return fwdDate;
	}

	public void setFwdDate(Date fwdDate) {
		this.fwdDate = fwdDate;
	}

	public String getFwdRule() {
		return fwdRule;
	}

	public void setFwdRule(String fwdRule) {
		this.fwdRule = fwdRule;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

    public String getTo() {
        return to;
    }

	public void setTo(String to) {
		this.to = to;
	}

	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}

	public String getTodt() {
		return todt;
	}

	public void setTodt(String todt) {
		this.todt = todt;
	}

    public String getDf() {
        return df;
    }

    public void setDf(String df) {
        this.df = df;
    }

	public String getMdcRequestId() {
		return mdcRequestId;
	}

	public void setMdcRequestId(String mdcRequestId) {
		this.mdcRequestId = mdcRequestId;
	}

    public String getBusinessError() {
        return businessError;
    }

    public void setBusinessError(String businessError) {
        this.businessError = businessError;
    }
}