<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="webapp" uri="http://jlab.org/webapp/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Report One"/>
<t:report-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
                <section>
                    <div id="filter-flyout-widget">
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
                    <table class="data-table stripped-table constrained-table compact-table">
                        <thead>
                            <tr>
                                <th>Column 1</th>
                                <th>Column 2</th>
                                <th>Column 3</th>
                                <th>Column 4</th>
                                <th>Column 5</th>
                                <th>Column 6</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td></td>
                                <td>This cell has way too much text in it to demonstrate overflow, wrapping, and word breaks: abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvwxyz</td>
                                <td></td>
                                <td></td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </section>
            </div>
        </div>
    </jsp:body>         
</t:report-page>