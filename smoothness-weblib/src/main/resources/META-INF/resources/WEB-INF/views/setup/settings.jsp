<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@taglib prefix="s" uri="jlab.tags.smoothness" %>
<c:set var="title" value="Settings"/>
<s:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
        <style>
            td:nth-child(1),
            td:nth-child(2) {
                width: 265px;
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
            ul.csv-items {
                margin: 0;
                padding-left: 1.5em;
            }
            #row-form .csv-field,
            #row-form.csv-form .text-field {
                display: none;
            }
            #row-form.csv-form .csv-field {
                display: table-row;
            }
            #row-csv-value {
                width: 600px;
                max-width: 100%;
                box-sizing: border-box;
                white-space: pre;
                overflow-wrap: normal;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script>
            jlab.editableRowTable = jlab.editableRowTable || {};
            jlab.editableRowTable.entity = 'Setting';
            jlab.editableRowTable.dialog.width = 400;
            jlab.editableRowTable.dialog.height = 300;
            /* A CSV setting is edited one item per line: trim each line, drop blank ones, and
               refuse a line with a comma, which csv() would split into two items. */
            jlab.csvLinesToValue = function(text) {
                var lines = text.split(/\r?\n/),
                    items = [];

                for (var i = 0; i < lines.length; i++) {
                    var item = lines[i].trim();

                    if (item === '') {
                        continue;
                    }

                    if (item.indexOf(',') !== -1) {
                        return {error: 'Line ' + (i + 1) + ' contains a comma, which would split it into two items: ' + item};
                    }

                    items.push(item);
                }

                if (items.length === 0) {
                    return {error: 'Enter at least one item'};
                }

                return {value: items.join(',')};
            };
            jlab.editRow = function() {
                var key = $("#row-key").text(),
                    value = $("#row-value").val(),
                    reloading = false;

                if ($("#row-form").hasClass("csv-form")) {
                    var csv = jlab.csvLinesToValue($("#row-csv-value").val());

                    if (csv.error) {
                        jlab.showErrorDialog('Unable to Save', csv.error);
                        return;
                    }

                    value = csv.value;
                }

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
                        jlab.showErrorDialog('Unable to Save', json.error);
                    }
                });

                request.fail(function(xhr, textStatus) {
                    window.console && console.log('Unable to edit setting; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
                    jlab.showRequestFailure(xhr, 'Unable to Save');
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
                var $selectedRow = $(".editable-row-table tr.selected-row"),
                    value = $selectedRow.attr("data-value"),
                    csv = $selectedRow.attr("data-type") === 'CSV';
                $("#row-key").text($selectedRow.find("td:first-child div.top").text());
                $("#row-form").toggleClass("csv-form", csv);
                $("#row-value").val(csv ? '' : value);
                $("#row-csv-value").val(csv ? value.split(",").join("\n") : '');
                $("#table-row-dialog").dialog("option", csv ? {
                    width: 800,
                    height: Math.min(720, $(window).height() - 40)
                } : {
                    width: jlab.editableRowTable.dialog.width,
                    height: jlab.editableRowTable.dialog.height
                });
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
                    <tr data-type="${setting.type}" data-value="${fn:escapeXml(setting.value)}">
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
                        <td>
                            <c:choose>
                                <c:when test="${setting.type eq 'CSV'}">
                                    <ul class="csv-items">
                                        <c:forTokens items="${setting.value}" delims="," var="item">
                                            <li><c:out value="${item}"/></li>
                                        </c:forTokens>
                                    </ul>
                                </c:when>
                                <c:otherwise><c:out value="${setting.value}"/></c:otherwise>
                            </c:choose>
                        </td>
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
                            <li class="text-field">
                                <div class="li-key">
                                    <label for="row-value">Value</label>
                                </div>
                                <div class="li-value">
                                    <input type="text" id="row-value" autocomplete="off"/>
                                </div>
                            </li>
                            <li class="csv-field">
                                <div class="li-key">
                                    <label for="row-csv-value">Items</label>
                                </div>
                                <div class="li-value">
                                    <textarea id="row-csv-value" rows="30" autocomplete="off" spellcheck="false"></textarea>
                                    <div>One item per line; an item can't contain a comma.</div>
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