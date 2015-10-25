package eu.europa.ec.fisheries.uvms.exchange.rest.mapper;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.rest.dto.Plugin;

public class ServiceMapper {

	public static List<Plugin> map(List<ServiceResponseType> serviceList) {
		List<Plugin> plugins = new ArrayList<>();
		if(serviceList != null) {
			for(ServiceResponseType service : serviceList) {
				Plugin plugin = new Plugin();
				plugin.setName(service.getName());
				plugin.setServiceClassName(service.getServiceClassName());
				plugin.setType(service.getPluginType().name());
				plugin.setStatus(service.getStatus().name());
				plugins.add(plugin);
			}
		}
		return plugins;
	}
}
