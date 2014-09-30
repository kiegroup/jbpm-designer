/**
 * Copyright (c) 2008
 * Zhen Peng
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.BPELLayouting = Clazz.extend({

	facade: undefined,
	
	isEnabled : undefined,
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.isEnabled = true;
		
		this.facade.offer({
			'name':ORYX.I18N.BPELSupport.enable,
			'functionality': this.enableBpelLayout.bind(this),
			'group': ORYX.I18N.BPELLayout.group,
			'icon': ORYX.BASE_FILE_PATH + "images/bpel_layout_enable.png",
			'description': ORYX.I18N.BPELLayout.enDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){ return !(this.isEnabled)}.bind(this)
		});
		
		this.facade.offer({
			'name':ORYX.I18N.BPELSupport.disable,
			'functionality': this.disableBpelLayout.bind(this),
			'group': ORYX.I18N.BPELLayout.group,
			'icon': ORYX.BASE_FILE_PATH + "images/bpel_layout_disable.png",
			'description': ORYX.I18N.BPELLayout.disDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){ return this.isEnabled}.bind(this)
		});
	
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL, this.handleLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL, this.handleLayoutVerticalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL, this.handleLayoutHorizontalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD, this.handleSingleChildLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE, this.handleAutoResizeLayoutEvent.bind(this));
	},
	
	/**************************** plug-in control ****************************/
	
	disableBpelLayout : function(){
		
		this.isEnabled = false;
	},
	
	enableBpelLayout : function(){
		
		this.isEnabled = true;
		
		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_ENABLE,text: 'Auto Layouting...'});
		
		//adjust all immediate child nodes(grand-children are adjusted recursively)
		nodes = this.facade.getCanvas().getChildNodes();
		for (var i = 0; i < nodes.size(); i++) {
			node = nodes[i];
			if (node.getStencil().id() == node.getStencil().namespace() + "process"){
				this._adjust_node(node);
			}
		}
		
		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
	},
	
	_adjust_node : function (node){
		
		// handle children first
		// that means, the innermost children should be at first arranged,
		var nodes = node.getChildNodes();
		for (var i = 0; i < nodes.size(); i++) {
			this._adjust_node(nodes[i]);
		};
		
		// handle the current node
		this._handleLayoutEventAdapter (node);
		//alert (node.getStencil().id());
		
	},
	
	_handleLayoutEventAdapter : function(node){
		
		if (node.getStencil().id() == node.getStencil().namespace() + "process"
	 		|| node.getStencil().id() == node.getStencil().namespace() + "invoke" 
	    	|| node.getStencil().id() == node.getStencil().namespace() + "scope"){
	    		
			this._handleLayoutEvent (node);
			
		} else if (node.getStencil().id() == node.getStencil().namespace() + "assign"
			|| node.getStencil().id() == node.getStencil().namespace() + "eventHandlers"
			|| node.getStencil().id() == node.getStencil().namespace() + "faultHandlers"
			|| node.getStencil().id() == node.getStencil().namespace() + "compensationHandler"
			|| node.getStencil().id() == node.getStencil().namespace() + "terminationHandler"){
			
			this._handleLayoutVerticalEvent (node);
			
		} else if  (node.getStencil().id() == node.getStencil().namespace() + "if"
			|| node.getStencil().id() == node.getStencil().namespace() + "sequence"
			|| node.getStencil().id() == node.getStencil().namespace() + "pick"){
			
			this._handleLayoutHorizontalEvent (node);
			
		} else if (node.getStencil().id() == node.getStencil().namespace() + "onMessage"
			|| node.getStencil().id() == node.getStencil().namespace() + "if_branch"
			|| node.getStencil().id() == node.getStencil().namespace() + "else_branch"
			|| node.getStencil().id() == node.getStencil().namespace() + "while"
			|| node.getStencil().id() == node.getStencil().namespace() + "repeatUntil"
			|| node.getStencil().id() == node.getStencil().namespace() + "forEach"
			|| node.getStencil().id() == node.getStencil().namespace() + "onAlarm"
			|| node.getStencil().id() == node.getStencil().namespace() + "onEvent"
			|| node.getStencil().id() == node.getStencil().namespace() + "catch"
			|| node.getStencil().id() == node.getStencil().namespace() + "catchAll"){
			
			this._handleSingleChildLayoutEvent (node);
			
		} else if (node.getStencil().id() == node.getStencil().namespace() + "flow"){
			
			this._handleAutoResizeLayoutEvent (node);
		} else {
			// other shapes cannot contain any children shapes.
			return;
		}
	
	},
	
	
	/***************************** Event Handler *****************************/
	
	handleLayoutEvent: function(event) {
		this._handleLayoutEvent (event.shape);
	},
	
	handleLayoutVerticalEvent: function(event) {
		this._handleLayoutVerticalEvent (event.shape);
	},
	
	handleLayoutHorizontalEvent: function(event) {
		this._handleLayoutHorizontalEvent (event.shape);
	},
	
	handleSingleChildLayoutEvent: function(event) {
		this._handleSingleChildLayoutEvent (event.shape);
	},
	
	handleAutoResizeLayoutEvent: function(event) {
		this._handleAutoResizeLayoutEvent (event.shape);
	},
	
		
	/************************* Auto Layout Processes ****************************/
	
	/**
	 *  realize special BPEL layouting:
	 *  main activity: placed left,
	 *  Handler: placed right.
	 */
	_handleLayoutEvent: function(shape) {
		
		if (this.isEnabled == false) {
			return;
		}
		
     	var elements = shape.getChildShapes(false);
     	
     	// if Autolayout is not required, do nothing.
		if (!this._requiredAutoLayout (shape)){
     		return;
     	};
     	
		// If there are no elements
		if(!elements || elements.length == 0) {
			this._resetBounds(shape);
			this._update(shape);
			return;
		};
		
     	var eventHandlers = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "eventHandlers");
			});
		
		var faultHandlers = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "faultHandlers");
			});
			
		var compensationHandler = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "compensationHandler");
			});	

		var terminationHandler = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "terminationHandler");
			});
			
		var otherElements = elements.findAll(function(node){
				return (node !== eventHandlers && node !== faultHandlers 
				&& node !== compensationHandler && node !== terminationHandler)
			});
		
		var nextLeftBound = 30;
		var nextUpperBound = 30;
		
		// handle Activity
		if (otherElements){
			
			// Sort top-down
			otherElements = otherElements.sortBy(function(element){
				return element.bounds.upperLeft().y;
			});
			
			// move some certain elements to the last child position
			// if it "true" returns, that means, the arrangement of elements
			// is changed, we should sort all elements again
			if (this._moveSomeElementToLastPosition(otherElements)){
				// Sort again
				otherElements = otherElements.sortBy(function(element){
					return element.bounds.upperLeft().y;
				});
			}
			
			var lastUpperYPosition = 0;
			var elementWidth;
			var maxElementWidth = 0;
			
			// Arrange shapes like Layout-Vertical
			otherElements.each (function(element){
		
				var ul = element.bounds.upperLeft();
				var oldUlY = ul.y;
			
				ul.y = lastUpperYPosition + 30;
				lastUpperYPosition = ul.y + element.bounds.height();
			
				if (ul.y != oldUlY) {
					element.bounds.moveTo(30, ul.y);
				};
				
				elementWidth = element.bounds.width();
				if (elementWidth > maxElementWidth){
					maxElementWidth = elementWidth;
				}
			});
			
			nextLeftBound = 30 + maxElementWidth + 30;
		
		}
		
		var width;
		var maxWidth = 0;
		
		// handle EventHanlders
		if (eventHandlers){
			eventHandlers.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = eventHandlers.bounds.lowerRight().y + 10;
			
			// record maximal width
			width = this._getRightestBoundOfAllChildren(eventHandlers)+ 30;
			if (width > maxWidth){
				maxWidth = width;
			}
		}
		// handle FaultHandlers
		if (faultHandlers){
			faultHandlers.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = faultHandlers.bounds.lowerRight().y + 10;
			
			// record maximal width
			width = this._getRightestBoundOfAllChildren(faultHandlers)+ 30;
			if (width > maxWidth){
				maxWidth = width;
			}
		}
		// handle CompensationHandler
		if (compensationHandler){
			compensationHandler.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = compensationHandler.bounds.lowerRight().y + 10;
			
			// record maximal width
			width = this._getRightestBoundOfAllChildren(compensationHandler)+ 30;
			if (width > maxWidth){
				maxWidth = width;
			}
		}
		
		// handle TerminationHandler
     	if (terminationHandler){
			terminationHandler.bounds.moveTo(nextLeftBound, nextUpperBound);
			
			// record maximal width
			width = this._getRightestBoundOfAllChildren(terminationHandler)+ 30;
			if (width > maxWidth){
				maxWidth = width;
			}
		}
		
		// resize all the handlers with the same width
		if (width > 0){
			var ul;
			var lr;
			
			if (eventHandlers){	
				width = eventHandlers.bounds.width();
				if (width !== maxWidth){
					ul = eventHandlers.bounds.upperLeft();
					lr = eventHandlers.bounds.lowerRight();
					eventHandlers.bounds.set(ul.x, ul.y, ul.x + maxWidth, lr.y);
					//eventHandlers._changed();
				}
			}

			if (faultHandlers){
				width = faultHandlers.bounds.width();
				if (width !== maxWidth){
					ul = faultHandlers.bounds.upperLeft();
					lr = faultHandlers.bounds.lowerRight();
					faultHandlers.bounds.set(ul.x, ul.y, ul.x + maxWidth, lr.y);
					//faultHandlers._changed();
				}
			}

			if (compensationHandler){
				width = compensationHandler.bounds.width();
				if (width !== maxWidth){
					ul = compensationHandler.bounds.upperLeft();
					lr = compensationHandler.bounds.lowerRight();
					compensationHandler.bounds.set(ul.x, ul.y, ul.x + maxWidth, lr.y);
					//compensationHandler._changed();
				}
			}
			
	     	if (terminationHandler){
				width = terminationHandler.bounds.width();
				if (width !== maxWidth){
					ul = terminationHandler.bounds.upperLeft();
					lr = terminationHandler.bounds.lowerRight();
					terminationHandler.bounds.set(ul.x, ul.y, ul.x + maxWidth, lr.y);
					//terminationHandler._changed();
				}
			}
		}
		
		this._autoResizeLayout(shape);
		
		this._update(shape);
		
		return;
		
	},
	
	_getRightestBoundOfAllChildren : function(shape){
		var elements = shape.getChildShapes(false);
     	
		// If there are no elements
		if(!elements || elements.length == 0) {
			// 160 is the default width of hanlders
			return 130;
		};
			
		// Sort left-right
		elements = elements.sortBy(function(element){
			return element.bounds.lowerRight().x;
		});
		
		return elements.last().bounds.lowerRight().x;
	},
	
	_handleLayoutVerticalEvent: function(shape) {
		
		if (this.isEnabled == false) {
			return;
		}
		
		var elements = shape.getChildShapes(false);
		
		// if Autolayout is not required, do nothing.
		if (!this._requiredAutoLayout (shape)){
     		return;
     	};
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			this._resetBounds(shape);
			return;
		};
		
		// Sort top-down
		elements = elements.sortBy(function(element){
			return element.bounds.upperLeft().y;
		});
		
					
		// move some certain elements to the last child position
		// if it "true" returns, that means, the arrangement of elements
		// is changed, we should sort all elements again
		if (this._moveSomeElementToLastPosition(elements)){
			// Sort again
			elements = elements.sortBy(function(element){
				return element.bounds.upperLeft().y;
			});
		}
		
		var lastUpperYPosition = 0;
		// Arrange shapes
		elements.each(function(element){
		
			var ul = element.bounds.upperLeft();
			var oldUlY = ul.y;
			
			ul.y = lastUpperYPosition + 30;
			lastUpperYPosition = ul.y + element.bounds.height();
			
			if ((ul.y != oldUlY)) {
				element.bounds.moveTo(30, ul.y);
			}
		});
		
		this._autoResizeLayout(shape);
	
		return;
	},
	
	_handleLayoutHorizontalEvent: function(shape) {

		if (this.isEnabled == false) {
			return;
		}
		
		var elements = shape.getChildShapes(false);
		
		// if Autolayout is not required, do nothing.
		if (!this._requiredAutoLayout (shape)){
     		return;
     	};
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			this._resetBounds(shape);
			return;
		};
					
		
		// Sort left-right
		elements = elements.sortBy(function(element){
			return element.bounds.upperLeft().x;
		});
		
		// move some certain elements to the last child position
		// if it "true" returns, that means, the arrangement of elements
		// is changed, we should sort all elements again
		if (this._moveSomeElementToLastPosition(elements)){
			// Sort again
			elements = elements.sortBy(function(element){
				return element.bounds.upperLeft().x;
			});
		}
		
		var lastLeftXPosition = 0;
		
		// Arrange shapes on rows (align left)
		elements.each(function(element){
		
			var ul = element.bounds.upperLeft();
			var oldUlX = ul.x;
			
			ul.x = lastLeftXPosition + 30;
			lastLeftXPosition = ul.x + element.bounds.width();

			if ((ul.x != oldUlX)) {
				element.bounds.moveTo(ul.x, 30);
			}
		});
		
		this._autoResizeLayout(shape);
			
		return;
	},
	
	
	
	_handleSingleChildLayoutEvent: function(shape) {
     	
		if (this.isEnabled == false) {
			return;
		}
		
		var elements = shape.getChildShapes(false);
		
		// if Autolayout is not required, do nothing.
		if (!this._requiredAutoLayout (shape)){
     		return;
     	};
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			this._resetBounds(shape);
			return;
		};
		
		elements.first().bounds.moveTo(30, 30);
		
		this._autoResizeLayout(shape);
		
		return;
	},
	
	_handleAutoResizeLayoutEvent: function(shape) {
		
		if (this.isEnabled == false) {
			return;
		};
		
		var elements = shape.getChildShapes(false);
		
		// if Autolayout is not required, do nothing.
		if (!this._requiredAutoLayout (shape)){
     		return;
     	};
		
		elements.each(function(element){
		
			var ul = element.bounds.upperLeft();
			
			if ((ul.x < 30)) {
				element.bounds.moveTo(30, ul.y);
				ul = element.bounds.upperLeft();
			}
			
			if ((ul.y < 30)) {
				element.bounds.moveTo(ul.x, 30);
			}
		});
		
		this._autoResizeLayout(shape);
	},
	
	/**
	 * Resizes the shape to the bounds of the child shapes 
	 */
	_autoResizeLayout: function(shape) {
		
		var elements = shape.getChildShapes(false);
		
		if (elements.length > 0) {

		    elements = elements.sortBy(function(element){
				return element.bounds.lowerRight().x;
		    });
		    
			var rightBound = elements.last().bounds.lowerRight().x;
                 
		    elements = elements.sortBy(function(element){
				return element.bounds.lowerRight().y;
		    });
		    
			var lowerBound = elements.last().bounds.lowerRight().y;
			
			var ul = shape.bounds.upperLeft();
			var lr = shape.bounds.lowerRight();
			
			//handle "flow" specially.
			if (shape.getStencil().id() ==shape.getStencil().namespace() + "flow"){
			 	
			 	if (lr.x < ul.x + rightBound + 30){
			 		shape.bounds.set(ul.x, ul.y, ul.x + rightBound + 30, lr.y);
			 		lr.x = ul.x + rightBound + 30;
			 		//shape._changed();
			 	};
			 	
			 	if (lr.y < ul.y + lowerBound + 30){
			 		shape.bounds.set(ul.x, ul.y, lr.x, ul.y + lowerBound + 30);
			 		//shape._changed();
			 	};			 	
			 } else {
			 	if (lr.x != ul.x + rightBound + 30 || lr.y != ul.y + lowerBound + 30){
			 		shape.bounds.set(ul.x, ul.y, ul.x + rightBound + 30, ul.y + lowerBound + 30);
			 		//shape._changed();
			 	};
			 };
		};
		
		return;
	},
	
	_resetBounds: function (shape) {

		// all the shapes without children will be reseted
		var ul = shape.bounds.upperLeft();
		var lr = shape.bounds.lowerRight();
		
		if (shape.getStencil().id() == shape.getStencil().namespace() + "process"){
			if (shape.getStencil().namespace() == "http://b3mn.org/stencilset/bpel#"){
				if (lr.x != ul.x + 600 || lr.y != ul.y + 500){
					shape.bounds.set(ul.x, ul.y, ul.x + 600, ul.y + 500);
					//shape._changed();
				};
			} else if (shape.getStencil().namespace() == "http://b3mn.org/stencilset/bpel4chor#"){
				if (lr.x != ul.x + 690 || lr.y != ul.y + 200){
					shape.bounds.set(ul.x, ul.y, ul.x + 690, ul.y + 200);
					//shape._changed();
				};
			} else {
				return;
			}
		} else if (shape.getStencil().id() == shape.getStencil().namespace() + "flow"){
			if (lr.x != ul.x + 290 || lr.y != ul.y + 250){
				shape.bounds.set(ul.x, ul.y, ul.x + 290, ul.y + 250);
				//shape._changed();
			};
		} else if (this._isHandlers(shape)){
			if (lr.x != ul.x + 160 || lr.y != ul.y + 80){
				shape.bounds.set(ul.x, ul.y, ul.x + 160, ul.y + 80);
				//shape._changed();
			};
		} else {
			if (lr.x != ul.x + 100 || lr.y != ul.y + 80){
				shape.bounds.set(ul.x, ul.y, ul.x + 100, ul.y + 80);
				//shape._changed();
			};	
		};

	},
	
	_isHandlers: function (shape) {
	  	if (shape.getStencil().id() == shape.getStencil().namespace() + "eventHandlers"){
	  		return true;
	  	};
	  	
	  	if (shape.getStencil().id() == shape.getStencil().namespace() + "faultHandlers"){
	  		return true;
	  	};
	  	
	  	if (shape.getStencil().id() == shape.getStencil().namespace() + "compensationHandler"){
	  		return true;
	  	};
	  	
	  	if (shape.getStencil().id() == shape.getStencil().namespace() + "terminationHandler"){
	  		return true;
	  	};
		
	  	return false;
	},
	
	_requiredAutoLayout: function(shape) {
		
		var key = "oryx-autolayout";
		var autolayout = shape.properties[key];
		
		if (autolayout == null){
			return true;
		};
		
		if (autolayout){
			return true;
		};
		
		return false;
	},
	
	/**
	 * find a element with the role "lastChild", that means, this shape should be
	 * the last child of their parent, e.g.: "else" in "if-block". then move these elements
	 * to the last position of the set.
	 * 
	 * 
	 * @param {} elements : the set of all elements
	 * @pre      all the elements in set are already once arranged, so we just put the 
	 *           "lastChild" after the current last one. 
	 * @return   if the arrangement of elements is changed.
	 */
	_moveSomeElementToLastPosition: function (elements){
		var lastChild = elements.find(function(node) {
			 	return (Array.indexOf(node.getStencil().roles(), node.getStencil().namespace() + "lastChild")>= 0);
			});	
		
		// if there are not such element or it's already the last child,
		// do nothing.	
		if (!lastChild || lastChild == elements.last()){
			return false;
		}
		
		// move it after the current last child
		ulOfCurrentLastChild = elements.last().bounds.upperLeft();
		lastChild.bounds.moveTo(ulOfCurrentLastChild.x + 1, ulOfCurrentLastChild.y + 1);
		
		return true;
	},
	
	_update : function(shape){
		// update the canvas only if the current node is "process", with this we can
		// make sure that, each time just once update after all the nodes are arranged
		// and we must check, whether the node "process" changed is, if not, don't update,
		// otherwise, an endless loop may occur, if there are more than three nesting level 
		// in a shape.
		if (shape.getStencil().id() == shape.getStencil().namespace() + "process"
		&& shape.isChanged){
		//	this.facade.getCanvas().update();
		}
	}
});