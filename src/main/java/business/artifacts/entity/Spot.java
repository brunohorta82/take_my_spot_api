package business.artifacts.entity;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Spot extends BaseEntity {
    @Id
    private long id;
    private String senderId;
    private String takerId;

    private double latitude;

    private double longitude;

    public Spot() {
    }

    public Spot(String senderId, String takerId, double latitude, double longitude) {
        this.senderId = senderId;
        this.takerId = takerId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTakerId() {
        return takerId;
    }

    public void setTakerId(String takerId) {
        this.takerId = takerId;
    }


}

