<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Page Two"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript">
            $(document).on("click", "#next-button, #previous-button", function() {
                $("#offset-input").val($(this).attr("data-offset"));
                $("#filter-form").submit();
            });
        </script>
    </jsp:attribute>        
    <jsp:body>
        <section class="scrollable-section">
            <h2><c:out value="${title}"/></h2>
            <form id="filter-form" class="filter-form" method="get" action="page-two">
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
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input type="submit" value="Apply"/>
                </fieldset>
            </form>         
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <table class="record-table">
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
            <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
            <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>
        </section>
    </jsp:body>         
</t:page>
