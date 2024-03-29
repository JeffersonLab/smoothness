<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb Four"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li><a href="crumb-one">Crumb One</a></li>
                <li><a href="crumb-two">Crumb Two</a></li>
                <li><a href="crumb-three">Crumb Three</a></li>
                <li>Crumb Four</li>
            </ul>
        </div>            
        <section>
            <div class="ribbon-breadbox">
                <ul>
                    <li>
                        <s:filter-flyout-widget>
                            <form id="filter-form" method="get" action="crumb-two">
                                <div id="filter-form-panel">
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
                                <input type="hidden" id="offset-input" name="offset" value="0"/>
                                <input id="filter-form-submit-button" type="submit" value="Apply"/>
                            </form>
                        </s:filter-flyout-widget>
                    </li>
                    <li><div>Item 1</div></li>
                    <li><div>Item 2</div></li>
                    <li><div>Item 3</div></li>
                </ul>
            </div>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <p>The End</p>
        </section>
    </jsp:body>         
</t:page>
