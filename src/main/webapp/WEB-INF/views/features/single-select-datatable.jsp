<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="webapp" uri="http://jlab.org/webapp/functions"%>
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
        <script type="text/javascript">
            jlab.editableRowTable.entity = 'Movie';
            jlab.editableRowTable.dialog.width = 500;
            jlab.editableRowTable.dialog.height = 400;
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
            <t:filter-flyout-widget>
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
            </t:filter-flyout-widget>                              
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"></div>
            <c:if test="${fn:length(movieList) > 0}">
                <t:editable-row-table-controls excludeAdd="${false}" excludeDelete="${false}" excludeEdit="${false}"/>
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
                                <tr>
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
        <t:editable-row-table-dialog>
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
        </t:editable-row-table-dialog>            
        <div id="exit-fullscreen-panel">
            <button id="exit-fullscreen-button">Exit Full Screen</button>
        </div>
    </jsp:body>         
</t:features-page>