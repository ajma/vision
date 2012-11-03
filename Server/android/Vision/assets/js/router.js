define(['jquery','underscore','backbone'], 
function($, _, Backbone){
	var AppRouter = Backbone.Router.extend({
		routes: {
			'search': 'search',
			'add': 'add',
			'export': 'export',

			// Default
			'*actions': 'defaultAction'
		},
		
		search: function() {
			console.log('search route hit');
		},
		
		add: function() {
			console.log('add route hit');
		},
		
		export: function() {
			console.log('export route hit');
		},
		
		defaultAction: function(actions) {
			console.log('No route:', actions);
		}
	});

	var initialize = function(){
		var app_router = new AppRouter;
		Backbone.history.start();
	};
	return {
		initialize: initialize
	};
});