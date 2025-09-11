<%@tag description="Filter Flyout Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@attribute name="ribbon" required="false" type="java.lang.Boolean"%>
<%@attribute name="requiredMessage" required="false" type="java.lang.Boolean"%>
<%@attribute name="clearButton" required="false" type="java.lang.Boolean"%>
<%@attribute name="resetButton" required="false" type="java.lang.Boolean"%>
<div class="filter-flyout-widget${ribbon ? ' filter-flyout-ribbon' : ''}">
    <div class="filter-flyout-button"><a class="filter-flyout-link" href="#">Choose...</a></div>
    <div class="filter-flyout-handle">
        <div class="filter-flyout-panel">
            <button class="filter-flyout-close-button" title="Close">X</button>
            <c:if test="${resetButton or clearButton}">
                <div class="reset-clear-panel">
                    (${resetButton ? '<span class="default-reset-panel"><a href="#">Reset</a></span>' : ''}${resetButton and clearButton ? ' | ' : ''}${clearButton ? '<span class="default-clear-panel"><a href="#">Clear</a></span>' : ''})
                </div>
            </c:if>
            <div class="filter-flyout-title">Choose Parameters${requiredMessage ? ' (<span class="required-field"></span> required)' : ''}</div>
            <jsp:doBody/>
        </div>
    </div>
</div>                    