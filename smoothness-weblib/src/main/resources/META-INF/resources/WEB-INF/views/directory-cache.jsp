<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="User Authorization Cache"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2>Directory Cache</h2>
            <p>Authentication and authorization is provided by Keycloak via OpenID Connect (OIDC) to enable Single Sign-on (SSO) and to manage users and roles centrally instead of redundantly in each app.
            </p>
            <p>
                There are two separate APIs powered by Keycloak in use: 1. Container Managed Security (CMS) and 2. App Directory Service lookups.  Authentication (AuthN) is always done using CMS and is never cached - passwords are never available to the app.  CMS authorization (AuthZ) applies when Servlets delegate to the container with HttpServletRequest.isUserInRole() and EJBs annotate methods with @RolesAllowed.   In these cases the authorization (AuthZ) is queried once per user login session and requires users to logout and re-login for changes in role membership or profile info to take effect.
            </p>
            <p>CMS is limited and only reveals a users profile to themselves, and does not allow viewing other's profile info.  Further, CMS will only reveal if the authenticated user is a member of a given role, not list all users whom are members.  Finally, per-method roles allowed lists isn't fine-grained enough in some cases such as when method arguments are used to determine whom is allowed.  Therefore, some apps need the extra directory service functionality.</p>
            <p>Directory service queries (via UserAuthorizationService) for role membership and user profiles (firstname, lastname, and email) can be performed against Keycloak as well, and the results are cached in memory and displayed here.  When updating role membership in Keycloak it will be necessary to clear this cache for changes to take effect.  This cache is automatically cleared each night at midnight.</p>
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
            <form method="post" action="directory-cache">
                <input type="hidden" name="action" value="clear"/>
                <input type="submit" value="Clear"/>
            </form>
            <h3>Search and Add User to Cache</h3>
            <form method="post" action="directory-cache">
                <label>Username:</label>
                <input type="text" name="username" value=""/>
                <input type="hidden" name="action" value="user"/>
                <input type="submit" value="Add"/>
            </form>
            <h3>Search and Add Role to Cache</h3>
            <form method="post" action="directory-cache">
                <label>Role Name:</label>
                <input type="text" name="role" value=""/>
                <input type="hidden" name="action" value="role"/>
                <input type="submit" value="Add"/>
            </form>
        </section>
    </jsp:body>
</s:setup-page>