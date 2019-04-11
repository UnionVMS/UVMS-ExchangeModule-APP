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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="log")
//@formatter:off
@NamedQueries({
  @NamedQuery(name = ExchangeLog.LOG_BY_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.id = :guid AND ((:typeRefType = null) OR (log.typeRefType = :typeRefType))"),
  @NamedQuery(name = ExchangeLog.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, query = "SELECT DISTINCT log FROM ExchangeLog log WHERE log.typeRefGuid IN (:refGuids)"),
  @NamedQuery(name = ExchangeLog.LOG_BY_TYPE_REF_AND_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.typeRefGuid = :typeRefGuid AND log.typeRefType in (:typeRefTypes)"),
	@NamedQuery(name = ExchangeLog.LATEST_LOG, query = "SELECT log FROM ExchangeLog log ORDER BY log.updateTime DESC")
})
//@formatter:on
public class ExchangeLog {

	public static final String LOG_BY_GUID = "Log.findByGuid";
	public static final String LOG_BY_TYPE_RANGE_OF_REF_GUIDS = "Log.findByRangeOfRefGuids";
	public static final String LOG_BY_TYPE_REF_AND_GUID = "Log.findByTypeRefGuid";
	public static final String LATEST_LOG = "Log.latestLog";

	@Id
	@Column(name="log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@Column(name="log_type")
	@Enumerated(EnumType.STRING)
	private LogType type;
	
	@Column(name="log_type_ref_guid")
	private UUID typeRefGuid;
	
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

	@Column(name = "log_transfer_incoming")
	private Boolean transferIncoming;

	@NotNull
	@Column(name = "log_senderreceiver")
	@Size(max=100)
	private String senderReceiver;

	@NotNull(message = "The dateReceived for the log cannot be empty!")
	@Column(name = "log_daterecieved")
	private Instant dateReceived;

	@NotNull(message = "The log_status field for the log cannot be empty!")
	@Enumerated(EnumType.STRING)
	@Column(name = "log_status")
	private ExchangeLogStatusTypeType status;

	@NotNull(message = "The log_updatetime field for the log cannot be empty!")
	@Column(name = "log_updatetime")
	private Instant updateTime;

	@NotNull(message = "The updatedBy field for the log cannot be empty!")
	@Size(max=100)
	@Column(name = "log_upuser")
	private String updatedBy;

	@OneToMany(mappedBy="log", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ExchangeLogStatus> statusHistory;

	@Size(max=100)
	@Column(name="log_recipient")
	private String recipient;

	@Column(name="log_fwddate")
	private Instant fwdDate;

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
	@PreUpdate
	public void updateTimestampOnCreateAndUpdate(){
		updateTime = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}


	public String getSenderReceiver() {
		return senderReceiver;
	}

	public void setSenderReceiver(String senderReceiver) {
		this.senderReceiver = senderReceiver;
	}

	public Instant getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Instant dateReceived) {
		this.dateReceived = dateReceived;
	}

	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}

	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Instant updateTime) {
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

	public UUID getTypeRefGuid() {
		return typeRefGuid;
	}

	public void setTypeRefGuid(UUID typeRefGuid) {
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

	public Instant getFwdDate() {
		return fwdDate;
	}

	public void setFwdDate(Instant fwdDate) {
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