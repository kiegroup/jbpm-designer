YAHOO.env.classMap = {"MOVI.model.Edge": "movi", "MOVI.widget.Toolbar": "movi", "MOVI.model.Canvas": "movi", "MOVI.model.Shape": "movi", "MOVI.util.Marker": "movi", "MOVI.widget.FullscreenViewer": "movi", "MOVI.widget.ModelNavigator": "movi", "MOVI.widget.ModelViewer": "movi", "MOVI.model.Node": "movi", "MOVI.stencilset.Stencilset": "movi", "MOVI.widget.ZoomSlider": "movi", "MOVI.util.Annotation": "movi", "MOVI.stencilset.Stencil": "movi", "MOVI.util.ShapeSelect": "movi"};

YAHOO.env.resolveClass = function(className) {
    var a=className.split('.'), ns=YAHOO.env.classMap;

    for (var i=0; i<a.length; i=i+1) {
        if (ns[a[i]]) {
            ns = ns[a[i]];
        } else {
            return null;
        }
    }

    return ns;
};
