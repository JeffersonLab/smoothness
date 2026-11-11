<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<c:set var="user" value="${pageContext.request.remoteUser}"/>
<s:loose-page title="${empty user ? 'Not Logged In' : 'Logged In'}">
    <jsp:attribute name="scripts">
        <script>
            jlab.session.announceLogin();
        </script>
    </jsp:attribute>
    <jsp:body>
        <section id="logged-in">
            <c:choose>
                <c:when test="${empty user}">
                    <h2>You're not logged in</h2>
                    <p>Return to the page you were working on and try logging in again.</p>
                </c:when>
                <c:otherwise>
                    <h2>You're logged in</h2>
                    <p>You're logged in to <c:out value="${initParam.appName}"/> as
                        <b><c:out value="${user}"/></b>. Close this tab and return to the page you
                        were working on.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </jsp:body>
</s:loose-page>
