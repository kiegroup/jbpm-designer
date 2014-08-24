/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


(function(){Ext.Layer=function(config,existingEl){config=config||{};var dh=Ext.DomHelper;var cp=config.parentEl,pel=cp?Ext.getDom(cp):document.body;if(existingEl){this.dom=Ext.getDom(existingEl);}
if(!this.dom){var o=config.dh||{tag:"div",cls:"x-layer"};this.dom=dh.append(pel,o);}
if(config.cls){this.addClass(config.cls);}
this.constrain=config.constrain!==false;this.visibilityMode=Ext.Element.VISIBILITY;if(config.id){this.id=this.dom.id=config.id;}else{this.id=Ext.id(this.dom);}
this.zindex=config.zindex||this.getZIndex();this.position("absolute",this.zindex);if(config.shadow){this.shadowOffset=config.shadowOffset||4;this.shadow=new Ext.Shadow({offset:this.shadowOffset,mode:config.shadow});}else{this.shadowOffset=0;}
this.useShim=config.shim!==false&&Ext.useShims;this.useDisplay=config.useDisplay;this.hide();};var supr=Ext.Element.prototype;var shims=[];Ext.extend(Ext.Layer,Ext.Element,{getZIndex:function(){return this.zindex||parseInt((this.getShim()||this).getStyle("z-index"),10)||11000;},getShim:function(){if(!this.useShim){return null;}
if(this.shim){return this.shim;}
var shim=shims.shift();if(!shim){shim=this.createShim();shim.enableDisplayMode('block');shim.dom.style.display='none';shim.dom.style.visibility='visible';}
var pn=this.dom.parentNode;if(shim.dom.parentNode!=pn){pn.insertBefore(shim.dom,this.dom);}
shim.setStyle('z-index',this.getZIndex()-2);this.shim=shim;return shim;},hideShim:function(){if(this.shim){this.shim.setDisplayed(false);shims.push(this.shim);delete this.shim;}},disableShadow:function(){if(this.shadow){this.shadowDisabled=true;this.shadow.hide();this.lastShadowOffset=this.shadowOffset;this.shadowOffset=0;}},enableShadow:function(show){if(this.shadow){this.shadowDisabled=false;this.shadowOffset=this.lastShadowOffset;delete this.lastShadowOffset;if(show){this.sync(true);}}},sync:function(doShow){var sw=this.shadow;if(!this.updating&&this.isVisible()&&(sw||this.useShim)){var sh=this.getShim();var w=this.getWidth(),h=this.getHeight();var l=this.getLeft(true),t=this.getTop(true);if(sw&&!this.shadowDisabled){if(doShow&&!sw.isVisible()){sw.show(this);}else{sw.realign(l,t,w,h);}
if(sh){if(doShow){sh.show();}
var a=sw.adjusts,s=sh.dom.style;s.left=(Math.min(l,l+a.l))+"px";s.top=(Math.min(t,t+a.t))+"px";s.width=(w+a.w)+"px";s.height=(h+a.h)+"px";}}else if(sh){if(doShow){sh.show();}
sh.setSize(w,h);sh.setLeftTop(l,t);}}},destroy:function(){this.hideShim();if(this.shadow){this.shadow.hide();}
this.removeAllListeners();Ext.removeNode(this.dom);Ext.Element.uncache(this.id);},remove:function(){this.destroy();},beginUpdate:function(){this.updating=true;},endUpdate:function(){this.updating=false;this.sync(true);},hideUnders:function(negOffset){if(this.shadow){this.shadow.hide();}
this.hideShim();},constrainXY:function(){if(this.constrain){var vw=Ext.lib.Dom.getViewWidth(),vh=Ext.lib.Dom.getViewHeight();var s=Ext.getDoc().getScroll();var xy=this.getXY();var x=xy[0],y=xy[1];var w=this.dom.offsetWidth+this.shadowOffset,h=this.dom.offsetHeight+this.shadowOffset;var moved=false;if((x+w)>vw+s.left){x=vw-w-this.shadowOffset;moved=true;}
if((y+h)>vh+s.top){y=vh-h-this.shadowOffset;moved=true;}
if(x<s.left){x=s.left;moved=true;}
if(y<s.top){y=s.top;moved=true;}
if(moved){if(this.avoidY){var ay=this.avoidY;if(y<=ay&&(y+h)>=ay){y=ay-h-5;}}
xy=[x,y];this.storeXY(xy);supr.setXY.call(this,xy);this.sync();}}},isVisible:function(){return this.visible;},showAction:function(){this.visible=true;if(this.useDisplay===true){this.setDisplayed("");}else if(this.lastXY){supr.setXY.call(this,this.lastXY);}else if(this.lastLT){supr.setLeftTop.call(this,this.lastLT[0],this.lastLT[1]);}},hideAction:function(){this.visible=false;if(this.useDisplay===true){this.setDisplayed(false);}else{this.setLeftTop(-10000,-10000);}},setVisible:function(v,a,d,c,e){if(v){this.showAction();}
if(a&&v){var cb=function(){this.sync(true);if(c){c();}}.createDelegate(this);supr.setVisible.call(this,true,true,d,cb,e);}else{if(!v){this.hideUnders(true);}
var cb=c;if(a){cb=function(){this.hideAction();if(c){c();}}.createDelegate(this);}
supr.setVisible.call(this,v,a,d,cb,e);if(v){this.sync(true);}else if(!a){this.hideAction();}}},storeXY:function(xy){delete this.lastLT;this.lastXY=xy;},storeLeftTop:function(left,top){delete this.lastXY;this.lastLT=[left,top];},beforeFx:function(){this.beforeAction();return Ext.Layer.superclass.beforeFx.apply(this,arguments);},afterFx:function(){Ext.Layer.superclass.afterFx.apply(this,arguments);this.sync(this.isVisible());},beforeAction:function(){if(!this.updating&&this.shadow){this.shadow.hide();}},setLeft:function(left){this.storeLeftTop(left,this.getTop(true));supr.setLeft.apply(this,arguments);this.sync();},setTop:function(top){this.storeLeftTop(this.getLeft(true),top);supr.setTop.apply(this,arguments);this.sync();},setLeftTop:function(left,top){this.storeLeftTop(left,top);supr.setLeftTop.apply(this,arguments);this.sync();},setXY:function(xy,a,d,c,e){this.fixDisplay();this.beforeAction();this.storeXY(xy);var cb=this.createCB(c);supr.setXY.call(this,xy,a,d,cb,e);if(!a){cb();}},createCB:function(c){var el=this;return function(){el.constrainXY();el.sync(true);if(c){c();}};},setX:function(x,a,d,c,e){this.setXY([x,this.getY()],a,d,c,e);},setY:function(y,a,d,c,e){this.setXY([this.getX(),y],a,d,c,e);},setSize:function(w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setSize.call(this,w,h,a,d,cb,e);if(!a){cb();}},setWidth:function(w,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setWidth.call(this,w,a,d,cb,e);if(!a){cb();}},setHeight:function(h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setHeight.call(this,h,a,d,cb,e);if(!a){cb();}},setBounds:function(x,y,w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);if(!a){this.storeXY([x,y]);supr.setXY.call(this,[x,y]);supr.setSize.call(this,w,h,a,d,cb,e);cb();}else{supr.setBounds.call(this,x,y,w,h,a,d,cb,e);}
return this;},setZIndex:function(zindex){this.zindex=zindex;this.setStyle("z-index",zindex+2);if(this.shadow){this.shadow.setZIndex(zindex+1);}
if(this.shim){this.shim.setStyle("z-index",zindex);}}});})();