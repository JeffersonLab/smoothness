package org.jlab.smoothness.business.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ObjectAndExceptionUtilTest {

  @Test
  void coalesceReturnsTheFirstNonNull() {
    assertEquals("a", ObjectUtil.coalesce(null, "a", "b"));
    assertEquals("a", ObjectUtil.coalesce("a", null));
    assertNull(ObjectUtil.coalesce((String) null, null));
    assertNull(ObjectUtil.coalesce());
  }

  @Test
  void getRootCauseFollowsTheCauses() {
    IllegalStateException root = new IllegalStateException("root");
    RuntimeException top = new RuntimeException(new IllegalArgumentException(root));

    assertSame(root, ExceptionUtil.getRootCause(top));
    assertSame(root, ExceptionUtil.getRootCause(root));
    assertNull(ExceptionUtil.getRootCause(null));
  }
}
