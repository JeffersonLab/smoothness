<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb Three"/>
<s:page title="${title}">
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
                <li><a href="crumb-one" class="partial-support">Crumb One</a></li>
                <li><a href="crumb-two" class="partial-support">Crumb Two</a></li>
                <li>Crumb Three</li>
            </ul>
        </div>
        <div class="dialog-content">
            <section>
                <div class="float-breadbox">
                    <ul>
                        <li>
                            <div><a href="crumb-two" class="partial-support">Previous</a></div>
                        </li>
                        <li class="hide-in-dialog">
                            <div><a href="crumb-three" class="dialog-opener">🗗</a></div>
                        </li>
                        <li><a href="crumb-four" class="partial-support">Next</a></li>
                    </ul>
                </div>
                <h2 class="page-header-title"><c:out value="${title}"/></h2>
                <p>Nothing to see here.</p>
            </section>
        </div>
    </jsp:body>         
</s:page>
