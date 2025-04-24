package org.jlab.smoothness.business.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.ServletContext;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.persistence.entity.Setting;
import org.jlab.smoothness.persistence.enumeration.SettingsType;
import org.jlab.smoothness.persistence.view.ImmutableSettings;
import org.jlab.smoothness.persistence.view.SettingChangeAction;

/**
 * @author ryans
 */
@Stateless
public class SettingsService extends JPAService<Setting> {

  /**
   * The immutable cached settings. When asking for settings this cache should generally be the
   * source.
   */
  public static volatile ImmutableSettings cachedSettings;

  /** Create a new SettingService */
  public SettingsService() {
    super(Setting.class);
  }

  /**
   * Query the Database for all the Settings. This method can be used to refresh the cache.
   *
   * @return The list of Settings.
   */
  @PermitAll
  public ImmutableSettings getImmutableSettings() {
    List<Setting> settingList = findAll();

    return new ImmutableSettings(settingList);
  }

  /**
   * Edit a Setting. Intended to be called via Setup/Settings page.
   *
   * <p>Caller is responsible for passing ServletContext so web cache can be updated.
   *
   * <p>In order to perform any cleanup action needed by updating a setting such as disabling
   * features (such as stopping scheduled timers) The registered SettingChangeAction is invoked, if
   * any.
   *
   * <p>We can't use RolesAllowed annotation because each app has a custom admin. Instead we
   * programmatically perform check
   *
   * @param key The Setting key
   * @param value The Setting value
   * @param context The ServletContext
   * @throws UserFriendlyException If unable to set the Setting
   */
  @PermitAll
  public void editSetting(String key, String value, ServletContext context)
      throws UserFriendlyException {
    checkAdmin();

    Setting setting = find(key);

    if (setting == null) {
      throw new UserFriendlyException("Setting not found with key: " + key);
    }

    if (SettingsType.BOOLEAN.equals(setting.getType())) {
      if (!"Y".equals(value) && !"N".equals(value)) {
        throw new UserFriendlyException("Boolean Setting value must be 'Y' or 'N'");
      }
    }

    setting.setValue(value);

    // Refresh caches
    ImmutableSettings immutable = getImmutableSettings();
    // Service Cache
    SettingsService.cachedSettings = immutable;
    // Servlet Cache
    context.setAttribute("settings", immutable);

    // Call Actions
    if (setting.getChangeActionJNDI() != null) {
      SettingChangeAction action = lookupChangeActionViaJNDI(setting.getChangeActionJNDI());
      if (action != null) {
        action.handleChange(key, value);
      }
    }
  }

  private SettingChangeAction lookupChangeActionViaJNDI(String name) {
    try {
      InitialContext ic = new InitialContext();
      return (SettingChangeAction) ic.lookup(name); // example: java:global/dtm/ScheduledEmailer
    } catch (NamingException e) {
      throw new RuntimeException("Unable to obtain EJB", e);
    }
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb, Root<Setting> root, String key, String tag) {
    List<Predicate> filters = new ArrayList<>();

    if (key != null && !key.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("key")), key.toLowerCase()));
    }

    if (tag != null && !tag.isEmpty()) {
      filters.add(cb.like(cb.lower(root.get("tag")), tag.toLowerCase()));
    }

    return filters;
  }

  /**
   * Filter the list of Settings by key and tag, paginated with offset and max per page.
   *
   * @param key The Setting key
   * @param tag The Setting tag
   * @param offset The page offset
   * @param max The page max
   * @return The list of Settings
   */
  @PermitAll
  public List<Setting> filterList(String key, String tag, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Setting> cq = cb.createQuery(Setting.class);
    Root<Setting> root = cq.from(Setting.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, root, key, tag);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("tag");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    Path p1 = root.get("weight");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    Path p2 = root.get("key");
    Order o2 = cb.asc(p2);
    orders.add(o2);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  /**
   * Return the count of Settings filter by key and tag.
   *
   * @param key The Setting key
   * @param tag The Setting tag
   * @return The count
   */
  @PermitAll
  public long countList(String key, String tag) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Setting> root = cq.from(Setting.class);

    List<Predicate> filters = getFilters(cb, root, key, tag);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    cq.select(cb.count(root));
    TypedQuery<Long> q = getEntityManager().createQuery(cq);
    return q.getSingleResult();
  }

  /**
   * Find the list of all tags.
   *
   * @return The list of tags
   */
  @PermitAll
  public List<String> findTags() {
    TypedQuery<String> q =
        getEntityManager()
            .createQuery("SELECT DISTINCT tag FROM Setting order by tag asc", String.class);
    return q.getResultList();
  }
}
