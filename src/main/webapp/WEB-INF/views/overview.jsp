<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
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
            <h3>How to Use</h3>
            <p>Create a new Java web project, and use the template CSS, JS, and Jar file.  The Jar file contains JSP Tag files (templating), as well as Filters and Servlets. You can copy default error and help JSPs from the template project as well.  It is also reasonable to follow the project file layout.  A CDN has been setup to allow easily using minified JS, CSS, and other web dependencies here:  <a href="https://cdn.acc.jlab.org">https://cdn.acc.jlab.org</a>.  You can obtain the Jar file from our internal artificatory repo: http://build.acc.jlab.org/artifactory using Maven/Gradle coordinates org.jlab:weblib.</p>
            <h3>Dependences</h3>
            <ul>
                <li>jQuery UI - dialogs, date picker, etc.</li>
                <li>jQuery - needed for jQuery UI and is generally used for DOM navigation and AJAX</li>
                <li>URI - a JavaScript library for manipulating URIs.</li>
                <li>flot - jQuery based graphing library</li>
                <li>select2 - fancy selection widget mainly needed to improve multiple selection</li>
                <li>Puppet Show - HTML to PDF / Image converter</li>
            </ul>
        </section>
    </jsp:body>         
</t:page>
