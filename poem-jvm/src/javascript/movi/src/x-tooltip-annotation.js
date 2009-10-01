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
	
	var zLevel = 50;
	
    MOVI.util.XTooltipAnnotation = function(marker, size, onclose, scope) {
		if (!marker) {
			throw new TypeError("No marker given", marker);
		}
		
		if (!size || !size.width || !size.height) {
			throw new TypeError("Invalid size given. Invalid object or height/width=0")
		}
		
		if (onclose instanceof Function) {
			this._onclose = {fn: onclose, sc: scope};
		}
		
		// create host element
		this._host = document.createElement("div");
		MOVI.util.XTooltipAnnotation.superclass.constructor.call(this, this._host, {
			"className": "x-tooltip-annotation"
		});
		
		var first_shape = marker.getShapes()[0];
		var first_rect = null;
		for(key in marker.shapeRects) {
			first_rect = marker.shapeRects[key];
			break;
		}
		
		var shape_bounds = first_shape.getAbsBounds();
		
		var canvas = marker.getShapes()[0].getCanvas();
		var canvas_bounds = {
			upperLeft: {
				x:0,
				y:0
			},
			lowerRight: {}
		}
		if (null != (h = canvas.getStyle("height").match(/(\d+)px/)) &&
		    null != (w = canvas.getStyle("width").match(/(\d+)px/)))
		{
			canvas_bounds.lowerRight = {
				x: parseInt(w[1]), 
				y: parseInt(h[1])
			}
		}
		else {
			throw "Failed to calculate canvas height"
		}

		var marker_bounds = first_shape.getAbsBounds();
		var tooltip_pos = {}

		if (marker_bounds.upperLeft.x + size.width > canvas_bounds.lowerRight.x && size.width < canvas_bounds.lowerRight.x) {
			tooltip_pos.right = shape_bounds.lowerRight.x - shape_bounds.upperLeft.x - 60;
		} else {
			tooltip_pos.left = shape_bounds.lowerRight.x - shape_bounds.upperLeft.x - 60;
		}
		
		if (marker_bounds.upperLeft.y + size.height > canvas_bounds.lowerRight.y && size.height < canvas_bounds.lowerRight.y ) {
			tooltip_pos.bottom = shape_bounds.lowerRight.y - shape_bounds.upperLeft.y - 20;
		} else {
			tooltip_pos.top = shape_bounds.lowerRight.y - shape_bounds.upperLeft.y - 20;
		}
		
		// create child elements
		this._draw(tooltip_pos, size);
		
		first_rect.appendChild(this);
		
		this._marker = marker;
		
	}

	MOVI.extend(MOVI.util.XTooltipAnnotation, YAHOO.util.Element, {
		
		show: function() {
			if (this.isShown()){ return; }
			
			this.setStyle("display", "block");
			this.fadeIn();
			this.bringToFront();
		},
		
		hide: function() {
			this.isHiding = true;
			var me = this;
			this.fadeOut(function(){
				me.setStyle("display", "none");
				delete me.isHiding;
				var shapes = me._marker.getShapes()
				if (shapes.length <= 0) {return}
				for(key in shapes) {
					if (shapes[key] instanceof Function || !shapes[key]){continue}
					shapes[key].get("element").style.zIndex  = 10;
				}
			
			});
		},

		toggle: function() {
			this.isShown() ? this.hide() : this.show();
		},
		
		isShown: function(){
			return !this.isHiding && this.getStyle("display") == "block";
		},
		
		fadeIn: function(){
			
			if(YAHOO.env.ua.ie){ return }
			
			this.setStyle("opacity", 0);	
			var anim = new YAHOO.util.ColorAnim(this, { opacity: { to: 1 } }, 0.4, YAHOO.util.Easing.easeOut);
			anim.animate();			
		},
		
		fadeOut: function(fn){
			
			if(YAHOO.env.ua.ie){ 
				if (fn instanceof Function) { fn() }
				return 
			}
			
			var anim = new YAHOO.util.ColorAnim(this, { opacity: { to: 0 } }, 0.4, YAHOO.util.Easing.easeOut);
			anim.animate();
			if (fn instanceof Function){
				anim.onComplete.subscribe(fn) 
			}
		},
				
		update: function(html) {
			this._content.set("innerHTML", "");
			
			if ("xml" == typeof(html)) { 
				this._content.set("innerHTML", html.toXMLString()); 
				return this;
			}

			if ("string" == typeof(html)) { 
				this._content.set("innerHTML", html);
				return this;
			};

			this._content.appendChild(html);
			return this;
		},
		
		close: function() {
			
			if (this._onclose && this._onclose.fn() === false) {
				return
			}
			
			this.hide();
			this.set("innerHTML", "");
			return null;
		},
		
		bringToFront: function() {
			
			var shapes = this._marker.getShapes()
			
			if (shapes.length <= 0) {return}
			var elements = this._marker.canvas.getElementsByClassName("movi-node");
			for(key in elements) {
				if (elements[key] instanceof Function || !elements[key]){continue}
				elements[key].style.zIndex  = 20;
			}
			// set the z-index of parent marker to maximum+1
			for(key in shapes) {
				if (shapes[key] instanceof Function || !shapes[key]){continue}
				var node = shapes[key].get("element");
				while(node && node !== this._marker.canvas.get("element")){
					node.style.zIndex  = 100;
					node = node.parentNode;
				}
			} 
		},
		
		_draw: function(pos, size) {
			
			var sprite = MOVI.util.XTooltipAnnotation.sprite || "/talkabout/lib/movi/src/img/x-tooltip-sprite.png";
			var margin = 20+20;     // margin, corner radius
			var vspace = 0;     // additional height through arrow
			var vsize = 50+20;      // vertical size of arrow
			var hsize = 60;      // horizontal size of arrow
			var maxsize = 1000+2*20;  // maxsize, w/o arrow
			var arrowpos = "";   // ["ne", "nw", "se", "sw"]; 
			                     // position of the arrow, needs to be calculated through viewport restriction
			var topspace = 0;
			var bottomspace = 0;

			if (undefined != pos.top) { arrowpos += "n" }
			else { arrowpos += "s" }
			if (undefined != pos.left) { arrowpos += "e"}
			else { arrowpos += "w" }

			// Cache the value
			this.arrowpos = arrowpos;

			// basic styling and ensure that size is within bounds
			var bstyle = {
				position: "absolute",
				width: (Math.min(Math.max(size.width, hsize), maxsize - 2*margin) + 2*margin) + "px",
				height: (Math.min(size.height, maxsize)  + 2*margin + vspace) +"px"
			}
			
			for (var i in pos) {
				bstyle[i] = pos[i]+"px";
			}

			if (arrowpos.match(/n/)) {
				topspace = vspace;
			}
			else {
				bottomspace = vspace;
			}

			var sprite_map = {
				tl: { // northeast corner
					div: {
						top: topspace,
						left: 0,
						width: margin,
						height: margin
					},
					img: {
						top: 0,
						left: 0,
						width: margin,
						height: margin
					}
				},

				t: { // north bar
					div: {
						top: topspace,
						left: ("ne" == arrowpos ? hsize : 0) + margin,
						height: margin,
						right: ("nw" == arrowpos ? hsize : 0) + margin
					},
					img: {
						top: 0,
						left: - margin,
						right: - margin,
						height: margin
					}
				},

				tr: { // northwest corner
					div: {
						top: topspace,
						right: 0,
						width: margin,
						height: margin
					},
					img: {
						top: 0,
						right: 0,
						width: margin,
						height: margin
					}
				},

				r: { // west bar 
					div: {
						right: 0,
						top: topspace + margin,
						width: margin,
						bottom: bottomspace + margin
					},
					img: {
						right: 0,
						top: -margin,
						width: margin,
						bottom: margin + vsize
					}
				},

				br: { // southwest corner
					div: {
						right: 0,
						bottom: bottomspace,
						height: margin,
						width: margin
					},
					img: {
						right: 0,
						bottom: -vsize,
						height: margin,
						width: margin
					}
				},

				b: { // south bar
					div: {
						right: ("sw" == arrowpos ? hsize : 0) + margin,
						bottom: bottomspace,
						left: ("se" == arrowpos ? hsize : 0) + margin,
						height: margin
					},
					img: {
						bottom: - vsize,
						right: - margin,
						left: - margin,
						height: margin
					}
				},

				bl: { // southeast corner
					div: {
						left: 0,
						bottom: bottomspace,
						width: margin,
						height: margin
					},
					img: {
						left: 0,
						bottom: - vsize,
						height: margin,
						width: margin
					}
				},

				l: { // east bar
					div: {
						left: 0,
						width: margin,
						top: topspace + margin,
						bottom: bottomspace + margin
					},

					img: {
						left: 0,
						top: -margin,
						width: margin,
						bottom: margin + vsize
					}
				},

				c: { // center fill
					div: {
						left: margin,
						top: topspace + margin,
						right: margin,
						bottom: bottomspace + margin
					},
					img: {
						left: -margin,
						top:  -margin,
						right: margin,
						bottom: vsize + margin
					}
				}
			}

			// add the arrow
			switch(arrowpos) {
				case "ne": 
					sprite_map.arrow = {
						div: {
							top: -30,
							left: margin, 
							height: vsize,
							width: hsize
						},
						img: {
							top: -maxsize,
							left: -3*hsize,
							width: hsize,
							height: vsize
						}
					}
					break;
				case "nw": 
					sprite_map.arrow = {
						div: {
							top: -30,
							right: margin, 
							height: vsize,
							width: hsize
						},
						img: {
							top: -maxsize,
							left: -2*hsize,
							width: hsize,
							height: vsize
						}
					}
					break;
				case "sw": 
					sprite_map.arrow = {
						div: {
							bottom: -30,
							right: margin, 
							height: vsize,
							width: hsize
						},
						img: {
							top: -maxsize,
							left: -1*hsize,
							width: hsize,
							height: vsize
						}
					}
					break;
				case "se": 
					sprite_map.arrow = {
						div: {
							bottom: -30,
							left: margin, 
							height: vsize,
							width: hsize
						},
						img: {
							top: -maxsize,
							left: 0*hsize,
							width: hsize,
							height: vsize
						}
					}
					break;
			}

			// style the balloon
			for (var i in bstyle) {
				this.setStyle(i, bstyle[i]);
			}

			// create children from sprites
			for (var i in sprite_map) {
				var item = sprite_map[i];

				var el = document.createElement("div");
				var div = new YAHOO.util.Element(el, {
					"className": i
				});
				div.setStyle("position", "absolute");
				div.setStyle("overflow", "hidden");
				for (var p in item.div) {
					div.setStyle(p, typeof item.div[p] == "number" ? item.div[p]+"px" : item.div[p]);
				};

				if (i == "c") {
					this._content = new YAHOO.util.Element(el.cloneNode(true));
					this._content.setStyle("overflow", "auto");
				}
				var img = new YAHOO.util.Element(document.createElement("img"), {
					"src": sprite
				});
				img.setStyle("position", "absolute");
				for (var p in item.img) {
					// don't set width and height
					if (["width", "height"].indexOf(p) == -1) {
						img.setStyle(p,  typeof item.img[p] == "number" ? item.img[p]+"px" : item.img[p]);
					}
					
				}

				div.appendChild(img);
				this.appendChild(div);
			}
			
			// create and register close btn
			var btn = new YAHOO.util.Element(document.createElement("div"));
			MOVI.util.setStyle(btn, {
				"position": "absolute",
				"right": parseInt(margin/2)+"px",
				"top": parseInt(margin/2+topspace) + "px",
				"width": parseInt(margin/2)+"px",
				"height": parseInt(margin/2)+"px",
				"overflow": "hidden",
				"cursor": "pointer"
			})
			
			btn.addClass("x-tool-close")
			btn.addListener("click", function(event, annotation) {
				annotation.close();
				event.stopPropagation();
			}, this, this);
			
			var img = new YAHOO.util.Element(document.createElement("img"), {
				"src": sprite
			});
			MOVI.util.setStyle(img, {
				"position": "absolute",
				"right": "0px",
				"bottom": "0px"
			});
			
			btn.appendChild(img);
			
			this.appendChild(this._content);
			this.appendChild(btn);
			
			this.addClass("arrowpos-"+arrowpos);
		}
		
	});
	
})();