/**
 * Copyright (c) 2009, Matthias Kunze, Kai Schlichting
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
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Config.Feedback = {
	VISIBLE_STATE: "visible",
	HIDDEN_STATE: "hidden",
	INFO: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, set eiusmod tempor incidunt et labore et dolore magna aliquam. Ut enim ad minim veniam, quis nostrud exerc. Irure dolor in reprehend incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse molestaie cillum. Tia non ob ea soluad incommod quae egen ium improb fugiend. Officia",
	CSS_FILE: ORYX.PATH + "/css/feedback.css"
}

ORYX.Plugins.Feedback = ORYX.Plugins.AbstractPlugin.extend({
	
    construct: function(facade, data){
		/*
		 * data.name == "ORYX.Plugins.Feedback"
		 * data.source == "feedback.js"
		 * data.properties ... properties defined in plugins.xml/profiles.xml [{key:value}, ...]
		 */
	
		this.facade = facade;
	
		// extract properties, we're interested in
		((data && data.properties) || []).each(function(property){
			if (property.cssfile) {ORYX.Config.Feedback.CSS_FILE = property.css_file}
		}.bind(this));
		
        // load additional css information
        var fileref = document.createElement("link");
            fileref.setAttribute("rel", "stylesheet");
            fileref.setAttribute("type", "text/css");
            fileref.setAttribute("href", ORYX.Config.Feedback.CSS_FILE);
        document.getElementsByTagName("head")[0].appendChild(fileref);

        // declare HTML references
        this.elements = {
    		container: null,
    		tab: null,
    		dialog: null,
			form: null,
			info: null
    	}
        
        // create feedback tab
        this.createFeedbackTab();
        
    },
    
    /**
     * Creates the feedback tab, which is used to open the feedback dialog.
     */
    createFeedbackTab: function(){
    	this.elements.tab = document.createElement("div");
    	this.elements.tab.setAttribute("class", "tab");
		this.elements.tab.innerHTML = (ORYX.I18N.Feedback.name + " &#8226;")
    	
    	this.elements.container = document.createElement("div");
    	this.elements.container.setAttribute("id", "feedback");
    	
    	this.elements.container.appendChild(this.elements.tab);
    	document.body.appendChild(this.elements.container);
          	    	
    	// register events
    	Event.observe(this.elements.tab, "click", this.toggleDialog.bindAsEventListener(this));
    },
    
    /**
     * Hides or shows the feedback dialog
     */
    toggleDialog: function(event) {

		if (event) {
			Event.stop(event);			
		}

    	var dialog = this.elements.dialog || this.createDialog();
    	
    	if (ORYX.Config.Feedback.VISIBLE_STATE == dialog.state) {
			this.elements.tab.innerHTML = (ORYX.I18N.Feedback.name + " &#8226;");
    		Element.hide(dialog);
    		dialog.state = ORYX.Config.Feedback.HIDDEN_STATE;
    	} 
    	else {
			this.elements.tab.innerHTML = (ORYX.I18N.Feedback.name + " &#215;");
    		Element.show(dialog);
    		dialog.state = ORYX.Config.Feedback.VISIBLE_STATE;
    	}

		console.info(dialog.state);
    },
    
    /**
     * Creates the feedback dialog
     */
    createDialog: function() {
    	if (this.elements.dialog) {
    		return this.elements.dialog;
    	}

		// reset the input formular
		var resetForm = function() {
			[description, title, mail].each(function(element){
				element.value = element._defaultText || "";
				element.className = "low";
			});
		}

		// wrapper for field focus behavior
		var fieldOnFocus = function(event) {
			var e = Event.element(event);
			if (e._defaultText && e.value.strip() == e._defaultText.strip()) {
				e.value = "";
				e.className = "high";
			}
		}		
		var fieldOnBlur = function(event) {
			var e = Event.element(event);
			if (e._defaultText && e.value.strip() == "") {
				e.value = e._defaultText;
				e.className = "low";
			}
		}

    	// create form and submit logic (ajax)
		this.elements.form = document.createElement("form");
		this.elements.form.action = ORYX.CONFIG.ROOT_PATH + "feedback";
		this.elements.form.method = "POST";
		this.elements.form.onsubmit = function(){
			
			try {
				
				var failure = function() {
					Ext.Msg.alert(ORYX.I18N.Feedback.failure, ORYX.I18N.Feedback.failureMsg);
	                this.facade.raiseEvent({
	                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
	                });
					// show dialog again with old information
					this.toggleDialog();
				}
				
				var success = function(transport) {
					if (transport.status < 200 || transport.status >= 400) {
						return failure(transport);
					}
					this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_STATUS,
						text:ORYX.I18N.Feedback.success
					});
					resetForm();
				}
				
			
				this.elements.form.model.value = this.facade.getSerializedJSON();
				this.elements.form.environment.value = this.getEnv();
			
				var params = {};
				$A(this.elements.form.elements).each(function(element){
					params[element.name] = element.value;
				});
				params["name"]= ORYX.Editor.Cookie.getParams().identifier;
				params["subject"] = ("[" + params["subject"] + "] " + params["title"]);
				this.facade.raiseEvent({
					type:ORYX.CONFIG.EVENT_LOADING_STATUS,
					text:ORYX.I18N.Feedback.sending
				});
				new Ajax.Request(ORYX.CONFIG.ROOT_PATH + "feedback", {
					method: "POST",
					parameters: params,
					onSuccess: success.bind(this),
					onFailure: failure.bind(this)
				});
			
				// hide dialog immediately 
				this.toggleDialog();
			}
			catch(e) {
				failure();
				console.warn(e);
			}
			// stop form submission through browser
			return false; 
		}.bind(this);
		
		
		// create input fields
		var fieldset = document.createElement("div");
			fieldset.className = "fieldset";
		    
		var f_subject = document.createElement("input");
		    f_subject.type = "hidden";
			f_subject.name = "subject";
			f_subject.style.display = "none";
		
		var description = document.createElement("textarea");
			description._defaultText = ORYX.I18N.Feedback.descriptionDesc;
		    description.name = "description";
		Event.observe(description, "focus", fieldOnFocus.bindAsEventListener());
		Event.observe(description, "blur", fieldOnBlur.bindAsEventListener());
		
		var title = document.createElement("input");
			title._defaultText = ORYX.I18N.Feedback.titleDesc;
			title.type = "text";
			title.name = "title";
		Event.observe(title, "focus", fieldOnFocus.bindAsEventListener());
		Event.observe(title, "blur", fieldOnBlur.bindAsEventListener());
			
		var mail = document.createElement("input");
			mail._defaultText = ORYX.I18N.Feedback.emailDesc;
			mail.type = "text";
			mail.name = "email";
		Event.observe(mail, "focus", fieldOnFocus.bindAsEventListener());
		Event.observe(mail, "blur", fieldOnBlur.bindAsEventListener());
		
		var submit = document.createElement("input");
			submit.type = "button";
			submit.className = "submit";
			submit.onclick=this.elements.form.onsubmit;
			if (ORYX.I18N.Feedback.submit) {
				submit.value = ORYX.I18N.Feedback.submit;
			}
			
		var environment = document.createElement("input");
			environment.name = "environment";
			environment.type = "hidden";
			environment.style.display = "none";
			
		var model = document.createElement("input");
			model.name = "model"
			model.type = "hidden";
			model.style.display = "none";
			
		fieldset.appendChild(f_subject);
		fieldset.appendChild(description);
		fieldset.appendChild(title);
		fieldset.appendChild(mail);
		fieldset.appendChild(environment);
		fieldset.appendChild(model);
		fieldset.appendChild(submit);
		
		// (p)reset default values of input fields
		resetForm();
			
		// create subjects
		var list = document.createElement("ul");
	    list.setAttribute("class", "subjects");
		
		var l_subjects = [];
		
		$A(ORYX.I18N.Feedback.subjects).each( function(subject, index){
			try {
				
				// create list item
				var item = document.createElement("li");
					item._subject = subject.id;
				    item.className = subject.id;
					item.innerHTML = subject.name;
					item.style.width = parseInt(100/$A(ORYX.I18N.Feedback.subjects).length)+"%"; // set width corresponding to number of subjects
				
				// add subjects to list
				l_subjects.push(item);
				list.appendChild(item);

				var handler = function(){
					l_subjects.each(function(element) {
						if (element.className.match(subject.id)) { // if current element is selected
							element.className = element._subject + " active";
							f_subject.value = subject.name;
							
							// update description, depending on subject if input field is empty
							if (description.value == description._defaultText) {
								description.value = subject.description;
							}
							
							// set _defaultText to newly selected subject
							description._defaultText = subject.description;
							
							// set info pane if appropriate
							if (subject.info && (""+subject.info).strip().length > 0) {
								this.elements.info.innerHTML = subject.info;
							}
							else {
								this.elements.info.innerHTML = ORYX.I18N.Feedback.info || "";
							}
						}
						else {
							element.className = element._subject;
						}
					}.bind(this));
				}.bind(this);
				
				// choose/unchoose topics
				Event.observe(item, "click", handler);
				
				// select last item
				if (index == (ORYX.I18N.Feedback.subjects.length - 1)) {
					description.value = "";
					description._defaultText = "";
					
					handler();
				}
				
			} // if something goes wrong, we wont give up, just ignore it
			catch (e) {
				ORYX.Log.warn("Incomplete I10N for ORYX.I18N.Feedback.subjects", subject, ORYX.I18N.Feedback.subjects)
			}
		}.bind(this));
	
		this.elements.form.appendChild(list);
		this.elements.form.appendChild(fieldset);
		
		this.elements.info = document.createElement("div");
		this.elements.info.setAttribute("class", "info");
		this.elements.info.innerHTML = ORYX.I18N.Feedback.info || "";
		
		var head = document.createElement("div");
			head.setAttribute("class", "head");

    	this.elements.dialog = document.createElement("div");
		this.elements.dialog.setAttribute("class", "dialog");
		this.elements.dialog.appendChild(head);
		this.elements.dialog.appendChild(this.elements.info);
		this.elements.dialog.appendChild(this.elements.form);

		
		this.elements.container.appendChild(this.elements.dialog);
		
    	return this.elements.dialog;
    },

    getEnv: function(){
        var env = "";
        
        env += "Browser: " + navigator.userAgent;
        
        env += "\n\nBrowser Plugins: ";
        if (navigator.plugins) {
            for (var i = 0; i < navigator.plugins.length; i++) {
                var plugin = navigator.plugins[i];
                env += plugin.name + ", ";
            }
        }
        
        if ((typeof(screen.width) != "undefined") && (screen.width && screen.height)) 
            env += "\n\nScreen Resolution: " + screen.width + 'x' + screen.height;
        
        return env;
    }
});
