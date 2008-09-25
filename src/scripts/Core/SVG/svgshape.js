/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

/**
 *
 * Config variables
 */
NAMESPACE_ORYX = "http://www.b3mn.org/oryx";
NAMESPACE_SVG = "http://www.w3.org/2000/svg/";

/**
 * @classDescription This class wraps the manipulation of a SVG basic shape or a path.
 * @namespace ORYX.Core.SVG
 * uses Inheritance (Clazz)
 * uses Prototype 1.5.0
 * uses PathParser by Kevin Lindsey (http://kevlindev.com/)
 * uses MinMaxPathHandler
 * uses EditPathHandler
 *
 */

//init package
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.SVG) {ORYX.Core.SVG = {};}

ORYX.Core.SVG.SVGShape = Clazz.extend({

	/**
	 * Constructor
	 * @param svgElem {SVGElement} An SVGElement that is a basic shape or a path.
	 */
	construct: function(svgElem) {
		arguments.callee.$.construct.apply(this, arguments);

		this.element = svgElem;
		this.x = undefined;
		this.y = undefined;
		this.width = undefined;
		this.height = undefined;
		this.oldX = undefined;
		this.oldY = undefined;
		this.oldWidth = undefined;
		this.oldHeight = undefined;
		this.isHorizontallyResizable = false;
		this.isVerticallyResizable = false;
		this.anchors = [];
		
		//attributes of path elements of edge objects
		this.allowDockers = true;
		this.resizeMarkerMid = false;

		this.editPathParser = new PathParser();
		this.editPathHandler = new ORYX.Core.SVG.EditPathHandler();
		this.editPathParser.setHandler(this.editPathHandler);

		this.init(); //initialisation of all the properties declared above.
	},

	/**
	 * Initializes the values that are defined in the constructor.
	 */
	init: function() {

		/**initialize position and size*/
		if(ORYX.Editor.checkClassType(this.element, 'SVGRectElement') || ORYX.Editor.checkClassType(this.element, 'SVGImageElement')) {
			var xAttr = this.element.getAttributeNS(null, "x");
			if(xAttr) {
				this.oldX = parseFloat(xAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var yAttr = this.element.getAttributeNS(null, "y");
			if(yAttr) {
				this.oldY = parseFloat(yAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var widthAttr = this.element.getAttributeNS(null, "width");
			if(widthAttr) {
				this.oldWidth = parseFloat(widthAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var heightAttr = this.element.getAttributeNS(null, "height");
			if(heightAttr) {
				this.oldHeight = parseFloat(heightAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGCircleElement')) {
			var cx = undefined;
			var cy = undefined;
			var r = undefined;

			var cxAttr = this.element.getAttributeNS(null, "cx");
			if(cxAttr) {
				cx = parseFloat(cxAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var cyAttr = this.element.getAttributeNS(null, "cy");
			if(cyAttr) {
				cy = parseFloat(cyAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var rAttr = this.element.getAttributeNS(null, "r");
			if(rAttr) {
				r = parseFloat(rAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			this.oldX = cx - r;
			this.oldY = cy - r;
			this.oldWidth = 2*r;
			this.oldHeight = 2*r;

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGEllipseElement')) {
			var cx = undefined;
			var cy = undefined;
			var rx = undefined;
			var ry = undefined;
			var cxAttr = this.element.getAttributeNS(null, "cx");
			if(cxAttr) {
				cx = parseFloat(cxAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var cyAttr = this.element.getAttributeNS(null, "cy");
			if(cyAttr) {
				cy = parseFloat(cyAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var rxAttr = this.element.getAttributeNS(null, "rx");
			if(rxAttr) {
				rx = parseFloat(rxAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var ryAttr = this.element.getAttributeNS(null, "ry");
			if(ryAttr) {
				ry = parseFloat(ryAttr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			this.oldX = cx - rx;
			this.oldY = cy - ry;
			this.oldWidth = 2*rx;
			this.oldHeight = 2*ry;

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGLineElement')) {
			var x1 = undefined;
			var y1 = undefined;
			var x2 = undefined;
			var y2 = undefined;
			var x1Attr = this.element.getAttributeNS(null, "x1");
			if(x1Attr) {
				x1 = parseFloat(x1Attr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var y1Attr = this.element.getAttributeNS(null, "y1");
			if(y1Attr) {
				y1 = parseFloat(y1Attr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var x2Attr = this.element.getAttributeNS(null, "x2");
			if(x2Attr) {
				x2 = parseFloat(x2Attr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			var y2Attr = this.element.getAttributeNS(null, "y2");
			if(y2Attr) {
				y2 = parseFloat(y2Attr);
			} else {
				throw "Missing attribute in element " + this.element;
			}
			this.oldX = Math.min(x1,x2);
			this.oldY = Math.min(y1,y2);
			this.oldWidth = Math.abs(x1-x2);
			this.oldHeight = Math.abs(y1-y2);

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGPolylineElement') || ORYX.Editor.checkClassType(this.element, 'SVGPolygonElement')) {
			var points = this.element.getAttributeNS(null, "points");

			if(points) {
				points = points.replace(/,/g , " ");
				var pointsArray = points.split(" ");
				pointsArray = pointsArray.without("");

				if(pointsArray && pointsArray.length && pointsArray.length > 1) {
					var minX = parseFloat(pointsArray[0]);
					var minY = parseFloat(pointsArray[1]);
					var maxX = parseFloat(pointsArray[0]);
					var maxY = parseFloat(pointsArray[1]);

					for(var i = 0; i < pointsArray.length; i++) {
						minX = Math.min(minX, parseFloat(pointsArray[i]));
						maxX = Math.max(maxX, parseFloat(pointsArray[i]));
						i++;
						minY = Math.min(minY, parseFloat(pointsArray[i]));
						maxY = Math.max(maxY, parseFloat(pointsArray[i]));
					}

					this.oldX = minX;
					this.oldY = minY;
					this.oldWidth = maxX-minX;
					this.oldHeight = maxY-minY;
				} else {
					throw "Missing attribute in element " + this.element;
				}
			} else {
				throw "Missing attribute in element " + this.element;
			}

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGPathElement')) {
			var parser = new PathParser();
			var handler = new ORYX.Core.SVG.MinMaxPathHandler();
			parser.setHandler(handler);
			parser.parsePath(this.element);

			this.oldX = handler.minX;
			this.oldY = handler.minY;
			this.oldWidth = handler.maxX - handler.minX;
			this.oldHeight = handler.maxY - handler.minY;

			delete parser;
			delete handler;
		} else {
			throw "Element is not a shape.";
		}

		/** initialize attributes of oryx namespace */
		//resize
		var resizeAttr = this.element.getAttributeNS(NAMESPACE_ORYX, "resize");
		if(resizeAttr) {
			var values = resizeAttr.split(" ");
			//TODO use reg exp instead
			if(values.include("horizontal")) {
				this.isHorizontallyResizable = true;
			} else {
				this.isHorizontallyResizable = false;
			}
			//TODO use reg exp instead
			if(values.include("vertical")) {
				this.isVerticallyResizable = true;
			} else {
				this.isVerticallyResizable = false;
			}
		} else {
			this.isHorizontallyResizable = false;
			this.isVerticallyResizable = false;
		}

		//anchors
		var anchorAttr = this.element.getAttributeNS(NAMESPACE_ORYX, "anchors");
		if(anchorAttr) {
			anchorAttr = anchorAttr.replace("/,/g", " ");
			this.anchors = anchorAttr.split(" ");
			this.anchors = this.anchors.without("");
		}
		
		//allowDockers and resizeMarkerMid
		if(ORYX.Editor.checkClassType(this.element, 'SVGPathElement')) {
			var allowDockersAttr = this.element.getAttributeNS(NAMESPACE_ORYX, "allowDockers"); 
			if(allowDockersAttr) {
				if(allowDockersAttr.toLowerCase() === "no") {
					this.allowDockers = false; 
				} else {
					this.allowDockers = true;
				}
			}
			
			var resizeMarkerMidAttr = this.element.getAttributeNS(NAMESPACE_ORYX, "resizeMarker-mid"); 
			if(resizeMarkerMidAttr) {
				if(resizeMarkerMidAttr.toLowerCase() === "yes") {
					this.resizeMarkerMid = true; 
				} else {
					this.resizeMarkerMid = false;
				}
			}
		}

		this.x = this.oldX;
		this.y = this.oldY;
		this.width = this.oldWidth;
		this.height = this.oldHeight;
	},

	/**
	 * Writes the changed values into the SVG element.
	 */
	update: function() {
		if(this.x !== this.oldX || this.y !== this.oldY || this.width !== this.oldWidth || this.height !== this.oldHeight) {
			if(ORYX.Editor.checkClassType(this.element, 'SVGRectElement') || ORYX.Editor.checkClassType(this.element, 'SVGImageElement')) {
				this.element.setAttributeNS(null, "x", this.x);
				this.element.setAttributeNS(null, "y", this.y);
			 	this.element.setAttributeNS(null, "width", this.width);
				this.element.setAttributeNS(null, "height", this.height);

			} else if(ORYX.Editor.checkClassType(this.element, 'SVGCircleElement')) {
				//calculate the radius
				var r;
				if(this.width/this.oldWidth <= this.height/this.oldHeight) {
					r = ((this.width > this.height) ? this.width : this.height)/2.0;
				} else {
				 	r = ((this.width < this.height) ? this.width : this.height)/2.0;
				}

				var cx = this.x + this.width/2.0;
				var cy = this.y + this.height/2.0;

				this.element.setAttributeNS(null, "cx", cx);
				this.element.setAttributeNS(null, "cy", cy);
				this.element.setAttributeNS(null, "r", r);

			} else if(ORYX.Editor.checkClassType(this.element, 'SVGEllipseElement')) {
				var rx = this.width/2;
				var ry = this.height/2;
				var cx = this.x + rx;
				var cy = this.y + ry;

				this.element.setAttributeNS(null, "cx", cx);
				this.element.setAttributeNS(null, "cy", cy);
				this.element.setAttributeNS(null, "rx", rx);
				this.element.setAttributeNS(null, "ry", ry);

			} else if(ORYX.Editor.checkClassType(this.element, 'SVGLineElement')) {
				var x2 = this.x + this.width;
				var y2 = this.y + this.height;
				this.element.setAttributeNS(null, "x1", this.x);
				this.element.setAttributeNS(null, "y1", this.y);
				this.element.setAttributeNS(null, "x2", x2);
				this.element.setAttributeNS(null, "y2", y2);

			} else if(ORYX.Editor.checkClassType(this.element, 'SVGPolylineElement') || ORYX.Editor.checkClassType(this.element, 'SVGPolygonElement')) {
				var points = this.element.getAttributeNS(null, "points");
				if(points) {
					points = points.replace(/,/g, " ");
					var pointsArray = points.split(" ");
					pointsArray = pointsArray.without("");

					if(pointsArray && pointsArray.length && pointsArray.length > 1) {

						//TODO what if oldWidth == 0?
						var widthDelta = (this.oldWidth === 0) ? 0 : this.width / this.oldWidth;
					    var heightDelta = (this.oldHeight === 0) ? 0 : this.height / this.oldHeight;

						var updatedPoints = "";
					    for(var i = 0; i < pointsArray.length; i++) {
							var x = (parseFloat(pointsArray[i])-this.oldX)*widthDelta + this.x;
							i++;
							var y = (parseFloat(pointsArray[i])-this.oldY)*heightDelta + this.y;
	    					updatedPoints = updatedPoints.concat(x + " " + y + " ");
					    }
						this.element.setAttributeNS(null, "points", updatedPoints);
					} else {
						//TODO error
					}
				} else {
					//TODO error
				}

			} else if(ORYX.Editor.checkClassType(this.element, 'SVGPathElement')) {

				//calculate scaling delta
				//TODO what if oldWidth == 0?
				var widthDelta = (this.oldWidth === 0) ? 0 : this.width / this.oldWidth;
				var heightDelta = (this.oldHeight === 0) ? 0 : this.height / this.oldHeight;

				//use path parser to edit each point of the path
				this.editPathHandler.init(this.x, this.y, this.oldX, this.oldY, widthDelta, heightDelta);
				this.editPathParser.parsePath(this.element);

				//change d attribute of path
				this.element.setAttributeNS(null, "d", this.editPathHandler.d);
			}

			this.oldX = this.x;
			this.oldY = this.y;
			this.oldWidth = this.width;
			this.oldHeight = this.height;
		}
	},
	
	isPointIncluded: function(pointX, pointY) {
	
		
	
		// Check if there are the right arguments and if the node is visible
		if(!pointX || !pointX || !this.isVisible()) {
			return false;
		}

		if(ORYX.Editor.checkClassType(this.element, 'SVGRectElement') || ORYX.Editor.checkClassType(this.element, 'SVGImageElement')) {
			return (pointX >= this.x && pointX <= this.x + this.width &&
					pointY >= this.y && pointY <= this.y+this.height);

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGCircleElement')) {
			//calculate the radius
			var r;
			if(this.width/this.oldWidth <= this.height/this.oldHeight) {
				r = ((this.width > this.height) ? this.width : this.height)/2.0;
			} else {
			 	r = ((this.width < this.height) ? this.width : this.height)/2.0;
			}

			var cx = this.x + this.width/2.0;
			var cy = this.y + this.height/2.0;
			
			return ORYX.Core.Math.isPointInEllipse({x: pointX, y:pointY}, {x:cx, y:cy, radiusX:r, radiusY:r});

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGEllipseElement')) {
			var rx = this.width/2;
			var ry = this.height/2;
			var cx = this.x + rx;
			var cy = this.y + ry;

			return ORYX.Core.Math.isPointInEllipse({x: pointX, y:pointY}, {x:cx, y:cy, radiusX:rx, radiusY:ry});
			
		} else if(ORYX.Editor.checkClassType(this.element, 'SVGLineElement')) {
			var x2 = this.x + this.width;
			var y2 = this.y + this.height;
			
			return ORYX.Core.Math.isPointInLine({x: pointX, y:pointY}, {point1:{x:this.x, y:this.y},
												  point2:{x:x2, y:y2}});

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGPolylineElement') || ORYX.Editor.checkClassType(this.element, 'SVGPolygonElement')) {
			var points = this.element.getAttributeNS(null, "points");

			if(points) {
				points = points.replace(/,/g , " ");
				var pointsArray = points.split(" ");
				pointsArray = pointsArray.without("");

				var arrayOfPoints = [];
				for(var i = 0; i < pointsArray.length-1; i++) {
					arrayOfPoints.push({x:parseFloat(pointsArray[i]),
										y:parseFloat(pointsArray[++i])});		
				}
				
				return ORYX.Core.Math.isPointInPolygone({x: pointX, y:pointY}, arrayOfPoints);
			} else {
				return false;
			}

		} else if(ORYX.Editor.checkClassType(this.element, 'SVGPathElement')) {
			var parser = new PathParser();
			var handler = new ORYX.Core.SVG.PointsPathHandler();
			parser.setHandler(handler);
			parser.parsePath(this.element);

			return ORYX.Core.Math.isPointInPolygone({x: pointX, y:pointY}, handler.points);

			delete parser;
			delete handler;
		} else {
			return false;
		}
	},

	isVisible: function(elem) {
		
		if (!elem) {
			elem = this.element;
		}
		//console.log(this, elem, elem.getAttributeNS(null, "display"));
		if (ORYX.Editor.checkClassType(elem, 'SVG')) {
			if (ORYX.Editor.checkClassType(elem, 'SVGGElement')) {
				if (elem.className && elem.className.baseVal == "me") 
					return true;
			}

			var attr = elem.getAttributeNS(null, "display");
			if(!attr)
				return this.isVisible(elem.parentNode);
			else if (attr == "none") 
				return false;
			else 
				return true;
		}

		return true;
	},

	toString: function() { return (this.element) ? "SVGShape " + this.element.id : "SVGShape " + this.element;}
 });