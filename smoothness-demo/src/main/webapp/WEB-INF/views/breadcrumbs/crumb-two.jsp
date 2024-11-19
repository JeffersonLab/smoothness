<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Crumb Two"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
    </jsp:attribute>
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li><a href="crumb-one">Crumb One</a></li>
                <li>Crumb Two</li>
            </ul>
        </div>
        <div class="dialog-content">
            <section>
                <s:filter-flyout-widget ribbon="true">
                    <form class="filter-form" method="get" action="crumb-two">
                        <div class="filter-form-panel">
                            <fieldset>
                                <legend>Filter</legend>
                                <ul class="key-value-list">
                                    <li>
                                        <div class="li-key">
                                            <label for="search">Search</label>
                                        </div>
                                        <div class="li-value">
                                            <input id="search" name="search" value="${fn:escapeXml(param.search)}"/>
                                            <div>(matches any attribute which starts with substring)</div>
                                        </div>
                                    </li>
                                </ul>
                            </fieldset>
                        </div>
                        <input type="hidden" class="offset-input" name="offset" value="0"/>
                        <input class="filter-form-submit-button" type="submit" value="Apply"/>
                    </form>
                </s:filter-flyout-widget>
                <div class="float-breadbox">
                    <ul>
                        <li>
                            <div><a href="crumb-one" data-dialog-title="Crumb One" class="dialog-friendly">Previous</a></div>
                        </li>
                        <li>
                            <div>Some middle ground</div>
                        </li>
                        <li><a href="crumb-three" data-dialog-title="Crumb Three" class="dialog-friendly">Next</a></li>
                    </ul>
                </div>
                <h2 class="page-header-title"><c:out value="${title}"/></h2>
                <c:if test="${pageContext.request.userPrincipal ne null}">
                    <div class="message-box"><c:out value="${selectionMessage}"/></div>
                </c:if>
                <c:choose>
                    <c:when test="${pageContext.request.userPrincipal ne null}">
                        <table class="data-table stripped-table">
                            <thead>
                            <tr>
                                <th>Lastname</th>
                                <th>Firstname</th>
                                <th>Username</th>
                                <th>Formatted</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${userList}" var="user">
                                <tr>
                                    <td><c:out value="${user.lastname}"/></td>
                                    <td><c:out value="${user.firstname}"/></td>
                                    <td><c:out value="${user.username}"/></td>
                                    <td><c:out value="${s:formatUser(user)}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <c:url var="doLoginUrl" value="/sso">
                            <c:param name="returnUrl" value="${pageContext.request.contextPath}/breadcrumbs/crumb-two"/>
                        </c:url>
                        <h3><a href="${doLoginUrl}">Login to view</a></h3>
                    </c:otherwise>
                </c:choose>
                <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
                        value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
                </button>
                <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
                        value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
                </button>
            </section>
        </div>
    </jsp:body>
</t:page>
