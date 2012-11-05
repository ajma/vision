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
			$('#addButton').click(function() {
				var test = $('#rxform').serialize();
				$.post('/api/glasses/add', test, function() {
					alert(test);
				});
			});
		}
	});
});