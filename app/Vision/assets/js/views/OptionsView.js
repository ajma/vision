define([ 'jquery', 'underscore', 'backbone', 'vision', 'text!templates/options.html'],
function($, _, Backbone, vision, template) {
	return Backbone.View.extend({
		el : $('#body'),
		initialize : function() {
			this.$el.empty();
		},
		render : function() {
			this.$el.append(template).hide().fadeIn();
			
			$('ul.nav .active').removeClass('active');
			$('#nav_options').addClass('active');
			
			var importLog = function(msg) {
			    $('#importLog').append('<div>' + msg + '</div>');
			}
			
			$('#importBtn').click(function() {
			    $('#importLog').empty();
			    importLog('start importing');
			    var lines = $.trim($('#importText').val()).split('\n');
			    var fields = ['Group','Number','OD_Spherical','OD_Cylindrical','OD_Axis','OD_Add','OD_Blind','OS_Spherical','OS_Cylindrical','OS_Axis','OS_Add','OS_Blind','AddedDate','RemovedDate'];
			    
			    var processNextLine = function() {
			        if(lines.length === 0) {
			            importLog('done importing');
			        } else {
			            var line = lines.shift();
			            if(line.charAt(0) === '#' || line === '') {
			                processNextLine();
			            } else {
	                        var vals = line.split(',');
	                        if(vals.length === fields.length) {
	                            var glasses = {};
	                            $.each(fields, function(index, field) {
	                                glasses[field] = $.trim(vals[index]);
	                            });
	                            vision.post('/api/glasses/import', glasses, function() {
	                                    importLog('imported ' + glasses.Group + '/' + glasses.Number);
	                                    processNextLine();
	                            });
	                        } else {
	                            importLog(line + ' not the right format. only contains ' + vals.length + ' fields');
	                            processNextLine();
	                        }
	                    }
			        }
			    };
			    processNextLine();
			});
		}
	});
});