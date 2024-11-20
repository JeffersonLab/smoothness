<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Crumb One"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <div class="dialog-content">
            <section>
                <s:filter-flyout-widget requiredMessage="true" ribbon="true" resetButton="true">
                    <form class="filter-form" method="get" action="crumb-one">
                        <div class="filter-form-panel" class="scrollable-filter-form">
                            <fieldset>
                                <legend>Filter Set 1</legend>
                                <ul class="key-value-list">
                                    <li>
                                        <div class="li-key"><span class="required-field">Key 1</span></div>
                                        <div class="li-value"><input type="text" name="key1"/></div>
                                    </li>
                                    <li>
                                        <div class="li-key"><span>Key 2</span></div>
                                        <div class="li-value"><input type="text" name="key2"/></div>
                                    </li>
                                    <li>
                                        <div class="li-key"><span>Key 3</span></div>
                                        <div class="li-value"><input type="text" name="key3"/></div>
                                    </li>
                                </ul>
                            </fieldset>
                            <fieldset>
                                <legend>Filter Set 2</legend>
                                <ul class="key-value-list">
                                    <li>
                                        <div class="li-key"><span>Key 4</span></div>
                                        <div class="li-value"><input type="text" name="key4"/></div>
                                    </li>
                                    <li>
                                        <div class="li-key"><span>Key 5</span></div>
                                        <div class="li-value"><input type="text" name="key5"/></div>
                                    </li>
                                    <li>
                                        <div class="li-key"><span class="key-label">Key 6</span></div>
                                        <div class="li-value"><input type="text" name="key6"/></div>
                                    </li>
                                </ul>
                            </fieldset>
                        </div>
                        <input class="filter-form-submit-button" type="submit" value="Apply"/>
                    </form>
                </s:filter-flyout-widget>
                <div class="float-breadbox right-only">
                    <ul>
                        <li class="hide-in-dialog">
                            <div><a href="crumb-one" class="dialog-opener">🗗</a></div>
                        </li>
                        <li>
                            <div><a href="crumb-two" class="partial-support">Next</a></div>
                        </li>
                    </ul>
                </div>
                <h2 class="page-header-title"><c:out value="${title}"/></h2>
                <p>
                    Hello World
                </p>
            </section>
        </div>
    </jsp:body>
</t:page>
