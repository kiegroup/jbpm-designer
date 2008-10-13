
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

	// On Click Handler
    click: null,
	
	// Image url 
    image: null,
	
	// Image style (only if an image url is setted) 
    imageStyle: null,

	toggle:false, 
	
	toggleStyle:null,

	selected:false,

    // private
    onRender : function(ct, position){
        if(!this.el){
            this.el = document.createElement('a');
            this.el.id = this.getId();
			
			if( !this.disabled )
            	this.el.href = "#" + this.text;

            if( !this.disabled ){
                this.el.addEventListener( 'click', this.onClick.bind(this), true);
            }
			
			if( this.image ){
				this.el.innerHTML = '<img src="' + this.image + '" title="' + this.text + '"' + ( this.imageStyle ? ' style="' + this.imageStyle + '"/>': '/>')
			} else {
				this.el.innerHTML = this.text ? Ext.util.Format.htmlEncode(this.text) : (this.html || '');	
			}

            if(this.forId){
                this.el.setAttribute('htmlFor', this.forId);
            }
        }
        Ext.LinkButton.superclass.onRender.call(this, ct, position);
    },
	
	onClick: function(e){
		
		if( this.disabled ){ Event.stop(e); return; }
		
		// Toggle the button
		if( this.toggle ){
			this.selected = !this.selected;
			if( this.toggleStyle ){
				this.el.dom.setAttribute('style','')
				if( this.selected ){
					this.el.applyStyles( this.toggleStyle )
				} else {
					this.el.applyStyles( this.initialConfig.style )
				}
			}
		}

		
		if( this.click instanceof Function )
			this.click.apply(this.click, [this, e]); 
		 
		Event.stop(e)
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