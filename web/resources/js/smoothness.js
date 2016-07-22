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
jlab.editableRowTable.entity = jlab.editableRowTable.entity || 'Row';
jlab.editableRowTable.width = jlab.editableRowTable.width || 800;
jlab.editableRowTable.height = jlab.editableRowTable.height || 600;

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
    $("#open-add-row-dialog-button").prop("disabled", true);
    $("#open-edit-row-dialog-button").prop("disabled", false);
    $("#remove-row-button").prop("disabled", false);
    $("#unselect-all-button").prop("disabled", false);
});
$(document).on("click", "#open-add-row-dialog-button", function () {
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
        $("#open-add-row-dialog-button").prop("disabled", true);
        $("#open-edit-row-dialog-button").prop("disabled", false);
        $("#remove-row-button").prop("disabled", false);
        $("#unselect-all-button").prop("disabled", false);
    } else {
        $("#open-add-row-dialog-button").prop("disabled", false);
        $("#open-edit-row-dialog-button").prop("disabled", true);
        $("#remove-row-button").prop("disabled", true);
        $("#unselect-all-button").prop("disabled", true);
    }
});
$(document).on("click", "#unselect-all-button", function () {
    jlab.editableRowTable.lastSelectedRow = null; /*If we are unselecting then reset shift select*/

    $(".editable-row-table tbody tr").removeClass("selected-row");
    $("#open-add-row-dialog-button").prop("disabled", false);
    $("#open-edit-row-dialog-button").prop("disabled", true);
    $("#remove-row-button").prop("disabled", true);
    $("#unselect-all-button").prop("disabled", true);

    jlab.editableRowTable.updateSelectionCount();
});
jlab.editableRowTable.updateSelectionCount = function () {
    var numSelected = $(".multiselect-table").find(".selected-row").length;
    $("#selected-count").text(numSelected);
    return numSelected;
};
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
        width: jlab.editableRowTable.width,
        minWidth: jlab.editableRowTable.width,
        height: jlab.editableRowTable.height,
        minHeight: jlab.editableRowTable.height
    });
});
/*Autologin*/
jlab.tryAutoLogin = function () {

    var success = false;

    var request = jQuery.ajax({
        url: "/spnego/ace-auth",
        type: "GET",
        dataType: "json"
    });

    request.done(function (json) {
        if (json.username !== null && json.username !== '' && json.username !== 'null') {
            success = true;
            window.location.reload();
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to auto-login: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
    });

    request.always(function () {
        if (!success) {
            jlab.tryAutoLoginCue();
        }
    });
};
jlab.tryAutoLoginCue = function () {

    var success = false;

    var request = jQuery.ajax({
        url: "/spnego/cue-auth",
        type: "GET",
        dataType: "json"
    });

    request.done(function (json) {
        if (json.username !== null && json.username !== '' && json.username !== 'null') {
            success = true;
            window.location.reload();
        }
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to auto-login: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
    });

    request.always(function () {
        if (!success) {
            document.getElementById("login-link").click();
        }
    });
};
$(document).on("click", "#auto-login", function () {
    jlab.tryAutoLogin();

    return false;
});