package business.artifacts.entity;


import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Spot extends BaseEntity {

    private String senderId;
    private String takerId;

    private double latitude;

    private double longitude;
    private boolean alreadyTaken;
    private boolean timeout;
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenIntentOn;
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenOn;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;


    public Spot() {
    }

    public Spot(String senderId, String takerId, double latitude, double longitude) {
        this.senderId = senderId;
        this.takerId = takerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdOn = new Date();
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public Date getTakenIntentOn() {
        return takenIntentOn;
    }

    public void setTakenIntentOn(Date takenIntentOn) {
        this.takenIntentOn = takenIntentOn;
    }

    public boolean isAlreadyTaken() {
        return alreadyTaken;
    }

    public void setAlreadyTaken(boolean alreadyTaken) {
        this.alreadyTaken = alreadyTaken;
    }

    public Date getTakenOn() {
        return takenOn;
    }

    public void setTakenOn(Date takenOn) {
        this.takenOn = takenOn;
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


    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}

