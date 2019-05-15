<%@tag description="The Setup Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<s:secondary-page title="${title}" category="Setup" keycloakClientIdKey="${initParam.keycloakClientIdKey}">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="primaryNavigation">
        <%@ include file="/WEB-INF/fragments/primary-nav.jspf" %>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <ul>
            <li${'/setup/setup-one' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/setup/setup-one">Setup One</a>
            </li>
            <li${'/setup/setup-two' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/setup/setup-two">Setup Two</a>
            </li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:secondary-page>
