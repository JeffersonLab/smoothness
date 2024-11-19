<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
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
            jlab.pageDialog.width = 800;
            jlab.pageDialog.height = 600;
            /*jlab.pageDialog.minWidth = 800;
            jlab.pageDialog.minHeight = 600;*/
            jlab.pageDialog.resizable = true;
        </script>
    </jsp:attribute>        
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li><a href="crumb-one">Crumb One</a></li>
                <li><a href="crumb-two">Crumb Two</a></li>
                <li>Crumb Three</li>
            </ul>
        </div>
        <div class="dialog-content">
            <section>
                <h2 class="page-header-title"><c:out value="${title}"/></h2>
                <p><a href="crumb-four" data-dialog-title="Crumb Four" class="dialog-friendly">Keep Going</a></p>
            </section>
        </div>
    </jsp:body>         
</t:page>
