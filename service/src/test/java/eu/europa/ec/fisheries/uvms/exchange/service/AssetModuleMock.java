package eu.europa.ec.fisheries.uvms.exchange.service;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/asset/rest/internal")
@Stateless
public class AssetModuleMock {

    @GET
    @Path("asset/{idType : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetById(@PathParam("idType") String type, @PathParam("id") String id) {
        AssetDTO a = getBasicAsset();
        return Response.ok(a).build();
    }

    private AssetDTO getBasicAsset() {
        AssetDTO asset = new AssetDTO();
        asset.setIrcs("IRCS");
        asset.setId(UUID.randomUUID());
        asset.setHistoryId(UUID.randomUUID());
        asset.setName("Test Asset");
        asset.setFlagStateCode("SWE");
        return asset;
    }
}
