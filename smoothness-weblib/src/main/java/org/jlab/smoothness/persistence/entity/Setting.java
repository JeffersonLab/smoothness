package org.jlab.smoothness.persistence.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.jlab.smoothness.persistence.enumeration.SettingsType;

/**
 * Editable setting JPA entity. See Also: org.jlab.dtm.persistence.model.ImmutableSettings.
 *
 * @author ryans
 */
@Entity
@Table(name = "SETTING")
public class Setting implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Key */
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "KEY", nullable = false)
  private String key;

  /** Value */
  @NotNull
  @Column(name = "VALUE", nullable = false)
  private String value;

  /** Type */
  @NotNull
  @Column(name = "TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private SettingsType type;

  /** Description */
  @NotNull
  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  /** Tag */
  @NotNull
  @Column(name = "TAG", nullable = false)
  private String tag;

  /** Weight */
  @NotNull
  @Column(name = "WEIGHT", nullable = false)
  private Integer weight;

  /** Change Action JNDI Name */
  @Column(name = "CHANGE_ACTION_JNDI")
  private String changeActionJNDI;

  public Setting() {}

  /**
   * Get the key.
   *
   * @return The key
   */
  public String getKey() {
    return key;
  }

  /**
   * Set the key.
   *
   * @param key The key
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Get the value.
   *
   * @return The value
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value The value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Get the type.
   *
   * @return The type
   */
  public SettingsType getType() {
    return type;
  }

  /**
   * Set the type.
   *
   * @param type The type
   */
  public void setType(SettingsType type) {
    this.type = type;
  }

  /**
   * Get description.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set description.
   *
   * @param description The description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get Tag.
   *
   * @return The tag
   */
  public String getTag() {
    return tag;
  }

  /**
   * Set tag.
   *
   * @param tag The tag
   */
  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * Get weight.
   *
   * @return The weight
   */
  public Integer getWeight() {
    return weight;
  }

  /**
   * Set weight.
   *
   * @param weight The weight
   */
  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  /**
   * Return the ChangeAction JNDI Name.
   *
   * @return The JNDI Name
   */
  public String getChangeActionJNDI() {
    return changeActionJNDI;
  }

  /**
   * Set the ChangeAction JNDI Name.
   *
   * @param changeActionJNDI The JNDI Name
   */
  public void setChangeActionJNDI(String changeActionJNDI) {
    this.changeActionJNDI = changeActionJNDI;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Setting)) return false;
    Setting setting = (Setting) o;
    return Objects.equals(key, setting.key)
        && Objects.equals(value, setting.value)
        && type == setting.type
        && Objects.equals(description, setting.description);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }
}
