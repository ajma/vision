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
    // create div elemnt
    var div = function (innerHtml, cssclass) {
        return '<div class="' + cssclass + '">' + innerHtml + '</div>';
    };
    // focusOut RX cleanup
    var focusoutRxCleaup = function () {
        switch (this.name.substr(3)) {
            case 'Spherical':
                if ($(this).val() >= 25 || $(this).val() <= -25) $(this).val($(this).val() / 100);
                $(this).val(Math.round($(this).val() * 4) / 4);
                break;
            case 'Cylindrical':
                if ($(this).val() >= 25 || $(this).val() <= -25) $(this).val(-Math.abs($(this).val()) / 100);
                $(this).val(-Math.abs(Math.round($(this).val() * 4) / 4));
                break;
            case 'Axis':
                $(this).val(Math.round($(this).val() / 1) * 1);
                break;
            case 'Add':
                $(this).val(Math.abs(Math.round($(this).val() * 4) / 4));
                break;
            case 'up': // 'Group' gets truncated to 'up'
                $(this).val(Math.round($(this).val()));
                break;
        };
    };

    // scrollTo jQuery plugin
    $.fn.scrollTo = function () {
        $('html, body').animate({ scrollTop: $(this).position().top }, 500);
    };

    // hook up jQuery tools tab
    $('#header ul').tabs("#body > div.pane", {
        effect: 'fade',
        fadeOutSpeed: 250,
        history: true,
        onBeforeClick: function (event, tabIndex) {
            currentTabIndex = tabIndex;
            switch (tabIndex) {
                case 0:
                    $('#sendButton').attr('value', 'Add');
                    $('#rxInput').fadeIn();
                    break;
                case 1:
                    $('#sendButton').attr('value', 'Search');
                    $('#rxInput').fadeIn();
                    break;
                case 2:
                    $('#rxInput').fadeOut();
                    break;
            }
        }
    });

    // add form input box data cleanup
    $('#addForm').find('input,').focusout(focusoutRxCleaup);

    // add button click
    $('#add').click(function () {
        $('#add').attr('disabled', 'disabled');
        $('#addWaiting').show();
        var glasses = formToJson($('#addForm').find('input,select'));
        var errMsg = '';
        var rxVal = function (property, min, max) {
            if (glasses[property] == '')
                errMsg += property + ' missing<br />';
            if (glasses[property] < min || glasses[property] > max)
                errMsg += property + ' must be between ' + min + ' and ' + max + '<br />';
        };
        var sphCylFormat = function (num) {
            if (num == null)
                return '';
            else
                return (num >= 0) ? '+' + num.toFixed(2) : num.toFixed(2);
        };

        rxVal('OD_Spherical', -20, 20);
        rxVal('OD_Cylindrical', -20, 0);
        rxVal('OD_Axis', 0, 180);
        rxVal('OD_Add', 0, 10);
        rxVal('OS_Spherical', -20, 20);
        rxVal('OS_Cylindrical', -20, 20);
        rxVal('OS_Axis', 0, 180);
        rxVal('OS_Add', 0, 10);

        if (errMsg != '') {
            $('#addErr').html(errMsg).fadeIn(200);
            $('#add').removeAttr('disabled');
            $('#addWaiting').hide();
        }
        else {
            $('#addErr').fadeOut(200);
            $.ajax({
                url: '/Glasses/Add',
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify(glasses),
                contentType: 'application/json; charset=utf-8',
                success: function (msg) {
                    var newmsg = $(div('Added with call number ' + msg.Group + ' / ' + msg.Number + '.', 'success hidden'));
                    $('#addMsg').prepend(newmsg);
                    newmsg.slideDown();
                    $('#add').removeAttr('disabled');
                    $('#addWaiting').slideUp();
                },
                error: function (msg) {
                    $('#addErr').html('Error Adding').fadeIn(200);
                    $('#add').removeAttr('disabled');
                    $('#addWaiting').hide();
                }
            });
        }
    });

    // add clear click
    $('#addClear').click(function () {
        $('#addForm').find('.rx_box').val('');
        $('#addForm').find('select').each(function () { $(this).val('U'); });
        $('#searchResults').html('');
        $('#addMsg').hide().html('');
    });

    // search type choosing
    $('input[name=searchType]').click(function () {
        if ($(this).val() === 'rx' && !$('#rxsearchform').is(':visible')) {
            $('#callnumsearchform').slideUp();
            $('#rxsearchform').slideDown();
        }
        if ($(this).val() === 'callnum' && !$('#callnumsearchform').is(':visible')) {
            $('#callnumsearchform').slideDown();
            $('#rxsearchform').slideUp();
        }
    });

    // search form input box data cleanup
    $('#searchForm').find('input,').focusout(focusoutRxCleaup);

    // search click
    $('#search').click(function () {
        var glasses = { search: formToJson($('#searchForm').find('input,select')) };
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
            url: '/Glasses/Search',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(glasses),
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                var results = '';
                var counter = 0;
                $.each(msg, function (index, value) {
                    results += '<div class="span-24 last rx_result_row' + (counter++ % 2 == 1 ? '_alt' : '') + '">' +
								'<div class="rx_result_col matchscore">' + Math.round(value.MatchScore) + '</div><div class="rx_result_col_border callnum">' + sphCylFormat(value.Glasses.Group).split('.')[0] + '/' + value.Glasses.Number + '</div>' +
                                '<span class="hidden">' + value.MatchScoreDetails + '</span>' +
								'<div class="rx_result_col">' + sphCylFormat(value.Glasses.OD_Spherical) + '</div><div class="rx_result_col">' + sphCylFormat(value.Glasses.OD_Cylindrical) + '</div><div class="rx_result_col">' + axisFormat(value.Glasses.OD_Axis) + '</div><div class="rx_result_col_border">' + sphCylFormat(value.Glasses.OD_Add) + '</div>' +
								'<div class="rx_result_col">' + sphCylFormat(value.Glasses.OS_Spherical) + '</div><div class="rx_result_col">' + sphCylFormat(value.Glasses.OS_Cylindrical) + '</div><div class="rx_result_col">' + axisFormat(value.Glasses.OS_Axis) + '</div><div class="rx_result_col_border">' + sphCylFormat(value.Glasses.OS_Add) + '</div>' +
                                '<div class="rx_result_col">' + (value.Glasses.Sunglasses ? 'Y' : 'N') + '</div><div class="rx_result_col">' + value.Glasses.Gender + '</div><div class="rx_result_col">' + value.Glasses.Size + '</div>' +
                                '</div>';
                });
                $('#searchResults').html(results);
                $('#rx_result_header').scrollTo();
                $('#searchWaiting').hide();
                $('.matchscore').tooltip({
                    tip: '#tooltip',
                    effect: 'slide',
                    position: 'bottom right',
                    predelay: 500,
                    onShow: function () {
                        $('#tooltip').find('pre').text(this.getTrigger().parent().find('.hidden').text());
                    }
                });
            }
        });
    });

    $('#searchClear').click(function () {
        $('#searchForm').find('.rx_box').each(function () { $(this).val(''); });
        $('#searchForm').find('select').each(function () { $(this).val('U'); });
        $('#searchResults').html('');
    });

    var clickedResultRow = null;
    $('.rx_result_row, .rx_result_row_alt').live('click', function () {
        var callnum = $(this).find('.callnum').html().split('/');
        $('#removeDialog').find('.group').text(callnum[0]);
        $('#removeDialog').find('.number').text(callnum[1]);
        var rx = div('OD:', 'prepend-2 span-1');
        var divs = $(this).find('div');
        for (var i = 2; i <= 9; i++) {
            if (i === 6)
                rx += div('OS:', 'prepend-2 span-1');
            rx += div($(divs[i]).text(), 'span-1');
        }
        $('#removeDialog').find('.rx').html(rx);
        $('#removeDialog').overlay().load();
        clickedResultRow = this;
    });

    $('.rx_result_row, .rx_result_row_alt').live('mouseover', function () {
        $(this).addClass('rx_result_row_hilite');
    });

    $('.rx_result_row, .rx_result_row_alt').live('mouseout', function () {
        $(this).removeClass('rx_result_row_hilite');
    });

    // need to call this to intialize. might be a jquery tools bug
    $('#removeDialog').overlay({
        top: '20%',
        fixed: false,
        mask: {
            color: '#ebecff',
            loadSpeed: 200,
            opacity: 0.9
        },
        closeOnClick: false
    }).load();

    $('#removeDialog').find('button').click(function (e) {
        if ($(this).text() === 'Remove') {
            $.ajax({
                url: '/Glasses/Remove',
                type: 'POST',
                dataType: 'json',
                data: '{ group:' + $('#removeDialog').find('.group').text() + ', number:' + $('#removeDialog').find('.number').text() + ' }',
                contentType: 'application/json; charset=utf-8',
                success: function (msg) {
                    alert(clickedResultRow);
                    $(clickedResultRow).slideUp(200);
                    clickedResultRow = null;
                },
                error: function (msg) {
                    alert('Could not remove');
                }
            });
        }
        $('#removeDialog').overlay().close();
    });

    $('#getHistory').click(function () {
        $('#historyResults').html('');
        $.ajax({
            url: '/Glasses/History',
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            success: function (msg) {
                var result = '';
                $.each(msg, function (index, value) {
                    var removalDate = new Date(parseInt(value.RemovalDate.substr(6)));
                    result += div(div('Entry ID: ' + value.GlassesHistoryID, 'span-2') + div(value.Group + '/' + value.Number, 'span-2') + div(removalDate.f('yyyy-NNN-dd hh:mm a'))
                     , 'span-24 last');
                });
                $('#historyResults').html(result);
            }
        });
    });
});