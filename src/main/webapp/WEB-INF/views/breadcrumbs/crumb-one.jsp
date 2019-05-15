<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb One"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
            <s:filter-flyout-widget requiredMessage="true" ribbon="true" resetButton="true">
                <form id="filter-form" method="get" action="crumb-one">
                    <div id="filter-form-panel" class="scrollable-filter-form">
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
                        <fieldset>         
                            <legend>Filter Set 3</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key"><span>Key 7</span></div>
                                    <div class="li-value"><input type="text" name="key7"/></div>
                                </li>
                                <li>
                                    <div class="li-key"><span>Key 8</span></div>
                                    <div class="li-value"><input type="text" name="key8"/></div>
                                </li>
                                <li>
                                    <div class="li-key"><span>Key 9</span></div>
                                    <div class="li-value"><input type="text" name="key9"/></div>
                                </li>
                                <li>
                                    <div class="li-key"><span>Key 10</span></div>
                                    <div class="li-value"><input type="text" name="key10"/></div>
                                </li>
                                <li>
                                    <div class="li-key"><span>Key 11</span></div>
                                    <div class="li-value"><input type="text" name="key11"/></div>
                                </li>
                                <li>
                                    <div class="li-key"><span>Key 12</span></div>
                                    <div class="li-value"><input type="text" name="key12"/></div>
                                </li>                                        
                            </ul>
                        </fieldset>	
                    </div>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <p>
                <a href="crumb-two">Keep Going!</a>
            </p>
        </section>
    </jsp:body>         
</t:page>
