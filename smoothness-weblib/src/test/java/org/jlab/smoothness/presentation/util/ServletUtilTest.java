package org.jlab.smoothness.presentation.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** ServletUtil, and ParamBuilder, which builds its parameter maps. */
class ServletUtilTest {

  @Test
  void buildQueryStringEncodesAndRepeatsKeys() {
    Map<String, List<String>> params = new LinkedHashMap<>();
    params.put("a", List.of("1", "2"));
    params.put("b", List.of("x y&z"));
    params.put("", List.of("skipped"));
    params.put("c", Arrays.asList((String) null));
    params.put("d", List.of());

    assertEquals("?a=1&a=2&b=x+y%26z&c=", ServletUtil.buildQueryString(params, "UTF-8"));
  }

  @Test
  void buildQueryStringOfNoParametersIsEmpty() {
    assertEquals("", ServletUtil.buildQueryString(new LinkedHashMap<>(), "UTF-8"));
  }

  @Test
  void buildUrl() {
    assertEquals(
        "/app/page?a=1", ServletUtil.buildUrl("/app", "/page", Map.of("a", List.of("1")), "UTF-8"));
    assertEquals("/app/page", ServletUtil.buildUrl("/app", "/page", Map.of(), "UTF-8"));
  }

  @Test
  void getCurrentUrlUsesTheRequestsPaths() {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("start", "2026-03-10");
    params.put("end", null);

    assertEquals(
        "/app/page?start=2026-03-10&end=",
        ServletUtil.getCurrentUrl(FakeRequest.withParams(), params));
  }

  @Test
  void paramBuilderKeepsOrderAndSkipsNulls() {
    ParamBuilder builder = new ParamBuilder();
    builder.add("b", "1");
    builder.add("a", new Object[] {2, null, "3"});
    builder.add("c", (Object[]) null);
    builder.add("b", "4");

    assertEquals(List.of("b", "a", "c"), new ArrayList<>(builder.getParams().keySet()));
    assertEquals(List.of("4"), builder.getParams().get("b"));
    assertEquals(List.of("2", "3"), builder.getParams().get("a"));
    assertEquals(List.of(), builder.getParams().get("c"));
    assertEquals("?b=4&a=2&a=3", ServletUtil.buildQueryString(builder.getParams(), "UTF-8"));
  }
}
