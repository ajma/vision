$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

var sphcylFormat = function (val) {
    return (val >= 0 ? '+' : '') + val.toFixed(2);
};

var axisFormat = function (val) {
    if (val >= 100)
        return val.toString();
    else if (val >= 10)
        return '0' + val.toString();
    else
        return '00' + val.toString();
};