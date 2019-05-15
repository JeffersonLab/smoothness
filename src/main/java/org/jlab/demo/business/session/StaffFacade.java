package org.jlab.demo.business.session;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jlab.demo.persistence.entity.Staff;

/**
 *
 * @author ryans
 */
@Stateless
public class StaffFacade extends AbstractFacade<Staff> {
    @PersistenceContext(unitName = "webappPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StaffFacade() {
        super(Staff.class);
    }
    
    public Staff findByUsername(String username) {
        TypedQuery<Staff> q = em.createQuery("select s from Staff s where username = :username", Staff.class);

        q.setParameter("username", username);

        Staff staff = null;

        List<Staff> resultList = q.getResultList();
        
        if (resultList != null && !resultList.isEmpty()) {
            staff = resultList.get(0);
        }

        return staff;
    }    

    public List<Staff> filterList(String lastname, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Staff> cq = cb.createQuery(Staff.class);
        Root<Staff> root = cq.from(Staff.class);
        cq.select(root);
        
        List<Predicate> filters = new ArrayList<>();

        if (lastname != null && !lastname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("lastname")), lastname.toLowerCase()));
        }

        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }
        
        List<Order> orders = new ArrayList<>();
        Path p0 = root.get("lastname");
        Order o0 = cb.asc(p0);
        orders.add(o0);
        cq.orderBy(orders);
        return getEntityManager().createQuery(cq).setFirstResult(offset).setMaxResults(max).getResultList();
    }

    public long countList(String lastname, int offset, int max) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Staff> root = cq.from(Staff.class);

        List<Predicate> filters = new ArrayList<>();

        if (lastname != null && !lastname.isEmpty()) {
            filters.add(cb.like(cb.lower(root.get("lastname")), lastname.toLowerCase()));
        }        
        
        if (!filters.isEmpty()) {
            cq.where(cb.and(filters.toArray(new Predicate[]{})));
        }

        cq.select(cb.count(root));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }
}
