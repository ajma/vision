define([ 'jquery', 'underscore', 'backbone', 
         'text!templates/search.html', 'text!templates/rxform.html'],
function($, _, Backbone, searchTemplate, rxForm) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(searchTemplate).hide().fadeIn();
			$('#rxform').append(rxForm);
			
			$('ul.nav .active').removeClass('active');
			$('#nav_search').addClass('active');
			
			
			$('#searchButton').click(function() {
				var query = $('#rxform').serialize();
				$.post('/api/glasses/search', query, function(data) {
					var rowTemplate = $('#resultRowTemplate').html();
					var tableBody = $('#searchResults tbody');
					tableBody.append(_.template(rowTemplate, data));
				});
			});
		}
	});
});