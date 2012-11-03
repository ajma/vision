require.config({
	paths: {
		jquery: 'libs/jquery/jquery',
		underscore: 'libs/underscore/underscore-min',
		backbone: 'libs/backbone/backbone-min'
	},
	shim: {
		bootstrap: ['jquery'],

		backbone: {
			deps: ['underscore', 'jquery'],
			exports: 'Backbone'
      }
  }

});

require(['router'], 
	function(Router){
		Router.initialize();
});