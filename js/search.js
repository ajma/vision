$(document).ready(function () {
    var currentTabIndex = 0;
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

    // search click
    $('#submit').click(function () {
        var glasses = { search: formToJson($('#rxform').find('input,select')) };
        var sphCylFormat = function (num) {
            if (num == null)
                return '';
            else
                return (num >= 0) ? '+' + num.toFixed(2) : num.toFixed(2);
        };
        var axisFormat = function (num) {
            if (num < 10)
                return '00' + num;
            else if (num < 100)
                return '0' + num;
            else
                return num;
        };
        $('#searchResults').html('');
        $('#searchWaiting').show();
        $.ajax({
            url: '/search/query',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(glasses),
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                $('#rxTemplate').tmpl(msg).appendTo('#rxList');
            }
        });
    });
});