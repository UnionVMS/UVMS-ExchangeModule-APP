package eu.europa.ec.fisheries.uvms.exchange;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.exchange.service.event.ServiceEvent;
import eu.europa.ec.fisheries.uvms.exchange.service.event.WebsocketEvent;

@Singleton
@ServerEndpoint("/exchange")
public class ExchangeWebSocket {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeWebSocket.class);

    @Inject
    @WebsocketEvent
    Event<ServiceEvent> event;

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    public void onServiceEvent(@Observes @WebsocketEvent ServiceEvent serviceEvent) {
    	for (Session peer : peers) {
			try {
				peer.getBasicRemote().sendText(serviceEvent.getMsg());
			} catch (IOException e) {
	            LOG.error("[ Error when sending message to websocket peer. ] {} ", e.getMessage());
			}
		}
    }

	@OnMessage
	public String onMessage(String message) {
		return "Hi, I am a web socket.";
	}

	@OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }
}
