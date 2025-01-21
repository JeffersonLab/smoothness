$(document).on("click", "#open-edit-rating-dialog-button", function () {
    var idArray = [],
        titleArray = [],
        descriptionArray = [],
        ratingArray = [],
        durationArray = [],
        releaseArray = [];

    if ($(".multiselect-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $(".multiselect-table .selected-row").each(function () {
        var id = $(this).attr("data-id"),
            title = $(this).find("td:nth-child(1)").text(),
            description = $(this).find("td:nth-child(2)").text(),
            rating = $(this).find("td:nth-child(3)").text(),
            duration = $(this).find("td:nth-child(4)").text(),
            release = $(this).find("td:nth-child(5)").text();

        idArray.push(id);
        titleArray.push(title);
        descriptionArray.push(description);
        ratingArray.push(rating);
        durationArray.push(duration);
        releaseArray.push(release);
    });

    var $selectedList = $("#movie-selected-row-list");

    $selectedList.attr("data-id-json", JSON.stringify(idArray));

    $selectedList.empty();

    for (var i = 0; i < titleArray.length; i++) {
        $selectedList.append('<li>' + String(titleArray[i]).encodeXml() + '</li>');
    }

    var count = $("#selected-count").text() * 1;
    var rowStr = (count === 1) ? ' Movie' : ' Movies';
    $("#dialog-selected-count").text(count + rowStr);

    $("#edit-rating").val(ratingArray[0]);

    var rowsDiffer = false;

    for (var i = 1; i < ratingArray.length; i++) {
        if (ratingArray[0] !== ratingArray[i]) {
            rowsDiffer = true;
            break;
        }
    }

    if (rowsDiffer) {
        $(".rows-differ-message").show();
    } else {
        $(".rows-differ-message").hide();
    }

    $("#rating-dialog").dialog("open");
});

$(document).on("click", "#rating-save-button", function () {
    var idArray = [];

    if ($(".editable-row-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $(".editable-row-table .selected-row").each(function () {
        var id = $(this).attr("data-id");

        idArray.push(id);
    });

    var url = jlab.contextPath + "/ajax/edit-movie-rating",
        rating = $("#edit-rating").val(),
        data = {'id[]': idArray, rating: rating},
        $dialog = $("#table-row-dialog");

    jlab.doAjaxJsonPostRequest(url, data, $dialog, true);
});

$(function () {
    $("#rating-dialog").dialog({
        autoOpen: false,
        width: 500,
        height: 500,
        resizable: false
    });
});

$(document).on("click", ".editable-row-table tr a", function () {
    alert("You clicked on: " + $(this).closest("tr").find("td:first-child").text());
});