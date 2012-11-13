SVGViewer = Ext.extend(Ext.Window, {
    initComponent: function() {
        this.bodyCfg = {
            id: 'svgviewerid',
            layout: 'anchor',
            autoCreate: true,
            title: 'SVG Viewer',
            modal: true,
            collapsible: false,
            shadow: true,
            tag: 'div',
            src: this.src,
            width: 400,
            height: 400,
            autoscroll: true,
            fixedcenter	: true
        };
        SVGViewer.superclass.initComponent.apply(this, arguments);
    },

    onRender: function() {
        SVGViewer.superclass.onRender.apply(this, arguments);
        this.body.on('load', this.onSVGLoad, this, {single: true});
    },

    onSVGLoad: function() {
        // var h = this.getFrameHeight(),
        //     w = this.getFrameWidth();
        // this.setSize(this.body.dom.offsetWidth + w, this.body.dom.offsetHeight + h);
    },

    setSrc: function(src) {
        this.body.on('load', this.onSVGLoad, this, {single: true});
        //this.body.dom.style.width = this.body.dom.style.width = 'auto';
        this.body.dom.src = src;
    },

    initEvents: function() {
        SVGViewer.superclass.initEvents.apply(this, arguments);
        if (this.resizer) {
            this.resizer.preserveRatio = true;
        }
    }
});