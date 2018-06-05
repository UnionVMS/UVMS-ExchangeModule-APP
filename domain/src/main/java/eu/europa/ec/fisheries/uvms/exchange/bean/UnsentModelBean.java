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
package eu.europa.ec.fisheries.uvms.exchange.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.UnsentMessageType;
import eu.europa.ec.fisheries.uvms.exchange.dao.UnsentMessageDao;
import eu.europa.ec.fisheries.uvms.exchange.entity.unsent.UnsentMessage;
import eu.europa.ec.fisheries.uvms.exchange.exception.ExchangeDaoException;
import eu.europa.ec.fisheries.uvms.exchange.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.exchange.mapper.UnsentMessageMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.exchange.UnsentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class UnsentModelBean implements UnsentModel {

    final static Logger LOG = LoggerFactory.getLogger(UnsentModelBean.class);

    @EJB
    UnsentMessageDao dao;

    @Override
    public String createMessage(UnsentMessageType message, String username) throws ExchangeModelException {
        if (message == null) {
            throw new InputArgumentException("No message to create");
        }

        try {
            UnsentMessage entity = UnsentMessageMapper.toEntity(message, username);
            UnsentMessage persistedEntity = dao.create(entity);
            UnsentMessageType model = UnsentMessageMapper.toModel(persistedEntity);
            return model.getMessageId();
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when creating unsent message ] {}", ex.getMessage());
            throw new ExchangeModelException("Error when creating unsent message ");
        }
    }

    @Override
    public String removeMessage(String unsentMessageId) throws ExchangeModelException {
        if (unsentMessageId == null) {
            throw new InputArgumentException("No message to remove");
        }

        try {
            UnsentMessage entity = dao.getByGuid(unsentMessageId);
            if (entity != null) {
                String guid = entity.getGuid();
                dao.remove(entity);
                return guid;
            } else {
                LOG.error("[ Error when removing unsent message ]");
                throw new ExchangeModelException("Error when removing unsent message ");
            }
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when creating unsent message ] {}", ex.getMessage());
            throw new ExchangeModelException("Error when creating unsent message ");
        }
    }

    @Override
    public List<UnsentMessageType> getMessageList() throws ExchangeModelException {
        try {
            List<UnsentMessage> list = dao.getAll();
            return UnsentMessageMapper.toModel(list);
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when getting unsent message list ] {}", ex.getMessage());
            throw new ExchangeModelException("Error when getting unsent message list");
        }
    }

    @Override
    public List<UnsentMessageType> resend(List<String> unsentMessageId) throws ExchangeModelException {
        if (unsentMessageId == null) {
            throw new InputArgumentException("No messageList to resend");
        }

        try {
            List<UnsentMessageType> unsentMessageList = new ArrayList<>();
            for (String messageId : unsentMessageId) {
                try {
                    UnsentMessage message = dao.getByGuid(messageId);
                    UnsentMessage removedMessage = dao.remove(message);
                    unsentMessageList.add(UnsentMessageMapper.toModel(removedMessage));
                } catch (NoEntityFoundException e) {
                    LOG.error("Couldn't find message to resend with guid: " + messageId);
                }
            }
            return unsentMessageList;
        } catch (ExchangeDaoException ex) {
            LOG.error("[ Error when resending message message list ] {}", ex.getMessage());
            throw new ExchangeModelException("Error when resending unsent message list");
        }
    }
}