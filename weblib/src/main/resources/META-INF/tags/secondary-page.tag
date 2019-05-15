<%@tag description="Secondary Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="title"%>
<%@attribute name="category"%>
<%@attribute name="keycloakClientIdKey"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="primaryNavigation" fragment="true"%>
<%@attribute name="secondaryNavigation" fragment="true"%>
<s:primary-page title="${category} - ${title}" keycloakClientIdKey="${keycloakClientIdKey}">
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="navigation">
        <jsp:invoke fragment="primaryNavigation"/>
    </jsp:attribute>
    <jsp:body>
        <div id="two-columns">
            <div id="left-column">
                <section>
                    <h2 id="left-column-header"><c:out value="${category}"/></h2>
                    <nav id="secondary-nav">
                        <jsp:invoke fragment="secondaryNavigation"/>
                    </nav>
                </section>
            </div>
            <div id="right-column">
                <jsp:doBody/>
            </div>
        </div>       
    </jsp:body>         
</s:primary-page>
