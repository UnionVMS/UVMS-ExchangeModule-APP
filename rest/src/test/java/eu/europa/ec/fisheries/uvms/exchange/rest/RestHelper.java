package eu.europa.ec.fisheries.uvms.exchange.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.Service;
import eu.europa.ec.fisheries.uvms.exchange.service.entity.serviceregistry.ServiceCapability;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RestHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> List<T> readResponseDtoList(String response, Class<T> clazz) throws Exception {
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseDto = jsonReader.readObject();
        JsonArray data = responseDto.getJsonArray("data");
        return objectMapper.readValue(data.toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static <T> T readResponseDto(String response, Class<T> clazz) throws Exception {
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseDto = jsonReader.readObject();
        JsonObject data = responseDto.getJsonObject("data");
        return objectMapper.readValue(data.toString(), clazz);
    }


    public static Service createBasicService(String name, String serviceClassName, PluginType pluginType){

        Service s = new Service();
        s.setActive(true);
        s.setDescription("Test description");
        s.setName(name);
        s.setSatelliteType(null);
        s.setServiceClassName(serviceClassName);
        s.setServiceResponse(serviceClassName + "PLUGIN_RESPONSE");
        s.setStatus(true);
        s.setType(pluginType);
        s.setUpdated(Instant.now());
        s.setUpdatedBy("Exchange Tests");

        List<ServiceCapability> serviceCapabilityList = new ArrayList<>();
        ServiceCapability serviceCapability = new ServiceCapability();
        serviceCapability.setService(s);
        serviceCapability.setUpdatedBy("Exchange Tests");
        serviceCapability.setUpdatedTime(Instant.now());
        serviceCapability.setCapability(CapabilityTypeType.POLLABLE);
        serviceCapability.setValue(true);
        serviceCapabilityList.add(serviceCapability);
        s.setServiceCapabilityList(serviceCapabilityList);

        s.setServiceSettingList(new ArrayList<>());

        return s;
    }
}
