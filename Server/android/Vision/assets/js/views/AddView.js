define([ 'jquery', 'underscore', 'backbone', 'text!templates/add.html', 'text!templates/rxform.html'],
function($, _, Backbone, addTemplate, rxForm) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(addTemplate).hide().fadeIn();
			$('#rxform').append(rxForm);
		}
	});
});