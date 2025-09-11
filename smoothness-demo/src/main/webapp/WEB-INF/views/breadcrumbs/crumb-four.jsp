<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="title" value="Crumb Four"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li><a href="crumb-one" class="partial-support">Crumb One</a></li>
                <li><a href="crumb-two" class="partial-support">Crumb Two</a></li>
                <li><a href="crumb-three" class="partial-support">Crumb Three</a></li>
                <li>Crumb Four</li>
            </ul>
        </div>
        <div class="dialog-content">
            <section>
                <div class="ribbon-breadbox">
                    <ul>
                        <li>
                            <s:filter-flyout-widget>
                                <form class="filter-form" method="get" action="crumb-four">
                                    <div class="filter-form-panel">
                                        <fieldset>
                                            <legend>Filter</legend>
                                            <ul class="key-value-list">
                                                <li>
                                                    <div class="li-key">
                                                        <label for="search">Search</label>
                                                    </div>
                                                    <div class="li-value">
                                                        <input id="search" name="search"
                                                               value="${fn:escapeXml(param.search)}"/>
                                                        <div>(matches any attribute which starts with substring)</div>
                                                    </div>
                                                </li>
                                            </ul>
                                        </fieldset>
                                    </div>
                                    <input type="hidden" class="offset-input" name="offset" value="0"/>
                                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                                </form>
                            </s:filter-flyout-widget>
                        </li>
                        <li>
                            <div>Item 1</div>
                        </li>
                        <li>
                            <div>Item 2</div>
                        </li>
                        <li>
                            <div>Item 3</div>
                        </li>
                    </ul>
                </div>
                <div class="float-breadbox left-only">
                    <ul>
                        <li>
                            <div><a href="crumb-three" class="partial-support">Previous</a></div>
                        </li>
                        <li class="hide-in-dialog">
                            <div><a href="crumb-four" class="dialog-opener">🗗</a></div>
                        </li>
                    </ul>
                </div>
                <h2 class="page-header-title"><c:out value="${title}"/></h2>
                <p>Try this in a dialog: <a href="${pageContext.request.contextPath}/features/multiselect-datatable" class="dialog-opener">Multi-select Datatable</a></p>
            </section>
        </div>
    </jsp:body>
</s:page>
