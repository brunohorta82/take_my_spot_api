package business.artifacts.entity;


import javax.persistence.Entity;

@Entity
public class SpotIntent extends BaseEntity {

    private String takerId;
    private long spotId;

    public SpotIntent() {
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

