package org.jlab.smoothness.persistence.view;

/** Implementers can be notified of a setting change. */
public interface SettingChangeAction {

  /**
   * A Setting has changed.
   *
   * @param key The key
   * @param value The value
   */
  public void handleChange(String key, String value);
}
