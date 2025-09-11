<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Report Two"/>
<t:report-page title="${title}">  
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <c:choose>
            <c:when test="${'NONE' eq resourceLocation}">
            </c:when>
            <c:when test="${'CDN' eq resourceLocation}">
                <script src="//${env[initParam.appSpecificEnvPrefix.concat('_CDN_SERVER')]}/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="//${env[initParam.appSpecificEnvPrefix.concat('_CDN_SERVER')]}/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:when>
            <c:otherwise><!-- LOCAL -->
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.min.js"></script>
                <script src="${pageContext.request.contextPath}/resources/jquery-plugins/flot/0.8.3/jquery.flot.resize.min.js"></script>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript">
            var jlab = jlab || {};

            jlab.doChart = function() {

                /*Wrap classes must be added before chart is generated*/
                $(".chart-wrap").addClass("has-x-axis-label").addClass("has-y-axis-label");

                var $placeholder = $("#report2"),
                        datasource = [
                            {label: 'Series 1', color: 'blue', data: [[0, 1], [1, 2], [2, 4], [3, 8], [4, 16]]},
                            {label: 'Series 2', color: 'red', data: [[0, 0], [1, 1], [2, 2], [3, 3], [4, 4]]}
                        ],
                        options = {
                            series: {
                                points: {
                                    show: true
                                },
                                lines: {
                                    show: true
                                }

                            },
                            xaxis: {
                                min: 0,
                                max: 4,
                                tickDecimals: 0,
                                ticks: 5
                            },
                            yaxis: {
                                min: 0,
                                tickDecimals: 0,
                                ticks: 4
                            },
                            grid: {
                                borderColor: 'gray',
                                backgroundColor: {colors: ["#fff", "#eee"]}
                            },
                            legend: {
                                backgroundOpacity: 0
                            }
                        };

                $.plot($placeholder, datasource, options);

                /*These must be added after chart is generated*/
                jlab.addXAxisLabel('X Axis Label', $placeholder);
                jlab.addYAxisLabel('Y Axis Label', $placeholder);
            };

            $(function() {
                jlab.doChart();
            });

        </script>
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
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <s:chart-widget placeholderId="report2">
            </s:chart-widget>
        </section>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>            
    </jsp:body>         
</t:report-page>