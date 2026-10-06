package org.jlab.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * The running demo and its services, as started by {@code docker compose -f build.yaml up}.
 *
 * <p>The tests use the same ports as docker compose: the SMOOTHNESS_*_PORT variables, which the
 * integrationTest task also reads from .env.
 */
final class Demo {

  static final String URL =
      "https://localhost:" + env("SMOOTHNESS_HTTPS_PORT", "8443") + "/smoothness-demo";
  static final String KEYCLOAK_URL =
      "http://localhost:" + env("SMOOTHNESS_KEYCLOAK_PORT", "8081") + "/auth";
  static final String MAILHOG_URL = "http://localhost:" + env("SMOOTHNESS_MAILHOG_PORT", "8025");

  /** The Keycloak test realm's users all have this password */
  static final String PASSWORD = "password";

  /** jdoe and tbrown have the smoothness-demo-admin role; jadams and jsmith don't */
  static final String ADMIN = "jdoe";

  static final String USER = "jadams";

  private static final Duration READY_TIMEOUT =
      Duration.ofSeconds(Long.parseLong(env("SMOOTHNESS_READY_TIMEOUT_SECONDS", "300")));

  private static boolean ready = false;

  /** Why the demo wasn't ready, so the other test classes fail at once instead of waiting too */
  private static String notReady = null;

  private Demo() {}

  /**
   * Waits for the demo, its database, and Keycloak, as they may still be starting. An admin's login
   * has its roles only once Keycloak's setup has finished.
   */
  static synchronized void awaitReady() throws InterruptedException {
    if (notReady != null) {
      fail(notReady);
    }

    Instant end = Instant.now().plus(READY_TIMEOUT);
    String problem = null;

    while (!ready && Instant.now().isBefore(end)) {
      try {
        HttpResponse<String> response = Session.loggedIn(ADMIN).get("/setup/settings");
        ready = response.statusCode() == 200;
        problem = "GET /setup/settings as " + ADMIN + " returned " + response.statusCode();
      } catch (IOException | RuntimeException | AssertionError e) {
        problem = e.toString();
      }

      if (!ready) {
        Thread.sleep(5_000);
      }
    }

    if (!ready) {
      notReady = "Demo at " + URL + " not ready after " + READY_TIMEOUT + ": " + problem;
      fail(notReady);
    }
  }

  /** A browser: it keeps its own cookies and follows redirects, as between the app and Keycloak. */
  static final class Session {

    private final HttpClient http;

    private Session(HttpClient.Redirect redirect) {
      http =
          HttpClient.newBuilder()
              .cookieHandler(new LocalhostCookieHandler())
              // The app is on HTTPS and Keycloak on HTTP, which NORMAL won't follow
              .followRedirects(redirect)
              .sslContext(sslContext())
              .connectTimeout(Duration.ofSeconds(5))
              .build();
    }

    /** A session that hasn't logged in. */
    static Session anonymous() {
      return new Session(HttpClient.Redirect.ALWAYS);
    }

    /** A session that hasn't logged in and doesn't follow redirects. */
    static Session notFollowingRedirects() {
      return new Session(HttpClient.Redirect.NEVER);
    }

    /** A session logged in through Keycloak's login form. */
    static Session loggedIn(String username) throws IOException, InterruptedException {
      Session session = anonymous();
      HttpResponse<String> page = session.login(username, PASSWORD);

      assertTrue(
          page.uri().toString().startsWith(URL) && page.body().contains(username),
          "login as " + username + " ended at " + page.uri() + " with " + title(page));

      return session;
    }

    /**
     * Logs in with Keycloak's login form, starting from the app's /sso page, which needs a login
     * and then returns to the overview. Its returnUrl keeps the redirects within HttpClient's limit
     * of 5: without it, /sso returns to the context root, which redirects twice more.
     *
     * @return the page the login ends at: the app's, or Keycloak's form if the login failed
     */
    HttpResponse<String> login(String username, String password)
        throws IOException, InterruptedException {
      return submitLoginForm(
          get("/sso?returnUrl=" + URI.create(URL).getPath() + "/overview"), username, password);
    }

