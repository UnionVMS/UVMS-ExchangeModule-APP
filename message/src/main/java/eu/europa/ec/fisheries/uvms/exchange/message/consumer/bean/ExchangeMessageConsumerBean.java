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
package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.consumer.ExchangeMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;

@Stateless
public class ExchangeMessageConsumerBean implements ExchangeMessageConsumer, ConfigMessageConsumer {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeMessageConsumerBean.class);
    private final static long TIMEOUT = 30*1000; //TODO timeout

    private Queue responseQueue;
    private ConnectionFactory connectionFactory;

    @PostConstruct
    private void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        responseQueue = JMSUtils.lookupQueue(ExchangeModelConstants.EXCHANGE_RESPONSE_QUEUE);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public <T> T getMessage(String correlationId, Class type) throws ExchangeMessageException {
    	if (correlationId == null || correlationId.isEmpty()) {
    		LOG.error("[ No CorrelationID provided when listening to JMS message, aborting ]");
    		throw new ExchangeMessageException("No CorrelationID provided!");
    	}
    	
    	Connection connection=null;
        try {

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            T response = (T) session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);
            if (response == null) {
                throw new ExchangeMessageException("[ Timeout reached or message null in ExchangeMessageConsumerBean. ]");
            }

            return response;
        } catch (Exception e) {
            LOG.error("[ Error when getting message ] {}", e.getMessage());
            throw new ExchangeMessageException("Error when retrieving message: ");
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        }
        catch (ExchangeMessageException e) {
            LOG.error("[ Error when getting config message. ]", e.getMessage());
            throw new ConfigMessageException("[ Error when getting config message. ]");
        }
    }

}