/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.grid.GridDragZone=function(grid,config){this.view=grid.getView();Ext.grid.GridDragZone.superclass.constructor.call(this,this.view.mainBody.dom,config);if(this.view.lockedBody){this.setHandleElId(Ext.id(this.view.mainBody.dom));this.setOuterHandleElId(Ext.id(this.view.lockedBody.dom));}
this.scroll=false;this.grid=grid;this.ddel=document.createElement('div');this.ddel.className='x-grid-dd-wrap';};Ext.extend(Ext.grid.GridDragZone,Ext.dd.DragZone,{ddGroup:"GridDD",getDragData:function(e){var t=Ext.lib.Event.getTarget(e);var rowIndex=this.view.findRowIndex(t);if(rowIndex!==false){var sm=this.grid.selModel;if(!sm.isSelected(rowIndex)||e.hasModifier()){sm.handleMouseDown(this.grid,rowIndex,e);}
return{grid:this.grid,ddel:this.ddel,rowIndex:rowIndex,selections:sm.getSelections()};}
return false;},onInitDrag:function(e){var data=this.dragData;this.ddel.innerHTML=this.grid.getDragDropText();this.proxy.update(this.ddel);},afterRepair:function(){this.dragging=false;},getRepairXY:function(e,data){return false;},onEndDrag:function(data,e){},onValidDrop:function(dd,e,id){this.hideProxy();},beforeInvalidDrop:function(e,id){}});