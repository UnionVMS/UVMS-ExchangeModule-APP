package eu.europa.ec.fisheries.uvms.exchange;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.notifications.NotificationEvent;
import eu.europa.ec.fisheries.uvms.notifications.NotificationMessage;
import eu.europa.ec.fisheries.uvms.notifications.NotificationUtils;

@Singleton
@ServerEndpoint("/activity")
public class ExchangeWebSocket {

    final static Logger LOG = LoggerFactory.getLogger(ExchangeWebSocket.class);

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    public void onServiceEvent(@Observes @NotificationEvent NotificationMessage notificationMessage) {
    	for (Session peer : peers) {
			try {
				peer.getBasicRemote().sendText(NotificationUtils.getTextMessage(notificationMessage));
			} catch (IOException e) {
	            LOG.error("[ Error when sending message to websocket peer. ] {} ", e.getMessage());
			}
		}
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
