var jlab = jlab || {};

jlab.editableRowTable.entity = 'Movie';
jlab.editableRowTable.dialog.width = 500;
jlab.editableRowTable.dialog.height = 400;

jlab.validateTableRowForm = function () {
    return true;
};

$(document).on("click", "#table-row-save-button", function () {
    if (!jlab.validateTableRowForm()) {
        return;
    }

    var title = $("#row-title").val(),
        description = $("#row-description").val(),
        rating = $("#row-rating").val(),
        duration = $("#row-duration").val(),
        release = $("#row-release").val(),
        url = jlab.contextPath + "/ajax/add-movie",
        data = {title: title, description: description, rating: rating, duration: duration, release: release},
        $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});

$(document).on("click", "#remove-row-button", function () {
    var idArray = new Array();

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