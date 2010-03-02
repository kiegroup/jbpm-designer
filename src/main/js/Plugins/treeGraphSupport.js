
/**
 * Copyright (c) 2008
 * Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.TreeGraphSupport = Clazz.extend({

    facade: undefined,
    
	/**
	 * 
	 * @param {Object} facade
	 */
    construct: function(facade){
        
		// Save the facade
		this.facade = facade;
        
		// Offer new functionality
        this.facade.offer({
            'name'				: ORYX.I18N.TreeGraphSupport.syntaxCheckName,
            'functionality'		: this.syntaxCheck.bind(this),
            'group'				: ORYX.I18N.TreeGraphSupport.group,
            'icon'				: ORYX.PATH + "images/checker_syntax.png",
            'description'		: ORYX.I18N.TreeGraphSupport.syntaxCheckDesc,
            'index'				: 1,
            'minShape'			: 0,
            'maxShape'			: 0
        });
        
    },
    
	/**
	 * 
	 */
    syntaxCheck: function() {
		this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_HIDE,
			id: 			"treegraph",
		});
		
		
         // Send the request to the server.
        new Ajax.Request(ORYX.CONFIG.TREEGRAPH_SUPPORT, {
            method: 'POST',
            asynchronous: false,
            parameters: {
                data: this.facade.getERDF()
            },
            onSuccess: function(request){
            	var resp = request.responseText.evalJSON();

				if (resp instanceof Array ) {
					if (resp.length > 0) {
					
						// Get all Valid ResourceIDs and collect all shapes
						resp.each(function( value ){ 
						
							var sh = this.facade.getCanvas().getChildShapeByResourceId( value );

							if( sh ){
								
								this.highlightShape(sh);
								
							}
						}.bind(this));
					}
				}
	            Ext.Msg.show({
	            	title	: ORYX.I18N.Oryx.title,
	            	msg		: request.responseText,
	            	icon		: Ext.MessageBox.INFO
	            });
            }.bind(this),
            	

                        
			onFailure: function(request){
            	Ext.Msg.show({
				   title	: ORYX.I18N.Oryx.title,
				   msg		: 'An error occurs while sending request!',
				   icon		: Ext.MessageBox.WARNING
				});
            }
        });

    },
    
	highlightShape: function(shape){
		// Creates overlay for an enabled shape
		// display is beeing ignored
		if(!(shape instanceof ORYX.Core.Shape)) return;
		
		var attr;
		if(shape instanceof ORYX.Core.Edge) {
			attr = {stroke: "red"};
		}
		else {
			attr = {fill: "red", stroke:"black", "stroke-width": 2};
		}
		
		

											
		this.facade.raiseEvent({
				type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
				id: 			"treegraph",
				shapes: 		[shape],
				attributes: 	attr,
			});
	}
});
