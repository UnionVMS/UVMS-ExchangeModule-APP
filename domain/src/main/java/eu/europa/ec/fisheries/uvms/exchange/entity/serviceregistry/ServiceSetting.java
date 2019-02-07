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

import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;

@Entity
@Table(name = "service_setting")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = ExchangeConstants.SETTING_FIND_BY_SERVICE, query = "SELECT s FROM ServiceSetting s where s.service.serviceClassName =:serviceClassName ") })
public class ServiceSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "servset_id")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "servset_updattim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "servset_upuser")
    private String updatedBy;

    @Size(max = 500)
    @Column(name = "servset_setting")
    private String setting;

    @JoinColumn(name = "servset_serv_id", referencedColumnName = "serv_id")
    @ManyToOne
    private Service service;

    @Size(max = 4000)
    @Column(name = "servset_value")
    private String value;

    public ServiceSetting() {
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

    public String getUser() {
        return updatedBy;
    }

    public void setUser(String user) {
        this.updatedBy = user;
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

	public String getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}



}