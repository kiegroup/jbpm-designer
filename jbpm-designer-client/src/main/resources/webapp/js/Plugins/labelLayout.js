/**
 * Copyright (c) 2009
 * Robert Böhme
 * 
 **/

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.LabelLayout = Clazz.extend({
		
	construct: function( facade ){
		this.facade = facade;	
		this.myLabel = undefined; //defines the current label
		this.labelSelected = false; //true->label is selected
		this.labelLength = undefined; //length of a label
		this.rotationPointCoordinates = {x:0, y:0};
		this.mouseCoordinates = {x:0, y:0};
		this.labelCoordinates = {x:0, y:0};
		this.myEdge = undefined;	// set the Edge Parent for the RotaionPoint	
		this.rotate = false; //true ->Rotation of label is active; False -> Rotation of Label is not active
		this.State = 0; 	//current States for Rotation
		this.prevState = 0;	//previous State for Rotation
		this.canvasLabel = undefined; //Reference to Canvas
		this.canvas = false; //true if Reference to Canvas was saved
		
		//Register Events
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		
		//Visual representaion of the default RotationPoint		
		this.rotationPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					"stroke-width": 2.0, "stroke":"black", "d":  "M4,4 L0,6 M0,6 L-4,4 M-4,4 L-6,0 M-6,0 L-4,-4 M-4,-4 L0,-6 M0,-6 L4,-4 M4,-4 L6,2 M6,2 L2,0 M6,2 L8,0", "line-captions": "round"
					}]);		
		
		//Visual representation of the association line
		this.line = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					'stroke-width': 1, stroke: 'silver', fill: 'none',
					'pointer-events': 'none'}]);
		
		//Visual representation of the moving arrows
		this.startMovingCross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					"stroke-width": 1.0, "stroke":"black", "d":  "M0,0 L-10,0 M-10,0 L-6,-4 M-10,0 L-6,4 M0,0 L10,0 M10,0 L6,4 M10,0 L6,-4 M0,0 L0,10 M0,10 L4,6 M0,10 L-4,6 M0,0 L0,-10 M0,-10 L4,-6 M0,-10 L-4,-6", "line-captions": "round"
					}]);
		
		//visual represetation to the set the position of a label
		this.endMovingCross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					"stroke-width": 1.0, "stroke":"black", "d":  "M-2,0 L-10,0 M2,0 L10,0 M0,2 L0,10 M0,-2 L0,-10 M-2,0 L-6,4 M-2,0 L-6,-4 M2,0 L6,4 M2,0 L6,-4 M0,-2 L4,-6 M0,-2 L-4,-6 M0,2 L4,6 M0,2 L-4,6", "line-captions": "round"
					}]);
		
		//Visual representation to move the mouse left/right to rotate the label
		this.moveLeftRight = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					"stroke-width": 2.0, "stroke":"black", "d":  "M0,0 L-15,0 M-15,0 L-11,-4 M-15,0 L-11,4 M0,0 L15,0 M15,0 L11,4 M15,0 L11,-4", "line-captions": "round"
					}]);
		
		 /*
		 this.facade.offer({
	            'name': "Label rotate left",
	            'functionality': this.rotate_left.bind(this),
	            'group': "Rotation",
	            'icon': ORYX.PATH + "images/rotate_left.png",
	            'description': "rotate a label left",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 
		 this.facade.offer({
	            'name': "Label rotate right",
	            'functionality': this.rotate_right.bind(this),
	            'group': "Rotation",
	            'icon': ORYX.PATH + "images/rotate_right.png",
	            'description': "rotate a label right",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });*/		 
},

/**
 * Mouse Over Handler
 */
handleMouseOver: function(event, uiObj) {

	//Save Canvas for Reference(used for showing Line between edge and label)
	if(this.canvas == false){
		if(uiObj instanceof ORYX.Core.Canvas){
			this.canvasLabel = uiObj;
			canvas = true;
		}
	}	
},

/**
 * Mouse Down Handler
 */
