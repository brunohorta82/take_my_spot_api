package business.artifacts.entity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    @Path("grab")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response grabSpot(SpotIntent spotIntent) {
        return Response.ok().entity(spotRepository.registerIntent(spotIntent)).build();
    }


}
