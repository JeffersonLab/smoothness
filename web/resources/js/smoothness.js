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
 * Configuration
 */
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = jlab.editableRowTable.entity || 'Row';
jlab.editableRowTable.width = jlab.editableRowTable.width || 800;
jlab.editableRowTable.height = jlab.editableRowTable.height || 600;
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
                width: 840,
                height: 590,
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
$(document).on("click", function (event) {
    if (!$(event.target).closest('.editable-row-table tbody').length && !$(event.target).closest('#editable-row-table-control-panel').length && !$(event.target).closest('.ui-dialog').length && !$(event.target).closest('.ui-widget-overlay').length) {
        $(".editable-row-table .selected-row").removeClass("selected-row");
        $("#open-add-row-dialog-button").removeAttr("disabled");
        $("#open-edit-row-dialog-button").attr("disabled", "disabled");
        $("#remove-row-button").attr("disabled", "disabled");
    }
});
$(document).on("click", ".editable-row-table tbody tr", function () {
    $(".editable-row-table .selected-row").removeClass("selected-row");
    $(this).addClass("selected-row");
    $("#open-add-row-dialog-button").attr("disabled", "disabled");
    $("#open-edit-row-dialog-button").removeAttr("disabled");
    $("#remove-row-button").removeAttr("disabled");
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