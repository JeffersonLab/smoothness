<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<c:set var="title" value="Loose Page Without Smoothness"/>
<%-- ?exclude=false keeps the smoothness resources, as leaving excludeSmoothResources out does --%>
<s:loose-page title="${title}" category="Features" description="A loose page without the smoothness styles and scripts" excludeSmoothResources="${param.exclude ne 'false'}">
    <h1><c:out value="${title}"/></h1>
    <p>This page has no tabs, styles, or scripts from the weblib.</p>
</s:loose-page>
