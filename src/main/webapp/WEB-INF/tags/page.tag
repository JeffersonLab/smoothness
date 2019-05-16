<%@tag description="The Site Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@attribute name="title"%>
<%@attribute name="category"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
<s:tabbed-page title="${title}" category="${category}" keycloakClientIdKey="KEYCLOAK_CLIENT_ID_SMOOTHNESS_TEMPLATE" disableSmoothnessCdn="false">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/smoothness.css"/>
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/smoothness.js"></script>
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="primaryNavigation">
        <ul>
            <li${'/overview' eq currentPath ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/overview">Overview</a>
            </li>
            <li${fn:startsWith(currentPath, '/features') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/multiselect-datatable">Features</a>
            </li>
            <li${fn:startsWith(currentPath, '/breadcrumbs') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/breadcrumbs/crumb-one">Breadcrumb (demo)</a>
            </li>
            <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}>
                <a href="${pageContext.request.contextPath}/reports/report-one">Reports (demo)</a>
            </li>
            <c:if test="${pageContext.request.isUserInRole('oability')}">
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