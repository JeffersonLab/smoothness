<%@tag description="A Loose Page (no navigation) Template that supports partial pages and optionally smoothness resources" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="title"%>
<%@attribute name="description"%>
<%@attribute name="category"%>
<%@attribute name="excludeSmoothResources" required="false" type="java.lang.Boolean" description="Defaults to false" %>
<c:set var="excludeSmoothResources" value="${(empty excludeSmoothResources) ? false : true}" />
<c:choose>
    <c:when test="${param.partial eq 'Y'}">
        <div id="partial" data-title="${title}">
            <div id="partial-css">
                <jsp:invoke fragment="stylesheets"/>
            </div>
            <div id="partial-html">
                <div class="partial">
                    <jsp:doBody/>
                </div>
            </div>
            <div id="partial-js">
                <jsp:invoke fragment="scripts"/>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['jakarta.servlet.forward.request_uri']}${requestScope['jakarta.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['jakarta.servlet.forward.query_string']) : ''}"/>
        <c:set var="currentPath" scope="request" value="${requestScope['jakarta.servlet.forward.servlet_path']}"/>
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
        <c:if test="${not excludeSmoothResources}">
            <s:smooth-style smoothnessCdn="${smoothnessCdn}" smoothnessServer="${smoothnessServer}" smoothnessVersion="${smoothnessVersion}"/>
        </c:if>
        <jsp:invoke fragment="stylesheets"/>
    </head>
    <body class="${param.print eq 'Y' ? 'print ' : ''} ${param.fullscreen eq 'Y' ? 'fullscreen' : ''}">
        <jsp:doBody/>
    <c:if test="${not excludeSmoothResources}">
        <s:smooth-script smoothnessCdn="${smoothnessCdn}" smoothnessServer="${smoothnessServer}" smoothnessVersion="${smoothnessVersion}"/>
    </c:if>
    <jsp:invoke fragment="scripts"/>
    </body>
</html>
    </c:otherwise>
</c:choose>