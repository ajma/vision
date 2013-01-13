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
			var batchSize = 40;
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
				$('#progressBar').width((count*100/batchSize) + '%');
			};
			
			var logGlasses = function(glasses) {
				var logHtml = $(tpl(glasses));
				$('#log').prepend(logHtml);
				logHtml.fadeIn('slow');
			};
			
			var checkIfBatchDone = function() {
				if(count == batchSize) {
					$('#rxformDiv').slideUp(function() {
						$('#finished').slideDown();
					});
				}
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
			$('#restart').click(function() {
				alert('restarting');
				$.getJSON('/api/batches/new', function(response) {
					batchId = response.data;
					$('#newBatchId').text('#' + batchId);
					$('#batchId').text(batchId);
					$('#newBatchModal').modal();
					$('#addGlassesForm').slideDown();
					count = 0;
					setProgress();
					$('#rxformDiv').show();
					$('#finished').hide();
					$('#log').empty();
				})
			});

			$('#loadBatchFeatureIcon').click(function() { $('#loadBatchModal').modal(); });
			$('#loadBatchModal').on('shown', function() { $('#loadBatchId').focus(); });
			
			$('#loadBatchButton').click(function() {
				hideFeatures();
				batchId = $('#loadBatchId').val();
				$.getJSON('/api/batches/get/' + batchId, function(response) {
					var glasses = (response.data.Glasses === '' ? [] : response.data.Glasses.trim().split(' '));
					count = glasses.length;
					setProgress();
					$('#batchId').text(batchId);
					$('#addGlassesForm').slideDown();
					
					if(glasses.length > 0) {
						console.log('Glasses to load: ' + glasses.length + '(IDs:' + response.data.Glasses.trim() + ')');
						// load glasses one-by-one. Sqlite seems to fail badly if we try to request all 40 at the same time
						var processNext = function() {
							var glassesId = glasses.shift();
							console.log('Loading GlassesID:' + glassesId);
							$.getJSON('/api/glasses/get/' + glassesId, function(response) {
								logGlasses(response.data);
								if(glasses.length > 0)
									processNext();
							});
						};
						processNext();
					}
					
					checkIfBatchDone();
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

					checkIfBatchDone();
				});
			});
		}
	});
});