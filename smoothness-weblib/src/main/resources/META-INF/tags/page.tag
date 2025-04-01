<%@tag description="The Site Page Template" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="title"%>
<%@attribute name="category"%>
<%@attribute name="description"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
<c:choose>
    <c:when test="${param.partial eq 'Y'}">
        <div id="partial" data-title="${title}">
            <div id="partial-css">
                <jsp:invoke fragment="stylesheets"/>
            </div>
            <div id="partial-html">
                <div class="partial">
                    <jsp:doBody/>
                </div>
            </div>
            <div id="partial-js">
                <jsp:invoke fragment="scripts"/>
            </div>
        </div>
    </c:when>
    <c:otherwise>
<s:tabbed-page title="${title}" category="${category}" description="${description}">
    <jsp:attribute name="stylesheets">
        <t:app-style/>
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <t:app-script/>
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="userExtra">
        <t:user-extra/>
    </jsp:attribute>
    <jsp:attribute name="footnote">
        <t:footnote/>
    </jsp:attribute>
    <jsp:attribute name="headerExtra">
        <t:header-extra/>
    </jsp:attribute>
    <jsp:attribute name="primaryNavigation">
        <t:primary-nav/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
        <jsp:invoke fragment="secondaryNavigation"/>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</s:tabbed-page>
    </c:otherwise>
</c:choose>