handleMouseDown: function(event, uiObj) {	
	
	if(this.myLabel && this.myLabel._text != ""){	
		//save MousePosition
		var MouseX = this.facade.eventCoordinates(event).x;
		var MouseY = this.facade.eventCoordinates(event).y;
		
		//refresh Coordinates
		this.calculateLabelCoordinates();
		this.calculateRotationPointCoordinates();
	
		//Set LabelPosition to MousePosition	
		if(this.labelSelected == true){				
			if(this.myLabel._rotate == 270 || this.myLabel._rotate == 315 || this.myLabel._rotate == 360 || this.myLabel._rotate == 45) {
				this.myLabel.x = this.facade.eventCoordinates(event).x+5;
				this.myLabel.y = this.facade.eventCoordinates(event).y-5;
			}else if (this.myLabel._rotate == 90 || this.myLabel._rotate == 135 || this.myLabel._rotate == 180 || this.myLabel._rotate == 225){
				this.myLabel.x = this.facade.eventCoordinates(event).x-5;
				this.myLabel.y = this.facade.eventCoordinates(event).y+5;
			}
			else {
				this.myLabel.x = this.facade.eventCoordinates(event).x+10;
				this.myLabel.y = this.facade.eventCoordinates(event).y-5;
			}
			
			//refresh real Rotationpoint
			if(this.myLabel._rotationPoint){
				this.myLabel._rotationPoint.x = MouseX;
				this.myLabel._rotationPoint.y = MouseY;
			}
			
			// save the current position of the label in edge.js, mark the label as free moved
			this.myLabel.edgePosition = "freeMoved";
			this.myLabel.update();			
		
			//Refresh Coordinates
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();				
			this.showOverlay("RotationPoint", this.myEdge, this.rotationPointCoordinates, this.rotationPoint);
			this.hideOverlay("SettingArrows");
			
			//Show Moving Arrows		
			this.mouseCoordinates = {x:this.labelCoordinates.x, y:this.labelCoordinates.y};			
			this.showOverlay("MovingArrows", this.myEdge, this.mouseCoordinates, this.startMovingCross);			
			
			//show the Association line
			this.showLine();
		}
		else {		
			this.hideLine();
			this.hideOverlay("RotationPoint");
			this.hideOverlay("MovingArrows");
			this.hideOverlay("SettingArrows");
		}
	
		//Check if Mouse is in the ClickArea of the Label 
		if(	this.labelSelected == false && 
				MouseX >= this.labelCoordinates.x-20 && 
				MouseX <= this.labelCoordinates.x+20 && 
				MouseY <= this.labelCoordinates.y+20 && 
				MouseY >= this.labelCoordinates.y-20){		
		
			//Set Label as Selected
			this.labelSelected=true;
						
			//refresh and show RotationPoint
			this.calculateRotationPointCoordinates();
			this.showOverlay("RotationPoint", this.myEdge, this.rotationPointCoordinates, this.rotationPoint);
		}
		else {
			//Set Label as not selected
			this.labelSelected = false;	
		}
	
		//Check if MouseClick is on RotationPoint
		if(	this.rotate == false && 
			MouseX >= this.rotationPointCoordinates.x-20 && 
			MouseX <= this.rotationPointCoordinates.x+20 && 
			MouseY >= this.rotationPointCoordinates.y-20 && 
			MouseY <= this.rotationPointCoordinates.y+20){
		
			//acitvate Rotation
			this.rotate = true;
			
			//Set current RotationState
			this.State = 0;
			
			//show RotationArrows to show that rotation is active
			this.showOverlay("RotationPointActive", this.myEdge, this.rotationPointCoordinates, this.moveLeftRight);
		}
		else{
			//deactivate Rotation
			this.rotate = false;
			
			//Hide yellow RotationArrows
			this.hideOverlay("RotationPointActive");
		}	
	}
	
	//clicking on an Edge saves the label and show the line
	if( uiObj instanceof ORYX.Core.Edge){  		
		//Identify and set the label of the current Edge	
		if(uiObj._labels[uiObj.id+"condition"]){
			this.myLabel = uiObj._labels[uiObj.id+"condition"]; //for BPMN
		}else if (uiObj._labels[uiObj.id+"name"]){
			this.myLabel = uiObj._labels[uiObj.id+"name"]; //for UML
		}else {
			this.myLabel = uiObj._labels[uiObj.id+"text"];
		}
	 
		//save the edge for adding rotationpoint	
		this.myEdge = uiObj;

		//Show the RotationPoint and line of the label of the current Edge
		if(this.myLabel && this.myLabel._text != ""){
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();
			this.showLine();			
			this.mouseCoordinates = {x:this.labelCoordinates.x, y:this.labelCoordinates.y};			
			this.showOverlay("MovingArrows", this.myEdge, this.mouseCoordinates, this.startMovingCross);
			this.showOverlay("RotationPoint", this.myEdge, this.rotationPointCoordinates, this.rotationPoint);
		}
	}
},

