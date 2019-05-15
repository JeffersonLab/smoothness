<%@tag description="The Feature Page Template" pageEncoding="UTF-8" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="title" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<s:secondary-page title="${title}" category="Features" keycloakClientIdKey="${initParam.keycloakClientIdKey}">
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
            <li${'/features/multiselect-datatable' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/multiselect-datatable">Multiselect
                    Datatable</a></li>
            <li${'/features/single-select-datatable' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/single-select-datatable">Single
                    Select Datatable</a></li>
            <li${'/features/autocomplete' eq currentPath ? ' class="current-secondary"' : ''}><a
                    href="${pageContext.request.contextPath}/features/autocomplete">Autocomplete
                Input</a></li>
            <li${'/features/parameter-persistence' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/parameter-persistence">Parameter
                    Persistence</a></li>
            <li${'/features/bracket-nav' eq currentPath ? ' class="current-secondary"' : ''}><a
                    href="${pageContext.request.contextPath}/features/bracket-nav">Bracket
                Navigation</a></li>
            <li${'/features/flyout-nav' eq currentPath ? ' class="current-secondary"' : ''}><a
                    href="${pageContext.request.contextPath}/features/flyout-nav">Flyout Menu
                Navigation</a></li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:secondary-page>
