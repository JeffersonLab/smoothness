package org.jlab.smoothness.business.service;

import jakarta.ejb.Stateless;
import org.jlab.smoothness.persistence.view.SettingChangeAction;
import org.jlab.smoothness.presentation.filter.IpReadFilter;

/** Reconfigure Allow List if IP addresses when admin reconfigures in Setup/Settings */
@Stateless
public class IpReadAllowlistReconfigureService implements SettingChangeAction {
  /**
   * Handle the Setting change.
   *
   * @param key The key
   * @param value The value
   */
  @Override
  public void handleChange(String key, String value) {
    IpReadFilter.reconfigureAllowlist();
  }
}
