<%@tag description="Smoothness Style Links" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@attribute name="smoothnessCdn"%>
<%@attribute name="smoothnessServer"%>
<%@attribute name="smoothnessVersion"%>
<c:choose>
    <c:when test="${smoothnessCdn}">
    <script src="//${smoothnessServer}/jquery/3.7.1.min.js"></script>
    <script src="//${smoothnessServer}/jquery-ui/1.14.1/jquery-ui.min.js"></script>
    <script src="//${smoothnessServer}/uri/uri-1.14.1.min.js"></script>
    <script src="//${smoothnessServer}/jquery-plugins/select2/4.0.13/select2.min.js"></script>
    <script src="//${smoothnessServer}/jquery-plugins/maskedinput/jquery.maskedinput-1.3.1.min.js"></script>
    <script src="//${smoothnessServer}/jquery-plugins/timepicker/jquery-ui-timepicker-1.5.0.min.js"></script>
    <script src="//${smoothnessServer}/jlab-theme/smoothness/${smoothnessVersion}/js/smoothness.min.js"></script>
    </c:when>
    <c:otherwise>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-3.7.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/jquery-ui-1.14.1/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/uri-1.14.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/jquery-plugins/select2/4.0.13/select2.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/jquery-plugins/maskedinput/1.3.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/jquery-plugins/timepicker/1.5.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/smoothness.js"></script>
    </c:otherwise>
</c:choose>
<c:url var="iframeLoginUrl" value="${env['KEYCLOAK_FRONTEND_SERVER_URL']}/auth/realms/${env['KEYCLOAK_REALM']}/protocol/openid-connect/auth">
    <c:param name="client_id" value="account"/>
    <c:param name="kc_idp_hint" value="${env['KEYCLOAK_HEADLESS_IDP']}"/>
    <c:param name="response_type" value="code"/>
    <c:param name="redirect_uri" value="${env['KEYCLOAK_FRONTEND_SERVER_URL']}/auth/realms/${env['KEYCLOAK_REALM']}/account/"/>
</c:url>
<c:url var="suLogoutUrl" value="${env['KEYCLOAK_FRONTEND_SERVER_URL']}/auth/realms/${env['KEYCLOAK_REALM']}/protocol/openid-connect/logout">
    <c:param name="redirect_uri" value="${env['KEYCLOAK_FRONTEND_SERVER_URL']}/auth/realms/${env['KEYCLOAK_REALM']}/account/"/>
</c:url>
<%-- The user the page is for, which smoothness.js checks is still logged in; none behind the public proxy --%>
<c:set var="sessionUser" value="${publicProxy ? '' : pageContext.request.remoteUser}"/>
<c:url var="sessionLoginUrl" value="/sso">
    <c:param name="returnUrl" value="${env['FRONTEND_SERVER_URL']}${pageContext.request.contextPath}/logged-in"/>
</c:url>
<script type="text/javascript" data-user="${fn:escapeXml(sessionUser)}" data-login-url="${fn:escapeXml(sessionLoginUrl)}">
    var jlab = jlab || {};
    jlab.user = document.currentScript.getAttribute('data-user');
    jlab.session = jlab.session || {};
    jlab.session.loginUrl = document.currentScript.getAttribute('data-login-url');
    jlab.contextPath = '${pageContext.request.contextPath}';
    jlab.logbookServerUrl = '${env["LOGBOOK_SERVER_URL"]}';
    jlab.keycloakServerUrl = '${env["KEYCLOAK_FRONTEND_SERVER_URL"]}';
    jlab.iframeLoginUrl = '${empty env['KEYCLOAK_HEADLESS_IDP'] ? '' : iframeLoginUrl}';
    jlab.suLogoutUrl = '${suLogoutUrl}';
    jlab.runUrl = '${env["JLAB_RUN_URL"]}';
</script>
