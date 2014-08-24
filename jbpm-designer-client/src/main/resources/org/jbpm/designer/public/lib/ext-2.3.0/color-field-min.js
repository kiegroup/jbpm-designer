Ext.ux.ColorField=Ext.extend(Ext.form.TriggerField,{invalidText:"'{0}' is not a valid color - it must be in a the hex format (# followed by 3 or 6 letters/numbers 0-9 A-F)",triggerClass:"x-form-color-trigger",defaultAutoCreate:{tag:"input",type:"text",size:"10",maxlength:"7",autocomplete:"off"},maskRe:/[#a-f0-9]/i,facade:undefined,validateValue:function(b){if(!Ext.ux.ColorField.superclass.validateValue.call(this,b)){return false
}if(b.length<1){this.setColor("");
return true
}var a=this.parseColor(b);
if(!b||(a==false)){this.markInvalid(String.format(this.invalidText,b));
return false
}this.setColor(b);
return true
},setColor:function(a){if(a==""||a==undefined){if(this.emptyText!=""&&this.parseColor(this.emptyText)){a=this.emptyText
}else{a="transparent"
}}if(this.trigger){this.trigger.setStyle({"background-color":a})
}else{this.on("render",function(){this.setColor(a)
},this)
}},validateBlur:function(){return !this.menu||!this.menu.isVisible()
},getValue:function(){return Ext.ux.ColorField.superclass.getValue.call(this)||""
},setValue:function(a){Ext.ux.ColorField.superclass.setValue.call(this,this.formatColor(a));
this.setColor(this.formatColor(a))
},parseColor:function(a){return(!a||(a.substring(0,1)!="#"))?false:(a.length==4||a.length==7)
},formatColor:function(a){if(!a||this.parseColor(a)){return a
}if(a.length==3||a.length==6){return"#"+a
}return""
},menuListeners:{select:function(a,b){this.setValue(b)
},show:function(){this.onFocus()
},hide:function(){this.focus.defer(10,this);
var a=this.menuListeners;
this.menu.un("select",a.select,this);
this.menu.un("show",a.show,this);
this.menu.un("hide",a.hide,this)
}},onTriggerClick:function(){if(this.disabled){return
}if(this.menu==null){this.menu=new Ext.menu.ColorMenu()
}this.menu.on(Ext.apply({},this.menuListeners,{scope:this}));
this.menu.show(this.el,"tl-bl?")
}});
Ext.reg("colorfield",Ext.ux.ColorField);