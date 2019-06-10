var jlab = jlab || {};

jlab.editableRowTable.entity = 'Movie';
jlab.editableRowTable.dialog.width = 500;
jlab.editableRowTable.dialog.height = 400;

jlab.validateTableRowForm = function () {
    return true;
};

jlab.getEditableMovieData = function() {
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