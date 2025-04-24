<%@tag description="The Primary Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@attribute name="title"%>
<%@attribute name="description"%>
<%@attribute name="category"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="primaryNavigation" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
<%@attribute name="userExtra" fragment="true" description="Extra info about authenticated user. (Optional)" %>
<%@attribute name="headerExtra" fragment="true" description="Extra section on header. (Optional)" %>
<%@attribute name="footnote" fragment="true" description="Footnote. (Optional)" %>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<c:set var="currentPath" scope="request" value="${requestScope['javax.servlet.forward.servlet_path']}"/>
<c:set var="smoothnessCdn" value="${settings.is('SMOOTHNESS_CDN_ENABLED')}"/>
<c:set var="smoothnessServer" value="${settings.get('SMOOTHNESS_SERVER')}"/>
<c:set var="smoothnessVersion" value="${initParam.smoothnessVersion}"/>
<!DOCTYPE html>
<html lang="en">
    <head>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="description" content="${fn:escapeXml(description)}">
        <title><c:out value="${initParam.appShortName}"/> - ${empty category ? '' : category.concat(' - ')}${title}</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/img/favicon.ico"/>
        <s:smooth-style smoothnessCdn="${smoothnessCdn}" smoothnessServer="${smoothnessServer}" smoothnessVersion="${smoothnessVersion}"/>
        <jsp:invoke fragment="stylesheets"/>
    </head>
    <body class="${param.print eq 'Y' ? 'print ' : ''} ${param.fullscreen eq 'Y' ? 'fullscreen' : ''}">
        <c:if test="${env['SERVER_MESSAGE'] ne null}">
            <div id="notification-bar">
                <c:out value="${env['SERVER_MESSAGE']}"/>
                <c:if test="${not empty env['SERVER_MESSAGE_URL'] && not empty env['SERVER_MESSAGE_URL_TEXT']}">
                    (<a href="${env['SERVER_MESSAGE_URL']}"><c:out value="${env['SERVER_MESSAGE_URL_TEXT']}"/></a>)
                </c:if>
            </div>
        </c:if>        
        <div id="page">
            <header>
                <h1><span id="page-header-logo"></span> <span id="page-header-text"><c:out value="${initParam.appName}"/></span></h1>
                <div id="auth">
                    <c:choose>
                        <c:when test="${publicProxy}">

                        </c:when>
                        <c:when test="${pageContext.request.userPrincipal ne null}">
                            <div id="user-extra">
                                <jsp:invoke fragment="userExtra"/>
                            </div>
                            <div id="username-container">
                                <c:out value="${pageContext.request.userPrincipal.name}"/>
                            </div>
                            <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                                <button type="submit" value="Logout">Logout</button>
                                <input type="hidden" name="returnUrl" value="${fn:escapeXml(domainRelativeReturnUrl)}"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <c:set var="absHostUrl" value="${env['FRONTEND_SERVER_URL']}"/>
                            <c:url value="/sso" var="loginUrl">
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <c:url value="/sso" var="suUrl">
                                <c:param name="kc_idp_hint" value="${env['KEYCLOAK_SU_IDP']}"/>
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <a id="login-link" href="${loginUrl}">Login</a>
                            <c:if test="${not empty env['KEYCLOAK_SU_IDP']}">
                                (<a id="su-link" href="${suUrl}">SU</a>)
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="s-header-extra">
                    <jsp:invoke fragment="headerExtra"/>
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
            <div id="s-footnote">
                <jsp:invoke fragment="footnote"/>
            </div>
        </div>
        <s:smooth-script smoothnessCdn="${smoothnessCdn}" smoothnessServer="${smoothnessServer}" smoothnessVersion="${smoothnessVersion}"/>
        <jsp:invoke fragment="scripts"/>        
    </body>
</html>