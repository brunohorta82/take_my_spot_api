package business.artifacts.entity;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Stateless
public class SpotRepository extends PersistenceRepository<Spot> {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String API_KEY = "1d7744bd-5d9f-48f8-b877-213c9f1dda33";
    public static final String AUTH = "Basic ZmQ0NzZkODItYmM3OC00NDQ3LWJjOWUtNTExZjVjYjIwMDg0";
    public static final String SENDER_ID = "senderId";
    public static final String TAKER_ID = "takerId";
    public static final String NEW_SPOT = "NEW_SPOT";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String SPOT_ID = "spotId";
    private static final String TIMEOUT_RESERVED = "TIMEOUT_RESERVED";
    @EJB
    SpotIntentRepository spotIntentRepository;

    @Override
    public Spot store(Spot entity) {

        entity.setCreatedOn(new Date());
        final Spot spot = super.store(entity);
        //1
        sendPushToAll(Json.createObjectBuilder().add("en", "Look, a Spot near you is available!").build(), Json.createObjectBuilder().add("type", NEW_SPOT).add("pushToken", spot.getSenderId()).add(SPOT_ID, spot.getId()).add(LATITUDE, spot.getLatitude()).add(SENDER_ID, spot.getSenderId()).
                add(LONGITUDE, spot.getLongitude()).build());
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
        final Spot spot = get(spotIntent.getSpotId());
        if (spot != null) {
            if (spot.getTakerId() == null && !spot.isTimeout()) {
                final SpotIntent store = spotIntentRepository.store(spotIntent);
                spot.setTakerId(spotIntent.getTakerId());
                spot.setTakenIntentOn(new Date());
                //2.1
                System.out.println("Someone is coming for your Spot!");
                //sendPush(Json.createObjectBuilder().add("en", "Good news, this Spot is available for you!").build(), Json.createObjectBuilder().add("type", "INFO").add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).add("spotId", spot.getId()).build(), spotIntent.getTakerId());
                sendPush(Json.createObjectBuilder().add("en", "Someone is coming for your Spot!").build(), Json.createObjectBuilder().add("type", "RESERVED").add(LATITUDE, spot.getLatitude()).add(SPOT_ID, spot.getId()).add(LONGITUDE, spot.getLongitude()).build(), spot.getSenderId());
                return store;
            } else {
                //2.2
                //sendPush(Json.createObjectBuilder().add("en", "Bad news, this spot already taken!").build(), Json.createObjectBuilder().add("type", "INFO").add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spotIntent.getTakerId());
                return null;
            }
        }
        return null;

    }

    @Schedule(hour = "*", minute = "*", second = "*/30", persistent = false)
    public void collectTopTenViews() {
        System.out.println("....");
        //   sendPushToAll(Json.createObjectBuilder().add("en", "Good news, this Spot is available for you!").build(), Json.createObjectBuilder().add("type", NEW_SPOT).add(SENDER_ID, "b5bd2f3e-b71e-4f5f-aa4e-7268156b26be").add(LATITUDE, 39.7431262).add(LONGITUDE, -8.8096282).build());
        // sendPushToAll(Json.createObjectBuilder().add("en", "Good news, this Spot is available for you!").build(), Json.createObjectBuilder().add("type", NEW_SPOT).add(SENDER_ID, "b5bd2f3e-b71e-4f5f-aa4e-7268156b26be").add(LATITUDE, 39.7364363).add(LONGITUDE, -8.7769553).build());
        //sendPushToAll(Json.createObjectBuilder().add("en", "Good news, this Spot is available for you!").build(), Json.createObjectBuilder().add("type", NEW_SPOT).add(SENDER_ID, "b5bd2f3e-b71e-4f5f-aa4e-7268156b26be").add(LATITUDE, 49.7364363).add(LONGITUDE, -9.7769553).build());

    }

    public List<Spot> findAllFreeSpots() {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<Spot> criteria = cb.createQuery(getType());
        Root<Spot> member = criteria.from(getType());
        criteria.select(member).where(cb.equal(member.get("alreadyTaken"), false), cb.equal(member.get("timeout"), false));
        return getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true).getResultList();
    }

    public List<Spot> findMyFreeSpots(String senderId) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<Spot> criteria = cb.createQuery(getType());
        Root<Spot> member = criteria.from(getType());
        criteria.select(member).where(cb.equal(member.get("alreadyTaken"), false), cb.equal(member.get(SENDER_ID), senderId), cb.equal(member.get("timeout"), false));
        return getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true).getResultList();
    }

    @Schedule(hour = "*", minute = "*", second = "*/5", persistent = false)
    public void checkTimeOuts() {
        final List<Spot> freeSpots = findAllFreeSpots();
        for (Spot spot : freeSpots) {
            if (spot.getTakerId() != null && spot.getTakenIntentOn().getTime() + 60000 < System.currentTimeMillis()) {
                System.out.println("Bad news, you have missed the spot!");

                sendPush(Json.createObjectBuilder().add("en", "Bad news, you took too must time and the Spot is now available for others!").build(), Json.createObjectBuilder().add("type", TIMEOUT).add(SPOT_ID, spot.getId()).add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spot.getTakerId());

                sendPush(Json.createObjectBuilder().add("en", "The Taker is took must time").build(), Json.createObjectBuilder().add("type", TIMEOUT_RESERVED).add(SPOT_ID, spot.getId()).add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spot.getSenderId());

                sendPushToAll(Json.createObjectBuilder().add("en", "Look, a Spot near you is available!").build(), Json.createObjectBuilder().add("type", NEW_SPOT).add("pushToken", spot.getSenderId()).add(SPOT_ID, spot.getId()).add(LATITUDE, spot.getLatitude()).add(SENDER_ID, spot.getSenderId()).
                        add(LONGITUDE, spot.getLongitude()).build());
                spot.setTakerId(null);
            }
            if (spot.getCreatedOn().getTime() + (60000) < System.currentTimeMillis() && spot.getTakerId() == null) {
                spot.setTimeout(true);
                System.out.println("Thanks for your time but nobody take you spot in time!");
                sendPush(Json.createObjectBuilder().add("en", "Looks like your Spot isn't that good!").build(), Json.createObjectBuilder().add("type", TIMEOUT).add(SPOT_ID, spot.getId()).add(LATITUDE, spot.getLatitude()).add(LONGITUDE, spot.getLongitude()).build(), spot.getSenderId());
            }
        }

    }

    public boolean validate(SpotValidation spotValidation) {
        final Spot spot = get(spotValidation.getSpotId());
        return spot != null && spot.getTakerId() != null && spot.getTakerId().equals(spotValidation.getTakerId()) && spot.getSenderId().equals(spotValidation.getSenderId());

    }

    public void notifySender(NavigationNotification navigationNotification) {
        System.out.println(navigationNotification.getLatitude() + " - " + navigationNotification.getLongitude());
        final Spot spot = get(navigationNotification.getSpotId());
        if (spot != null && spot.getTakerId() != null && !spot.isTimeout()) {
            spot.setTakerId(navigationNotification.getTakerId());
            sendPush(Json.createObjectBuilder().add("en", "Taker Coordinates").build(), Json.createObjectBuilder().add("type", "NAVIGATION").add(LATITUDE, navigationNotification.getLatitude()).add(SPOT_ID, spot.getId()).add(LONGITUDE, navigationNotification.getLongitude()).build(), spot.getSenderId());
        }

    }
}
