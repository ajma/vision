require.config({
	paths : {
		jquery : 'libs/jquery/jquery',
		underscore : 'libs/underscore/underscore-min',
		backbone : 'libs/backbone/backbone-min',
		bootstrap : 'libs/bootstrap/bootstrap.min',
		text : 'libs/require/text',
		templates : '../templates'
	},
	shim : {
		bootstrap : [ 'jquery' ],

		underscore : {
			deps : [ 'jquery' ],
			exports : '_'
		},

		backbone : {
			deps : [ 'underscore', 'jquery' ],
			exports : 'Backbone'
		}
	}
});

require([ 'jquery', 'router', 'bootstrap' ], function($, Router) {
	Router.initialize();
});