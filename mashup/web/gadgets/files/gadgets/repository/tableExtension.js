/* This code should not be modified */

(function(){

var Dom = YAHOO.util.Dom,

	REPOSITORY_LOCAL  = "/gadgets/files/gadgets/repository",
    STRING_STATENAME  = 'yui_dt_state',

    CLASS_EXPANDED    = 'yui-dt-expanded',
    CLASS_COLLAPSED   = 'yui-dt-collapsed',
    CLASS_EXPANSION   = 'yui-dt-expansion',
    CLASS_LINER       = 'yui-dt-liner',

    //From YUI 3
    indexOf = function(a, val) {
        for (var i=0; i<a.length; i=i+1) {
            if (a[i] === val) {
                return i;
            }
        }

        return -1;
    };

YAHOO.lang.augmentObject(
        YAHOO.widget.DataTable.prototype , {

        ////////////////////////////////////////////////////////////////////
        //
        // Private members
        //
        ////////////////////////////////////////////////////////////////////

        /**
            * Gets state object for a specific record associated with the
            * DataTable.
            * @method _getRecordState
            * @param {Mixed} record_id Record / Row / or Index id
            * @param {String} key Key to return within the state object.
            * Default is to return all as a map
            * @return {Object} State data object
            * @type mixed
            * @private
        **/
        _getRecordState : function( record_id, key ){
            var row_data    = this.getRecord( record_id ),
                row_state   = row_data.getData( STRING_STATENAME ),
                state_data  = (row_state && key) ? row_state[ key ]:row_state;

            return state_data || {};
        },

        /**
            * Sets a value to a state object with a unique id for a record
            * which is associated with the DataTable
            * @method _setRecordState
            * @param {Mixed} record_id Record / Row / or Index id
            * @param {String} key Key to use in map
            * @param {Mixed} value Value to assign to the key
            * @return {Object} State data object
            * @type mixed
            * @private
        **/
        _setRecordState : function( record_id, key, value ){
            var row_data      = this.getRecord( record_id ).getData(),
                merged_data   = row_data[ STRING_STATENAME ] || {};

            merged_data[ key ] = value;

            this.getRecord(record_id).setData(STRING_STATENAME, merged_data);

            return merged_data;

        },

        //////////////////////////////////////////////////////////////////////
        //
        // Public methods
        //
        //////////////////////////////////////////////////////////////////////

        /**
            * Over-ridden initAttributes method from DataTable
            * @method initAttributes
            * @param {Mixed} record_id Record / Row / or Index id
            * @param {String} key Key to use in map
            * @param {Mixed} value Value to assign to the key
            * @return {Object} State data object
            * @type mixed
        **/
        initAttributes : function( oConfigs ) {
		
			oConfigs = oConfigs || {};
			YAHOO.widget.DataTable.superclass.initAttributes.call(this, oConfigs);

		
			this.setAttributeConfig("summary", {
				value: "",
				validator: YAHOO.lang.isString,
				method: function(sSummary) {
					if(this._elTable) {
						this._elTable.summary = sSummary;
					}
				}
			});

			/**
			* @attribute selectionMode
			* @description Specifies row or cell selection mode. Accepts the following strings:
			*    <dl>
			*      <dt>"standard"</dt>
			*      <dd>Standard row selection with support for modifier keys to enable
			*      multiple selections.</dd>
			*
			*      <dt>"single"</dt>
			*      <dd>Row selection with modifier keys disabled to not allow
			*      multiple selections.</dd>
			*
			*      <dt>"singlecell"</dt>
			*      <dd>Cell selection with modifier keys disabled to not allow
			*      multiple selections.</dd>
			*
			*      <dt>"cellblock"</dt>
			*      <dd>Cell selection with support for modifier keys to enable multiple
			*      selections in a block-fashion, like a spreadsheet.</dd>
			*
			*      <dt>"cellrange"</dt>
			*      <dd>Cell selection with support for modifier keys to enable multiple
			*      selections in a range-fashion, like a calendar.</dd>
			*    </dl>
			*
			* @default "standard"
			* @type String
			*/
			this.setAttributeConfig("selectionMode", {
				value: "standard",
				validator: YAHOO.lang.isString
			});

			/**
			* @attribute sortedBy
			* @description Object literal provides metadata for initial sort values if
			* data will arrive pre-sorted:
			* <dl>
			*     <dt>sortedBy.key</dt>
			*     <dd>{String} Key of sorted Column</dd>
			*     <dt>sortedBy.dir</dt>
			*     <dd>{String} Initial sort direction, either YAHOO.widget.DataTable.CLASS_ASC or YAHOO.widget.DataTable.CLASS_DESC</dd>
			* </dl>
			* @type Object | null
			*/
			this.setAttributeConfig("sortedBy", {
				value: null,
				// TODO: accepted array for nested sorts
				validator: function(oNewSortedBy) {
					if(oNewSortedBy) {
						return (YAHOO.lang.isObject(oNewSortedBy) && oNewSortedBy.key);
					}
					else {
						return (oNewSortedBy === null);
					}
				},
				method: function(oNewSortedBy) {
					// Stash the previous value
					var oOldSortedBy = this.get("sortedBy");
					
					// Workaround for bug 1827195
					this._configs.sortedBy.value = oNewSortedBy;

					// Remove ASC/DESC from TH
					var oOldColumn,
						nOldColumnKeyIndex,
						oNewColumn,
						nNewColumnKeyIndex;
						
					if(this._elThead) {
						if(oOldSortedBy && oOldSortedBy.key && oOldSortedBy.dir) {
							oOldColumn = this._oColumnSet.getColumn(oOldSortedBy.key);
							nOldColumnKeyIndex = oOldColumn.getKeyIndex();
							
							// Remove previous UI from THEAD
							var elOldTh = oOldColumn.getThEl();
							Dom.removeClass(elOldTh, oOldSortedBy.dir);
							this.formatTheadCell(oOldColumn.getThLinerEl().firstChild, oOldColumn, oNewSortedBy);
						}
						if(oNewSortedBy) {
							oNewColumn = (oNewSortedBy.column) ? oNewSortedBy.column : this._oColumnSet.getColumn(oNewSortedBy.key);
							nNewColumnKeyIndex = oNewColumn.getKeyIndex();
			
							// Update THEAD with new UI
							var elNewTh = oNewColumn.getThEl();
							// Backward compatibility
							if(oNewSortedBy.dir && ((oNewSortedBy.dir == "asc") ||  (oNewSortedBy.dir == "desc"))) {
								var newClass = (oNewSortedBy.dir == "desc") ?
										YAHOO.widget.DataTable.CLASS_DESC :
										YAHOO.widget.DataTable.CLASS_ASC;
								Dom.addClass(elNewTh, newClass);
							}
							else {
								 var sortClass = oNewSortedBy.dir || YAHOO.widget.DataTable.CLASS_ASC;
								 Dom.addClass(elNewTh, sortClass);
							}
							this.formatTheadCell(oNewColumn.getThLinerEl().firstChild, oNewColumn, oNewSortedBy);
						}
					}
				  
					if(this._elTbody) {
						// Update TBODY UI
						this._elTbody.style.display = "none";
						var allRows = this._elTbody.rows,
							allCells;
						for(var i=allRows.length-1; i>-1; i--) {
							allCells = allRows[i].childNodes;
							if(allCells[nOldColumnKeyIndex]) {
								Dom.removeClass(allCells[nOldColumnKeyIndex], oOldSortedBy.dir);
							}
							if(allCells[nNewColumnKeyIndex]) {
								Dom.addClass(allCells[nNewColumnKeyIndex], oNewSortedBy.dir);
							}
						}
						this._elTbody.style.display = "";
					}
						
					this._clearTrTemplateEl();
				}
			});
			
			/**
			* @attribute paginator
			* @description An instance of YAHOO.widget.Paginator.
			* @default null
			* @type {Object|YAHOO.widget.Paginator}
			*/
			this.setAttributeConfig("paginator", {
				value : null,
				validator : function (val) {
					return val === null || val instanceof YAHOO.widget.Paginator;
				},
				method : function () { this._updatePaginator.apply(this,arguments); }
			});

			/**
			* @attribute caption
			* @description Value for the CAPTION element. NB: Not supported in
			* ScrollingDataTable.    
			* @type String
			*/
			this.setAttributeConfig("caption", {
				value: null,
				validator: YAHOO.lang.isString,
				method: function(sCaption) {
					this._initCaptionEl(sCaption);
				}
			});

			/**
			* @attribute draggableColumns
			* @description True if Columns are draggable to reorder, false otherwise.
			* The Drag & Drop Utility is required to enable this feature. Only top-level
			* and non-nested Columns are draggable. Write once.
			* @default false
			* @type Boolean
			*/
			this.setAttributeConfig("draggableColumns", {
				value: false,
				validator: YAHOO.lang.isBoolean,
				method: function(oParam) {
					if(this._elThead) {
						if(oParam) {
							this._initDraggableColumns();
						}
						else {
							this._destroyDraggableColumns();
						}
					}
				}
			});

			/**
			* @attribute renderLoopSize 	 
			* @description A value greater than 0 enables DOM rendering of rows to be
			* executed from a non-blocking timeout queue and sets how many rows to be
			* rendered per timeout. Recommended for very large data sets.     
			* @type Number 	 
			* @default 0 	 
			*/ 	 
			 this.setAttributeConfig("renderLoopSize", { 	 
				 value: 0, 	 
				 validator: YAHOO.lang.isNumber 	 
			 }); 	 

			/**
			* @attribute formatRow
			* @description A function that accepts a TR element and its associated Record
			* for custom formatting. The function must return TRUE in order to automatically
			* continue formatting of child TD elements, else TD elements will not be
			* automatically formatted.
			* @type function
			* @default null
			*/
			this.setAttributeConfig("formatRow", {
				value: null,
				validator: YAHOO.lang.isFunction
			});

			/**
			* @attribute generateRequest
			* @description A function that converts an object literal of desired DataTable
			* states into a request value which is then passed to the DataSource's
			* sendRequest method in order to retrieve data for those states. This
			* function is passed an object literal of state data and a reference to the
			* DataTable instance:
			*     
			* <dl>
			*   <dt>pagination<dt>
			*   <dd>        
			*         <dt>offsetRecord</dt>
			*         <dd>{Number} Index of the first Record of the desired page</dd>
			*         <dt>rowsPerPage</dt>
			*         <dd>{Number} Number of rows per page</dd>
			*   </dd>
			*   <dt>sortedBy</dt>
			*   <dd>                
			*         <dt>key</dt>
			*         <dd>{String} Key of sorted Column</dd>
			*         <dt>dir</dt>
			*         <dd>{String} Sort direction, either YAHOO.widget.DataTable.CLASS_ASC or YAHOO.widget.DataTable.CLASS_DESC</dd>
			*   </dd>
			*   <dt>self</dt>
			*   <dd>The DataTable instance</dd>
			* </dl>
			* 
			* and by default returns a String of syntax:
			* "sort={sortColumn}&dir={sortDir}&startIndex={pageStartIndex}&results={rowsPerPage}"
			* @type function
			* @default HTMLFunction
			*/
			this.setAttributeConfig("generateRequest", {
				value: function(oState, oSelf) {
					// Set defaults
					oState = oState || {pagination:null, sortedBy:null};
					var sort = encodeURIComponent((oState.sortedBy) ? oState.sortedBy.key : oSelf.getColumnSet().keys[0].getKey());
					var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "desc" : "asc";
					var startIndex = (oState.pagination) ? oState.pagination.recordOffset : 0;
					var results = (oState.pagination) ? oState.pagination.rowsPerPage : null;
					
					// Build the request
					return  "sort=" + sort +
							"&dir=" + dir +
							"&startIndex=" + startIndex +
							((results !== null) ? "&results=" + results : "");
				},
				validator: YAHOO.lang.isFunction
			});

			/**
			* @attribute initialRequest
			* @description Defines the initial request that gets sent to the DataSource
			* during initialization. Value is ignored if initialLoad is set to any value
			* other than true.    
			* @type MIXED
			* @default null
			*/
			this.setAttributeConfig("initialRequest", {
				value: null
			});

			/**
			* @attribute initialLoad
			* @description Determines whether or not to load data at instantiation. By
			* default, will trigger a sendRequest() to the DataSource and pass in the
			* request defined by initialRequest. If set to false, data will not load
			* at instantiation. Alternatively, implementers who wish to work with a 
			* custom payload may pass in an object literal with the following values:
			*     
			*    <dl>
			*      <dt>request (MIXED)</dt>
			*      <dd>Request value.</dd>
			*
			*      <dt>argument (MIXED)</dt>
			*      <dd>Custom data that will be passed through to the callback function.</dd>
			*    </dl>
			*
			*                    
			* @type Boolean | Object
			* @default true
			*/
			this.setAttributeConfig("initialLoad", {
				value: true
			});
			
			/**
			* @attribute dynamicData
			* @description If true, sorting and pagination are relegated to the DataSource
			* for handling, using the request returned by the "generateRequest" function.
			* Each new DataSource response blows away all previous Records. False by default, so 
			* sorting and pagination will be handled directly on the client side, without
			* causing any new requests for data from the DataSource.
			* @type Boolean
			* @default false
			*/
			this.setAttributeConfig("dynamicData", {
				value: false,
				validator: YAHOO.lang.isBoolean
			});

			/**
			 * @attribute MSG_EMPTY 	 
			 * @description Message to display if DataTable has no data.     
			 * @type String 	 
			 * @default "No records found." 	 
			 */ 	 
			 this.setAttributeConfig("MSG_EMPTY", { 	 
				 value: "No records found.", 	 
				 validator: YAHOO.lang.isString 	 
			 }); 	 

			/**
			 * @attribute MSG_LOADING	 
			 * @description Message to display while DataTable is loading data.
			 * @type String 	 
			 * @default "Loading..." 	 
			 */ 	 
			 this.setAttributeConfig("MSG_LOADING", { 	 
				 value: "Loading...", 	 
				 validator: YAHOO.lang.isString 	 
			 }); 	 

			/**
			 * @attribute MSG_ERROR	 
			 * @description Message to display while DataTable has data error.
			 * @type String 	 
			 * @default "Data error." 	 
			 */ 	 
			 this.setAttributeConfig("MSG_ERROR", { 	 
				 value: "Data error.", 	 
				 validator: YAHOO.lang.isString 	 
			 }); 	 

			/**
			 * @attribute MSG_SORTASC 
			 * @description Message to display in tooltip to sort Column in ascending order.
			 * @type String 	 
			 * @default "Click to sort ascending" 	 
			 */ 	 
			 this.setAttributeConfig("MSG_SORTASC", { 	 
				 value: "Click to sort ascending", 	 
				 validator: YAHOO.lang.isString,
				 method: function(sParam) {
					if(this._elThead) {
						for(var i=0, allKeys=this.getColumnSet().keys, len=allKeys.length; i<len; i++) {
							if(allKeys[i].sortable && this.getColumnSortDir(allKeys[i]) === YAHOO.widget.DataTable.CLASS_ASC) {
								allKeys[i]._elThLabel.firstChild.title = sParam;
							}
						}
					}      
				 }
			 });

			/**
			 * @attribute MSG_SORTDESC 
			 * @description Message to display in tooltip to sort Column in descending order.
			 * @type String 	 
			 * @default "Click to sort descending" 	 
			 */ 	 
			 this.setAttributeConfig("MSG_SORTDESC", { 	 
				 value: "Click to sort descending", 	 
				 validator: YAHOO.lang.isString,
				 method: function(sParam) {
					if(this._elThead) {
						for(var i=0, allKeys=this.getColumnSet().keys, len=allKeys.length; i<len; i++) {
							if(allKeys[i].sortable && this.getColumnSortDir(allKeys[i]) === YAHOO.widget.DataTable.CLASS_DESC) {
								allKeys[i]._elThLabel.firstChild.title = sParam;
							}
						}
					}               
				 }
			 });
			 
			/**
			 * @attribute currencySymbol
			 * @deprecated
			 */
			this.setAttributeConfig("currencySymbol", {
				value: "$",
				validator: YAHOO.lang.isString
			});
			
			/**
			 * Default config passed to YAHOO.util.Number.format() by the 'currency' Column formatter.
			 * @attribute currencyOptions
			 * @type Object
			 * @default {prefix: $, decimalPlaces:2, decimalSeparator:".", thousandsSeparator:","}
			 */
			this.setAttributeConfig("currencyOptions", {
				value: {
					prefix: this.get("currencySymbol"), // TODO: deprecate currencySymbol
					decimalPlaces:2,
					decimalSeparator:".",
					thousandsSeparator:","
				}
			});
			
			/**
			 * Default config passed to YAHOO.util.Date.format() by the 'date' Column formatter.
			 * @attribute dateOptions
			 * @type Object
			 * @default {format:"%m/%d/%Y", locale:"en"}
			 */
			this.setAttributeConfig("dateOptions", {
				value: {format:"%m/%d/%Y", locale:"en"}
			});
			
			/**
			 * Default config passed to YAHOO.util.Number.format() by the 'number' Column formatter.
			 * @attribute numberOptions
			 * @type Object
			 * @default {decimalPlaces:0, thousandsSeparator:","}
			 */
			this.setAttributeConfig("numberOptions", {
				value: {
					decimalPlaces:0,
					thousandsSeparator:","
				}
			});
		
			this.setAttributeConfig("rowExpansionTemplate", {
                        value: "",
                        validator: function( template ){
                    return (
                        YAHOO.lang.isString( template ) ||
                        YAHOO.lang.isFunction( template )
                    );
            },
                method: this.initRowExpansion
            });

        },

        initRowExpansion : function( template ){
            //Set subscribe restore method
            this.subscribe('postRenderEvent',
                this.onEventRestoreRowExpansion);

            //Setup template
            this.rowExpansionTemplate = template;

            //Set table level state
            this.a_rowExpansions = [];
        },

        toggleRowExpansion : function( record_id ){
            var state = this._getRecordState( record_id );
            
            var row_data          = this.getRecord( record_id );
            var row               = this.getRow( row_data );
            
            if( state && state.expanded ){
            	 row.firstChild.innerHTML = '<img src= "' + REPOSITORY_LOCAL + '/icons/arrow_right.gif" />';
                this.collapseRow( record_id );
            } else {
            	 row.firstChild.innerHTML = '<img src= "' + REPOSITORY_LOCAL + '/icons/arrow_down.gif" />';
                this.expandRow( record_id );
            }
        },


        expandRow : function( record_id, restore ){

            var state = this._getRecordState( record_id );
            
           

            if( !state.expanded || restore ){

                var row_data          = this.getRecord( record_id ),
                    row               = this.getRow( row_data ),
                    new_row           = document.createElement('tr'),
                    column_length     = this.getFirstTrEl().childNodes.length,
                    expanded_data     = row_data.getData(),
                    expanded_content  = null,
                    template          = this.rowExpansionTemplate,
                    next_sibling      = Dom.getNextSibling( row );

                
                //Construct expanded row body
                new_row.className = CLASS_EXPANSION;
                var new_column = document.createElement( 'td' );
                new_column.colSpan = column_length;

                new_column.innerHTML = '<div class="'+CLASS_LINER+'"></div>';
                new_row.appendChild( new_column );

                var liner_element = new_row.firstChild.firstChild;

                if( YAHOO.lang.isString( template ) ){

                    liner_element.innerHTML = YAHOO.lang.substitute( 
                        template, 
                        expanded_data
                    );

                } else if( YAHOO.lang.isFunction( template ) ) {

                    template( {
                        row_element : new_row,
                        liner_element : liner_element,
                        data : row_data, 
                        state : state 

                    } );

                } else {
                    return false;
                }

                //Insert new row
                newRow = Dom.insertAfter( new_row, row );

                if (newRow.innerHTML.length) {

                    this._setRecordState( record_id, 'expanded', true );

                    if( !restore ){
                        this.a_rowExpansions.push(
                            this.getRecord(record_id).getId()
                        );
                    }

                    Dom.removeClass( row, CLASS_COLLAPSED );
                    Dom.addClass( row, CLASS_EXPANDED );

                    //Fire custom event
                    this.fireEvent( "rowExpandEvent",
                        { record_id : row_data.getId() } );

                    return true;
                } else {
                    return false;
                } 
            }
        },

        collapseRow : function( record_id ){
            var row_data    = this.getRecord( record_id ),
                row         = Dom.get( row_data.getId() ),
                state       = row_data.getData( STRING_STATENAME );

            if( state && state.expanded ){
                var next_sibling = Dom.getNextSibling( row ),
                        hash_index = indexOf(this.a_rowExpansions, record_id);

                if( Dom.hasClass( next_sibling, CLASS_EXPANSION ) ) {
                    next_sibling.parentNode.removeChild( next_sibling );
                    this.a_rowExpansions.splice( hash_index, 1 );
                    this._setRecordState( record_id, 'expanded', false );

                    Dom.addClass( row, CLASS_COLLAPSED );
                    Dom.removeClass( row, CLASS_EXPANDED );

                    //Fire custom event
                    this.fireEvent("rowCollapseEvent",
                        {record_id:row_data.getId()});

                    return true;
                } else {
                    return false;
                }
            }
        },

        collapseAllRows : function(){
            var rows = this.a_rowExpansions;

            for( var i = 0, l = rows.length; l > i; i++ ){
                //Always pass 0 since collapseRow
                //removes item from the a_rowExpansions array
                this.collapseRow( rows[ 0 ] );

            }

            a_rowExpansions = [];

        },

        restoreExpandedRows : function(){
            var expanded_rows = this.a_rowExpansions;
            
            if( !expanded_rows.length ){
                return;
            }

            if( this.a_rowExpansions.length ){
                for( var i = 0, l = expanded_rows.length; l > i; i++ ){
                    this.expandRow( expanded_rows[ i ] , true );
                }
            }
        },

        onEventRestoreRowExpansion : function( oArgs ){
            this.restoreExpandedRows();
        },

        onEventToggleRowExpansion : function( oArgs ){
            if(YAHOO.util.Dom.hasClass(oArgs.target,
                'yui-dt-expandablerow-trigger')){
                this.toggleRowExpansion( oArgs.target );
            }
        }

    }, true//This boolean needed to override members of the original object
);

})();
