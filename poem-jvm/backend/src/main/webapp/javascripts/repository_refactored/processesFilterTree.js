/**
 * Copyright (c) 2008
 * Sven Wagner-Boysen
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

/* filter for processes on the left side of the repository */

Repository.viewelements.processesFilterTree = Ext.extent(Ext.tree.TreePanel, {
	initComponent: function() {
		
	}
});
                loader: new Ext.tree.TreeLoader(),
                root: new Ext.tree.AsyncTreeNode({
					listeners : {
						append: function(tree, parent, node, index) {
							// add child nodes for the model types dynamically loaded from and offered by the server
							if (node.id == "models_by_type") {
								Repository.app.loadModelTypes(function(model_types) {
									model_types.each(function(modeltype) {
										node.appendChild(
											new Ext.tree.TreeNode({
												text: modeltype.title,
												id: modeltype.namespace,
												leaf: true,
												icon: modeltype.icon_url,
												qtip: modeltype.description,
												listeners: {
													click: function() {
														Repository.app.filterModelsByModelType(modeltype.namespace);
													}
												}
											})
										)
									})
									
									node.collapse();
								
								})
								
							} // end of if (node.id == "tree_node_processes_by_type") {
							
							if(Repository.app.isPublicUser() && node.id == "all_items"){
								parent.removeChild(node)
							}
						}
						/*
						load: function( parent ){
							Repository.app.loadModelTypes(function(model_types) {
								
									parent.childNodes.each(function(child){
										
										model_types.each(function(modeltype) {
											child.appendChild(
												new Ext.tree.TreeNode({
													text: modeltype.title,
													leaf: true,
													icon: modeltype.icon_url,
													qtip: modeltype.description,
													listeners: {
														click: function() {
															Repository.app.filterModelsByAccessAndType( child.id, modeltype.uri);
														}
													}
												})
											)
										})	
										
										if(child !== parent.firstChild){
											child.collapse();
										}										
									})									
								})
						}*/
					},
					
					
                    expanded: true,
                    children: [{
						text: 'All items',
						id: 'all_items',
						expanded: true,
						listeners: {
							click: function(){
								Repository.app.filterModelsByAccessAndType();
							}},
						children: [{
							text: 'My processes',
							id: 'my_processes',
							leaf: true,
							listeners: {
								click: function(){
									Repository.app.filterModelsByAccessAndType('my_processes');
								}
							}
						}, {
							text: 'My shared processes',
							id: 'shared_processes',
							leaf: true,
							listeners: {
								click: function(){
									Repository.app.filterModelsByAccessAndType('shared_processes');
								}
							}
						}, {
							text: 'Me as a contributor',
							id: 'contributor',
							leaf: true,
							listeners: {
								click: function(){
									Repository.app.filterModelsByAccessAndType('contributor');
								}
							}
						}, {
							text: 'Me as a reader',
							id: 'reader',
							leaf: true,
							listeners: {
								click: function(){
									Repository.app.filterModelsByAccessAndType('reader');
								}
							}
						}]
					}, {
						text: 'Models by type',
						id: 'models_by_type',
						expanded: true,
						children: [{
							text: 'Show all',
							leaf: true,
							listeners: {
								click: function(){
									Repository.app.filterModelsByAccessAndType();
								}
							}
						}]
					}, {
						text: 'Public',
						id: 'public',
						leaf: true,
						listeners: {
							click: function(){
								Repository.app.filterModelsByAccessAndType('public');
							}
						}
					}]
                }),
                rootVisible: false
            },