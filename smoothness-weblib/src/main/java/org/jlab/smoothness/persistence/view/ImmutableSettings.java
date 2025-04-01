package org.jlab.smoothness.persistence.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.smoothness.persistence.entity.Setting;

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
   * Get the raw String valued Setting by key.
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
   * Get a boolean valued Setting by key.
   *
   * <p>This accessor doesn't currently check if the value isn't actually of type boolean. If the
   * value doesn't exist, false is returned.
   *
   * @param key The key
   * @return The boolean value
   */
  public boolean is(String key) {
    Setting s = map.get(key);

    if (s == null) return false;

    // assert s.getType() == SettingsType.BOOLEAN;

    return "Y".equals(s.getValue());
  }

  /**
   * Get a CSV valued Setting by key.
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
