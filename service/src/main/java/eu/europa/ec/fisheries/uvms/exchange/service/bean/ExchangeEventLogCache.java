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
	}
	
	public void put(String messageId, String logGuid) {
		//System.out.println("Wants to put. " + messageId + ", " + logGuid);
		//System.out.println("Size before: " + cache.size());
		cache.put(messageId, logGuid);
		//System.out.println("Size after: " + cache.size());
	}

	public String acknowledged(String messageId) {
		//System.out.println(messageId + " acknowledged. Size before: " + cache.size());
		return cache.remove(messageId);
	}
	
	//TODO set TTL on cached objects
}