/**
 * Mouse Move Handler
 */
handleMouseMove: function(event, uiObj) {

	//if label is selected Posision is set to MousePosition
	if(this.labelSelected == true){
		if(this.myLabel){			
			//Set Label positon for different Rotations
			if(this.myLabel._rotate == 270 || this.myLabel._rotate == 315 || this.myLabel._rotate == 360 || this.myLabel._rotate == 45) {
				this.myLabel.x = this.facade.eventCoordinates(event).x+5;
				this.myLabel.y = this.facade.eventCoordinates(event).y-5;
				this.myLabel._rotationPoint.x = this.facade.eventCoordinates(event).x+10;
				this.myLabel._rotationPoint.y = this.facade.eventCoordinates(event).y-10;
			}else if(this.myLabel._rotate == 90 || this.myLabel._rotate == 135 || this.myLabel._rotate == 180 || this.myLabel._rotate == 225){
				this.myLabel.x = this.facade.eventCoordinates(event).x-5;
				this.myLabel.y = this.facade.eventCoordinates(event).y+5;
				this.myLabel._rotationPoint.x = this.facade.eventCoordinates(event).x-10;
				this.myLabel._rotationPoint.y = this.facade.eventCoordinates(event).y+10;
			}else {
				this.myLabel.x = this.facade.eventCoordinates(event).x+10;
				this.myLabel.y = this.facade.eventCoordinates(event).y-5;
				this.myLabel._rotationPoint.x = this.facade.eventCoordinates(event).x+10;
				this.myLabel._rotationPoint.y = this.facade.eventCoordinates(event).y-10;
			}
				
			this.myLabel.update();
		
			//refresh Coordinates
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();		

			this.hideOverlay("RotationPoint");
			this.hideOverlay("MovingArrows");
			this.hideOverlay("SettingArrows");			
			
			//refresh Position of the Overlay arrows
			if(this.myLabel._rotate == 270 || this.myLabel._rotate == 315 || this.myLabel._rotate == 360 || this.myLabel._rotate == 45) {	
				this.mouseCoordinates = {x:this.facade.eventCoordinates(event).x, y:this.facade.eventCoordinates(event).y};	
				this.showOverlay("SettingArrows", this.myEdge, this.mouseCoordinates, this.endMovingCross);
			}else {
				this.mouseCoordinates = {x:this.facade.eventCoordinates(event).x, y:this.facade.eventCoordinates(event).y};	
				this.showOverlay("SettingArrows", this.myEdge, this.mouseCoordinates, this.endMovingCross);
			}			
			//Refresh the Line
			this.hideLine();
			this.showLine();
		}
	}
	
	//perform the Statevalidation for Rotation
	if(this.rotate == true) {
		
		//save MouseCoordinates
		var MouseX = this.facade.eventCoordinates(event).x;
		var MouseY = this.facade.eventCoordinates(event).y;

		//refresh Coordinates
		this.calculateLabelCoordinates();
		this.calculateRotationPointCoordinates();
		
		//defines the States for Rotation(every 20px there is a new State)
		if(MouseX < this.rotationPointCoordinates.x-150 ){
			this.State = -8;
		}
		else if (MouseX < this.rotationPointCoordinates.x-130 && MouseX >= this.rotationPointCoordinates.x-150){
			this.State = -7;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-110 && MouseX >= this.rotationPointCoordinates.x-130){
			this.State =- 6;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-90 && MouseX >= this.rotationPointCoordinates.x-110){
			this.State = -5;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-70 && MouseX >= this.rotationPointCoordinates.x-90){
			this.State = -4;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-50 && MouseX >= this.rotationPointCoordinates.x-70){
			this.State = -3;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-30 && MouseX >= this.rotationPointCoordinates.x-50){
			this.State = -2;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-10 && MouseX >= this.rotationPointCoordinates.x-30){
			this.State = -1;
		}
		else if (MouseX < this.rotationPointCoordinates.x+10 && MouseX >= this.rotationPointCoordinates.x-10){
			this.State = 0;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+30 && MouseX >= this.rotationPointCoordinates.x+10){
			this.State = 1;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+50 && MouseX >= this.rotationPointCoordinates.x+30){
			this.State = 2;
		}
		else if (MouseX < this.rotationPointCoordinates.x+70 && MouseX >= this.rotationPointCoordinates.x+50){
			this.State = 3;
		}
		else if ( MouseX < this.rotationPointCoordinates.x+90 && MouseX >= this.rotationPointCoordinates.x+70){
			this.State = 4;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+110 && MouseX >= this.rotationPointCoordinates.x+90){
			this.State = 5;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+130 && MouseX >= this.rotationPointCoordinates.x+110){
			this.State = 6;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+150 && MouseX >= this.rotationPointCoordinates.x+130){
			this.State7;	
		}
		else if(MouseX >= this.rotationPointCoordinates.x+150){
			this.State = 8;
		}
		
		//checks the way of moving the Mouse through the states and rotate
		if(this.State > this.prevState){
			this.rotate_right();
			this.prevState = this.State;
		}
		else if(this.State < this.prevState){
			this.rotate_left();
			this.prevState = this.State;
		}
	}
},

