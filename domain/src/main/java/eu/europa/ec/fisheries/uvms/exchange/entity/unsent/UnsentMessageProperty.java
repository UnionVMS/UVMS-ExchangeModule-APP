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

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageTypePropertyKey;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@Table(name = "unsent_message_property")
@XmlRootElement
public class UnsentMessageProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="unsentprop_id")
    private UUID id;

    @JoinColumn(name = "unsentprop_unsent_id", referencedColumnName = "unsent_guid")
    @ManyToOne
    private UnsentMessage unsentMessage;

    @Enumerated(EnumType.STRING)
    @Column(name="unsentprop_key")
    private UnsentMessageTypePropertyKey key;

    @Column(name="unsentprop_value")
    private String value;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UnsentMessageTypePropertyKey getKey() {
        return key;
    }

    public void setKey(UnsentMessageTypePropertyKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UnsentMessage getUnsentMessage() {
        return unsentMessage;
    }

    public void setUnsentMessage(UnsentMessage unsentMessage) {
        this.unsentMessage = unsentMessage;
    }
}