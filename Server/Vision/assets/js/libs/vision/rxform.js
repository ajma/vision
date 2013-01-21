define([ 'jquery', 'vision' ],
function($, vision) {
    var pluginName = 'rxForm',
        defaults = {
            min: -20,
            max: 20,
            littleStep: 0.25,
            bigStep: 1,

            afterDecimal: 2,
            beforeDecimal: 1,
            // if the number is over this value, divide by 100
            autoDecimal: 25,
            autoZero: true
        };

    function RxForm(element, sph_options, cyl_options, axis_options, add_options) {
        var form = $(element);

        this._defaults = defaults;
        this._name = pluginName;

        rxInput(form.find('input.sph'), $.extend({}, defaults, sph_options));
        rxInput(form.find('input.cyl'), $.extend({}, defaults, cyl_options));
        rxInput(form.find('input.axis'), $.extend({}, defaults, axis_options));
        rxInput(form.find('input.add'), $.extend({}, defaults, add_options));
        
        form.find('.rxFormTipsLink').click(function(ev) {
        	ev.preventDefault();
        	vision.help.rxFormShorcuts();
        });
    }

    var rxInput = function (inputBox, options) {
        var updateBox = function (box, val) {
            var updatedValue = val;
            if (isNaN(val)) updatedValue = 0;
            if (options.min <= 0 && options.max <= 0 && updatedValue >= 0) updatedValue = -updatedValue;
            if (updatedValue > options.max) updatedValue = options.max;
            if (updatedValue < options.min) updatedValue = options.min;
            var padding = '';
            for(var i=1; i < options.beforeDecimal;i++) {
                if(Math.pow(10, i) > updatedValue)
                    padding+='0';
            }
            $(box).val(padding + updatedValue.toFixed(options.afterDecimal));
        };

        if (options.autoZero) {
            $(inputBox).focusout(function () {
                if ($(this).val() === '') {
                    $(this).val('0');
                    $(this).change();
                }
            });
        }
        $(inputBox).change(function () {
            var currentValue = parseFloat($(this).val());
            if (currentValue >= options.autoDecimal) currentValue = currentValue / 100;
            updateBox(this, currentValue);
        });
        $(inputBox).keydown(function (event) {
        	if(event.which == 40) {
                updateBox(this, parseFloat($(this).val()) - (event.shiftKey ? options.bigStep : options.littleStep));
                event.preventDefault();
        	} else if(event.which == 38) {
                updateBox(this, parseFloat($(this).val()) + (event.shiftKey ? options.bigStep : options.littleStep));
                event.preventDefault();
            }
        });
    };

    // A really lightweight plugin wrapper around the constructor, 
    // preventing against multiple instantiations
    $.fn[pluginName] = function (sph_options, cyl_options, axis_options, add_options) {
        return this.each(function () {
            if (!$.data(this, 'plugin_' + pluginName)) {
                $.data(this, 'plugin_' + pluginName, new RxForm(this, sph_options, cyl_options, axis_options, add_options));
            }
        });
    }

    $.fn['rxTips'] = function () {
        return this.each(function () {
            $(this).find('.alert').show();
        });
    }
});