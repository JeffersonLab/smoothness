<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Crumb Two"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="crumb-one">Crumb One</a></li>
                <li>Crumb Two</li>
            </ul>
        </div>
        <section>
            <div id="filter-flyout-widget" class="filter-flyout-ribbon">
                <div id="filter-flyout-button">
                    <a id="filter-flyout-link" href="#">Choose...</a>
                </div>
                <div id="filter-flyout-handle">
                    <div id="filter-flyout-panel" style="display: none;">
                        <button id="filter-flyout-close-button" title="Close">X</button>
                        <div id="filter-flyout-title">Choose Parameters</div>
                        <form id="filter-form" method="get" action="crumb-two">
                            <div id="filter-form-panel">
                                <fieldset>
                                    <legend>Filter</legend>
                                    <ul class="key-value-list">                       
                                        <li>
                                            <div class="li-key">
                                                <label for="lastname">Lastname</label>
                                            </div>
                                            <div class="li-value">
                                                <input id="lastname" name="lastname" value="${fn:escapeXml(param.lastname)}"/>
                                                (use % as wildcard)
                                            </div>
                                        </li>                        
                                    </ul>
                                </fieldset>
                            </div>
                            <input type="hidden" id="offset-input" name="offset" value="0"/>                                
                            <input id="filter-form-submit-button" type="submit" value="Apply"/>
                        </form>
                    </div>
                </div>
            </div>                        
            <h2 id="page-header-title"><c:out value="${title}"/></h2>
            <p>
                <a href="crumb-three">Even More Ahead!</a>
            </p>              
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <div class="dialog-content">
                <table class="data-table stripped-table">
                    <thead>
                        <tr>
                            <th>Lastname</th>
                            <th>Firstname</th>
                            <th>Username</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${staffList}" var="staff">
                            <tr>
                                <td><c:out value="${staff.lastname}"/></td>
                                <td><c:out value="${staff.firstname}"/></td>
                                <td><c:out value="${staff.username}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
            <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>                              
        </section>
    </jsp:body>         
</t:page>
