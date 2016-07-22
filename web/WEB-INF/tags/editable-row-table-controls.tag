<%@tag description="Filter Flyout Widget" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<%@attribute name="excludeAdd" required="false" type="java.lang.Boolean"%>
<%@attribute name="excludeEdit" required="false" type="java.lang.Boolean"%>
<%@attribute name="excludeDelete" required="false" type="java.lang.Boolean"%>
<div id="editable-row-table-control-panel">
    <c:if test="${not excludeAdd}">
        <button type="button" id="open-add-row-dialog-button">Add</button>        
    </c:if>
    <c:if test="${not excludeEdit}">
        <button type="button" id="open-edit-row-dialog-button" disabled="disabled">Edit</button>
    </c:if>
    <c:if test="${not excludeDelete}">
        <button type="button" id="remove-row-button" disabled="disabled">Remove</button>
    </c:if>
    <jsp:doBody/>
</div>                   