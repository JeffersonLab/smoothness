<%@tag description="Help Panel Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@attribute name="title"%>
<section>
    <h2><c:out value="${title}"/></h2>
    <h3>About <c:out value="${initParam.appName}"/></h3>
    <ul class="key-value-list">
        <li>
            <div class="li-key"><span>Release Version</span></div>
            <div class="li-value"><c:out value="${initParam.releaseNumber}"/></div>
        </li>
        <li>
            <div class="li-key"><span>Release Date</span></div>
            <div class="li-value"><c:out value="${initParam.releaseDate}"/></div>
        </li>
        <li>
            <div class="li-key"><span>Web lib Version</span></div>
            <div class="li-value"><c:out value="${initParam.smoothnessVersion}"/></div>
        </li>
        <c:set var="contentContact" value="${settings.get('CONTENT_CONTACT')}"/>
        <c:if test="${not empty contentContact}">
            <li>
                <div class="li-key"><span>Content Contact</span></div>
                <div class="li-value"><c:out value="${contentContact}"/></div>
            </li>
        </c:if>
        <c:set var="techContact" value="${settings.get('TECHNICAL_CONTACT')}"/>
        <c:if test="${not empty techContact}">
            <li>
                <div class="li-key"><span>Technical Contact</span></div>
                <div class="li-value"><c:out value="${techContact}"/></div>
            </li>
        </c:if>
        <c:if test="${pageContext.request.userPrincipal ne null}">
            <li>
                <div class="li-key"><span>Admins</span></div>
                <div class="li-value"><c:out value="${s:getMemberUsernameCsv(settings.get('ADMIN_ROLE_NAME'))}"/></div>
            </li>
        </c:if>
    </ul>
    <jsp:doBody var="bodyText"/>
    <c:set var="docCsv" value="${env[initParam.appSpecificEnvPrefix.concat('_DOC_CSV')]}"/>
    <c:if test="${not empty docCsv}">
    <c:set var="docArray" value="${docCsv.split(',')}"/>
    <h3>Documentation</h3>
    <ul>
        <c:forEach items="${docArray}" var="doc">
            <c:set var="mtuple" value="${doc.split('\\\|')}"/>
            <li><a href="${mtuple[0]}">${mtuple[1]}</a></li>
        </c:forEach>
    </ul>
    </c:if>
    <c:if test="${not empty fn:trim(bodyText)}">
        ${bodyText}
    </c:if>
    <c:choose>
        <c:when test="${pageContext.request.userPrincipal ne null}">
            <h3>Feedback Form</h3>
            <div style="font-weight: bold; margin-bottom: 4px;">(<span class="required-field"></span> required)</div>
            <fieldset id="feedback-fieldset">
                <form method="post" action="ajax/feedback">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key"><label class="required-field" for="subject">Subject</label></div>
                            <div class="li-value"><input type="text" id="subject" name="subject"/></div>
                        </li>
                        <li>
                            <div class="li-key"><label class="required-field" for="body">Message</label></div>
                            <div class="li-value"><textarea id="body" name="body"></textarea></div>
                        </li>
                    </ul>
                    <button type="button" id="send-feedback-button">Submit</button>
                </form>
            </fieldset>
        </c:when>
        <c:otherwise>
            <c:url var="feedbackUrl" value="/sso">
                <c:param name="returnUrl" value="${pageContext.request.contextPath}/help"/>
            </c:url>
            <h3><a href="${feedbackUrl}">Feedback Form</a></h3>
        </c:otherwise>
    </c:choose>
</section>