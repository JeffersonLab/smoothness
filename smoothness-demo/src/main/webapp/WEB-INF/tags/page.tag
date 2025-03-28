<%@tag description="The Site Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@attribute name="title"%>
<%@attribute name="category"%>
<%@attribute name="description"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
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
<s:tabbed-page title="${title}" category="${category}" description="${description}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/demo.css"/>
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="userExtra">
        <div>Welcome </div>
    </jsp:attribute>
    <jsp:attribute name="footnote">
        <a href="https://jlab.org">Jefferson Lab</a>
    </jsp:attribute>
    <jsp:attribute name="headerExtra">
         <a href="https://github.com/JeffersonLab/jam">JAM</a> | <a href="https://github.com/JeffersonLab/btm">BTM</a> | <a href="https://github.com/JeffersonLab/cnm">CNM</a> | <a href="https://github.com/JeffersonLab/dtm">DTM</a> | <a href="https://github.com/JeffersonLab/srm">SRM</a>
    </jsp:attribute>
    <jsp:attribute name="primaryNavigation">
        <ul>
            <li${'/overview' eq currentPath ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/overview">Overview</a>
            </li>
            <li${fn:startsWith(currentPath, '/features') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/single-select-datatable">Features</a>
            </li>
            <li${fn:startsWith(currentPath, '/breadcrumbs') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/breadcrumbs/crumb-one">Breadcrumb (demo)</a>
            </li>
            <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/reports/report-one">Reports (demo)</a>
            </li>
            <c:if test="${pageContext.request.isUserInRole('smoothness-demo-admin')}">
                <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
                    <a href="${pageContext.request.contextPath}/setup/setup-one">Setup (demo)</a>
                </li>
            </c:if>
            <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/help">Help</a>
            </li>
        </ul>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <jsp:invoke fragment="secondaryNavigation"/>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:tabbed-page>
    </c:otherwise>
</c:choose>