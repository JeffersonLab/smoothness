<%@tag description="Smoothness Style Links" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@attribute name="smoothnessCdn"%>
<%@attribute name="smoothnessServer"%>
<%@attribute name="smoothnessVersion"%>
<c:choose>
    <c:when test="${smoothnessCdn}">
        <link rel="stylesheet" type="text/css" href="//${smoothnessServer}/jquery-ui/1.14.1/theme/smoothness/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="//${smoothnessServer}/jlab-theme/smoothness/${smoothnessVersion}/css/smoothness.min.css"/>
        <link rel="stylesheet" type="text/css" href="//${smoothnessServer}/jquery-plugins/select2/4.0.13/select2.min.css"/>
        <link rel="stylesheet" type="text/css" href="//${smoothnessServer}/jquery-plugins/timepicker/jquery-ui-timepicker-1.5.0.css"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-ui-1.14.1/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/smoothness.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-plugins/select2/4.0.13/select2.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery-plugins/timepicker/1.5.0.min.css"/>
    </c:otherwise>
</c:choose>