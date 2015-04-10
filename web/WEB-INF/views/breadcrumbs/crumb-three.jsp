<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb Three"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript">
        </script>
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="crumb-one">Crumb One</a></li>
                <li><a href="crumb-two">Crumb Two</a></li>
                <li>Crumb Three</li>
            </ul>
        </div>            
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <p><a href="crumb-two" class="dialog-ready" data-dialog-title="Hello World">Check this out</a></p>
        </section>
    </jsp:body>         
</t:page>
