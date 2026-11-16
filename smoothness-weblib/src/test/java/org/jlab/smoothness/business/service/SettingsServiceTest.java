package org.jlab.smoothness.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests how {@link SettingsService#editSetting} cleans a CSV value before saving it. */
class SettingsServiceTest {

  @Test
  void keepsACleanValue() {
    assertEquals("a|A,b|B", SettingsService.cleanCsv("a|A,b|B"));
  }

  @Test
  void trimsEachItem() {
    assertEquals("a|A,b|B", SettingsService.cleanCsv("  a|A , \tb|B\n"));
  }

  @Test
  void dropsEmptyItems() {
    assertEquals("a,b", SettingsService.cleanCsv(",a,, ,b,"));
  }

  @Test
  void keepsSpacesInsideAnItem() {
    assertEquals("a|Label with spaces", SettingsService.cleanCsv(" a|Label with spaces "));
  }

  @Test
  void blankBecomesEmpty() {
    assertEquals("", SettingsService.cleanCsv(" , "));
  }

  @Test
  void nullStaysNull() {
    assertNull(SettingsService.cleanCsv(null));
  }
}
