package org.jlab.smoothness.presentation.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.jsp.jstl.core.Config;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/** AuditFilter, CharacterEncodingFilter, and LocaleFilter, which prepare each request. */
class RequestFiltersTest {

  final FakeHttp.Request request = new FakeHttp.Request();
  final FakeHttp.Response response = new FakeHttp.Response();

  /** Runs AuditFilter, returning the AuditContext the chain saw. */
  AuditContext audit() throws Exception {
    List<AuditContext> seen = new ArrayList<>();
    new AuditFilter()
        .doFilter(
            request.proxy(),
            response.proxy(),
            new FakeHttp.Chain((req, res) -> seen.add(AuditContext.getCurrentInstance())));
    return seen.get(0);
  }

  @Test
  void auditContextHoldsTheUserAndAddressDuringTheRequest() throws Exception {
    request.remoteAddr = "192.168.1.5";
    request.remoteUser = "jdoe";

    AuditContext context = audit();

    assertEquals("192.168.1.5", context.getIp());
    assertEquals("jdoe", context.getUsername());
    assertNull(AuditContext.getCurrentInstance(), "context left on the thread");
  }

  @Test
  void auditContextUsesTheFirstForwardedAddress() throws Exception {
    request.headers.put("X-Forwarded-For", "203.0.113.9, 10.0.0.2");

    assertEquals("203.0.113.9", audit().getIp());
  }

  @Test
  void auditContextOfAnonymousRequestHasNoUser() throws Exception {
    assertNull(audit().getUsername());
  }

  @Test
  void auditUsernameWithColonsIsItsThirdPart() throws Exception {
    request.remoteUser = "realm:id:jdoe";

    assertEquals("jdoe", audit().getUsername());
  }

  @Test
  void auditUsernameWithOneColonThrows() {
    request.remoteUser = "realm:jdoe";

    assertThrows(ArrayIndexOutOfBoundsException.class, this::audit);
    assertNull(AuditContext.getCurrentInstance());
  }

  @Test
  void auditContextIsClearedWhenTheRequestFails() {
    RuntimeException failure = new RuntimeException("servlet failed");

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class,
            () ->
                new AuditFilter()
                    .doFilter(
                        request.proxy(),
                        response.proxy(),
                        new FakeHttp.Chain(
                            (req, res) -> {
                              throw failure;
                            })));

    assertEquals(failure, thrown);
    assertNull(AuditContext.getCurrentInstance());
  }

  @Test
  void auditContextExtras() {
    AuditContext context = new AuditContext();

    assertNull(context.putExtra("reason", "test"));
    assertEquals("test", context.getExtra("reason"));
    assertEquals("test", context.putExtra("reason", "again"));
  }

  @Test
  void characterEncodingIsUtf8BeforeTheServletRuns() throws Exception {
    List<String> seen = new ArrayList<>();

    new CharacterEncodingFilter()
        .doFilter(
            request.proxy(),
            response.proxy(),
            new FakeHttp.Chain(
                (req, res) ->
                    seen.add(request.characterEncoding + " " + response.characterEncoding)));

    assertEquals(List.of("UTF-8 UTF-8"), seen);
  }

  @Test
  void localeIsUsEnglishForJstlFormatting() throws Exception {
    FakeHttp.Chain chain = new FakeHttp.Chain();
    var proxy = request.proxy();

    new LocaleFilter().doFilter(proxy, response.proxy(), chain);

    assertEquals(Locale.US, Config.get(proxy, Config.FMT_LOCALE));
    assertTrue(chain.called());
  }
}
