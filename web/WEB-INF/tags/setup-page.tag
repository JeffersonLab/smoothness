<%@tag description="The Setup Page Template Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<%@attribute name="title"%>
<%@attribute name="stylesheets" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<t:page title="Setup - ${title}"> 
    <jsp:attribute name="stylesheets">       
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>    
    <jsp:body>
        <div class="two-column-content-liner">
            <div id="two-column-content">
                <div id="left-column" class="sub-nav">
                    <section>
                        <h2>Setup</h2>
                        <nav id="left-nav">
                            <ul>
                                <li${'/setup/setup-one' eq currentPath ? ' class="sub-current"' : ''}><a href="${pageContext.request.contextPath}/setup/setup-one">Setup One</a></li>
                                <li${'/setup/setup-two' eq currentPath ? ' class="sub-current"' : ''}><a href="${pageContext.request.contextPath}/setup/setup-two">Setup Two</a></li>
                            </ul>  
                        </nav>
                    </section>
                </div>
                <div id="right-column">        
                    <jsp:doBody/>
                </div>
            </div>
        </div>
    </jsp:body>         
</t:page>
