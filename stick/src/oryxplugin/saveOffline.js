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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.SaveOffline = Clazz.extend({
	
    facade: undefined,
	
	processURI: undefined,
	
    construct: function(facade){
		this.facade = facade;
		this.modelLoaded = false;
		this.modelId = this.getParam("Id");
		
		
		this.facade.offer({
			'name': ORYX.I18N.Save.save,
			'functionality': this.save.bind(this,false),
			'group': ORYX.I18N.Save.group,
			'icon': ORYX.PATH + "images/disk.png",
			'description': ORYX.I18N.Save.saveDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		});
		
		
		this.facade.offer({
			'name': ORYX.I18N.Save.saveAs,
			'functionality': this.save.bind(this,true),
			'group': ORYX.I18N.Save.group,
			'icon': ORYX.PATH + "images/disk_multi.png",
			'description': ORYX.I18N.Save.saveAsDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0
		});	
		
		window.onbeforeunload = this.onUnLoad.bind(this)

		this.serializedDOM = DataManager.__persistDOM(this.facade);
		
		var scr = document.createElement("script");
		scr.setAttribute("type", "text/javascript");
		scr.setAttribute("src", "./stick/lib/gears_init.js");
		document.body.insertBefore(scr, document.body.childNodes[0]);
	},
	
	onSelectionChanged: function() {
		if(!this.modelLoaded) {
			this.modelLoaded = true;
			this.loadModel();
		}
	},
	
	loadModel: function() {
		if(this.modelId) {
			if (!window.google || !google.gears) {
				window.setTimeout(this.loadModel.bind(this), 10);
				return;
			}

			//get permission
			google.gears.factory.getPermission();
			
			//create database
			var db = google.gears.factory.create('beta.database');
			db.open("oryx-database");
			var rs = db.execute('select * from Models where ID=?', [this.modelId]);
			
			if (rs.isValidRow()) {
				var parser	= new DOMParser();			
				var doc 	=  parser.parseFromString( rs.field(2) ,"text/xml");
				this.facade.importERDF(doc);
				window.document.title = rs.field(1) + " - Oryx";
				//this.facade.getCanvas().update();
			}
			
			db.close();
		}
	},
	
	onUnLoad: function(){

		
		if( this.serializedDOM != DataManager.__persistDOM(this.facade) ){
		
			return ORYX.I18N.Save.unsavedData;
			
		}			
		
	},
		
	
    saveSynchronously: function(forceNew){
            
		// Get the serialized dom
		this.serializedDOM = DataManager.__persistDOM(this.facade);
		
		var reqURI = this.processURI ? this.processURI : location.href;
		
		if(forceNew){
			var ss 		= this.facade.getStencilSets();
			var source 	= ss[ss.keys()[0]].source().split('stencilsets')[0];
	
			reqURI = '/backend/poem' + ORYX.CONFIG.ORYX_NEW_URL + "?stencilset=/stencilsets" + source ;		
		}


		// Get the serialized svg image source
        var svgClone 	= this.facade.getCanvas().getSVGRepresentation(true);
        var svgDOM 		= DataManager.serialize(svgClone);
		
		
		if( !this.modelId || forceNew){
			
			// Get the stencilset
			var ss = this.facade.getStencilSets().values()[0]
		
			// Define Default values
			var defaultData = {title:ORYX.I18N.Save.newProcess, summary:'', type:ss.title(), url: reqURI, namespace: ss.namespace() }
			
			// Create a Template
			var dialog = new Ext.XTemplate(		
						// TODO find some nice words here -- copy from above ;)
						'<form class="oryx_repository_edit_model" action="#" id="edit_model" onsubmit="return false;">',
				
							'<fieldset><legend>General Properties</legend>',
								'<p class="description">These properties describe the model.</p>',
								'<input type="hidden" name="namespace" value="{namespace}" />',
								'<p><label for="edit_model_title">Title</label><input type="text" class="text" name="title" value="{title}" id="edit_model_title" onfocus="this.className = \'text activated\'" onblur="this.className = \'text\'"/></p>',
								'<p><label for="edit_model_summary">Description</label><textarea rows="5" name="summary" id="edit_model_summary" onfocus="this.className = \'activated\'" onblur="this.className = \'\'">{summary}</textarea></p>',
								'<p><label for="edit_model_type">Type</label><input type="text" name="type" class="text disabled" value="{type}" disabled="disabled" id="edit_model_type" /></p>',
								
							'</fieldset>',
						
						'</form>')
			
			// Create the callback for the template
			callback = function(form){
						
				var title 		= form.elements["title"].value.strip();
				title 			= title.length == 0 ? defaultData.title : title;
				
				//added changing title of page after first save
				window.document.title = title + " - Oryx";
				
				var summary = form.elements["summary"].value.strip();	
				summary 	= summary.length == 0 ? defaultData.summary : summary;
				
				var namespace	= form.elements["namespace"].value.strip();
				namespace		= namespace.length == 0 ? defaultData.namespace : namespace;
				
				win.destroy();
				
				// Send the request out
				this.sendSaveRequest( reqURI, { data: this.serializedDOM, svg: svgDOM, title: title, summary: summary, type: namespace }, forceNew);
				
			}.bind(this);
			
			// Create a new window				
			win = new Ext.Window({
				id:		'Propertie_Window',
		        width:	'auto',
		        height:	'auto',
		        title:	ORYX.I18N.Save.saveAsTitle,
		        modal:	true,
				bodyStyle: 'background:#FFFFFF',
		        html: 	dialog.apply( defaultData ),
				buttons:[{
					text: ORYX.I18N.Save.saveBtn,
					handler: function(){
						callback( $('edit_model') )					
					}
				},{
                	text: ORYX.I18N.Save.close,
                	handler: function(){
		               this.facade.raiseEvent({
		                    type: 'loading.disable'
		                });						
                    	win.destroy();
                	}.bind(this)
				}]
		    });
					      
			win.show();
			
		} else {
			
			// Send the request out
			this.sendSaveRequest( reqURI, { data: this.serializedDOM, svg: svgDOM } );
			
		}
    },
	
	sendSaveRequest: function(url, params, forceNew){

		//get permission
		google.gears.factory.getPermission();
		
		params.data		= params.data.gsub('><div', ">\n<div");		
		params.data		= '<?xml version="1.0" encoding="utf-8"?>\n<div class="processdata">\n' + params.data + "\n</div>"; 
		
		//create database
		var db = google.gears.factory.create('beta.database');
		db.open("oryx-database");
		
		if(forceNew) {
				var newId = this.getNewId();
				db.execute('insert into Models (ID, Name, Model, editorUrl, Type) values (?, ?, ?, ?, ?)', [newId,params.title,params.data,window.location.toString().split("?")[0] + "?Id=" + newId, this.facade.getStencilSets().values()[0].title()]);
		
				var newUrl = window.location.toString().split("?")[0] + "?Id=" + newId;
				var newURLWin = new Ext.Window({
					title:		ORYX.I18N.Save.savedAs, 
					bodyStyle:	"background:white;padding:10px", 
					width:		'auto', 
					height:		'auto',
					html:"<div style='font-weight:bold;margin-bottom:10px'>The process diagram is stored under:</div><span><a href='" + newUrl +"' target='_blank'>" + newUrl + "</a></span>",
					buttons:[{text:'Ok',handler:function(){newURLWin.destroy()}}]
				});
				newURLWin.show();
		} else {
			if(this.modelId) {
				db.execute('update Models set Model=? where ID=?', [params.data, this.modelId]);
			} else {
				this.modelId = this.getNewId();
				db.execute('insert into Models (ID, Name, Model, editorUrl, Type) values (?, ?, ?, ?, ?)', [this.modelId,params.title,params.data,window.location.toString().split("?")[0] + "?Id=" + this.modelId, this.facade.getStencilSets().values()[0].title()]);
			}
		}
		
		//show saved status
		this.facade.raiseEvent({
			type:'loading.status',
			text:ORYX.I18N.Save.saved
		});
					
		db.close();
				
	},
    
    /**
     * Saves the current process to the server.
     */
    save: function(forceNew, event){
    
        // raise loading enable event
        this.facade.raiseEvent({
            type: 'loading.enable',
			text: ORYX.I18N.Save.saving
        });
        
        // asynchronously ...
        window.setTimeout((function(){
        
            // ... save synchronously
            this.saveSynchronously(forceNew);
            
        }).bind(this), 10);

        
        return true;
    },
	
	getParam: function(name) {
		// get the current URL
		var url = window.location.toString();
		//get the parameters
		url.match(/\?(.+)$/);
		var params = RegExp.$1;
		// split up the query string and store in an
		// associative array
		var params = params.split("&");
		
		for(var i=0;i<params.length;i++)
		{
		    var tmp = params[i].split("=");
		    if(tmp[0] == name)
				return tmp[1];
		}
	},
	
	getNewId: function() {
		//get permission
		google.gears.factory.getPermission();
		
		//create database
		var db = google.gears.factory.create('beta.database');
		db.open("oryx-database");
		var rs = db.execute("select ID from Models");

		var ids = []
		while(rs.isValidRow()) {
			ids.push(rs.field(0));
			rs.next();
		}
		db.close();

		var newId = 1;
		
		while(ids.member(newId))
			newId++;
			
		return newId;
	}
});