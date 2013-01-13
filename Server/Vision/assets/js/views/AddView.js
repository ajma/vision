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
			};
			
			var logGlasses = function(glasses) {
				var logHtml = $(tpl(glasses));
				$('#log').prepend(logHtml);
				logHtml.fadeIn('slow');
			};
			
			$('#newBatchFeatureIcon').click(function() {
				hideFeatures();
				$.getJSON('/api/batches/new', function(response) {
					batchId = response.data;
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
				batchId = $('#loadBatchId').val();
				$.getJSON('/api/batches/get/' + batchId, function(response) {
					var glasses = response.data.Glasses.trim().split(' ');
					count = glasses.length;
					setProgress();
					$('#batchId').text(batchId);
					$('#addGlassesForm').slideDown();
					
					$.each(glasses, function() {
						console.log('Loading GlassesID:' + this);
						$.getJSON('/api/glasses/get/' + this, function(response) {
							logGlasses(response.data);
						})
					});
				});
			});
						
			$('#addButton').click(function() {
				var newGlasses = $('#rxform').serialize();
				console.log(newGlasses);
				$.post('/api/glasses/add', newGlasses, function(response) {
					console.log(response.data);
					count++;
					setProgress();
					logGlasses(response.data);
					
					var batch = { BatchId: batchId, Glasses: String(response.data.GlassesId)};
					$.post('/api/batches/addglasses', batch, function() { });
				});
			});
		}
	});
});