
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

var guvnorPopupEditor;

/**
 * Plugin that provides a way to edit constraints in DRL format (or as plain text too).
 * This plugin uses Guvnor Standalone Editor to open a popup with an editor that 
 * you can use to author your rules. 
 * 
 * Example of a field using Regex Text Editor:
 *      {
            "id":"conditionexpression",
            "type":"simpleConstraintExpressionEditor",
            "title":"Expression",
            "title_de":"Expression",
            "value":"",
            "description":"",
            "readonly":false,
            "optional":true,
            "showConstraintEditorWhen": {
                "property": "conditionlanguage",
                "value": "Rule",
             }
        }
 * 
 * This plugin defines 2 new types: simpleConstraintExpressionEditor 
 * and contextAwareConstraintExpressionEditor.
 * 
 * The only difference between them is that contextAwareConstraintExpressionEditor
 * makes use of Working-Sets to constraint the available Fact Classes when
 * authoring rules to only those classes used in the process. This is an experimental
 * feature that is not available yet.
 * 
 * The attribute used to configure this
 * editor is:
 *    1.- showConstraintEditorWhen: this object has 2 attributes:
 *      1.- property: the id of another property of this element. 
 *      2.- value: Only show the popup editor if the property indicated by 
 *      the previous attribute has this value. Otherwise open a plain text editor.
 *      
 * This plugin is also configurable throug config.js using all the ORYX.CONFIG.GUVNOR_*
 * elements     
 */
ORYX.Plugins.ConstraintExpressionEditor = Clazz.extend({
    
    construct: function(facade, ownPluginData){
        this.facade = facade;

        //Set default configuration
        /*
         * If true, the rules are going to be edited under ORYX.CONFIG.GUVNOR_FIXED_PACKAGE package
         * If false, the package is taken from process' package attribute
         */
        ORYX.CONFIG.GUVNOR_USE_FIXED_PACKAGE    =   false;  
        ORYX.CONFIG.GUVNOR_FIXED_PACKAGE        =   "mortgages";
        ORYX.CONFIG.GUVNOR_CATEGORY             =   "Home Mortgage";
        ORYX.CONFIG.GUVNOR_HIDE_RHS             =   true;
        ORYX.CONFIG.GUVNOR_HIDE_ATTRIBUTES      =   true;
        
        console.log(ownPluginData);
        
        //Read properties
        if (ownPluginData.properties) {
            ownPluginData.properties.each( function(property) {			
                    if (property.useFixedPackage) {ORYX.CONFIG.GUVNOR_USE_FIXED_PACKAGE = (property.useFixedPackage == "true");}		
                    if (property.fixedPackage) {ORYX.CONFIG.GUVNOR_FIXED_PACKAGE = property.fixedPackage;}
                    if (property.category) {ORYX.CONFIG.GUVNOR_CATEGORY = property.category;}
                    if (property.hideRHS) {ORYX.CONFIG.GUVNOR_HIDE_RHS = (property.hideRHS == "true");}
                    if (property.hideAttributes) {ORYX.CONFIG.GUVNOR_HIDE_ATTRIBUTES = (property.hideAttributes == "true");}
            }.bind(this));
        }
        
        ORYX.FieldEditors["simpleconstraintexpressioneditor"] = new ORYX.Plugins.ConstraintExpressionEditor.SimpleConstraintExpressionEditorFactory();
        ORYX.FieldEditors["contextawareconstraintexpressioneditor"] = new ORYX.Plugins.ConstraintExpressionEditor.ContextAwareConstraintExpressionEditorFactory();
    }
}); 


ORYX.Plugins.ConstraintExpressionEditor.SimpleConstraintExpressionEditorFactory = Clazz.extend({
    construct: function(){

    },          
    /**
     * This function gets executed by propertyWindow in its own context,
     * so this = propertyWindow
     */
    init: function(){
        //arguments: key, pair, icons, index
        var key = arguments[0];
        var pair = arguments[1];
        var index = arguments[3];
        
        return new ORYX.Plugins.ConstraintExpressionEditor.BaseConstraintExpressionEditorFactory().createEditor.bind(this)(false, key, pair, index);
        
    }        
});

