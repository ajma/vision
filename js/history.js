// from: http://www.filamentgroup.com/lab/jquery_plugin_for_requesting_ajax_like_file_downloads/
jQuery.download = function (url, data, method) {
    //url and data options required
    if (url && data) {
        //data can be string of parameters or array/object
        data = typeof data == 'string' ? data : jQuery.param(data);
        //split params into form inputs
        var inputs = '';
        jQuery.each(data.split('&'), function () {
            var pair = this.split('=');
            inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
        });
        //send request
        jQuery('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
		.appendTo('body').submit().remove();
    };
};

$(document).ready(function () {
    // bind to clear checkbox button
    $('#clearChecked').click(function (e) {
        e.preventDefault();
        $('td input[type=checkbox]').each(function () {
            $(this).prop("checked", false);
        });
    });

    // bind to checkbox click event so that I can select multiple checkboxes easier
    $('td input[type=checkbox]').click(function () {
        var lastIndex = -1;
        var seenFirstCheck = false;

        // find the last checked item
        $('td input[type=checkbox]').each(function (index) {
            if ($(this).is(':checked'))
                lastIndex = index;
        });


        $('td input[type=checkbox]').each(function (index) {
            if (!seenFirstCheck && $(this).is(':checked'))
                seenFirstCheck = true;
            else if (seenFirstCheck && index < lastIndex)
                $(this).prop("checked", true);
        });
    });

    // bind to export button
    $('#export').click(function (e) {
        e.preventDefault();
        var data = '';
        $('td input[type=checkbox]').each(function () {
            if ($(this).is(':checked')) {
                if (data != '')
                    data += '&';
                data += 'callnum=' + $(this).prop('name');
            }
        });
        console.log(data);
        $.download('/history/InsertionHistory', data, 'post');
    });
});