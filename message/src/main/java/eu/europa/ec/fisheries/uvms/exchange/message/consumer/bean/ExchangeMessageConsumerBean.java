package eu.europa.ec.fisheries.uvms.exchange.message.consumer.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

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
    final static int ONE_MINUTE = 60000;

    @Resource(mappedName = ExchangeModelConstants.EXCHANGE_RESPONSE_QUEUE)
    private Queue responseQueue;

    @Resource(lookup = ExchangeModelConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    private Connection connection = null;
    private Session session = null;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public <T> T getMessage(String correlationId, Class type) throws ExchangeMessageException {
        try {

            if (correlationId == null || correlationId.isEmpty()) {
                LOG.error("[ No CorrelationID provided when listening to JMS message, aborting ]");
                throw new ExchangeMessageException("No CorrelationID provided!");
            }
            connectToQueue();

            T response = (T) session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'").receive(5000);
            if (response == null) {
                throw new ExchangeMessageException("[ No response from module. ]");
            }

            return response;

        } catch (Exception e) {
            LOG.error("[ Error when getting message ] {}", e.getMessage());
            throw new ExchangeMessageException("Error when retrieving message: ");
        } finally {
            try {
                connection.stop();
                connection.close();
            } catch (JMSException e) {
                LOG.error("[ Error when closing JMS connection ] {}", e.getMessage());
                throw new ExchangeMessageException("Error closing JMS connection");
            }
        }
    }

    private void connectToQueue() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
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
