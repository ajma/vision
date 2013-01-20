define([ 'jquery', 'underscore', 'backbone', 'vision', 'rxform',
         'text!templates/search.html', 'text!templates/rxform.html'],
function($, _, Backbone, vision, rxform, searchTemplate, rxForm) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(searchTemplate).hide().fadeIn();

			$('#rxform').append(rxForm);
			$('input.sph').rxForm({ autoZero: false });
	        $('input.cyl').rxForm({ autoZero: false, min: -20, max: 0 });
	        $('input.axis').rxForm({ autoZero: false, min: 0, max: 180, littleStep: 5, bigStep: 10, beforeDecimal: 3, afterDecimal: 0, autoDecimal: 999 });
	        $('input.add').rxForm({ autoZero: false, min: 0, max: 20 });
			
			$('ul.nav .active').removeClass('active');
			$('#nav_search').addClass('active');

			$('#searchButton').click(function() {
				$('#searchButton').addClass('disabled');
				$('#searching').fadeIn();
				var tableBody = $('#searchResults tbody');
				tableBody.empty();
				var query = $('#rxform').serialize();
				vision.post('/api/glasses/search', query, function(data) {
					var rowTemplate = $('#resultRowTemplate').html();					
					tableBody.append(_.template(rowTemplate, { results: data }));
					$('#searchButton').removeClass('disabled');
					$('#searching').hide();
				});
			});
		}
	});
});