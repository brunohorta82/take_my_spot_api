package business.artifacts.entity;


import javax.persistence.Entity;

@Entity
public class Spot extends BaseEntity {

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

