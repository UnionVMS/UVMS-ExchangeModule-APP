package eu.europa.ec.fisheries.uvms.exchange.service.mockdata;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.v1.ServiceType;

public class MockData {

    /**
     * Get mocked data single object
     *
     * @param id
     * @return
     */
    public static ServiceType getDto(Long id) {
        ServiceType dto = new ServiceType();
        dto.setId(id.toString());
        return null;
    }

    /**
     * Get mocked data as a list
     *
     * @param amount
     * @return
     */
    public static List<ServiceType> getDtoList(Integer amount) {
        List<ServiceType> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(getDto(Long.valueOf(i)));
        }
        return null;
    }

}
