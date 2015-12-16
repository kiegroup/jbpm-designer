
/**
 * @namespace Oryx name space for different utility methods
 * @name ORYX.Utils
*/

ORYX.Utils = {
    /**
     * General helper method for parsing a param out of current location url
     * @example
     * // Current url in Browser => "http://oryx.org?param=value"
     * ORYX.Utils.getParamFromUrl("param") // => "value" 
     * @param {Object} name
     */
    getParamFromUrl: function(name){
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.href);
        if (results == null) {
            return null;
        }
        else {
            return results[1];
        }
    },
	
	adjustGradient: function(gradient, reference){
		
		if (ORYX.CONFIG.DISABLE_GRADIENT && gradient){
		
			var col = reference.getAttributeNS(null, "stop-color") || "#ffffff";
			
			$A(gradient.getElementsByTagName("stop")).each(function(stop){
				if (stop == reference){ return; }
				stop.setAttributeNS(null, "stop-color", col);
			})
		}
	},

    getDialogSize : function (defaultHeight, defaultWidth) {
        var docHeight = document.documentElement.clientHeight;
        var docWidth = document.documentElement.clientWidth;
        var winHeight = Math.min(defaultHeight, docHeight * 2/3);
        var winWidth = Math.min(defaultWidth, docWidth * 2/3);
        var dialogSize = {height: winHeight, width: winWidth};
        return dialogSize;
    }

}
