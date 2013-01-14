define([ 'jquery', 'underscore', 'backbone', 'text!templates/home.html'],
function($, _, Backbone, template) {
	return {
		post : function(url, data, success) {
			$.post(url, data, function(response) {
				console.log(response.data);
				if(response.status == "200 OK") {
					sucess(response);
				} else {
					$('#errorModal').modal();
				}
			});
		}
	};
});