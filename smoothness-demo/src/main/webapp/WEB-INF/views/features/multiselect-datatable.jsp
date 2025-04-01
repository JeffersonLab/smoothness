<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Multiselect Datatable"/>
<t:features-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css"
              href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/movie-table.css"/>
        <!-- This is just demonstrating that style tags with an ID are copied in partial dialogs -->
        <style id="movie-table-inline-style">
            .multiselect-instructions {
                font-style: italic;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/movie-table.js"></script>
        <script type="text/javascript"
                src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/movie-table-multi.js"></script>
        <!-- This is just demonstrating that script tags with an ID are copied in partial dialogs -->
        <script id="movie-table-inline-script">
            $(document).on("click", ".multiselect-instructions", function() {
                console.log("clicked on instructions");
            });
        </script>
        <script id="ignore-me-because-of-class" class="full-page-only">
            // scripts with class full-page-only are ignored in partial page loads
            $(document).on("click", ".multiselect-instructions", function() {
                console.log("full page only - clicked on instructions");
            });
        </script>
        <script>
            // inline script tags without an ID are ignored by partial page loads
            $(function () {
                movieTable.pageInit();
                movieTableMulti.pageInit();
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
            <s:filter-flyout-widget>
                <form class="filter-form" method="get" action="multiselect-datatable">
                    <div class="filter-form-panel">
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
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <c:if test="${fn:length(movieList) > 0}">
                <s:editable-row-table-controls excludeAdd="${false}" excludeDelete="${false}" excludeEdit="${true}"
                                               multiselect="${true}">
                    <button type="button" id="open-edit-rating-dialog-button" class="selected-row-action"
                            disabled="disabled">Edit Rating
                    </button>
                </s:editable-row-table-controls>
                <div class="chart-wrap chart-wrap-backdrop">
                    <table id="movie-table" class="data-table stripped-table multiselect-table editable-row-table">
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Description</th>
                            <th>MPAA Rating</th>
                            <th>Duration (Minutes)</th>
                            <th>Release Date</th>
                            <th>Link</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${movieList}" var="movie">
                            <tr data-id="${movie.movieId}">
                                <td><c:out value="${movie.title}"/></td>
                                <td><c:out value="${movie.description}"/></td>
                                <td><c:out value="${movie.mpaaRating}"/></td>
                                <td><c:out value="${movie.durationMinutes}"/></td>
                                <td><fmt:formatDate value="${movie.releaseDate}"
                                                    pattern="${s:getFriendlyDatePattern()}"/></td>
                                <td><a href="#">Click</a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="multiselect-instructions">Hold down the control (Ctrl) or shift key when clicking to
                        select multiple. Hold down the Command (⌘) key on Mac.
                    </div>
                </div>
            </c:if>
        </section>
        <%@ include file="/WEB-INF/fragments/editable-movie-dialog.jspf" %>
        <div class="dialog" id="rating-dialog" title="Edit Rating">
            <section>
                <form>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="movie-selected-row-list">Movie</label>
                            </div>
                            <div class="li-value">
                                <ul id="movie-selected-row-list" class="selected-row-list"></ul>
                                <span id="dialog-selected-count"></span>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="edit-rating">MPAA Rating</label>
                            </div>
                            <div class="li-value">
                                <select id="edit-rating">
                                    <option value="">&nbsp;</option>
                                    <option>G</option>
                                    <option>PG</option>
                                    <option>PG-13</option>
                                    <option>R</option>
                                    <option>NC-17</option>
                                </select>
                            </div>
                        </li>
                    </ul>
                    <div class="rows-differ-message">WARNING: One or more selected movies have an existing rating that
                        differs
                    </div>
                </form>
            </section>
            <div class="dialog-button-panel">
                <button type="button" id="rating-save-button" class="dialog-submit-button">Save</button>
                <button type="button" class="dialog-close-button">Cancel</button>
            </div>
        </div>
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>
</t:features-page>