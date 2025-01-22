<%@tag description="Chart Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@attribute name="placeholderId"%>
<div class="chart-legend-panel">
    <div class="chart-panel">
        <div class="chart-wrap">
            <div id="${placeholderId}" class="chart-placeholder"></div>
        </div>
    </div>
    <div class="legend-panel">
        <jsp:doBody/>       
    </div>       
</div>