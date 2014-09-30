/**
 * Copyright (c) 2009
 * Kai Schlichting
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
Ext.ns("Oryx.Plugins");

ORYX.Plugins.PetriNetSoundnessChecker = ORYX.Plugins.AbstractPlugin.extend({

    hideOverlays: function(){
        //TODO set in constructor!!!
        if(!this.overlayIds) 
            return;
    
        Ext.each(this.overlayIds, function(overlayId){
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
                id: overlayId
            });
        }.bind(this));
    },
    
    getChildShapesByResourceIds: function(resourceIds){
        var shapes = [];
    
        Ext.each(resourceIds, function(resourceId){
            shapes.push(this.facade.getCanvas().getChildShapeByResourceId(resourceId));
        }.bind(this));
        
        return shapes;
    },
    
    /**
       Show overlay on given shape.
       @methodOf ORYX.Plugins.AbstractPlugin.prototype
       @example
       showOverlay(
           myShape,
           { stroke: "green" },
           ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
               "title": "Click the element to execute it!",
               "stroke-width": 2.0,
               "stroke": "black",
               "d": "M0,-5 L5,0 L0,5 Z",
               "line-captions": "round"
           }])
       )
       @param {Oryx.XXX.Shape[]} shapes One shape or array of shapes the overlay should be put on
       @param {Oryx.XXX.Attributes} attributes some attributes...
       @param {Oryx.svg.node} svgNode The svg node which should be used as overlay
       @param {String} [svgNode="NW"] The svg node position where the overlay should be placed
    */
    showOverlay: function(shapes, attributes, svgNode, svgNodePosition ){
        if(!this.overlayIds){
            this.overlayIds = [];
        }
        
        if( !(shapes instanceof Array) ){
            shapes = [shapes]
        }
        
        // Define Shapes
        shapes = shapes.map(function(shape){
            var el = shape;
            if( typeof shape == "string" ){
                el = this.facade.getCanvas().getChildShapeByResourceId( shape );
                el = el || this.facade.getCanvas().getChildById( shape, true );
            }
            return el;
        }.bind(this)).compact();
        
        // Define unified id
        var overlayId = this.type + ORYX.Editor.provideId();
        this.overlayIds.push(overlayId);
        
        this.facade.raiseEvent({
            type        : ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id          : overlayId,
            shapes      : shapes,
            attributes  : attributes,
            node        : svgNode,
            nodePosition: svgNodePosition || "NW"
        });
        
    },

    // Offers the plugin functionality
    construct: function(facade){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
                
        this.facade.offer({
            'name':ORYX.I18N.PetriNetSoundness.checkSoundness,//ORYX.I18N.BPMN2PNConverter.name,
            'functionality': this.showCheckerWindow.bind(this),
            'group': "Verification",
            'icon': ORYX.BASE_FILE_PATH + "images/checker_validation.png",
            'description': ORYX.I18N.PetriNetSoundness.checkSoundness_desc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    showCheckerWindow: function(){
        var plugin = this;
        
        var CheckNode = Ext.extend(Ext.tree.TreeNode, {
            constructor: function(config) {
                if(!config.icon && !this.icon)
                    config.icon = CheckNode.UNKNOWN_STATUS;

                CheckNode.superclass.constructor.apply(this, arguments);
                
                Ext.apply(this, config);
                
                if(this.clickHandler){
                    this.on('click', this.clickHandler.bind(this));
                }
            },

            setIcon: function(status) {
                this.ui.getIconEl().src = status;
            },
            getIcon: function(status) {
                return this.ui.getIconEl().src;
            },
            reset: function(){
                plugin.hideOverlays();
                this.hideMarking();
                // Reset syntax errors
                plugin.facade.raiseEvent({type: ORYX.Plugins.SyntaxChecker.RESET_ERRORS_EVENT});
            },
            hideMarking: function(){
                if(!plugin.marking)
                    return;
            
                for(place in plugin.marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    if(placeShape)//place can be null if removed
                        placeShape.setProperty("oryx-numberoftokens", 0);
                }
                // Show changes
                plugin.facade.getCanvas().update();
                
                plugin.marking = undefined;
            },
            showMarking: function(marking){
                plugin.marking = marking;
            
                for(place in marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    placeShape.setProperty("oryx-numberoftokens", marking[place]);
                }
                // Show changes
                plugin.facade.getCanvas().update();
            },
            showErrors: function(errors){
                // Remove all old error nodes
                Ext.each(this.childNodes, function(child){
                    if(child && child.itemCls === 'error')
                        child.remove();
                });
                
                // Show Unknown status on child nodes
                Ext.each(this.childNodes, function(childNode){
                    // Only change icon if it is in loading state (otherwise structural soundness icon would be replaced)
                    if(childNode.getIcon().search(CheckNode.LOADING_STATUS) > -1){
                        childNode.setIcon(CheckNode.UNKNOWN_STATUS);
                    }
                });
                
                // Show errors
                Ext.each(errors, function(error){
                    this.insertBefore(new CheckNode({
                        icon: CheckNode.ERROR_STATUS,
                        text: error,
                        itemCls: 'error'
                    }), this.childNodes[0]);
                }.bind(this));
            },
            showOverlayWithStep: function(shapeIds){
                Ext.each(shapeIds, function(shapeId, index){
                    plugin.showOverlay(
                        plugin.facade.getCanvas().getChildShapeByResourceId(shapeId), 
                        {
                            fill: "#FB7E02"//orange
                        },
                        ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['text', {
                            "style": "font-size: 16px; font-weight: bold;"
                        }, (index + 1)+"."]),
                        "SE" //position in south east
                    );
                });
            },
            showOverlay: function(shapes){
                if(shapes.length === 0)
                    return;

                if(! shapes[0] instanceof ORYX.Core.Node)
                    shapes = plugin.getChildShapesByResourceIds(shapes)
            
                plugin.showOverlay(
                    shapes, 
                    {
                        fill: "#FB7E02"//orange
                    }
                );
            }
        });
        CheckNode.UNKNOWN_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'asterisk_yellow.png';
        CheckNode.ERROR_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'exclamation.png';
        CheckNode.OK_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'accept.png';
        CheckNode.LOADING_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'loading.gif';
        
        var DeadLocksNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = ORYX.I18N.PetriNetSoundness.tipTerminationCriteria;
            
                DeadLocksNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
            
                if(this.deadLocks.length == 0) return;
                
                var deadLock = node.deadLocks[0];
                this.showOverlayWithStep(deadLock.path);
                this.showMarking(deadLock.marking);
            },
            update: function(deadLocks){
                this.deadLocks = deadLocks;
                this.setIcon(this.deadLocks.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText(ORYX.I18N.PetriNetSoundness.thereIs+(this.deadLocks.length == 0 ? ORYX.I18N.PetriNetSoundness.no : ORYX.I18N.PetriNetSoundness.a)+' '+ORYX.I18N.PetriNetSoundness.pathsDeadLock);
            }
        });
        
        var ImproperTerminatingsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = ORYX.I18N.PetriNetSoundness.tipProperTerminationCriteria;
            
                ImproperTerminatingsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
            
                if(node.improperTerminatings.length == 0) return;
                
                var improperTerminating = node.improperTerminatings[0];
                this.showOverlayWithStep(improperTerminating.path);
                this.showMarking(improperTerminating.marking);
            },
            update: function(improperTerminatings){
                this.improperTerminatings = improperTerminatings;
                
                this.setIcon(this.improperTerminatings.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText(ORYX.I18N.PetriNetSoundness.thereAre +' '+ this.improperTerminatings.length +' ' +ORYX.I18N.PetriNetSoundness.markings);
            }
        });
        
        var DeadTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = ORYX.I18N.PetriNetSoundness.tipDeadTransitionsCriteria;
            
                DeadTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                this.showOverlay(this.deadTransitions);
            },
            update: function(deadTransitions){
                this.deadTransitions = deadTransitions;
                
                this.setIcon(this.deadTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText(ORYX.I18N.PetriNetSoundness.thereAre +' ' + this.deadTransitions.length +' '+ORYX.I18N.PetriNetSoundness.deadTransitions);
            }
        });
        
        var NotParticipatingTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = ORYX.I18N.PetriNetSoundness.tipTransitionParticipationCriteria;
            
                NotParticipatingTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                this.showOverlay(this.notParticipatingTransitions);
            },
            update: function(notParticipatingTransitions){
                this.notParticipatingTransitions = notParticipatingTransitions;
                
                this.setIcon(this.notParticipatingTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText(ORYX.I18N.PetriNetSoundness.thereAre + ' ' + this.notParticipatingTransitions.length +' '+ ORYX.I18N.PetriNetSoundness.transtionsNoParticipants);
            }
        });
        
        this.checkerWindow = new Ext.Window({
            title: ORYX.I18N.PetriNetSoundness.soundnessChecker,
            autoScroll: true,
            width: '500',
            tbar: [
                {
                    text: ORYX.I18N.PetriNetSoundness.check,
                    handler: function(){
                        this.checkerWindow.check();
                    }.bind(this)
                },
                {
                    text: ORYX.I18N.PetriNetSoundness.hideErrors,
                    handler: function(){
                        this.checkerWindow.getTree().getRootNode().reset();
                    }.bind(this)
                },
                '->',
                {
                    text: ORYX.I18N.Save.close,
                    handler: function(){
                        this.checkerWindow.close();
                    }.bind(this)
                }
            ],
            getTree: function(){
                return this.items.get(0);
            },
            check: function(renderAll){
                this.prepareCheck(renderAll);
                this.checkSyntax(this.checkSoundness.bind(this));
            },
            prepareCheck: function(renderAll){//call with renderAll=true if showing for the first time
                var root = this.getTree().getRootNode();
                
                root.reset();
                
                // Set loading status to all child nodes
                Ext.each(root.childNodes, function(childNode){
                    if(renderAll)//this expands all nodes so they're rendered a first time
                        childNode.expand(true);
                    childNode.collapse(true); //collapse deeply
                    childNode.setIcon(CheckNode.LOADING_STATUS);
                });
            },
            checkSyntax: function(callback){
                plugin.facade.raiseEvent({
                    type: ORYX.Plugins.SyntaxChecker.CHECK_FOR_ERRORS_EVENT,
                    onErrors: function(){
                        Ext.Msg.alert(ORYX.I18N.TreeGraphSupport.syntaxCheckName, ORYX.I18N.PetriNetSoundness.syntaxErrors)
                        this.turnLoadingIntoUnknownStatus();
                    }.bind(this),
                    onNoErrors: function(){
                        callback();
                    }
                });
            },
            // All child nodes with loading status get unknown status
            turnLoadingIntoUnknownStatus: function(){
                Ext.each(this.getTree().getRootNode().childNodes, function(childNode){
                    // Only change icon if it is in loading state (otherwise structural soundness icon would be replaced)
                    if(childNode.getIcon().search(CheckNode.LOADING_STATUS) > -1){
                        childNode.setIcon(CheckNode.UNKNOWN_STATUS);
                    }
                });
            },
            checkSoundness: function(){
                var root = this.getTree().getRootNode();
                
                // Check for structural soundness (no server request needed and return, if any has been found       
                if(! root.findChild("id", "structuralSound").check()){
                    this.turnLoadingIntoUnknownStatus();
                    return;
                }
                
                // Check other soundness criteria which needs server requests
                Ext.Ajax.request({
                    url: ORYX.CONFIG.ROOT_PATH + 'checksoundness',
                    method: 'POST',
                    success: function(request){
                        var res = Ext.decode(request.responseText);
                        
                        root.showErrors(res.errors);
                        
                        if(res.errors.length === 0){
                            root.findChild("id", "sound").check(res);
                            root.findChild("id", "weakSound").check(res);
                            root.findChild("id", "relaxedSound").check(res);
                        }
                    },
                    failure: function(){
                    },
                    params: {
                        data: plugin.getSerializedDOM()
                    }
                });
                
            },
            items: [new Ext.tree.TreePanel({
                useArrows: true,
                autoScroll: true,
                rootVisible: false,
                animate: true,
                containerScroll: true,
                
                root: new CheckNode({
                    text: ORYX.I18N.PetriNetSoundness.checks,
                    id: 'source',
                    expanded: true
                }),
                listeners: {
                    render: function(treePanel){
                        var structuralSoundNode = new CheckNode({
                            text: ORYX.I18N.PetriNetSoundness.structuralSound,
                            id: 'structuralSound',
                            /* Returns false when any error has been found */
                            check: function(){
                                this.checkInitialNode.update();
                                this.checkFinalNode.update();
                                this.checkConnectedNode.update(this.checkInitialNode.initialNodes, this.checkFinalNode.finalNodes);
                                
                                if(this.checkInitialNode.hasErrors() || this.checkFinalNode.hasErrors() || this.checkConnectedNode.hasErrors()){
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                    return false;
                                } else {
                                    this.setIcon(CheckNode.OK_STATUS);
                                    return true;
                                }
                            },
                            checkInitialNode: new CheckNode({
                                qtip: ORYX.I18N.PetriNetSoundness.exactlyOneInitialPlace,
                                update: function(){
                                    this.initialNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape.getIncomingShapes().length == 0 && shape.getStencil().id().search(/Place/) > -1){
                                            this.initialNodes.push(shape);
                                        }
                                    }.bind(this));
                                    
                                    this.setText(this.initialNodes.length + ' '+ORYX.I18N.PetriNetSoundness.initialPlacesFound);
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.initialNodes);
                                },
                                hasErrors: function(){
                                    return this.initialNodes.length !== 1;
                                }
                            }),
                            checkFinalNode: new CheckNode({
                                qtip: ORYX.I18N.PetriNetSoundness.exactlyOneFinalPlace,
                                update: function(){
                                    this.finalNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape.getOutgoingShapes().length == 0 && shape.getStencil().id().search(/Place/) > -1){
                                            this.finalNodes.push(shape);
                                        }
                                    }.bind(this));
                                    
                                    this.setText(this.finalNodes.length + ' '+ORYX.I18N.PetriNetSoundness.finalPlacesFound);
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.finalNodes);
                                },
                                hasErrors: function(){
                                    return this.finalNodes.length !== 1;
                                }
                            }),
                            checkConnectedNode: new CheckNode({
                                qtip: ORYX.I18N.PetriNetSoundness.eachNode,
                                update: function(initialNodes, finalNodes){
                                    //Step through without semantic knowledge
                                    if(initialNodes.length !== 1 || finalNodes.length !== 1){
                                        this.setText(ORYX.I18N.PetriNetSoundness.exactlyOneInitAndFinalPlace);
                                        this.setIcon(CheckNode.UNKNOWN_STATUS);
                                        return;
                                    }
                                    
                                    this.notParticipatingNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape instanceof ORYX.Core.Node)
                                            this.notParticipatingNodes.push(shape);
                                    }.bind(this));
                                    
                                    this.passedNodes = [];
                                    
                                    this.findNotParticipatingNodes(initialNodes[0]);
                                    
                                    this.setText(this.notParticipatingNodes.length + ' '+ORYX.I18N.PetriNetSoundness.nodesNoInPath);
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.notParticipatingNodes);
                                },
                                findNotParticipatingNodes: function(currentNode){
                                    this.passedNodes.push(currentNode);
                                    this.notParticipatingNodes.remove(currentNode);
                                    
                                    Ext.each(currentNode.getOutgoingShapes(), function(nextNode){
                                        if(!this.passedNodes.include(nextNode)){
                                            this.findNotParticipatingNodes(nextNode);
                                        };
                                    }.bind(this));
                                },
                                hasErrors: function(){
                                    return this.notParticipatingNodes.length !== 0;
                                }
                            })
                        });
                        structuralSoundNode.appendChild([
                            structuralSoundNode.checkInitialNode,
                            structuralSoundNode.checkFinalNode,
                            structuralSoundNode.checkConnectedNode
                        ]);
                    
                        var soundNode = new CheckNode({
                            text: ORYX.I18N.PetriNetSoundness.sound,
                            id: 'sound',
                            check: function(res){
                                if (res.isSound) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }
                                
                                this.deadTransitionsNode.update(res.deadTransitions);
                                this.improperTerminatingsNode.update(res.improperTerminatings);
                                this.deadLocksNode.update(res.deadLocks);
                            },
                            deadTransitionsNode: new DeadTransitionsNode({}),
                            improperTerminatingsNode: new ImproperTerminatingsNode({}),
                            deadLocksNode: new DeadLocksNode({})
                        });
                        soundNode.appendChild([
                            soundNode.deadTransitionsNode,
                            soundNode.improperTerminatingsNode,
                            soundNode.deadLocksNode
                        ]);
                        
                        var weakSoundNode = new CheckNode({
                            text: ORYX.I18N.PetriNetSoundness.weakSound,
                            id: 'weakSound',
                            check: function(res){
                                if (res.isWeakSound) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }

                                this.improperTerminatingsNode.update(res.improperTerminatings);
                                this.deadLocksNode.update(res.deadLocks);
                            },
                            deadTransitionsNode: new DeadTransitionsNode({}),
                            improperTerminatingsNode: new ImproperTerminatingsNode({}),
                            deadLocksNode: new DeadLocksNode({})
                        });
                        weakSoundNode.appendChild([
                            weakSoundNode.improperTerminatingsNode,
                            weakSoundNode.deadLocksNode
                        ]);
                        
                        var relaxedSoundNode = new CheckNode({
                            text: ORYX.I18N.PetriNetSoundness.relaxedSound,
                            id: 'relaxedSound',
                            check: function(res){
                                if (res.isRelaxedSound) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }
                                
                                this.notParticipatingTransitionsNode.update(res.notParticipatingTransitions);
                            },
                            notParticipatingTransitionsNode: new NotParticipatingTransitionsNode({})
                        });
                        relaxedSoundNode.appendChild([
                            relaxedSoundNode.notParticipatingTransitionsNode
                        ]);
                        
                        treePanel.getRootNode().appendChild([structuralSoundNode, soundNode, weakSoundNode, relaxedSoundNode]);
                        
                    }
                }
            })],
            listeners: {
                close: function(window){
                    this.checkerWindow.getTree().getRootNode().reset();
                }.bind(this)
            }
        });
        
        this.checkerWindow.show();
        this.checkerWindow.check(true);
    }
});