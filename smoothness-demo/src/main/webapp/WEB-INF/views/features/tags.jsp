<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Tags"/>
<t:features-page title="${title}">
    <jsp:body>
        <section>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <p>The weblib's tags with options the other pages don't use.</p>

            <h3>Filter Flyout Widget</h3>
            <p>No options:</p>
            <div id="plain-flyout">
                <s:filter-flyout-widget>
                    <p>No options: a Choose... link that opens this panel.</p>
                </s:filter-flyout-widget>
            </div>
            <p>ribbon="true", which hangs the link off the panel's left edge:</p>
            <div id="ribbon-flyout">
                <s:filter-flyout-widget ribbon="true">
                    <p>ribbon="true"</p>
                </s:filter-flyout-widget>
            </div>
            <p>requiredMessage, resetButton, and clearButton, around two date ranges:</p>
            <div id="date-flyout">
                <s:filter-flyout-widget requiredMessage="true" resetButton="true" clearButton="true">
                    <form class="filter-form" method="get" action="tags">
                        <fieldset>
                            <legend>Date Range: dates, from midnight</legend>
                            <s:date-range prefix="dates"/>
                        </fieldset>
                        <fieldset>
                            <legend>Date Range: required date and time, from 7:00</legend>
                            <s:date-range prefix="datetimes" required="true" datetime="true" sevenAmOffset="true"/>
                        </fieldset>
                    </form>
                </s:filter-flyout-widget>
            </div>

            <h3>Editable Row Table Controls</h3>
            <p>multiselect="true" and excludeEdit="true", for the table below:</p>
            <s:editable-row-table-controls multiselect="true" excludeEdit="true"/>
            <table id="tag-table" class="data-table stripped-table multiselect-table editable-row-table">
                <thead>
                    <tr><th>Row</th></tr>
                </thead>
                <tbody>
                    <tr data-id="1"><td>One</td></tr>
                    <tr data-id="2"><td>Two</td></tr>
                    <tr data-id="3"><td>Three</td></tr>
                </tbody>
            </table>

            <h3>Loose Page</h3>
            <p><a id="loose-page-link" href="${pageContext.request.contextPath}/features/loose-page-without-smoothness">A loose page without the smoothness styles and scripts</a></p>
        </section>
    </jsp:body>
</t:features-page>
