define(['jquery', 'underscore', 'backbone', 
        'views/HomeView', 'views/SearchView', 'views/AddView', 'views/OptionsView'], 
function($, _, Backbone, 
		HomeView, SearchView, AddView, OptionsView){
	var AppRouter = Backbone.Router.extend({
		routes: {
			'': 'home',
			'search': 'search',
			'add': 'add',
			'options': 'options',

			// Default
			'*actions': 'defaultAction'
		},
		
		home: function() {
			console.log('home route hit');
			var homeView = new HomeView();
			homeView.render();
		},
		
		search: function() {
			console.log('search route hit');
			var searchView = new SearchView();
			searchView.render();
		},
		
		add: function() {
			console.log('add route hit');
			var addView = new AddView();
			addView.render();
		},
		
		options: function() {
			console.log('options route hit');
			var optionsView = new OptionsView();
			optionsView.render();
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