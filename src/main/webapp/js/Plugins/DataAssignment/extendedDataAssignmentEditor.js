if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();


   
ORYX.Plugins.ExtendedDataAssignmentEditor = Clazz.extend({
    facade: undefined,
    
    construct: function(facade){
        this.facade = facade;
        ORYX.FieldEditors["extendeddataassignment"] = new ORYX.Plugins.ExtendedDataAssignmentEditor.EditorFactory();
    }

});

ORYX.Plugins.ExtendedDataAssignmentEditor.EditorFactory = Clazz.extend({
    construct: function(){
        
    },          
    /**
     * This function gets executed by propertyWindow in its own context,
     * so this = propertyWindow
     */
    init: function(){
        //arguments: key, pair, icons, index
        var key = arguments[0];
        var pair = arguments[1];
        var index = arguments[3];
        
        var factType = pair._jsonProp.lookupType;

        var cf = new Ext.form.ExtendedDataAssignmentEditor({
                allowBlank: pair.optional(),
                dataSource: this.dataSource,
                grid: this.grid,
                row: index,
                facade: this.facade,
                shapes: this.shapeSelection.shapes            
        });
        
        cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});	
        
        return new Ext.Editor(cf);
    }
});


Ext.form.ExtendedDataAssignmentEditor = function(_config){
    
    var defaultConfig = {
        onTriggerClick : function(){
            var grid = Ext.form.ExtendedDataAssignmentEditor.superclass.onTriggerClick.call(this);
            if (!grid){
                return null;
            }
            
            var originalGetCellEditor = grid.getColumnModel().getCellEditor;
            
            grid.getColumnModel().getCellEditor = function( colIndex, rowIndex ){
                if (colIndex == 5){
                    var record = grid.getStore().getAt(rowIndex);
                    var editorClass = ORYX.AssociationEditors[record.get("dataType")];
                    //is there any editor registered for this type?
                    if (editorClass !== undefined){
                        return editorClass.init.bind(this, grid, record)();
                    }
                    //if no custom editor, then let's go with the default'
                }
                return originalGetCellEditor.call(grid.getColumnModel(), colIndex, rowIndex );
            }
        }
    }
    
    //merge provided config with default
    if (_config){
        Ext.applyIf(_config, defaultConfig);
    }else{
        _config = defaultConfig;
    }
    
    Ext.form.ExtendedDataAssignmentEditor.superclass.constructor.call(this,_config);
}

Ext.extend(Ext.form.ExtendedDataAssignmentEditor,Ext.form.ComplexDataAssignmenField,{});