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
package eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;

@Entity
@Table(name = "service")
//@formatter:off
@NamedQueries({
    @NamedQuery(name = Service.SERVICE_FIND_ALL, query = "SELECT s FROM Service s WHERE s.active = true ORDER BY s.updated ASC"),
    @NamedQuery(name = Service.SERVICE_FIND_BY_NAME, query = "SELECT s FROM Service s WHERE s.name = :name"),
    @NamedQuery(name = Service.SERVICE_FIND_BY_SERVICE_CLASS_NAME, query = "SELECT s FROM Service s WHERE s.serviceClassName = :serviceClassName"),
    @NamedQuery(name = Service.SERVICE_FIND_BY_TYPES, query = "SELECT s FROM Service s WHERE s.type IN :types"),
    @NamedQuery(name = Service.SERVICE_FIND_BY_CAPABILITY, query = "SELECT s FROM Service s JOIN s.serviceCapabilityList c WHERE c.capability = :capability and c.value = true")
})
//@formatter:on
public class Service implements Serializable {

    public static final String SERVICE_FIND_ALL = "Service.findAll";
    public static final String SERVICE_FIND_BY_NAME = "Service.findByServiceName";
    public static final String SERVICE_FIND_BY_SERVICE_CLASS_NAME = "Service.findByServiceClassName";
    public static final String SERVICE_FIND_BY_TYPES = "Service.findByTypes";
    public static final String SERVICE_FIND_BY_CAPABILITY = "Service.findByCapability";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "serv_id")
    private UUID id;

    @Size(max = 500)
    @Column(name = "serv_serviceclassname", unique = true)
    private String serviceClassName;

    @Size(max = 100)
    @Column(name = "serv_name", unique = true)
    private String name;

    @Size(max = 500)
    @Column(name = "serv_serviceresponse", unique = true)
    private String serviceResponse;

    @Size(max = 200)
    @Column(name = "serv_desc")
    private String description;

    @Column(name = "serv_active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "serv_type")
    private PluginType type;

    @Column(name = "serv_sat_type")
    private String satelliteType;

    @Column(name = "serv_status")
    private boolean status;

    @NotNull
    @Column(name = "serv_updatetime")
    private Instant updated;

    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "serv_upuser")
    private String updatedBy;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "service", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ServiceCapability> serviceCapabilityList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "service", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ServiceSetting> serviceSettingList;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<ServiceCapability> getServiceCapabilityList() {
        return serviceCapabilityList;
    }

    public void setServiceCapabilityList(List<ServiceCapability> serviceCapabilityList) {
        this.serviceCapabilityList = serviceCapabilityList;
    }

    public Service() {
    }

    public List<ServiceSetting> getServiceSettingList() {
        return serviceSettingList;
    }

    public void setServiceSettingList(List<ServiceSetting> serviceSettingList) {
        this.serviceSettingList = serviceSettingList;
    }

    public PluginType getType() {
        return type;
    }

    public void setType(PluginType type) {
        this.type = type;
    }

    public String getSatelliteType() {
        return satelliteType;
    }

    public void setSatelliteType(String satelliteType) {
        this.satelliteType = satelliteType;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getServiceResponse() {
        return serviceResponse;
    }

    public void setServiceResponse(String serviceResponse) {
        this.serviceResponse = serviceResponse;
    }

}