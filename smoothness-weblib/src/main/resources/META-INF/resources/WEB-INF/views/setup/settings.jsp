<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness" %>
<c:set var="title" value="Settings"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <style>
            td:nth-child(1),
            td:nth-child(2) {
                width: 255px;
                word-break: break-word;
                position: relative;
                padding-bottom: 2.5em;
            }
            td:nth-child(3) {
                word-break: break-word;
            }
            div.bottom {
                position: absolute;
                bottom: 8px;
                font-size: 0.8em;
                color: #595959;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script>
            jlab.editableRowTable = jlab.editableRowTable || {};
            jlab.editableRowTable.entity = 'Setting';
            jlab.editableRowTable.dialog.width = 400;
            jlab.editableRowTable.dialog.height = 300;
            jlab.editRow = function() {
                var key = $("#row-key").text(),
                    value = $("#row-value").val(),
                    reloading = false;

                $(".dialog-submit-button")
                    .height($(".dialog-submit-button").height())
                    .width($(".dialog-submit-button").width())
                    .empty().append('<div class="button-indicator"></div>');
                $(".dialog-close-button").attr("disabled", "disabled");
                $(".ui-dialog-titlebar button").attr("disabled", "disabled");

                var request = jQuery.ajax({
                    url: jlab.contextPath + "/setup/ajax/edit-setting",
                    type: "POST",
                    data: {
                        key: key,
                        value: value
                    },
                    dataType: "json"
                });

                request.done(function(json) {
                    if (json.stat === 'ok') {
                        reloading = true;
                        window.location.reload();
                    } else {
                        alert(json.error);
                    }
                });

                request.fail(function(xhr, textStatus) {
                    window.console && console.log('Unable to edit setting; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
                    alert('Unable to Save: Server unavailable or unresponsive');
                });

                request.always(function() {
                    if (!reloading) {
                        $(".dialog-submit-button").empty().text("Save");
                        $(".dialog-close-button").removeAttr("disabled");
                        $(".ui-dialog-titlebar button").removeAttr("disabled");
                    }
                });
            };
            $(document).on("click", ".default-clear-panel", function () {
                $("#key").val('');
                $("#tag-select").val('');
                return false;
            });
            $(document).on("click", "#open-edit-row-dialog-button", function() {
                var $selectedRow = $(".editable-row-table tr.selected-row");
                $("#row-key").text($selectedRow.find("td:first-child div:nth-child(2)").text());
                $("#row-value").val($selectedRow.find("td:nth-child(3)").text());
            });
            $(document).on("table-row-edit", function() {
                jlab.editRow();
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true">
                <form class="filter-form" action="settings" method="get">
                    <div id="filter-form-panel">
                        <fieldset>
                            <legend>Filter</legend>
                            <ul class="key-value-list">
                                <li>
                                    <div class="li-key">
                                        <label for="key">Key</label>
                                    </div>
                                    <div class="li-value">
                                        <input type="text" id="key" name="key" value="${fn:escapeXml(param.key)}"/>
                                    </div>
                                </li>
                                <li>
                                    <div class="li-key">
                                        <label for="tag-select">Tag</label>
                                    </div>
                                    <div class="li-value">
                                        <select id="tag-select" name="tag">
                                            <option value=""> </option>
                                            <c:forEach items="${tagList}" var="tag">
                                                <option value="${tag}"${(param.tag eq tag) ? ' selected="selected"' : ''}><c:out value="${tag}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </li>
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" class="offset-input" name="offset" value="0"/>
                    <input type="submit" class="filter-form-submit-button" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <form method="post" action="settings" style="float: right;">
                <button type="submit">Refresh Cache</button>
            </form>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div style="font-family: monospace; color: yellow; background-color: gray; padding: 1em; margin: 1em;">WARNING: Some setting changes may break app and Settings page itself such that you can only undo via external DB Console.  Changes made directly in the DB without using this interface won't take effect until the app is redeployed OR the Refresh Cache button in top right corner is pressed.  Proceed with caution.</div>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <s:editable-row-table-controls excludeAdd="true" excludeDelete="true"/>
            <table id="settings-table" class="data-table stripped-table uniselect-table editable-row-table">
                <thead>
                <tr>
                    <th>Key / Tag</th>
                    <th>Description / Type</th>
                    <th>Value</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="setting" items="${settingList}">
                    <tr>
                        <td>
                            <div class="container">
                                <div class="top"><c:out value="${setting.key}"/></div>
                                <div class="bottom"><c:out value="${setting.tag}"/></div>
                            </div>
                        </td>
                        <td>
                            <div class="container">
                                <div class="top"><c:out value="${setting.description}"/></div>
                                <div class="bottom"><c:out value="${setting.type}"/></div>
                            </div>
                        </td>
                        <td><c:out value="${setting.value}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <s:editable-row-table-dialog>
                <section>
                    <form id="row-form">
                        <ul class="key-value-list">
                            <li>
                                <div class="li-key">
                                    Key:
                                </div>
                                <div class="li-value">
                                    <span id="row-key"></span>
                                </div>
                            </li>
                            <li>
                                <div class="li-key">
                                    <label for="row-value">Value</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="row-value" autocomplete="off"/>
                                </div>
                            </li>
                        </ul>
                    </form>
                </section>
            </s:editable-row-table-dialog>
            <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
                    value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
            </button>
            <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
                    value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
            </button>
        </section>
    </jsp:body>
</s:setup-page>