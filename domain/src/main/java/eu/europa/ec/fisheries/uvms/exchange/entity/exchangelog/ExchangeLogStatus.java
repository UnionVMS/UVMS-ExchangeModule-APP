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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;

@Entity
@Table(name="log_status")
public class ExchangeLogStatus {

	@Id
	@Column(name="logstatus_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="logstatus_status")
	private ExchangeLogStatusTypeType status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="logstatus_timestamp")
	private Date statusTimestamp;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="logstatus_log_id")
	private ExchangeLog log;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="logstatus_updattim")
	private Date updateTime;
	
	@Size(max=60)
	@Column(name="logstatus_upuser")
	private String updatedBy;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ExchangeLogStatusTypeType getStatus() {
		return status;
	}

	public void setStatus(ExchangeLogStatusTypeType status) {
		this.status = status;
	}

	public Date getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Date statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

	public ExchangeLog getLog() {
		return log;
	}

	public void setLog(ExchangeLog log) {
		this.log = log;
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
	
}