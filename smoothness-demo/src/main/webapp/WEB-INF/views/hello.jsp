<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<c:set var="title" value="Hello"/>
<s:loose-page title="${title}" category="" description="Example loose page">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
    <section>
    <c:if test="${param.partial ne 'Y'}">
        <h2><c:out value="${title}"/></h2>
    </c:if>
        Hello World
    </section>
    </jsp:body>
</s:loose-page>
