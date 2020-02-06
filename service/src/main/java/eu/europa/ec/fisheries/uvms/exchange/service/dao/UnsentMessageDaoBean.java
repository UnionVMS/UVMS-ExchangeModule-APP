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
package eu.europa.ec.fisheries.uvms.exchange.service.dao;

import eu.europa.ec.fisheries.uvms.exchange.service.entity.unsent.UnsentMessage;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
public class UnsentMessageDaoBean extends AbstractDao {

    public UnsentMessage create(UnsentMessage unsentMessage) {
        em.persist(unsentMessage);
        return unsentMessage;
    }

    public UnsentMessage remove(UnsentMessage unsentMessage) {
        em.remove(unsentMessage);
        return unsentMessage;
    }

    public List<UnsentMessage> getAll() {
        TypedQuery<UnsentMessage> query = em.createNamedQuery(UnsentMessage.UNSENT_FIND_ALL, UnsentMessage.class);
        return query.getResultList();
    }

    public UnsentMessage getByGuid(UUID guid) {
        return em.find(UnsentMessage.class, guid);
    }
}