ORYX.Plugins.ConstraintExpressionEditor.ContextAwareConstraintExpressionEditorFactory = Clazz.extend({
    
    construct: function(){
        
    },          
    /**
     * This function gets executed by propertyWindow in its own context,
     * so this = propertyWindow
     */
    init: function(){
        //arguments: key, pair, icons, index
        var key = arguments[0];
        var pair = arguments[1];
        var index = arguments[3];
        
        return new ORYX.Plugins.ConstraintExpressionEditor.BaseConstraintExpressionEditorFactory().createEditor.bind(this)(true, key, pair, index);
        
    }        
});

ORYX.Plugins.ConstraintExpressionEditor.BaseConstraintExpressionEditorFactory = Clazz.extend({
    construct: function(){
        
    },
    /**
     * This function gets (indirectly) executed by propertyWindow in its own context,
     * so this = propertyWindow
     */
    createEditor: function(contextAware, key, pair, index){
        
        var showPopup = true;
        //only show Guvnor Editor if 'showConstraintEditorWhen' is met
        if (pair._jsonProp.showConstraintEditorWhen){
            var prop = pair._jsonProp.showConstraintEditorWhen.property;
            if (!prop){
                alert ("Error reading definition of showConstraintEditorWhen: 'property' is missing!");
                return null;
            }
            var value = pair._jsonProp.showConstraintEditorWhen.value;
            if (!value){
                alert ("Error reading definition of showConstraintEditorWhen: 'value' is missing!");
                return null;
            }
            showPopup = this.shapeSelection.shapes[0].properties[pair.prefix() + "-" + prop] == value;
        }
        
        var editor; 

        
        if (showPopup){
            var initialShape = contextAware?this.shapeSelection.shapes[0]:undefined;
            editor = new Ext.form.GuvnorPopupEditor(initialShape, function(_value){
                this.editDirectly(key, _value);
            }.bind(this));
            
            //Guvnor's iframe needs a global object to stablish communication with.
            guvnorPopupEditor = editor;
        }else{
            editor = new Ext.form.ComplexTextField({
                    allowBlank: pair.optional(),
                    dataSource:this.dataSource,
                    grid:this.grid,
                    row:index,
                    facade:this.facade
            });
            editor.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:editor});							
        }

        return new Ext.Editor(editor);
        
    }
});