/**
 * rotate the label to the right with 45° (clockwise)
 * 
 */
rotate_right:function() {

	var myRotPoint = {x:this.labelCoordinates.x, y:this.labelCoordinates.y};
	var myRotation = this.myLabel._rotate;
	
	if(myRotation == 0 || myRotation < 45 && myRotation > 0 || myRotation == 360){
		this.myLabel.rotate(45, myRotPoint);
	}
	else if(myRotation == 45 || myRotation < 90 && myRotation > 45){
		this.myLabel.rotate(90, myRotPoint);
	}
	else if(myRotation == 315 || myRotation > 315 && myRotation < 360){
		this.myLabel.rotate(360,myRotPoint);
	}
	else if(myRotation == 270 || myRotation > 270 && myRotation < 315){
		this.myLabel.rotate(315,myRotPoint);
	}
	else if(myRotation == 90 || myRotation < 135 && myRotation > 90){
		this.myLabel.rotate(135,myRotPoint);
	}
	else if(myRotation == 135 || myRotation < 180 && myRotation > 135){
		this.myLabel.rotate(180,myRotPoint);
	}
	else if(myRotation == 180 || myRotation < 225 && myRotation > 180){
		this.myLabel.rotate(225,myRotPoint);		
	}
	else if(myRotation == 225 || myRotation < 270 && myRotation > 225){
		this.myLabel.rotate(270,myRotPoint);
	}
	this.myLabel.update();
},

/**
 * rotate the label to the left with 45° (anticlockwise)
 * 
 */
rotate_left:function() {
	var myRotPoint = {x:this.labelCoordinates.x, y:this.labelCoordinates.y};
	var myRotation = this.myLabel._rotate;
	
	if(myRotation == 0 || myRotation < 360 && myRotation > 315 || myRotation == 360){
		this.myLabel.rotate(315,myRotPoint);
	}
	else if(myRotation == 315 || myRotation < 315 && myRotation > 270){
		this.myLabel.rotate(270,myRotPoint);
	}
	else if(myRotation == 45||myRotation <45 && myRotation >0){
		this.myLabel.rotate(360,myRotPoint);
	}
	else if(myRotation == 90||myRotation < 90 && myRotation > 45){
		this.myLabel.rotate(45,myRotPoint);
	}
	else if(myRotation == 135 || myRotation < 135 && myRotation > 90){
		this.myLabel.rotate(90,myRotPoint);
	}
	else if(myRotation == 180 || myRotation < 180 && myRotation > 135){
		this.myLabel.rotate(135,myRotPoint);
	}
	else if(myRotation == 225 || myRotation < 225 && myRotation > 180){
		this.myLabel.rotate(180,myRotPoint);
	}
	else if(myRotation == 270 || myRotation < 270 && myRotation > 225){
		this.myLabel.rotate(225,myRotPoint);
	}
	this.myLabel.update();	
},

