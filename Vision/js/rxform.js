; (function ($, window, document, undefined) {
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

    function Plugin(element, options) {
        this.element = element;

        this.options = $.extend({}, defaults, options);

        this._defaults = defaults;
        this._name = pluginName;

        this.init();
    }

    Plugin.prototype.init = function () {
        var options = this.options;
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
            $(this.element).focus(function () {
                if ($(this).val() === '') {
                    $(this).val('0');
                    $(this).change();
                }
            });
        }
        $(this.element).change(function () {
            var currentValue = parseFloat($(this).val());
            if (currentValue >= options.autoDecimal) currentValue = currentValue / 100;
            updateBox(this, currentValue);
        });
        $(this.element).keydown(function (event) {
            switch (event.which) {
                case 40:
                    event.preventDefault();
                    break;
                case 38:
                    event.preventDefault();
                    break;
            }
        });
        $(this.element).keyup(function (event) {
            switch (event.which) {
                case 40:
                    updateBox(this, parseFloat($(this).val()) - options.littleStep);
                    event.preventDefault();
                    break;
                case 38:
                    updateBox(this, parseFloat($(this).val()) + options.littleStep);
                    event.preventDefault();
                    break;
                case 34:
                    updateBox(this, parseFloat($(this).val()) - options.bigStep);
                    event.preventDefault();
                    break;
                case 33:
                    updateBox(this, parseFloat($(this).val()) + options.bigStep);
                    event.preventDefault();
                    break;
            }
        });
    };

    // A really lightweight plugin wrapper around the constructor, 
    // preventing against multiple instantiations
    $.fn[pluginName] = function (options) {
        return this.each(function () {
            if (!$.data(this, 'plugin_' + pluginName)) {
                $.data(this, 'plugin_' + pluginName, new Plugin(this, options));
            }
        });
    }

})(jQuery, window, document);