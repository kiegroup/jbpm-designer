if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.VoiceCommand = Clazz.extend({
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_VOICE_COMMAND, this.handleVoiceCommand.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_ADD_START_EVENT, this.addNode.bind(this, "StartNoneEvent"));
		
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_USER, this.updateTask.bind(this, "User"));
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_SCRIPT, this.updateTask.bind(this, "Script"));
		
		this.commands = this._initCommands();
		// register soundex
		String.prototype.soundex = function(p){
			var i, j, l, r, p = isNaN(p) ? 4 : p > 10 ? 10 : p < 4 ? 4 : p,
			m = {BFPV: 1, CGJKQSXZ: 2, DT: 3, L: 4, MN: 5, R: 6},
			r = (s = this.toUpperCase().replace(/[^A-Z]/g, "").split("")).splice(0, 1);
			for(i = -1, l = s.length; ++i < l;)
				for(j in m)
					if(j.indexOf(s[i]) + 1 && r[r.length-1] != m[j] && r.push(m[j]))
						break;
			return r.length > p && (r.length = p), r.join("") + (new Array(p - r.length + 1)).join("0");
		};
	},
	handleVoiceCommand : function(options) {
		if(options && options.entry) {
			if(options.entry.length > 0) {
				var found = false;
				for(var c in this.commands) {
				    if(this.commands.hasOwnProperty(c)) {
				    	var parts = c.split(",");
				    	for(var i=0; i < parts.length; i++) {
			    			var nextPart = parts[i];
			    			if(options.entry.soundex() == nextPart.soundex()) {
					    		found = true;
					    		this.facade.raiseEvent({
						            type: this.commands[c]
						        });
					    		break;
					    	}
			    		}
				    	if(found) {
				    		break;
				    	}
				    }
				}
				if(!found) {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.voiceCommand.commandNotFound+': ' + options.entry,
                        title       : ''

                    });
				}
			} else {
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.voiceCommand.invalidcommand,
                    title       : ''

                });
			}
		} else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.voiceCommand.invalidcommand,
                title       : ''

            });
		}
	},
	_initCommands : function() {
		var c = {};
		c[ORYX.CONFIG.VOICE_ENTRY_GENERATE_FORMS] = ORYX.CONFIG.VOICE_COMMAND_GENERATE_FORMS;
		c[ORYX.CONFIG.VOICE_ENTRY_GENERATE_IMAGE] = ORYX.CONFIG.VOICE_COMMAND_GENERATE_IMAGE;
		c[ORYX.CONFIG.VOICE_ENTRY_VIEW_SOURCE]    = ORYX.CONFIG.VOICE_COMMAND_VIEW_SOURCE;
		c[ORYX.CONFIG.VOICE_ENTRY_ADD_TASK]       = ORYX.CONFIG.VOICE_COMMAND_ADD_TASK;
		c[ORYX.CONFIG.VOICE_ENTRY_ADD_GATEWAY]    = ORYX.CONFIG.VOICE_COMMAND_ADD_GATEWAY;
		c[ORYX.CONFIG.VOICE_ENTRY_ADD_END_EVENT]  = ORYX.CONFIG.VOICE_COMMAND_ADD_END_EVENT;
		c[ORYX.CONFIG.VOICE_ENTRY_ADD_START_EVENT] = ORYX.CONFIG.VOICE_COMMAND_ADD_START_EVENT;
		c[ORYX.CONFIG.VOICE_ENTRY_TASK_TYPE_USER] = ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_USER;
		c[ORYX.CONFIG.VOICE_ENTRY_TASK_TYPE_SCRIPT] = ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_SCRIPT;
		c[ORYX.CONFIG.VOICE_ENTRY_GATEWAY_TYPE_PARALLEL] = ORYX.CONFIG.VOICE_COMMAND_GATEWAY_TYPE_PARALLEL;
		return c;
	},
	updateTask : function(taskType) {
		var selection = this.facade.getSelection();
		if(selection.length == 1) {
			var shape = selection.first();
			shape.setProperty("oryx-tasktype", taskType);
			shape.refresh();
		}
	},
	addNode : function(nodeName) {
		var commandClass = ORYX.Core.Command.extend({
			construct: function(option, currentParent, canAttach, position, facade){
				this.option = option;
				this.currentParent = currentParent;
				this.canAttach = canAttach;
				this.position = position;
				this.facade = facade;
				this.selection = this.facade.getSelection();
				this.shape;
				this.parent;
			},			
			execute: function(){
				if (!this.shape) {
					this.shape 	= this.facade.createShape(option);
					this.parent = this.shape.parent;
				} else {
					this.parent.add(this.shape);
				}
					
				if( this.canAttach &&  this.currentParent instanceof ORYX.Core.Node && this.shape.dockers.length > 0){
					
					var docker = this.shape.dockers[0];
		
					if( this.currentParent.parent instanceof ORYX.Core.Node ) {
						this.currentParent.parent.add( docker.parent );
					}
												
					docker.bounds.centerMoveTo( this.position );
					docker.setDockedShape( this.currentParent );
					//docker.update();	
				}
		
				this.facade.setSelection([this.shape]);
				this.facade.getCanvas().update();
				this.facade.updateSelection();
			},
			rollback: function(){
				this.facade.deleteShape(this.shape);
				this.facade.setSelection(this.selection.without(this.shape));
				this.facade.getCanvas().update();
				this.facade.updateSelection();
				
			}
		});
							
		var position = {x: 178, y: 209};
		var option = {type: "http://b3mn.org/stencilset/bpmn2.0#"+nodeName, 
				      namespace: "http://b3mn.org/stencilset/bpmn2.0#", 
				      connectingType: true,
				      isHandle: false,
				      position: position,
				      parent: ORYX.EDITOR._canvas};
		
		var command = new commandClass(option, ORYX.EDITOR._canvas, undefined, position, this.facade);
		
		this.facade.executeCommands([command]);
	}
});