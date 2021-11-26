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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.RecipientInfoType;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.wsdl.user.types.Channel;
import eu.europa.ec.fisheries.wsdl.user.types.EndPoint;
import eu.europa.ec.fisheries.wsdl.user.types.Organisation;

@Stateless
public class ExchangeUserService {

    @Resource(name = "java:global/user_endpoint")
    private String userEndpoint;
    
    public List<RecipientInfoType> getRecipientInfoType(Organisation organisation) {
        List<RecipientInfoType> recipientInfoList = new ArrayList<>();
        List<EndPoint> endPoints = organisation.getEndPoints();
        for (EndPoint endPoint : endPoints) {
            for (Channel channel : endPoint.getChannels()) {
                RecipientInfoType recipientInfo = new RecipientInfoType();
                recipientInfo.setKey(channel.getDataFlow());
                recipientInfo.setValue(endPoint.getUri());
                recipientInfoList.add(recipientInfo);
            }
        }
        return recipientInfoList;
    }
    
    public Organisation getOrganisation(String organisationName) {
        return getWebTarget()
            .path("getOrganisation")
            .queryParam("organisationName", organisationName)
            .request(MediaType.APPLICATION_JSON)
            .get(Organisation.class);
    }

    private WebTarget getWebTarget() {
        Client client = ClientBuilder.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        client.register(JsonBConfigurator.class);
        return client.target(userEndpoint + "/user");
    }
}
