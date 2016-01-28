/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

if(!ORYX.Plugins) {
    ORYX.Plugins = new Object();
}

ORYX.Plugins.DataIOEditorPlugin = {

    currentElement: undefined,

    construct: function (facade) {
        this.facade = facade;

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DATAIOEDITOR_SHOW, this.showDataIOEditor.bind(this));
    },

    showDataIOEditor: function (event) {
        this.currentElement = event.element;
        this.getDataTypesForDataIOEditor(this.currentElement);
    },

    getDataTypesForDataIOEditor: function (element) {
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        Ext.Ajax.request({
            url: ORYX.PATH + 'calledelement',
            method: 'POST',
            success: function (response) {
                try {
                    if (response.responseText.length >= 0 && response.responseText != "false") {
                        var responseJson = Ext.decode(response.responseText);
                        this.doShowDataIOEditor(element, responseJson);
                    } else {
                        this.facade.raiseEvent({
                            type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype: 'error',
                            msg: 'Unable to find Data Types.',
                            title: ''

                        });
                    }
                } catch (e) {
                    this.facade.raiseEvent({
                        type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype: 'error',
                        msg: 'Error retrieving Data Types info ' + ' :\n' + e,
                        title: ''

                    });
                }
            }.bind(this),
            failure: function () {
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype: 'error',
                    msg: 'Error retrieving Data Types info.',
                    title: ''

                });
            },
            params: {
                profile: ORYX.PROFILE,
                uuid: ORYX.UUID,
                ppackage: processPackage,
                pid: processId,
                action: 'showdatatypes'
            }
        });
    },

    doShowDataIOEditor: function (element, dataTypesJson) {
        var javaDataTypes = "";
        var unsortedData = new Array();
        for (var key in dataTypesJson) {
            var keyVal = dataTypesJson[key];
            unsortedData.push(keyVal);
        }
        unsortedData.sort();

        for (var t = 0; t < unsortedData.length; t++) {
            var presStr = unsortedData[t];
            var presStrParts = presStr.split(".");

            var classPart = presStrParts[presStrParts.length - 1];
            var pathPart = presStr.substring(0, presStr.length - (classPart.length + 1));

            var newType = classPart + " [" + pathPart + "]:" + presStr;
            javaDataTypes = javaDataTypes + newType;
            if (t < unsortedData.length - 1) {
                javaDataTypes = javaDataTypes + ",";
            }
        }

        var testTypes = ""; //"UserCommand [org.dummy.examples.cmd]:org.dummy.examples.cmd.UserCommand,User [org.dummy.examples.data]:org.dummy.examples.data.User,";

        var datatypes = "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object, ******:******," + testTypes + javaDataTypes;

        var stencil = element.getStencil();

        var taskname = undefined;
        if (stencil.property('oryx-name') !== undefined) {
            taskname = element.properties['oryx-name'];
        }
        var datainput = undefined;
        if (stencil.property('oryx-datainput') !== undefined) {
            datainput = element.properties['oryx-datainput'];
        }
        var datainputset = undefined;
        if (stencil.property('oryx-datainputset') !== undefined) {
            datainputset = element.properties['oryx-datainputset'];
        }

        var dataoutput = undefined;
        if (stencil.property('oryx-dataoutput') !== undefined) {
            dataoutput = element.properties['oryx-dataoutput'];
        }
        var dataoutputset = undefined;
        if (stencil.property('oryx-dataoutputset') !== undefined) {
            dataoutputset = element.properties['oryx-dataoutputset'];
        }

        var assignments = undefined;
        if (stencil.property('oryx-assignments') !== undefined) {
            assignments = element.properties['oryx-assignments'];
        }
        else if (stencil.property('oryx-datainputassociations') !== undefined) {
            assignments = element.properties['oryx-datainputassociations'];
        }
        else if (stencil.property('oryx-dataoutputassociations') !== undefined) {
            assignments = element.properties['oryx-dataoutputassociations'];
        }

        var processvars = ORYX.DataIOEditorUtils.getProcessVars(element);

        var disallowedPropertyNames = ORYX.DataIOEditorUtils.getDisallowedPropertyNames(element);

        parent.designersignalshowdataioeditor(taskname, datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, datatypes, disallowedPropertyNames,
                function (data) {
                    //window.alert("passed back to dataioeditor: " + data);
                    var obj = JSON.parse(data);

                    var element = this.currentElement;
                    var stencil = element.getStencil();

                    var newProperties = new Hash();
                    var oldProperties = new Hash();
                    if (stencil.property('oryx-datainput') !== undefined) {
                        newProperties['oryx-datainput'] = obj['inputVariables'];
                        oldProperties['oryx-datainput'] = element.properties['oryx-datainput'];
                    }
                    if (stencil.property('oryx-datainputset') !== undefined) {
                        newProperties['oryx-datainputset'] = obj['inputVariables'];
                        oldProperties['oryx-datainputset'] = element.properties['oryx-datainputset'];
                    }

                    if (stencil.property('oryx-dataoutput') !== undefined) {
                        newProperties['oryx-dataoutput'] = obj['outputVariables'];
                        oldProperties['oryx-dataoutput'] = element.properties['oryx-dataoutput'];
                    }
                    if (stencil.property('oryx-dataoutputset') !== undefined) {
                        newProperties['oryx-dataoutputset'] = obj['outputVariables'];
                        oldProperties['oryx-dataoutputset'] = element.properties['oryx-dataoutputset'];
                    }

                    if (stencil.property('oryx-assignments') !== undefined) {
                        newProperties['oryx-assignments'] = obj['assignments'];
                        oldProperties['oryx-assignments'] = element.properties['oryx-assignments'];
                    }
                    else if (stencil.property('oryx-datainputassociations') !== undefined) {
                        newProperties['oryx-datainputassociations'] = obj['assignments'];
                        oldProperties['oryx-datainputassociations'] = element.properties['oryx-datainputassociations'];
                    }
                    else if (stencil.property('oryx-dataoutputassociations') !== undefined) {
                        newProperties['oryx-dataoutputassociations'] = obj['assignments'];
                        oldProperties['oryx-dataoutputassociations'] = element.properties['oryx-dataoutputassociations'];
                    }

                    if (stencil.property('oryx-assignments') !== undefined) {
                        newProperties['oryx-assignmentsview'] = obj['variablecountsstring'];
                        oldProperties['oryx-assignmentsview'] = element.properties['oryx-assignmentsview'];
                    }
                    else if (stencil.property('oryx-datainputassociations') !== undefined) {
                        newProperties['oryx-datainputassociationsview'] = obj['variablecountsstring'];
                        oldProperties['oryx-datainputassociationsview'] = element.properties['oryx-datainputassociationsview'];
                    }
                    else if (stencil.property('oryx-dataoutputassociations') !== undefined) {
                        newProperties['oryx-dataoutputassociationsview'] = obj['variablecountsstring'];
                        oldProperties['oryx-dataoutputassociationsview'] = element.properties['oryx-dataoutputassociationsview'];
                    }
                    this.setElementProperties(element, newProperties, oldProperties);

                }.bind(this)
        );
    },

    setElementProperties: function (element, newProperties, oldProperties) {

        var facade = this.facade;

        // Implement the specific command for property change
        var commandClass = ORYX.Core.Command.extend({
            construct: function () {
                this.newProperties = newProperties;
                this.oldProperties = oldProperties;
                this.selectedElements = [element];
                this.facade = facade;
            },
            execute: function () {
                this.newProperties.each(function (pair) {
                    if (!element.getStencil().property(pair.key).readonly()) {
                        element.setProperty(pair.key, pair.value);
                    }
                }.bind(this));
                this.facade.setSelection(this.selectedElements);
                this.facade.getCanvas().update();
                this.facade.updateSelection();
            },
            rollback: function () {
                this.oldProperties.each(function (pair) {
                    if (!element.getStencil().property(pair.key).readonly()) {
                        element.setProperty(pair.key, pair.value);
                    }
                }.bind(this));
                this.facade.setSelection(this.selectedElements);
                this.facade.getCanvas().update();
                this.facade.updateSelection();
            }
        })
        // Instantiate the class
        var command = new commandClass();

        // Execute the command
        this.facade.executeCommands([command]);

        newProperties.each(function (pair) {
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED,
                elements: [element],
                key: pair.key,
                value: pair.value
            });
        }.bind(this));

    }

}

