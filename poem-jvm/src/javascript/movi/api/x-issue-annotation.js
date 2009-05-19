/**
 * Copyright (c) 2009
 * Matthias Kunze
 *
 * This software is provided under the same terms as the MOVI API package.
 */

MOVI.namespace("util");

(function() {
	
	MOVI.util.setStyle = function(element, style) {
		for (var i in style) {
			element.setStyle(i, style[i]);
		}
	}
	
    MOVI.util.XIssueAnnotation = function(marker, index) {
		if (!marker) {
			throw new TypeError("No marker given", marker);
		}
		
		switch(index) {
			case "low":    index = 0; break;
			case "medium": index = 1; break;
			case "high":   index = 2; break;
			default: throw new TypeError("Index out of range (low, medium, high)")
		}
		
		// create host element
		this._host = document.createElement("div");
		MOVI.util.XIssueAnnotation.superclass.constructor.call(this, this._host, {
			"className": "x-issue-annotation"
		});
		
		// create child elements
		this._draw(index);
		
		// append annotation to first marker rect
		for(key in marker.shapeRects) {
			if(!YAHOO.lang.hasOwnProperty(marker.shapeRects, key)) continue;
			
			marker.shapeRects[key].appendChild(this);
			break;
		}
	}

	MOVI.extend(MOVI.util.XIssueAnnotation, YAHOO.util.Element, {
		
		show: function() {
			this.setStyle("display", "block");
		},
		
		hide: function() {
			this.setStyle("display", "none");
		},
		
		update: function(num) {
			this._content.set("innerHTML", num);
			return this;
		},
		
		close: function() {
			this.hide();
			this.set("innerHTML", "");
			return null;
		},
		
		_draw: function(index) {
			
			var size = 30; // size of one sprite element
			var sprite = MOVI.util.XIssueAnnotation.sprite || "../api/img/x-issue-sprite.png";
			
			MOVI.util.setStyle(this, {
				"position": "absolute",
				"left": -parseInt(size/3)+"px",
				"bottom": -parseInt(size/3)+"px",
				"width": size+"px",
				"height": size+"px",
				"overflow": "hidden"
			});
			
			var img = new YAHOO.util.Element(document.createElement("img"), {
				src: sprite
			});
			MOVI.util.setStyle(img, {
				position: "absolute",
				left: -(index*size)+"px"
			});
			
			this._content = new YAHOO.util.Element(document.createElement("div"));
			MOVI.util.setStyle(this._content, {
				"position": "absolute",
				"top": parseInt(size/4)+"px",
				"left": "0px",
				"width": size+"px",
				"text-align":"center",
				"font-size": parseInt(size/2)+"px"
			});

			this.appendChild(img);
			this.appendChild(this._content);
		}
		
	});
	
})();