require.config({
	paths : {
		jquery : 'libs/jquery/jquery',
		underscore : 'libs/underscore/underscore-min',
		backbone : 'libs/backbone/backbone-min',
		bootstrap : 'libs/bootstrap/bootstrap.min',
		text : 'libs/require/text',
		templates : '../templates',
		vision : 'libs/vision/vision',
		rxform : 'libs/vision/rxform'
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
		},
		
		rxform : [ 'jquery' ]
	}
});

require([ 'jquery', 'backbone', 'router', 'bootstrap' ], function($, Backbone, Router) {
	Router.initialize();
});