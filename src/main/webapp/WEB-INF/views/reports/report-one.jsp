<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Report One"/>
<t:report-page title="${title}">  
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
            <s:filter-flyout-widget>
                <form id="filter-form" method="get" action="report-one">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key"><label class="required-field" for="range">Date Range</label></div>
                                    <div class="li-value">
                                        <select id="range" class="datetime-range seven-am-offset">
                                            <option value="1fiscalyear"${range eq '1fiscalyear' ? ' selected="selected"' : ''}>Previous Fiscal Year</option>
                                            <option value="1year"${range eq '1year' ? ' selected="selected"' : ''}>Previous Year</option>

                                            <c:if test="${previousRun ne null}">
                                                <option value="1run"${range eq '1run' ? ' selected="selected"' : ''}>Previous Run</option>
                                            </c:if>

                                            <option value="1month"${range eq '1month' ? ' selected="selected"' : ''}>Previous Month</option>
                                            <option value="1week"${range eq '1week' ? ' selected="selected"' : ''}>Previous Week (from Wed)</option>
                                            <option value="1shift"${range eq '1shift' ? ' selected="selected"' : ''}>Previous CC Shift</option>
                                            <option value="0fiscalyear"${range eq '0fiscalyear' ? ' selected="selected"' : ''}>Current Fiscal Year</option>
                                            <option value="0year"${range eq '0year' ? ' selected="selected"' : ''}>Current Year</option>

                                            <c:if test="${currentRun ne null}">
                                                <option value="0run"${range eq '0run' ? ' selected="selected"' : ''}>Current Run</option>
                                            </c:if>

                                            <option value="0month"${range eq '0month' ? ' selected="selected"' : ''}>Current Month</option>
                                            <option value="0week"${range eq '0week' ? ' selected="selected"' : ''}>Current Week (from Wed)</option>
                                            <option value="0shift"${range eq '0shift' ? ' selected="selected"' : ''}>Current CC Shift</option>
                                            <option value="past10days"${range eq 'past10days' ? ' selected="selected"' : ''}>Past 10 Days</option>
                                            <option value="past7days"${range eq 'past7days' ? ' selected="selected"' : ''}>Past 7 Days</option>
                                            <option value="past3days"${range eq 'past3days' ? ' selected="selected"' : ''}>Past 3 Days</option>
                                            <option value="1day"${range eq '1day' ? ' selected="selected"' : ''}>Yesterday</option>
                                            <option value="0day"${range eq '0day' ? ' selected="selected"' : ''}>Today</option>
                                            <option value="custom"${range eq 'custom' ? ' selected="selected"' : ''}>Custom...</option>
                                        </select>
                                    </div>
                                </li>
                            </ul>
                            <ul id="custom-date-range-list"
                                class="key-value-list" ${range ne 'custom' ? 'style="display: none;"' : ''}>
                                <li>
                                    <div class="li-key">
                                        <label class="required-field" for="start" title="Inclusive (Closed)">Start
                                            Date</label>
                                        <div class="date-note">(Inclusive)</div>
                                    </div>
                                    <div class="li-value">
                                        <input type="text" class="date-field" id="start" name="start" autocomplete="off"
                                               placeholder="DD-MMM-YYYY hh:mm" value="${param.start}"/>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label class="required-field" for="end" title="Exclusive (Open)">End Date</label>
                                        <div class="date-note">(Exclusive)</div>
                                    </div>
                                    <div class="li-value">
                                        <input type="text" class="date-field" id="end" name="end" autocomplete="off"
                                               placeholder="DD-MMM-YYYY hh:mm" value="${param.end}"/>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>	
                    </div>
                    <input type="hidden" name="qualified" value=""/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${message}"/></div>
            <div id="chart-wrap" class="chart-wrap-backdrop">
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
                            <td>Hello</td>
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
                        <tr>
                            <td>Hi</td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>         
</t:report-page>