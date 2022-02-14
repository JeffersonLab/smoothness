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
 * Date constants
 */
jlab.triCharMonthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
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
jlab.doAjaxJsonGetRequest = function (url, data, quiet) {

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
        if(quiet) {
            window.console && console.log('Unable to perform request: ' + message);
        } else {
            alert('Unable to perform request: ' + message);
        }
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
    return uri.path() + uri.search() + uri.hash();
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
    uri.addSearch("print", "N");
    uri.addSearch("fullscreen", "N");
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
// Date/Time utilities
jlab.toFriendlyDateTimeString = function (x) {
    var year = x.getFullYear(),
        month = x.getMonth(),
        day = x.getDate(),
        hour = x.getHours(),
        minute = x.getMinutes();

    return jlab.pad(day, 2) + '-' + jlab.triCharMonthNames[month] + '-' + year + ' ' + jlab.pad(hour, 2) + ':' + jlab.pad(minute, 2);
};
jlab.toFriendlyDateString = function (x) {
    var year = x.getFullYear(),
        month = x.getMonth(),
        day = x.getDate();

    return jlab.pad(day, 2) + '-' + jlab.triCharMonthNames[month] + '-' + year;
};
jlab.fromFriendlyDateTimeString = function(x) {
    var day = parseInt(x.substring(0, 2)),
        month = jlab.triCharMonthNames.indexOf(x.substring(3, 6)),
        year = parseInt(x.substring(7, 11)),
        hour = parseInt(x.substring(12, 14)),
        minute = parseInt(x.substring(15, 17));
    return new Date(year, month, day, hour, minute);
};

jlab.fromFriendlyDateString = function (x) {
    var day = parseInt(x.substring(0, 2)),
        month = jlab.triCharMonthNames.indexOf(x.substring(3, 6)),
        year = parseInt(x.substring(7, 11));
    return new Date(year, month, day);
};
jlab.toIsoDateTimeString = function (x) {
    var year = x.getFullYear(),
        month = x.getMonth() + 1,
        day = x.getDate(),
        hour = x.getHours(),
        minute = x.getMinutes();

    return year + '-' + jlab.pad(month, 2) + '-' + jlab.pad(day, 2) + 'T' + jlab.pad(hour, 2) + ':' + jlab.pad(minute, 2);
};
jlab.toIsoDateString = function (x) {
    var year = x.getFullYear(),
        month = x.getMonth() + 1,
        day = x.getDate();
    return year + '-' + jlab.pad(month, 2) + '-' + jlab.pad(day, 2);
};
jlab.fromIsoDateTimeString = function (x) {
    var year = parseInt(x.substring(0, 4)),
        month = parseInt(x.substring(5, 7)) - 1,
        day = parseInt(x.substring(8, 10)),
        hour = parseInt(x.substring(11, 13)),
        minute = parseInt(x.substring(14, 16)),
        second = parseInt(x.substring(17, 19));
    return new Date(year, month, day, hour, minute, second);
};
jlab.fromIsoDateString = function (x) {
    var year = parseInt(x.substring(0, 4)),
        month = parseInt(x.substring(5, 7)) - 1,
        day = parseInt(x.substring(8, 10));
    return new Date(year, month, day);
};
jlab.updateDateRange = function (start, end, includeTime) {
    $("#custom-date-range-list").hide();

    if (includeTime) {
        $("#start").val(jlab.toFriendlyDateTimeString(start));
        $("#end").val(jlab.toFriendlyDateTimeString(end));
    } else {
        $("#start").val(jlab.toFriendlyDateString(start));
        $("#end").val(jlab.toFriendlyDateString(end));
    }
};
jlab.getCcShiftStart = function (dateInShift) {
    var start = new Date(dateInShift.getTime());

    start.setMinutes(0);
    start.setSeconds(0);
    start.setMilliseconds(0);

    var hour = start.getHours();

    if (hour === 23) {
        // Already start!
    } else if (hour <= 6) {
        start.setDate(start.getDate() - 1);
        start.setHours(23);
    } else if (hour <= 14) {
        start.setHours(7);
    } else {
        start.setHours(15);
    }

    return start;
};
jlab.getCcShiftEnd = function (dateInShift) {
    var end = new Date(dateInShift.getTime());

    end.setMinutes(0);
    end.setSeconds(0);
    end.setMilliseconds(0);

    var hour = end.getHours();

    if (hour === 23) {
        end.setDate(end.getDate() + 1);
        end.setHours(7);
    } else if (hour <= 6) {
        end.setHours(7);
    } else if (hour <= 14) {
        end.setHours(15);
    } else {
        end.setHours(23);
    }

    return end;
};
jlab.getStartOfWeek = function(dateInWeek, startDayOfWeekIndex) {
    var startOfWeek = new Date(dateInWeek),
        dayOfWeekIndex = dateInWeek.getDay(),
        distance = startDayOfWeekIndex - dayOfWeekIndex;

    if (distance < 0) {
        distance = 7 + distance;
    }

    startOfWeek.setDate(startOfWeek.getDate() + distance - 7);

    return startOfWeek;
};
jlab.getStartOfFiscalYear = function(dateInYear) {

    const octIndex = 9; /* October */

    var start = new Date(dateInYear);

    if(start.getMonth() < octIndex) {
        start.setFullYear(start.getFullYear() - 1);
    }

    start.setMonth(octIndex);
    start.setDate(1);
    start.setHours(0);
    start.setMinutes(0);
    start.setSeconds(0);
    start.setMilliseconds(0);

    return start;
};
jlab.encodeRange = function (start, end, sevenAmOffset) {
    const wedIndex = 3; /* Wednesday */

    var now = new Date();

    now.setMilliseconds(0);
    now.setSeconds(0);
    now.setMinutes(0);

    var today = new Date(now);

    if (sevenAmOffset) {
        today.setHours(7);
    } else {
        today.setHours(0);
    }

    var tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);

    var oneDayAgo = new Date(today);
    oneDayAgo.setDate(today.getDate() - 1);

    var threeDaysAgo = new Date(today);
    threeDaysAgo.setDate(today.getDate() - 3);

    var sevenDaysAgo = new Date(today);
    sevenDaysAgo.setDate(today.getDate() - 7);

    var tenDaysAgo = new Date(today);
    tenDaysAgo.setDate(today.getDate() - 10);

    var currentShiftStart = jlab.getCcShiftStart(now),
        currentShiftEnd = jlab.getCcShiftEnd(now);

    var dateInPreviousShift = new Date(currentShiftStart);
    dateInPreviousShift.setHours(currentShiftStart.getHours() - 1);

    var previousShiftStart = jlab.getCcShiftStart(dateInPreviousShift),
        previousShiftEnd = jlab.getCcShiftEnd(dateInPreviousShift);

    var currentWeekStart = jlab.getStartOfWeek(today, wedIndex);

    var currentWeekEnd = new Date(currentWeekStart);
    currentWeekEnd.setDate(currentWeekStart.getDate() + 7);

    var previousWeekStart = new Date(currentWeekStart);
    previousWeekStart.setDate(currentWeekStart.getDate() - 7);

    var previousWeekEnd = new Date(currentWeekEnd);
    previousWeekEnd.setDate(currentWeekEnd.getDate() - 7);

    var currentMonthStart = new Date(now);
    currentMonthStart.setDate(1);
    currentMonthStart.setHours(0); /* now has cleared minutes, seconds, milliseconds, but not hours because shift logic uses it */

    var currentMonthEnd = new Date(currentMonthStart);
    currentMonthEnd.setMonth(currentMonthStart.getMonth() + 1);

    var previousMonthStart = new Date(currentMonthStart);
    previousMonthStart.setMonth(currentMonthStart.getMonth() - 1);

    var previousMonthEnd = currentMonthStart;

    var currentYearStart = new Date(currentMonthStart);
    currentYearStart.setMonth(0); /*Zero indexed month = January*/

    var currentYearEnd = new Date(currentYearStart);
    currentYearEnd.setFullYear(currentYearStart.getFullYear() + 1);

    var previousYearStart = new Date(currentYearStart);
    previousYearStart.setFullYear(currentYearStart.getFullYear() - 1);

    var previousYearEnd = currentYearStart;

    var currentFiscalYearStart = jlab.getStartOfFiscalYear(now);

    var currentFiscalYearEnd = new Date(currentFiscalYearStart);
    currentFiscalYearEnd.setFullYear(currentFiscalYearStart.getFullYear() + 1);

    var previousFiscalYearStart = new Date(currentFiscalYearStart);
    previousFiscalYearStart.setFullYear(currentFiscalYearStart.getFullYear() - 1);

    var previousFiscalYearEnd = currentFiscalYearStart;

    var currentFiscalYearQ1Start = currentFiscalYearStart;
    var currentFiscalYearQ1End = new Date(currentFiscalYearStart);
    currentFiscalYearQ1End.setMonth(currentFiscalYearQ1End.getMonth() + 3);

    var currentFiscalYearQ2Start = currentFiscalYearQ1End;
    var currentFiscalYearQ2End = new Date(currentFiscalYearQ1End);
    currentFiscalYearQ2End.setMonth(currentFiscalYearQ2End.getMonth() + 3);

    var currentFiscalYearQ3Start = currentFiscalYearQ2End;
    var currentFiscalYearQ3End = new Date(currentFiscalYearQ2End);
    currentFiscalYearQ3End.setMonth(currentFiscalYearQ3End.getMonth() + 3);

    var currentFiscalYearQ4Start = currentFiscalYearQ3End;
    var currentFiscalYearQ4End = new Date(currentFiscalYearQ3End);
    currentFiscalYearQ4End.setMonth(currentFiscalYearQ4End.getMonth() + 3);

    var previousFiscalYearQ1Start = previousFiscalYearStart;
    var previousFiscalYearQ1End = new Date(previousFiscalYearStart);
    previousFiscalYearQ1End.setMonth(previousFiscalYearQ1End.getMonth() + 3);

    var previousFiscalYearQ2Start = previousFiscalYearQ1End;
    var previousFiscalYearQ2End = new Date(previousFiscalYearQ1End);
    previousFiscalYearQ2End.setMonth(previousFiscalYearQ2End.getMonth() + 3);

    var previousFiscalYearQ3Start = previousFiscalYearQ2End;
    var previousFiscalYearQ3End = new Date(previousFiscalYearQ2End);
    previousFiscalYearQ3End.setMonth(previousFiscalYearQ3End.getMonth() + 3);

    var previousFiscalYearQ4Start = previousFiscalYearQ3End;
    var previousFiscalYearQ4End = new Date(previousFiscalYearQ3End);
    previousFiscalYearQ4End.setMonth(previousFiscalYearQ4End.getMonth() + 3);

    var range = "custom";

    if (end.getTime() == currentShiftEnd.getTime() && start.getTime() == currentShiftStart.getTime()) {
        range = "0ccshift";
    } else if (end.getTime() == previousShiftEnd.getTime() && start.getTime() == previousShiftStart.getTime()) {
        range = "1ccshift";
    } else if (end.getTime() == tomorrow.getTime() && start.getTime() == today.getTime()) {
        range = "0day";
    } else if (end.getTime() == today.getTime() && start.getTime() == oneDayAgo.getTime()) {
        range = "1day";
    } else if (end.getTime() == currentWeekEnd.getTime() && start.getTime() == currentWeekStart.getTime()) {
        range = "0week";
    } else if (end.getTime() == previousWeekEnd.getTime() && start.getTime() == previousWeekStart.getTime()) {
        range = "1week";
    } else if (end.getTime() == currentMonthEnd.getTime() && start.getTime() == currentMonthStart.getTime()) {
        range = "0month";
    } else if (end.getTime() == previousMonthEnd.getTime() && start.getTime() == previousMonthStart.getTime()) {
        range = "1month";
    } else if (end.getTime() == currentYearEnd.getTime() && start.getTime() == currentYearStart.getTime()) {
        range = "0year";
    } else if (end.getTime() == previousYearEnd.getTime() && start.getTime() == previousYearStart.getTime()) {
        range = "1year";
    } else if (end.getTime() == currentFiscalYearEnd.getTime() && start.getTime() == currentFiscalYearStart.getTime()) {
        range = "0fiscalyear";
    } else if (end.getTime() == previousFiscalYearEnd.getTime() && start.getTime() == previousFiscalYearStart.getTime()) {
        range = "1fiscalyear";
    } else if (end.getTime() == previousFiscalYearQ1End.getTime() && start.getTime() == previousFiscalYearQ1Start.getTime()) {
        range = "1fiscalyearq1";
    } else if (end.getTime() == previousFiscalYearQ2End.getTime() && start.getTime() == previousFiscalYearQ2Start.getTime()) {
        range = "1fiscalyearq2";
    } else if (end.getTime() == previousFiscalYearQ3End.getTime() && start.getTime() == previousFiscalYearQ3Start.getTime()) {
        range = "1fiscalyearq3";
    } else if (end.getTime() == previousFiscalYearQ4End.getTime() && start.getTime() == previousFiscalYearQ4Start.getTime()) {
        range = "1fiscalyearq4";
    } else if (end.getTime() == currentFiscalYearQ1End.getTime() && start.getTime() == currentFiscalYearQ1Start.getTime()) {
        range = "0fiscalyearq1";
    } else if (end.getTime() == currentFiscalYearQ2End.getTime() && start.getTime() == currentFiscalYearQ2Start.getTime()) {
        range = "0fiscalyearq2";
    } else if (end.getTime() == currentFiscalYearQ3End.getTime() && start.getTime() == currentFiscalYearQ3Start.getTime()) {
        range = "0fiscalyearq3";
    } else if (end.getTime() == currentFiscalYearQ4End.getTime() && start.getTime() == currentFiscalYearQ4Start.getTime()) {
        range = "0fiscalyearq4";
    } else if (jlab.currentRun != null && jlab.currentRun.end.getTime() == end.getTime() && jlab.currentRun.start.getTime() == start.getTime()) {
        range = "0run";
    } else if (jlab.previousRun != null && jlab.previousRun.end.getTime() == end.getTime() && jlab.previousRun.start.getTime() == start.getTime()) {
        range = "1run";
    } else if (end.getTime() == today.getTime()) {
        if (start.getTime() == tenDaysAgo.getTime()) {
            range = "past10days";
        } else if (start.getTime() == sevenDaysAgo.getTime()) {
            range = "past7days";
        } else if (start.getTime() == threeDaysAgo.getTime()) {
            range = "past3days";
        }
    }

    return range;
}
jlab.decodeRange = function(range, sevenAmOffset) {
    const wedIndex = 3; /* Wednesday */
    const octIndex = 9; /* October */

    var start = new Date(),
        end = new Date();

    switch (range) {
        case '1fiscalyear':
            end.setFullYear(end.getFullYear() - 1);

            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setFullYear(end.getFullYear() + 1);
            break;
        case '1fiscalyearq1':
            end.setFullYear(end.getFullYear() - 1);

            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '1fiscalyearq2':
            end.setFullYear(end.getFullYear() - 1);

            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 3);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '1fiscalyearq3':
            end.setFullYear(end.getFullYear() - 1);

            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 6);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '1fiscalyearq4':
            end.setFullYear(end.getFullYear() - 1);

            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 9);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '0fiscalyear':
            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setFullYear(end.getFullYear() + 1);
            break;
        case '0fiscalyearq1':
            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '0fiscalyearq2':
            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 3);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '0fiscalyearq3':
            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 6);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '0fiscalyearq4':
            if (end.getMonth() < octIndex) {
                end.setFullYear(end.getFullYear() - 1);
            }

            end.setMonth(octIndex + 9);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 3);
            break;
        case '1year':
            end.setMonth(0);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            start.setFullYear(end.getFullYear() - 1);
            break;
        case '0year':
            end.setMonth(0);
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setFullYear(end.getFullYear() + 1);
            break;
        case '1run':
            start = jlab.previousRun.start;
            end = jlab.previousRun.end;
            break;
        case '0run':
            start = jlab.currentRun.start;
            end = jlab.currentRun.end;
            break;
        case '1month':
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            start.setMonth(end.getMonth() - 1);
            break;
        case '0month':
            end.setDate(1);
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);
            end.setHours(0);

            start.setTime(end.getTime());
            end.setMonth(end.getMonth() + 1);
            break;
        case '1week':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            var dayOfWeekIndex = end.getDay(),
                distance = wedIndex - dayOfWeekIndex;
            if (distance < 0) {
                distance = 7 + distance;
            }
            end.setDate(end.getDate() + distance - 7);

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 7);
            break
        case '0week':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            var dayOfWeekIndex = end.getDay(),
                distance = wedIndex - dayOfWeekIndex;
            if (distance < 0) {
                distance = 7 + distance;
            }
            end.setDate(end.getDate() + distance);

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 7);
            break;
        case 'past10days':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 10);
            break;
        case 'past7days':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 7);
            break;
        case 'past3days':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 3);
            break;
        case '0day':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            start.setTime(end.getTime());
            end.setDate(end.getDate() + 1);
            break;
        case '1day':
            end.setMilliseconds(0);
            end.setSeconds(0);
            end.setMinutes(0);

            if (sevenAmOffset) {
                end.setHours(7);
            } else {
                end.setHours(0);
            }

            start.setTime(end.getTime());
            start.setDate(start.getDate() - 1);
            break;
        case '1ccshift':
            var now = new Date(),
                dateInPreviousShift = jlab.getCcShiftStart(now);
            dateInPreviousShift.setHours(dateInPreviousShift.getHours() - 1);

            start = jlab.getCcShiftStart(dateInPreviousShift);
            end = jlab.getCcShiftEnd(dateInPreviousShift);
            break;
        case '0ccshift':
            var now = new Date();

            start = jlab.getCcShiftStart(now),
                end = jlab.getCcShiftEnd(now);
            break;
        default:
            start = null;
            end = null;
            break;
    }

    return {start: start, end: end};
};
jlab.initDateRange = function() {
    var promise = jlab.doAjaxJsonGetRequest('/btm/rest/runs', {}, true);

    promise.done(function(json) {
        jlab.currentRun = null;
        jlab.previousRun = null;

        if(json.current) {
            jlab.currentRun = {};
            jlab.currentRun.start = jlab.fromIsoDateString(json.current.start);
            jlab.currentRun.end = jlab.fromIsoDateString(json.current.end);

            $('#date-range option[value="0year"]').after('<option value="0run">Current Run</option>');
        }

        if(json.previous) {
            jlab.previousRun = {};
            jlab.previousRun.start = jlab.fromIsoDateString(json.previous.start);
            jlab.previousRun.end = jlab.fromIsoDateString(json.previous.end);

            $('#date-range option[value="1year"]').after('<option value="1run">Previous Run</option>');
        }

        jlab.setupDateRange();
    });

    promise.error(function(json) {
        jlab.setupDateRange();
    });

    return promise;
};

