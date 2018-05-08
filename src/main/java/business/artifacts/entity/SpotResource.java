package business.artifacts.entity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("spots")
public class SpotResource {

    @EJB
    private SpotRepository spotRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response store(Spot spot) {
        return Response.ok().entity(spotRepository.store(spot)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        spotRepository.sendPush(Json.createObjectBuilder().add("en", "Olá").build(), Json.createObjectBuilder().add("point", "0.0").build(), "6c9eec65-e948-4484-8d57-5f3de02a4cf6");
        return Response.ok().entity("ok").build();
    }


}
