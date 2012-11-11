define([ 'jquery', 'underscore', 'backbone', 'text!templates/home.html'],
function($, _, Backbone, template) {
	return Backbone.View.extend({
		el : $('#body'),
		render : function() {
			this.$el.empty();
			var compiledTemplate = _.template(template, {});
			this.$el.append(compiledTemplate).hide().fadeIn();
			
			$('.home_feature_icon').hover(function() {
				$(this).parent().find('h2').stop().animate({ paddingTop : '40px' }, 'fast');
			    $(this).stop().animate({ fontSize : '220px' }, 'fast');
			}, function() {
				$(this).parent().find('h2').stop().animate({ paddingTop : '50px' }, 'fast');
			    $(this).stop().animate({ fontSize : '180px' }, 'fast');
			});
		}
	});
});