jlab.setupDateRange = function() {
    var startInput = $("input#start"),
        endInput = $("input#end"),
        sevenAmOffset = $("#date-range").hasClass("seven-am-offset"),
        includeTime = $("#date-range").hasClass("datetime-range");

    if(startInput.length > 0 && endInput.length > 0) {
        var start = startInput.val();
        var end = endInput.val();

        if(includeTime) {
            start = jlab.fromFriendlyDateTimeString(start);
            end = jlab.fromFriendlyDateTimeString(end);
        } else {
            start = jlab.fromFriendlyDateString(start);
            end = jlab.fromFriendlyDateString(end);
        }

        var range = jlab.encodeRange(start, end, sevenAmOffset);

        $("#date-range").val(range).change();
    }
};
jlab.initDateTimePickers = function() {
    var myControl = {
        create: function (tp_inst, obj, unit, val, min, max, step) {
            $('<input class="ui-timepicker-input" value="' + val + '" style="width:50%">')
                .appendTo(obj)
                .spinner({
                    min: min,
                    max: max,
                    step: step,
                    change: function (e, ui) { // key events
                        // don't call if api was used and not key press
                        if (e.originalEvent !== undefined)
                            tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    },
                    spin: function (e, ui) { // spin events
                        tp_inst.control.value(tp_inst, obj, unit, ui.value);
                        tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    }
                });
            return obj;
        },
        options: function (tp_inst, obj, unit, opts, val) {
            if (typeof (opts) === 'string' && val !== undefined)
                return obj.find('.ui-timepicker-input').spinner(opts, val);
            return obj.find('.ui-timepicker-input').spinner(opts);
        },
        value: function (tp_inst, obj, unit, val) {
            if (val !== undefined)
                return obj.find('.ui-timepicker-input').spinner('value', val);
            return obj.find('.ui-timepicker-input').spinner('value');
        }
    };

    $(".datetime-input").datetimepicker({
        dateFormat: 'dd-M-yy',
        controlType: myControl,
        timeFormat: 'HH:mm'
    }).mask("99-aaa-9999 99:99", {placeholder: " "});
};
jlab.initDatePickers = function() {
    $(".date-input").datepicker({
        dateFormat: 'dd-M-yy',
    }).mask("99-aaa-9999", {placeholder: " "});
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
// Date Range selection events
$(document).on("change", "#date-range", function () {
    var selected = $("#date-range option:selected").val(),
        includeTime = $("#date-range").hasClass("datetime-range"),
        sevenAmOffset = $("#date-range").hasClass("seven-am-offset");

    if(selected === 'custom') {
        $("#custom-date-range-list").show();
    } else {
        var range = jlab.decodeRange(selected, sevenAmOffset);

        if(range.start != null && range.end != null) {
            jlab.updateDateRange(range.start, range.end, includeTime);
        }
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
    var printUrl = jlab.getPrintUrl(),
    waitForSelector = $("#image-menu-item").attr("data-wait-for-selector") || "";
    window.location = jlab.contextPath + '/convert?filename=chart.png&waitForSelector=' + encodeURIComponent(waitForSelector) + '&url=' + encodeURIComponent(printUrl);
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
            secondary: "ui-icon-triangle-1-s"
        }
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

    if($(".datetime-input").length) {
        jlab.initDateTimePickers();
    }
    if($(".date-input").length) {
        jlab.initDatePickers();
    }

    if($("#date-range").length) {
        var runLookupPromise = jlab.initDateRange();

        runLookupPromise.always(function(){
            var event = new Event('smoothnessready');
            document.dispatchEvent(event);
        });
    } else {
        var event = new Event('smoothnessready');
        document.dispatchEvent(event);
    }
});
/*Autologin*/
jlab.su = function (url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function () {
        i.parentNode.removeChild(i);
        window.location.href = url;
    };
    i.src = jlab.suLogoutUrl;
    document.body.appendChild(i);
};
jlab.iframeLogin = function (url) {
    var i = document.createElement('iframe');
    i.style.display = 'none';
    i.onload = function () {
        i.parentNode.removeChild(i);
        window.location.href = url;
    };
    i.src = jlab.iframeLoginUrl;
    document.body.appendChild(i);
};
$(document).on("click", "#login-link", function () {
    var url = $(this).attr("href");


    if(jlab.iframeLoginUrl !== '') {
        jlab.iframeLogin(url);

        return false;
    } /* else perform standard action (follow link) */
});
$(document).on("click", "#su-link", function () {
    var url = $(this).attr("href");

    jlab.su(url);

    return false;
});
/*Parameter Handling*/
/**
 * Client-side parameter handling (optional).  Smoothness template primarly assumes parameter handling is done
 * server-side.  This is an alternative useful when client has default value info like current / previous run and
 * server does not so client should handle params.
 *
 * Supports browsing mode where users can specify all parameters for a web page, partial parameters, or
 * no parameters and the app will use provided params, a set of defaults, and session favorites to determine the
 * effective parameters.
 *
 * Uses three sets of parameters in decreasing precedence to determine proper request parameters and to set a
 * bookmarkable URL:
 * 1. searchParams (URL params)
 * 2. sessionStorage params (favorites from last time)
 * 3. defaultParams (expected keys with default values)
 *
 * The defaultParams enumerate the list of expected parameters.  Keys and values are always strings, but a keys can
 * have multiple values via an array of strings.
 *
 * A URL parameter key has three states: single present, multiple present, missing.  Values are always strings but may
 * take on the special value of empty, which can't be reserved for private use since client's may actually want to
 * set a parameter to empty string, which differs from missing.  With multi-valued keys empty can't be represented:
 * a multi-valued key with a single value of empty is not the same as the key is empty.  In other words you can't
 * distinguish the difference between a single valued key that is empty and a multi-valued key that has only one value,
 * and that value is empty. This nuanced distinction is a huge complication.  It essentially means you can't use
 * presence of key to determine if request parameter value is supposed to be empty or should rely on defaults/favorites.
 *
 * You can set a default (expected) value as an empty array.  From the URL side, without additional indication there is
 * no way to differentiate between requests with an empty value for a given key vs request asking for defaults / session
 * favorites for the key.
 *
 * To overcome this we use the special parameter named "qualified", if present, regardless of
 * value (including empty value) then the client is saying use the supplied parameters as is (any missing
 * parameters are missing on purpose - user trying to set empty value).  If not present, then assume missing
 * parameters mean use value from session favorites if available, else defaults.  This means partial parameter requests
 * can not be accommodated.  So instead, we apply the qualified logic only to multi-valued keys.  Single-valued
 * keys are treated more leniently - if not qualified but provided, we use it.  Now partial-valued requests work too,
 * but only for single-valued keys - an unqualified partially specified request with multi-valued keys missing results
 * in session/defaults being used - if user intention is for empty multi-valued key then they MUST qualify request.
 * Also, if unqualified, but request contains all expected parameters then no redirect is performed.
 *
 * After all this logic is executed client-side
 * the server still has to verify the URL is "good" - if any required keys are missing the server is
 * required to deal with it.  Server must convert and validate String parameters too...
 *
 * It would have better performance to use the history API to update the URL client-side instead of server redirect.
 * However, at this time the smoothness template assumes server makes decisions on what to render via params, so
 * updated params need to be sent to server.
 *
 * @param defaultParams The expected keys with default values
 */
jlab.initParams = function(defaultParams) {
    let redirect = false,
        searchParams = new URLSearchParams(window.location.search);

    if(searchParams.has("qualified")) {
        // We still need to update session "favorites"
        Object.entries(defaultParams).forEach(function([key, defaultValue]){
            if(searchParams.has(key)) {
                sessionStorage.setItem(key, JSON.stringify(searchParams.getAll(key)));
            } else {
                /* All this complexity is really for THIS case - client says no, really, I want an empty multi-valued
                 * key (nothing selected in HTML select element with multiple attribute).  Single valued keys can
                 * be empty here too, but they could also be empty via key = empty string approach.
                 * */
                sessionStorage.setItem(key, JSON.stringify("[]")); /* Empty array */
            }
        });
    } else {
        // Check if params we expect are here, use the ones you find, otherwise resort to session/defaults + redirect
        Object.entries(defaultParams).forEach(function([key, defaultValue]){
            if(searchParams.has(key)) {
                sessionStorage.setItem(key, JSON.stringify(searchParams.getAll(key)));
            } else {
                let sessionValue = sessionStorage.getItem(key);
                if(sessionValue && sessionValue.length > 0) {
                    jlab.searchParamsAppendAll(searchParams, key, JSON.parse(sessionValue));
                } else {
                    jlab.searchParamsAppendAll(searchParams, key, defaultValue);
                }
                redirect = true;
            }
        });
    }

    if(redirect) {
        searchParams.set("qualified", "");
        window.location.search = searchParams.toString();
    }

    return redirect;
};

/**
 * Check if value is an array and if so append all string values to searchParams otherwise just append value
 */
jlab.searchParamsAppendAll = function(searchParams, key, value) {
    if(Array.isArray(value)) {
        value.forEach(item => searchParams.append(key, item));
    } else {
        searchParams.append(key, value);
    }
}