    /**
     * Fills in and submits Keycloak's login form, on a page a redirect to log in led to.
     *
     * @return the page the login ends at: the app's, or Keycloak's form if the login failed
     */
    HttpResponse<String> submitLoginForm(
        HttpResponse<String> form, String username, String password)
        throws IOException, InterruptedException {
      Matcher action =
          Pattern.compile("action=\"([^\"]*/login-actions/authenticate[^\"]*)\"")
              .matcher(form.body());

      assertTrue(action.find(), "no Keycloak login form at " + form.uri() + ": " + title(form));

      return post(
          action.group(1).replace("&amp;", "&"),
          Map.of("username", username, "password", password));
    }

    /** GET a path in the app, or an absolute URL. */
    HttpResponse<String> get(String path) throws IOException, InterruptedException {
      return http.send(
          HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    /** POST a form to a path in the app, or an absolute URL. */
    HttpResponse<String> post(String path, Map<String, String> form)
        throws IOException, InterruptedException {
      return http.send(
          HttpRequest.newBuilder(uri(path))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .POST(HttpRequest.BodyPublishers.ofString(encode(form)))
              .build(),
          HttpResponse.BodyHandlers.ofString());
    }

    /** A path in the app, an absolute URL, or a path from the server's root, as links have. */
    private static URI uri(String path) {
      if (path.startsWith("http")) {
        return URI.create(path);
      }
      String contextPath = URI.create(URL).getPath();
      return path.startsWith(contextPath + "/")
          ? URI.create(URL).resolve(path)
          : URI.create(URL + path);
    }
  }

  /**
   * Cookies, with http://localhost treated as secure, as browsers and curl do: Keycloak marks its
   * login cookies Secure though it runs on HTTP, and CookieManager sends Secure cookies only over
   * HTTPS.
   */
  private static final class LocalhostCookieHandler extends CookieHandler {

    private final CookieManager cookies = new CookieManager();

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> headers)
        throws IOException {
      return cookies.get(secure(uri), headers);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> headers) throws IOException {
      cookies.put(secure(uri), headers);
    }

    private static URI secure(URI uri) {
      if ("http".equals(uri.getScheme()) && "localhost".equals(uri.getHost())) {
        return URI.create("https" + uri.toString().substring("http".length()));
      }
      return uri;
    }
  }

  /** The page's title, with the whitespace the JSP leaves around it collapsed. */
  static String title(HttpResponse<String> page) {
    return title(page.body());
  }

  /** The title in a page's HTML, with the whitespace the JSP leaves around it collapsed. */
  static String title(String html) {
    Matcher m = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL).matcher(html);
    return m.find() ? m.group(1).trim().replaceAll("\\s+", " ") : null;
  }

  static String encode(Map<String, String> form) {
    return form.entrySet().stream()
        .map(
            e ->
                URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                    + "="
                    + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
  }

  /**
   * Trusts the usual CAs, plus the self-signed certificate for localhost in the
   * jeffersonlab/wildfly image that the demo runs in. If a new version of that image changes its
   * certificate, the tests fail with an SSLHandshakeException: save the new one with {@code openssl
   * s_client -connect localhost:8443 </dev/null | openssl x509 >
   * smoothness-demo/src/integration/resources/wildfly-localhost.crt}
   */
  private static SSLContext sslContext() {
    Path cacerts = Path.of(System.getProperty("java.home"), "lib", "security", "cacerts");

    try (InputStream defaults = Files.newInputStream(cacerts)) {
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(defaults, null);
      trustStore.setCertificateEntry("wildfly-localhost", certificate());

      TrustManagerFactory trustManagers =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagers.init(trustStore);

      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, trustManagers.getTrustManagers(), null);
      return context;
    } catch (IOException | GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  /** The demo's self-signed certificate, saved in src/integration/resources (see sslContext). */
  static Certificate certificate() throws IOException, GeneralSecurityException {
    try (InputStream in = Demo.class.getResourceAsStream("/wildfly-localhost.crt")) {
      return CertificateFactory.getInstance("X.509").generateCertificate(in);
    }
  }

  private static String env(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
