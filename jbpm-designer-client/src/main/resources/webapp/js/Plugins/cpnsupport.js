if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.CpnSupport = Clazz.extend({

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) 
	{
		this.facade = facade;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.resetTokenPosition.bind(this));
	},
	
	resetTokenPosition: function()
	{
		// Get selected places		
		var allplaces = this.facade.getSelection().findAll(function(selectedItem) {
			return (selectedItem.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Place");
		});
		
		if (allplaces.length > 0)
		{
			allplaces.each(function(place) {
				
				var placeBounds = place.absoluteBounds();
				var placeCenter = placeBounds.center();
				
				// Calculate radius in order to check if a token is in the place
				var radiusY = placeCenter.y - placeBounds.upperLeft().y;
				var radiusX = placeCenter.x - placeBounds.upperLeft().x;
				var radius = Math.min(radiusY,radiusX);
				var c = radius / 2;
				
				// Get all tokens inside the place 
				var alltokens = place.getChildNodes(false).findAll(function(child) {
					return (child.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Token");
				});
				
				if (alltokens.length > 0)
				{
					var i = 0;
					var x = 0;
					var y = 0;
					
					alltokens.each(function(token) {
						var tokenBounds = token.absoluteBounds();
						var tokenCenter = tokenBounds.center();
						
						// Calculate the distance between token and center of the place
						var diffX = placeCenter.x - tokenCenter.x;
						var diffY = placeCenter.y - tokenCenter.y;
						var distanceToPlaceCenter= diffX*diffX + diffY*diffY; // take care it's squared
						
						// Check if the token is in the place
						if (radius*radius <= distanceToPlaceCenter)
						{	// if the token is out of the place, calculate the position for the token
							// the token are positioned in circle which is in the place
							y = Math.round(Math.sin((Math.PI / 6) * i) * c);
							x = Math.round(Math.cos((Math.PI / 6) * i) * c);
							// take care centerMoveTo is referred to the position in the selected place (not absolute) 
							token.bounds.centerMoveTo(place.bounds.width() / 2  + x, place.bounds.height() / 2 + y);
							token.update();
							i = i + 1;
						}
					});					
				}
			});

		}			
		this.facade.getCanvas().update();
	}	
});

