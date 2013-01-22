if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.NodeXMLViewer = Clazz.extend({
	sourceEditor: undefined,
	
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_NODEXML_SHOW, this.showNodeXML.bind(this));
		this.sourceMode = false;
	},
	showNodeXML: function(options) {
		if(options && options.nodesource) {
			this.sourceEditor = undefined;
			
			var nextTAID = Ext.id();
			var cf = new Ext.form.TextArea({
				id: nextTAID,
   	            fieldLabel:"Node Source",
   	            value:options.nodesource,
   	            autoScroll:true
   	            });
			
			var nextWINID = Ext.id();
			this.win = new Ext.Window({
   				width:600,
   				id: nextWINID,
   				height:550,
   				layout: 'fit',
   				title:'Node Source',
   				items: [cf],
   				buttons		: [{
					text : 'Close',
					handler:function(){
						this.win.hide();
						this.sourceEditor = undefined;
					}.bind(this)
				}]
   				});
			this.win.show();
			this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
   			this.sourceEditor = CodeMirror.fromTextArea(document.getElementById(nextTAID), {
   			  mode: "application/xml",
   			  lineNumbers: true,
   			  lineWrapping: true,
   			  onGutterClick: this.foldFunc
   			});
		} else {
			Ext.Msg.alert('Node source was not specified.');
		}
	}
});