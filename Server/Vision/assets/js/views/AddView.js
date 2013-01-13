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
			var count = 0;
			var batchId = 0;
			var tpl = _.template($('#glasses-tpl').text());
			
			$('ul.nav .active').removeClass('active');
			$('#nav_add').addClass('active');
			
			var hideFeatures = function() {
				$('#add_features').fadeOut();
			};
			
			var setProgress = function() {
				$('#progressCount').text(count);
				$('#progressBar').width((count*100/40) + '%');
			}
			
			$('#newBatchFeatureIcon').click(function() {
				hideFeatures();
				$.getJSON('/api/batches/new', function(data) {
					batchId = data.data;
					$('#newBatchId').text('#' + batchId);
					$('#batchId').text(batchId);
					$('#newBatchModal').modal();
					$('#addGlassesForm').slideDown();
					count = 0;
					setProgress();
				})
			});
			
			$('#loadBatchFeatureIcon').click(function() { $('#loadBatchModal').modal(); });
			
			$('#loadBatchButton').click(function() {
				hideFeatures();
				$('#addGlassesForm').slideDown();
				batchId = $('#loadBatchId').val();
				$('#batchId').text(batchId);
			});
						
			$('#addButton').click(function() {
				var newGlasses = $('#rxform').serialize();
				console.log(newGlasses);
				$.post('/api/glasses/add', newGlasses, function(data) {
					console.log(data.data);
					count++;
					setProgress();
					var a = $(tpl(data.data));
					$('#log').prepend(a);
					a.fadeIn('slow');
					
					var batch = { BatchId: batchId, Glasses: String(data.data.GlassesId)};
					$.post('/api/batches/addglasses', batch, function() { });
				});
			});
		}
	});
});