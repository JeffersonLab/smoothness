var jlab = jlab || {};

jlab.editableRowTable.entity = 'Movie';
jlab.editableRowTable.dialog.width = 500;
jlab.editableRowTable.dialog.height = 400;

jlab.validateTableRowForm = function () {
    return true;
};

jlab.getEditableMovieData = function () {
    var title = $("#row-title").val(),
        description = $("#row-description").val(),
        rating = $("#row-rating").val(),
        duration = $("#row-duration").val(),
        release = $("#row-release").val();

    return {title: title, description: description, rating: rating, duration: duration, release: release};
};

$(document).on("table-row-add", function () {
    if (!jlab.validateTableRowForm()) {
        return;
    }

    var url = jlab.contextPath + "/ajax/add-movie",
        data = jlab.getEditableMovieData();
    $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});

$(document).on("table-row-edit", function () {
    if (!jlab.validateTableRowForm()) {
        return;
    }

    var url = jlab.contextPath + "/ajax/edit-movie",
        data = jlab.getEditableMovieData(),
        $dialog = $("#table-row-dialog");

    data.id = $(".selected-row").attr("data-id");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});

$(document).on("click", "#remove-row-button", function () {
    var idArray = [];

    if ($(".editable-row-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $(".editable-row-table .selected-row").each(function () {
        var id = $(this).attr("data-id");

        idArray.push(id);
    });

    var url = jlab.contextPath + "/ajax/remove-movie",
        data = {'id[]': idArray},
        $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});

/*Custom time picker*/
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

$(".date-time-field").datetimepicker({
    dateFormat: 'dd-M-yy',
    controlType: myControl,
    timeFormat: 'HH:mm'
}).mask("99-aaa-9999 99:99", {placeholder: " "});

$(".date-field").datepicker({
    dateFormat: "dd-M-yy"
});

$(document).on("click", "#excel-menu-item", function () {
    $("#excel").click();
});

$("#mpaa-select").select2({
    width: 200
});

$(function () {
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