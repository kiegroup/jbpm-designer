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
if(!ORYX.Core.StencilSet) {ORYX.Core.StencilSet = {};}

/**
 * Class Rules
 * uses Prototpye 1.5.0
 * uses Inheritance
 *
 * This class implements the API to check the stencil sets' rules.
 */
ORYX.Core.StencilSet.Rules = Clazz.extend({

	/**
	 * Constructor
	 */
	construct: function() {
		arguments.callee.$.construct.apply(this, arguments);

		this._stencils = [];
		
		this._cachedConnectSET = new Hash();
		this._cachedConnectTE = new Hash();
		this._cachedCardSE = new Hash();
		this._cachedCardTE = new Hash();
		this._cachedContainPC = new Hash();
		
		this._connectionRules = new Hash();
		this._cardinalityRules = new Hash();
		this._containmentRules = new Hash();
		
		this._outgoingEdgeStencilsCallbacks = new Hash();
		this._incomingEdgeStencilsCallbacks = new Hash();
		this._sourceStencilsCallbacks = new Hash();
		this._targetStencilsCallbacks = new Hash();
		this._canConnectCallbacks = new Hash();
		this._canContainCallbacks = new Hash();
	},

	initializeRules: function(stencilSet) {
		var jsonRules = stencilSet.jsonRules();
		var namespace = stencilSet.namespace();
		
		this._stencils = this._stencils.concat(stencilSet.stencils());

		//init connection rules
		var cr = this._connectionRules;
		if(jsonRules.connectionRules) {
			jsonRules.connectionRules.each((function(rules) {
				if(this._isRoleOfOtherNamespace(rules.role)) {
					if(!cr[rules.role]) {
						cr[rules.role] = new Hash();
					}
				} else {
					cr[namespace + rules.role] = new Hash();
				}
				
				rules.connects.each((function(connect) {
					var toRoles = [];
					if(connect.to) {
						if(!(connect.to instanceof Array)) {
							connect.to = [connect.to];
						}
						connect.to.each((function(to) {
							if(this._isRoleOfOtherNamespace(to)) {
								toRoles.push(to);
							} else {
								toRoles.push(namespace + to);
							}
						}).bind(this));
					} 
					
					if(this._isRoleOfOtherNamespace(rules.role)) {
						if(this._isRoleOfOtherNamespace(connect.from)) {
							cr[rules.role][connect.from] = toRoles;
						} else {
							cr[rules.role][namespace + connect.from] = toRoles;
						}	
					} else {
						if(this._isRoleOfOtherNamespace(connect.from)) {
							cr[namespace + rules.role][connect.from] = toRoles;
						} else {
							cr[namespace + rules.role][namespace + connect.from] = toRoles;
						}
					}
				}).bind(this));
			}).bind(this));
		}

		//init cardinality rules
		var cardr = this._cardinalityRules;
		if(jsonRules.cardinalityRules) {
			jsonRules.cardinalityRules.each((function(rules) {
				var cardrKey;
				if(this._isRoleOfOtherNamespace(rules.role)) {
					cardrKey = rules.role;
				} else {
					cardrKey = namespace + rules.role;
				}
		
				if(!cardr[cardrKey]) {
					cardr[cardrKey] = rules;
				}
				
				var oe = new Hash();
				if(rules.outgoingEdges) {
					rules.outgoingEdges.each((function(rule) {
						if(this._isRoleOfOtherNamespace(rule.role)) {
							oe[rule.role] = rule;
						} else {
							oe[namespace + rule.role] = rule;
						}
					}).bind(this));
				}
				cardr[cardrKey].outgoingEdges = oe;
				var ie = new Hash();
				if(rules.incomingEdges) {
					rules.incomingEdges.each((function(rule) {
						if(this._isRoleOfOtherNamespace(rule.role)) {
							ie[rule.role] = rule;
						} else {
							ie[namespace + rule.role] = rule;
						}
					}).bind(this));
				}
				cardr[cardrKey].incomingEdges = ie;
			}).bind(this));
		}
	
		//init containment rules
		var conr = this._containmentRules;
		if(jsonRules.containmentRules) {
			jsonRules.containmentRules.each((function(rules) {
				var conrKey;
				if(this._isRoleOfOtherNamespace(rules.role)) {
					conrKey = rules.role;
				} else {
					conrKey = namespace + rules.role;
				}
				if(!conr[conrKey]) {
					conr[conrKey] = [];
				}
				rules.contains.each((function(containRole) {
					if(this._isRoleOfOtherNamespace(containRole)) {
						conr[conrKey].push(containRole);
					} else {
						conr[conrKey].push(namespace + containRole);
					}
				}).bind(this));
			}).bind(this));
		}

		//init callbacks
		if(jsonRules.outgoingEdgeStencils) {
			this._outgoingEdgeStencilsCallbacks[namespace] = jsonRules.outgoingEdgeStencils;
		}
		if(jsonRules.incomingEdgeStencils) {
			this._incomingEdgeStencilsCallbacks[namespace] = jsonRules.incomingEdgeStencils;
		}
		if(jsonRules.sourceStencils) {
			this._sourceStencilsCallbacks[namespace] = jsonRules.sourceStencils;
		}
		if(jsonRules.targetStencils) {
			this._targetStencilsCallbacks[namespace] = jsonRules.targetStencils;
		}
		if(jsonRules.canConnect) {
			this._canConnectCallbacks[namespace] = jsonRules.canConnect;
		}
		if(jsonRules.canContain) {
			this._canContainCallbacks[namespace] = jsonRules.canContain;
		}

	},
	
	/**
	 * Caches the rules to increase performance at run time.
	 */
	cacheRules: function() {
		//cache connection
		this._stencils.each((function(source) {
			var edges = new Hash();
			this._cachedConnectSET[source.id()] = edges;

			this._stencils.each((function(edge) {
				var args = {
					sourceStencil:source,
					edgeStencil:edge
				};
				if(this._canConnect(args)) {

					var targets = new Hash();
					edges[edge.id()] = targets;
					
					this._stencils.each((function(target) {
						args = {
							sourceStencil:source,
							edgeStencil:edge,
							targetStencil:target
						};

						targets[target.id()] = this._canConnect(args);
					}).bind(this));
				}
			}).bind(this));
		}).bind(this));
		
		this._stencils.each((function(target) {
			var edges = new Hash();
			this._cachedConnectTE[target.id()] = edges;
			
			this._stencils.each((function(edge) {
				var args = {
					edgeStencil:edge,
					targetStencil:target
				};
				
				edges[edge.id()] = this._canConnect(args);
			}).bind(this));
		}).bind(this));

		//cache cardinality
		this._stencils.each((function(source) {
			var edges = new Hash();
			this._cachedCardSE[source.id()] = edges;
			
			this._stencils.each((function(edge) {
				var args = {
					edgeStencil:edge,
					sourceStencil:source
				};
				
				edges[edge.id()] = this._getMaximumNumberOfOutgoingEdge(args);
			}).bind(this));
		}).bind(this));
		
		this._stencils.each((function(target) {
			var edges = new Hash();
			this._cachedCardTE[target.id()] = edges;
			
			this._stencils.each((function(edge) {
				var args = {
					edgeStencil:edge,
					targetStencil:target
				};
				
				edges[edge.id()] = this._getMaximumNumberOfIncomingEdge(args);
			}).bind(this));
		}).bind(this));
		
		//cache containment
		this._stencils.each((function(parent) {
			var children = new Hash();
			this._cachedContainPC[parent.id()] = children;
			
			this._stencils.each((function(child) {
				var args = {
					containingStencil:parent,
					containedStencil:child
				};
				children[child.id()] = [this._canContain(args) ,this._getMaximumOccurrence(parent, child)];
			}).bind(this));
		}).bind(this));
	},

	/** Begin connection rules' methods */
	
	/**
	 * 
	 * @param {Object} args
	 *  sourceStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  sourceShape:   ORYX.Core.Shape | undefined
	 *  
	 *  At least sourceStencil or sourceShape has to be specified
	 *  
	 * @return {Array} Array of stencils of edges that can be outgoing edges of
	 * 				   the source.
	 */
	outgoingEdgeStencils: function(args) {
		//check arguments
		if(!args.sourceShape && !args.sourceStencil) {
			return [];
		}
		
		//init arguments
		if(args.sourceShape) {
			args.sourceStencil = args.sourceShape.getStencil();
		}
		
		var _edges = [];
		
		//test each edge, if it can connect to source
		this._stencils.each((function(stencil) {
			if(stencil.type() === "edge") {
				var newArgs = Object.clone(args);
				newArgs.edgeStencil = stencil;
				if(this.canConnect(newArgs)) {
					_edges.push(stencil);
				}
			}
		}).bind(this));

		return _edges;
	},

	/**
	 * 
	 * @param {Object} args
	 *  targetStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  targetShape:   ORYX.Core.Shape | undefined
	 *  
	 *  At least targetStencil or targetShape has to be specified
	 *  
	 * @return {Array} Array of stencils of edges that can be incoming edges of
	 * 				   the target.
	 */
	incomingEdgeStencils: function(args) {
		//check arguments
		if(!args.targetShape && !args.targetStencil) {
			return [];
		}
		
		//init arguments
		if(args.targetShape) {
			args.targetStencil = args.targetShape.getStencil();
		}
		
		var _edges = [];
		
		//test each edge, if it can connect to source
		this._stencils.each((function(stencil) {
			if(stencil.type() === "edge") {
				var newArgs = Object.clone(args);
				newArgs.edgeStencil = stencil;
				if(this.canConnect(newArgs)) {
					_edges.push(stencil);
				}
			}
		}).bind(this));

		return _edges;
	},
	
	/**
	 * 
	 * @param {Object} args
	 *  edgeStencil:   ORYX.Core.StencilSet.Stencil | undefined
	 *  edgeShape:     ORYX.Core.Edge | undefined
	 *  targetStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  targetShape:   ORYX.Core.Node | undefined
	 *  
	 *  At least edgeStencil or edgeShape has to be specified!!!
	 *  
	 *  @return {Array} Returns an array of stencils that can be source of 
	 *  				the specified edge.
	 */
	sourceStencils: function(args) {
		//check arguments
		if(!args || 
		   !args.edgeShape && !args.edgeStencil) {
			return [];
		}
		
		//init arguments
		if(args.targetShape) {
			args.targetStencil = args.targetShape.getStencil();
		}
		
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}
		
		var _sources = [];
		
		//check each stencil, if it can be a source
		this._stencils.each((function(stencil) {
			var newArgs = Object.clone(args);
			newArgs.sourceStencil = stencil;
			if(this.canConnect(newArgs)) {
				_sources.push(stencil);
			}
		}).bind(this));

		return _sources;
	},
	
	/**
	 * 
	 * @param {Object} args
	 *  edgeStencil:   ORYX.Core.StencilSet.Stencil | undefined
	 *  edgeShape:     ORYX.Core.Edge | undefined
	 *  sourceStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  sourceShape:   ORYX.Core.Node | undefined
	 *  
	 *  At least edgeStencil or edgeShape has to be specified!!!
	 *  
	 *  @return {Array} Returns an array of stencils that can be target of 
	 *  				the specified edge.
	 */
	targetStencils: function(args) {
		//check arguments
		if(!args || 
		   !args.edgeShape && !args.edgeStencil) {
			return [];
		}
		
		//init arguments
		if(args.sourceShape) {
			args.sourceStencil = args.sourceShape.getStencil();
		}
		
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}
		
		var _targets = [];
		
		//check stencil, if it can be a target
		this._stencils.each((function(stencil) {
			var newArgs = Object.clone(args);
			newArgs.targetStencil = stencil;
			if(this.canConnect(newArgs)) {
				_targets.push(stencil);
			}
		}).bind(this));

		return _targets;
	},

	/**
	 * 
	 * @param {Object} args
	 *  edgeStencil:   ORYX.Core.StencilSet.Stencil
	 *  edgeShape:     ORYX.Core.Edge |undefined
	 *  sourceStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  sourceShape:   ORYX.Core.Node |undefined
	 *  targetStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  targetShape:   ORYX.Core.Node |undefined
	 *  
	 *  At least source or target has to be specified!!!
	 *  
	 *  @return {Boolean} Returns, if the edge can connect source and target.
	 */
	canConnect: function(args) {	
		//check arguments
		if(!args ||
		   (!args.sourceShape && !args.sourceStencil &&
		    !args.targetShape && !args.targetStencil) ||
		    !args.edgeShape && !args.edgeStencil) {
		   	return false; 
		}
		
		//init arguments
		if(args.sourceShape) {
			args.sourceStencil = args.sourceShape.getStencil();
		}
		if(args.targetShape) {
			args.targetStencil = args.targetShape.getStencil();
		}
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}
		
		var result;
		
		if(args.sourceStencil) {
			var source = this._cachedConnectSET[args.sourceStencil.id()];
			
			var edge = source[args.edgeStencil.id()];
			
			if(!edge)
				result = false;
			else if(!args.targetStencil)
				result = true;
			else {	
				var target = edge[args.targetStencil.id()];
				
				result = (target) ? true : false;
			}
		} else { //targetStencil && !sourceStencil
			var target = this._cachedConnectTE[args.targetStencil.id()];
			
			var edge = target[args.edgeStencil.id()];
			
			result = (edge) ? true : false;
		}	
		
		//check cardinality
		if (result) {
			if(args.sourceShape) {
				var source = this._cachedCardSE[args.sourceStencil.id()];
				var max = source[args.edgeStencil.id()];
				if(max) {
					result = args.sourceShape.getOutgoingShapes().all(function(cs) {
								if((cs.getStencil().id() === args.edgeStencil.id()) && 
								   ((args.edgeShape) ? cs !== args.edgeShape : true)) {
									max--;
									return (max > 0) ? true : false;
								} else {
									return true;
								}
							});
				}
			} else if (args.targetShape) {
				var target = this._cachedCardSE[args.targetStencil.id()];
				var max = target[args.edgeStencil.id()];
				if (max) {
					result = args.targetShape.getIncomingShapes().all(function(cs){
								if ((cs.getStencil().id() === args.edgeStencil.id()) &&
								((args.edgeShape) ? cs !== args.edgeShape : true)) {
									max--;
									return (max > 0) ? true : false;
								}
								else {
									return true;
								}
							});
				}
			}
		} 	
		
		return result;
	},
	
	/**
	 * 
	 * @param {Object} args
	 *  edgeStencil:   ORYX.Core.StencilSet.Stencil
	 *  edgeShape:     ORYX.Core.Edge |undefined
	 *  sourceStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  sourceShape:   ORYX.Core.Node |undefined
	 *  targetStencil: ORYX.Core.StencilSet.Stencil | undefined
	 *  targetShape:   ORYX.Core.Node |undefined
	 *  
	 *  At least source or target has to be specified!!!
	 *  
	 *  @return {Boolean} Returns, if the edge can connect source and target.
	 */
	_canConnect: function(args) {
		//check arguments
		if(!args ||
		   (!args.sourceShape && !args.sourceStencil &&
		    !args.targetShape && !args.targetStencil) ||
		    !args.edgeShape && !args.edgeStencil) {
		   	return false; 
		}
		
		//init arguments
		if(args.sourceShape) {
			args.sourceStencil = args.sourceShape.getStencil();
		}
		if(args.targetShape) {
			args.targetStencil = args.targetShape.getStencil();
		}
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}

		//1. check connection rules
		var resultCR;
		
		//get all connection rules for this edge
		var edgeRules = this._getConnectionRulesOfEdgeStencil(args.edgeStencil);

		//check connection rules, if the source can be connected to the target 
		// with the specified edge.
		if(edgeRules.keys().length === 0) {
			resultCR = false;
		} else {
			if(args.sourceStencil) {
				resultCR = args.sourceStencil.roles().any(function(sourceRole) {
					var targetRoles = edgeRules[sourceRole];

					if(!targetRoles) {return false;}
		
					if(args.targetStencil) {
						return (targetRoles.any(function(targetRole) {
							return args.targetStencil.roles().member(targetRole);
						}));
					} else {
						return true;
					}
				});
			} else { //!args.sourceStencil -> there is args.targetStencil
				resultCR = edgeRules.values().any(function(targetRoles) {
					return args.targetStencil.roles().any(function(targetRole) {
						return targetRoles.member(targetRole);
					});
				});
			}
		}
		
		return resultCR;
		
