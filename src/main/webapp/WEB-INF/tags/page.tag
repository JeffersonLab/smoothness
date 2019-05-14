<%@tag description="The Site Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="org.jlab/smoothness"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<s:primary-page title="${title}" keycloakClientIdKey="KEYCLOAK_CLIENT_ID_SMOOTHNESS_TEMPLATE">
    <jsp:attribute name="stylesheets">
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="navigation">
                    <ul>
                        <li${'/overview' eq currentPath ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/overview">Overview</a>
                        </li>
                        <li${fn:startsWith(currentPath, '/features') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/features/multiselect-datatable">Features</a>
                        </li>
                        <li${fn:startsWith(currentPath, '/breadcrumbs') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/breadcrumbs/crumb-one">Breadcrumbs</a>
                        </li>
                        <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/reports/report-one">Reports</a>
                        </li>
                        <c:if test="${pageContext.request.isUserInRole('oability')}">
                            <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
                                <a href="${pageContext.request.contextPath}/setup/setup-one">Setup</a>
                            </li>
                        </c:if>
                        <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/help">Help</a>
                        </li>
                    </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:primary-page>