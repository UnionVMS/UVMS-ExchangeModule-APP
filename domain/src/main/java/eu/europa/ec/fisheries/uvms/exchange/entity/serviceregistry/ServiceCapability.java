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
package eu.europa.ec.fisheries.uvms.exchange.entity.serviceregistry;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;

@Entity
@Table(name = "service_capability")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = ExchangeConstants.CAPABILITY_FIND_BY_SERVICE, query = "SELECT s FROM ServiceCapability s where s.service.serviceClassName =:serviceClassName") })
public class ServiceCapability implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "servcap_id")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "servcap_updattim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "servcap_upuser")
    private String updatedBy;

    @NotNull
    @Size(max=25)
    @Column(name = "servcap_value")
    private String value;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "servcap_capability")
    private CapabilityTypeType capability;
    
    @JoinColumn(name = "servcap_serv_id", referencedColumnName = "serv_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Service service;

    public ServiceCapability() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CapabilityTypeType getCapability() {
		return capability;
	}

	public void setCapability(CapabilityTypeType capability) {
		this.capability = capability;
	}
}