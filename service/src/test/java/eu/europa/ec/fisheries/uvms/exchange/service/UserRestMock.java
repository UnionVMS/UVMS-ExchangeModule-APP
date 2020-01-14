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
package eu.europa.ec.fisheries.uvms.exchange.service;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import eu.europa.ec.fisheries.wsdl.user.types.Organisation;

@Path("user/rest/user")
@Stateless
public class UserRestMock {

    @GET
    @Path("getOrganisation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganisation(@QueryParam("organisationName") String organisationName) {
        return Response.ok(getBasicOrganisation(organisationName)).build();
    }
    
    private Organisation getBasicOrganisation(String organisationName) {
        Organisation organisation = new Organisation();
        organisation.setName(organisationName);
        organisation.setNation("UNK");
        return organisation;
    }
}