package org.jlab.demo.business.params;

import java.util.Date;

/** Parameters for ReportOne. */
public class ReportOneParams {
  private Date start;
  private Date end;

  /**
   * Return the start Date.
   *
   * @return The start Date
   */
  public Date getStart() {
    return start;
  }

  /**
   * Set the start Date.
   *
   * @param start The start Date
   */
  public void setStart(Date start) {
    this.start = start;
  }

  /**
   * Get the end Date.
   *
   * @return The end Date
   */
  public Date getEnd() {
    return end;
  }

  /**
   * Set the end Date.
   *
   * @param end The end Date
   */
  public void setEnd(Date end) {
    this.end = end;
  }
}
