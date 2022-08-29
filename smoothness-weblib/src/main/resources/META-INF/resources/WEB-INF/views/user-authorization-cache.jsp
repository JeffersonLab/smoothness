<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="User Authorization Cache"/>
<s:tabbed-page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2>User Authorization Cache</h2>
            <h3>Users</h3>
            <table class="data-table stripped-table">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>First name</th>
                    <th>Last name</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach items="${userCache.values()}" var="user">
                <tr>
                    <td><c:out value="${user.username}"/></td>
                    <td><c:out value="${user.firstname}"/></td>
                    <td><c:out value="${user.lastname}"/></td>
                </tr>
            </c:forEach>
            </tbody>
            </table>
            <h3>Roles</h3>
            <table class="data-table stripped-table">
                <thead>
                <tr>
                    <th>Role Name</th>
                    <th>Users</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${roleCache}" var="entry">
                    <tr>
                        <td><c:out value="${entry.key}"/></td>
                        <td>
                            <c:forEach items="${entry.value}" var="user" varStatus="status">
                                <c:out value="${user.username}"/>
                                <c:if test="${not status.last}">
                                    <br/>
                                </c:if>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <h3>Clear Cache</h3>
            <form method="post" action="user-authorization-cache">
                <input type="hidden" name="action" value="clear"/>
                <input type="submit" value="Clear"/>
            </form>
            <h3>Add User</h3>
            <form method="post" action="user-authorization-cache">
                <label>Username:</label>
                <input type="text" name="username" value=""/>
                <input type="hidden" name="action" value="user"/>
                <input type="submit" value="Add"/>
            </form>
            <h3>Add Role</h3>
            <form method="post" action="user-authorization-cache">
                <label>Role Name:</label>
                <input type="text" name="role" value=""/>
                <input type="hidden" name="action" value="role"/>
                <input type="submit" value="Add"/>
            </form>
        </section>
    </jsp:body>
</s:tabbed-page>