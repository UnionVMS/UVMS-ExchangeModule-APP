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
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.concurrent.ConcurrentHashMap;

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

	String acknowledged(String messageId) {
		LOG.info(".acknowledged( " + messageId + ")");
		return cache.remove(messageId);
	}
	

}