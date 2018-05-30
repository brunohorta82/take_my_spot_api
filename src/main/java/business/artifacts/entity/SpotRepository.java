package business.artifacts.entity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
public class SpotRepository extends PersistenceRepository<Spot> {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String API_KEY = "1d7744bd-5d9f-48f8-b877-213c9f1dda33";
    public static final String AUTH = "Basic ZmQ0NzZkODItYmM3OC00NDQ3LWJjOWUtNTExZjVjYjIwMDg0";
    @EJB
    SpotIntentRepository spotIntentRepository;

    @Override
    public Spot store(Spot entity) {

        final Spot spot = super.store(entity);
        sendPushToAll(Json.createObjectBuilder().add("en", "Look, this Spot can be yours!").build(), Json.createObjectBuilder().add("pushToken", spot.getSenderId()).add("spotId", spot.getId()).add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build());
        return spot;
    }

    public void sendPush(JsonObject payload, JsonObject payloadExtra, String deviceId) {
        Client client = ClientBuilder.newBuilder().build();

        JsonObject loginCredential = Json.createObjectBuilder()
                .add("app_id", API_KEY)
                .add("include_player_ids", Json.createArrayBuilder().add(deviceId).build())
                .add("contents", payload)
                .add("data", payloadExtra)
                .build();
        client.target("https://onesignal.com")
                .path("api/v1/notifications")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", AUTH)
                .post(Entity.json(loginCredential));
    }

    public void sendPushToAll(JsonObject payload, JsonObject payloadExtra) {
        Client client = ClientBuilder.newBuilder().build();

        JsonObject loginCredential = Json.createObjectBuilder()
                .add("app_id", API_KEY)
                .add("included_segments", Json.createArrayBuilder().add("Active Users").build())
                .add("contents", payload)
                .add("data", payloadExtra)
                .build();
        final Response authorization = client.target("https://onesignal.com")
                .path("api/v1/notifications")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", AUTH)
                .post(Entity.json(loginCredential));
        System.out.println(authorization.readEntity(String.class));
    }

    public SpotIntent registerIntent(SpotIntent spotIntent) {
        final SpotIntent store = spotIntentRepository.store(spotIntent);
        final Spot spot = get(spotIntent.getSpotId());
        if (spot != null) {
            if (spot.getTakerId() == null) {
                spot.setTakerId(spotIntent.getTakerId());
                sendPush(Json.createObjectBuilder().add("en", "Good news, this Spot is available for you!").build(), Json.createObjectBuilder().add("type","info").add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spotIntent.getTakerId());
                sendPush(Json.createObjectBuilder().add("en", "Your spot has been reserved!").build(), Json.createObjectBuilder().add("type","info").add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spot.getSenderId());
            } else {
                sendPush(Json.createObjectBuilder().add("en", "Bad news, this spot already taken!").build(), Json.createObjectBuilder().add("type","info").add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spotIntent.getTakerId());
            }
        }
        return store;
    }
}
