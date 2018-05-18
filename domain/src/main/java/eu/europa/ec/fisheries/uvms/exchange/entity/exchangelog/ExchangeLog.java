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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;

@Entity
@Table(name="log")
//@formatter:off
@NamedQueries({
  @NamedQuery(name = ExchangeConstants.LOG_BY_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.guid = :guid AND ((:typeRefType = null) OR log.typeRefType = :typeRefType))"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, query = "SELECT DISTINCT log FROM ExchangeLog log WHERE log.typeRefGuid IN (:refGuids)"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_REF_AND_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.typeRefGuid = :typeRefGuid AND ((:duplicate IS null) OR (duplicate = :duplicate)) AND log.typeRefType in (:typeRefTypes)")
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
	
	@Size(max=36)
	@Column(name="log_type_ref_guid")
	private String typeRefGuid;
	
	@Enumerated(EnumType.STRING)
	@Column(name="log_type_ref_type")
	private TypeRefType typeRefType;

	@Column(name = "log_type_ref_message")
	private String typeRefMessage;
	
	@NotNull
	@Size(max=36)
	@Column(name = "log_guid", unique=true)
	private String guid;
	
	@Column(name = "log_transfer_incoming")
	private Boolean transferIncoming;

	private Boolean duplicate;
	
	@NotNull
	@Column(name = "log_senderreceiver")
	private String senderReceiver;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_daterecieved")
	private Date dateReceived;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "log_status")
	private ExchangeLogStatusTypeType status;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_updattim")
	private Date updateTime;
	
	@NotNull
	@Size(max=60)
	@Column(name = "log_upuser")
	private String updatedBy;

	@OneToMany(mappedBy="log", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ExchangeLogStatus> statusHistory;

	@Size(max=50)
	@Column(name="log_recipient")
	private String recipient;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="log_fwddate")
	private Date fwdDate;

	@Size(max=50)
	@Column(name="log_fwdrule")
	private String fwdRule;

	@Size(max=50)
	@Column(name="log_source")
	private String source;

	@Size(max=50)
	@Column(name="log_destination")
	private String destination;

	@Size(max=36)
	@Column(name="log_mdc_request_id")
	private String mdcRequestId;

	@PrePersist
	public void prepersist() {
		setGuid(UUID.randomUUID().toString());
        Boolean dup = getDuplicate();
        if (dup == null){
            setDuplicate(false);
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

	public void setMDCRequestId(String mdcRequestId) {
		this.mdcRequestId = mdcRequestId;
	}

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }
}