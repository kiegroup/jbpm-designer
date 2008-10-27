
/**
 * Copyright (c) 2008
 * Willi Tscheschner
 * 
 */

/**
 * Implementation of a Star-Rating Component
 * 
 * 
 */
Ext.Rating = Ext.extend(Ext.BoxComponent, {

	// Defines the max score value
	maxScore			: 5,

	// Defines the min score value
	minScore			: 1,
	
	// Defines both imgs
	scoreImgEnabled		: '../images/silk/star.png',
	scoreImgDisabled	: '../images/silk/star_gray.png',
	
	imgStyle			: 'width:12px;margin:2px;',
	imgEditStyle		: 'cursor:pointer;',
	textStyle			: 'margin-left:5px;position:relative;top:-4px;',
	
	// Defines a disabled img
	disabledImg			: '../images/silk/bullet_black.png',	

	value				: 0,
	
	text				: '',
	
	stars				: [],
	
	editable			: false,
	
    // private
    onRender : function(ct, position){
        if(!this.el){
            this.el = document.createElement('div');
            this.el.id = this.getId();
			
			this.stars = [];
			for (var i = this.minScore; i <= this.maxScore; i++){
				var img = document.createElement('img')
				
				if( this.editable ){
					img.addEventListener( 'mouseover', this.onMouseOverStar.bind(this), true);
					img.addEventListener( 'mouseout', this.onMouseOutStar.bind(this), true);
					img.addEventListener( 'click', this.onClickStar.bind(this), true);	
					img.setAttribute('style', this.imgStyle + '' + this.imgEditStyle );			
				} else {
					img.setAttribute('style', this.imgStyle );	
				}
				this.stars.push( img );
			}
			
			// Add all the imgs
			this.stars.each(function(star){
				this.el.appendChild( star );
			}.bind(this))
			
			// Add a text behind
			this.textEl = document.createElement('span');
			this.textEl.setAttribute('style', this.textStyle );	
			this.textEl.innerHTML = this.text;
			this.el.appendChild( this.textEl );
					
			if(this.forId){
                this.el.setAttribute('htmlFor', this.forId);
            }
        }

		// Set the imgs
		this.setStars();

		if( ct )
       		Ext.Rating.superclass.onRender.call(this, ct, position);
    },
	
	setStars: function( value ){
						
		if( !value ){
			value = this.value;
		}

		var isDisabled = this.disabled || value <= 0;
				
		this.stars.each(function(star, index){
			
			var pos = index + this.minScore;
			
			var isBelowScore = pos <= (value+0.5);
			var imgScr 	= isBelowScore 	? this.scoreImgEnabled 	: this.scoreImgDisabled;
			imgScr 		= isDisabled 	? this.disabledImg 		: imgScr ;
			
			star.setAttribute('src', 	imgScr);
			star.setAttribute('title', 	pos);
			
		}.bind(this));
	},
	
	onMouseOverStar: function(e){
		this.setStars( this.stars.indexOf( e.target ) + this.minScore )
	},

	onMouseOutStar: function(e){
		this.setStars()
	},
	
	onClickStar: function(e){
		var index = this.stars.indexOf( e.target ) + this.minScore
		
		this.setValue( index );
		 
		if( this.changed instanceof Function ){
			this.changed( index )
		}
	},
			
    
    setValue: function(t){
        this.value = t;
		this.setStars();
    },
	    
    setText: function(t){
        this.text = t;
		
		if( this.textEl )
			this.textEl.innerHTML = t;
    },
	
	startEdit: function(){
		this.el.dom.setAttribute('style', 'left:22px;position:absolute;top:2px;')
	},
	completeEdit: function(){
		
	},
	cancelEdit: function(){
		
	},		
});

Ext.reg('rating', Ext.Rating);



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
	this.validationEvent = true;
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
        while (left > 0 && (v.charAt(left) != s  /* || v.slice(left+1).blank()*/)) {
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

    // private
    initEvents : function(){
        Ext.form.ComboBoxMulti.superclass.initEvents.call(this);
		
		this.keyNav.doRelay = function(foo, bar, hname){
                if(this.scope.isExpanded()){
                   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                }
                return true;
            }
	},	    
    initQuery: function(){
        this.doQuery(this.sep ? this.getActiveEntry() : this.getRawValue());
    },

    selectText : function(start, end){
        var v = this.sep ? this.getActiveEntry() : this.getRawValue();
        if(v.length > 0){
            start = start === undefined ? this.getRawValue().indexOf(v) : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if(d.setSelectionRange){
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart("character", start);
                range.moveEnd("character", end-v.length);
                range.select();
            }
        }
    },
	
    // private
    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0){
            this.expand();
            this.restrictHeight();
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.selectText()
                }
                if(!this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                this.selectNext();
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.onEmptyResults();
        }
        //this.el.focus();
    },	

    /**
     * @cfg {Number} growMin The minimum height to allow when grow = true (defaults to 60)
     */
    growMin : 60,
    /**
     * @cfg {Number} growMax The maximum height to allow when grow = true (defaults to 1000)
     */
    growMax: 1000,
	
    // private
    onKeyUp : function(e){
        if(!e.isNavKeyPress() || e.getKey() == e.ENTER){          		
			Ext.form.ComboBoxMulti.superclass.onKeyUp.call(this, e);
			this.autoSize();
        }
    },

    /**
     * Automatically grows the field to accomodate the height of the text up to the maximum field height allowed.
     * This only takes effect if grow = true, and fires the autosize event if the height changes.
     */
    autoSize : function(){
        if(!this.grow || this.defaultAutoCreate.tag !== "textarea" || !this.el ){
            return;
        }
		
        var el 	= this.el;
        var v 	= el.dom.value;
		var h 	= Number(el.getStyle('line-height').replace("px", "")) * (v.split("\n").length+1);
		h		-= 6;
        h = Math.min(this.growMax, Math.max(h, this.growMin));
        if(h != this.lastHeight){
            this.lastHeight = h;
            this.el.setHeight(h);
        }
    }	
});

Ext.reg('combomulti', Ext.form.ComboBoxMulti);