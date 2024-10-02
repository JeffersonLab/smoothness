package org.jlab.smoothness.business.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBAccessException;
import javax.ejb.SessionContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * An abstract parent base class for JPA services.
 *
 * @author ryans
 */
public abstract class JPAService<T> {
  /** The SessionContext */
  @Resource protected SessionContext context;

  /** The EntityManager */
  @PersistenceContext(unitName = "webappPU")
  protected EntityManager em;

  /** The Class */
  protected final Class<T> entityClass;

  /**
   * Create a new JPAService with provided Class.
   *
   * @param entityClass The entity class
   */
  public JPAService(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Return the EntityManager
   *
   * @return The EntityManager
   */
  protected EntityManager getEntityManager() {
    return em;
  }

  /**
   * Create an entity.
   *
   * @param entity The entity
   */
  public void create(T entity) {
    getEntityManager().persist(entity);
  }

  /**
   * Edit (update) an entity.
   *
   * @param entity The entity
   */
  public void edit(T entity) {
    getEntityManager().merge(entity);
  }

  /**
   * Remove (delete) an entity.
   *
   * @param entity The entity
   */
  public void remove(T entity) {
    getEntityManager().remove(getEntityManager().merge(entity));
  }

  /**
   * Find an entity by ID
   *
   * @param id The ID
   * @return The entity
   */
  public T find(Object id) {
    return getEntityManager().find(entityClass, id);
  }

  /**
   * Query all records.
   *
   * @return The list of all entities
   */
  public List<T> findAll() {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
    cq.select(cq.from(entityClass));
    return getEntityManager().createQuery(cq).getResultList();
  }

  /**
   * Query all records and order by the supplied directives.
   *
   * @param directives The order directives
   * @return The list of all entities
   */
  public List<T> findAll(OrderDirective... directives) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(entityClass);
    Root<T> root = cq.from(entityClass);
    cq.select(root);
    List<Order> orders = new ArrayList<>();
    for (OrderDirective ob : directives) {
      Order o;

      Path p = root.get(ob.field);

      if (ob.asc) {
        o = cb.asc(p);
      } else {
        o = cb.desc(p);
      }

      orders.add(o);
    }
    cq.orderBy(orders);
    return getEntityManager().createQuery(cq).getResultList();
  }

  /** Order Directive for JPA Query. */
  public static class OrderDirective {

    private final String field;
    private final boolean asc;

    /**
     * Create a new OrderDirective specifying a field and accepting ascending direction.
     *
     * @param field The field to order by asc
     */
    public OrderDirective(String field) {
      this(field, true);
    }

    /**
     * Create a new OrderDirective specifying both field and direction.
     *
     * @param field The field name
     * @param asc true for ascending, false for descending
     */
    public OrderDirective(String field, boolean asc) {
      this.field = field;
      this.asc = asc;
    }

    /**
     * Return the field name.
     *
     * @return The field name
     */
    public String getField() {
      return field;
    }

    /**
     * Check if direction is ascending.
     *
     * @return true if ascending, false if descending
     */
    public boolean isAsc() {
      return asc;
    }
  }

  /**
   * Check if request is authenticated and return the username if it is.
   *
   * @return The username of the authenticated user
   * @throws EJBAccessException If not authenticated
   */
  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public String checkAuthenticated() {
    String username = context.getCallerPrincipal().getName();
    if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
      throw new EJBAccessException("You must be authenticated to perform the requested operation");
    }
    return username;
  }
}
