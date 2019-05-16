var jlab = jlab || {};
jlab.feedback = jlab.feedback || {};

jlab.feedback.sendEmail = function() {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var subject = $("#subject").val(),
            body = $("#body").val();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/feedback",
        type: "POST",
        data: {
            subject: subject,
            body: body
        },
        dataType: "html"
    });

    request.done(function(data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to send email: ' + $(".reason", data).html());
        } else {
            $("#subject").val('');
            $("#body").val('');
            alert('Email sent');
        }

    });

    request.error(function(xhr, textStatus) {
        window.console && console.log('Unable to send email: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to send email');
    });

    request.always(function() {
        jlab.requestEnd();
    });
};

$(document).on("click", "#send-feedback-button", function() {
    jlab.feedback.sendEmail();
});
