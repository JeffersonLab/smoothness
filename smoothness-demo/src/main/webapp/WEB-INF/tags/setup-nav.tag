<%@tag description="Setup Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<ul>
    <li${'/setup/settings' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/setup/settings">Settings</a>
    </li>
    <li${'/setup/directory-cache' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/setup/directory-cache">Directory Cache</a>
    </li>
    <li${'/setup/setup-one' eq currentPath ? ' class="current-secondary"' : ''}>
        <a href="${pageContext.request.contextPath}/setup/setup-one">Setup One</a>
    </li>
</ul>