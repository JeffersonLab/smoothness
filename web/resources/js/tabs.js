var jlab = jlab || {};

jlab.isRequest = function() {
    return jlab.ajaxInProgress;
}

jlab.requestStart = function() {
    jlab.ajaxInProgress = true;      
}

jlab.requestEnd = function() {
    jlab.ajaxInProgress = false;   
}

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
}

jlab.integerWithCommas = function(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

$(document).on("click", ".dialog-ready", function() {
    var title = $(this).attr("data-dialog-title");    
        
    jlab.closePageDialogs();
    jlab.openPageInDialog($(this).attr("href"), title);        
    return false; 
});
    
$(document).on("click", ".dialog-close-button", function() {
    $(this).closest(".dialog").dialog("close");
});

/*Enter key will often do plain old form submit, which is generally not what we want in a dialog!*/
$(document).on("keypress", ".dialog input", function(e) {
    if(e.which == 13) {
        $(this).closest("form").find(".dialog-submit-button").click();
        
        return false;
    }
});

$(document).on("change", ".change-submit", function() {
    $(this).closest("form").submit();
});

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


