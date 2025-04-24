package org.jlab.smoothness.persistence.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.smoothness.persistence.entity.Setting;
import org.jlab.smoothness.persistence.enumeration.SettingsType;

/**
 * Read-only (immutable) application Settings.
 *
 * <p>Settings are looked up frequently and change infrequently. They're cached in two places: -
 * ServletContext.getAttribute("settings") - For global Servlets/JSP access -
 * SettingsFacade.cachedSettings - For global EJB access (static volatile)
 *
 * <p>The editable JPA Settings entity should be used only by Setup page that edits Settings.
 * Everywhere else should use this ImmutableSettings POJO class via one of the caches above.
 *
 * <p>Settings are initialized at org.jlab.smoothness.presentation.util.SettingsCacheInit during app
 * startup. Changes to settings should be done via Setup tab and at that time the caches should be
 * refreshed.
 */
public final class ImmutableSettings {
  private final Map<String, Setting> map;

  /**
   * Create an ImmutableSettings.
   *
   * @param settingList The List of Settings
   */
  public ImmutableSettings(List<Setting> settingList) {
    map = new HashMap<String, Setting>();
    for (Setting setting : settingList) {
      map.put(setting.getKey(), setting);
    }
  }

  /**
   * Get the raw String valued Setting by key or null if none exists.
   *
   * @param key The key
   * @return The value
   */
  public String get(String key) {
    Setting setting = map.get(key);

    if (setting == null) {
      return null;
    }

    return setting.getValue();
  }

  /**
   * Get a boolean valued Setting by key or returns false if key does not exist.
   *
   * <p>
   * When creating a new BOOLEAN Setting you should design it cognisant of the default value of false.
   * That way, when if users do nothing (do not provide a value) then the default value
   * of false provides the default behavior you want.
   * For example, for a flag determining if emails should be sent, call it EMAIL_ENABLED
   * if you want the default to be emails do not go out.  If instead you want the default of emails to go out, then
   * call it EMAIL_DISABLED.
   * </p>
   *
   * <p>If the type of the Setting is not BOOLEAN then a RuntimeException is thrown.   Test your code.
   *
   * @param key The key
   * @return The boolean value
   * @throws RuntimeException If the Setting requested is not of type BOOLEAN
   */
  public boolean is(String key) {
    Setting s = map.get(key);

    if(SettingsType.BOOLEAN != s.getType()) {
      throw new RuntimeException("Requesting Boolean Setting " + s.getKey() + " with actual type " + s.getType());
    }

    if (s == null) return false;

    return "Y".equals(s.getValue());
  }

  /**
   * Get a CSV valued Setting by key or return null if key does not exist.
   *
   * @param key The key
   * @return A List
   */
  public List<String> csv(String key) {
    Setting s = map.get(key);

    if (s == null) return null;

    String value = s.getValue();

    if (value == null) return null;

    return List.of(value.split(","));
  }
}
