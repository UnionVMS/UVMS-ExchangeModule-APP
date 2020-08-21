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
package eu.europa.ec.fisheries.uvms.exchange.service.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.exchange.service.domain.constant.ExchangeConstants;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.dao.Dao;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.dao.UnsentMessageDao;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.service.domain.exception.NoEntityFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class UnsentMessageDaoBean extends Dao implements UnsentMessageDao {
    final static Logger LOG = LoggerFactory.getLogger(UnsentMessageDaoBean.class);
    
	@Override
	public UnsentMessage create(UnsentMessage unsentMessage) throws ExchangeDaoException {
		try {
			em.persist(unsentMessage);
			return unsentMessage;
		}  catch (Exception e) {
			throw new ExchangeDaoException("Error when creating unsent message",e);
		}
	}

	@Override
	public UnsentMessage remove(UnsentMessage unsentMessage) throws ExchangeDaoException {
		try {
			em.remove(unsentMessage);
			return unsentMessage;
		}  catch (Exception e) {
			throw new ExchangeDaoException("Error when removing unsent message",e);
		}
	}

	@Override
	public List<UnsentMessage> getAll() throws ExchangeDaoException {
		try {
            TypedQuery<UnsentMessage> query = em.createNamedQuery(ExchangeConstants.UNSENT_FIND_ALL, UnsentMessage.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new ExchangeDaoException("Error when getting entity list.",e);
        }
	}

	@Override
	public UnsentMessage getByGuid(String guid) throws NoEntityFoundException {
		try {
            TypedQuery<UnsentMessage> query = em.createNamedQuery(ExchangeConstants.UNSENT_BY_GUID, UnsentMessage.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoEntityFoundException("Error when getting entity by ID. "+ guid,e);
        }
	}

}