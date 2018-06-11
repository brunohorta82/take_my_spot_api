package business.artifacts.boundary;

import business.artifacts.entity.BaseEntity;

import javax.persistence.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Logger;


public abstract class PersistenceRepository<T extends BaseEntity> {
    protected static final String HIBERNATE_CACHE = "org.hibernate.cacheable";


    protected Class<T> type;

    @PersistenceContext
    private EntityManager em;

    protected final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public T get(long id) {
        return getManager().find(type, id);
    }




    protected PersistenceRepository() {

        Class<? extends PersistenceRepository> aClass = getClass();
        while (!(aClass.getGenericSuperclass() instanceof ParameterizedType)) {
            aClass = aClass.getClass().cast(aClass.getSuperclass());
        }

        Type paramType = ParameterizedType.class.cast(aClass.getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            this.type = Class.forName(paramType.getTypeName()).getClass().cast(paramType);
        } catch (ClassNotFoundException e) {
            log(e);
        }
    }



    public EntityManager getManager() {
        return em;
    }

    public Class<T> getType() {
        return type;
    }


    public void remove(long id) {
        try {
            T reference = getManager().getReference(getType(), id);
            getManager().remove(reference);
        } catch (EntityNotFoundException e) {
            log(e);
        }

    }


    public T merge(T entity) {
        return getManager().merge(entity);
    }


    public T store(T entity) {
        return merge(entity);
    }



    public void log(Throwable throwable) {

        logger.info("IGNORED " + throwable.getMessage());
    }


}
