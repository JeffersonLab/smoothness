<%@tag description="Chart Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="placeholderId" required="false" type="java.lang.String"%>
<c:set value="${empty placeholderId ? 'chart-placeholder' : placeholderId}" var="placeholderId"/>
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