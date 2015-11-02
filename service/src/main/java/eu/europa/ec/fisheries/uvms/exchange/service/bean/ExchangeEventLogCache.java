package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExchangeEventLogCache {
	final static Logger LOG = LoggerFactory.getLogger(ExchangeEventLogCache.class);
	
	private ConcurrentHashMap<String, String> cache;
	
	@PostConstruct
	public void init() {
		cache = new ConcurrentHashMap<String, String>();
		//TODO set TTL on cached objects
	}
	
	public void put(String messageId, String logGuid) {
		LOG.info(".put( " + messageId + ", " + logGuid + ")");
		cache.put(messageId, logGuid);
	}

	public String acknowledged(String messageId) {
		LOG.info(".acknowledged( " + messageId + ")");
		return cache.remove(messageId);
	}
	

}
