package eu.europa.ec.fisheries.uvms.exchange.message.producer.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.exchange.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.event.carrier.ExchangeMessageEvent;
import eu.europa.ec.fisheries.uvms.exchange.message.exception.ExchangeMessageException;
import eu.europa.ec.fisheries.uvms.exchange.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

@Stateless
public class MessageProducerBean implements MessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    @Resource(mappedName = ExchangeModelConstants.QUEUE_DATASOURCE_INTERNAL)
    private Queue localDbQueue;

    @Resource(mappedName = ExchangeModelConstants.EXCHANGE_RESPONSE_QUEUE)
    private Queue responseQueue;

    @Resource(mappedName = ExchangeModelConstants.EVENTBUS)
    private Topic eventBus;

    @Resource(lookup = ExchangeModelConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = ExchangeModelConstants.CONFIG_MESSAGE_IN_QUEUE)
    private Queue configQueue;

    private Connection connection = null;
    private Session session = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, DataSourceQueue queue) throws ExchangeMessageException {
        try {
            connectJMS();
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
                case INTERNAL:
                    session.createProducer(localDbQueue).send(message);
                    break;
                default:
                    break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] {0}", e.getMessage());
            throw new ExchangeMessageException("[ Error when sending message. ]", e);
        } finally {
            disconnectJMS();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendEventBusMessage(String text, String serviceName) throws ExchangeMessageException {
        try {
            connectJMS();
            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, serviceName);

            session.createProducer(eventBus).send(message);

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] {0}", e.getMessage());
            throw new ExchangeMessageException("[ Error when sending message. ]", e);
        } finally {
            disconnectJMS();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String text) throws ExchangeMessageException {
        try {
            connectJMS();
            TextMessage message = session.createTextMessage();
            message.setText(text);
            message.setJMSReplyTo(responseQueue);
            session.createProducer(configQueue).send(message);
            return message.getJMSMessageID();
        }
        catch (Exception e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ExchangeMessageException("Error when sending config message.", e);
        }
        finally {
            disconnectJMS();
        }
    }

    private void connectJMS() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    private void disconnectJMS() {
        try {
            connection.stop();
            connection.close();
        } catch (JMSException e) {
            LOG.error("[ Error when stopping or closing JMS queue. ] {}", e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @ErrorEvent ExchangeMessageEvent message) {
        try {
            connectJMS();
            LOG.debug("Sending error message back from Exchange module to recipient om JMS Queue with correlationID: {} ", message
                    .getJmsMessage().getJMSMessageID());

            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getErrorFault());

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            session.createProducer(message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (ExchangeModelMapperException | JMSException e) {
            LOG.error("Error when returning Error message to recipient", e.getMessage());
        } finally {
            disconnectJMS();
        }
    }

    @Override
    public void sendModuleResponseMessage(TextMessage message, String text) {
        try {
            LOG.info("Sending message back to recipient from ExchangeModule with correlationId {} on queue: {}", message.getJMSMessageID(),
                    message.getJMSReplyTo());
            connectJMS();
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            session.createProducer(message.getJMSReplyTo()).send(response);
        } catch (JMSException e) {
            LOG.error("[ Error when returning module exchange request. ]");
        } finally {
            disconnectJMS();
        }
    }

}
