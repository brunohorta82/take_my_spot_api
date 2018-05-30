package business.artifacts.entity;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.Serializable;

@Startup
@Singleton
public class ApplicationStartUp implements Serializable {

    @EJB
    private SpotRepository spotRepository;

    @PostConstruct
    public void init() {
    }


}
