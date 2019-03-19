package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeBaseRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.PingResponse;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.ExchangeModelConstants;
import eu.europa.ec.fisheries.uvms.movement.model.mapper.JAXBMarshaller;

import javax.jms.*;
import java.util.Enumeration;

public class JMSHelper {

    private static final long TIMEOUT = 20000;
    public static final String EXCHANGE_QUEUE = "UVMSExchangeEvent";
    public static final String RESPONSE_QUEUE = "IntegrationTestsResponseQueue";

    private final ConnectionFactory connectionFactory;
    MessageConsumer subscriber;
    Topic eventBus;
    Session session;

    public JMSHelper(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public PingResponse pingExchange() throws Exception {
        ExchangeBaseRequest request = new PingRequest();
        request.setMethod(ExchangeModuleMethod.PING);
        String pingRequest = JAXBMarshaller.marshallJaxBObjectToString(request);
        String correlationId = sendExchangeMessage(pingRequest, null, null);
        Message response = listenForResponseOnStandardQueue(correlationId);
        return JAXBMarshaller.unmarshallTextMessage((TextMessage) response, PingResponse.class);
    }

    public void registerSubscriber(String selector) throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        eventBus = session.createTopic("EventBus");
        subscriber = session.createConsumer(eventBus, selector, true);
    }

    public String sendMessageOnEventQueue(String text) throws Exception{
        Connection connection = connectionFactory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic eventTopic = session.createTopic(MessageConstants.EVENT_BUS_TOPIC_NAME);


            TextMessage message = session.createTextMessage();
            message.setStringProperty(ExchangeModelConstants.SERVICE_NAME, ExchangeModelConstants.EXCHANGE_REGISTER_SERVICE);
            message.setText(text);

            session.createProducer(eventTopic).send(message);

            return message.getJMSMessageID();
        } finally {
            connection.close();
        }
    }

    public String sendExchangeMessage(String text, String groupId, String function) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);
            Queue exchangeQueue = session.createQueue(EXCHANGE_QUEUE);

            TextMessage message = session.createTextMessage();
            message.setStringProperty("JMSXGroupID", groupId);
            message.setStringProperty("FUNCTION", function);
            message.setText(text);
            message.setJMSReplyTo(responseQueue);

            session.createProducer(exchangeQueue).send(message);

            return message.getJMSMessageID();
        } finally {
            connection.close();
        }
    }

    public Message listenForResponseOnStandardQueue(String correlationId) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(RESPONSE_QUEUE);

            return session.createConsumer(responseQueue, "JMSCorrelationID='" + correlationId + "'")
                    .receive(TIMEOUT);
        } finally {
            connection.close();
        }
    }

    public Message listenOnEventBus(String selector, Long timeoutInMillis) throws Exception {

        try {
            return subscriber.receive(timeoutInMillis);
        } finally {
            subscriber.close();
        }
    }

    public Message listenOnQueue(String queue) throws Exception {
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(queue);

            return session.createConsumer(responseQueue)
                    .receive(TIMEOUT);
        } finally {
            connection.close();
        }
    }

    public int checkQueueSize(String queue) throws Exception {
        int messages = 0;
        Connection connection = connectionFactory.createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(queue);

            QueueBrowser browser = session.createBrowser(responseQueue);

            Enumeration enumeration = browser.getEnumeration();
            while(enumeration.hasMoreElements()) {
                enumeration.nextElement();
                messages++;
            }
        } finally {
            connection.close();
        }
        return messages;
    }

    public void clearQueue(String queue) throws Exception {
        Connection connection = connectionFactory.createConnection();
        MessageConsumer consumer;
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue responseQueue = session.createQueue(queue);
            consumer = session.createConsumer(responseQueue);

            while (consumer.receive(10L) != null);
        } finally {
            connection.close();
        }
    }
}