Ext.form.GuvnorPopupEditor = function(_srcShape, _onSave){

    var brlCommentString = "#-#"

    var brlValue = "";
    
    var drlValue = "";
    
    var onSave = _onSave;
    
    var srcShape = _srcShape;

    Ext.form.GuvnorPopupEditor.superclass.constructor.call(this,{
        defaultAutoCreate : {
            tag: "textarea", 
            rows:1, 
            style:"height:16px;overflow:hidden;"
        },

        /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
        onTriggerClick : function(){
		
            if(this.disabled){
                return;
            }	

            var _width = document.body.clientWidth - 20;
            var _height = document.body.clientHeight - 20;

            //Guvnor url
            var _guvnorURL= ORYX.EXTERNAL_PROTOCOL+"://"+ORYX.EXTERNAL_HOST+"/"+ORYX.EXTERNAL_SUBDOMAIN+"/org.drools.guvnor.Guvnor/standaloneEditorServlet";
        
            //Guvnor editor parameters
            var _guvnorParameters = [];
            _guvnorParameters.push({
                name:"client", 
                value: ''
            });
            
            var _pkg = ORYX.CONFIG.GUVNOR_FIXED_PACKAGE;
            
            if (!ORYX.CONFIG.GUVNOR_USE_FIXED_PACKAGE){
                var _json = ORYX.EDITOR.getJSON();
                if (_json.properties['package'] && _json.properties['package'] != ""){
                    
                }else{
                    alert ("Please configure Process' 'package' attribute first");
                    return;
                }
                _pkg = _json.properties['package'];
            }
            
            _guvnorParameters.push({
                name:"packageName", 
                value: _pkg
            });
            
            _guvnorParameters.push({
                name:"categoryName", 
                value: ORYX.CONFIG.GUVNOR_CATEGORY
            });
            _guvnorParameters.push({
                name:"hideRuleRHS", 
                value: ''+ORYX.CONFIG.GUVNOR_HIDE_RHS
            });
            _guvnorParameters.push({
                name:"hideRuleAttributes", 
                value: ''+ORYX.CONFIG.GUVNOR_HIDE_ATTRIBUTES
            });
        
            if (brlValue == ""){
                _guvnorParameters.push({
                    name:"brlSource", 
                    value: '<rule><name>Condition Constraint</name><modelVersion>1.0</modelVersion><attributes></attributes><metadataList/><lhs></lhs><rhs></rhs></rule>'
                });
            }else{
                _guvnorParameters.push({
                    name:"brlSource", 
                    value: brlValue
                });
            }
        
           //get the available Classes from the node's path
           if (srcShape){
               
               var _modelEntitiesInPath = collectNodesInPath(srcShape, new RegExp("ModelEntity"));
               _modelEntitiesInPath = _modelEntitiesInPath.concat(collectNodesInPath(srcShape, new RegExp("Model_")));
               
               if (!_modelEntitiesInPath || _modelEntitiesInPath.length == 0){
                   alert ("You must define at least 1 Model Entity in your process!");
                   return;
               }

               var errors = [];

               //convert each Model Entity into Working Set config data
               //and add it to the request parameter
               var _workingSetConfigData = [];
               _modelEntitiesInPath.each(function(_modelEntity){
                   var _validFact = _modelEntity.properties['oryx-modelentity'];
                   var _factField = _modelEntity.properties['oryx-fieldconstraint'];
                   var _matchesString = _modelEntity.properties['oryx-constraintvalue'];
                   
                   if (!_validFact){
                       errors.push("Fact Name is mandatory!");
                       return;
                   }
                   if (!_factField){
                       errors.push("You must specify a field for '"+_validFact+"' Model Entity");
                       return;
                   }
                   if (!_matchesString){
                       errors.push("You must specify a value for '"+_validFact+"."+_factField+"' Model Entity");
                       return;
                   }
                   
                   _workingSetConfigData.push("{"+_validFact+"--@--"+_factField+"--@--"+_matchesString+"}");
                    
               });

               if (errors.length > 0){
                   this.showErrors(errors);
                   return;
               }

               //get working-set definition for the model entities found in path
               var workingSetXML = "";
               
               new Ajax.Request("/workingSet", {
			asynchronous: false,
			method: 'POST',
                        parameters: {
                            "action": "createWorkingSetWithMandatoryConstraint",
                            "config": _workingSetConfigData
                        },
			onSuccess: function(transport){
				workingSetXML = transport.responseText;
			}.bind(this),
			onFailure: (function(transport){
				errors.push("Error getting Working Set Definition: "+transport.responseText);
			}).bind(this)
		});

                if (errors.length > 0){
                    this.showErrors(errors);
                    return;
                }
                
                alert (workingSetXML);
                
                //add Working Set XML to request
                _guvnorParameters.push({
                    name: "workingSetXMLDefinitions", 
                    value: workingSetXML
                });

           }
           
            if (_guvnorParameters.length > 0){
                var i = 0;
                var separator = "";
                _guvnorURL += "?";
                for (i = 0; i < _guvnorParameters.length; i++){
                    var p = _guvnorParameters[i];
                    var uriComponent= separator + p.name+"="+encodeURIComponent(p.value);
                    if (separator == ""){
                        separator = "&amp;"; // %26
                    }
                
                    _guvnorURL += uriComponent;
                }
            }
            
            var w=new Ext.Window({
                id          : 'guvnorWindow',
                layout      : 'fit',
                width       : _width,
                height      : _height,
                closeAction :'close',
                plain       : true,
                modal       : true,
     
                title       : 'Title',
                autoScroll  : true,
                resizable: true,
                html: '<iframe id="guvnorFrame" name="guvnorFrame" width="'+_width+'" height="'+_height+'"  onload="attachCallbacksToGuvnor();" src="'+_guvnorURL+'"></iframe>'
            });
        
            w.show();
        },
        
        showErrors : function (errors){
            var msg = "Errors:";
            
            errors.each(function(error){
                msg += "\n\t"+error;
            });
            
            alert(msg);
        },
    
        encodeBRL : function(){
            var _value = "";
            
            //process the brl part
            if (brlValue){
                //split the brl into single lines
                var brlLines = brlValue.split("\n");
                for (var i=0; i < brlLines.length; i++) {
                    var brlLine = brlLines[i];

                    //encode its content to avoid forbidden xml elements
                    brlLine = encodeURIComponent(brlLine);
                    
                    //comment the line: jbpm will ignore this brl.
                    brlLine = brlCommentString + brlLine;
                    
                    //append the line to the final value
                    _value += brlLine + "\n";
                }
            }
            
            return _value;
        },
        
        trimDRL : function(){
            var _value = ""
            
            //we only need the RHS of the rule
            if (drlValue){
                var validLine = false;
                var drlLines = drlValue.split("\n");
                for (var i=0; i < drlLines.length; i++) {
                    var drlLine = drlLines[i];
                    
                    //trim
                    drlLine = drlLine.replace(/^\s+/, '').replace(/\s+$/, '');
                    
                    if (drlLine == "then"){
                        //stop the loop
                        break;
                    }
                    
                    if (validLine){
                        _value += drlLine + "\n";
                    }
                    
                    if (drlLine == "when"){
                        validLine = true
                    }
                    
                }
            }
            
            return _value;
        },
        
        getValue : function(){
            var value = "";
            
            //process the brl part
            value += this.encodeBRL();
            
            value += "\n";
            
            //process the drl part
            value += this.trimDRL();
            
            //alert (value);
            return value;
        },
        
        closeGuvnorWindow : function (){
            Ext.getCmp('guvnorWindow').close();
        },

        guvnorSaveAndCloseButtonCallback : function (){
            getGuvnorFrame(top).guvnorEditorObject.getBRL(function(brl){
                this.setBRLValue(brl);

                getGuvnorFrame(top).guvnorEditorObject.getDRL(function(drl){
                    this.setDRLValue(drl);

                    this.closeGuvnorWindow();
                    
                    if (onSave){
                        onSave(this.getValue());
                    }
                }.bind(this));

            }.bind(this));
        },

        guvnorCancelButtonCallback : function(){
            this.closeGuvnorWindow();
        },
    
        setValue : function(value){
            drlValue = "";
            brlValue = "";
            
            var brlCommentPattern = new RegExp("^"+brlCommentString+".*");
            var lines = value.split("\n");
            for (var i=0; i < lines.length; i++) {
                var line = lines[i];
                
                if (line.match(brlCommentPattern)){
                    brlValue += decodeURIComponent(line.substring(brlCommentString.length))+"\n"; 
                }else{
                    drlValue += line + "\n";
                }
            }
            
        },
        
        getDRLValue : function(){
            return drlValue;
        },
    
        setDRLValue : function(drl){
            drlValue = drl;
        },
        
        getBRLValue : function(){
            return brlValue;
        },
    
        setBRLValue : function(brl){
            brlValue = brl;
        }
    });
}

