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
 * Init namespace
 */
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.StencilSet) {ORYX.Core.StencilSet = {};}

/**
 * This class represents a stencil set. It offers methods for accessing
 *  the attributes of the stencil set description JSON file and the stencil set's
 *  stencils.
 */
ORYX.Core.StencilSet.StencilSet = Clazz.extend({

	/**
	 * Constructor
	 * @param source {URL} A reference to the stencil set specification.
	 *
	 */
	construct: function(source) {
		arguments.callee.$.construct.apply(this, arguments);

		if(!source) {throw "ORYX.Core.StencilSet.StencilSet(construct): Parameter 'source' is not defined."; }
		
		if(source.endsWith("/")) {
			source = source.substr(0, source.length-1);
		}

		this._source = source;
		this._baseUrl = source.substring(0, source.lastIndexOf("/")+1);
		
		this._jsonObject;

		this._stencils = new Hash();

		new Ajax.Request(source, {asynchronous:false, method:'get', onSuccess:this._init.bind(this), onFailure:this._cancelInit.bind(this)});
	},
	
	/**
	 * @param {ORYX.Core.StencilSet.StencilSet} stencilSet
	 * @return {Boolean} True, if stencil set has the same namespace.
	 */
	equals: function(stencilSet) {
		return (this.namespace() === stencilSet.namespace());
	},

	stencils: function() {
		return this._stencils.values();
	},

	nodes: function() {
		return this._stencils.values().findAll(function(stencil) {			return (stencil.type() === 'node')		});
	},

	edges: function() {
		return this._stencils.values().findAll(function(stencil) {			return (stencil.type() === 'edge')		});
	},

	stencil: function(id) {
		return this._stencils[id];
	},

	title: function() {
		return this._jsonObject.title;
	},

	description: function() {
		return this._jsonObject.description;
	},

	namespace: function() {
		return this._jsonObject.namespace;
	},
	
	jsonRules: function() {
		return this._jsonObject.rules;
	},
	
	source: function() {
		return this._source;
	},

	_init: function(response) {
		//CALLING EVAL, because prototype's paresJSON method does not work, 
		// if the JSON file contains functions as values.
		//this._jsonObject = response.responseText.parseJSON(true);
		try {
			eval("this._jsonObject =" + response.responseText);
		} catch (e) {
			throw "ORYX.Core.StencilSet.StencilSet(_init): Response is not a JSON Object.";
		}
		
		if(!this._jsonObject) { throw "ORYX.Core.StencilSet.StencilSet(_init): Response is not a JSON object.";}
				
		if(!this._jsonObject.namespace ||  
		   this._jsonObject.namespace === "") {
			throw "ORYX.Core.StencilSet.StencilSet(_init): Namespace of stencil set is not defined.";
		}
		
		if(!this._jsonObject.namespace.endsWith("#")) {
			this._jsonObject.namespace = this._jsonObject.namespace + "#";
		}
		
		if(!this._jsonObject.title) { this._jsonObject.title = ""; }
		
		if(!this._jsonObject.description) { this._jsonObject.description = ""; }
			
		//init stencils
		if(this._jsonObject.stencils && this._jsonObject.stencils instanceof Array) {
			$A(this._jsonObject.stencils).each((function(stencil) {
				var oStencil = new ORYX.Core.StencilSet.Stencil(stencil, this.namespace(), this._baseUrl, this);
				this._stencils[oStencil.id()] = oStencil;
			}).bind(this));
		}
	},

	_cancelInit: function(response) {
		throw "ORYX.Core.StencilSet.StencilSet(_cancelInit): Requesting stencil set file failed.";	},

	toString: function() { return "StencilSet " + this.title() + " (" + this.namespace() + ")"; }
});