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

Ext.namespace("ModelProperties")

ModelProperties.FIELD_WITH_ERROR = "field_with_error";
ModelProperties.FIELD_WITH_ERROR_MSG = "field_with_error_msg";

ModelProperties.app = {

	/**
	 * saves a copy of the current model
	 * 
     * must be kept in sync with ui information
     * should not be used to overwrite a complete model on server side, instead cach information client-side
	 */
	current_model: null,
	
	anonymous_user: "public", // do not change, except it's changed server side
	
	create_model_uri: "/model/", // the uri a POST request containing the models details  will be sent to create a new model
	
	init: function(current_user) {
		var expected_ext_version = "2.0";
        if (Ext.version.indexOf(expected_ext_version) != 0) {
            throw "ExtJS has wrong version: " + Ext.version + " (expected " + expected_ext_version + ")";
        }
		
		// register the current user
		if (!current_user && current_user != this.anonymous_user) {
			throw "Current user missing"
		}
		this.current_user = current_user;
		
		ModelProperties.render.init();
	},

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
	}
	,
		
	/**
	 * updates info data of the model
	 * 
	 * @param {Object} form
	 */
	updateModelInfo: function(form) {
		
		var title = form.elements["title"].value.strip();
		if (title == "") {
			form.elements["title"].className += " " + ModelProperties.FIELD_WITH_ERROR
			var errormsg = document.createElement("span");
			errormsg.className = ModelProperties.FIELD_WITH_ERROR_MSG;
			errormsg.innerHTML = "The title of a model must not be empty"
			
			form.elements["title"].parentNode.appendChild(errormsg);
			return false;
		}
		
		var summary = form.elements["summary"].value.strip();
		
		try {	
			Ext.Ajax.request({
				method: "POST",
				url: this.current_model.info.edit_uri,
				params: {
					title: title,
					summary: summary
				},
				success: function(response, options) {
	                var model = Ext.util.JSON.decode(response.responseText);
					ModelProperties.app.current_model.info = model.info
					ModelProperties.render.renderPropertiesForm(ModelProperties.app.current_model);
				},
				failure: function(response, options) {
					switch (response.status) {
						case 405:
						case 401:
							Ext.MessageBox.alert("Update Failed", "You cannot update this model's information.").setIcon(Ext.MessageBox.ERROR);
							break;
						case 404:
							Ext.MessageBox.alert("Update Failed", "The model does not seem to exist anymore. Try reloading the repository.").setIcon(Ext.MessageBox.ERROR);
							break;
						default:
							Ext.MessageBox.alert("Update Failed", "Updating the model failed due to some internal reasons. Try reloading the repository or updating the model later.").setIcon(Ext.MessageBox.ERROR);
					}
				}
			})
		}
		catch(e) {
			Ext.MessageBox.alert("Update Failed", "Updating the model failed due to some internal reasons. Try reloading the repository or updating the model later.").setIcon(Ext.MessageBox.ERROR);
		}
	},
	
	
	/**
	 * adds access for a user to the current model 
	 * We came to the decision that adding user access as single requests eases a lot of work.
	 * - we don't neeed to encode collections
	 * - we can provide validation and transactional behavior per user access
	 * 
	 * 
	 * @param {String} openid
	 * @param {String} predicate possible values: "read", "write"
	 * @param {Function} success
	 * @param {Function} failure
	 */
	_addAccess: function(openid, predicate, success, failure) {
		// save some traffic and don't add the 
		var access_rights = ModelProperties.app.current_model.access.access_rights;
		for (var i=0; i < access_rights.length; i++) {
			
			// we check if the same user acces already exists, 
			// we explicitly allow to add one user multiple times with different predicates
			if (ModelProperties.app.equalUsers(access_rights[i].subject, openid)
				&& access_rights[i].predicate == predicate)
			{
				// this user already has this access
				return false;
			}
		}
		
		Ext.Ajax.request({
			method: "POST",
			url: ModelProperties.app.current_model.access.edit_uri,
			params: {
				subject: openid,
				predicate: predicate
			},
			success: function(response, options) {
				if (typeof success == typeof function(){}) {
					var new_access = Ext.util.JSON.decode(response.responseText);
					// TODO add response validation
					ModelProperties.app.current_model.access.access_rights.push(new_access);
					
					if (typeof success == typeof function(){}) {
						success(response, options)
					}
				}
			},
			
			failure: function(response, options) {
				if (typeof failure == typeof function(){}) {
					failure(response, options);
				}
			}
		})
	},
	
	
	_removeAccess: function(openid, success, failure) {
		var index = null; // saves the index of the openid
		for (var i=0; i < ModelProperties.app.current_model.access.access_rights.length; i++) {
			if (ModelProperties.app.equalUsers(ModelProperties.app.current_model.access.access_rights[i].subject, openid)) {
				index = i;
				break;
			}
		}
		
		if (null === index) {
			// access not found
			return false;
		}

		Ext.Ajax.request({
			method: "DELETE",
			url: ModelProperties.app.current_model.access.access_rights[index].uri,
			success: function(response, options) {
				ModelProperties.app.current_model.access.access_rights.splice(i,1);
				if (typeof success == typeof function() {}) {
					success(response, options);
				}
			},
			failure: function(response, options) {
				if (typeof failure == typeof function() {}) {
					failure(response, options);
				}
			}
		});
	},
	
	/**
	 * callback for UI to add user access
	 * covers validation and handling of a colection of users
	 */
	addAccessRights: function(form) {
		var tmp = form.elements["subjects"].value.split(/[,;]/);
		var subjects = [];
		
		// clean array from empty entries
		for (var i=0; i< tmp.length; i++) {
			if (tmp[i].strip().length > 0) {
				subjects.push(tmp[i])
			}
		}
		var total_count = subjects.length;
		
		var predicate = null;
		for (var i=0; i<form.elements["predicate"].length; i++) {
			if (form.elements["predicate"][i].checked) {
				predicate = form.elements["predicate"][i].value;
			}
		}
		
		// before starting remove old error messages
		var parent = Ext.get(form);
		parent.select("input."+ModelProperties.FIELD_WITH_ERROR).each(function(element){
			element.removeClass(ModelProperties.FIELD_WITH_ERROR)
		})
		
		parent.select("."+ModelProperties.FIELD_WITH_ERROR_MSG).each(function(element) {
			element.remove();
		})
		
		
		if (null === predicate || (predicate != "read" && predicate != "write")) {
			Ext.DomHelper.insertAfter(
				form.elements["predicate"][form.elements["predicate"].length - 1].nextSibling, 
				"<span class=\""+ModelProperties.FIELD_WITH_ERROR_MSG+"\">You must select whether users shall be added as viewers or contributers.</span>"
			);
			return false;
		}
		
		if (subjects.length == 0) {
			Ext.DomHelper.insertAfter(
				form.elements["subjects"], 
				"<span class=\""+ModelProperties.FIELD_WITH_ERROR_MSG+"\">You must specify users that shall be added.</span>"
			);
			return false;
		}

		
		
		// TODO start progressbar
		var progressbar = Ext.MessageBox.progress("Adding user access ... ");
		
		// This works as following:
		//  after each finished request (successful or not) the next item of subject will be grabbed
		//  and an _addAccess request will be called, when this one finishes, the procedure starts over again
		//  the scope of the method next is a private scope within addAccessRights that is not accessible from external
		var next = function() {
			var failed = true;
			while (subjects.length > 0 && failed == true) {
				var openid = subjects.pop().strip();
				if (ModelProperties.app.isOpenId(openid)) {
					
					// change progressbar and caption value = 1 - (subjects.length/total_count)
					progressbar.updateProgress(1 - (subjects.length / total_count),  (total_count - subjects.length) + " of " + total_count, "adding " + openid);
					
					failed = (false === ModelProperties.app._addAccess(openid, predicate, 
						function success(response, options) {
							access = Ext.util.JSON.decode(response.responseText);
							
							// add user to the list of accessors
							if (predicate == "write") {
								ModelProperties.render.access_template.user.append('model_access_writer', access);
							}
							else {
								ModelProperties.render.access_template.user.append('model_access_reader', access);
							}
							
							// remove user from the input text area
							// var reg = new RegExp("\s*" + openid + "\s*,?");
							// form.elements["subjects"].value = form.elements["subjects"].value.replace(reg, "").replace(/^(\s*,?\s*)*/,"").replace(/(\s*,?\s*)*$/,"").strip();
							
							next();
						}, 
						function failure(response, options) {
							// server refused to add user
							Ext.DomHelper.insertAfter(
								form.elements["subjects"], 
								"<span class=\""+ModelProperties.FIELD_WITH_ERROR_MSG+"\">The user <b>" + openid + "</b> could not be added (Probably, his user was added by another contributor of this model. Try reloading the repository.).</span>"
							);
							
							next();
						}));
						
					if (failed) {
						// _addAccess failed => the specified openid already has this access	
						Ext.DomHelper.insertAfter(
							form.elements["subjects"], 
							"<span class=\""+ModelProperties.FIELD_WITH_ERROR_MSG+"\">The user <b>" + openid + "</b> already has this access right.</span>"
						);
					}
				}
				else {
					// user is not valid openid
					Ext.DomHelper.insertAfter(
						form.elements["subjects"], 
						"<span class=\""+ModelProperties.FIELD_WITH_ERROR_MSG+"\"><b>" + openid + "</b> is not a valid OpenID.</span>"
					);
				}
			}

			if (subjects.length == 0) {
				// probably we want to clear the whole list anyway
				form.elements["subjects"].value = "";
				
				// finally, hide the progressbar
				progressbar.hide();
			}
		}
		
		next();
		
	
	},
	
	/**
	 * callback for UI to delete a user access
	 * delegates actual deletion to _deleteAccess
	 * 
	 * @param {String} openid
	 * @param {Element} container the container that should visually be removed, when the user access is removed
	 * @param {Function} callback that shall be called to change the ui INSTEAD of removing the user form the list
	 */
	deleteAccessRight: function(openid, container) {
		// TODO find some nice words here
		
		// just a simple warning
		var confirm_msg = "Are you sure you want to withdraw acces to this model for this user?\n\n " + openid + "\n\nThis cannot be undone.";
		if (this.isCurrentUser(openid)) {
			// when a user deltes himself, he will loose all rights
			confirm_msg = "You are attempting to remove your own access to this model. You cannot add yourself again, and thus cannot access the model anymore.\nAre you sure that you want to continue?"
			
			// ... and all unsaved changes will be lost, since the window closes
			if (ModelProperties.render.window._isDirty) {
				confirm_msg += "\n\nYou still have unsaved changes. These will get lost also."
			}
		}
		
		Ext.MessageBox.confirm("Delete User Access", confirm_msg, function() {
		
			// hide the container, delete it later
			$(container).style.display = "none";		
			
			ModelProperties.app._removeAccess(
				openid,
				function success(response, options) {
					// remove content from DOM
					$(container).parentNode.removeChild($(container));

					// force close window, if the deleted himselfs
					if (ModelProperties.app.isCurrentUser(openid)) {
						ModelProperties.render.window._isDirty = false;
						ModelProperties.render.window.close();
					}
				},
				function failure(response, options) {
					switch (response.status) {
						case 405:
						case 401:
							Ext.MessageBox.alert("Delete Failed", "You cannot delete this user access.").setIcon(Ext.MessageBox.ERROR);
							$(container).style.display = "block";
							break;
						case 404:
							Ext.MessageBox.alert("Delete Failed", "The user access does not seem to exist anymore. Try reloading the repository.").setIcon(Ext.MessageBox.ERROR);
							$(container).parentNode.removeChild($(container));
							break;
						default:
							Ext.MessageBox.alert("Delete Failed", "Deleting the user access failed due to some internal reasons. Try reloading the repository or deleting the access later.").setIcon(Ext.MessageBox.ERROR);
							$(container).style.display = "block";
					}
				}
			);
		}); // end of Ext.MessageBox.confirm(
	},	
	
	unpublishModel: function() {
		this.current_model.access.access_rights.each(function(access){
			if (ModelProperties.app.isAnonymousUser(access.subject)) {
				ModelProperties.app._removeAccess(
					access.subject,
					function success(){
						ModelProperties.render.renderAccessFormPublication({
							published: false,
							is_owner_or_writer: true,
							model: ModelProperties.app.current_model
						});
					},
					function failure(){
						Ext.MessageBox.alert("Unpublishing Failed","The model could not be unpublished due to an internal reason.").setIcon(Ext.MessageBox.ERROR);
					}
				);
			}
		})
	},
	
	publishModel: function() {
		this.current_model.access.access_rights.each(function(access) {
			if (ModelProperties.app.isAnonymousUser(access.subject)) {
				return false;
			}
		});
		
		
		this._addAccess(this.anonymous_user, "read", 
			function success(response, options) {
				ModelProperties.render.renderAccessFormPublication({
					published: true,
					is_owner_or_writer: true,
					model: ModelProperties.app.current_model
				});
			},
			function failure(response, options) {
				Ext.MessageBox.alert("Publishing Failed","The model could not be published due to an internal reason.").setIcon(Ext.MessageBox.ERROR)
			});
	}
}

