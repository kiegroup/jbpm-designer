/**
 * Copyright (c) 2009
 * Sven Wagner-Boysen
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
   @namespace Oryx name space for plugins
   @name ORYX.Plugins
*/
 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
	

/**
 * This plugin provides methodes to layout the choreography diagrams of BPMN 2.0.
 * 
 * @class ORYX.Plugins.Bpmn2_0Choreography
 * @extends ORYX.Plugins.AbstractPlugin
 * @param {Object} facade
 * 		The facade of the Editor
 */
ORYX.Plugins.Bpmn2_0Choreography = {
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		
		/* Register on event ORYX.CONFIG.EVENT_STENCIL_SET_LOADED and ensure that
		 * the stencil set extension is loaded.
		 */
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, 
										this.handleStencilSetLoaded.bind(this));
		
		/**
		 * FF 3.0 Bugfixing: Check if all events are loaded
		 */
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, function(){
			if (!this._eventsRegistered) {
				this.handleStencilSetLoaded({});
				this.afterLoad();
			}
		}.bind(this));
		
		this.participantSize = 20;
		this.extensionSizeForMarker = 10;
		this.choreographyTasksMeta = new Hash();
		
		/* Disable the layout callback until the diagram is loaded. */
		this._isLayoutEnabled = false;
	},
	
	
	/**
	 * Check if the 'http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#'
	 * stencil set extension is loaded and thus register or unregisters on the 
	 * appropriated events.
	 */
	handleStencilSetLoaded : function(event) {
		
		/* Enable layout callback */
		if(event.lazyLoaded) {
			this._isLayoutEnabled = true;
		}
		
		if(this.isStencilSetExtensionLoaded('http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#')) {
			this.registerPluginOnEvents();
		} else {
			this.unregisterPluginOnEvents();
		}
	},
	
	/**
	 * Register this plugin on the events.
	 */
	registerPluginOnEvents: function() {
		this._eventsRegistered = true;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.addParticipantsOnCreation.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.choreography.task', this.handleLayoutChoreographyTask.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.choreography.subprocess.expanded', this.handleLayoutChoreographySubprocessExpanded.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.choreography.subprocess.collapsed', this.handleLayoutChoreographySubprocessCollapsed.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.afterLoad.bind(this));

//		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.handlePropertyChanged.bind(this));
	},
	
	/**
	 * Unregisters this plugin from the events.
	 */
	unregisterPluginOnEvents: function() {
//		this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		//this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_SHAPEADDED, this.addParticipantsOnCreation.bind(this));
//		this.facade.unregisterOnEvent('layout.bpmn2_0.choreography.task', this.handleLayoutChoreographyTask.bind(this));
//		this.facade.unregisterOnEvent('layout.bpmn2_0.choreography.subprocess.expanded', this.handleLayoutChoreographySubprocessExpanded.bind(this));
//		this.facade.unregisterOnEvent('layout.bpmn2_0.choreography.subprocess.collapsed', this.handleLayoutChoreographySubprocessCollapsed.bind(this));
		this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_LOADED, this.afterLoad.bind(this));
	},
	
	/**
	 * Init the meta values for the layout mechanism of the choreography task 
	 * and enables the layout callback
	 * 
	 * @param {Object} event
	 * 		The event object
	 */
	afterLoad : function(event) {
		
		//if(this._isLayoutEnabled) {return;}
		/* Enable the layout callback for choreography activities */
		this._isLayoutEnabled = true;
		
		/* Initialize layout meta values for each choreography task */
		this.facade.getCanvas().getChildNodes(true).each(function(shape){
			if (!(shape.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyTask" ||
				shape.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessCollapsed" ||
				shape.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessExpanded")) {
				return;
			}

			var participantsOnTop = new Array();
			var participantsOnBottom = new Array();
			
			var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(shape);
			
			/* Get participants */
			var participants = shape.getChildNodes(false).findAll(function(node) {
				return node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant";
			}); 
			
			/* Sort participants from up to bottom */
			participants = participants.sort(function(a,b) {
				var ay = Math.round(a.absoluteBounds().upperLeft().y);
				var by = Math.round(b.absoluteBounds().upperLeft().y);
				return  ay < by ? -1 : (ay > by ? 1 : 0);
			});
			
			/* Determine participants on top and bottom side */
			var expectedYValue = 0;
			var participantsExtendedOnTop = 0;
			var participantsExtendedOnBottom = 0;
			participants.each(function(participant) {
				/* Disable Resizing */
				participant.isResizable = false;
				
				var extended = (participant.properties['oryx-multiple_instance'] === "" ? 
						false : participant.properties['oryx-multiple_instance']);
				if(participant.bounds.upperLeft().y == expectedYValue) {
					participantsOnTop.push(participant);
					expectedYValue = participant.bounds.lowerRight().y;
					if (extended) {
						participantsExtendedOnTop++;
					}
				} else {
					/* Participant is member of the bottom band */
					participantsOnBottom.push(participant);
					if (extended) {
						participantsExtendedOnBottom++;
					}
				}
			});
			
			/* Initialize meta values */
			choreographyTaskMeta.numOfParticipantsOnTop = participantsOnTop.length;
			choreographyTaskMeta.numOfParticipantsOnBottom = participantsOnBottom.length;
			choreographyTaskMeta.numOfParticipantsExtendedOnBottom = participantsExtendedOnBottom;
			choreographyTaskMeta.numOfParticipantsExtendedOnTop = participantsExtendedOnTop;
			
			choreographyTaskMeta.bottomYStartValue = (participantsOnBottom.first() ? 
					participantsOnBottom.first().bounds.upperLeft().y : shape.bounds.height());
					
			choreographyTaskMeta.topYEndValue = (participantsOnTop.last() ? 
					participantsOnTop.last().bounds.lowerRight().y : 0);
			
			choreographyTaskMeta.center = choreographyTaskMeta.topYEndValue +
				(choreographyTaskMeta.bottomYStartValue - choreographyTaskMeta.topYEndValue) / 2;
			
			choreographyTaskMeta.oldHeight = shape.bounds.height();
			choreographyTaskMeta.oldBounds = shape.bounds.clone();
			
			choreographyTaskMeta.topParticipants = participantsOnTop;
			choreographyTaskMeta.bottomParticipants = participantsOnBottom;
			
			shape.isChanged = true;
		}.bind(this));
		
		/* Update to force marker positioning */
		this.facade.getCanvas().update();
	},
	
	/**
	 * Handler for 'layout.bpmn2_0.choreography.subprocess.expanded'
	 * Applies the layout for an expanded subprocess. e.g. positioning of the 
	 * text field.
	 * 
	 * @param {Object} event
	 * 		The layout event.
	 */
	handleLayoutChoreographySubprocessExpanded : function(event) {
		if(!this._isLayoutEnabled) {return;}
		
		var choreographyTask = event.shape;
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(choreographyTask);
		var heightDelta = choreographyTask.bounds.height() / choreographyTask._oldBounds.height();
	
		/* Handle text field position */
		var textField = choreographyTask._labels[choreographyTask.getId() + 'text_name'];
		if(textField) {
			var top = choreographyTaskMeta.topYEndValue + 5;

			/* Consider changed in update cycle */
			if(choreographyTask.isResized && heightDelta) {
				textField.y = top / heightDelta;
			} else {
				textField.y = top;
			}
			
		}
	},
	
	
	/**
	 * Handler for 'layout.bpmn2_0.choreography.subprocess.collapsed'
	 * Applies the layout for a collapsed subprocess. 
	 * e.g. plus marker
	 * 
	 * @param {Object} event
	 * 		The layout event.
	 */
	handleLayoutChoreographySubprocessCollapsed : function(event) {
		if(!this._isLayoutEnabled) {return;}
		
		var choreographyTask = event.shape;
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(choreographyTask);
		
		/* Calculate position of the "plus" marker of the subprocess */
		var plusMarker = choreographyTask._svgShapes.find(function(svgShape) {
			return svgShape.element.id == choreographyTask.getId() + 'plus_marker';
		});
		
		var plusMarkerBorder = choreographyTask._svgShapes.find(function(svgShape) {
			return svgShape.element.id == choreographyTask.getId() + 'plus_marker_border';
		});
		
		if(plusMarker && plusMarkerBorder) {
				plusMarker._isYLocked = true;
				plusMarker.y = choreographyTaskMeta.bottomYStartValue - 12;
				
				plusMarkerBorder._isYLocked = true;
				plusMarkerBorder.y = choreographyTaskMeta.bottomYStartValue - 14;
		}
	},
	
	/**
	 * When a choreography task is created, two participants automatically will
	 * be added (one initiating and one returning)
	 * 
	 * @param {Object} event
	 * 		The ORYX.CONFIG.EVENT_SHAPEADDED event
	 */
	addParticipantsOnCreation: function(event) {
		if(!this._isLayoutEnabled) {return;}
		var shape = event.elements[0];
		if(shape&&event.elements.length===1&&shape._stencil&&
			!shape.initialParticipantsAdded && 
			(shape.getStencil().id() === 
				"http://b3mn.org/stencilset/bpmn2.0#ChoreographyTask" ||
			shape.getStencil().id() === 
				"http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessCollapsed" ||
			shape.getStencil().id() === 
				"http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessExpanded")	){
		
			var hasParticipants = shape.getChildNodes().find(function(node) {
				return (node.getStencil().id() === 
							"http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant");
			});
			
			if(hasParticipants) {return;}
			
			/* Insert initial participants */
			var participant1 = {
				type:"http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant",
				position:{x:0,y:0},
				namespace:shape.getStencil().namespace(),
				parent:shape
			};
			var shapeParticipant1 = this.facade.createShape(participant1);
			shapeParticipant1.setProperty('oryx-initiating', true);
			var propEvent = {
				elements 	: [shapeParticipant1],
				key 		: "oryx-initiating",
				value		: true
			};
			this.handlePropertyChanged(propEvent);
			
			var participant2 = {
				type:"http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant",
				position:{x:0,y:shape.bounds.lowerRight().y},
				namespace:shape.getStencil().namespace(),
				parent:shape
			};
			this.facade.createShape(participant2);
			this.facade.getCanvas().update();
			this.facade.setSelection([shape]);
			shape.initialParticipantsAdded = true;
		}
	},
	
	/**
	 * Initialize the meta data object for the choreography task if necessary and
	 * return it.
	 * 
	 * @param {Object} choregraphyTask
	 * 		The choreography task
	 * @return {Object} choreographyTaskMetaData
	 * 		Positioning values to handle child participants.
	 */
	addOrGetChoreographyTaskMeta: function(choreographyTask) {
		if(!this.choreographyTasksMeta[choreographyTask.getId()]) {
			
			/* Initialize meta values */			
			this.choreographyTasksMeta[choreographyTask.getId()] = new Object();
			this.choreographyTasksMeta[choreographyTask.getId()].numOfParticipantsOnTop = 0;
			this.choreographyTasksMeta[choreographyTask.getId()].numOfParticipantsOnBottom = 0;
			this.choreographyTasksMeta[choreographyTask.getId()].numOfParticipantsExtendedOnBottom = 0;
			this.choreographyTasksMeta[choreographyTask.getId()].numOfParticipantsExtendedOnTop = 0;
			
			this.choreographyTasksMeta[choreographyTask.getId()].bottomYStartValue = 
				choreographyTask.bounds.height();
			this.choreographyTasksMeta[choreographyTask.getId()].topYEndValue = 0;
			this.choreographyTasksMeta[choreographyTask.getId()].center = 
				choreographyTask.bounds.height() / 2;
			
			this.choreographyTasksMeta[choreographyTask.getId()].oldHeight = 
				choreographyTask.bounds.height();
			this.choreographyTasksMeta[choreographyTask.getId()].oldBounds = 
				choreographyTask.bounds.clone();
			
			/* Ensure the side of participants while resizing */
			this.choreographyTasksMeta[choreographyTask.getId()].topParticipants = new Array();
			this.choreographyTasksMeta[choreographyTask.getId()].bottomParticipants = new Array();
			
		}
		return this.choreographyTasksMeta[choreographyTask.getId()];
	},
	
	/**
	 * Adjust the meta values, if the choreography task is resized.
	 * 
	 * @param {Object} choreographyTask
	 * @param {Object} choreographyTaskMeta
	 */
	handleResizingOfChoreographyTask: function(choreographyTask, choreographyTaskMeta) {
		if(choreographyTask.bounds.height() == choreographyTaskMeta.oldHeight) {return;}
		
		/* Ensure that the choreography task is not too small in height */
		
		var minimumHeight = choreographyTaskMeta.numOfParticipantsOnTop 
							* this.participantSize + 
							choreographyTaskMeta.numOfParticipantsExtendedOnTop *
							this.extensionSizeForMarker +
							choreographyTaskMeta.numOfParticipantsOnBottom 
							* this.participantSize +
							choreographyTaskMeta.numOfParticipantsExtendedOnBottom *
							this.extensionSizeForMarker 
							+ 40;
		if(minimumHeight > choreographyTask.bounds.height()) {
			var ul = choreographyTask.bounds.upperLeft();
			var oldUl = choreographyTaskMeta.oldBounds.upperLeft();
			var lr = choreographyTask.bounds.lowerRight();
			var oldLr = choreographyTaskMeta.oldBounds.lowerRight();
			
			if(ul.y != oldUl.y) {
				/* Resized on top side */
				choreographyTask.bounds.set(ul.x, lr.y - minimumHeight, lr.x, lr.y);
			} else if(lr.y != oldLr.y) {
				/* Resized on bottom side */
				choreographyTask.bounds.set(ul.x, ul.y, lr.x, ul.y + minimumHeight);
			}
		}
		
		/* Adjust the y coordinate for the starting position of the bottom participants */
		var yAdjustment = choreographyTaskMeta.oldHeight - choreographyTask.bounds.height();
		choreographyTaskMeta.bottomYStartValue -= yAdjustment;
		
		/* Signals it was resized */
		return true;
	},
	
	/**
	 * Handler for layouting event 'layout.bpmn2_0.choreography.task'
	 * 
	 * @param {Object} event
	 * 		The layout event
	 */
	handleLayoutChoreographyTask: function(event) {
		if(!this._isLayoutEnabled) {return;}
		
		var choreographyTask = event.shape;
		var isNew = !this.choreographyTasksMeta[choreographyTask.getId()];
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(choreographyTask);
		
		var isResized = this.handleResizingOfChoreographyTask(choreographyTask, choreographyTaskMeta);

		var oldCountTop = choreographyTaskMeta.numOfParticipantsOnTop;
		var oldCountBottom = choreographyTaskMeta.numOfParticipantsOnBottom;
		
		/* ------- Handle participants on top side  ------- */
		
		if(isResized) {
			/* Do not calculate the position of a paraticipant if it was only a resizing */
			var participants = choreographyTaskMeta.topParticipants;
		} else {
			var participants = this.getParticipants(choreographyTask,true,false);
			
			if(!participants) {return;}
			this.ensureParticipantsParent(choreographyTask, participants);
		}
		
		var numOfParticipantsExtended = 0;

		/* Put participants into the right position */
		participants.each(function(participant, i) {
			
			/* Disable resizing by the user interface */
			participant.isResizable = false;
			
			participant.setProperty('oryx-corners', "None");
			var isExtended = this.setBoundsOfParticipantDependOnProperties(
													participant,
													i,
													numOfParticipantsExtended,
													choreographyTask.bounds.width(),
													0);
			
			/* Count extended participants */										
			if(isExtended) {numOfParticipantsExtended++;}
													
//			participant.bounds.set(0, i * this.participantSize, 
//								choreographyTask.bounds.width(), 
//								this.participantSize +  i * this.participantSize);
			
			/* The first participants gets rounded corners */
			if(i == 0) {
				participant.setProperty('oryx-corners', "Top");
			}
			
			this.adjustTopBackground(participant);
		}.bind(this));
		
		/* Resize choreography task to top side */
		var resizeFactor = participants.length - 
									choreographyTaskMeta.numOfParticipantsOnTop;
		var resizeFactorExtended = numOfParticipantsExtended -
							choreographyTaskMeta.numOfParticipantsExtendedOnTop;
		
		var bounds = choreographyTask.bounds;
		var ul = bounds.upperLeft();
		var lr = bounds.lowerRight();
		
		if (!isNew)
			bounds.set(ul.x, 
					ul.y - resizeFactor * this.participantSize 
					- resizeFactorExtended * this.extensionSizeForMarker, lr.x, lr.y);
		
		/* Set new top and bottom border values */
		choreographyTaskMeta.topYEndValue = 
							participants.length * this.participantSize 
						+	numOfParticipantsExtended * this.extensionSizeForMarker;
		
		
		/* Set new meta value for top participant band */	
		choreographyTaskMeta.numOfParticipantsExtendedOnTop = numOfParticipantsExtended;
		choreographyTaskMeta.numOfParticipantsOnTop = participants.length;
		choreographyTaskMeta.topParticipants = participants;
		
		
		/* ----- Handle participants on bottom side --------- */
		if(isResized) {
			/* Do not calculate the position of a paraticipant if it was only a resizing */
			var participants = choreographyTaskMeta.bottomParticipants;
		} else {
			var participants = this.getParticipants(choreographyTask,false,true);
			
			if(!participants) {return;}
			this.ensureParticipantsParent(choreographyTask, participants);
		}
		
				
		if (isNew){
			choreographyTaskMeta.bottomYStartValue = (bounds.height() - 
				(participants.length != 0 ? 
					eval(participants.map(function(p){ return this.participantSize + (this.isExtended(p)?this.extensionSizeForMarker:0) }.bind(this)).join("+")) :
					0));
		} else {
			choreographyTaskMeta.bottomYStartValue += 
				resizeFactor * this.participantSize + 
				resizeFactorExtended * this.extensionSizeForMarker;
		}
		
		var bottomStartYValue = choreographyTaskMeta.bottomYStartValue;
		var numOfParticipantsExtended = 0;
		
		/* Put participants into the right position */
		participants.each(function(participant, i) {
			
			/* Disable resizing by the user interface */
			participant.isResizable = false;
			
			participant.setProperty('oryx-corners', "None");
			
			var isExtendedParticipant = 
				this.setBoundsOfParticipantDependOnProperties(participant, 
								i,
								numOfParticipantsExtended,
								choreographyTask.bounds.width(),
								bottomStartYValue);
			
			/* Count extended participants */
			if(isExtendedParticipant) {numOfParticipantsExtended++;}
			
//			participant.bounds.set(0, bottomStartYValue + 
//														 i * this.participantSize, 
//								choreographyTask.bounds.width(), 
//								bottomStartYValue +
//								this.participantSize +  i * this.participantSize);
			
			/* The last participants gets rounded corners */
			if(i == participants.length - 1) {
				participant.setProperty('oryx-corners', "Bottom");
			}
			
			this.adjustTopBackground(participant);
			
		}.bind(this));
		
		/* Resize choreography task to top bottom side */
		
		var resizeFactor = participants.length - 
								choreographyTaskMeta.numOfParticipantsOnBottom;
		var resizeFactorExtended = numOfParticipantsExtended - 
						choreographyTaskMeta.numOfParticipantsExtendedOnBottom;
		
		var bounds = choreographyTask.bounds;
		var ul = bounds.upperLeft();
		var lr = bounds.lowerRight();
		
		if (!isNew)
		bounds.set( ul.x, 
					ul.y, 
					lr.x, 
					lr.y + resizeFactor * this.participantSize 
					+ resizeFactorExtended * this.extensionSizeForMarker);
		
		/* Store new meta values */
		choreographyTaskMeta.numOfParticipantsExtendedOnBottom = numOfParticipantsExtended;
		choreographyTaskMeta.numOfParticipantsOnBottom = participants.length;
		choreographyTaskMeta.bottomParticipants = participants;
		
		/* Check if participants has changed */
		var participantsHasChanged = 	oldCountTop !== choreographyTaskMeta.numOfParticipantsOnTop ||
										oldCountBottom !==choreographyTaskMeta.numOfParticipantsOnBottom;	
		
		/* Handle positioning of sub elements */
		this.ensureCenterPositionOfMagnets(choreographyTask, isResized, participantsHasChanged);
		this.adjustTextFieldAndMarkerPosition(choreographyTask);
		
		choreographyTaskMeta.oldHeight = bounds.height();
		choreographyTaskMeta.oldBounds = bounds.clone();
	},
	
	/**
	 * Return TRUE if the participant is extended (has the attribute muliple instance)
	 * @param {ORYX.Core.Node} participant
	 */
	isExtended: function(participant){
		return (!participant || participant.properties['oryx-multiple_instance'] === "" ? 
					false : !!participant.properties['oryx-multiple_instance']);
	},
	
	/**
	 * Resizes the participant depending on value of the multi-instances 
	 * property.
	 * 
	 * @param {ORYX.Core.Node} participant
	 * 		The concerning participant
	 * @param {Integer} numParticipantsBefore
	 * 		Number of participants before current
	 * @param {Integer} numParticipantsExtendedBefore
	 *		Number of participants extended in size before current
	 * @param {Float} width
	 * 		The width of the participant
	 * @param {Integer} yOffset
	 * 		Offset for the position of the bottom participants
	 */
	setBoundsOfParticipantDependOnProperties: function(	participant, 
														numParticipantsBefore, 
														numParticipantsExtendedBefore,
														width,
														yOffset) {
		var extended = this.isExtended(participant);
		var ulY = yOffset + 
			numParticipantsBefore * this.participantSize + 
			numParticipantsExtendedBefore * this.extensionSizeForMarker;
		var lrY = yOffset + this.participantSize +
			numParticipantsBefore * this.participantSize + 
			(extended ? (numParticipantsExtendedBefore + 1) * this.extensionSizeForMarker : 
				numParticipantsExtendedBefore * this.extensionSizeForMarker);
		
		participant.bounds.set(	0, 
			ulY, 
			width, 
			lrY);
			
		/* Is a multi-instance participant */
		return extended;
	},
	
	/**
	 * Set the y coordinate for the text field and multiple instance marker 
	 * position in order to ensure that the text or marker is not hidden 
	 * by a participant.
	 * 
	 * @param {ORYX.Core.Node} choreographyTask
	 * 		The choreography task.
	 */
	adjustTextFieldAndMarkerPosition: function(choreographyTask) {
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(choreographyTask);
		var heightDelta = choreographyTask.bounds.height() / choreographyTask._oldBounds.height();
		
		/* Handle text field position */
		var textField = choreographyTask._labels[choreographyTask.getId() + 'text_name'];
		if(textField) {
			var center = choreographyTaskMeta.topYEndValue +
				(choreographyTaskMeta.bottomYStartValue - choreographyTaskMeta.topYEndValue) / 2;

			/* Consider changed in update cycle */
			if(choreographyTask.isResized && heightDelta) {
				textField.y = center / heightDelta;
			} else {
				textField.y = center;
			}
			
		}
		
		/* Handle MI and loop marker position */
		
		var loopMarker = choreographyTask._svgShapes.find(function(svgShape) {
			return svgShape.element.id == choreographyTask.getId() + 'loop_path';
		});
		if(loopMarker) {
				loopMarker._isYLocked = true;
				loopMarker.y = choreographyTaskMeta.bottomYStartValue - 7;
		}
		
		var miMarker = choreographyTask._svgShapes.find(function(svgShape) {
			return svgShape.element.id == choreographyTask.getId() + 'mi_path';
		}); 
		if(miMarker) {
			miMarker._isYLocked = true;
			miMarker.y = choreographyTaskMeta.bottomYStartValue - 11;
		}
		
	},
	
	/**
	 * The magnets of choreography activity were placed in the middle of both
	 * participant bands.
	 * 
	 * @param {ORYX.Core.Node} choreographyTask
	 * 		The choregraphy task containing the magnets
	 * @param {boolean} isResized
	 * 		Flag indicating a resizing of the task
	 * @param {boolean} participantsHasChanged
	 * 		Flag indicating if a new participants has been added or 
	 * 		changed the position (e.g.from top to bottom).
	 * 
	 */
	ensureCenterPositionOfMagnets: function(choreographyTask, isResized, participantsHasChanged) {
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(choreographyTask);
		var center = choreographyTaskMeta.topYEndValue + 
					(choreographyTaskMeta.bottomYStartValue 
								- choreographyTaskMeta.topYEndValue) / 2;
		
		var yAdjustment = center - choreographyTaskMeta.center;
		
		var heightDelta = choreographyTask.bounds.height() / 
							choreographyTaskMeta.oldBounds.height();
		if(!yAdjustment && !heightDelta) {return;}
		
		/* Find magnets that should be positioned relativly to the center */
		var magnets = choreographyTask.magnets.findAll(function(magnet) {
			return (!magnet.anchorTop && !magnet.anchorBottom)
		});
		
		/* Move magnets */
		magnets.each(function(magnet) {
			var x = magnet.bounds.center().x;
			var y = (magnet.bounds.center().y + yAdjustment) / heightDelta
			magnet.bounds.centerMoveTo(x,y);
		});
		
		/* Also move dockers */
		var absoluteTopYEndValue = choreographyTask.absoluteBounds().upperLeft().y 
									+ choreographyTaskMeta.topYEndValue;
		var absoluteBottomYStartValue = choreographyTask.absoluteBounds().upperLeft().y 
									+ choreographyTaskMeta.bottomYStartValue;
		var dockers = new Array();
		
		choreographyTask.incoming.each(function(seqFlow) {
			if(!(seqFlow instanceof ORYX.Core.Edge)) {return;}
			var docker = seqFlow.dockers.last();
			if(absoluteTopYEndValue <= docker.bounds.center().y 
				&& docker.bounds.center().y <= absoluteBottomYStartValue ) {
				dockers.push(docker);
			}
		});
		
		choreographyTask.outgoing.each(function(seqFlow) {
			if(!(seqFlow instanceof ORYX.Core.Edge)) {return;}
			var docker = seqFlow.dockers.first();
			if(absoluteTopYEndValue <= docker.bounds.center().y 
				&& docker.bounds.center().y <= absoluteBottomYStartValue ) {
				dockers.push(docker);
			}
		});
		
		if (participantsHasChanged&&choreographyTask.initialParticipantsAdded){
			dockers.each(function(dockerShape) {
				var ref = dockerShape.referencePoint;
				dockerShape.setReferencePoint({x:ref.x,y:(ref.y + yAdjustment) / heightDelta});
			});
		}

		
		/* Update center */
		choreographyTaskMeta.center = center;
	},
	
	/**
	 * Ensure that the parent of the participant is the choreography task.
	 * 
	 * @param {Object} shape
	 * 		The choreography task
	 * @param {Object} participants
	 * 		The participants
	 */
	ensureParticipantsParent: function(shape, participants) {
		if(!shape || !participants) {return;}
		
		participants.each(function(participant) {
			if(participant.parent.getId() == shape.getId()) {return;}
			
			
			
			
			/* Set ChoreographyTask as Parent */
			participant.parent.remove(participant);
			shape.add(participant);
		});
	},
	
	/**
	 * Returns the participants of a choreography task ordered by theire position.
	 * 
	 * @param {Object} shape
	 * 		The choreography task
	 * @param {Object} onTop
	 * 		Flag to get the participants from the top side of the task.
	 * @param {Object} onBottom
	 * 		Flag to get the participants from the bottom side of the task.
	 * @return {Array} participants;
	 * 		The child participants
	 */
	getParticipants: function(shape, onTop, onBottom) {
		if(shape.getStencil().id() !== "http://b3mn.org/stencilset/bpmn2.0#ChoreographyTask" &&
			shape.getStencil().id() !== "http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessCollapsed" &&
			shape.getStencil().id() !== "http://b3mn.org/stencilset/bpmn2.0#ChoreographySubprocessExpanded") {
			return null;
		}
		
		var choreographyTaskMeta = this.addOrGetChoreographyTaskMeta(shape);
		var center = shape.absoluteBounds().upperLeft().y +
			 choreographyTaskMeta.topYEndValue +
			(choreographyTaskMeta.bottomYStartValue - choreographyTaskMeta.topYEndValue) / 2;
		
		/* Get participants of top side */
		var participantsTop = shape.getChildNodes(true).findAll(function(node) { 
			return (onTop && 
					node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
					node.absoluteBounds().center().y <= center &&
					this.isParticipantOfShape(shape, node)); 
		}.bind(this));
		
		/* Get participants of bottom side */
		var participantsBottom = shape.getChildNodes(true).findAll(function(node) { 
			return (onBottom && 
					node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
					node.absoluteBounds().center().y > center && 
					this.isParticipantOfShape(shape, node)); 
		}.bind(this));
		
		var participants = participantsTop.concat(participantsBottom);
		
		participants = participants.sort(function(a,b) {
			var ay = Math.round(a.absoluteBounds().upperLeft().y);
			var by = Math.round(b.absoluteBounds().upperLeft().y);
			return  ay < by ? -1 : (ay > by ? 1 : 0);
		});
		
		return participants;
	},
	
	/**
	 * Checks if the participant belongs to the shape. Used to detect choreography
	 * tasks inside an expanded choreography subprocess.
	 * 
	 * @param {ORYX.Core.Node} shape
	 * 		The choreography element
	 * 
	 * @param {ORYX.Core.Node} participant
	 * 		The participant node
	 * 
	 * @return {boolean} 
	 * 		True if the participant is a direct child of the shape and is not
	 * 		contained in aother choreography task or subprocess
	 */
	isParticipantOfShape: function(shape, participant) {
		var participantsParent = participant.parent;
		
		/* Get a non-participant parent of the participant */
		while(participantsParent.getStencil().id() === 
				"http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant") {
			participantsParent = participantsParent.parent;			
		}
		
		/* The detected parent should be the shape */
		
		return participantsParent.getId() === shape.getId();
	},
	
	adjustTopBackground: function(shape){
		var pos = shape.properties["oryx-corners"];
		var bg = $(shape.getId()+"roundedBgRect");
		if (!bg){ return }
		
		if(pos==="Top") {
			bg.setAttributeNS(null, "fill", "url(#"+shape.getId()+"background_top) white");
		} else {
			var bgColor = shape.properties["oryx-color"];
			bg.setAttributeNS(null, "fill", bgColor);
		}	
	},
	
	/**
	 * PropertyWindow.PropertyChanged Handler
	 * 
	 * It sets the correct color of the elements of a participant depending on
	 * either initiating or returning nature.
	 * 
	 * @param {Object} event
	 * 		The property changed event
	 */
	handlePropertyChanged: function(event) {
		var shapes = event.elements;
		var propertyKey = event.key || event.name;
		var propertyValue = event.value;
		
		var changed = false;
		shapes.each(function(shape) {
			if (shape.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
			propertyKey === "oryx-initiating") {
			
				if (!propertyValue) {
					shape.setProperty("oryx-color", "#acacac");
				}
				else {
					shape.setProperty("oryx-color", "#ffffff");
				}
				
				changed = true;
			}
		})
		
		/* Update visualisation if necessary */
		if(changed) {
			this.facade.getCanvas().update();
		}
	}
	
};

ORYX.Plugins.Bpmn2_0Choreography = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.Bpmn2_0Choreography);
