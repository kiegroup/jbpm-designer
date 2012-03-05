if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();


   
ORYX.Plugins.FieldDataAssignmentEditors = Clazz.extend({
    facade: undefined,
    
    construct: function(facade){
        this.facade = facade;
        
        var dateEditorFactory = new ORYX.Plugins.FieldDataAssignmentEditors.DateFieldEditorFactory();
        ORYX.AssociationEditors["java.util.Date"] = dateEditorFactory;
        ORYX.AssociationEditors["Date"] = dateEditorFactory;
        
        var intEditorFactory = new ORYX.Plugins.FieldDataAssignmentEditors.IntegerFieldEditorFactory();
        ORYX.AssociationEditors["java.lang.Integer"] = intEditorFactory;
        ORYX.AssociationEditors["Integer"] = intEditorFactory;
        ORYX.AssociationEditors["int"] = intEditorFactory;
        
        var floatEditorFactory = new ORYX.Plugins.FieldDataAssignmentEditors.FloatFieldEditorFactory();
        ORYX.AssociationEditors["java.lang.Double"] = floatEditorFactory;
        ORYX.AssociationEditors["java.lang.Float"] = floatEditorFactory;
        ORYX.AssociationEditors["Float"] = floatEditorFactory;
        ORYX.AssociationEditors["Double"] = floatEditorFactory;
        ORYX.AssociationEditors["float"] = floatEditorFactory;
        ORYX.AssociationEditors["double"] = floatEditorFactory;
        
        var booleanEditorFactory = new ORYX.Plugins.FieldDataAssignmentEditors.BooleanFieldEditorFactory();
        ORYX.AssociationEditors["java.lang.Boolean"] = booleanEditorFactory;
        ORYX.AssociationEditors["Boolean"] = booleanEditorFactory;
        ORYX.AssociationEditors["boolean"] = booleanEditorFactory;
        
    }

});

ORYX.Plugins.FieldDataAssignmentEditors.BooleanFieldEditorFactory = Clazz.extend({
    construct: function(){
        
    },
    
    init: function(){
        var grid = arguments[0];
        var record = arguments[1];
        
        return new Ext.Editor(new Ext.form.Checkbox());
    }
});

ORYX.Plugins.FieldDataAssignmentEditors.FloatFieldEditorFactory = Clazz.extend({
    construct: function(){
        
    },
    
    init: function(){
        var grid = arguments[0];
        var record = arguments[1];
        
        return new Ext.Editor(new Ext.form.NumberField({
            allowDecimals: true
        }));
    }
});

ORYX.Plugins.FieldDataAssignmentEditors.IntegerFieldEditorFactory = Clazz.extend({
    construct: function(){
        
    },
    
    init: function(){
        var grid = arguments[0];
        var record = arguments[1];
        
        return new Ext.Editor(new Ext.form.NumberField({
            allowDecimals: false
        }));
    }
});

ORYX.Plugins.FieldDataAssignmentEditors.DateFieldEditorFactory = Clazz.extend({
    construct: function(){
        
    },
    
    init: function(){
        var grid = arguments[0];
        var record = arguments[1];
        
        return new Ext.Editor(new Ext.form.DateField());
    }
});