define([ 'jquery', 'underscore', 'backbone', 'text!templates/options.html'],
function($, _, Backbone, template) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(template).hide().fadeIn();
			
			$('ul.nav .active').removeClass('active');
			$('#nav_options').addClass('active');
		}
	});
});