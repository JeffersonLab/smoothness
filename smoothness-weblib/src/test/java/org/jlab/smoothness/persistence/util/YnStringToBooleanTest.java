package org.jlab.smoothness.persistence.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YnStringToBooleanTest {

  final YnStringToBoolean converter = new YnStringToBoolean();

  @Test
  void toDatabaseColumn() {
    assertEquals("Y", converter.convertToDatabaseColumn(true));
    assertEquals("N", converter.convertToDatabaseColumn(false));
    assertNull(converter.convertToDatabaseColumn(null));
  }

  @Test
  void toEntityAttributeIsTrueOnlyForUppercaseY() {
    assertTrue(converter.convertToEntityAttribute("Y"));
    assertFalse(converter.convertToEntityAttribute("N"));
    assertFalse(converter.convertToEntityAttribute("y"));
    assertFalse(converter.convertToEntityAttribute(""));
    assertNull(converter.convertToEntityAttribute(null));
  }
}
