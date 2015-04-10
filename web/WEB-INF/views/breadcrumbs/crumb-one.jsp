<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb One"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
            <div id="filter-flyout-widget" class="filter-flyout-ribbon">
                <div id="filter-flyout-button">
                    <a id="filter-flyout-link" href="#">Choose...</a>
                </div>
                <div id="filter-flyout-handle">
                    <div id="filter-flyout-panel" style="display: none;">
                        <button id="filter-flyout-close-button" title="Close">X</button>
                        <div id="filter-flyout-title">Choose Parameters</div>
                        <form id="filter-form" method="get" action="primary.html">
                            <div id="filter-form-panel">
                                <fieldset>
                                    <legend>Filter</legend>
                                    <ul class="key-value-list">
                                        <li>
                                            <div class="li-key"><span class="key-label">Key 1</span></div>
                                            <div class="li-value">Value 1</div>
                                        </li>
                                        <li>
                                            <div class="li-key"><span class="key-label">Key 2</span></div>
                                            <div class="li-value">Value 2</div>
                                        </li>
                                    </ul>
                                </fieldset>	
                            </div>
                            <input id="filter-form-submit-button" type="submit" value="Apply"/>
                        </form>
                    </div>
                </div>
            </div>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <p>
                <a href="crumb-two">Keep Going!</a>
            </p>
        </section>
    </jsp:body>         
</t:page>
