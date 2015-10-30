package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class ExchangeEventLogCache {

	private ConcurrentHashMap<String, String> cache;
	
	@PostConstruct
	public void init() {
		cache = new ConcurrentHashMap<String, String>();
		//TODO set TTL on cached objects
	}
	
	public void put(String messageId, String logGuid) {
		cache.put(messageId, logGuid);
	}

	public String acknowledged(String messageId) {
		return cache.remove(messageId);
	}
	

}
