<%@tag description="The Primary Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@attribute name="title"%>
<%@attribute name="category"%>
<%@attribute name="keycloakClientIdKey"%>
<%@attribute name="resourceLocation" description="How to load CSS/JS/IMG files. (Optional) Choose one of CDN or NONE, defaults to LOCAL" %>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="primaryNavigation" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<c:set var="currentPath" scope="request" value="${requestScope['javax.servlet.forward.servlet_path']}"/>
<c:set var="smoothnessLibver" value="2.0.0"/>
<!DOCTYPE html>
<html>
    <head>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${initParam.appShortName}"/> - ${empty category ? '' : category.concat(' - ')}${title}</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/img/favicon.ico"/>
        <c:choose>
            <c:when test="${'NONE' eq resourceLocation}">
            </c:when>
            <c:when test="${'CDN' eq resourceLocation}">
                <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jquery-ui/1.10.3/theme/smoothness/jquery-ui.min.css"/>
                <link rel="stylesheet" type="text/css" href="${cdnContextPath}/jlab-theme/smoothness/${smoothnessLibver}/css/smoothness.min.css"/>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-ui-1.10.3/jquery-ui.min.css"/>
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/smoothness.css"/>
            </c:otherwise>
        </c:choose>
        <jsp:invoke fragment="stylesheets"/>
    </head>
    <body class="${param.print eq 'Y' ? 'print ' : ''} ${param.fullscreen eq 'Y' ? 'fullscreen' : ''}">
        <c:if test="${env['SERVER_MESSAGE'] ne null}">
            <div id="notification-bar"><c:out value="${env['SERVER_MESSAGE']}"/></div>
        </c:if>        
        <div id="page">
            <header>
                <h1><span id="page-header-logo"></span> <span id="page-header-text"><c:out value="${initParam.appName}"/></span></h1>
                <div id="auth">
                    <c:choose>
                        <c:when test="${publicProxy}">

                        </c:when>
                        <c:when test="${pageContext.request.userPrincipal ne null}">
                            <div id="username-container">
                                <c:out value="${pageContext.request.userPrincipal.name.split(':')[2]}"/>
                            </div>
                            <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                                <button type="submit" value="Logout">Logout</button>
                                <input type="hidden" name="returnUrl" value="${fn:escapeXml(domainRelativeReturnUrl)}"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <c:set var="absHostUrl" value="${'https://'.concat(pageContext.request.getServerName())}"/>
                            <c:url value="/sso" var="loginUrl">
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <c:url value="/sso" var="suUrl">
                                <c:param name="kc_idp_hint" value="ace-su-keycloak-oidc"/>
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <a id="login-link" href="${loginUrl}">Login</a> (<a id="su-link" href="${suUrl}" href="#">SU</a>)
                        </c:otherwise>
                    </c:choose>
                </div>
                <nav id="primary-nav">
                    <jsp:invoke fragment="primaryNavigation"/>
                </nav>                
            </header>
            <div id="content">     
                <div id="content-liner">
                    <jsp:invoke fragment="secondaryNavigation" var="secondaryNavText"/>
                    <c:choose>
                        <c:when test="${empty fn:trim(secondaryNavText)}">
                            <jsp:doBody/>
                        </c:when>
                        <c:otherwise>
                            <div id="two-columns">
                                <div id="left-column">
                                    <section>
                                        <h2 id="left-column-header"><c:out value="${category}"/></h2>
                                        <nav id="secondary-nav">
                                            ${secondaryNavText}
                                        </nav>
                                    </section>
                                </div>
                                <div id="right-column">
                                    <jsp:doBody/>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <c:choose>
            <c:when test="${'NONE' eq resourceLocation}">
            </c:when>
            <c:when test="${'CDN' eq resourceLocation}">
                <script type="text/javascript" src="${cdnContextPath}/jquery/1.10.2.min.js"></script>
                <script type="text/javascript" src="${cdnContextPath}/jquery-ui/1.10.3/jquery-ui.min.js"></script>
                <script type="text/javascript" src="${cdnContextPath}/uri/uri-1.14.1.min.js"></script>
                <script type="text/javascript" src="${cdnContextPath}/jlab-theme/smoothness/${smoothnessLibver}/js/smoothness.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery-ui-1.10.3/jquery-ui.min.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/uri-1.14.1.min.js"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/smoothness.js"></script>
            </c:otherwise>
        </c:choose>
        <c:url var="loginUrl" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/auth">
            <c:param name="client_id" value="account"/>
            <c:param name="kc_idp_hint" value="cue-keycloak-oidc"/>
            <c:param name="response_type" value="code"/>
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
        </c:url>
        <c:url var="logoutUrl" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/logout">
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
        </c:url>
        <script type="text/javascript">
            var jlab = jlab || {};
            jlab.contextPath = '${pageContext.request.contextPath}';
            jlab.logbookHost = '${env["LOGBOOK_HOSTNAME"]}';
            jlab.keycloakHostname = '${env["KEYCLOAK_HOSTNAME"]}';
            jlab.clientId = '${env[keycloakClientIdKey]}';
            jlab.loginUrl = '${loginUrl}';
            jlab.logoutUrl = '${logoutUrl}';
        </script>
        <jsp:invoke fragment="scripts"/>        
    </body>
</html>