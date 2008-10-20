
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
	
	href:false,

    // private
    onRender : function(ct, position){
        if(!this.el){
            this.el = document.createElement('a');
            this.el.id = this.getId();
			
			if( !this.disabled )
            	this.el.href = this.href ? this.href : "#" + this.text;

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


/********
 * Multi-Value ComboBox
 * 
 * 
 * */
/**
* @class Ext.form.ComboBoxMulti
* @extends Ext.form.ComboBox
* Adds freeform multiselect and duplicate entry prevention to the standard combobox
* @constructor
* Create a new ComboBoxMulti.
* @param {Object} config Configuration options
*/
Ext.form.ComboBoxMulti = function(config){
    /**
     * @cfg {String} sep is used to separate text entries
     */
    /**
     * @cfg {Boolean} preventDuplicates indicates whether repeated selections of the same option will generate extra entries
     */
    Ext.apply(config);
    
    // this option will interfere will expected operation
    this.typeAhead 		= false;
    // these options customize behavior
    this.minChars 		= 1;
    this.hideTrigger = true;
    this.defaultAutoCreate = {
        tag: config.renderAsTextArea ? "textarea" : "input",
        autocomplete: "off"
    };
    
    Ext.form.ComboBoxMulti.superclass.constructor.call(this, config);
};

Ext.form.ComboBoxMulti = Ext.extend(Ext.form.ComboBoxMulti, Ext.form.ComboBox, {
    getPosition: function(){
        if (document.selection) { // IE
            var r = document.selection.createRange();
            var d = r.duplicate();
            d.moveToElementText(this.el.dom);
            d.setEndPoint('EndToEnd', r);
            return d.text.length;
        }
        else {
            return this.el.dom.selectionEnd;
        }
    },
    
    getActiveRange: function(){
        var s = this.sep;
        var p = this.getPosition();
        var v = this.getRawValue();
		var d = v.split( this.sep );
        var left = p;
        while (left > 0 && (v.charAt(left) != s  || v.slice(left+1).blank())) {
            --left;
        }
        if (left > 0) {
            left++;
        }
        return {
            left: left,
            right: p
        };
    },
    
    getActiveEntry: function(){
        var r = this.getActiveRange();
        return this.getRawValue().substring(r.left, r.right).replace(/^\s+|\s+$/g, '');
    },
    
    replaceActiveEntry: function(value){
        var r = this.getActiveRange();
        var v = this.getRawValue();
        if (this.preventDuplicates && v.indexOf(value) >= 0) {
            return;
        }
        var pad = (this.sep == ' ' ? '' : ' '); 
        pad = (this.sep == "\n" ? '' : pad);	
		
        this.setValue(v.substring(0, r.left) + (r.left > 0 ? pad : '') + value + this.sep + pad + v.substring(r.right));
        var p = r.left + value.length + 2 + pad.length;
        this.selectText.defer(200, this, [p, p]);
    },
    
    onSelect: function(record, index){
        if (this.fireEvent('beforeselect', this, record, index) !== false) {
            var value = record.data[this.valueField || this.displayField];
            if (this.sep) {
                this.replaceActiveEntry(value);
            }
            else {
                this.setValue(value);
            }
            this.collapse();
            this.fireEvent('select', this, record, index);
        }
    },
    
    initQuery: function(){
        this.doQuery(this.sep ? this.getActiveEntry() : this.getRawValue());
    }
});

Ext.reg('combomulti', Ext.form.ComboBoxMulti);