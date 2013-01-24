/**
 * Copyright (c) 2008-2009
 * Kerstin Pfitzner, Oliver Kopp, Sven Wagner-Boysen
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

/**
 * This plugin offers the serialize callbacks used in the BPMNplus stencil set.
 * 
 * @class ORYX.Plugins.BPMNPlus
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
ORYX.Plugins.BPMNPlusSerialization = {
	/** @lends ORYX.Plugins.BPMNPlusLayout.prototype */
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade){
		this.facade = facade;
		
		this.facade.registerOnEvent("serialize.bpmnplus.pool", 
							this.handleSerializePool.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.variable", 
							this.handleSerializeVariable.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.dataobject", 
							this.handleSerializeDataObject.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.attachedevent", 
							this.handleSerializeAttachedEvent.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.unidirectedassociation", 
							this.handleSerializeUnidirectedAssociation.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.directedassociation", 
							this.handleSerializeDirectedAssociation.bind(this));
		this.facade.registerOnEvent("serialize.bpmnplus.messageflow", 
							this.handleSerializeMessageFlow.bind(this));					
	},
	
	/**
	 * Handler to serialize the BPMN+ pool
	 * 
	 * @param {Object} event
	 * 		The serialize event
	 */
	handleSerializePool: function(event) {
		var shape = event.shape;
		var data = event.data;
		
		var poolId = shape.resourceId;
		var processId = shape.properties["oryx-processid"];
		if (processId == "") {
			processId = poolId + "_process";
			var processRec;
			for (var i = 0; i < data.length; i++) {
				if (data[i].name == "processid") {
					processRec = data[i];
					break;
				}
			}
			processRec.value=processId;
		}
		
		event.result = data;
	},
	
	/**
	 * The handler to serialize a bpmnplus variable
	 * 
	 * @param {Object} event
	 * 		The serialization event to handle
	 */
	handleSerializeVariable: function(event) {
		var shape = event.shape;
		var data = event.data;
		
		// serialize pool, poolSet, process and subProcess
		var parent = shape.getParentShape();
		var subProcess = false;
		while(parent.getParentShape != undefined) {
			if ((parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#Scope") || 
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#FaultHandler") ||
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#CompensationHandler") ||
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#TerminationHandler") ||
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#MessageHandler") ||
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#TimerHandler")) {
				// determine oryx-subprocess
				var id = parent.resourceId;
				if (id == "") {
					id = parent.resourceId;
					if (id == undefined) {
						id = "";
					}
				}
				if (!subProcess) {
					data.push({
						name:"subprocess",
					 	prefix:"oryx",				 
					 	value:id,
						type:"literal"
					});										
					subProcess = true;
				}
				parent = parent.getParentShape();					
			} else if (	(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#Pool") ||
						(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#PoolSet")) {
				// generate pool and process
				var name;
				if (parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#Pool") {
					name = "pool";
				} else {
					name = "poolset";
				}
				
				var poolId = parent.resourceId;
				if (poolId == "") {
					poolId = parent.resourceId;
					if (poolId == undefined) {
						poolId = "";
					}
				}
				
				data.push({
					name:name,
				 	prefix:"oryx",				 
				 	value:poolId,
					type:"literal"
				});
						
				if (!subProcess) {
					var processId = parent.properties["oryx-processid"];
					if (processId == "") {
						processId = poolId + "_process";
					}
					data.push({
						name:"process",
					 	prefix:"oryx",				 
					 	value:processId,
						type:"literal"
					});
				}
										
				break;
			} else {
				parent = parent.getParentShape();
			}
		}
		
		event.result =  data;
	},
	
	/**
	 * The handle to serialize any kind of data object
	 * 
	 * @param {Object} event
	 * 		The serialization event
	 */
	handleSerializeDataObject : function(event) {
		var shape = event.shape;
		var data = event.data;
		
		
		// serialize pool or poolSet
		var parent = shape.getParentShape();		
		while(parent.getParentShape != undefined) {
			if ((parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#Pool") ||
				(parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#PoolSet")) {
				// generate pool and process
				var name;
				if (parent.getStencil().id() == "http://b3mn.org/stencilset/bpmnplus#Pool") {
					name = "pool";
				} else {
					name = "poolset";
				}
				
				var poolId = parent.resourceId;
				if (poolId == "") {
					poolId = parent.resourceId;
					if (poolId == undefined) {
						poolId = "";
					}
				}
				
				data.push({
					name:name,
				 	prefix:"oryx",				 
				 	value:poolId,
					type:"literal"
				});													
										
				break;
			} else {
				parent = parent.getParentShape();
			}
		}
		
		event.result = data;
	},
	
	/**
	 * The handler to serialize attached events
	 * 
	 * @param {Object} event
	 * 		The serialization event
	 */
	handleSerializeAttachedEvent : function(event) {
		var shape = event.shape;
		var data = event.data;
		
		var attached;
		var incomingShapes = shape.getIncomingShapes();
		incomingShapes.each(function(next) { 
			var roles = next.getStencil().roles();						
			for (var i = 0; i < roles.length; i++) {
				if (roles[i] == next.getStencil().namespace() + "attachmentAllowed") {
					attached = next;
					break;
				}
			}
		});

		if (attached != undefined) {
			var attachedId = attached.resourceId;
			if (attachedId == "") {
				attachedId = attached.resourceId;
			}
			if (attachedId != undefined) {
				data.push({
					name:"target",
				 	prefix:"oryx",				 
				 	value:attachedId,
			 		type:"literal"
				});
			}
	 	}
		
		event.result = data;
	},
	
	/**
	 * The handler to serialize an unidirected association
	 * 
	 * @param {Object} event
	 * 		The serialization event
	 */
	handleSerializeUnidirectedAssociation : function(event) {
		var shape = event.shape;
		var data = event.data;
		
		var sources = shape.getIncomingShapes();
		var switched = false;
		if (sources.length > 0) {
			var source = sources[0];
			// determine oryx-source
			var type = source.getStencil().id();
			var id = source.resourceId;
			var name;
			if (type == "http://b3mn.org/stencilset/bpmnplus#StandardVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#FaultVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#MessageVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#CounterVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#ParticipantReferenceDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#ParticipantSetDataObject") {
				name = "target";
				switched = true;
			} else {
				name = "source";
			}
			data.push({
					name:"direction",
				 	prefix:"oryx",
				 	value:"None",
				 	type:"literal",
				});
			
			if (id != undefined) {
				data.push({
						name:name,
					 	prefix:"oryx",
					 	value:id,
					 	type:"literal"
				});
			}
		}

		// determine oryx-target
		var targets = shape.getOutgoingShapes();
		if (targets.length > 0) {
			var target = targets[0];
			var id = target.resourceId;
			var name;
			if (switched) {
				name = "source";
			} else {
				name = "target";
			}

			if (id != undefined) {
				data.push({
					name:name,
				 	prefix:"oryx",
				 	value:id,
				 	type:"literal"
				});
			}
		}
		
		event.result = data;
	},
	
	handleSerializeDirectedAssociation : function(event) {
		var shape = event.shape;
		var data = event.data;
		
		
		var sources = shape.getIncomingShapes();
		var switched = false;
		if (sources.length > 0) {
			var source = sources[0];
			// determine oryx-direction and oryx-source
			var type= source.getStencil().id();
			var id = source.resourceId;
			var name;
			if (type == "http://b3mn.org/stencilset/bpmnplus#StandardVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#FaultVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#MessageVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#CounterVariableDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#ParticipantReferenceDataObject" ||
				type == "http://b3mn.org/stencilset/bpmnplus#ParticipantSetDataObject") {
				data.push({
					name:"direction",
				 	prefix:"oryx",
				 	value:"To",
				 	type:"literal"
				});
				name = "target";
				switched = true;
			} else {
				data.push({
					name:"direction",
				 	prefix:"oryx",
				 	value:"From",
				 	type:"literal"
				});
				name="source";
			}
			if (id != undefined) {
				data.push({
						name:name,
					 	prefix:"oryx",
					 	value:id,
					 	type:"literal"
				});
			}
		}											
		
		// determine oryx-target
		var targets = shape.getOutgoingShapes();
		if (targets.length > 0) {
			var target = targets[0];
			var id = target.resourceId;
			if (id == "") {
				id = target.resourceId;
			}
			var name;
			if (switched) {
				name = "source";
			} else {
				name = "target";
			}
			if (id != undefined) {
				data.push({
					name:name,
				 	prefix:"oryx",
				 	value:id,
				 	type:"literal"
				});
			}
		}
		
		event.result = data;
	},
	
	/**
	 * The handler to serialize a message flow
	 * 
	 * @param {Object} event
	 * 		The serialization event
	 */
	handleSerializeMessageFlow: function(event) {
		var shape = event.shape;
		var data = event.data;
		
		var sources = shape.getIncomingShapes();
		if (sources.length > 0) {
			var source = sources[0];
			var id = source.resourceId;
			if (id != undefined) {
				data.push({
					name:"source",
				 	prefix:"oryx",
				 	value:id,
				 	type:"literal"
				});
			}
		}
		
		var targets = shape.getOutgoingShapes();
		if (targets.length > 0) {
			var target = targets[0];
			var id = target.resourceId;
			if (id != undefined) {
				data.push({
					name:"target",
				 	prefix:"oryx",
				 	value:id,
				 	type:"literal"
				});
			}
		}
		
		event.result = data;
	}
};

ORYX.Plugins.BPMNPlusSerialization = Clazz.extend(ORYX.Plugins.BPMNPlusSerialization);