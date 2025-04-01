<%@tag description="Primary Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<ul>
    <li${'/overview' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/overview">Overview</a>
    </li>
    <li${fn:startsWith(currentPath, '/features') ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/features/single-select-datatable">Features</a>
    </li>
    <li${fn:startsWith(currentPath, '/breadcrumbs') ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/breadcrumbs/crumb-one">Breadcrumb</a>
    </li>
    <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/reports/report-one">Reports</a>
    </li>
    <c:if test="${pageContext.request.isUserInRole('smoothness-demo-admin')}">
        <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
            <a href="${pageContext.request.contextPath}/setup/settings">Setup</a>
        </li>
    </c:if>
    <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/help">Help</a>
    </li>
</ul>