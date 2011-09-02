var formToJson = function (form) {
    var glasses = {};
    form.each(function (index) {
        switch ($(this).attr('type')) {
            case 'button':
                break;
            case 'checkbox':
                glasses[$(this).attr('name')] = $(this).is(':checked');
                break;
            default:
                glasses[$(this).attr('name')] = $(this).val();
        }
    });
    return glasses;
};

$(document).ready(function () {
    // add button click
    $('#add').click(function (e) {
        e.preventDefault();
        $('#add').attr('disabled', 'disabled');
        $('#addWaiting').show();
        var glasses = formToJson($('#rxform').find('input,select'));
        var errMsg = '';
        var rxValRangeCheck = function (property, min, max) {
            if (glasses[property] == '')
                errMsg += property + ' missing<br />';
            if (glasses[property] < min || glasses[property] > max)
                errMsg += property + ' must be between ' + min + ' and ' + max + '<br />';
        };

        rxValRangeCheck('OD_Spherical', -20, 20);
        rxValRangeCheck('OD_Cylindrical', -20, 0);
        rxValRangeCheck('OD_Axis', 0, 180);
        rxValRangeCheck('OD_Add', 0, 10);
        rxValRangeCheck('OS_Spherical', -20, 20);
        rxValRangeCheck('OS_Cylindrical', -20, 20);
        rxValRangeCheck('OS_Axis', 0, 180);
        rxValRangeCheck('OS_Add', 0, 10);

        if (errMsg != '') {
            if (console) console.log(errMsg);
            $('#addErr').html(errMsg).fadeIn(200);
            $('#add').removeAttr('disabled');
            $('#addWaiting').hide();
        }
        else {
            $('#addErr').fadeOut(200);
            $.ajax({
                url: '/Add/Add',
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify(glasses),
                contentType: 'application/json; charset=utf-8',
                success: function (msg) {
                    msg.Glasses = glasses;
                    $('#addMsgTmpl').tmpl(msg).prependTo('#addMsg');

                    $('#add').removeAttr('disabled');
                    $('#addWaiting').slideUp();

                    // clear form so user can add next pair of glasses
                    $('#rxform').find('.rx_box').each(function () { $(this).val(''); });
                    $('#rxform').find('select').each(function () { $(this).val('U'); });
                },
                error: function (msg) {
                    $('#addErr').html('Error Adding').fadeIn(200);
                    $('#add').removeAttr('disabled');
                    $('#addWaiting').hide();
                }
            });
        }
    });

    $('#clear').click(function (e) {
        e.preventDefault();
        $('#rxform').find('.rx_box').each(function () { $(this).val(''); });
        $('#rxform').find('select').each(function () { $(this).val('U'); });
        $('#addErr').fadeOut(200);
    });
});