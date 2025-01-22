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
        <script type="text/javascript">
            $("#report-one-body").on("click", ".default-clear-panel", function () {
                $("#report1-start").val('');
                $("#report1-end").val('');
                $("#report1-date-range").val('custom').trigger('change');

                return false;
            });
        </script>
    </jsp:attribute>        
    <jsp:body>
        <section id="report-one-body">
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
            <s:filter-flyout-widget clearButton="true">
                <form class="filter-form" method="get" action="report-one">
                    <div class="filter-form-panel">
                        <fieldset>
                            <legend>First Filter</legend>
                            <s:date-range prefix="report1" datetime="${true}" sevenAmOffset="${true}"/>
                        </fieldset>
                    </div>
                    <input type="hidden" name="qualified" value=""/>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${message}"/></div>
            <div class="chart-wrap chart-wrap-backdrop">
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