/**
 * set the Coordinates of the RotationPoint for different degree values
 * 
 */
calculateRotationPointCoordinates: function(){	
	if(this.rotate == false) {
		this.labelLength = this.myLabel._estimateTextWidth(this.myLabel._text,14);
	
		if(this.myLabel._rotate == 360){
			this.rotationPointCoordinates.x = this.labelCoordinates.x-8 + this.labelLength/3;
			this.rotationPointCoordinates.y = this.labelCoordinates.y-35;	
		}
		else if(this.myLabel._rotate == 90) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x + 35;
			this.rotationPointCoordinates.y = this.labelCoordinates.y-8 + this.labelLength/3;			
		}
		else if(this.myLabel._rotate == 180) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x - this.labelLength/2;
			this.rotationPointCoordinates.y = this.labelCoordinates.y+35;
		}
		else if(this.myLabel._rotate == 270) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x-35;
			this.rotationPointCoordinates.y = this.labelCoordinates.y+8 - this.labelLength/2;	
		}
		else if(this.myLabel._rotate == 45) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x+40;
			this.rotationPointCoordinates.y = this.labelCoordinates.y;	
		}
		else if(this.myLabel._rotate == 135) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x;
			this.rotationPointCoordinates.y = this.labelCoordinates.y+40;	
		}
		else if(this.myLabel._rotate == 225) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x-40;
			this.rotationPointCoordinates.y = this.labelCoordinates.y;	
		}
		else if(this.myLabel._rotate == 315) {
			this.rotationPointCoordinates.x = this.labelCoordinates.x;
			this.rotationPointCoordinates.y = this.labelCoordinates.y-40;	
		}
		else {
			//Default
			this.rotationPointCoordinates.x = this.labelCoordinates.x-8 + this.labelLength/3;
			this.rotationPointCoordinates.y = this.labelCoordinates.y-35;	
		}
	}
},

/**
 * set the Coordinates of the Label
 * 
 */
calculateLabelCoordinates: function(){		
		this.labelCoordinates.x = this.myLabel.x;
		this.labelCoordinates.y = this.myLabel.y;
},

/**
 * show a Overlay with a certain id and add it to parent Shape
 * 
 * @param {String} id
 * @param {Shape} shape
 * @param {Point} position
 * @param {SVG} visualObject
 */
showOverlay: function(id, shape, position, visualObject) {
	this.facade.raiseEvent({
		type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
		id: 			id,
		shapes: 		[shape],
		node:			visualObject,
		labelPoint:		position,
		dontCloneNode:	true
	});		
},

/**
 * hide a Overlay
 * 
 * @param {String} id
 */
hideOverlay: function(id) {
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: id
	});	
},


/**
 * show the Association Line between Edge and Label
 * 
 */
showLine: function() {
	var x = this.myEdge.dockers[0].bounds.b.x-8;
	var y = this.myEdge.dockers[0].bounds.b.y-8;
	
	//Set the Position of the Line
	this.line.setAttributeNS(null, 'd', 'M'+x+' '+y+' L '+this.labelCoordinates.x+' '+this.labelCoordinates.y);

	this.facade.raiseEvent({
		type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
		id: 			"line",
		shapes: 		[this.canvasLabel],
		node:			this.line,
		position:		"northeast",
		dontCloneNode:	true
	});
	
},

/**
 * hide the Association Line between Edge and Label
 * 
 */
hideLine: function() {
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "line"
	});
}

});