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
package eu.europa.ec.fisheries.uvms.exchange.entity.unsent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="unsent_message")
//@formatter:off
@NamedQueries({
	@NamedQuery(name = UnsentMessage.UNSENT_FIND_ALL, query = "SELECT um FROM UnsentMessage um"),
	@NamedQuery(name = UnsentMessage.UNSENT_BY_GUID, query = "SELECT um FROM UnsentMessage um WHERE um.guid = :guid")
})
//@formatter:on
public class UnsentMessage {

	public static final String UNSENT_FIND_ALL = "UnsentMessage.findAll";
	public static final String UNSENT_BY_GUID = "UnsentMessage.findByGuid";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="unsent_guid")
	private UUID guid;
	
	@Size(max=100)
	@Column(name="unsent_senderreceiver")
	private String senderReceiver;
	
	@Size(max=100)
	@Column(name="unsent_recipient")
	private String recipient;
	
	@NotNull
	@Column(name="unsent_datereceived")
	private Instant dateReceived;
	
	@NotNull
	@Size(max=8192)
	@Column(name="unsent_message")
	private String message;
	
	@Column(name="unsent_updattim")
	private Instant updateTime;
	
	@Size(max=60)
	@Column(name="unsent_upuser")
	private String updatedBy;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY, mappedBy = "unsentMessage")
    private List<UnsentMessageProperty> properties;

	public UUID getGuid() {
		return guid;
	}

	public void setGuid(UUID guid) {
		this.guid = guid;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Instant getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Instant dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getSenderReceiver() {
		return senderReceiver;
	}

	public void setSenderReceiver(String senderReceiver) {
		this.senderReceiver = senderReceiver;
	}

    public List<UnsentMessageProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<UnsentMessageProperty> properties) {
        this.properties = properties;
    }
}