/*		var resultCardSrc = true;
		var resultCardTgt = true;
		if(args.edgeStencil) {
			if(args.sourceShape) {
				//2. check if source's maximum number of outgoing edges of this type is reached
				resultCardSrc = !this._isMaximumNumberOfOutgoingEdgesReached(args);
			}
			if(args.targetShape) {
				//3. check if target's maximum number of incoming edges of this type is reached
				resultCardTgt = !this._isMaximumNumberOfIncomingEdgesReached(args);
			}
		}

		//all tests have to evaluate to true to validate the connection
		var result = resultCR && resultCardSrc && resultCardTgt;
		
		//THE canConnect CALLBACK IS DEACTIVATED BECAUSE OF PERFORMANCE PROBLEMS!!!
		/*if(result) {
			//4. ask the stencil sets, but only if the previous result is true.
			//if one callback returns false, no other callback will be called
			result = this._canConnectCallbacks.values().all(function(cb) {
				try {
					var newArgs = Object.clone(args);
					newArgs.result = result;
					return cb(newArgs);
				} catch (e) {return true;}
			}); 
		} * /
		return result;
*/
	},

	/** End connection rules' methods */


	/** Begin containment rules' methods */

	/**
	 * 
	 * @param {Object} args
	 *  containingStencil: ORYX.Core.StencilSet.Stencil
	 *  containingShape:   ORYX.Core.AbstractShape
	 *  containedStencil:  ORYX.Core.StencilSet.Stencil
	 *  containedShape:    ORYX.Core.Shape
	 */
	canContain: function(args) {
		if(!args ||
		   !args.containingStencil && !args.containingShape ||
		   !args.containedStencil && !args.containedShape) {
		   	return false;
		}
		
		//init arguments
		if(args.containedShape) {
			args.containedStencil = args.containedShape.getStencil();
		}
		
		if(args.containingShape) {
			args.containingStencil = args.containingShape.getStencil();
		}
		
		if(args.containingStencil.type() == 'edge' || args.containedStencil.type() == 'edge')
			return false;
		
		var parent = this._cachedContainPC[args.containingStencil.id()];
		
		var childValues = parent[args.containedStencil.id()];
		
		if(!childValues[0])
			return false;
		else if (!childValues[1])
			return true;
		else {
			if(args.containingShape) {
				var max = childValues[1];
				return args.containingShape.getChildShapes(false).all(function(as) {
					if(as.getStencil().id() === containedStencil.id()) {
						max--;
						return (max > 0) ? true : false;
					} else {
						return true;
					}
				});
			}
		}
	},
	
	/**
	 * 
	 * @param {Object} args
	 *  containingStencil: ORYX.Core.StencilSet.Stencil
	 *  containingShape:   ORYX.Core.AbstractShape
	 *  containedStencil:  ORYX.Core.StencilSet.Stencil
	 *  containedShape:    ORYX.Core.Shape
	 */
	_canContain: function(args) {
		if(!args ||
		   !args.containingStencil && !args.containingShape ||
		   !args.containedStencil && !args.containedShape) {
		   	return false;
		}
		
		//init arguments
		if(args.containedShape) {
			args.containedStencil = args.containedShape.getStencil();
		}
		
		if(args.containingShape) {
			args.containingStencil = args.containingShape.getStencil();
		}
		
		if(args.containingShape) {
			if(args.containingShape instanceof ORYX.Core.Edge) {
				//edges cannot contain other shapes
				return false;
			} /*else {
				//check maximum occurrence of the contained stencil in the containing shape
				if(!(this._isMaximumOccurrenceReached(args.containingShape, args.containedStencil))) {
					return false;
				}
			}*/
		}

		
		var result;
		
		//check containment rules
		result = args.containingStencil.roles().any((function(role) {
			var roles = this._containmentRules[role];
			if(roles) {
				return roles.any(function(role) {
					return args.containedStencil.roles().member(role);
				});
			} else {
				return false;
			}
		}).bind(this));

/*		if(result) {
			//ask the stencil sets, if the previous result is true
			//if one callback returns false, no other callback will be called
			result = this._canContainCallbacks.values().all(function(cb) {
				try {
					var newArgs = Object.clone(args);
					newArgs.result = result;
					return cb(newArgs);
				} catch (e) {return true;}
			}); 
		}
*/
		
		return result;
	},
	
	/** End containment rules' methods */


	/** Helper methods */

	/**
	 * 
	 * @param {String} role
	 * 
	 * @return {Array} Returns an array of stencils that can act as role.
	 */
	_stencilsWithRole: function(role) {
		return this._stencils.findAll(function(stencil) {
			return (stencil.roles().member(role)) ? true : false;
		});
	},
	
	/**
	 * 
	 * @param {String} role
	 * 
	 * @return {Array} Returns an array of stencils that can act as role and
	 * 				   have the type 'edge'.
	 */
	_edgesWithRole: function(role) {
		return this._stencils.findAll(function(stencil) {
			return (stencil.roles().member(role) && stencil.type() === "edge") ? true : false;
		});
	},
	
	/**
	 * 
	 * @param {String} role
	 * 
	 * @return {Array} Returns an array of stencils that can act as role and
	 * 				   have the type 'node'.
	 */
	_nodesWithRole: function(role) {
		return this._stencils.findAll(function(stencil) {
			return (stencil.roles().member(role) && stencil.type() === "node") ? true : false;
		});
	},

	/**
	 * 
	 * @param {ORYX.Core.StencilSet.Stencil} parent
	 * @param {ORYX.Core.StencilSet.Stencil} child
	 * 
	 * @returns {Boolean} Returns the maximum occurrence of shapes of the 
	 * 					  stencil's type inside the parent.
	 */
	_getMaximumOccurrence: function(parent, child) {
		var max;
		child.roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];
			if(cardRule && cardRule.maximumOccurrence) {
				if(max) {
					max = Math.min(max, cardRule.maximumOccurrence);
				} else {
					max = cardRule.maximumOccurrence;
				}
			}
		}).bind(this));
		
		return max;
	},
		
	/**
	 * 
	 * @param {ORYX.Core.AbstractShape} parent
	 * @param {ORYX.Core.StencilSet.Stencil} stencil
	 * 
	 * @returns {Boolean} Returns, if the maximum occurrence of shapes of the 
	 * 					  stencil's type inside the parent is reached.
	 */
	/*_isMaximumOccurrenceReached: function(parent, stencil) {
		var max;
		stencil.roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];
			if(cardRule && cardRule.maximumOccurrence) {
				if(max) {
					max = Math.min(max, cardRule.maximumOccurrence);
				} else {
					max = cardRule.maximumOccurrence;
				}
			}
		}).bind(this));
		
		if(max) {
			return parent.getChildShapes(false).all(function(as) {
				if(as.getStencil().id() === stencil.id()) {
					max--;
					return (max > 0) ? true : false;
				} else {
					return true;
				}
			});
		} else {
			return true;
		}
		
		return max;
	},*/

	/**
	 * 
	 * @param {Object} args
	 *  sourceStencil: ORYX.Core.Node
	 *  edgeStencil: ORYX.Core.StencilSet.Stencil
	 *  
	 *  @return {Boolean} Returns, the maximum number of outgoing edges of 
	 *  				  the type specified by edgeStencil of the sourceShape.
	 */
	_getMaximumNumberOfOutgoingEdge: function(args) {
		if(!args ||
		   !args.sourceStencil ||
		   !args.edgeStencil) {
		   	return false;
		}
		
		var max;
		args.sourceStencil.roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];

			if(cardRule && cardRule.outgoingEdges) {
				args.edgeStencil.roles().each(function(edgeRole) {
					var oe = cardRule.outgoingEdges[edgeRole];

					if(oe && oe.maximum) {
						if(max) {
							max = Math.min(max, oe.maximum);
						} else {
							max = oe.maximum;
						}
					}
				});
			}
		}).bind(this));

		return max;
	},
	
	/**
	 * 
	 * @param {Object} args
	 *  targetStencil: ORYX.Core.StencilSet.Stencil
	 *  edgeStencil: ORYX.Core.StencilSet.Stencil
	 *  
	 *  @return {Boolean} Returns the maximum number of incoming edges of 
	 *  				  the type specified by edgeStencil of the targetShape.
	 */
	_getMaximumNumberOfIncomingEdge: function(args) {
		if(!args ||
		   !args.targetStencil ||
		   !args.edgeStencil) {
		   	return false;
		}
		
		var max;
		args.targetStencil.roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];
			if(cardRule && cardRule.incomingEdges) {
				args.edgeStencil.roles().each(function(edgeRole) {
					var ie = cardRule.incomingEdges[edgeRole];
					if(ie && ie.maximum) {
						if(max) {
							max = Math.min(max, ie.maximum);
						} else {
							max = ie.maximum;
						}
					}
				});
			}
		}).bind(this));

		return max;
	},
		
	/**
	 * 
	 * @param {Object} args
	 *  sourceShape: ORYX.Core.Node
	 *  edgeStencil: ORYX.Core.StencilSet.Stencil
	 *  edgeShape:   ORYX.Core.Edge | undefined
	 *  
	 *  @return {Boolean} Returns, if the maximum number of outgoing edges of 
	 *  				  the type specified by edgeStencil of the sourceShape 
	 *  				  is reached.
	 */
	/*_isMaximumNumberOfOutgoingEdgesReached: function(args) {
		if(!args ||
		   !args.sourceShape ||
		   !args.edgeShape && !args.edgeStencil) {
		   	return false;
		}
		
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}
		
		var max;
		args.sourceShape.getStencil().roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];

			if(cardRule && cardRule.outgoingEdges) {
				args.edgeStencil.roles().each(function(edgeRole) {
					var oe = cardRule.outgoingEdges[edgeRole];

					if(oe && oe.maximum) {
						if(max) {
							max = Math.min(max, oe.maximum);
						} else {
							max = oe.maximum;
						}
					}
				});
			}
		}).bind(this));

		if(max) {
			return !args.sourceShape.getOutgoingShapes().all(function(cs) {
				if((cs.getStencil().id() === args.edgeStencil.id()) && 
				   ((args.edgeShape) ? cs !== args.edgeShape : true)) {
					max--;
					return (max > 0) ? true : false;
				} else {
					return true;
				}
			}); 
		} else {
			return false;
		}
	},*/
	
	/**
	 * 
	 * @param {Object} args
	 *  targetShape: ORYX.Core.Node
	 *  edgeStencil: ORYX.Core.StencilSet.Stencil
	 *  edgeShape:   ORYX.Core.Edge | undefined
	 *  
	 *  @return {Boolean} Returns, if the maximum number of incoming edges of 
	 *  				  the type specified by edgeStencil of the targetShape 
	 *  				  is reached.
	 */
	/*_isMaximumNumberOfIncomingEdgesReached: function(args) {
		if(!args ||
		   !args.targetShape ||
		   !args.edgeShape && !args.edgeStencil) {
		   	return false;
		}
		
		if(args.edgeShape) {
			args.edgeStencil = args.edgeShape.getStencil();
		}
		
		var max;
		args.targetShape.getStencil().roles().each((function(role) {
			var cardRule = this._cardinalityRules[role];
			if(cardRule && cardRule.incomingEdges) {
				args.edgeStencil.roles().each(function(edgeRole) {
					var ie = cardRule.incomingEdges[edgeRole];
					if(ie && ie.maximum) {
						if(max) {
							max = Math.min(max, ie.maximum);
						} else {
							max = ie.maximum;
						}
					}
				});
			}
		}).bind(this));

		if(max) {
			return !args.targetShape.getIncomingShapes().all(function(cs) {
				if((cs.getStencil().id() === args.edgeStencil.id()) && 
				   ((args.edgeShape) ? cs !== args.edgeShape : true)) {
					max--;
					return (max > 0) ? true : false;
				} else {
					return true;
				}
			}); 
		} else {
			return false;
		}
	},*/
	
	/**
	 * 
	 * @param {ORYX.Core.StencilSet.Stencil} edgeStencil
	 * 
	 * @return {Hash} Returns a hash map of all connection rules for edgeStencil.
	 */
	_getConnectionRulesOfEdgeStencil: function(edgeStencil) {
		var edgeRules = new Hash();
		edgeStencil.roles().each((function(role) {
			if(this._connectionRules[role]) {
				this._connectionRules[role].each(function(cr) {
					if(edgeRules[cr.key]) {
						edgeRules[cr.key] = edgeRules[cr.key].concat(cr.value);
					} else {
						edgeRules[cr.key] = cr.value;
					}
				});
			}
		}).bind(this));
		
		return edgeRules;
	},
	
	_isRoleOfOtherNamespace: function(role) {
		return (role.indexOf("#") > 0);
	},

	toString: function() { return "Rules"; }
});