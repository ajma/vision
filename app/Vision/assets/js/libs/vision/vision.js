define([ 'jquery', 'underscore', 'backbone', 'text!templates/help.html'],
function($, _, Backbone, helpTemplate) {
	var help = $(helpTemplate);
	var showHelp = function(id) {
		return function() {
			$('#helpModal .modal-body').append(help.find('#' + id));
			$('#helpModal').modal();
		};
	};

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
		help : {
			rxFormShorcuts : showHelp('rxFormShorcuts')
		}
	};
});