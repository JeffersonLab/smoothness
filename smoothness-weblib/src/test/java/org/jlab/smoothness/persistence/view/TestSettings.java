package org.jlab.smoothness.persistence.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.smoothness.business.service.SettingsService;
import org.jlab.smoothness.persistence.entity.Setting;
import org.jlab.smoothness.persistence.enumeration.SettingsType;

/** Settings for tests, in place of the SETTING table the app caches when it starts. */
public final class TestSettings {

  private TestSettings() {}

  /**
   * Sets SettingsService.cachedSettings, as the app does when it starts. Values of Y or N are
   * BOOLEAN settings; the rest are STRING.
   */
  public static void cache(Map<String, String> values) {
    List<Setting> list = new ArrayList<>();
    values.forEach(
        (key, value) -> {
          Setting setting = new Setting();
          setting.setKey(key);
          setting.setValue(value);
          setting.setType(
              "Y".equals(value) || "N".equals(value) ? SettingsType.BOOLEAN : SettingsType.STRING);
          list.add(setting);
        });
    SettingsService.cachedSettings = new ImmutableSettings(list);
  }
}
