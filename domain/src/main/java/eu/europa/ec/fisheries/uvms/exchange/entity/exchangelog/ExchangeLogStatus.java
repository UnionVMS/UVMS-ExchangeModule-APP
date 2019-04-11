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


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="log_status")
public class ExchangeLogStatus {

	@Id
	@Column(name="logstatus_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="logstatus_status")
	private ExchangeLogStatusTypeType status;
	
	@Column(name="logstatus_timestamp")
	private Instant statusTimestamp;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="logstatus_log_id")
	private ExchangeLog log;

	@Column(name="logstatus_updatetime")
	private Instant updateTime;
	
	@Size(max=60)
	@Column(name="logstatus_upuser")
	private String updatedBy;

	@PreUpdate
	@PrePersist
	public void preUpdate(){
		updateTime = Instant.now();
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}

	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}

	public Instant getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Instant statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

	public ExchangeLog getLog() {
		return log;
	}

	public void setLog(ExchangeLog log) {
		this.log = log;
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
	
}