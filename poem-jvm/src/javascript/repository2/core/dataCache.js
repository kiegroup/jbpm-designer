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

	construct : function() {
		
		// Stores data returned from /config handler
		this._configData = null;
		
		// Stores data returned from /user handler
		this._userData = null;
		
		// stores meta data of available modeltypes
		this._model_types = [];	
		// Stores cache type as key and the corresponding hash as value
		this._data = new Hash();
	
		
		this._addHandler 		= new EventHandler();
		this._updateHandler 	= new EventHandler();
		this._removeHandler 	= new EventHandler();
		this._userUpdateHandler = new EventHandler();
		
		this._busyHandler 		= { start: new EventHandler(), end:new EventHandler() };
		
		
		
	},
	
	
	getAddHandler 		: function() {return this._addHandler;},
	getUpdateHandler 	: function() {return this._updateHandler;},
	getRemoveHandler 	: function() {return this._removeHandler;},
	getBusyHandler 		: function() {return this._busyHandler;},
	getUserUpdateHandler : function() {return this._userUpdateHandler;},

	
	getDataAsync : function(fetchDataUri, ids, callback) {
		
		this._busyHandler.start.invoke();
		
		var modelIds = $A(ids); // Ensure that ids is an array
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
			
			if( callback )
				callback(result);
			
			this._busyHandler.end.invoke();
			return;
		}
		// Build query object
		var query = {};
		query.modelIds 		= modelIds;
		query.fetchDataUri 	= fetchDataUri;
		query.callback 		= callback;
		query.cacheMisses 	= cacheMisses;
		
		cacheMisses.each(function(modelId) {
			// Remove leading slash from model uri
			var requestUrl = modelId.substring(1) +  fetchDataUri 
			Ext.Ajax.request({url : requestUrl,  success : this.defaultReturnHandler.bind(this, query, modelId), failure:function(){/*console.log(arguments)*/}});
		}.bind(this));
		
		if( cacheMisses.length <= 0){
			this._busyHandler.end.invoke();
		}
	},
	
	updateObject : function(fetchDataUri, id, data, forceNotUpdate) {
		if (!this._data.get(fetchDataUri)) {
			this._data.set(fetchDataUri, new Hash())
		}
		this._data.get(fetchDataUri).set(id, data);
		if( !forceNotUpdate )
			this._updateHandler.invoke(id);
	},
	
	defaultReturnHandler : function(queryData, modelId, response, options) { 

		// Decode JSON
		var respText 		= response.responseText;
		var returnedData 	= respText.length > 0 ? Ext.util.JSON.decode(respText) : null;

		queryData.cacheMisses = queryData.cacheMisses.without( modelId );
		this.updateObject(queryData.fetchDataUri, modelId, returnedData, true); // Force update event only when at last request 

		// Everything returned from server
		if (queryData.cacheMisses.length == 0) {
			var queriedData = new Hash()
			
			queryData.modelIds.each(function (id){
				queriedData.set(id, this._data.get(queryData.fetchDataUri).get(id) ); // Write data to output hash
			}.bind(this));
			
			if( queryData.callback )
				queryData.callback(queriedData, response); 
			
			
			this._busyHandler.end.invoke();
		
		}
	},
	
	setData:  function( modelIds, uriSuffix, params, successHandler, raiseUserUpdateEvent ){
		this._sendRequest( modelIds, uriSuffix, 'post', params, successHandler, raiseUserUpdateEvent)
	}, 
	
	deleteData:  function( modelIds, uriSuffix, params, successHandler,  raiseUserUpdateEvent){
		this._sendRequest( modelIds, uriSuffix, 'delete', params, successHandler, raiseUserUpdateEvent)
	},	
	
	_sendRequest: function( modelIds, uriSuffix, method, params, successHandler, raiseUserUpdateEvent ){
		
		this._busyHandler.start.invoke();
		
		if( !(modelIds instanceof Array) ){
			modelIds = [ modelIds ]
		}

		uriSuffix = (uriSuffix.startsWith("/") ? uriSuffix : "/" + uriSuffix)	
		
		// Build query object
		var query = {};
		query.modelIds 		= modelIds;
		query.fetchDataUri 	= uriSuffix;
		query.callback 		= function(){
			
			this._updateHandler.invoke( modelIds );
			
			if( successHandler )
				successHandler.apply( successHandler , arguments)
				
			if( raiseUserUpdateEvent )
				this.updateUserData()
			
		}.bind(this);
						
		query.cacheMisses 	= modelIds;
		
		modelIds.each(function(modelId) {
			var requestUrl = modelId.substring(1) + uriSuffix;
			
			if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
				requestUrl += params ? "?" + $H(params).toQueryString() : "";
			}
			
			Ext.Ajax.request({
				    url		: requestUrl,
				    method	: method,
				    params	: params,
				    success	: this.defaultReturnHandler.bind(this, query, modelId),
					failure	: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					}
				});
		
		}.bind(this));

		if( modelIds.length <= 0){
			this._busyHandler.end.invoke();
		}
	},
	
	getModelTypes : function() {
		// lazy loading
		if (!this._modelTypes) {
			
			this._busyHandler.start.invoke();
			
			new Ajax.Request( Repository.Config.STENCILSET_URI, 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._modelTypes = transport.responseText.evalJSON();
					this._modelTypes.each(function(type) {
						type.iconUrl = this.oryxUrl + this.stencilsetUrl + type.icon_url;
						type.url = this.stencilsetUrl + type.uri
					}.bind(this));
					
					
					this._busyHandler.end.invoke();
			
				}.bind(this),
				onFailure: function() {
						alert("Fehler modelTypes");
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
		return this._modelTypes;
	},
	
	/* The following functions handle the requests to the /config server handler
	 * 
	 */
	
	
	_ensureConfigData : function() {
		// lazy loading
		if (!this._configData) {
			
			this._busyHandler.start.invoke();
		
			new Ajax.Request("config", 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._configData = transport.responseText.evalJSON();
					this._busyHandler.end.invoke();
				}.bind(this),
				onFailure: function() {
						alert("Error loading config data.")
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
	},
	
	getAvailableLanguages : function() {
		this._ensureConfigData();
		return this._configData.availableLanguages;
	},
	
	getAvailableSorts : function() {
		this._ensureConfigData();
		return this._configData.availableSorts;
	},

	getAvailableExports : function() {
		this._ensureConfigData();
		return this._configData.availableExports;
	},
		
	/* The following functions handle the requests to the /user server handler
	 * 
	 */

	updateUserData: function(useCache){
		
		if( !useCache ){
			this._userData = null;
			this._ensureConfigData();
		}
		
		this._userUpdateHandler.invoke();
				
	},
		
	_ensureUserData : function() {
		// lazy loading
		if (!this._userData) {
			
			this._busyHandler.start.invoke();
			new Ajax.Request("user", 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._userData = transport.responseText.evalJSON();
					this._busyHandler.end.invoke();
				}.bind(this),
				onFailure: function() {
						alert("Error loading user data.")
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
	},
	
	getUserTags : function() {
		this._ensureUserData();
		return this._userData.userTags;
	},
	
	getFriends : function() {
		this._ensureUserData();
		return this._userData.friends;
	},
	
	getLanguage : function() {
		this._ensureUserData();
		return this._userData.currentLanguage;
	},
	
	setLanguage : function(languagecode, countrycode) {
		
		
		this._busyHandler.start.invoke();
			
		new Ajax.Request("user", 
				 {
					method: "post",
					asynchronous : false,
					parameters : { 
						"languagecode" : languagecode,
						"countrycode" : countrycode
					},
					onSuccess: function(transport) {
						
						this._busyHandler.end.invoke();
						window.location.reload(); // reload repository to 
					}.bind(this),
					onFailure: function() {
						alert("Changing langauge failed!")
						this._busyHandler.end.invoke();
					}.bind(this)
				});
	},
	
	
	doRequest: function( url, successHandler, params,  method, asynchronous ){
		
		if(!url){
			return
		}
		
		// Set Busy
		this._busyHandler.start.invoke();
		
		// Define successcallback
		var callback = function(){
		
			this._busyHandler.end.invoke();
			
			if( successHandler )
				successHandler.apply( successHandler , arguments)
			
		}.bind(this);
		
		// Check URL
		method = method ? method : "get";
		if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
			url += params ? "?" + $H(params).toQueryString() : "";
			params = null;
		}				

		if( !asynchronous ){
			new Ajax.Request(url, 
				 {
					method			: "get",
					asynchronous 	: false,
					onSuccess		: callback,
					onFailure		: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					},
					parameters 		: params
				});
		} else {
			// Send request	
			Ext.Ajax.request({
				    url		: url,
				    method	: method,
				    params	: params,
				    success	: callback,
					failure	: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					}
				});			
		}

		

	}

	
	
};

Repository.Core.DataCache = Clazz.extend(Repository.Core.DataCache);

