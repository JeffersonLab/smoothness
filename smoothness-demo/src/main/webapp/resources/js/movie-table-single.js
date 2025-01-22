$(document).on("click", "#open-edit-row-dialog-button", function () {
    var $selectedRow = $(".editable-row-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    $("#row-title").val($selectedRow.find("td:nth-child(1)").text());
    $("#row-description").val($selectedRow.find("td:nth-child(2)").text());
    $("#row-rating").val($selectedRow.find("td:nth-child(3)").text());
    $("#row-duration").val($selectedRow.find("td:nth-child(4)").text());
    $("#row-release").val($selectedRow.find("td:nth-child(5)").text());
});