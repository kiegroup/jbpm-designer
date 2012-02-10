/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
params = window.location.search.toQueryParams();

if (params['profile'] === undefined) {
	params['profile'] = 'default';
}

ORYX = {};
ORYX.CONFIG = {};
ORYX.UUID = params['uuid'];
ORYX.PROFILE = params['profile'];

var segments = window.location.pathname.split("/").without("");

ORYX.CONFIG.ROOT_PATH = "/" + segments.first() + "/";
ORYX.PATH = ORYX.CONFIG.ROOT_PATH;

if (ORYX.UUID === undefined) {
	ORYX.UUID = segments.pop();
}

var addScript = function(url, urls, prefix, finalCallback){

    var script = document.createElement("script")
    script.type = "text/javascript";
    if (urls) {
    	// wait for the script to be loaded to add a new script to respect loading order.
    	if (script.readyState){  //IE
    		script.onreadystatechange = function(){
    			if (script.readyState == "loaded" || script.readyState == "complete"){
    				script.onreadystatechange = null;
    				addScriptSequential(urls, prefix, finalCallback);
    			}
    		}.bind(urls, prefix, finalCallback);
    	} else {  //Others
    		script.onload = function(){
    			addScriptSequential(urls, prefix, finalCallback);
    		}.bind(urls, prefix, finalCallback);
    	}
    }
    script.src = url;
    document.head.appendChild(script);
    
};

var addScriptSequential = function(urls, prefix, finalCallback) {
	if (urls.size() == 0) {
		if (finalCallback) {
			finalCallback();
		}
		return;
	}
	url = urls.shift();
	addScript(prefix + url, urls, prefix, finalCallback);
	
};

function loadLanguageFiles() {
	addScript(ORYX.PATH + "i18n/translation_en_us.js");
}

function loadProfile(nextStep) {
	// get the profile, ask for its info, load the files.

	new Ajax.Request(ORYX.PATH + "profile?name=" + params['profile'], {
		asynchronous: false,
		method: 'get',
		contentType: 'application/json',
		onSuccess: function(result) {
		profile = result.responseText.evalJSON();
		// set the title:
		document.getElementsByTagName('title').item(0).text = profile.title;

		// set the stencilset to use:
		ORYX.CONFIG.SSET= profile.stencilset;
		// set the stencilset extensions in effect:
		ORYX.CONFIG.SSEXTS= profile.ssexts.map(function(ssext) {
			// for each of the extensions, we get the extension file and return its contents.
			var contents = null;
			
			new Ajax.Request(ORYX.PATH + "stencilset/" + ssext, {
				asynchronous: false,
				method: 'get',
				contentType: 'application/json',
				onSuccess: function(result) {
				  contents = result.responseText.evalJSON();
			    },
			    onFailure: function(result) {
			    	alert("Could not load Process Designer"); //TODO even better logging ?
			    }
			});
			return contents;
		});
		//get the complete plugin info:
		new Ajax.Request(ORYX.PATH + "plugins", {
			asynchronous: false,
			method: 'get',
			contentType: 'application/json',
			onSuccess: function(result) {
			var allPlugins = {};
			result.responseText.evalJSON().each(function (p) {
				allPlugins[p.name] = p;
			}.bind(allPlugins));
			// install the current plugins
			ORYX.availablePlugins = [];
			profile.plugins.each(function(pluginName) {
				ORYX.availablePlugins.push(allPlugins[pluginName]);
			}.bind(allPlugins));
			
			// now load the files as requested:
			addScriptSequential(profile.plugins.map(function(pluginName) { return pluginName + ".js"; }), 
					ORYX.PATH + 'plugin/', nextStep);
		},
	    onFailure: function(result) {
	    	alert("Could not load Process Designer"); //TODO even better logging ?
	    }
			
		});
		
		
		
	},
	onFailure: function(result) {
		alert("Could not load Process Designer"); //TODO even better logging ?
	}
	});

}

//get the core files to load:
new Ajax.Request(ORYX.PATH + "env", {
	asynchronous: false,
	method: 'get',
	contentType: 'application/json',
	onSuccess: function(result) {
	  addScriptSequential(result.responseText.evalJSON().files, ORYX.PATH, function() { 
		  loadLanguageFiles();
		  loadProfile(ORYX.load);
		  
	  });
    },
    onFailure: function(result) {
	  alert("Could not load Process Designer"); //TODO even better logging ?
    }
});



