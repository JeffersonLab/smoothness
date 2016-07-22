<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="webapp" uri="http://jlab.org/webapp/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Parameter Persistence"/>
<t:features-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
            <div id="report-page-actions">
                <button id="fullscreen-button">Full Screen</button>
                <div id="export-widget">
                    <button id="export-menu-button">Export</button>
                    <ul id="export-menu">
                        <li id="image-menu-item">Image</li>
                        <li id="print-menu-item">Print</li>
                    </ul>
                </div>
            </div>
            <t:filter-flyout-widget>
                <form id="filter-form" method="get" action="parameter-persistence">
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
            </t:filter-flyout-widget>                              
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>         
</t:features-page>