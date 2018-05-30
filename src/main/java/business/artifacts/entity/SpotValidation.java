package business.artifacts.entity;


import javax.persistence.Entity;

@Entity
public class SpotValidation extends BaseEntity {

    private String senderId;
    private String takerId;
    private long spotId;

    public SpotValidation() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getSpotId() {
        return spotId;
    }

    public void setSpotId(long spotId) {
        this.spotId = spotId;
    }

    public String getTakerId() {
        return takerId;
    }

    public void setTakerId(String takerId) {
        this.takerId = takerId;
    }


}

