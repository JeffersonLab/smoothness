<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Multiselect Datatable"/>
<t:features-page title="${title}">  
    <jsp:attribute name="stylesheets">
        <style type="text/css">
            td:nth-child(4) {
                text-align: right;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript">
            jlab.editableRowTable.entity = 'Movie';
            jlab.editableRowTable.dialog.width = 500;
            jlab.editableRowTable.dialog.height = 400;

            jlab.validateTableRowForm = function () {
                return true;
            };

            $(document).on("click", "#open-edit-rating-dialog-button", function () {
                var idArray = new Array(),
                        titleArray = new Array(),
                        descriptionArray = new Array(),
                        ratingArray = new Array(),
                        durationArray = new Array(),
                        releaseArray = new Array();

                if ($(".multiselect-table .selected-row").length < 1) {
                    window.console && console.log('No rows selected');
                    return;
                }

                $(".multiselect-table .selected-row").each(function () {
                    var id = $(this).attr("data-id"),
                            title = $(this).find("td:nth-child(1)").text(),
                            description = $(this).find("td:nth-child(2)").text(),
                            rating = $(this).find("td:nth-child(3)").text(),
                            duration = $(this).find("td:nth-child(4)").text(),
                            release = $(this).find("td:nth-child(5)").text();

                    idArray.push(id);
                    titleArray.push(title);
                    descriptionArray.push(description);
                    ratingArray.push(rating);
                    durationArray.push(duration);
                    releaseArray.push(release);
                });

                var $selectedList = $("#selected-row-list");

                $selectedList.attr("data-id-json", JSON.stringify(idArray));

                $selectedList.empty();

                for (var i = 0; i < titleArray.length; i++) {
                    $selectedList.append('<li>' + titleArray[i] + '</li>');
                }

                var count = $("#selected-count").text() * 1;
                var rowStr = (count === 1) ? ' Movie' : ' Movies';
                $("#dialog-selected-count").text(count + rowStr);

                $("#edit-rating").val(ratingArray[0]);

                var rowsDiffer = false;

                for (var i = 1; i < ratingArray.length; i++) {
                    if (ratingArray[0] !== ratingArray[i]) {
                        rowsDiffer = true;
                        break;
                    }
                }

                if (rowsDiffer) {
                    $(".rows-differ-message").show();
                } else {
                    $(".rows-differ-message").hide();
                }

                $("#rating-dialog").dialog("open");
            });

            $(document).on("click", "#table-row-save-button", function () {
                if (!jlab.validateTableRowForm()) {
                    return;
                }

                var title = $("#row-title").val(),
                        description = $("#row-description").val(),
                        rating = $("#row-rating").val(),
                        duration = $("#row-duration").val(),
                        release = $("#row-release").val(),
                        url = jlab.contextPath + "/actions/add-component",
                        data = {title: title, description: description, rating: rating, duration: duration, release: release},
                $dialog = $("#table-row-dialog");

                jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
            });
            
            $(function() {
                $("#rating-dialog").dialog({
                    autoOpen: false,
                    width: 500,
                    height: 500,
                    resizable: false
                });
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
                <form id="filter-form" method="get" action="multiselect-datatable">
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
                <s:editable-row-table-controls excludeAdd="${false}" excludeDelete="${false}" excludeEdit="${true}" multiselect="${true}">
                    <button type="button" id="open-edit-rating-dialog-button" class="selected-row-action" disabled="disabled">Edit Rating</button>
                </s:editable-row-table-controls>
                <div id="chart-wrap" class="chart-wrap-backdrop">
                    <table class="data-table stripped-table multiselect-table editable-row-table">
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
                                <tr>
                                    <td><c:out value="${movie.title}"/></td>
                                    <td><c:out value="${movie.description}"/></td>
                                    <td><c:out value="${movie.mpaaRating}"/></td>
                                    <td><c:out value="${movie.durationMinutes}"/></td>
                                    <td><c:out value="${movie.releaseDate}"/></td>
                                    <td><a href="#">Click</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="multiselect-instructions">Hold down the control (Ctrl) or shift key when clicking to select multiple. Hold down the Command (⌘) key on Mac.</div>
                </div>
            </c:if>
        </section>
        <s:editable-row-table-dialog>
            <form id="row-form">
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="row-title">Title</label>
                        </div>
                        <div class="li-value">
                            <input type="text" maxlength="128" required="required" id="row-title"/>
                        </div>
                    </li>                       
                    <li>
                        <div class="li-key">
                            <label for="row-description">Description</label>
                        </div>
                        <div class="li-value">
                            <input type="text" maxlength="512" required="required" id="row-description"/>
                        </div>
                    </li> 
                    <li>
                        <div class="li-key">
                            <label for="row-rating">MPAA Rating</label>
                        </div>
                        <div class="li-value">
                            <input type="text" maxlength="5" id="row-rating"/>
                        </div>
                    </li> 
                    <li>
                        <div class="li-key">
                            <label for="row-duration">Duration (Minutes)</label>
                        </div>
                        <div class="li-value">
                            <input type="number" min="0" id="row-duration"/>
                        </div>
                    </li>
                    <li>
                        <div class="li-key">
                            <label for="row-release">Release Date</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="row-release"/>
                        </div>
                    </li>                    
                </ul>  
            </form>
        </s:editable-row-table-dialog>
        <div class="dialog" id="rating-dialog" title="Edit Rating">
            <form>
                <ul class="key-value-list">
                    <li>
                        <div class="li-key">
                            <label for="selected-row-list">Movie</label>
                        </div>
                        <div class="li-value">
                            <ul id="selected-row-list"></ul>
                            <span id="dialog-selected-count"></span>
                        </div>
                    </li>                       
                    <li>
                        <div class="li-key">
                            <label for="edit-rating">MPAA Rating</label>
                        </div>
                        <div class="li-value">
                            <input type="text" maxlength="5" id="edit-rating"/>
                        </div>
                    </li>                    
                </ul>
                <div class="rows-differ-message">WARNING: One or more selected movies have an existing rating that differs from the above</div>
            </form>
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