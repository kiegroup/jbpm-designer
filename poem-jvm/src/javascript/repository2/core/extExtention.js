
/**
 * Copyright (c) 2008
 * Willi Tscheschner
 * 
 */

/**
 * Implementation of an Ext-LinkButton
 * 
 * 
 */
Ext.LinkButton = Ext.extend(Ext.BoxComponent, {

    click: null,

    // private
    onRender : function(ct, position){
        if(!this.el){
            this.el = document.createElement('a');
            this.el.id = this.getId();
            this.el.href = "#" + this.text;

            if( this.click instanceof Function ){
                this.el.addEventListener( 'click', function(e){ this.click.apply(this.click, arguments); Event.stop(e)}.bind(this), true);
            }
            this.el.innerHTML = this.text ? Ext.util.Format.htmlEncode(this.text) : (this.html || '');
            if(this.forId){
                this.el.setAttribute('htmlFor', this.forId);
            }
        }
        Ext.LinkButton.superclass.onRender.call(this, ct, position);
    },
    
    setText: function(t, encode){
        this.text = t;
        if(this.rendered){
            this.el.dom.innerHTML = encode !== false ? Ext.util.Format.htmlEncode(t) : t;
        }
        return this;
    }
});

Ext.reg('linkbutton', Ext.LinkButton);