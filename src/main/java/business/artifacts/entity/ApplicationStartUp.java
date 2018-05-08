package business.artifacts.entity;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;

@Startup
@Singleton
public class ApplicationStartUp implements Serializable {

    @EJB
    private SpotRepository spotRepository;

    @PostConstruct
    public void init() {
        spotRepository.store(new Spot("sender", "taker", 10,5));

    }

    private Point wktToGeometry(String wktPoint) {
        WKTReader fromText = new WKTReader();
        Point geom = null;
        try {
            geom = fromText.read(wktPoint).getCentroid();
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wktPoint);
        }
        return geom;
    }
}
