//Javascript que lanza el pop-up de mensajes
function messagePopUp(message, close) {
    $("[name='messagePopUpSuccess']").html(message);
    $("[name='message-success']").fadeIn(1000, function () {
        if (close) {
            var command = "$('[name=" + '"message-success"' + "]').fadeOut(1000);";
            setTimeout(command, 2000);
        }
    });
}

function messagePopUpError(message, close) {
    $("[name='messagePopUpError']").html(message);
    $("[name='message-error']").fadeIn(1000, function () {
        if (close) {
            var command = "$('[name=" + '"message-error"' + "]').fadeOut(1000);";
            setTimeout(command, 2000);
        }
    });
}