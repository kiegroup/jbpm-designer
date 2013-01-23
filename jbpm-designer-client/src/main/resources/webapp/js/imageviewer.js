ImageViewer = Ext.extend(Ext.Window, {
    initComponent: function() {
        var uid = Ext.id();
        ORYX.EDITOR.imagePreviewSVG = this.src;
        this.bodyCfg = {
            id: 'imageviewerid',
            layout: 'anchor',
            autoCreate: true,
            closeAction :'close',
            title: 'Image Viewer',
            plain: true,
            modal: true,
            collapsible: false,
            resizeable: true,
            shadow: true,
            html: '<iframe id="imageViewFrame" name="imageViewFrame" frameborder="0" scrolling="auto" width="100%" height="400" src="' + ORYX.PATH + 'imageview/imageview.html?'+uid+'"></iframe>',
            width: 400,
            height: 400,
            autoScroll: true,
            fixedcenter	: true
        };
        ImageViewer.superclass.initComponent.apply(this, arguments);
    },

    onRender: function() {
        ImageViewer.superclass.onRender.apply(this, arguments);
        this.body.on('load', this.onImageLoad, this, {single: true});
    },

    onImageLoad: function() {
       // var h = this.getFrameHeight(),
       //     w = this.getFrameWidth();
       // this.setSize(this.body.dom.offsetWidth + w, this.body.dom.offsetHeight + h);
    },

    setSrc: function(src) {
        this.body.on('load', this.onImageLoad, this, {single: true});
        //this.body.dom.style.width = this.body.dom.style.width = 'auto';
        this.body.dom.src = src;
    },

    initEvents: function() {
        ImageViewer.superclass.initEvents.apply(this, arguments);
        if (this.resizer) {
            this.resizer.preserveRatio = true;
        }
    }
});