ModelProperties.render = {
	
	/**
	 * Prepares the attributes and the view.
	 * Initializes and configures all required HTML-templates
	 * 
	 * Call this method only once per application.
	 */
	init: function() {
		
		this.template_methods = {
			// returns true if userr is owner of this model
			userIsOwnerOrWriter: function(access) {
				var is_owner_or_writer = false;
				
				if (access && access.access_rights) {
					access.access_rights.each(function(accessor){
						if (ModelProperties.app.isCurrentUser(accessor.subject) &&
						(accessor.predicate == "owner" || accessor.predicate == "write")) {
							is_owner_or_writer = true;
							return $break;
						}
					});
				}
				
				return is_owner_or_writer;
			},
			
			dateFormat: function(string){
				var d = string.match(/(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/)
				var date = new Date(d[1], d[2], d[3], d[4], d[5], d[6]);
				
				return date.toLocaleDateString() + " - " + date.toLocaleTimeString()
			}
		}
		
	/** edit properties form *********************************************/
		
		this.properties_template = new Ext.XTemplate(
			'<tpl if="!this.userIsOwnerOrWriter(access)">',
				'<form class="oryx_repository_edit_model" action="#" id="edit_model">',
					'<p class="info">You don\'t possess the access rights to change values here.</p>',
					'<tpl for="info">',
						// TODO find some nice words here
						'<fieldset><legend>General Properties</legend>',
						'<p class="description">These properties describe the model.</p>',
							'<p><label for="edit_model_title">title</label><span class="input" id="edit_model_title">{title}</span></p>',
							'<p><label for="edit_model_summary">description</label><span class="input"  id="edit_model_summary">{summary}</span></p>',
						'</fieldset>',
						'<fieldset>',
							'<p><label for="edit_model_type">type</label><input type="text" class="text disabled" value="{type:htmlEncode}" disabled="disabled" id="edit_model_type" /></p>',
							'<p><label for="edit_model_created">created</label><input type="text" class="text disabled" value="{created:this.dateFormat}" disabled="disabled" name="created" id="edit_model_created" /></p>',
							'<p><label for="edit_model_updated">last update</label><input type="text" class="text disabled" value="{updated:this.dateFormat}" disabled="disabled" id="edit_model_updated" /></p>',
						'</fieldset>',
					'</tpl>',
				'</form>',
			'</tpl>',
				
			// the actual form	
			'<tpl if="this.userIsOwnerOrWriter(access)">',
				'<tpl for="info">',
					'<form class="oryx_repository_edit_model" action="#" id="edit_model" onsubmit="ModelProperties.app.updateModelInfo(this); return false;">',
						'<p class="info">You can edit the information about the model. Press the <b>save</b> button to persist changes.</p>',					
						'<input type="hidden" name="url" value="{edit_uri}" />',
					
						// TODO find some nice words here -- copy from above ;)
						'<fieldset><legend>General Properties</legend>',
						'<p class="description">These properties describe the model.</p>',
							'<p><label for="edit_model_title">title</label><input type="text" class="text" name="title" value="{title}" id="edit_model_title" /></p>',
							'<p><label for="edit_model_summary">description</label><textarea rows="7" name="summary" id="edit_model_summary">{summary}</textarea></p>',
						'</fieldset>',
						'<fieldset>',
							'<p><label for="edit_model_type">type</label><input type="text" name="type" class="text disabled" value="{type:uppercase}" disabled="disabled" id="edit_model_type" /></p>',
							'<p><label for="edit_model_created">created</label><input type="text" name="created" class="text disabled" value="{created:this.dateFormat}" disabled="disabled" id="edit_model_created" /></p>',
							'<p><label for="edit_model_updated">last update</label><input type="text" name="updated" class="text disabled" value="{updated:this.dateFormat}" disabled="disabled" id="edit_model_updated" /></p>',
						'</fieldset>',
						'<fieldset>',
							'<p class="inline"><input type="submit" class="button" value="save" id="edit_model_properties_submit" disabled="disabled"/><input type="reset" class="button" value="undo changes" id="edit_model_properties_reset" disabled="disabled"</p>',
						'</fieldset>',
					'</form>',
				'</tpl>',
			'</tpl>',
			
			// TODO find some nice words here
			'<fieldset><legend>Further Links</legend>',
				'<p class="description">These further links are provided by this model</p>',
				'<dl>',
					'<tpl for="uris">',
						'<dt><a href="{href}" target="_blank">{title}</a></dt>',
						'<dd>{description:undef}</dd>',
					'</tpl>',
				'</dl>',
			'</fieldset>',
		this.template_methods);
		this.properties_template.compile();
		
	/** edit access form *************************************************/
	
		// edit access form body
		this.access_template = {}
		this.access_template.body = new Ext.XTemplate(
			'<form action="#" method="post">' + 
				'<p class="info">',
					'<tpl if="is_owner_or_writer">', 'You can edit the kind of access specific users have to the model.', '</tpl>',
					'<tpl if="!is_owner_or_writer">', 'You don\'t possess the access rights to change values here.', '</tpl>',
				'</p>',
				'<div id="edit_model_access_published"></div>',
				'<p/>',
				
				'<fieldset><legend>Owner ({cnt_owners})</legend>',
					'<p class="description">The owner of the model possesses all rigthts and cannot be changed.</p>',
					'<ul class="access" id="model_access_owner">',
					'</ul>',
				'</fieldset>',
				'<fieldset><legend>Contributors ({cnt_writers})</legend>',
					'<p class="description">Contributors may edit the model and information, and invite more people.</p>',
					'<ul class="access" id="model_access_writer">',
					'</ul>' + 
				'</fieldset>',
				'<fieldset><legend>Viewers ({cnt_readers})</legend>',
					'<p class="description">Viewers may view the model but cannot edit it.</p>',
					'<ul class="access" id="model_access_reader">',	
					'</ul>',
				'</fieldset>',
			'</form>',
			
			'<div id="edit_model_access_form"></div>',
			this.template_methods);
			
		this.access_template.body.compile();
	
		// edit access form list elements for users - rendered when form user may change settings
		this.access_template.user = new Ext.Template(
			'<li>' +
				'<span class="model_access_user">{subject:htmlEncode}</span> - ' +
				'<a href="#" onclick="ModelProperties.app.deleteAccessRight(\'{subject}\', this.parentNode); return false;">remove</a>' +
			'</li>'
		);
		this.access_template.user.compile();
		
		// edit access form list elements for users - rendered when form user cannot change enything, i.e. form is read only
		this.access_template.user_disabled = new Ext.Template(
			'<li>' +
				'<span class="model_access_user">{subject:htmlEncode}</span>' +
			'</li>'
		);
		this.access_template.user_disabled.compile();
		
		// displays a link ot the model, in case it's published
		this.access_template.publication = new Ext.XTemplate(
			'<fieldset>',
				'<tpl if="published">',
					'<legend>This model is published</legend>',
					'<tpl for="model.info">',
						'<p class="description">A published model in its currently saved state can be viewed by everyone online. It is available at: ',
						'<a href="{self_uri}" target="_blank">{self_uri}</a></p>',
					'</tpl>',
					
					'<tpl if="is_owner_or_writer">',
						'<button onclick="ModelProperties.app.unpublishModel(); return false">unpublish model</button>',
					'</tpl>',
				'</tpl>',
								
				'<tpl if="!published">',
					'<legend>This model is not published</legend>',
					'<tpl for="model.info">',
						'<p class="description">A published model in its currently saved state can be viewed by everyone online. It would be available at: ',
						'<em style="font-style: italic;">{self_uri}</em></p>',
					'</tpl>',
					
					'<tpl if="is_owner_or_writer">',
						'<button onclick="ModelProperties.app.publishModel(); return false">publish model</button>',
					'</tpl>',
				'</tpl>',
			'</fieldset>'
		);
		this.access_template.publication.compile();
		
		
		// add user access to model form -- grouped_access object is passed
		this.access_template.form = new Ext.XTemplate(
			'<hr/>',
			'<form action="#" method="post" onsubmit="ModelProperties.app.addAccessRights(this); return false;">',
				'<fieldset><legend>Add Users</legend>',
					'<p class="description">Select the desired access type and enter the OpenIDs of people you want to collaborate with, separated by comma.</p>',
					'<p class="inline">',
						'<input type="radio" class="checkbox" name="predicate" value="read" id="edit_model_access_type_read" />',
						'<label for="edit_model_access_type_read">as viewers</label>',
						'<input type="radio" class="checkbox" name="predicate" value="write" id="edit_model_access_type_write" />',
						'<label for="edit_model_access_type_write">as contributors</label>',
					'</p>',
					'<p>', 
	//					'<label for="edit_model_access_new">people to add</label>',
						'<textarea name="subjects" id="edit_model_access_new" rows="7"></textarea>',
					'</p>',
					'<p>', 
						'<input type="submit" value="save" disabled="disabled" id="edit_model_access_submit"/>',
					'</p>',
				'</fieldset>',
			'</form>'
		);
		this.access_template.form.compile();
	},

	/**
	 * Loads the model into a properties window, renders its parts and displays it
	 * @param {Object} model
	 * @param {Object} listeners - an object (key-value-hash) defining listeners that shall be registered at the window
	 */
	show: function(model, remote_listeners) {
		
		
		ModelProperties.app.current_model = model;
		
		// ensure correct type of remote listeners
		if (typeof remote_listeners != typeof {}) {
			remote_listeners = {};
		}
		
		// create the window to display the form		
		this.window = new Ext.Window({
			title: "Edit model: " + model.info.title,
			layout:'fit',
			width:600,
			height:500,
			modal: true,
			items : new Ext.TabPanel({
			    activeTab: 0,
			    items: [{
					id: "tab1",
			        title: 'Model Properties',
					autoScroll: true,
					html: '<div id="edit_model_properties"></div>',
					listeners: {
						activate : function (tab) {
							if (!tab._loaded) {
								tab._loaded = true;
								// render form and append some nice functionality
								ModelProperties.render.renderPropertiesForm(model);
							}
						}
					}
			    },{
					id:"tab2",
			        title: 'Model Access',
					html: '<div id="edit_model_access"></div>',
					autoScroll: true,
					listeners : {
						activate : function(tab) {
							if (!tab._loaded) {
								tab._loaded = true;
								// since rendering is a little bit more complex, this will be done in a outsourced method
								ModelProperties.render.renderAccessForm(model);
							}
						}
					}
			    }]
			}), // end of new Ext.TabPanel ...
			
			listeners : {
				beforeclose: function(window){
					// confirm message is only to be displayed if the window is not dirty and the user did not already confirm the close
					if (!window._close_confirmed && 
						window._is_dirty && (window._is_dirty.properties || window._is_dirty.access)) 
					{
						Ext.Msg.confirm("Sure?", "Sure to close?", function(answer){
							if (answer == "yes") {
								window._close_confirmed = true;
								window.close();
							}
						}).setIcon(Ext.MessageBox.QUESTION);
						
						return false;
					}
				}
			} 
		}); // end of new Ext.Window ...
		
		// add remote listeners
		for(listener in remote_listeners) {
			this.window.addListener(listener, remote_listeners[listener]);
		}
		
		
		this.window.show();
	},
	
	renderPropertiesForm: function(model) {
		container = $('edit_model_properties');
		
		form = ModelProperties.render.properties_template.overwrite(container, model);
		ModelProperties.render.formHelper(form, ["edit_model_properties_submit", "edit_model_properties_reset"]);
	},
	
	/**
	 * renders the form to edit access settings for the model
	 * @param {Object} model
	 */
	renderAccessForm: function(model) {

		if (!model.access || !Object.isArray(model.access.access_rights)) {
			throw "invalid data"
		}

		// sort users by name
		var m_access = model.access.access_rights.sort(function(a,b) {
			a = a.subject;
			b = b.subject;
			
			if (a < b) return -1;
			if (a > b) return 1;
			return 0;
		})
		
		// group and count users
		grouped_access = {
			owners: [],
			readers: [],
			writers: [],
			is_owner_or_writer: false,
			published: false
		}	
		
		m_access.each(function(access) {
			// published (empty string indicates anonymous user --> anonymous user can only read)
			if (ModelProperties.app.isAnonymousUser(access.subject)) {
				grouped_access.published = access;
				return;
			}
			
			// owner
			if (access.predicate == "owner") {
				grouped_access.owners.push(access);
				if (ModelProperties.app.isCurrentUser(access.subject)) {
					grouped_access.is_owner_or_writer = true;
				}
				return;
			}
			// writer
			if (access.predicate.indexOf("write") != -1) {
				grouped_access.writers.push(access);
				if (ModelProperties.app.isCurrentUser(access.subject)) {
					grouped_access.is_owner_or_writer = true;
				}
				return;
			}
			// reader
			grouped_access.readers.push(access)
			return;
		});


		// render the structure for displaying accessors
		Ext.DomHelper.overwrite($('edit_model_access'), ModelProperties.render.access_template.body.apply({
			cnt_owners: grouped_access.owners.length,
			cnt_writers: grouped_access.writers.length,
			cnt_readers: grouped_access.readers.length,
			is_owner_or_writer: grouped_access.is_owner_or_writer 
		}));
		

		// choose the accessor template according to the access right of the durrent user
		var disabled_user_tpl = ModelProperties.render.access_template.user_disabled;
		var dynamic_user_tpl = grouped_access.is_owner_or_writer ? ModelProperties.render.access_template.user : disabled_user_tpl;

		// add user names to the list
		grouped_access.owners.each(function(access){
			disabled_user_tpl.append('model_access_owner', access);
		});
		grouped_access.writers.each(function(access){
			dynamic_user_tpl.append('model_access_writer', access);
		});
		grouped_access.readers.each(function(access){
			dynamic_user_tpl.append('model_access_reader', access);
		});
		
		// cache the access rights of the current user regarding this model
		ModelProperties.app.is_owner_or_writer = grouped_access.is_owner_or_writer;
		
		// show the published link and form
		this.renderAccessFormPublication({
			published: grouped_access.published,
			is_owner_or_writer: grouped_access.is_owner_or_writer,
			model: model
		});
		
		// render the model if the current user has access rights
		if (grouped_access.is_owner_or_writer) {
			this.renderAccessFormAdd(model)
		}
	},
	
	renderAccessFormPublication: function(model) {
		var container = $('edit_model_access_published');
		
		Ext.DomHelper.overwrite(container, this.access_template.publication.apply(model));
	},
	
	renderAccessFormAdd: function() {
		var container = $('edit_model_access_form');
		
		form = Ext.DomHelper.append(container, ModelProperties.render.access_template.form.apply(grouped_access));
		ModelProperties.render.formHelper(form, "edit_model_access_submit");
	},
	/**
	 * adds some nice functionality to elements of a form
	 *  - elements stay highlighted after editing (by adding CSS-class "activated")
	 *  - elements can be enabled when another element of a form becomes focused
	 * @param {Object} form
	 */
	formHelper: function(form, enables) {
		
		// force enables to be an array
		if (enables) {
			enables = Object.isArray(enables) ? enables : [enables]
		}
		else {
			enables = []
		}
		
		// enables disabled elements when called
		// if element has its own enable function, it will be used
		// otherweise it will be enabled by dom and by removing the CSS-class "disabled"
		var doEnable = function() {
			enables.each(function(element) {
				element = Ext.get(element);
				if (element.enable) {
					element.enable();
				}
				else {
					element.dom.disabled = false,
					element.removeClass("disabled");
				}
			});
		}
		
		// iterate over all elements of a form and register according listeners
		form  = $(form);
		for (j = 0; j < form.elements.length; j++) {
			var element = Ext.get(form.elements[j]);
			
			// fieldsets are only for grouping, but part of form.elements - ignore them
			if (element.dom.nodeName.toLowerCase() != "fieldset" && element.dom.disabled == false) {
				var type = element.dom.type.toLowerCase();
				
				// register activation callbacks based on the type of the element
				if (["checkbox", "radio"].indexOf(type) >= 0) {
					// safari does not recognize onfocus on checkboxes
					element.on('click', doEnable); // seems to work, was click
				}
				
				else if (["hidden", "submit", "reset"].indexOf(type) == -1) {
					element.on('focus', function() { // seems to work, was focus
						Ext.get(this).addClass("activated");
						doEnable();
					})
				}
			}
		} // end of: for (j = 0; j < form.elements.length; j++) 
	}
}