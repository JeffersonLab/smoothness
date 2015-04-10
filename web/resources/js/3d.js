/**
 * Global String Enhancements
 */
if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function(str) {
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
 * Common Namespaced Functions
 */
//Function to ensure a AJAX request is only issued serially
jlab.isRequest = function() {
    return jlab.ajaxInProgress;
};
jlab.requestStart = function() {
    jlab.ajaxInProgress = true;      
};
jlab.requestEnd = function() {
    jlab.ajaxInProgress = false;   
};
//Display a piece of another page in a dialog
jlab.openPageInDialog = function(href, title) {
    $("<div class=\"page-dialog\"></div>")
    .load(href + ' .dialog-content')
    .dialog({
        autoOpen: true,
        title: title,
        width: 840,
        height: 590,
        close: function() {
            $(this).dialog('destroy').remove();    
        }
    });    
};
jlab.closePageDialogs = function() {
    $(".page-dialog").dialog('destroy').remove();
};
// Format a string representation of an integer with commas
jlab.integerWithCommas = function(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};
/**
 * Common Event Handlers
 */
// Filter flyout events
$(document).on("click", "#filter-flyout-link", function() {
    $("#filter-flyout-panel").slideDown();
    return false;
});
$(document).on("click", "#filter-flyout-close-button", function() {
    $("#filter-flyout-panel").slideUp();
    return false;
});
// Dialog events
$(document).on("click", ".dialog-ready", function() {
    var title = $(this).attr("data-dialog-title");    
        
    jlab.closePageDialogs();
    jlab.openPageInDialog($(this).attr("href"), title);        
    return false; 
});    
$(document).on("click", ".dialog-close-button", function() {
    $(this).closest(".dialog").dialog("close");
});
// Prevent keypress from doing Full page HTTP POST in a dialog; instead programatically click dialog submit button
$(document).on("keypress", ".dialog input", function(e) {
    if(e.which === 13) {
        $(this).closest("form").find(".dialog-submit-button").click();
        
        return false;
    }
});
// Some form inputs can trigger submit on change
$(document).on("change", ".change-submit", function() {
    $(this).closest("form").submit();
});