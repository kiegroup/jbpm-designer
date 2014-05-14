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


if (!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}

ORYX.Plugins.ShapeRepository = {

	facade: undefined,

	construct: function(facade) {
		this.facade = facade;
		this._currentParent;
		this._canContain = undefined;
		this._canAttach  = undefined;
        this._patternData;

		this.shapeList = new Ext.tree.TreeNode({
			
		})

		var panel = new Ext.tree.TreePanel({
            cls:'shaperepository',
			loader: new Ext.tree.TreeLoader(),
			root: this.shapeList,
			autoScroll:true,
			rootVisible: false,
			lines: false,
			anchors: '0, -30'
		});
		
//		var sorter = new Ext.tree.TreeSorter(panel, {
//		    folderSort: true,
//		    dir: "asc",
//		    sortType: function(node) {
//		        return node.text;
//		    }
//		});
		
		var region = this.facade.addToRegion("west", panel, ORYX.I18N.ShapeRepository.title);
		
//		Ext.Ajax.request({
//            url: ORYX.PATH + "processinfo",
//            method: 'POST',
//            success: function(request) {
//    	   		try {
//    	   			var infopanel = new Ext.Panel({
//    	   				bodyStyle:'background:#eee;font-size:9px;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;',
//    	   				autoScroll:true,
//    	   				lines: false,
//    	   				html: request.responseText,
//    	   				title: 'Process Information'
//    	   			});
//    	   			this.facade.addToRegion("west", infopanel);
//    	   		} catch(e) {
//    	   			ORYX.Log.error("Failed to retrieve Process Info :\n" + e);
//    	   		}
//            }.createDelegate(this),
//            failure: function(){
//            	ORYX.Log.error("Failed to retrieve Process Info");
//            },
//            params: {
//            	profile: ORYX.PROFILE,
//            	uuid : ORYX.UUID
//            }
//        });

        Ext.Ajax.request({
            url: ORYX.PATH + "stencilpatterns",
            method: 'POST',
            success: function(response) {
                try {
                    this._patternData = Ext.decode(response.responseText);
                } catch(e) {
                    ORYX.Log.error("Failed to retrieve Stencil Patterns Data :\n" + e);
                }
            }.createDelegate(this),
            failure: function(){
                ORYX.Log.error("Failed to retrieve Stencil Patterns Data");
            },
            params: {
                profile: ORYX.PROFILE,
                uuid :  window.btoa(encodeURI(ORYX.UUID))
            }
        });

		// Create a Drag-Zone for Drag'n'Drop
		var DragZone = new Ext.dd.DragZone(this.shapeList.getUI().getEl(), {shadow: !Ext.isMac});
		DragZone.afterDragDrop = this.drop.bind(this, DragZone);
		DragZone.beforeDragOver = this.beforeDragOver.bind(this, DragZone);
		DragZone.beforeDragEnter = function(){this._lastOverElement = false; return true}.bind(this);
		
		// Load all Stencilssets
		this.setStencilSets();
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, this.setStencilSets.bind(this));
	},
	
	
	/**
	 * Load all stencilsets in the shaperepository
	 */
	setStencilSets: function() {
		// Remove all childs
		var child = this.shapeList.firstChild;
		while(child) {
			this.shapeList.removeChild(child);
			child = this.shapeList.firstChild;
		}

		// Go thru all Stencilsets and stencils
		this.facade.getStencilSets().values().each((function(sset) {
			
			// For each Stencilset create and add a new Tree-Node
			var stencilSetNode
			
			var typeTitle = ORYX.I18N.propertyNames[sset.title()];
			var extensions = sset.extensions();
//			if (extensions && extensions.size() > 0) {
//				typeTitle += " / " + ORYX.Core.StencilSet.getTranslation(extensions.values()[0], "title");
//			} 

			this.shapeList.appendChild(stencilSetNode = new Ext.tree.TreeNode({
				text:typeTitle, 			// Stencilset Name
				allowDrag:false,
        		allowDrop:false,
				iconCls:'headerShapeRepImg',
	            cls:'headerShapeRep',
				singleClickExpand:true}));
			stencilSetNode.render();
			stencilSetNode.expand();
			// Get Stencils from Stencilset
			var stencils = sset.stencils(this.facade.getCanvas().getStencil(),
										 this.facade.getRules());	
			var treeGroups = new Hash();
			
			// Sort the stencils according to their position and add them to the repository
			stencils = stencils.sortBy(function(value) { return value.position(); } );
			stencils.each((function(value) {
				if (value.hidden()) {
					return;
				}
				
				// Get the groups name
				var groups = value.groups();
				
				// For each Group-Entree
				groups.each((function(group) {
					
					// If there is a new group
					if(!treeGroups[group]) {
                        if(Ext.isIE) {
                            // Create a new group
                            treeGroups[group] = new Ext.tree.TreeNode({
                                text: group,					// Group-Name
                                allowDrag:false,
                                allowDrop:false,
                                iconCls:'headerShapeRepImg', // Css-Class for Icon
                                cls:'headerShapeRepChild',  // CSS-Class for Stencil-Group
                                singleClickExpand:true,
                                expanded:true});
                            treeGroups[group].expand();
                        } else {
                            // Create a new group
                            treeGroups[group] = new Ext.tree.TreeNode({
                                text: group,					// Group-Name
                                allowDrag:false,
                                allowDrop:false,
                                iconCls:'headerShapeRepImg', // Css-Class for Icon
                                cls:'headerShapeRepChild',  // CSS-Class for Stencil-Group
                                singleClickExpand:true});
                        }
						// Add the Group to the ShapeRepository
						stencilSetNode.appendChild(treeGroups[group]);
						treeGroups[group].render();	
					}
					
					// Create the Stencil-Tree-Node
					this.createStencilTreeNode(treeGroups[group], value);	
					
				}).bind(this));
				
				
				
				// If there is no group
				if(groups.length == 0) {
					// Create the Stencil-Tree-Node
					this.createStencilTreeNode(stencilSetNode, value);						
				}

				// sort the groups
				var stencilOrder = ORYX.CONFIG.STENCIL_GROUP_ORDER();
                stencilSetNode.sort(function(a, b) {
                    return stencilOrder[sset.namespace()][a.text] - stencilOrder[sset.namespace()][b.text];
                });

			}).bind(this));
		}).bind(this));
			
		//if (this.shapeList.firstChild.firstChild) {
		//	this.shapeList.firstChild.firstChild.expand(false, true);
		//}	
	},

	createStencilTreeNode: function(parentTreeNode, stencil) {
		// Create and add the Stencil to the Group
        var IdParts = stencil.id().split("#");
        var textTitle = ORYX.I18N.propertyNames[IdParts[1]];
        if(!textTitle) {
            textTitle = stencil.title();
        } else {
            if(textTitle.length <= 0) {
                textTitle = stencil.title();
            }
        }
        var newElement = new Ext.tree.TreeNode({
				text:		textTitle, 		// Text of the stencil
				icon:		decodeURIComponent(stencil.icon()),			// Icon of the stencil
				allowDrag:	false,					// Don't use the Drag and Drop of Ext-Tree
				allowDrop:	false,
				iconCls:	'ShapeRepEntreeImg', 	// CSS-Class for Icon
				cls:		'ShapeRepEntree'		// CSS-Class for the Tree-Entree
				});

		parentTreeNode.appendChild(newElement);		
		newElement.render();	
				
		var ui = newElement.getUI();
		
		// Set the tooltip
		ui.elNode.setAttributeNS(null, "title", stencil.description());
		
		// Register the Stencil on Drag and Drop
		Ext.dd.Registry.register(ui.elNode, {
				node: 		ui.node,
		        handles: 	[ui.elNode, ui.textNode].concat($A(ui.elNode.childNodes)), // Set the Handles
		        isHandle: 	false,
				type:		stencil.id(),			// Set Type of stencil
                title:      stencil.title(),
				namespace:	stencil.namespace()		// Set Namespace of stencil
				});
								
	},
	
	drop: function(dragZone, target, event) {
		this._lastOverElement = undefined;
		
		// Hide the highlighting
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'shapeRepo.added'});
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'shapeRepo.attached'});
		
		// Check if drop is allowed
		var proxy = dragZone.getProxy()
		if(proxy.dropStatus == proxy.dropNotAllowed) { return }
		
		// Check if there is a current Parent
		if(!this._currentParent) { return }
		
		var option = Ext.dd.Registry.getHandle(target.DDM.currentTarget);
		
		var xy = event.getXY();
		var pos = {x: xy[0], y: xy[1]};

		var a = this.facade.getCanvas().node.getScreenCTM();

		// Correcting the UpperLeft-Offset
		pos.x -= a.e; pos.y -= a.f;
		// Correcting the Zoom-Faktor
		pos.x /= a.a; pos.y /= a.d;
		// Correting the ScrollOffset
		pos.x -= document.documentElement.scrollLeft;
		pos.y -= document.documentElement.scrollTop;
		// Correct position of parent
		var parentAbs = this._currentParent.absoluteXY();
		pos.x -= parentAbs.x;
		pos.y -= parentAbs.y;

		// Set position
		option['position'] = pos
		
		// Set parent
		if( this._canAttach &&  this._currentParent instanceof ORYX.Core.Node ){
			option['parent'] = undefined;	
		} else {
			option['parent'] = this._currentParent;
		}
		
		
		var commandClass = ORYX.Core.Command.extend({
			construct: function(option, currentParent, canAttach, position, facade, ttype){
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
		
				//this.currentParent.update();
				//this.shape.update();

                if(ttype && ttype.length > 0 && this.shape instanceof ORYX.Core.Node) {
                    this.shape.setProperty("oryx-tasktype", ttype);
                    this.shape.refresh();
                }

				this.facade.setSelection([this.shape]);
				this.facade.getCanvas().update();
				this.facade.updateSelection();
				
				this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_DROP_SHAPE, shape:this.shape});
				
			},
			rollback: function(){
				this.facade.deleteShape(this.shape);
				
				//this.currentParent.update();

				this.facade.setSelection(this.selection.without(this.shape));
				this.facade.getCanvas().update();
				this.facade.updateSelection();
				
			}
		});

		var position = this.facade.eventCoordinates( event.browserEvent );
        var typeParts = option.type.split("#");
        var isCustom = false;
        if(ORYX.PREPROCESSING) {
            var customParts = ORYX.PREPROCESSING.split(",");
            for (var i = 0; i < customParts.length; i++) {
                if(customParts[i] == typeParts[1]) {
                    isCustom = true;
                }
            }
        }
        if(typeParts[1].startsWith("wp-") && !isCustom) {
            this.facade.raiseEvent({
                type: ORYX.CONFIG.CREATE_PATTERN,
                pid: typeParts[1],
                pdata: this._patternData,
                pos: position
            });
        } else if(typeParts[1].endsWith("Task") && !isCustom) {
            var ttype = typeParts[1];
            ttype = ttype.substring(0, ttype.length - 4);
            option.type = typeParts[0] + "#Task";

            if(ttype.length < 1) {
                if(option.title == "User" ||
                    option.title == "Send" ||
                    option.title == "Receive" ||
                    option.title == "Manual" ||
                    option.title == "Service" ||
                    option.title == "Business Rule" ||
                    option.title == "Script") {
                    ttype = option.title;
                }
            }

            var command = new commandClass(option, this._currentParent, this._canAttach, position, this.facade, ttype);
            this.facade.executeCommands([command]);
        } else {
           var command = new commandClass(option, this._currentParent, this._canAttach, position, this.facade);
           this.facade.executeCommands([command]);
        }
		this._currentParent = undefined;
	},

	beforeDragOver: function(dragZone, target, event){

		var coord = this.facade.eventCoordinates(event.browserEvent);
		var aShapes = this.facade.getCanvas().getAbstractShapesAtPosition( coord );

		if(aShapes.length <= 0) {
			
				var pr = dragZone.getProxy();
				pr.setStatus(pr.dropNotAllowed);
				pr.sync();
				
				return false;
		}	
		
		var el = aShapes.last();
	
		
		if(aShapes.lenght == 1 && aShapes[0] instanceof ORYX.Core.Canvas) {
			
			return false;
			
		} else {
			// check containment rules
			var option = Ext.dd.Registry.getHandle(target.DDM.currentTarget);

			var stencilSet = this.facade.getStencilSets()[option.namespace];

			var stencil = stencilSet.stencil(option.type);

			if(stencil.type() === "node") {
				
				var parentCandidate = aShapes.reverse().find(function(candidate) {
					return (candidate instanceof ORYX.Core.Canvas 
							|| candidate instanceof ORYX.Core.Node
							|| candidate instanceof ORYX.Core.Edge);
				});
				
				if(  parentCandidate !== this._lastOverElement){
					
					this._canAttach  = undefined;
					this._canContain = undefined;
					
				}
				
				if( parentCandidate ) {
					//check containment rule					
						
					if (!(parentCandidate instanceof ORYX.Core.Canvas) && parentCandidate.isPointOverOffset(coord.x, coord.y) && this._canAttach == undefined) {
					
						this._canAttach = this.facade.getRules().canConnect({
												sourceShape: parentCandidate,
												edgeStencil: stencil,
												targetStencil: stencil
											});
                        if(parentCandidate && parentCandidate.properties['oryx-tasktype'] && parentCandidate.properties['oryx-tasktype'] == "Script") {
                            this._canAttach = false;
                        }
						
						if( this._canAttach ){
							// Show Highlight
							this.facade.raiseEvent({
								type: ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW,
								highlightId: "shapeRepo.attached",
								elements: [parentCandidate],
								style: ORYX.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE,
								color: ORYX.CONFIG.SELECTION_VALID_COLOR
							});
							
							this.facade.raiseEvent({
								type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
								highlightId: "shapeRepo.added"
							});
							
							this._canContain	= undefined;
						} 					
						
					}
					
					if(!(parentCandidate instanceof ORYX.Core.Canvas) && !parentCandidate.isPointOverOffset(coord.x, coord.y)){
						this._canAttach 	= this._canAttach == false ? this._canAttach : undefined;						
					}
					
					if( this._canContain == undefined && !this._canAttach) {
											
						this._canContain = this.facade.getRules().canContain({
															containingShape:parentCandidate, 
															containedStencil:stencil
															});
															
						// Show Highlight
						this.facade.raiseEvent({
															type:		ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW, 
															highlightId:'shapeRepo.added',
															elements:	[parentCandidate],
															color:		this._canContain ? ORYX.CONFIG.SELECTION_VALID_COLOR : ORYX.CONFIG.SELECTION_INVALID_COLOR
														});	
						this.facade.raiseEvent({
															type: 		ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
															highlightId:"shapeRepo.attached"
														});						
					}
						
				
					
					this._currentParent = this._canContain || this._canAttach ? parentCandidate : undefined;
					this._lastOverElement = parentCandidate;
					var pr = dragZone.getProxy();
					pr.setStatus(this._currentParent ? pr.dropAllowed : pr.dropNotAllowed );
					pr.sync();
	
				} 
			} else { //Edge
				this._currentParent = this.facade.getCanvas();
				var pr = dragZone.getProxy();
				pr.setStatus(pr.dropAllowed);
				pr.sync();
			}		
		}
		
		
		return false
	}
}

ORYX.Plugins.ShapeRepository = Clazz.extend(ORYX.Plugins.ShapeRepository);

