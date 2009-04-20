/**
 * Copyright (c) 2009, Andreas Meyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.ResourceAssignment = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.ResourceAssignment.name,
            'functionality': this.assignResource.bind(this),
            'group': ORYX.I18N.ResourceAssignment.group,
           // 'dropDownGroupIcon': ORYX.PATH + "images/hr.png",
            'icon': ORYX.PATH + "images/hr.png",
            'description': ORYX.I18N.ResourceAssignment.desc,
            'index': 0,
            'toggle': false,
            'minShape': 1,
            'maxShape': 0
        });
		
    },
    
	assignResource: function(){
    	//variable declaration
    	var selectedElements = this.facade.getSelection(); //all selected elements
    	var taskElements = []; //all selected tasks
    	var checkedValues = []; //values checked in resource selection dialog
    	var i = 0; //counter
    	var len = selectedElements.length; //number of selected elements
    	
    	//get only selected tasks
    	for (var index = 0; index < len; index++) {
    		var item = selectedElements[index];
    		if(item.properties["oryx-activitytype"] == "Task") {
    			taskElements[i] = item;
				if(taskElements[i].properties["oryx-id"] == "") {
					taskElements[i].setProperty("oryx-id", taskElements[i].id);
				}
    			i++;
    		}
    	}

    	//get allocation type and resources to be possibly assigned to the current task
		var allocationTypeData = this.handleAllocationTypeData(taskElements[0].properties["oryx-id"]);
		if(allocationTypeData[0] != null) { //skip following if no resource/ role is selected
			var resourceData = allocationTypeData[0];
			var allocationType = allocationTypeData[1];
			
			if(resourceData != "--automatic execution--") { //skip following if automatic execution shall take place
				//Dialog to select resources respectively roles
				var resourceSelection = new Ext.Window({
					frame: true,
					title: 'Please choose resources for assignment',
					width: 600,
					modal: true,
					closable: false,
					plain: true,
					items: {
						xtype: 'checkbox',
						boxLabel: resourceData.resource[0].resource,
						name: 'checkbox0'
					},
					buttons: [{
						text: 'Select',
						handler: function() {
							resourceSelection.close();
							checkedValues = this.getCheckedValues(resourceSelection);
							this.writeAssignments(checkedValues, taskElements, allocationType);
						}.bind(this)
					},{
						text: 'Close',
						handler: function() {
							resourceSelection.close();
						}.bind(this)
					}]
				});
				var checkbox;
				var checkboxName;
				//add one checkbox for each entry (resource, role) to dialog window
				for (var index = 1; index < resourceData.resource.length; index++) {
					checkboxName = "checkbox" + index;
					checkbox = new Ext.form.Checkbox ({
					boxLabel: resourceData.resource[index].resource,
					name: checkboxName
					});
					resourceSelection.add(checkbox);
				}
				resourceSelection.show();
			} else { //automatic execution
				checkedValues[0] = resourceData;
				this.writeAssignments(checkedValues, taskElements, allocationType);
			}
		}
    },
	
	getResourceData: function(allocationType) { //Ajax request to get resource/ role information from servlet
		var resp;
		//get Resource List
		new Ajax.Request(ORYX.CONFIG.ROOT_PATH + 'resourceList', {
			method: 'POST',
			asynchronous: false,
			parameters: {
				allocation: allocationType
			},
			onSuccess: function(request){
				resp = request.responseText.evalJSON();
				returnValue = resp;
			}.bind(this)
    	});
		return resp;
	},
	
	getCheckedValues: function(resourceSelection) { //identify check elements and return the value
		var selectedValues = [];
		var j = 0;
		for (var index = 0; index < resourceSelection.items.items.length; index++) {
			if(resourceSelection.items.items[index].checked == true) {
				selectedValues[j] = resourceSelection.items.items[index].boxLabel;
				j++;
			}
		}
		return selectedValues;
	},
	
	writeAssignments: function(newResources, taskElements, allocationType) { //create and write JSON object to task attributes
		for (var taskCounter = 0; taskCounter < taskElements.length; taskCounter++) {
			var jsonObjectString;
			var currentTask = taskElements[taskCounter];
			if (currentTask.properties["oryx-resourceassignments"] != "") { //assignments existing?
				var jsonObject = currentTask.properties["oryx-resourceassignments"].evalJSON();
				var totalCount = parseInt(jsonObject.totalCount) + newResources.length;
				var items = jsonObject.items.toArray();
				//eliminate duplicates
				for (var m = 0; m < newResources.length; m++) {
					for (var n = 0; n < items.length; n++) {
						if (items[n].assignmentName == newResources[m]) {
							if (items[n].assignmentType == allocationType) { //same allocation type, otherwise resources are not equal
								//remove duplicate
								newResources.splice(m, 1);
								totalCount--;
								m--;
								break;
							}
						}
					}
				}
				
				//write existing assignments
				jsonObjectString = "{'totalCount':" + totalCount + ", 'items':[{assignmentType:\"" + items[0].assignmentType + "\", assignmentName:\"" + items[0].assignmentName + "\"}";
				for (var c = 1; c < items.length; c++) {
					jsonObjectString = jsonObjectString + ", {assignmentType:\"" + items[c].assignmentType + "\", assignmentName:\"" + items[c].assignmentName + "\"}";
				}
				//write new assignments
				for (var c = 0; c < newResources.length; c++) {
					jsonObjectString = jsonObjectString + ", {assignmentType:\"" + allocationType + "\", assignmentName:\"" + newResources[c] + "\"}";
				}
				jsonObjectString = jsonObjectString + "]}";
			}
			else { //no existing assignments
				//write new assignments
				jsonObjectString = "{'totalCount':" + newResources.length + ", 'items':[{assignmentType:\"" + allocationType + "\", assignmentName:\"" + newResources[0] + "\"}";
				for (var c = 1; c < newResources.length; c++) {
					jsonObjectString = jsonObjectString + ", {assignmentType:\"" + allocationType + "\", assignmentName:\"" + newResources[c] + "\"}";
				}
				jsonObjectString = jsonObjectString + "]}";
			}
			//complete allocation by writing final entry
			currentTask.setProperty("oryx-resourceassignments", jsonObjectString);
			
		}
	},
	
	handleAllocationTypeData: function(taskId) { //ask for type of allocation via a prompt and collect resource information to return it
		var returnValue = [];
		var resourceData;
		//get possible new assignments for current task
		var promptResult = prompt("for task " + taskId + "\n1: direct, \n2: role, \n3: org, \n4: auto");
		if(promptResult != "1" && promptResult != "2" && promptResult != "3" && promptResult != "4" && promptResult != null) {
			alert("wrong entry, please try again");
			returnValue = this.handleAllocationTypeData(taskId);
		} else {
			if(promptResult == "1") {
				resourceData = this.getResourceData("direct");
				promptResult = "Single User";
				//promptResult = "Direct Allocation";
			} else if(promptResult == "2") {
				resourceData = this.getResourceData("functional");
				promptResult = "Functional Role";
				//promptResult = "Functional-Role-based Allocation";
			} else if(promptResult == "3") {
				resourceData = this.getResourceData("organisational");
				promptResult = "Organisational Role";
				//promptResult = "Organisational-Role-based Allocation";
			} else if(promptResult == "4") {
				resourceData = "--automatic execution--";
				promptResult = "Automatic Execution";
				//promptResult = "Automatic Execution";
			} else if(promptResult == null) {
				resourceData = null;
			}
			returnValue[0] = resourceData;
			returnValue[1] = promptResult;
		}
		return returnValue;
	},
    
    getSavedAssignmentsData: function(taskElement) {
    	var returnValue = [];
    	var elementIds = []; //collection of used ids for saved assignments
		var totalCount = 0; //number of defined separations for a specific task
    	var highestId = 1; //next element id for separations of a specific task
    	var j = 0; //counter
    	var m = 0; //counter
		var assignments = taskElement.properties["oryx-resourceassignments"]; //get saved assignments
		assignments = assignments.toString();
    	//store saved assignments including task ids
    	var savedAssignmentsString = assignments.substring(0,assignments.indexOf(']'));
    	//extract number of entries and element ids of these entries
    	while(assignments.indexOf(',') > -1) {
    		if(j%2 == 1) { //get element ids as integer
    			elementIds[m] = parseInt(assignments.substring((assignments.indexOf('"')) + 1));
    			m++;
    			assignments = assignments.substring((assignments.indexOf(',')) + 1);
    		} else if(j == 0) { //get total count as integer
    			totalCount = parseInt(assignments.substring((assignments.indexOf(':')) + 1));
    			assignments = assignments.substring((assignments.indexOf(',')) + 1);
    		} else { //task ids are already saved in "savedSeparations", so that these entries can be discarded
    			assignments = assignments.substring((assignments.indexOf(',')) + 1);
    		}
	        j++;
    	}
    	
    	//get highest element id
    	for(var index = 0; index < elementIds.length; index++) {
    		if(elementIds[index] > highestId) {
    			highestId = elementIds[index];
    		}
    	}
    	returnValue[0] = savedAssignmentsString;
    	returnValue[1] = totalCount;
    	returnValue[2] = highestId;
    	return returnValue;
    }
});