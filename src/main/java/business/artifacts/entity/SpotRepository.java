package business.artifacts.entity;

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

    public void sendPush(JsonObject payload,JsonObject payloadExtra, String deviceId) {
        Client client = ClientBuilder.newBuilder().build();

        JsonObject loginCredential = Json.createObjectBuilder()
                .add("app_id", "1d7744bd-5d9f-48f8-b877-213c9f1dda33")
                .add("include_player_ids", Json.createArrayBuilder().add(deviceId).build())
                .add("contents", payload)
                .add("data", payloadExtra)
                .build();
        Response response = client.target("https://onesignal.com")
                .path("api/v1/notifications")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic ZmQ0NzZkODItYmM3OC00NDQ3LWJjOWUtNTExZjVjYjIwMDg0")
                .post(Entity.json(loginCredential));
        JsonObject readEntity = response.readEntity(JsonObject.class);
        System.out.println(readEntity);
    }
}
