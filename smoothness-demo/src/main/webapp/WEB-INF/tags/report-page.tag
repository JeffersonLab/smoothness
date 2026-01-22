<%@tag description="The Report Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<s:page title="${title}" category="Reports">
    <jsp:attribute name="stylesheets">
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <ul>
            <li${'/reports/report-one' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/reports/report-one">Report One</a>
            </li>
            <li${'/reports/report-two' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/reports/report-two">Report Two</a>
            </li>
            <li${'/reports/report-three' eq currentPath ? ' class="current-secondary"' : ''}>
                <a href="${pageContext.request.contextPath}/reports/report-three">Report Three</a>
            </li>
        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:page>
