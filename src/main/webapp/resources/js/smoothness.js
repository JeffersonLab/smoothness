/**
 * Global String Enhancements
 */
if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function (str) {
        return this.indexOf(str) === 0;
    };
}
if (!String.prototype.encodeXml) {
    String.prototype.encodeXml = function () {
        return this.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/'/g, '&apos;')
                .replace(/"/g, '&quot;');
    };
}
if (!String.prototype.decodeXml) {
    String.prototype.decodeXml = function () {
        return this.replace(/&quot;/g, '"')
                .replace(/&apos;/g, '\'')
                .replace(/&gt;/g, '>')
                .replace(/&lt;/g, '<')
                .replace(/&amp;/g, '&');
    };
}
/**
 * Common Namespace Declaration
 */
var jlab = jlab || {};
/**
 * Editable Row Table Configuration
 */
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.dialog = jlab.editableRowTable.dialog || {};
jlab.editableRowTable.entity = jlab.editableRowTable.entity || 'Row';
jlab.editableRowTable.dialog.width = jlab.editableRowTable.dialog.width || 800;
jlab.editableRowTable.dialog.height = jlab.editableRowTable.dialog.height || 600;
jlab.editableRowTable.dialog.minWidth = jlab.editableRowTable.dialog.minWidth || 0;
jlab.editableRowTable.dialog.minHeight = jlab.editableRowTable.dialog.minHeight || 0;
jlab.editableRowTable.dialog.resizable = jlab.editableRowTable.dialog.resizable || false;

/**
 * Private row selection state
 */
jlab.editableRowTable.lastSelectedRow = null; /*Initially no rows are selected*/

/**
 * Page Dialog Configuration
 */
jlab.pageDialog = jlab.pageDialog || {};
jlab.pageDialog.width = jlab.pageDialog.width || 840;
jlab.pageDialog.height = jlab.pageDialog.height || 590;
jlab.pageDialog.minWidth = jlab.pageDialog.minWidth || 0;
jlab.pageDialog.minHeight = jlab.pageDialog.minHeight || 0;
jlab.pageDialog.resizable = jlab.pageDialog.resizable || true;
/**
 * Common Namespaced Functions
 */
//Function to ensure a AJAX request is only issued serially
jlab.isRequest = function () {
    return jlab.ajaxInProgress;
};
jlab.requestStart = function () {
    jlab.ajaxInProgress = true;
};
jlab.requestEnd = function () {
    jlab.ajaxInProgress = false;
};
jlab.doAjaxJsonGetRequest = function (url, data) {

    var promise = jQuery.ajax({
        url: url,
        type: "GET",
        data: data,
        dataType: "json"
    });

    promise.error(function (xhr, textStatus) {
        var json;

        try {
            json = $.parseJSON(xhr.responseText);
        } catch (err) {
            window.console && console.log('Response is not JSON: ' + xhr.responseText);
            json = {};
        }

        var message = json.error || 'Server did not handle request';
        alert('Unable to perform request: ' + message);
    });

    return promise;
};
jlab.doAjaxJsonPostRequest = function (url, data, $dialog, reload) {

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    if ($dialog === null) {
        $dialog = $();
    }

    var $submitButton = $dialog.find(".dialog-submit-button"),
            $cancelButton = $dialog.find(".dialog-close-button"),
            $titleBarButton = $dialog.find(".ui-dialog-titlebar button");

    $submitButton
            .height($submitButton.height())
            .width($submitButton.width())
            .empty().append('<div class="button-indicator"></div>');
    $cancelButton.prop("disabled", true);
    $titleBarButton.prop("disabled", true);

    var promise = jQuery.ajax({
        url: url,
        type: "POST",
        data: data,
        dataType: "json"
    });

    promise.done(function () {
        if (reload) {
            window.location.reload(true);
        } else {
            $submitButton.empty().text("Save");
            $cancelButton.prop("disabled", false);
            $titleBarButton.prop("disabled", false);

            $dialog.dialog("close");
        }
    });

    promise.error(function (xhr, textStatus) {
        var json, html;

        try {
            json = $.parseJSON(xhr.responseText);
        } catch (err) {
            window.console && console.log('Response is not JSON: ' + xhr.responseText);
            json = {};

            try {
                html = $.parseHTML(xhr.responseText);

                /*Let's see if we just got back Java Servlet Login Page*/
                var $requesterInput = $(html).find("input[name=requester]");

                if ($requesterInput.length > 0 && $requesterInput.val() === 'login') {
                    json.error = 'Your session has expired: You can try to re-login from a separate tab and then return here to resubmit';
                }
            } catch (err2) {
                window.console && console.log('Response is not HTML either: ' + err2);
            }
        }

        var message = json.error || 'Server did not handle request';
        alert('Unable to perform request: ' + message);

        $submitButton.empty().text("Save");
        $cancelButton.prop("disabled", false);
        $titleBarButton.prop("disabled", false);
    });

    promise.always(function () {
        jlab.requestEnd();
    });

    return promise;
};
//Display a piece of another page in a dialog
jlab.openPageInDialog = function (href, title) {
    $("<div class=\"page-dialog\"></div>")
            .load(href + ' .dialog-content')
            .dialog({
                autoOpen: true,
                title: title,
                width: jlab.pageDialog.width,
                height: jlab.pageDialog.height,
                minWidth: jlab.pageDialog.minWidth,
                minHeight: jlab.pageDialog.minHeight,
                resizable: jlab.pageDialog.resizable,
                close: function () {
                    $(this).dialog('destroy').remove();
                }
            });
};
jlab.closePageDialogs = function () {
    $(".page-dialog").dialog('destroy').remove();
};
// Format a string representation of an integer with commas
jlab.integerWithCommas = function (x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};
// Format a number by padding with zeros
jlab.pad = function (n, width, z) {
    z = z || '0';
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
};
// Reports/Export functions
jlab.getPrintUrl = function () {
    var uri = URI();
    uri.removeSearch("print");
    uri.addSearch("print", "Y");
    return uri.toString();
};
jlab.getFullscreenUrl = function () {
    var uri = URI();
    uri.removeSearch("print");
    uri.removeSearch("fullscreen");
    uri.addSearch("print", "Y");
    uri.addSearch("fullscreen", "Y");
    return uri.toString();
};
jlab.getExitFullscreenUrl = function () {
    var uri = URI();
    uri.removeSearch("print");
    uri.removeSearch("fullscreen");
    return uri.toString();
};
/*Chart Axis Labels*/
jlab.addYAxisLabel = function (label) {
    var yaxisLabel = $("<div class='axis-label y-axis-label'></div>")
            .text(label)
            .appendTo($("#chart-placeholder"));
    yaxisLabel.css("margin-top", yaxisLabel.width() / 2);
};
jlab.addXAxisLabel = function (label) {
    $("<div class='axis-label x-axis-label'></div>")
            .text(label)
            .appendTo($("#chart-placeholder"));
};
/**
 * Common Event Handlers
 */
// Filter flyout events
$(document).on("click", "#filter-flyout-link", function () {
    $("#filter-flyout-panel").slideToggle();
    return false;
});
$(document).on("click", "#filter-flyout-close-button", function () {
    $("#filter-flyout-panel").slideUp();
    return false;
});
$(document).keyup(function (e) {
    if (e.keyCode === 27) {
        $('#filter-flyout-panel').slideUp();
    }
});
// Dialog events
$(document).on("click", ".dialog-ready", function () {
    var title = $(this).attr("data-dialog-title");

    jlab.closePageDialogs();
    jlab.openPageInDialog($(this).attr("href"), title);
    return false;
});
$(document).on("click", ".dialog-close-button", function () {
    $(this).closest(".dialog").dialog("close");
});
// Prevent keypress from doing Full page HTTP POST in a dialog; instead programatically click dialog submit button
$(document).on("keypress", ".dialog input", function (e) {
    if (e.which === 13) {
        $(this).closest("form").find(".dialog-submit-button").click();

        return false;
    }
});
// Some form inputs can trigger submit on change
$(document).on("change", ".change-submit", function () {
    $(this).closest("form").submit();
});
// Pagination controls
$(document).on("click", "#next-button, #previous-button", function () {
    $("#offset-input").val($(this).attr("data-offset"));
    $("#filter-form").submit();
});
// Report FullScreen/Export events
$(document).on("mouseover", "#export-menu li", function () {
    $(this).addClass("ui-state-focus");
});
$(document).on("mouseleave", "#export-menu li", function () {
    $(this).removeClass("ui-state-focus");
});
$(document).on("click", "#fullscreen-button", function () {
    window.location = jlab.getFullscreenUrl();
});
$(document).on("click", "#exit-fullscreen-button", function () {
    window.location = jlab.getExitFullscreenUrl();
});
$(document).on("click", "#print-menu-item", function () {
    window.location = jlab.getPrintUrl();
});
$(document).on("click", "#image-menu-item", function () {
    var printUrl = jlab.getPrintUrl();
    window.location = '/html-to-image/convert?filename=chart.png&width=1024&url=' + encodeURIComponent(printUrl);
});
$(document).on("click", "#excel-menu-item", function () {
    $("#excel").click();
});
$(document).on("click", "#shiftlog-menu-item", function () {
    $("#shiftlog").click();
});
// Editable Row Table
$(document).on("click", ".uniselect-table tbody tr", function () {
    $(".editable-row-table .selected-row").removeClass("selected-row");
    $(this).addClass("selected-row");
    $(".no-selection-row-action").prop("disabled", true);
    $(".selected-row-action").prop("disabled", false);
});
$(document).on("click", "#open-add-row-dialog-button", function () {
    $("#row-form")[0].reset();

    $("#table-row-dialog").dialog("option", "title", "Add " + jlab.editableRowTable.entity).dialog("open");
});
$(document).on("click", "#open-edit-row-dialog-button", function () {
    $("#table-row-dialog").dialog("option", "title", "Edit " + jlab.editableRowTable.entity).dialog("open");
});
$(document).on("click", "#table-row-save-button", function () {
    var eventType = 'table-row-edit';
    if ($("#table-row-dialog").dialog("option", "title").startsWith("Add")) {
        eventType = 'table-row-add';
    }
    $.event.trigger({
        type: eventType
    });
});
/**
 * 
 * Multiselect table
 */
$(document).on("click", ".multiselect-table tbody tr", function (e) {
    if (e.ctrlKey) {
        $(this).toggleClass("selected-row");
    } else if (e.shiftKey) {
        /*console.log('shift held');*/
        if (jlab.editableRowTable.lastSelectedRow === null) { // Regular click if no previous click
            /*console.log('no last selected');*/
            $(this).addClass("selected-row").siblings().removeClass("selected-row");
        } else { // Select a range
            var first = jlab.editableRowTable.lastSelectedRow,
                    second = $(this).index(),
                    start = Math.min(first, second),
                    end = Math.max(first, second);

            /*console.log('start: ' + start);
             console.log('end: ' + end);*/

            $(".multiselect-table tbody tr").slice(start, end + 1).addClass("selected-row");
        }
    } else {
        $(this).addClass("selected-row").siblings().removeClass("selected-row");
    }

    if ($(this).hasClass("selected-row")) {
        jlab.editableRowTable.lastSelectedRow = $(this).index();
    } else {
        jlab.editableRowTable.lastSelectedRow = null; /*If we are unselecting then reset shift select*/
    }

    var numSelected = jlab.editableRowTable.updateSelectionCount();

    if (numSelected > 0) {
        $(".no-selection-row-action").prop("disabled", true);
        $(".selected-row-action").prop("disabled", false);
    } else {
        $(".no-selection-row-action").prop("disabled", false);
        $(".selected-row-action").prop("disabled", true);
    }
});
$(document).on("click", "#unselect-all-button", function () {
    jlab.editableRowTable.lastSelectedRow = null; /*If we are unselecting then reset shift select*/

    $(".editable-row-table tbody tr").removeClass("selected-row");
    $(".no-selection-row-action").prop("disabled", false);
    $(".selected-row-action").prop("disabled", true);

    jlab.editableRowTable.updateSelectionCount();
});
jlab.editableRowTable.updateSelectionCount = function () {
    var numSelected = $(".multiselect-table").find(".selected-row").length;
    $("#selected-count").text(numSelected);
    return numSelected;
};
$(document).on("click", ".expand-icon", function () {
    $(this).closest("table").toggleClass("expanded-table");
});
/**
 * DOM Ready actions 
 */
$(function () {
    // Create report buttons
    $("#fullscreen-button, #exit-fullscreen-button").button().show();
    $("#export-menu-button").button({
        icons: {
            secondary: "ui-icon-triangle-1-s"}
    }).click(function () {
        var menu = $("#export-menu").slideDown();

        $(document).one("click", function () {
            menu.hide();
        });
        return false;
    }).show();
    $("#export-menu").menu().hide();

    // Editable Row Table Dialog
    $("#table-row-dialog").dialog({
        autoOpen: false,
        modal: true,
        width: jlab.editableRowTable.dialog.width,
        minWidth: jlab.editableRowTable.dialog.minWidth,
        height: jlab.editableRowTable.dialog.height,
        minHeight: jlab.editableRowTable.dialog.minHeight,
        resizable: jlab.editableRowTable.dialog.resizable
    });
});
/*Autologin*/
jlab.su = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.logoutUrl;
    document.body.appendChild(i);
};
jlab.login = function(url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function() { i.parentNode.removeChild(i); window.location.href = url; };
    i.src = jlab.loginUrl;
    document.body.appendChild(i);
};
$(document).on("click", "#login-link", function () {
    var url = $(this).attr("href");

    jlab.login(url);

    return false;
});
$(document).on("click", "#su-link", function() {
    var url = $(this).attr("href");

    jlab.su(url);

    return false;
});