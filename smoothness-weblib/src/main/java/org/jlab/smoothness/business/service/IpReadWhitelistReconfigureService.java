package org.jlab.smoothness.business.service;

import javax.ejb.Stateless;
import org.jlab.smoothness.persistence.view.SettingChangeAction;
import org.jlab.smoothness.presentation.filter.IpReadFilter;

@Stateless
public class IpReadWhitelistReconfigureService implements SettingChangeAction {
  @Override
  public void handleChange(String s, String s1) {
    IpReadFilter.reconfigureWhitelist();
  }
}
