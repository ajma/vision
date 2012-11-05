define([ 'jquery', 'underscore', 'backbone', 'text!templates/home.html'],
function($, _, Backbone, template) {
	return Backbone.View.extend({
		el : $('#body'),
		render : function() {
			this.$el.empty();
			var compiledTemplate = _.template(template, {});
			this.$el.append(compiledTemplate).hide().fadeIn();
		}
	});
});