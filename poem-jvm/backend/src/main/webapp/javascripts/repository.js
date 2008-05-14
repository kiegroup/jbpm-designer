/**
 * Copyright (c) 2008
 * Matthias Kunze
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
//Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

/*
window.onbeforeunload = function() {
	return "sure to quit?"
}
*/

// reference local blank image
Ext.BLANK_IMAGE_URL = '/backend/ext/resources/images/default/s.gif';

// create namespace
Ext.namespace('Repository');

// define some exception(name)s
Repository.Exceptions = {
    INVALID_DATA: "invalid data"
}

// create application
Repository.app = {

    model_panels: [],
	
	current_user: null,
	
	anonymous_user: "",
	
    models: [], // saves all loaded models
    /**
     * saves information for debugging, especially for problem identifying when page is loaded
     * access information via javascript console: Repository.app.debug.<...>
     */
    debug: {
        errors: [] // save error messages for debugging 
    },
    viewport: null,
    
    init: function(current_user){
        var expected_ext_version = "2.0";
        if (Ext.version.indexOf(expected_ext_version) != 0) {
            throw "ExtJS has wrong version: " + Ext.version + " (expected " + expected_ext_version + ")";
        }
		
		// register the current user
		if (!current_user && current_user != this.anonymous_user) {
			throw "Current user missing"
		}
		this.current_user = current_user;
		
        // initialize the rendering object
        Repository.render.init()
        
        // create viewport
        this.viewport = Repository.render.viewport();
        
        // preload panels
        this.preloadPanels();
		
		// initialize the Model Properties form window
		ModelProperties.app.init(this.current_user);

    },
    
    /**************************************************************************
     * CALLBACKS
     *
     * These methods create prepared callback objects for Ext listeners
     */
	
    /**
     * creates a callback that register a penal for preloading data
     */
    registerPanel: function(params, container, min_items){
        min_items = min_items || 3;
        return function(panel){
            Repository.app.model_panels.push({
                params: params,
                container: container,
                panel: panel,
                min_items: min_items
            });
        }
    },
    
    /**
     * loads the content of a panel if it's not empty
	 *
     * @param: panel - the Ext.Panel object (used for lookup of the panels data)
     */
    expandPanel: function(panel){
		if (!panel._loaded) {
			var panel_info = null; // find the according panel info
			Repository.app.model_panels.each(function(model_panel){
				if (panel == model_panel.panel) {
					panel._loaded = true;
					Repository.app.loadModelList(
						model_panel,
						function success(response, options, cnt) {},
						function failure(response, options) {
							// reset the panel
							panel.collapse()
							panel._loaded = false;
						}
					)
				}
			})
		}
	},

    /**************************************************************************
     * BUSINESS LOGIC
     */
	
	/**
	 * compares, whether two openids are the same --- is sort of flexible and fault tolerant
	 * 
	 * @param {String} openid1
	 * @param {String} openid2
	 */
	equalUsers: function(openid1, openid2) {
		openid1 = openid1+"";
		openid2 = openid2+"";
		
		filter = function(url) {
			return url.replace(/^https?:\/\//, "").replace(/\/*$/,"");
		}
		
		return filter(openid1) == filter(openid2)
	},
	
	/**
	 * checks whether a string represents a valid openid
	 * @param {String} openid
	 */
	isOpenId: function(openid) {
		// TODO implement
		
		re_uri = /^[^ \^\\\!\´\`\°\"\'\n\r\t\<\>]+$/ //currently  avoids only spaces and quotes
		return null !== openid.match(re_uri)
	},
	
	/**
	 * Checks whether a user is the currently logged in user
	 * 
	 * @param {Sring} openid - the openid of the user to check
	 */
	isCurrentUser: function(openid) {
		return this.equalUsers(openid, this.current_user);
	},
	
	/**
	 * Checks whether a user is the anonymous user, 
	 * based on the definition that an empty string (!== null) identifies the anonymous user
	 * 
	 * @param {String} openid
	 */
	isAnonymousUser: function(openid) {
		return this.equalUsers(openid, this.anonymous_user);
	},
	
	/**
	 * creates a time string from milliseconds since 1. Januar 1970, 0:00:00 Uhr UTC (via new Date().getTime())
	 * 
	 * @param {Object} time
	 */
	dateFormat: function(time) {
		var date = new Date(time);
		var response = 
		       date.getUTCFullYear() + "-" + 
		      (date.getUTCMonth() < 9 ? "0" : "") + parseInt(date.getUTCMonth()+1) + "-" +
			  (date.getUTCDate() < 10 ? "0" : "") + parseInt(date.getUTCDate()) + " " +
			  (date.getUTCHours() < 10 ? "0" : "") + parseInt(date.getUTCHours()) + ":" +
			  (date.getUTCMinutes() < 10 ? "0" : "") + parseInt(date.getUTCMinutes()) + ":" +
			  (date.getUTCSeconds() < 10 ? "0" : "") + parseInt(date.getUTCSeconds())

		ORYX.Log.debug("calculated date: %0 [%1]", response, typeof response);
		return response;
	},
	
	/**
	 * preloads models when repository starts
	 * - panels have to be registered via Repository.app.registerPanel
	 * - number of loaded models is defined by the min_items config of at the panels registration
	 * - loads panel by panel
	 */
	preloadPanels: function() {
		
		var panels = []; // clone the panels array
		for (var i=0; i < Repository.app.model_panels.length; i++) {
			panels[i] = Repository.app.model_panels[i];
		};
		
		var models_loaded_total = 0;
		var models_loaded_min = panels[0].min_items || models_loaded_min; // initialize to force loading
		
		var loadNextPanel = function(cnt) {
			
			if (panels.length > 0) {
				models_loaded_total += cnt;
				models_loaded_min = panels[0].min_items;
				
				if (models_loaded_total < models_loaded_min) {
				
					// update control vars
					var current_panel = panels.shift();
					
					// set panel loaded. thus, expand will nto do a reload
					current_panel.panel._loaded = true;
					
					// actually load panel content
					Repository.app.loadModelList(
						current_panel, 
						function success(response, options, cnt) {
							current_panel.panel.expand();
							loadNextPanel(cnt);
						}, 
						function failure(response, options) {
							current_panel.panel.collapse();
							current_panel.panel._loaded = false;
							loadNextPanel(0);
					});
				}
			}
		}
		
		loadNextPanel(0);
	},
	
	/**
	 * reloads models for each loaded panel
	 * - panels have to be registered via Repository.app.registerPanel
	 * - loads panel by panel
	 */
	updatePanels: function() {
		
		// show filter button, if no filter was set
		var filter_info = ""
		for(var i in this.filter) {
			filter_info += (filter_info.length == 0 ? "" : ", ")
			        + i + ": " + this.filter[i];
		}
		
		if (filter_info.length != 0) {
			ORYX.Log.debug("Filter Models: %0", filter_info)
			Ext.getCmp("toolbar_filter_button").setText("<b>currently shown models are filtered:</b> " + filter_info);
			Ext.getCmp("toolbar_filter_button").show();
		}
		else {
			ORYX.Log.debug("Filter Models: No filters")
			Ext.getCmp("toolbar_filter_button").hide();
		}
				
		var panels = []; // clone the panels that are currently loaded in order to update them
		for (var i=0; i < Repository.app.model_panels.length; i++) {
			if (Repository.app.model_panels[i].panel._loaded) {
				panels.push(Repository.app.model_panels[i]);
			}
		};
		
		panels.each(function(panel){
			ORYX.Log.trace(panel)
		})
		
		var updateNextPanel = function(cnt) {

			ORYX.Log.debug("%0 panels to update", panels.length);
			
			if (panels.length > 0) {
				// update control vars
				var current_panel = panels.shift();
				
				// set panel loaded. thus, expand will nto do a reload
				current_panel.panel._loaded = true;
				
				// actually load panel content
				Repository.app.loadModelList(
					current_panel, 
					function success(response, options, cnt) {
						current_panel.panel.expand();
						updateNextPanel(cnt);
					}, 
					function failure(response, options) {
						current_panel.panel.collapse();
						current_panel.panel._loaded = false;
						updateNextPanel(0);
				});
			}
		}
		
		updateNextPanel(0);
	},
	
	
	
	/**
	 * load all models for a panel
	 * overwrites the content o f apnel, to allow filtering of models
	 * 
	 * @param {Object} panel_info -- see Repository.app.registerModel()
	 * @param {Function} success, called with the params of Ext.Ajax.request#success with additional param specifying the number of models loaded
	 * @param {Function} failure, called with the params of Ext.Ajax.request#failure
	 */
    loadModelList: function(panel_info, success, failure){

		// initialize and show a loading message box
		var mb = Ext.MessageBox.show({
            closable: false,
            modal: true,
            title: "Loading",
            msg: "Loading models ..",
            icon: "ajax_loader"
        });

		// merge the configuration for the model list
		var params = this.filter || {};
		
		for (var property in panel_info.params) {
			params[property] = panel_info.params[property];
		}
		
		// encode a HTTP query based on the params
		var query = ""
		for (var property in params) {
			query += (query.length == 0 ? "?" : "&") + escape(property) + "=" + escape(params[property]);
		}
	
		// run the request
        Ext.Ajax.request({
			// TODO implement correct url here
            // url: '/proxy/http://bpt-imac2.hpi.uni-potsdam.de:3000/data/' + url,

            url: './model' + query,
            method: 'GET',
            
            success: function(response, options){
				
				// delete all children of the panels container -- Martin Czuchra will provide a better method to remove the container's DOM children
				panel_info.container.innerHTML = "";
				
                json = Ext.util.JSON.decode(response.responseText);
                var cnt = 0;
                
				// sort models by date DESCENDING
				json.sort(function(a,b) {
					a = a.info.updated;
					b = b.info.updated;
					
					if (a < b) return 1;
					if (a > b) return -1;
					return 0;
				});
				
                json.each(function(model) {
					if (Repository.app.registerModel(model, panel_info.container)) {
						cnt++;
					}
				})

				// update title
				panel_info.panel.setTitle(panel_info.panel.title.replace(/\(\d.*\)/, "") + " (" + cnt + " models)")
     
	 			// set some default content, if no panels were loaded
                if (cnt == 0) {
                    panel_info.container.innerHTML = '<p class="no_items">No Models</p>';
                }
				
				// hide message box
				mb.hide();
                
				// call passed callback if appropriate
                if (success instanceof Function) {
                    success(response, options, cnt);
                }
            },
			
            failure: function(response, options){
				ORYX.Log.error("loading of panel %0 failed with status %1. Message: %2", panel_info.panel, response.status, response.responseText)
				
				// hide message box
				mb.hide();

				// show some error information
				Ext.MessageBox.alert("Connection Failure", "Data could not be loaded").setIcon(Ext.MessageBox.ERROR);
				
				// call passed callback if appropriate
                if (failure instanceof Function) {
                    failure(response, options);
                }
            }
        });
    },
	
	/**
	 * stores a local copy for each model
	 * storage may contain more models that are displayed
	 * 
	 * @param {Object} model
	 * @param {Object} container
	 */
	registerModel: function(model, container) {
		// we use soem strange syntax and object paths since we don't have prototypes bind here
		 try {
			var key = Repository.render.renderModel(model, container);
			this.models[key] = model;
        } 
        catch (e) {
            // ignore invalid data items
            if (e != Repository.Exceptions.INVALID_DATA) {
                throw e
            }
			ORYX.Log.error("Received invalid model data from server");
			return false;
        }
		return true;
	},
	
	/**
	 * updates a single model in the repository
	 * - refreshes its representation in this.models
	 * - rerenders the model (uses the models uri for identification of the DOM container)
	 * 
	 * @param {Object} item
	 * @param {Object} url
	 */
	reloadModel: function(item, url) {
		var old_model = this.models[url];
		
		Ext.Ajax.request({
			method: "GET",
//TODO get the correct url from the model (should be info-access somehow)			
			url: old_model.info.meta_uri,
			success: function(response, options){
                json = Ext.util.JSON.decode(response.responseText);
				
				Repository.app.registerModel(json);
			}
		})
	},
	
    /**
     * starts to edit a model
     */
    editModel: function(item, url) {
        var model = this.models[url];
        
		ModelProperties.render.show(model, {
			close: function(){
				Repository.app.reloadModel(item, url);
			}
		});
    },
	
	createModel: function(modeltype) {

		ORYX.Log.debug("Create new Model:%0",modeltype);
		
		var modelTypeObj;
		
		Repository.app.model_types.each(function(el){ 
			if( el.id == modeltype ){
				modelTypeObj = el
			} 
		});
		
		var stencilSetURI	= modelTypeObj ? modelTypeObj.uri : null;
		
		// If the server has sends not uri value, take the bpmn stencilset
		stencilSetURI		= stencilSetURI ? stencilSetURI : '/stencilsets/bpmn/bpmn.json';
		
		var url = './new' + '?stencilset=' + stencilSetURI;

		var editor = window.open( url );
		window.setTimeout(
			function() {
				if(!editor || !editor.opener || editor.closed) {
					Ext.MessageBox.alert("Editor not started.", "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.").setIcon(Ext.MessageBox.QUESTION)
				}
			}, 5000);
		
	},
	
	deleteModel: function(item, url) {
        ORYX.Log.debug("DeleteUrl:%0",this.models[url]);
		var model = this.models[url];
		// console.log(model);
		if (window.confirm("Are you sure you want to delete this model?\n\n  " + model.info.title + "\n\nThis cannot be undone.")) {
			// hide the container, delete it later
			$(item).style.display = "none";
			ORYX.Log.debug("DeleteUrl:%0",model.info.self_uri);
			Ext.Ajax.request({
				method: "DELETE",
				url: model.info.self_uri,
				success: function() {
					// remove element from dom
					$(item).parentNode.removeChild($(item));
				},
				failure: function(response, options) {
					switch (response.status) {
						case 405:
						case 401:
							Ext.MessageBox.alert("Delete Failed", "You cannot delete this model.").setIcon(Ext.MessageBox.ERROR);
							$(item).style.display = "block";
							break;
						case 404:
							Ext.MessageBox.alert("Delete Failed", "The model does not seem to exist anymore. Try reloading the repository.").setIcon(Ext.MessageBox.ERROR);
							$(item).parentNode.removeChild($(item));
							break;
						default:
							Ext.MessageBox.alert("Delete Failed", "Deleting the model failed due to some internal reasons. Try reloading the repository or deleting the model later.").setIcon(Ext.MessageBox.ERROR);
							$(item).style.display = "block";
					}
				}
			})
		}
	},
	
	startModel: function(item, url){
		var model = this.models[url];
		var editor = window.open(model.info.self_uri);
		window.setTimeout(
			function() {
				if(!editor || !editor.opener || editor.closed) {
					Ext.MessageBox.alert("Editor not started.", "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.").setIcon(Ext.MessageBox.QUESTION)
				}
			}, 5000);
	},
	/**
	 * loads the provided modeltypes and returns them as parameter submitted when calling the callback
	 * uses a local cache unless reload is set true
	 * 
	 * @param {Function} callback
	 * @param {Boolean} reload optional; forces to reload the model regardless whether the lsit was loaded before
	 */
	loadModelTypes: function(callback, reload) {
		// defer calls, if we are currently loading the model types list
		if (Repository.app._model_types_loading) {
			window.setTimeout(function() {
				Repository.app.loadModelTypes(callback, reload);
			}, 1000);	
			
			ORYX.Log.debug("deferred loadModelTypes")
			return;
		}
		
		if (!this.model_types || reload === true) {
			Repository.app._model_types_loading = true;
			
			// load list of diagram types
			Ext.Ajax.request({
				method: "GET",
				// TODO change URL to be dynamically
				url: "/backend/poem/model_types",
				success: function success(response, options) {
					Repository.app.model_types  = Ext.util.JSON.decode(response.responseText);

					ORYX.Log.debug("server offered %0 model types", Repository.app.model_types.length)
					
					Repository.app._model_types_loading = false;
					
					if (callback instanceof Function) {
						callback(Repository.app.model_types);
					}
				},
				
				failure: function failure(response, options){
					Repository.app.model_types = [];
					
					Repository.app._model_types_loading = false;
					if (callback instanceof Function) {
						callback(Repository.app.model_types);
					}
				}
			})
			return;
		}
		
		if (callback instanceof Function) {
			callback(this.model_types);
		}
	},
	
	filterModelsByModelType: function(modeltype_id) {
						
		this.filter = modeltype_id ? {type: modeltype_id} : {};
		
		this.updatePanels();
	},
	
	filterModelsByAccessAndType: function(access, modeltype_id) {
		
		var newFilter = {};
		
		switch(access) {
			case "my_processes": 
				newFilter = {owner: true}
				break;
			case "shared_processes":
				newFilter = {owner: true, is_shared: true}
				break;
			case "contributor":
				newFilter = {contributor: true}
				break;
			case "reader":
				newFilter = {reader: true}
				break;
			case "public":
				newFilter = {is_public: true}
				break;
		}
		
		if(modeltype_id){
			newFilter['type'] = modeltype_id
		}
		
		this.filter = newFilter;
		this.updatePanels();
	},
	
	/**
	 * remove the models filter
	 */
	filterModelsByNothing: function() {
		this.filter = {};
		this.updatePanels();
	}
}

Repository.render = {

    /**
     * prepares the elements of the viewport
     * creates templates, adds custom template methods and precompiles templates for performance
     */
    init: function(){

    	// start tooltips
		Ext.QuickTips.init();
		
		// openid login template
		// TODO implement openid-login -- login or logout REQUIRES reload of repository!
		this.openid_tpl = new Ext.XTemplate(
			'<div id="oryx_repository_header" onmouseover="this.className = \'mouseover\'" onmouseout="this.className = \'\'">',
				'<img src="/backend/images/style/oryx.small.gif" id="oryx_repository_logo" alt="ORYX Logo" title="ORYX"/>',
		
				'<tpl if="this.isAnonymousUser(current_user) || this.isPublic(current_user)">',
					'<form action="/backend/consumer" method="post" id="openid_login">',
						'<div>',
							'<span>',
								'<img src="/backend/images/repository/hpi.png" onclick="Repository.render.openid_tpl.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\', 39, 8)"/>',
								'<img src="/backend/images/repository/blogger.png" onclick="Repository.render.openid_tpl.changeOpenId(\'http://username.blogspot.com/\', 7, 8)"/>',
								'<img src="/backend/images/repository/myopenid.png" onclick="Repository.render.openid_tpl.changeOpenId(\'http://username.myopenid.com/  \', 7, 8)"/>',
							'</span>',
							'<input type="text" name="openid_identifier" id="openid_login_openid" class="text gray" value="your.openid.net" onblur="if(this.value.replace(/^\s+/, \'\').replace(/\s+$/, \'\').length==0) {this.value=\'your.openid.net\'; this.className+=\' gray\';}" onfocus="this.className = this.className.replace(/ gray/ig, \'\'); if(this.value==\'your.openid.net\') this.value=\'\';" />',
							'<input type="submit" class="button" value="login"/>',
						'</div>',
					'</form>',
				'</tpl>',
				
				'<tpl if="!this.isAnonymousUser(current_user) && !this.isPublic(current_user)">',
					'<form action="/backend/logout.jsp" method="post" id="openid_login">',
						'<div>',
							'Hi, {current_user}',
							'<input type="submit" class="button" value="logout"/>',
						'</div>',
					'</form>',
		
				'</tpl>',
			
				'<div style="clear: both;"></div>',
			'</div>',
			{
				isAnonymousUser: function(user){
					ORYX.Log.debug("current user: >%0<", user);
					// local function, due to scope
					return Repository.app.isAnonymousUser(user);
				},
				
				isPublic: function(user){
					return user == "public"
				},
				changeOpenId: function(url, start, size){
					var o = document.getElementById('openid_login_openid');
					o.value = url;
					o.focus();
					
					if (window.ActiveXObject) {
						try {
							var tr = o.createTextRange();
							tr.collapse(true);
							tr.moveStart('character', start);
							tr.moveEnd('character', size);
							tr.select();
						} 
						catch (e) {
						}
					}
					else {
						o.setSelectionRange(start, start + size);
					}
				}
			});
		
	
        // list item template
        this.item_tpl = new Ext.XTemplate(
		
		// the models icon
		'<tpl for="info">',
			'<a href="#start-modeling" onclick="Repository.app.startModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">',
			// the following is funny, thx Ext.XTemplate
				'<tpl if="!parent.info.icon_url">',
					'<img src="http://www.macprime.ch/images/uploads/128x128_mactracker_icon.png" class="icon"/>',
				'</tpl>',
				'<tpl if="parent.info.icon_url">',
					'<img src="{icon_url:htmlEncode}" class="icon"/>',
				'</tpl>',
			'</a>',
		'</tpl>',
	
		// decide which actions are allowed on the model
		'<tpl for="access">',
			'<div class="ctrl">',
				'<a href="#start-modeling" class="start_model_button" title="start modeling" onclick="Repository.app.startModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">&nbsp;</a>',
						
			'<tpl if="this.userIsOwner(access_rights)">',
	        	'<a href="#edit-model-properties" class="edit_model_button" title="edit model properties" onclick="Repository.app.editModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">&nbsp;</a>',
				'<a href="#delete-this-model" class="delete_model_button" title="delete this model" onclick="Repository.app.deleteModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">&nbsp;</a>',
			'</tpl>',		
			
			'<tpl if="this.userIsWriter(access_rights)">',
				'<a href="#edit-model-properties" class="edit_model_button" title="edit model properteis" onclick="Repository.app.editModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">&nbsp;</a>',
			'</tpl>',
			
			'<tpl if="this.userIsReader(access_rights)">',
				'<a href="#view-model-properties" class="view_model_button" title="view model properties" onclick="Repository.app.editModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">&nbsp;</a>',
			'</tpl>',
			'</div>',
		'</tpl>',

		// show textual model info
		'<tpl for="info">',
	        '<h3>',
				'<a href="#start-modeling" onclick="Repository.app.startModel(\'{[parent.item_id]}\',\'{[parent.info.edit_uri]}\'); return false;">',
					'{title} ({type})',
				'</a>',
			'</h3>',
	        '<p class="description">{summary:htmlEncode}</p>',
			'<p class="updated">updated: {updated:this.dateFormat} </p>',
		'</tpl>',
		
		'<tpl for="access">',
    	    '<p class="owner">owner: {access_rights:this.getOwner}</p>',
//			'<p>access: {access:this.access}</p>',
		'</tpl>',
		
		{
			// returns true if userr is owner of this model
			userIsOwner: function(access) {
				var is_owner = false;
				
				access.each(function(accessor){
					if (Repository.app.isCurrentUser(accessor.subject) && 
					    accessor.predicate == "owner") 
					{
						is_owner = true;
						return $break;
					}
				});
				return is_owner;
			},
			
			// returns true if usre can write on this model but is not owner
			userIsWriter: function(access) {
				var is_writer = false
				
				access.each(function(accessor){
					// if current user is owner, he is not a writer
					if (Repository.app.isCurrentUser(accessor.subject)) {
						if (accessor.predicate == "owner") {
							is_writer = false;
							return $break;
						}
						
						if (accessor.predicate == "write") {
							is_writer = true;
							return $break;
						}
					}
				});
				return is_writer;
			},
			
			// returns true if user can only read this model
			userIsReader: function(access) {
				var is_reader = true;
				
				access.each(function(accessor){
					// if current user is owner, he is not a reader
					if (Repository.app.isCurrentUser(accessor.subject)) {
						if (accessor.predicate == "owner") {
							is_reader = false;
							return $break;
						}
						
						// if current user is writer, he is not a reader
						if (accessor.predicate == "write") {
							is_reader = false;
							return $break;
						}
					}
				});
				return is_reader;
			},
			
			// returns the owner of the model
			getOwner: function(access) {
				var owner = "unknown";
				
				access.each(function(accessor){
					if (accessor.predicate == "owner") {
						owner = accessor.subject;
						return $break;
					}
				});
				return owner;
			},
			
			dateFormat: function(string) {
				var d = string.match(/(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/)
				var date = new Date(d[1], d[2]-1, d[3], d[4], d[5], d[6]);
				
				return date.toLocaleDateString() + " - " + date.toLocaleTimeString()
			}
		});
        
        this.item_tpl.access = function(access){
            var msg = "";
            if (access._public.read || access._public.write) {
                msg += "Everyone can view and change the model.";
            }
            else 
                if (access._public.read || access._public.write) {
                    msg += "Everyone can " +
                    (access._public.read ? "view" : "") +
                    (access._public.write ? "change" : "") +
                    "the model";
                }
            
            if (access._shared.read && access._shared.write) {
                if (msg.length > 0) {
                    msg += "<br/>";
                }
                
                msg += "Some persons can view and change the model";
            }
            else 
                if (access._shared.read || access._shared.write) {
                    msg += "Some persons can " +
                    (access._public.read ? "view" : "") +
                    (access._public.write ? "change" : "") +
                    "the model";
                }
            
            if (msg.length == 0) {
                msg = "Only you can view and change the model."
            }
            return msg;
        }
        
        this.item_tpl.compile();
        
        // create additional HTML elements
        this.this_weeks_models_container = document.createElement("div");
        this.this_weeks_models_container.className = "oryx_repository_list_container"
        document.body.appendChild(this.this_weeks_models_container);
        
        this.this_years_models_container = document.createElement("div");
        this.this_years_models_container.className = "oryx_repository_list_container"
        document.body.appendChild(this.this_years_models_container);
        
        this.old_models_container = document.createElement("div");
        this.old_models_container.className = "oryx_repository_list_container"
        document.body.appendChild(this.old_models_container);
		
		
        
    },
    
    viewport: function(){
        return new Ext.Viewport({
            layout: 'border',
            defaults: {}, // default config for all child widgets
            items: [ // header
			{
                region: 'north',
                html: Repository.render.openid_tpl.apply({current_user: Repository.app.current_user}),
                height: 30
            }, 
			// model filter
            {
                region: 'west',
                title: 'Filter Processes',
                split: true,
                width: 200,
                minSize: 175,
                maxSize: 400,
                collapsible: true,
                autoScroll: true,
                margins: '0 0 0 0',
                xtype: 'treepanel',
                loader: new Ext.tree.TreeLoader(),
                root: new Ext.tree.AsyncTreeNode({
					listeners : {
						/*append: function(tree, parent, node, index) {
							// add child nodes for the model types dynamically loaded from and offered by the server
							if (node.id == "tree_node_processes_by_type") {
								Repository.app.loadModelTypes(function(model_types) {
									model_types.each(function(modeltype) {
										node.appendChild(
											new Ext.tree.TreeNode({
												text: modeltype.title,
												leaf: true,
												icon: modeltype.icon_url,
												qtip: modeltype.description,
												listeners: {
													click: function() {
														Repository.app.filterModelsByModelType(modeltype.id);
													}
												}
											})
										)
									})
								})
							} // end of if (node.id == "tree_node_processes_by_type") {
						}*/
						load: function( parent ){
							Repository.app.loadModelTypes(function(model_types) {
								
									parent.childNodes.each(function(child){
										
										model_types.each(function(modeltype) {
											child.appendChild(
												new Ext.tree.TreeNode({
													text: modeltype.title,
													leaf: true,
													icon: modeltype.icon_url,
													qtip: modeltype.description,
													listeners: {
														click: function() {
															Repository.app.filterModelsByAccessAndType( child.id, modeltype.id );
														}
													}
												})
											)
										})	
										
										if(child !== parent.firstChild){
											child.collapse();
										}										
									})									
								})
						}
					},
					
					
                    expanded: true,
                    children: [{
                        text: 'my processes',
						id: 'my_processes',
                        expanded: true,
						children: [{
                            text: "show all",
                            leaf: true,
							listeners: {
								click: function() {
									Repository.app.filterModelsByAccessAndType('my_processes');
								}
							}
                        }]
                    },{
                        text: 'shared processes',
						id: 'shared_processes',
                        expanded: true,
						children: [{
                            text: "show all",
                            leaf: true,
							listeners: {
								click: function() {
									Repository.app.filterModelsByAccessAndType('shared_processes');
								}
							}
                        }]
                    },{
                        text: 'contributor',
						id: 'contributor',
                        expanded: true,
						children: [{
                            text: "show all",
                            leaf: true,
							listeners: {
								click: function() {
									Repository.app.filterModelsByAccessAndType('contributor');
								}
							}
                        }]
                    },{
                        text: 'reader',
						id: 'reader',
                        expanded: true,
						children: [{
                            text: "show all",
                            leaf: true,
							listeners: {
								click: function() {
									Repository.app.filterModelsByAccessAndType('reader');
								}
							}
                        }]
                    },{
                        text: 'public',
						id: 'public',
                        expanded: true,
						children: [{
                            text: "show all",
                            leaf: true,
							listeners: {
								click: function() {
									Repository.app.filterModelsByAccessAndType('public');
								}
							}
                        }]
                    }]
                }),
                rootVisible: false
            }, // model list
 			new Ext.Panel({
                region: 'center',
                autoScroll: true,
                items: [
					new Ext.Toolbar([
						{
							text: 'Create new Model',
							iconCls: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
							icon: "/backend/images/silk/shape_square_add.png",
							tooltip: {
								text: 'Create a new model of the selected type',
								autoHide: true
							},
							menu: new Ext.menu.Menu({
								items: [],
								listeners: {
									beforeshow: function(menu) {
										if (!menu._model_types_loaded) {
											menu._model_types_loaded = true;
											
											Repository.app.loadModelTypes(function(model_types){
												model_types.each(function(modeltype){
													menu.addMenuItem({
														text: modeltype.title,
														icon: modeltype.icon_url,
														qtip: modeltype.description,
														handler: function(){
															Repository.app.createModel(modeltype.id);
														}
													})
												})
											});
										}
									}
								}
							})
						}, 
						'-', //separator
						{
							text: 'Refresh List',
							iconCls: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
							icon: "/backend/images/silk/arrow_refresh.png",
							tooltip: {
								text: 'Updates the list of displayed models',
								autoHide: true
							},
							handler: Repository.app.updatePanels,
						},
						'->', // spacer,
						new Ext.Toolbar.Button({
							id: "toolbar_filter_button",
							text: 'remove filter',
							iconCls: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
							icon: "/backend/images/silk/lightbulb.png",
							tooltip: {
								text: 'Click to remove the filter.',
								autoHide: true
							},
							handler: function(){
								Repository.app.filterModelsByNothing();
							},
							hidden: true

						})
						
					]),
					new Ext.Panel({
	                    title: "edited last seven days",
	                    collapsible: true,
	                    collapsed: true,
	                    border: false,
	                    items: {
	                        el: this.this_weeks_models_container,
	                        border: false,
	                    },
	                    listeners: {
	                        add: Repository.app.registerPanel(
								{
									from: Repository.app.dateFormat(new Date().getTime() - 7*24*60*60*1000), // today - one week  (in milliseconds)
								}, 
								this.this_weeks_models_container, 
								10),
	                        expand: Repository.app.expandPanel
	                    }
	                }), new Ext.Panel({
	                    title: "edited within a month",
	                    collapsible: true,
	                    collapsed: true,
	                    border: false,
	                    items: {
	                        el: this.this_years_models_container,
	                        border: false,
	                    },
	                    listeners: {
	                        add: Repository.app.registerPanel(
								{
									from: Repository.app.dateFormat(new Date().getTime() - 30*24*60*60*1000), // today - one month  (in milliseconds)
									to:   Repository.app.dateFormat(new Date().getTime() - 7*24*60*60*1000) // today - one week  (in milliseconds)
								},
								this.this_years_models_container, 
								4),
	                        expand: Repository.app.expandPanel
	                    }
	                }), new Ext.Panel({
	                    title: "edited within a year",
	                    collapsible: true,
	                    collapsed: true,
	                    border: false,
	                    items: {
	                        el: this.old_models_container,
	                        border: false,
	                    },
	                    listeners: {
	                        add: Repository.app.registerPanel(
								{
									to:   Repository.app.dateFormat(new Date().getTime() - 30*24*60*60*1000) // today - one week  (in milliseconds)
								},
								this.old_models_container),
	                        expand: Repository.app.expandPanel
	                    }
	                })
				]
            }), ]
        });
    }, // end of Repository.render.viewport()
    
    /**
     * renders the model as a list into the specified element
     *
	 * @param {Object} model
	 * @param {Object} container
	 */
    renderModel: function(model, container){
       
        // render projection in template and display in the according container
        // a dom id will be crated according to the url of the model, because each model has it's own url
        //   http://www.w3.org/TR/html401/types.html#type-name
        //   HTML id may contain any letter, digit, period, colon, hyphen, and underscore
        //   HTML id must start with a letter
		function toHtmlId(string) {
			return string.replace(/[^a-z0-9\.\:\-\_]/ig, "_").replace(/^([^a-z])/i, "id$1");
		}
		
        model.item_id =  toHtmlId(model.info.edit_uri);
        var item = $(model.item_id);
        
        // if item is null, it does not exist in the dom, thus it is new 
        // if item exists but lies in another container we should not overwrite that one
		// this can only be performed if the container is given and valid
        if ((!item || item.parentNode != container) && $(container)) {
            // create a new entry in the list and add it to the container
            item = document.createElement("div");
            item.id = model.item_id;
            item.className = "oryx_repository_list_item";
            $(container).appendChild(item);
        }
        
		if (item) {
			//Ext.Template#overwrite overwrites the innerHTML of the element
	        this.item_tpl.overwrite(item, model);
		}
		else {
			throw "invalid data";
		}
        
		// return the meta url that is used to save the model in an associative array
        // this url is also used within the controls of the according template (this.item_tpl)
        return model.info.edit_uri;
    }
}