ORYX.Plugins.DataIOEditorPlugin = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.DataIOEditorPlugin);

ORYX.DataIOEditorUtils = {
    /**
     * Tests whether an element has any properties related to Data I/O
     *
     * @param element
     * @returns {boolean}
     */
    hasDataIOProperty: function(element) {
        var stencil = element.getStencil();
        var dataIOPropertyIds = ["oryx-assignmentsview", "oryx-datainputassociationsview", "oryx-dataoutputassociationsview",
            "oryx-datainput", "oryx-datainputset", "oryx-dataoutput", "oryx-dataoutputset"];
        for (var i = 0; i < dataIOPropertyIds.length; i++) {
            if (stencil.property(dataIOPropertyIds[i]) !== undefined) {
                var property = stencil.property(dataIOPropertyIds[i]);
                if((property.visible() && property.visible() == true) && property.hidden() != true) {
                    var tasktype = element.properties["oryx-tasktype"];
                    if(property.fortasktypes() && property.fortasktypes().length > 0) {
                        var tts = property.fortasktypes().split("|");
                        for(var i = 0; i < tts.size(); i++) {
                            if(tts[i] == tasktype) {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    },

    getDisallowedPropertyNames : function (element) {
        if (element.properties['oryx-tasktype'] !== undefined && element.properties['oryx-tasktype'] == "User") {
            return "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify";
        }
        else {
            return "";
        }
    },

    getProcessVars: function (element) {
        var vars = "** " + ORYX.I18N.DataIOEditorPlugin.VariableDefinitions + " **,";

        if (element && element.parent) {
            var parentvars = this.getParentVars(element.parent);
            if (parentvars && parentvars.length > 0) {
                vars = vars + parentvars; // + '******:******,';
            }
        }

        var processvars = "";
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var vardefs = jsonPath(processJSON.evalJSON(), "$.properties.vardefs");
        if (vardefs) {
            vardefs.forEach(function (item) {
                if (item.length > 0) {
                    processvars = processvars + item + ',';
                }
            });
        }
        if (processvars && processvars.length > 0) {
            vars = vars + processvars;
        }
        return vars;
    },

    getParentVars: function (thisNode) {
        var parentvars = "";
        if (thisNode) {
            if (thisNode._stencil._jsonStencil.id == "http://b3mn.org/stencilset/bpmn2.0#MultipleInstanceSubprocess"
                    || thisNode._stencil._jsonStencil.id == "http://b3mn.org/stencilset/bpmn2.0#Subprocess"
                    || thisNode._stencil._jsonStencil.id == "http://b3mn.org/stencilset/bpmn2.0#EventSubprocess"
                    || thisNode._stencil._jsonStencil.id == "http://b3mn.org/stencilset/bpmn2.0#AdHocSubprocess") {

                var vardefsprop = thisNode.properties["oryx-vardefs"];
                if (vardefsprop && vardefsprop.length > 0) {
                    parentvars = parentvars + this.sortVarsString(vardefsprop);
                }

                if (thisNode._stencil._jsonStencil.id == "http://b3mn.org/stencilset/bpmn2.0#MultipleInstanceSubprocess") {
                    var midatainputsprop = thisNode.properties["oryx-multipleinstancedatainput"];
                    if (midatainputsprop && midatainputsprop.length > 0) {
                        parentvars = parentvars + this.sortVarsString(midatainputsprop);
                    }

                    var midataOutputsprop = thisNode.properties["oryx-multipleinstancedataoutput"];
                    if (midataOutputsprop && midataOutputsprop.length > 0) {
                        parentvars = parentvars + this.sortVarsString(midataOutputsprop);
                    }
                }
            }
            if (thisNode.parent) {
                var grandparentvars = this.getParentVars(thisNode.parent);
                if (grandparentvars && grandparentvars.length > 0) {
                    parentvars = parentvars + grandparentvars;
                }
            }
        }
        return parentvars;
    },

    sortVarsString: function (varsString) {
        if (!varsString || varsString.length < 1) {
            return "";
        }
        var arrVars = varsString.split(",");
        arrVars.sort();
        var sortedVarString = "";
        for (var i = 0; i < arrVars.length; i++) {
            sortedVarString = sortedVarString + arrVars[i] + ",";
        }
        return sortedVarString + ',';
    },

    setAssignmentsViewProperty: function (element) {
        var stencil = element.getStencil();

        var datainput = undefined;
        if (stencil.property('oryx-datainput') !== undefined) {
            datainput = element.properties['oryx-datainput'];
        }
        var datainputset = undefined;
        if (stencil.property('oryx-datainputset') !== undefined) {
            datainputset = element.properties['oryx-datainputset'];
        }

        var dataoutput = undefined;
        if (stencil.property('oryx-dataoutput') !== undefined) {
            dataoutput = element.properties['oryx-dataoutput'];
        }
        var dataoutputset = undefined;
        if (stencil.property('oryx-dataoutputset') !== undefined) {
            dataoutputset = element.properties['oryx-dataoutputset'];
        }

        var assignments = undefined;
        if (stencil.property('oryx-assignments') !== undefined) {
            assignments = element.properties['oryx-assignments'];
        }
        else if (stencil.property('oryx-datainputassociations') !== undefined) {
            assignments = element.properties['oryx-datainputassociations'];
        }
        else if (stencil.property('oryx-dataoutputassociations') !== undefined) {
            assignments = element.properties['oryx-dataoutputassociations'];
        }

        var processvars = this.getProcessVars(element);

        var disallowedPropertyNames = this.getDisallowedPropertyNames(element);

        var assignmentsViewProperty = parent.designersignalgetassignmentsviewproperty(datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, disallowedPropertyNames);
        if (assignmentsViewProperty && assignmentsViewProperty.length > 0) {
            if (stencil.property('oryx-assignmentsview') !== undefined) {
                element.setProperty('oryx-assignmentsview', assignmentsViewProperty);
            }
            else if (stencil.property('oryx-datainputassociationsview') !== undefined) {
                element.setProperty('oryx-datainputassociationsview', assignmentsViewProperty);
            }
            else if (stencil.property('oryx-dataoutputassociationsview') !== undefined) {
                element.setProperty('oryx-dataoutputassociationsview', assignmentsViewProperty);
            }
        }

    }
}