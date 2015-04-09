$(document).on("click", "#filter-flyout-link", function() {
    $("#filter-flyout-panel").slideDown();
    return false;
});
$(document).on("click", "#filter-flyout-close-button", function() {
    $("#filter-flyout-panel").slideUp();
    return false;
});
