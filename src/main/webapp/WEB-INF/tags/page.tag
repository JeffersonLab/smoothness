<%@tag description="The Site Page Template" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<c:url var="domainRelativeReturnUrl" scope="request" context="/" value="${requestScope['javax.servlet.forward.request_uri']}${requestScope['javax.servlet.forward.query_string'] ne null ? '?'.concat(requestScope['javax.servlet.forward.query_string']) : ''}"/>
<c:set var="currentPath" scope="request" value="${requestScope['javax.servlet.forward.servlet_path']}"/>
<!DOCTYPE html>
<html>
    <head>        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><c:out value="${initParam.appShortName}"/> - ${title}</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/img/favicon.ico"/>        
        <link rel="stylesheet" type="text/css" href="//cdn.acc.jlab.org/jquery-ui/1.10.3/theme/smoothness/jquery-ui.min.css"/>        
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/css/smoothness.css"/>        
        <jsp:invoke fragment="stylesheets"/>
    </head>
    <body class="${param.print eq 'Y' ? 'print ' : ''} ${param.fullscreen eq 'Y' ? 'fullscreen' : ''}">
        <c:if test="${env['SERVER_MESSAGE'] ne null}">
            <div id="notification-bar"><c:out value="${env['SERVER_MESSAGE']}"/></div>
        </c:if>        
        <div id="page">
            <header>
                <h1><span id="page-header-logo"></span> <span id="page-header-text"><c:out value="${initParam.appName}"/></span></h1>
                <div id="auth">
                    <c:choose>
                        <c:when test="${pageContext.request.userPrincipal ne null}">
                            <div id="username-container">
                                <c:out value="${pageContext.request.userPrincipal.name.split(':')[2]}"/>
                            </div>
                            <form id="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
                                <button type="submit" value="Logout">Logout</button>
                                <input type="hidden" name="returnUrl" value="${fn:escapeXml(domainRelativeReturnUrl)}"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <c:set var="absHostUrl" value="${'https://'.concat(pageContext.request.getServerName())}"/>
                            <c:url value="/sso" var="loginUrl">
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <c:url value="/sso" var="suUrl">
                                <c:param name="kc_idp_hint" value="ace-su-keycloak-oidc"/>
                                <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
                            </c:url>
                            <a id="login-link" href="${loginUrl}">Login</a> (<a id="su-link" href="${suUrl}" href="#">SU</a>)
                        </c:otherwise>
                    </c:choose>
                </div>
                <nav id="primary-nav">
                    <ul>
                        <li${'/overview' eq currentPath ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/overview">Overview</a>
                        </li>  
                        <li${fn:startsWith(currentPath, '/features') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/features/multiselect-datatable">Features</a>
                        </li>                        
                        <li${fn:startsWith(currentPath, '/breadcrumbs') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/breadcrumbs/crumb-one">Breadcrumbs</a>
                        </li>                        
                        <li${fn:startsWith(currentPath, '/reports') ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/reports/report-one">Reports</a>
                        </li>                    
                        <c:if test="${pageContext.request.isUserInRole('ADMIN')}">
                            <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
                                <a href="${pageContext.request.contextPath}/setup/setup-one">Setup</a>
                            </li>            
                        </c:if>
                        <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
                            <a href="${pageContext.request.contextPath}/help">Help</a>
                        </li>
                    </ul>
                </nav>                
            </header>
            <div id="content">     
                <div id="content-liner">
                    <jsp:doBody/>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="//cdn.acc.jlab.org/jquery/1.10.2.min.js"></script>
        <script type="text/javascript" src="//cdn.acc.jlab.org/jquery-ui/1.10.3/jquery-ui.min.js"></script>  
        <script type="text/javascript" src="//cdn.acc.jlab.org/uri/uri-1.14.1.min.js"></script>        
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/smoothness.js"></script>
        <script type="text/javascript">
            jlab.contextPath = '${pageContext.request.contextPath}';
            jlab.logbookHost = '${env["LOGBOOK_HOSTNAME"]}';
            jlab.keycloakHostname = '${env["KEYCLOAK_HOSTNAME"]}';
            jlab.clientId = '${env["KEYCLOAK_CLIENT_ID_SMOOTHNESS_TEMPLATE"]}';
            <c:url var="url" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/auth">
            <c:param name="client_id" value="account"/>
            <c:param name="kc_idp_hint" value="cue-keycloak-oidc"/>
            <c:param name="response_type" value="code"/>
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
            </c:url>
            jlab.loginUrl = '${url}';
            <c:url var="url" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/protocol/openid-connect/logout">
            <c:param name="redirect_uri" value="https://${env['KEYCLOAK_HOSTNAME']}/auth/realms/jlab/account/"/>
            </c:url>
            jlab.logoutUrl = '${url}';
        </script>
        <jsp:invoke fragment="scripts"/>        
    </body>
</html>