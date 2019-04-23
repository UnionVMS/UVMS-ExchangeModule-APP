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
package eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.serviceregistry;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.constant.ExchangeConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "service")
@XmlRootElement
//@formatter:off
@NamedQueries({
    @NamedQuery(name = ExchangeConstants.SERVICE_FIND_ALL, query = "SELECT s FROM Service s WHERE s.active = true"),
    @NamedQuery(name = ExchangeConstants.SERVICE_FIND_BY_SERVICE_CLASS_NAME, query = "SELECT s FROM Service s WHERE s.serviceClassName = :serviceClassName"),
    @NamedQuery(name = ExchangeConstants.SERVICE_FIND_BY_TYPES, query = "SELECT s FROM Service s WHERE s.type IN :types")
})
//@formatter:on
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "serv_id")
    private Long id;

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

    @Size(max = 20)
    @Column(name = "serv_status")
    private String status;

    @Basic(optional = false)
    @NotNull
    @Column(name = "serv_updattim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "serv_upuser")
    private String updatedBy;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "service", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ServiceCapability> serviceCapabilityList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "service", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ServiceSetting> serviceSettingList;

    /*
     @OneToMany(mappedBy="service", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
     private List<ServiceParameterMapping> map;*/
    public Long  getId() {
        return id;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @XmlTransient
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceResponse() {
        return serviceResponse;
    }

    public void setServiceResponse(String serviceResponse) {
        this.serviceResponse = serviceResponse;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", serviceClassName='" + serviceClassName + '\'' +
                ", name='" + name + '\'' +
                ", serviceResponse='" + serviceResponse + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                ", type=" + type +
                ", satelliteType='" + satelliteType + '\'' +
                ", status='" + status + '\'' +
                ", updated=" + updated +
                ", updatedBy='" + updatedBy + '\'' +
                ", serviceCapabilityList=" + serviceCapabilityList +
                ", serviceSettingList=" + serviceSettingList +
                '}';
    }
}