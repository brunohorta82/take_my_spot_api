package business.artifacts.boundary;

import business.artifacts.entity.NavigationNotification;
import business.artifacts.entity.Spot;
import business.artifacts.entity.SpotIntent;
import business.artifacts.entity.SpotValidation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
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
        if (!spotRepository.findMyFreeSpots(spot.getSenderId()).isEmpty()) {
            System.out.println("You already have one spot in evaluation");
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("reason", "You already have one spot in evaluation").build()).build();
        }
        final Spot store = spotRepository.store(spot);
        if(store != null) {
            System.out.println("Spot Stored");
            return Response.ok().entity(store).build();
        }else {
            System.out.println("Invalid Coordinates");
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("reason","Invalid Coordinates").build()).build();
        }
    }

    @Path("grab")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response grabSpot(SpotIntent spotIntent) {
        final SpotIntent spotIntent1 = spotRepository.registerIntent(spotIntent);
        if (spotIntent1 != null) {
            return Response.ok().entity(spotIntent1).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("reason", "This spot is not available!").build()).build();
        }
    }

    @Path("navigate")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response navigate(NavigationNotification navigationNotification) {
        spotRepository.notifySender(navigationNotification);
        return Response.ok().entity(Json.createObjectBuilder().add("result", "ok").build()).build();
    }

    @Path("proof")
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response proof(SpotValidation spotValidation) {
        return Response.ok().entity(Json.createObjectBuilder().add("result",spotRepository.validate(spotValidation)).build()).build();
    }


}