Ext.extend(Ext.form.GuvnorPopupEditor,Ext.form.TriggerField,{});

function attachCallbacksToGuvnor(){
    if (!getGuvnorFrame(top).guvnorEditorObject){
        setTimeout('this.attachCallbacksToGuvnor()', 1000);
        return;
    }
    getGuvnorFrame(top).guvnorEditorObject.registerAfterSaveAndCloseButtonCallbackFunction(guvnorPopupEditor.guvnorSaveAndCloseButtonCallback.bind(guvnorPopupEditor));
    getGuvnorFrame(top).guvnorEditorObject.registerAfterCancelButtonCallbackFunction(guvnorPopupEditor.guvnorCancelButtonCallback.bind(guvnorPopupEditor));
}

function getGuvnorFrame(context){
    if (context.frames["guvnorFrame"]){
        return context.frames["guvnorFrame"];
    }
    
    for (var i=0; i < context.frames.length; i++){
        var frm = getGuvnorFrame(context.frames[i]);
        if (frm){
            return frm;
        }
    }
    
    return null;
}

function collectNodesInPath(srcNode, nodeStencilId){
    
    if (!srcNode.incoming || srcNode.incoming.length == 0){
        return [];
    }
    
    var foundNodes = [];
    srcNode.incoming.each(function(incomingNode){
        if (incomingNode._stencil._jsonStencil.id.match(nodeStencilId)){
            foundNodes.push(incomingNode);
        }
        
        //inspect children 
        foundNodes = foundNodes.concat(collectNodesInPath(incomingNode, nodeStencilId));
        
    });
    
    return foundNodes;
}
