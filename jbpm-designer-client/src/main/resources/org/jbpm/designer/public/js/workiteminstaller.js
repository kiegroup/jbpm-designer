Ext.ns('Extensive.grid');

Extensive.grid.WorkitemInstaller = Ext.extend(Ext.grid.RowSelectionModel, {

    width: 30,

    sortable: false,
    dataIndex: 0, // this is needed, otherwise there will be an error

    menuDisabled: true,
    fixed: true,
    id: 'workiteminstaller',
    dtype: "",

    setDType: function(dtype) {
        if(dtype && dtype.length > 0) {
            this.dtype = dtype;
        }
    },

    initEvents: function() {
        Extensive.grid.WorkitemInstaller.superclass.initEvents.call(this);
        this.grid.on('cellclick', function(grid, rowIndex, columnIndex, e){
            if(columnIndex==grid.getColumnModel().getIndexById('workiteminstaller')) {
                var record = grid.getStore().getAt(rowIndex);

                Ext.MessageBox.confirm(
                    'Install',
                    ORYX.I18N.view.installSelectedWorkitem,
                    function(btn){
                        if (btn == 'yes') {
                            ORYX.EDITOR._pluginFacade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_INSTALL_WORKITEM,
                                dtype       : this.dtype,
                                rcd         : record,
                                mn          : record.get('name'),
                                cat         : record.get('category')
                            });
                        }
                    }.bind(this)
                );
            }
        }.bind(this));
    },

    renderer: function(v, p, record, rowIndex){
        return '<div class="extensive-install" style="width: 15px; height: 16px;" title="Install"></div>';
    }
});