if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.PatternCreator = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        this.facade.registerOnEvent(ORYX.CONFIG.CREATE_PATTERN, this.handleCreatePattern.bind(this));
        this.patternShapes = {};
        this.patternPositions = {};
        this.selectedRoots = [];
        this.selectedRootsCount;
        this.createdElementCount;

        // TODO - finish pattern creation from selected nodes
//        this.facade.offer({
//            'name': "Create Pattern",
//            'functionality': this.createPatternFromSelection.bind(this),
//            'group': "pattern",
//            'icon': ORYX.BASE_FILE_PATH + "images/pattern.png",
//            'description': "Create a Workflow pattern from selection",
//            'index': 1,
//            'minShape': 0,
//            'maxShape': 0,
//            'isEnabled': function(){
//                profileParamName = "profile";
//                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//                regexa = new RegExp( regexSa );
//                profileParams = regexa.exec( window.location.href );
//                profileParamValue = profileParams[1];
//                return profileParamValue == "jbpm";
//            }.bind(this)
//        });
    },
    handleCreatePattern : function(options) {
        if(options && options.pid && options.pdata && options.pos) {
            this.patternShapes = {};
            this.patternPositions = {};
            this.selectedRoots = [];
            this.selectedRootsCount = 0;
            this.createdElementCount = 0;
            for(var i=0; i<options.pdata.length; i++) {
                var pattern = options.pdata[i];
                if(pattern.id == options.pid) {
                    var patternElements = pattern.elements;
                    var selectedElements = this.facade.getSelection();
                    selectedElements.each(function(shape) {
                        if(shape instanceof ORYX.Core.Node) {
                            this.selectedRoots[this.selectedRootsCount] = shape;
                            this.selectedRootsCount++;
                        }
                    }.bind(this));
                    var patternRoots = this.getPatternRoots(patternElements);
                    if(this.selectedRoots.length > 0 && (this.selectedRoots.length != patternRoots.length)) {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.patternCreator.errorAttaching,
                            title       : ''

                        });
                        return;
                    }
                    for(var j=0; j<patternElements.length; j++) {
                        var element = patternElements[j];
                        if(this.patternShapes[element.id] === undefined) {
                            this.createElement(element, options);
                            this.createElementChildren(element, patternElements);
                        } else {
                            this.createElementChildren(element, patternElements);
                        }
                    }
                }
            }
            this.facade.setSelection([]);
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.patternCreator.invalidData,
                title       : ''

            });
        }
    },
    getPatternRoots : function(patternElements) {
        var roots = [];
        var i = 0;
        for(var j=0; j<patternElements.length; j++) {
            var element = patternElements[j];
            if(element.parent.length == 0) {
                roots[i] = element;
                i++;
            }
        }
        return roots;
    },
    findChildObject : function(elementChild, patternElements) {
        for(var i=0; i<patternElements.length; i++) {
            var ele = patternElements[i];
            if(ele.id == elementChild) {
                return ele;
            }
        }
        return undefined;
    },
    createElement : function(element, options) {
        // shape.absoluteXY
        // shape.absoluteCenterXY
        if(element.parent.length == 0 && this.selectedRoots.length > 0) {
            // substitute root element

            this.patternShapes[element.id] = this.selectedRoots[this.createdElementCount];
            this.patternPositions[element.id] = this.selectedRoots[this.createdElementCount].absoluteCenterXY();
            this.createdElementCount++;
            return;
        }
        var elementPosition = {x:0, y:0};
        if(this.patternPositions[element.id] === undefined) {
            elementPosition.x = options.pos.x;
            elementPosition.y = options.pos.y;
        } else {
            elementPosition.x = this.patternPositions[element.id].x;
            elementPosition.y = this.patternPositions[element.id].y;
        }
        elementPosition.x += element.xyOffset[0];
        elementPosition.y += element.xyOffset[1];
        var elementOptions = {
            type: element.nodetype,
            namespace: element.namespace,
            connectingType: element.connectingType,
            position: elementPosition,
            parent:ORYX.EDITOR._canvas
        };
        this.patternShapes[element.id] = this.facade.createShape(elementOptions);
        this.patternPositions[element.id] = elementPosition;
        //this.patternShapes[element.id].setProperty("oryx-name", element.name);
        this.patternShapes[element.id].refresh();
        this.facade.getCanvas().update();

    },
    createElementChildren : function(element, patternElements) {
        var elementChildren = element.children;
        for(var k=0;k<elementChildren.length;k++) {
            var elementChild = elementChildren[k];
            if(this.patternShapes[elementChild] === undefined) {
                var elementChildObject = this.findChildObject(elementChild, patternElements);
                if(elementChildObject) {
                    var childPosition = {x:0, y:0};
                    childPosition.x = this.patternPositions[element.id].x;
                    childPosition.y = this.patternPositions[element.id].y;
                    childPosition.x += elementChildObject.xyOffset[0];
                    childPosition.y += elementChildObject.xyOffset[1];
                    var elementChildOptions = {
                        type: elementChildObject.nodetype,
                        namespace: elementChildObject.namespace,
                        connectingType: elementChildObject.connectingType,
                        connectedShape: this.patternShapes[element.id],
                        position: childPosition,
                        parent:ORYX.EDITOR._canvas
                    };
                    this.patternShapes[elementChildObject.id] = this.facade.createShape(elementChildOptions);
                    this.patternPositions[elementChildObject.id] = childPosition;
                    //this.patternShapes[elementChildObject.id].setProperty("oryx-name", elementChildObject.name);
                    this.patternShapes[elementChildObject.id].refresh();
                    this.facade.getCanvas().update();
                }
            } else {
                var con;
                var elementChildObject = this.findChildObject(elementChild, patternElements);
                var stencil = ORYX.Core.StencilSet.stencil(elementChildObject.connectingType);
                con = new ORYX.Core.Edge({'eventHandlerCallback':this.facade.raiseEvent }, stencil);
                con.dockers.first().setDockedShape(this.patternShapes[element.id]);
                var magnet = this.patternShapes[element.id].getDefaultMagnet();
                var cPoint = magnet ? magnet.bounds.center() : this.patternShapes[element.id].bounds.midPoint();
                con.dockers.first().setReferencePoint( cPoint );
                con.dockers.last().setDockedShape(this.patternShapes[elementChild]);
                con.dockers.last().setReferencePoint(this.patternShapes[elementChild].getDefaultMagnet().bounds.center());
                this.facade.getCanvas().add(con);
                this.facade.getCanvas().update();
            }
        }
    },
    createPatternFromSelection : function() {
        var selection = this.facade.getSelection();
        if(selection && selection.size() > 0) {
            var parentShapes = this.findParentShapes(selection);
            if(parentShapes && parentShapes.size() > 0) {
                var patternNameField = new Ext.form.TextField({ fieldLabel: ORYX.I18N.patternCreator.patternName, allowBlank: false, id: 'patternName', regex: /^[a-z0-9 \-\.\_]*$/i });
                var patternForm = new Ext.form.FormPanel({
                    baseCls: 		'x-plain',
                    labelWidth: 	150,
                    labelAlign: 'right',
                    bodyStyle:'padding:15x 15px 15px 15px',
                    defaultType: 	'textfield',
                    items: [
                        patternNameField
                    ]
                });
                var dialog = new Ext.Window({
                    layout		: 'anchor',
                    autoCreate	: true,
                    title		: ORYX.I18N.patternCreator.create,
                    height		: 150,
                    width		: 400,
                    modal		: true,
                    collapsible	: false,
                    fixedcenter	: true,
                    shadow		: true,
                    resizable   : true,
                    proxyDrag	: true,
                    autoScroll  : true,
                    keys:[{
                        key	: 27,
                        fn	: function(){
                            dialog.hide()
                        }.bind(this)
                    }],
                    items		:[patternForm],
                    listeners	:{
                        hide: function(){
                            dialog.destroy();
                        }.bind(this)
                    },
                    buttons		: [{
                        text: ORYX.I18N.PropertyWindow.ok,
                        handler: function(){

                            // todo finish
                            //alert(patternNameField.getValue());

                            dialog.hide()
                        }.bind(this)
                    }, {
                        text: ORYX.I18N.PropertyWindow.cancel,
                        handler: function(){
                            dialog.hide()
                        }.bind(this)
                    }]
                });

                dialog.show();
            } else {
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.patternCreator.invalidSelect,
                    title       : ''

                });
            }
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.patternCreator.noNodesSel,
                title       : ''

            });
        }
    },
    findParentShapes : function(selection) {
        var parents = [];
        var i = 0;
        selection.each(function(shape) {
            if(shape.getIncomingShapes() && shape.getIncomingShapes().size() > 0) {
                if(!this.isInSelection(selection, shape.getIncomingShapes())) {
                    if(shape instanceof ORYX.Core.Node) {
                        parents[i] = shape;
                        i++;
                    }
                }
            } else {
                parents[i] = shape;
                i++;
            }
        }.bind(this));
        return parents;
    },
    isInSelection : function(selection, shapes) {
        var ret = false;
        if(!shapes || shapes.size() == 0) {
            return false;
        }
        for(var i=0; i < shapes.length; i++) {
            selection.each(function(shape) {
                if(shape.resourceId == shapes[i].resourceId) {
                    ret = true;
                }
            }.bind(this));
        }
        return ret;
    }
});
