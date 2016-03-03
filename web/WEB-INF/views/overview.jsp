<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Overview"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">              
    </jsp:attribute>        
    <jsp:body>
        <section>
            <t:filter-flyout-widget ribbon="true">
                <form id="filter-form" method="get" action="page-one">
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
            <p>This web application template is named &quot;Smoothness&quot;, and is designed to pair with the jQuery UI theme of the same name.</p>
            <h3>Intended Use</h3>
            <p>This template is optimized for Jefferson Lab desktop Intranet computers which have a screen size of 1280x1024 or greater.   Many of the applications that use this template have large tables and graphs.  This template is data-centric and provides support for pagination and query parameterization.  This template does not currently provide mobile device support.</p>
            <h3>Navigation</h3>
            <p>This template supports the following navigation styles:</p>
            <ul>
                <li>Top-Level Tab Navigation - global always visible</li>
                <li>Second-Level Tab Navigation - local to a top level tab</li>
                <li>Breadcrumb Navigation - arbitrary depth serial navigation.</li>
                <li>Dialog Navigation - content from other pages can be displayed in a dialog</li>
            </ul>
            <p>Any page can optionally prompt users for parameters via a parameter widget.</p>
            <h3>Common Pages and Styles</h3>
            <p>Modules are provided for a help/about page, a login page, and a report page as those are used in nearly every web application.  Modules for tables and lists are also provided.</p>
            <h3>Related Software</h3>
            <ul>
                <li>jQuery UI - dialogs, date picker, etc.</li>
                <li>jQuery - needed for jQuery UI and is generally used for DOM navigation and AJAX</li>
                <li>URI - a JavaScript library for manipulating URIs.</li>
                <li>flot - jQuery based graphing library</li>
                <li>select2 - fancy selection widget mainly needed to improve multiple selection</li>
            </ul>
        </section>
    </jsp:body>         
</t:page>