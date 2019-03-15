package eu.europa.ec.fisheries.uvms.exchange.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.List;

public class RestHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static <T> List<T> readResponseDtoList(String response, Class<T> clazz) throws Exception {
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseDto = jsonReader.readObject();
        JsonArray data = responseDto.getJsonArray("data");
        return objectMapper.readValue(data.toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    static <T> T readResponseDto(String response, Class<T> clazz) throws Exception {
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseDto = jsonReader.readObject();
        JsonObject data = responseDto.getJsonObject("data");
        return objectMapper.readValue(data.toString(), clazz);
    }
}
