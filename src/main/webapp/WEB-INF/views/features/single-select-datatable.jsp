<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Single Select Datatable"/>
<t:features-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            td:nth-child(4) {
                text-align: right;
            }
        </style>        
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.resourceVersionNumber}/js/movie-table.js"></script>
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
                <form id="filter-form" method="get" action="single-select-datatable">
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
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <c:if test="${fn:length(movieList) > 0}">
                <s:editable-row-table-controls excludeAdd="${false}" excludeDelete="${false}" excludeEdit="${false}"/>
                <div id="chart-wrap" class="chart-wrap-backdrop">
                    <table class="data-table stripped-table uniselect-table editable-row-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Description</th>
                                <th>MPAA Rating</th>
                                <th>Duration (Minutes)</th>
                                <th>Release Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${movieList}" var="movie">
                                <tr data-id="${movie.movieId}">
                                    <td><c:out value="${movie.title}"/></td>
                                    <td><c:out value="${movie.description}"/></td>
                                    <td><c:out value="${movie.mpaaRating}"/></td>
                                    <td><c:out value="${movie.durationMinutes}"/></td>
                                    <td><c:out value="${movie.releaseDate}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>            
        </section>
        <%@ include file="/WEB-INF/fragments/editable-movie-dialog.jspf"%>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>         
</t:features-page>