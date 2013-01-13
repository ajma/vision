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
			var tpl = _.template($('#glasses-tpl').text());
			
			$('ul.nav .active').removeClass('active');
			$('#nav_add').addClass('active');
			
			var hideFeatures = function() {
				$('#add_features').slideUp();
			};
			
			$('#newBatchFeatureIcon').click(function() {
				hideFeatures();
				$.getJSON('/api/batches/new', function(data) {
					$('#newBatchId').text('#' + data.data.BatchId);
					$('#batchId').text(data.data.BatchId);
					$('#newBatchModal').modal();
					$('#addGlassesForm').slideDown();
				})
			});
			
			$('#loadBatchFeatureIcon').click(function() {
				$('#loadBatchModal').modal();
			});
			
			var count = 0;
			$('#addButton').click(function() {
				var newGlasses = $('#rxform').serialize();
				console.log(newGlasses);
				$.post('/api/glasses/add', newGlasses, function(data) {
					console.log(data.data);
					$('#progressCount').text(++count);
					$('#progressBar').width((count*100/40) + '%');
					var a = $(tpl(data.data));
					$('#log').prepend(a);
					a.fadeIn('slow');
				});
			});
		}
	});
});