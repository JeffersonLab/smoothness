package org.jlab.smoothness.presentation.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.jlab.smoothness.presentation.filter.CacheFilter.CachableResponse;
import org.jlab.smoothness.presentation.filter.CacheFilter.CacheControlResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * CacheFilter sets the cache headers when the servlet sets the content type: a year for static
 * types, none for the rest, unless the servlet says otherwise.
 */
class CacheFilterTest {

  final FakeHttp.Response response = new FakeHttp.Response();

  /** Runs the filter with a servlet that sets the content type. */
  void filter(String contentType) throws Exception {
    new CacheFilter()
        .doFilter(
            new FakeHttp.Request().proxy(),
            response.proxy(),
            new FakeHttp.Chain((req, res) -> res.setContentType(contentType)));
  }

  void assertMaxCache(long before) {
    long expires = response.dateHeaders.get("Expires");
    assertTrue(
        expires >= before + CacheFilter.MAX_EXPIRE_MILLIS
            && expires <= System.currentTimeMillis() + CacheFilter.MAX_EXPIRE_MILLIS,
        "Expires " + expires + " is not a year from now");
    assertEquals(Map.of(), response.headers, "no Cache-Control or Pragma");
  }

  void assertNoCache() {
    assertEquals(
        Map.of("Cache-Control", "no-store, no-cache, must-revalidate", "Pragma", "no-cache"),
        response.headers);
    assertEquals(Map.of("Expires", 0L), response.dateHeaders);
  }

  @Test
  void passesTheServletAWrappedResponse() throws Exception {
    FakeHttp.Chain chain = new FakeHttp.Chain();
    var request = new FakeHttp.Request().proxy();

    new CacheFilter().doFilter(request, response.proxy(), chain);

    assertSame(request, chain.request);
    assertInstanceOf(CacheControlResponse.class, chain.response);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "text/css",
        "text/javascript",
        "application/javascript",
        "image/png",
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/icon",
        "image/x-icon",
        "image/vnd.microsoft.icon",
        "image/svg+xml"
      })
  void staticTypesAreCachedForAYear(String contentType) throws Exception {
    long before = System.currentTimeMillis();

    filter(contentType);

    assertEquals(contentType, response.contentType);
    assertMaxCache(before);
  }

  @ParameterizedTest
  @ValueSource(strings = {"text/html", "application/json", "text/plain", "application/pdf"})
  void otherTypesAreNotCached(String contentType) throws Exception {
    filter(contentType);

    assertNoCache();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"text/css;charset=UTF-8", "text/css; charset=UTF-8", "Text/CSS", " image/png "})
  void typeIsMatchedWithoutParametersOrCase(String contentType) throws Exception {
    long before = System.currentTimeMillis();

    filter(contentType);

    assertEquals(contentType, response.contentType, "content type passed on as set");
    assertMaxCache(before);
  }

  @Test
  void otherTypeWithParametersIsNotCached() throws Exception {
    filter("text/html;charset=UTF-8");

    assertNoCache();
  }

  @Test
  void servletCanCacheAnyType() throws Exception {
    long before = System.currentTimeMillis();

    new CacheFilter()
        .doFilter(
            new FakeHttp.Request().proxy(),
            response.proxy(),
            new FakeHttp.Chain(
                (req, res) ->
                    ((CacheControlResponse) res)
                        .setContentType("application/json", CachableResponse.MAX)));

    assertEquals("application/json", response.contentType);
    assertMaxCache(before);
  }

  @Test
  void servletCanTurnCachingOff() throws Exception {
    new CacheFilter()
        .doFilter(
            new FakeHttp.Request().proxy(),
            response.proxy(),
            new FakeHttp.Chain(
                (req, res) ->
                    ((CacheControlResponse) res).setContentType("text/css", CachableResponse.OFF)));

    assertNoCache();
  }

  @Test
  void maxCacheRemovesHeadersSetEarlier() throws Exception {
    response.headers.put("Cache-Control", "private");
    response.headers.put("Pragma", "no-cache");

    filter("image/png");

    assertEquals(Map.of(), response.headers);
  }
}
