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
        
        var plugin = this;
        
        var CheckNode = Ext.extend(Ext.tree.TreeNode, {
            constructor: function(config) {
                CheckNode.superclass.constructor.apply(this, arguments);
                
                if(this.clickHandler){
                    this.on('click', this.clickHandler)
                }
            },

            setIcon: function(status) {
                this.ui.getIconEl().src = status;
            },
            reset: function(){
                plugin.hideOverlays();
                this.hideMarking();
            },
            hideMarking: function(){
                if(!plugin.marking)
                    return;
            
                for(place in plugin.marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    placeShape.setProperty("oryx-numberoftokens", 0);
                }
                
                plugin.marking = undefined;
            },
            showMarking: function(marking){
                plugin.marking = marking;
            
                for(place in marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    placeShape.setProperty("oryx-numberoftokens", marking[place]);
                }
            }
        });
        CheckNode.UNKNOWN_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'asterisk_yellow.png';
        CheckNode.ERROR_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'exclamation.png';
        CheckNode.OK_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'accept.png';
        CheckNode.LOADING_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'loading.gif';
        
        var DeadLocksNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                this.deadLocks = config.deadLocks;
                config.icon = config.deadLocks.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS;
                config.text = 'There are ' + config.deadLocks.length +' paths which lead to dead locks.';
            
                DeadLocksNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
            
                if(node.deadLocks.length == 0) return;
                
                var deadLock = node.deadLocks[0];
                
                plugin.showOverlay(
                    plugin.getChildShapesByResourceIds(deadLock.path), 
                    {
                        fill: "red"
                    }
                );
                
                this.showMarking(deadLock.marking);
            }
        });
        
        var ImproperTerminatingsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                this.improperTerminatings = config.improperTerminatings;
                config.icon = config.improperTerminatings.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS;
                config.text = 'There are ' + config.improperTerminatings.length + ' markings which don\'t terminate properly.';
            
                ImproperTerminatingsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
            
                if(node.improperTerminatings.length == 0) return;
                
                var improperTerminating = node.improperTerminatings[0];
                
                plugin.showOverlay(
                    plugin.getChildShapesByResourceIds(improperTerminating.path), 
                    {
                        fill: "red"
                    }
                );
                
                this.showMarking(improperTerminating.marking);
            }
        });
        
        var DeadTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                this.deadTransitions = config.deadTransitions;
                config.icon = config.deadTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS;
                config.text = 'There are ' + config.deadTransitions.length + ' dead transitions.';
            
                DeadTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                plugin.showOverlay(
                    plugin.getChildShapesByResourceIds(this.deadTransitions), 
                    {
                        fill: "red"
                    }
                );
            }
        });
        
        var NotParticipatingTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                this.notParticipatingTransitions = config.notParticipatingTransitions;
                config.icon = config.notParticipatingTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS;
                config.text = 'There are ' + config.notParticipatingTransitions.length + ' transitions which aren\'t participating at any process instance.';
            
                NotParticipatingTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                plugin.showOverlay(
                    plugin.getChildShapesByResourceIds(this.notParticipatingTransitions), 
                    {
                        fill: "red"
                    }
                );
            }
        });
        
        new Ext.Window({
             width: '400',
            items: [new Ext.tree.TreePanel({
                useArrows: true,
                autoScroll: true,
                rootVisible: false,
                animate: true,
                containerScroll: true,
                
                root: new Ext.tree.TreeNode({
                    text: 'Checks',
                    id: 'source',
                    expanded: true
                }),
                listeners: {
                    render: function(treePanel){
                        var soundNode = new CheckNode({
                            icon: CheckNode.UNKNOWN_STATUS,
                            text: 'Sound',
                            id: 'sound',
                            listeners: {
                                click: function(node){
                                    node.setIcon(CheckNode.LOADING_STATUS);
                                    Ext.each(node.childNodes, function(childNode){
                                        node.removeChild(childNode);
                                    });
                                    node.expand();
                                    
                                    Ext.Ajax.request({
                                        url: '/oryx/checksoundness',
                                        method: 'POST',
                                        success: function(request){
                                            var res = Ext.decode(request.responseText);
                                            if (res.isSound) {
                                                node.setIcon(CheckNode.OK_STATUS);
                                            }
                                            else {
                                                node.setIcon(CheckNode.ERROR_STATUS);
                                            }
                                            
                                            node.appendChild(new DeadTransitionsNode({
                                                deadTransitions: res.deadTransitions
                                            }));
                                            
                                            node.appendChild(new ImproperTerminatingsNode({
                                                improperTerminatings: res.improperTerminatings
                                            }));
                                            
                                            node.appendChild(new DeadLocksNode({
                                                deadLocks: res.deadLocks
                                            }));
                                        },
                                        failure: function(){
                                        },
                                        params: {
                                            data: plugin.getSerializedDOM()
                                        }
                                    });
                                }
                            }
                        });
                        
                        var weakSoundNode = new CheckNode({
                            icon: CheckNode.UNKNOWN_STATUS,
                            text: 'WeakSound',
                            id: 'weakSound',
                            listeners: {
                                click: function(node){
                                    node.setIcon(CheckNode.LOADING_STATUS);
                                    Ext.each(node.childNodes, function(childNode){
                                        node.removeChild(childNode);
                                    });
                                    node.expand();
                                    
                                    Ext.Ajax.request({
                                        url: '/oryx/checksoundness',
                                        method: 'POST',
                                        success: function(request){
                                            var res = Ext.decode(request.responseText);
                                            if (res.isSound) {
                                                node.setIcon(CheckNode.OK_STATUS);
                                            }
                                            else {
                                                node.setIcon(CheckNode.ERROR_STATUS);
                                            }
                                            
                                            node.appendChild(new ImproperTerminatingsNode({
                                                improperTerminatings: res.improperTerminatings
                                            }));
                                            
                                            node.appendChild(new DeadLocksNode({
                                                deadLocks: res.deadLocks
                                            }));
                                        },
                                        failure: function(){
                                        },
                                        params: {
                                            data: plugin.getSerializedDOM()
                                        }
                                    });
                                }
                            }
                        });
                        
                        var relaxedSoundNode = new CheckNode({
                            icon: CheckNode.UNKNOWN_STATUS,
                            text: 'Relaxed Sound',
                            id: 'relaxedSound',
                            listeners: {
                                click: function(node){
                                    node.setIcon(CheckNode.LOADING_STATUS);
                                    Ext.each(node.childNodes, function(childNode){
                                        node.removeChild(childNode);
                                    });
                                    node.expand();
                                    
                                    Ext.Ajax.request({
                                        url: '/oryx/checksoundness',
                                        method: 'POST',
                                        success: function(request){
                                            var res = Ext.decode(request.responseText);
                                            if (res.isRelaxedSound) {
                                                node.setIcon(CheckNode.OK_STATUS);
                                            }
                                            else {
                                                node.setIcon(CheckNode.ERROR_STATUS);
                                            }
                                            
                                            node.appendChild(new NotParticipatingTransitionsNode({
                                                notParticipatingTransitions: res.notParticipatingTransitions
                                            }));
                                        },
                                        failure: function(){
                                        },
                                        params: {
                                            data: plugin.getSerializedDOM()
                                        }
                                    });
                                }
                            }
                        });
                        
                        treePanel.getRootNode().appendChild([soundNode, weakSoundNode, relaxedSoundNode]);
                    }
                }
            })]
        }).show();
        
        this.facade.offer({
            'name': ORYX.I18N.BPMN2PNConverter.name,
            'functionality': this.exportIt.bind(this),
            'group': ORYX.I18N.BPMN2PNConverter.group,
            'icon': ORYX.PATH + "images/export2.png",
            'description': ORYX.I18N.BPMN2PNConverter.desc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    exportIt: function(){
    
    }
});