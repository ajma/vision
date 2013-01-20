define([ 'jquery', 'underscore', 'backbone', 'text!templates/home.html'],
function($, _, Backbone, template) {
	return {
		post : function(url, data, success) {
			$.post(url, data, function(data) {
				console.log(data);
				success(data);
			}).error(function() { $('#errorModal').modal(); });
		},
		getJSON : function(url, success) {
			$.getJSON(url, function(data) {
				console.log(data);
				success(data);
			}).error(function() { $('#errorModal').modal(); });
		},
	};
});