package business.artifacts.entity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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


    public List<T> findAllLazy(int selectedPage, int pageSize, String field, Object value) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(getType());
        Root<T> member = criteria.from(getType());
        criteria.select(member).where(cb.equal(member.get(field), value));
        TypedQuery<T> query = getManager().createQuery(criteria).setFirstResult((selectedPage) * pageSize).setMaxResults(pageSize).setHint(HIBERNATE_CACHE, true);
        return query.getResultList();
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

        //counters = new Counters<>();
    }

    public T findBy(String field, Object value) throws NoRegistryFoundOnDatabase {
        try {
            CriteriaBuilder cb = getManager().getCriteriaBuilder();
            CriteriaQuery<T> criteria = cb.createQuery(getType());
            Root<T> member = criteria.from(getType());
            criteria.select(member).where(cb.equal(member.get(field), value));
            return getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true).getSingleResult();
        } catch (NoResultException result) {
            throw new NoRegistryFoundOnDatabase(result);
        }
    }

    public List<T> findAll() {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(getType());
        Root<T> member = criteria.from(getType());
        criteria.select(member);
        TypedQuery<T> query = getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true);
        return query.getResultList();
    }

    public List<T> findAllOrderBy(String orderBy, boolean asc) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(getType());
        Root<T> member = criteria.from(getType());
        criteria.select(member).orderBy(asc ? cb.asc(member.get(orderBy)) : cb.desc(member.get(orderBy)));
        TypedQuery<T> query = getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true);
        return query.getResultList();
    }

    public List<T> findAllLazy(int selectedPage, int pageSize) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(getType());
        Root<T> member = criteria.from(getType());
        criteria.select(member).orderBy(cb.desc(member.get("id")));
        TypedQuery<T> query = getManager().createQuery(criteria).setFirstResult((selectedPage) * pageSize).setMaxResults(pageSize).setHint(HIBERNATE_CACHE, true);
        return query.getResultList();
    }

    public List<T> findListBy(String field, Object value) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(getType());
        Root<T> member = criteria.from(getType());
        criteria.select(member).where(cb.equal(member.get(field), value));
        return getManager().createQuery(criteria).setHint(HIBERNATE_CACHE, true).getResultList();
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


    public void persist(T entity) {
        em.persist(entity);
    }


    public void log(Throwable throwable) {

        logger.info("IGNORED " + throwable.getMessage());
    }


}
