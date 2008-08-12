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
 * Init namespaces
 */
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.SVG) {ORYX.Core.SVG = {};}

/**
 * @classDescription Class for adding text to a shape.
 * 
 */
ORYX.Core.SVG.Label = Clazz.extend({
	
	/**
	 * Constructor
	 * @param options {Object} :
	 * 	textElement
	 * 
	 */
	construct: function(options) {
		arguments.callee.$.construct.apply(this, arguments);
		
		if(!options.textElement) {
			throw "Label: No parameter textElement." 
		} else if (!options.textElement instanceof SVGTextElement) {
			throw "Label: Parameter textElement is not an SVGTextElement."	
		}
		
		this.node = options.textElement;
		this.shapeId = options.shapeId;
		
		this.id;
		
		this.fitToElemId;
		
		this.x;
		this.y;
		this.oldX;
		this.oldY;
		
		this._text;
		this._verticalAlign;
		this._horizontalAlign;
		this._rotate;
		this._fontSize;
		
		this.anchors = [];
		
		this._isChanged = true;

		//if the text element already has an id, don't change it.
		var _id = this.node.getAttributeNS(null, 'id');
		if(_id) {
			this.id = _id;
		}
		
		//initialization	
		
		//set referenced element the text is fit to
		this.fitToElemId = this.node.getAttributeNS(ORYX.CONFIG.NAMESPACE_ORYX, 'fittoelem');
		if(this.fitToElemId)
			this.fitToElemId = this.shapeId + this.fitToElemId;
		
		//set alignment	
		var alignValues = this.node.getAttributeNS(ORYX.CONFIG.NAMESPACE_ORYX, 'align');
		if(alignValues) {
			alignValues = alignValues.replace(/,/g, " ");
			alignValues = alignValues.split(" ");
			alignValues = alignValues.without("");
			
			alignValues.each((function(alignValue) {
				switch (alignValue) {
					case 'top':
					case 'middle':
					case 'bottom':
						if(!this._verticalAlign) {this._verticalAlign = alignValue;}
						break;
					case 'left':
					case 'center':
					case 'right':
						if(!this._horizontalAlign) {this._horizontalAlign = alignValue;}
						break;
				}
			}).bind(this));
		}
		
		//set rotation
		var rotateValue = this.node.getAttributeNS(ORYX.CONFIG.NAMESPACE_ORYX, 'rotate');
		if(rotateValue) {
			try {
				this._rotate = parseFloat(rotateValue);
			} catch (e) {
				this._rotate = 0;
			}
		} else {
			this._rotate = 0;
		}
		
		//anchors
		var anchorAttr = this.node.getAttributeNS(NAMESPACE_ORYX, "anchors");
		if(anchorAttr) {
			anchorAttr = anchorAttr.replace("/,/g", " ");
			this.anchors = anchorAttr.split(" ");
			this.anchors = this.anchors.without("");
		}
		
		//if no alignment defined, set default alignment
		if(!this._verticalAlign) { this._verticalAlign = 'bottom'; }
		if(!this._horizontalAlign) { this._horizontalAlign = 'left'; }
		
		//set line height
		var fsValue = this.node.getAttributeNS(null, 'font-size');
		this._fontSize = parseFloat(fsValue);
		if(!this._fontSize) {
			this._fontSize = ORYX.CONFIG.LABEL_DEFAULT_LINE_HEIGHT;
			this.node.setAttributeNS(null, 'font-size', this._fontSize);
		}
		
		//TODO temporary deactivate events
		//this.node.setAttributeNS(null, 'pointer-events', 'none');

		var xValue = this.node.getAttributeNS(null, 'x');
		if(xValue) {
			this.x = parseFloat(xValue);
			this.oldX = this.x;
		} else {
			//TODO error
		}
		
		var yValue = this.node.getAttributeNS(null, 'y');
		if(yValue) {
			this.y = parseFloat(yValue);
			this.oldY = this.y;
		} else {
			//TODO error
		}
		
		//set initial text
		this.text(this.node.textContent);
		
		//workaround for firefox on mac bug that avoids rendering text fill
		if(navigator.platform.indexOf("Mac") > -1) {
			this.node.setAttributeNS(null, 'stroke', 'black');
			this.node.setAttributeNS(null, 'stroke-width', '0.5px');
			this.node.setAttributeNS(null, 'font-family', 'Skia');
			this.node.setAttributeNS(null, 'letter-spacing', '2px');
		} else {
			this.node.setAttributeNS(null, 'stroke', 'none');
			if (!this.node.getAttributeNS(null, 'font-family')) {
				this.node.setAttributeNS(null, 'font-family', 'Verdana');
			}
		}
	},
	
	/**
	 * Update the SVG text element.
	 */
	update: function() {
		if(this._isChanged || this.x !== this.oldX || this.y !== this.oldY) {
		//if(true) {
		 	this._isChanged = false;	
			
			this.node.setAttributeNS(null, 'x', this.x);
			this.node.setAttributeNS(null, 'y', this.y);
			
			this.node.setAttributeNS(null, 'font-size', this._fontSize);
			//this.node.setAttributeNS(ORYX.CONFIG.NAMESPACE_ORYX, 'align', this._horizontalAlign + " " + this._verticalAlign);
			
			this.oldX = this.x;
			this.oldY = this.y;
			
			//set rotation
			if(this._rotate) {
				this.node.setAttributeNS(null, 'transform', 'rotate(' + this._rotate + ' ' + this.x + ' ' + this.y + ')' );	
			}
			
			var textLines = this._text.split("\n");
			while(textLines.last() == "")
				textLines.remove(textLines.last());
				
			this.node.textContent = "";

			if(this.node.ownerDocument) {
				textLines.each((function(textLine, index) {
					var tspan = this.node.ownerDocument.createElementNS(ORYX.CONFIG.NAMESPACE_SVG, 'tspan');
					tspan.textContent = textLine;
					tspan.setAttributeNS(null, 'x', this.x);
					tspan.setAttributeNS(null, 'y', this.y);

					//append tspan to text node
					this.node.appendChild(tspan);
				}).bind(this));
				
				//Work around for Mozilla bug 293581
				if(this.isVisible) {
					this.node.setAttributeNS(null, 'visibility', 'hidden');
				}

				if(this.fitToElemId)
					window.setTimeout(this._checkFittingToReferencedElem.bind(this), 0);
				else
					window.setTimeout(this._positionText.bind(this), 0);
			}
		}
	},
	
	_checkFittingToReferencedElem: function() {
		var tspans = $A(this.node.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'tspan'));
		
		var newtspans = [];
		
		var refNode = this.node.ownerDocument.getElementById(this.fitToElemId);

		if (refNode) {
			
			var refbb = refNode.getBBox();
			
			var startIndex = 0;
			var lastSeperatorIndex = 0;
			
			for (var j = 0; j < tspans.length; j++) {
				var tspan = tspans[j];
				
				var textLength = tspan.getComputedTextLength();
				
				if(textLength > refbb.width) {

					for (var i = 0; i < tspan.textContent.length; i++) {
						var sslength = tspan.getSubStringLength(startIndex, i - startIndex);

						if(sslength > refbb.width) {
							var newtspan = this.node.ownerDocument.createElementNS(ORYX.CONFIG.NAMESPACE_SVG, 'tspan');
							if(lastSeperatorIndex <= startIndex) {
								newtspan.textContent = tspan.textContent.slice(startIndex, i);
								lastSeperatorIndex = i;
							} else{
								newtspan.textContent = tspan.textContent.slice(startIndex, ++lastSeperatorIndex);
							}
							
							newtspan.setAttributeNS(null, 'x', this.x);
							newtspan.setAttributeNS(null, 'y', this.y);
		
							//insert tspan to text node
							//this.node.insertBefore(newtspan, tspan);
							newtspans.push(newtspan);
							
							startIndex = lastSeperatorIndex;
							
						} else {
							var curChar = tspan.textContent.charAt(i);
							if(curChar == ' ' || 
							   curChar == '-' || 
							   curChar == "." || 
							   curChar == "," ||
							   curChar == ";" ||
							   curChar == ":") {
								lastSeperatorIndex = i;
							}
						}
					}

					tspan.textContent = tspan.textContent.slice(startIndex);
				}
				
				newtspans.push(tspan);
			}
			
			while(this.node.hasChildNodes())
				this.node.removeChild(this.node.childNodes[0]);
				
			while(newtspans.length > 0) {
				this.node.appendChild(newtspans.shift());
			}
		}
		
		window.setTimeout(this._positionText.bind(this), 0);
	},
	
	/**
	 * This is a work around method for Mozilla bug 293581.
	 * Before the method getComputedTextLength works, the text has to be rendered.
	 */
	_positionText: function() {
		try {
			var tspans = this.node.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'tspan');
			
			$A(tspans).each((function(tspan, index){
				//set vertical position
				var dy = 0;
				switch (this._verticalAlign) {
					case 'bottom':
						dy = -(tspans.length - index - 1) * (this._fontSize + ORYX.CONFIG.LABEL_LINE_DISTANCE);
						break;
					case 'middle':
						dy = -(tspans.length / 2.0 - index - 1) * (this._fontSize + ORYX.CONFIG.LABEL_LINE_DISTANCE);
						dy -= ORYX.CONFIG.LABEL_LINE_DISTANCE / 2;
						break;
					case 'top':
						dy = index * (this._fontSize + ORYX.CONFIG.LABEL_LINE_DISTANCE);
						dy += this._fontSize;
						break;
				}
				
				tspan.setAttributeNS(null, 'dy', dy);
				
				//set horizontal position
				var textLength = tspan.getComputedTextLength();
				switch (this._horizontalAlign) {
					case 'left':
						tspan.setAttributeNS(null, 'dx', 0);
						break;
					case 'center':
						tspan.setAttributeNS(null, 'dx', -textLength / 2);
						break;
					case 'right':
						tspan.setAttributeNS(null, 'dx', -textLength);
						break;
				}
			}).bind(this));

		} catch(e) {
			this._isChanged = true;
		}
		
		
		if(this.isVisible) {
			this.node.setAttributeNS(null, 'visibility', 'inherit');
		}				
	},
	
	/**
	 * If no parameter is provided, this method returns the current text.
	 * @param text {String} Optional. Replaces the old text with this one.
	 */
	text: function() {
		switch (arguments.length) {
			case 0:
				return this._text
				break;
			
			case 1:
				var oldText = this._text;
				if(arguments[0]) {
					this._text = arguments[0].toString();
				} else {
					this._text = "";
				}
				if(oldText !== this._text) {
					this._isChanged = true;
				}
				break;
				
			default: 
				//TODO error
				break;
		}
	},
	
	verticalAlign: function() {
		switch(arguments.length) {
			case 0:
				return this._verticalAlign;
			case 1:
				if(['top', 'middle', 'bottom'].member(arguments[0])) {
					var oldValue = this._verticalAlign;
					this._verticalAlign = arguments[0];
					if(this._verticalAlign !== oldValue) {
						this._isChanged = true;
					}
				}
				break;
				
			default:
				//TODO error
				break;
		}
	},
	
	horizontalAlign: function() {
		switch(arguments.length) {
			case 0:
				return this._horizontalAlign;
			case 1:
				if(['left', 'center', 'right'].member(arguments[0])) {
					var oldValue = this._horizontalAlign;
					this._horizontalAlign = arguments[0];
					if(this._horizontalAlign !== oldValue) {
						this._isChanged = true;
					}	
				}
				break;
				
			default:
				//TODO error
				break;
		}
	},
	
	fontSize: function() {
		switch(arguments.length) {
			case 0:
				return this._fontSize;
			case 1:
				var value = parseFloat(arguments[0]);
				if(value) {
					if(arguments[0] !== this._fontSize) {
						this._fontSize = arguments[0];
						this._isChanged = true;
					}
				}
				break;
				
			default:
				//TODO error
				break;
		}
	},
	
	hide: function() {
		
	},
	
	show: function() {
		
	},
	
	toString: function() { return "Label " + this.id }
 });