/**
 * Copyright (c) 2008
 * Bj�rn Wagner, Sven Wagner-Boysen
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

// set namespace

if(!Repository) var Repository = {};
if(!Repository.Core) Repository.Core = {};

Repository.Core.DataCache = {
		
	oryxUrl : '/oryx',
	stencilsetUrl : '/stencilsets',

	construct : function(models) {
		
		// Stores the id of all models available to the user as key and their uri as value
		this._models = new Hash(models); 
		// Stores cache type as key and the corresponding hash as value
		this._data = new Hash();
	
		
		this._addHandler = new EventHandler();
		this._updateHandler = new EventHandler();
		this._removeHandler = new EventHandler();
		
		// stores meta data of available modeltypes
		this.model_types = [];	
		// Stores the id of all models available to the user as key and their uri as value
		this._models = new Hash(models); 
		// Stores cache type as key and the corresponding hash as value
		this._data = new Hash();
	
		
		this._addHandler = new EventHandler();
		this._updateHandler = new EventHandler();
		this._removeHandler = new EventHandler();
		
		
		
	},
	
	addModel : function(id, uri) {
		this._models.set(id, uri);
	},
	
	getAddHandler : function() {return this._addHandler;},
	getUpdateHandler : function() {return this._updateHandler;},
	getRemoveHandler : function() {return this._removeHandler;},
	
	getModelUri : function(modelId) {
		return this._models.get(modelId);
	},
	
	getDataAsync : function(fetchDataUri, id, callback) {
		var modelIds = $A(id); // Ensure that ids is an array
		var cacheMisses = []; // Stores ids of models that aren't cached
		if (this._data.get(fetchDataUri)) {
			// Check if all models 
			modelIds.each(function(modelId) {
				var data = this._data.get(fetchDataUri).get(modelId); // Read value from data hash
				// data isn't cached
				if (data == undefined) {
					cacheMisses.push(modelId);
				}				
			}.bind(this));
		} else {
			cacheMisses = modelIds.clone(); // load all if nothing was loaded before
			this._data.set(fetchDataUri, new Hash());
		}
		// All model data is cached, no server request necessary
		if (cacheMisses.length == 0) {
			result = new Hash();
			modelIds.each(function(id) {
				result.set(id, this._data.get(fetchDataUri).get(id));
			}.bind(this));
			callback(result);
			return;
		}
		// Build query object
		var query = {};
		query.id = id;
		query.fetchDataUri = fetchDataUri;
		query.callback = callback;
		query.updateMethod = this.updateObject.bind(this);
		query.allIds = modelIds;
		query.missingIds = cacheMisses;
		query.cachedData = this._data.get(fetchDataUri);
		
		cacheMisses.each(function(modelId) {
			// Remove leading slash from model uri
			var requestUrl = this._models.get(modelId).substring(1) +  fetchDataUri // + "?id=" + id;
			Ext.Ajax.request({url : requestUrl,  success : this.getDefaultReturnHandler(query) });
		}.bind(this));
	},
	
	updateObject : function(fetchDataUri, id, data) {
		if (!this._data.get(fetchDataUri)) {
			this._data.set(fetchDataUri, new Hash())
		}
		this._data.get(fetchDataUri).set(id, data);
		this._updateHandler.invoke(id);
	},
	
	getDefaultReturnHandler : function(binding) { 
		// Returns function which handles the Ext.Ajax.Request success response
		// The function has to be in a query context
		return function(response, options) {
			// Decode JSON
			var returnedData = new Hash(Ext.util.JSON.decode(response.responseText));
			// Server returns JSON object. model id is the key and the data the value of the object,
			returnedData.each(function(pair) {
				this.missingIds = this.missingIds.without(pair.key);
				this.updateMethod(this.fetchDataUri, pair.key, pair.value);
			}.bind(this));
			// Everything returned from server
			if (this.missingIds.length == 0) {
				queriedData = new Hash()
				this.allIds.each(function (modelId){
					queriedData.set(modelId, this.cachedData.get(modelId)); // Write data to output hash
				}.bind(this));
				this.callback(queriedData); 
			}
		}.bind(binding);
	},
	
	getIds : function() {
		// May be clone it before return
		return this._models.keys();
	},
	
	setData:  function( modelId, uriSuffix, params, successHandler ){
		this._sendRequest( modelId, uriSuffix, 'post', params, successHandler)
	}, 
	
	deleteData:  function( modelId, uriSuffix, params, successHandler ){
		this._sendRequest( modelId, uriSuffix, 'delete', params, successHandler)
	},	
	
	_sendRequest: function( modelId, uriSuffix, method, params, successHandler ){
		uriSuffix = (uriSuffix.startsWith("/") ? uriSuffix : "/" + uriSuffix)
		var requestUrl = this._models.get(modelId).substring(1) + uriSuffix;
		
		if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
			requestUrl += "?" + $H(params).toQueryString();
		}
		
		Ext.Ajax.request({
		   url		: requestUrl,
		   method	: method,
		   params	: params,
		   success	: function(res) {
					
						var returnedData = new Hash(Ext.util.JSON.decode(res.responseText));
						
						returnedData.each(function(pair) {
							this.updateObject(uriSuffix, pair.key, pair.value);
						}.bind(this));
						
						if( successHandler ){
							successHandler( res );
						}
			}.bind(this),
			failure	: function(){
				Ext.Msg.alert('Oryx','Server communication failed!')
			}
		});

	},
	
	getModelTypes : function() {
		// lazy loading
		if (!this.modelTypes) {
			new Ajax.Request("/oryx/stencilsets/stencilsets.json", 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this.modelTypes = transport.responseText.evalJSON();
					this.modelTypes.each(function(type) {
						type.iconUrl = this.oryxUrl + this.stencilsetUrl + type.icon_url;
						type.url = this.stencilsetUrl + type.uri
					}.bind(this));
				}.bind(this),
				onFailure: function() {alert("Fehler modelTypes")}
			});
		}
		return this.modelTypes;
	},
	
};

Repository.Core.DataCache = Clazz.extend(Repository.Core.DataCache);

