<%@tag description="The Feature Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<t:page title="${title}" category="Features">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <ul>
            <li${'/features/multiselect-datatable' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/multiselect-datatable">Multiselect
                    Datatable</a></li>
            <li${'/features/single-select-datatable' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/features/single-select-datatable">Single
                    Select Datatable</a></li>
            <li>
                <a href="${pageContext.request.contextPath}/features/404Demo">404 Error Demo</a>
            </li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:page>
