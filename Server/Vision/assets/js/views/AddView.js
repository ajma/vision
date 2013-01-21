define([ 'jquery', 'underscore', 'backbone', 'vision', 'rxform', 'text!templates/add.html', 'text!templates/rxform.html'],
function($, _, Backbone, vision, rxform, addTemplate, rxFormTemplate) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(addTemplate).hide().fadeIn();
			
			$('#rxform').append(rxFormTemplate);
			$('#editModalRxForm').append(rxFormTemplate);
			
			$('#rxform, #editModalRxForm').rxForm(
					{},
					{ min: -20, max: 0 },
					{ min: 0, max: 180, littleStep: 5, bigStep: 10, beforeDecimal: 3, afterDecimal: 0, autoDecimal: 999 },
					{ min: 0, max: 20 });
			$('#rxform').rxTips();

			var batchSize = 40;
			var count = 0;
			var batchId = 0;
			var tpl = _.template($('#glasses-tpl').text());

			$('ul.nav .active').removeClass('active');
			$('#nav_add').addClass('active');

			var hideFeatures = function() {
				$('#add_features').fadeOut();
			};

			var setBatchId = function() {
				$('#newBatchId').text('#' + batchId);
				$('#batchId').text(batchId);
				$('#addGlassesForm').slideDown();
			};

			var setProgress = function() {
				$('#progressCount').text(count);
				$('#progressBar').width((count*100/batchSize) + '%');
			};

			var logGlasses = function(glasses) {
				var logHtml = $(tpl(glasses));
				$('#log').prepend(logHtml);
				logHtml.data(glasses);
				logHtml.fadeIn('slow');
				count++;
				setProgress();
			};

			var checkIfBatchDone = function() {
				if(count >= batchSize) {
					$('#rxformDiv').slideUp(function() {
						$('#finished').slideDown();
					});
				}
			};

			var createNewBatch = function() {
				$('#creating').show();
				$('#created').hide();
				$('#newBatchModal').modal();
				vision.getJSON('/api/batches/new', function(data) {
					$('#creating').hide();
					$('#created').show();
					hideFeatures();
					batchId = data;
					setBatchId();
					count = 0;
					setProgress();
					$('#rxformDiv').show();
					$('#finished').hide();
					$('#log').empty();
				})
			};
			$('#newBatchFeatureIcon').click(createNewBatch);
			$('#restart').click(createNewBatch);

			$('#loadBatchFeatureIcon').click(function() { $('#loadBatchModal').modal(); });
			$('#loadBatchModal').on('shown', function() { $('#loadBatchId').focus(); });

			$('#loadBatchId').keypress(function(e) { if(e.which == 13) $('#loadBatchButton').click(); });
			$('#loadBatchButton').click(function() {
				$('#loadingBatch').fadeIn();
				$('#loadBatchAlert').hide();
				batchId = $('#loadBatchId').val();
				vision.getJSON('/api/batches/get/' + batchId, function(data) {
					$('#loadingBatch').hide();
					if(data == null) {
						$('#loadBatchAlert').slideDown();
						return;
					}
					
					hideFeatures();
					var glasses = (data.Glasses === '' ? [] : data.Glasses.trim().split(' '));
					count = 0;
					setBatchId();

					if(glasses.length > 0) {
						console.log('Glasses to load: ' + glasses.length + '(IDs:' + data.Glasses.trim() + ')');
						// load glasses one-by-one. Sqlite seems to fail badly if we try to request all 40 at the same time
						var processNext = function() {
							var glassesId = glasses.shift();
							console.log('Loading GlassesID:' + glassesId);
							vision.getJSON('/api/glasses/get/' + glassesId, function(data) {
								logGlasses(data);
								if(glasses.length > 0)
									processNext();
							});
						};
						processNext();
					}

					checkIfBatchDone();
					$('#loadBatchModal').modal('hide');
				});
			});

			$('#addButton').click(function() {
				var newGlasses = $('#rxform').serialize();
				$('#rxform input').val('');
				$('#rxform input[type="checkbox"]').prop('checked', false);
				console.log(newGlasses);
				vision.post('/api/glasses/add', newGlasses, function(data) {
					logGlasses(data);

					var batch = { BatchId: batchId, Glasses: String(data.GlassesId)};
					vision.post('/api/batches/addglasses', batch, function() { });

					checkIfBatchDone();
				});
			});

			var editingDiv;
			$('#log').on('click', '.editGlasses', function(event) {
				event.preventDefault();

				var rxform = $('#editModal').find('.rx_form');
				editingDiv = $(this).parents('.glasses');
				var glasses = $(this).parents('.glasses').data(); 

				$('#editModalCallNum').text(glasses.Group + '/' + glasses.Number);
				rxform.find('input').each(function() {
					var input = $(this)
					input.val(glasses[input.attr('name')]).change();
				});

				$('#editModal').modal();
				$('#editModal').data(glasses);
			});

			$('#editModalSave').click(function() {
				var originalGlasses = $('#editModal').data();
				var updateGlasses = $('#editModalRxForm').serialize() + 
					"&GlassesId=" + originalGlasses.GlassesId;
				console.log(updateGlasses);
				vision.post('/api/glasses/update', updateGlasses, function(data) {
					var logHtml = $(tpl(data));
					editingDiv.replaceWith(logHtml);
					logHtml.show();
					logHtml.data(data);
				});
			});

		}
	});
});