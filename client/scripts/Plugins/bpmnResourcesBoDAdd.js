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

ORYX.Plugins.ResourcesBoDAdd = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.ResourcesBoDAdd.name,
            'functionality': this.defineBoD.bind(this),
            'group': ORYX.I18N.ResourcesBoDAdd.group,
            'dropDownGroupIcon': ORYX.PATH + "images/bod.png",
            'icon': ORYX.PATH + "images/bod+.png",
            'description': ORYX.I18N.ResourcesBoDAdd.desc,
            'index': 3,
            'toggle': false,
            'minShape': 2,
            'maxShape': 0
        });
		
    },
    
	defineBoD: function(){
    	//variable declaration
    	var selectedElements = this.facade.getSelection(); //all selected elements
    	var taskElements = []; //all selected tasks
    	var selectedTasks; //new tasks to be separated
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
    			if(i == 0) { //get ids of tasks to be bound
    				selectedTasks = taskElements[i].properties["oryx-id"];
    			} else {
    				selectedTasks = selectedTasks + "; " + taskElements[i].properties["oryx-id"];
    			}
    			i++;
    		}
    	}
    	
		if (taskElements.length > 1) {
			for (var taskCounter = 0; taskCounter < taskElements.length; taskCounter++) {
				//variable declaration
				var savedBindingsData = [];	//already saved bindings
				var savedSeparationsData = false; //already saved separations
				
				//check whether other bindings are defined for current task
				if (taskElements[taskCounter].properties["oryx-bindingsofduties"] != "") {
					savedBindingsData = this.bindingsCheck(taskElements[taskCounter], selectedTasks);
					if (savedBindingsData[3] == true) {
						break;
					}
				} else {
					savedBindingsData[1] = 0; //total number of binding entries is 0 as field is empty
					savedBindingsData[2] = 0; //highest id used is 0 as no id is used
				}
				
				//check whether a contradicting separation is defined for current task
				if (taskElements[taskCounter].properties["oryx-separationofduties"] != ("")) {
					savedSeparationsData = this.separationsCheck(taskElements[taskCounter], selectedTasks);
					if (savedSeparationsData == true) {
						break;
					}
				}
				
				//write entry
				this.writeEntry(savedBindingsData, selectedTasks, taskElements[taskCounter]);
			}
		} else {
			alert("Please select at least two tasks to define constraints for.");
		}
    },
    
    bindingsCheck: function(taskElement, selectedTasks) { //check for existing bindings with same tasks
		var returnValue = [];
		//get previous binding of duties assignments
		var elementIds = []; //collection of used ids for saved separations
		var taskIds = []; //collection of saved task ids
		var totalCount = 0; //number of defined bindings for a specific task
    	var highestId = 1; //next element id for bindings of a specific task
    	var j = 0; //counter
    	var m = 0; //counter
    	var n = 0; //counter
		var bindings = taskElement.properties["oryx-bindingsofduties"]; //get saved bindings
    	bindings = bindings.toString();
    	//store saved bindings including task ids
    	var savedBindings = bindings.substring(0,bindings.indexOf(']'));
    	//extract number of entries and element ids of these entries
    	while(bindings.indexOf(',') > -1) {
    		if(j%2 == 1) { //get element ids as integer
    			elementIds[m] = parseInt(bindings.substring((bindings.indexOf('"')) + 1));
    			m++;
		    	bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		} else if(j == 0) { //get total count as integer
    			totalCount = parseInt(bindings.substring((bindings.indexOf(':')) + 1));
    			bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		} else { //get task ids of bindings
    			bindings = bindings.substring((bindings.indexOf('"')) + 1);
    			taskIds[n] = bindings.substring(0,bindings.indexOf('"'));
    			n++;
    			bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		}
	        j++;
    	}
    	if(bindings.indexOf('"') > -1) {
    		taskIds[n] = bindings.substring((bindings.indexOf('"')) + 1);
    	} else {
    		taskIds[n] = bindings; //get last task ids entry
    	}
    	taskIds[n]= taskIds[n].substring(0,taskIds[n].indexOf('"'));
    	
        //check whether entry does already exist
        var boundTasksCopy = selectedTasks; //working copy
        var singleTaskIdsInsertedBinding = []; //task ids of new bindings
        var singleTaskIdsSavedBindings = []; //task ids of saved bindings
		
        var y = 0; //counter
        
		var equal = false; //duplicate existing?
        
        //extract task ids of newly entered binding tasks
    	while(boundTasksCopy.indexOf(';') > -1) {
    		singleTaskIdsInsertedBinding[y] = boundTasksCopy.substring(0, boundTasksCopy.indexOf(';'));
    		boundTasksCopy = boundTasksCopy.substring((boundTasksCopy.indexOf(';'))+2);
	        y++;
        }
    	singleTaskIdsInsertedBinding[y] = boundTasksCopy;
        
        //extract task ids of stored binding tasks
        for(var index = 0; index < taskIds.length; index++) {
        	var z = 0; //counter
			var equalCounter = 0;
			while(taskIds[index].indexOf(';') > -1) {
        		singleTaskIdsSavedBindings[z] = taskIds[index].substring(0, taskIds[index].indexOf(';'));
        		taskIds[index] = taskIds[index].substring((taskIds[index].indexOf(';'))+2);
		        z++;
	        }
        	singleTaskIdsSavedBindings[z] = taskIds[index];
        	z++;
        	
			//compare all collected tasks/ task ids
        	if(singleTaskIdsInsertedBinding.length == singleTaskIdsSavedBindings.length) {
        		for(var x = 0; x < singleTaskIdsSavedBindings.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedBinding.length; w++) {
        				if(singleTaskIdsInsertedBinding[w] == singleTaskIdsSavedBindings[x]) {
        					equal = true;
        					break;
        				}
        				equal = false;
        			}
        			if(equal == false) { //one item of set 1 not found in set 2
    					break;
    				}
        		}
        	} else if(singleTaskIdsInsertedBinding.length > singleTaskIdsSavedBindings.length) {
        		for(var x = 0; x < singleTaskIdsSavedBindings.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedBinding.length; w++) {
						if (singleTaskIdsInsertedBinding[w] == singleTaskIdsSavedBindings[x]) {
							equalCounter++;
							break;
						}
					}
					if(equalCounter == singleTaskIdsSavedBindings.length) { //all saved tasks are found in new entries
						var result = this.deleteBindingEntry(taskElement, (index+1)); //delete existing entry to write new entry later and following updating the entry
						savedBindings = result[0];
						totalCount = result[1];
						equalCounter = 0;
						break;
					}
				}
				equal = false;
        	} else {
        		for(var x = 0; x < singleTaskIdsInsertedBinding.length; x++) {
        			for(var w = 0; w < singleTaskIdsSavedBindings.length; w++) {
        				if(singleTaskIdsSavedBindings[w] == singleTaskIdsInsertedBinding[x]) {
        					equal = true;
        					break;
        				}
        				equal = false;
        			}
        			if(equal == false) { //one item of smaller set not found in bigger set
    					break;
    				}
        		}
        	}
			singleTaskIdsSavedBindings.splice(0,singleTaskIdsSavedBindings.length);
        	//entry does exist, that's why no new one needs to be done
	        if(equal == true) {
	        	break;
	        }
        }
		
    	//get highest element id
    	for(var index = 0; index < elementIds.length; index++) {
    		if(elementIds[index] > highestId) {
    			highestId = elementIds[index];
    		}
    	}
    	returnValue[0] = savedBindings;
    	returnValue[1] = totalCount;
    	returnValue[2] = highestId;
    	returnValue[3] = equal;
    	return returnValue;
    },
    
    separationsCheck: function(taskElement, selectedTasks) { //check for existing separation with same tasks
    	var returnValue;
		//get previous binding of duties assignments
		var boundTaskIds = []; //collection of saved task ids
    	var l = 0; //counter
    	var q = 0; //counter
		var separations = taskElement.properties["oryx-separationofduties"]; //get saved separations
    	separations = separations.toString();
    	//extract number of entries and element ids of these entries
    	while(separations.indexOf(',') > -1) {
    		if(l%2 == 1) { //ignore element ids
		    	separations = separations.substring((separations.indexOf(',')) + 1);
    		} else if(l == 0) { //ignore totalCount
    			separations = separations.substring((separations.indexOf(',')) + 1);
    		} else {
    			separations = separations.substring((separations.indexOf('"')) + 1);
    			boundTaskIds[q] = separations.substring(0,separations.indexOf('"'));
    			q++;
    			separations = separations.substring((separations.indexOf(',')) + 1);
    		}
	        l++;
    	}
    	if(separations.indexOf('"') > -1) {
    		boundTaskIds[q] = separations.substring((separations.indexOf('"')) + 1);
    	} else {
    		boundTaskIds[q] = separations; //never reached?
    	}
    	boundTaskIds[q]= boundTaskIds[q].substring(0,boundTaskIds[q].indexOf('"'));
    	
        //check whether entry does already exist
        var separatedTasksCopy = selectedTasks; //working copy
        var singleTaskIdsInsertedSeparation = []; ////task ids of new separations
        var singleTaskIdsSavedSeparations = []; //task ids of saved separations
        var s = 0; //counter
        var t = 0; //counter
        var duplicate = false; //duplicate existing?
        
        //extract task ids of newly entered separation tasks
    	while(separatedTasksCopy.indexOf(';') > -1) {
    		singleTaskIdsInsertedSeparation[s] = separatedTasksCopy.substring(0, separatedTasksCopy.indexOf(';'));
    		separatedTasksCopy = separatedTasksCopy.substring((separatedTasksCopy.indexOf(';'))+2);
	        s++;
        }
    	singleTaskIdsInsertedSeparation[s] = separatedTasksCopy;
        
        //extract task ids of stored bound tasks
        for(var index = 0; index < boundTaskIds.length; index++) {
        	while(boundTaskIds[index].indexOf(';') > -1) {
        		singleTaskIdsSavedSeparations[t] = boundTaskIds[index].substring(0, boundTaskIds[index].indexOf(';'));
        		boundTaskIds[index] = boundTaskIds[index].substring((boundTaskIds[index].indexOf(';'))+2);
		        t++;
	        }
        	singleTaskIdsSavedSeparations[t] = boundTaskIds[index];
        	
	        var check = 0;
        	if(singleTaskIdsInsertedSeparation.length >= singleTaskIdsSavedSeparations.length) {
        		for(var x = 0; x < singleTaskIdsSavedSeparations.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedSeparation.length; w++) {
        				if(singleTaskIdsInsertedSeparation[w] == singleTaskIdsSavedSeparations[x]) {
        					check++;
        					if(check == 2) {
        						duplicate = true;
        					}
        					break;
        				}
        				duplicate = false;
        			}
        			if(duplicate == true) { //two items of set 1 found in set 2 --> contradicting
    					break;
    				}
        		}
        	} else {
        		for(var x = 0; x < singleTaskIdsInsertedSeparation.length; x++) {
        			for(var w = 0; w < singleTaskIdsSavedSeparations.length; w++) {
        				if(singleTaskIdsSavedSeparations[w] == singleTaskIdsInsertedSeparation[x]) {
        					check++;
        					if(check == 2) {
        						duplicate = true;
        					}
        					break;
        				}
        				duplicate = false;
        			}
        			if(duplicate == true) { //two items of set 1 found in set 2 --> contradicting
    					break;
    				}
        		}
        	}
        	
	        if(duplicate == true) { //entry does exist, delete?
				var bodDelete = confirm("Do you want to delete the existing contradicting separation for allowing this binding constraint to be entered? Otherwise, this binding constraint will be discarded.");
				if(bodDelete == true) {
					this.findSeparationEntry(selectedTasks);
					duplicate = false;
				}
	        	break;
	        }
        }//end check whether separation entry does already exist
    	
        returnValue = duplicate;
        return returnValue;
    },
    
    writeEntry: function(savedBindingsData, selectedTasks, taskElement) { //create JSON object and write to task attributes
    	if(savedBindingsData[1] > 0) { //saved entries do exist
    		savedBindingsData[1]++; //number of entries
    		savedBindingsData[2]++; //highestId needs to be higher than all found ids 
    		savedBindingsData[0] = savedBindingsData[0].substring((savedBindingsData[0].indexOf(':'))+2);
    		savedBindingsData[0] = "{'totalCount':" + savedBindingsData[1] + savedBindingsData[0] + ", {bodId:\"" + savedBindingsData[2] + "\", BoundTasks:\"" + selectedTasks + "\"}]}";
    	} else { //first entry to be done
    		savedBindingsData[1] = 1;
    		savedBindingsData[2]++; //highestId needs to be higher than all found ids
    		savedBindingsData[0] = "{'totalCount':" + savedBindingsData[1] + ", 'items':[{bodId:\"" + savedBindingsData[2] + "\", BoundTasks:\"" + selectedTasks + "\"}]}";
    	}
    	taskElement.setProperty("oryx-bindingsofduties", savedBindingsData[0]);
    },
	
	deleteBindingEntry: function(taskElement, elementId) { //delete given entry from given task from binding contraints
		var returnValue = [];
		var bindings = taskElement.properties["oryx-bindingsofduties"]; //get saved separations
		var keptBindings;
		var temp;
		var totalCount = parseInt(bindings.substring((bindings.indexOf(':')) + 1));
		totalCount = totalCount - 1;
		if(totalCount > 0)  {
			bindings = bindings.substring(bindings.indexOf('[')); //{'totalCount':3, 'items':
			keptBindings = bindings.substring(0,bindings.indexOf('{')); //[
			bindings = bindings.substring(bindings.indexOf('{')); //[
			temp = bindings.substring(0,(bindings.indexOf('"')) + 1); //{bodId:"
			bindings = bindings.substring((bindings.indexOf('"')) + 1); //{bodId:"
			while(parseInt(bindings) != elementId) {
				keptBindings = keptBindings + temp + bindings.substring(0,(bindings.indexOf('{')) - 2); //'id'", BoundTasks:"task_a; task_b"}
				bindings = bindings.substring((bindings.indexOf('{')) - 2); //'id'", BoundTasks:"task_a; task_b"}
				temp = bindings.substring(0,((bindings.indexOf('"')) + 1)); //, {bodId:"
				bindings = bindings.substring((bindings.indexOf('"')) + 1); //, {bodId:"
			}
			bindings = bindings.substring((bindings.indexOf('}'))+3); //'bodId'", BoundTasks:"task_c; task_d; task_e"} or //{bodId:"'id'", BoundTasks:"task_c; task_d; task_e"},_
			var entry = "{'totalCount':" + totalCount + ", 'items':" + keptBindings + bindings + "]}";
			taskElement.setProperty("oryx-bindingsofduties", entry);
			
			entry = entry.toString();
			entry = entry.substring(0,entry.indexOf(']'));
		
		} else {
			var entry = "";
			taskElement.setProperty("oryx-bindingsofduties", entry);
		}
		
		returnValue[0] = entry;
		returnValue[1] = totalCount;
		return returnValue;
	},
	
	findSeparationEntry: function(taskIds) { //find separation entry with given task ids to be deleted at all affected tasks
		var taskIdsCopy = taskIds;
		var singleTaskIds = [];
		var s = 0;
		
		//extract task ids of affected tasks
    	while(taskIdsCopy.indexOf(';') > -1) {
    		singleTaskIds[s] = taskIdsCopy.substring(0, taskIdsCopy.indexOf(';'));
    		taskIdsCopy = taskIdsCopy.substring((taskIdsCopy.indexOf(';'))+2);
	        s++;
        }
    	singleTaskIds[s] = taskIdsCopy;
		
		taskIdsCopy = taskIds + ";";
		for (var m = 0; m < singleTaskIds.length; m++) { //go through all affected tasks
			var currentTask = this.getTaskById(singleTaskIds[m]);
			var separations = currentTask.properties["oryx-separationofduties"]; //get saved separations
			var separatedTaskIds = [];
			var totalCount = 0;
			var i = 0; //counter
			var j = 0; //counter
			while(separations.indexOf(',') > -1) {
	    		if(i%2 == 1) { //get element ids as integer
			    	separations = separations.substring((separations.indexOf(',')) + 1);
	    		} else if(i == 0) { //get totalCount
	    			totalCount = parseInt(separations.substring((separations.indexOf(':')) + 1));
					separations = separations.substring((separations.indexOf(',')) + 1);
	    		} else { //get task ids
	    			separations = separations.substring((separations.indexOf('"')) + 1);
	    			separatedTaskIds[j] = separations.substring(0,separations.indexOf('"'));
	    			j++;
	    			separations = separations.substring((separations.indexOf(',')) + 1);
	    		}
		        i++;
	    	}
			//transform id string into array - one constraint set equals one array entry
	    	if(separations.indexOf('"') > -1) {
	    		separatedTaskIds[j] = separations.substring((separations.indexOf('"')) + 1);
	    	} else {
	    		separatedTaskIds[j] = separations; //never reached??
	    	}
	    	separatedTaskIds[j]= separatedTaskIds[j].substring(0,separatedTaskIds[j].indexOf('"'));
						
			var singleTaskIdsStoredSeparation = [];
			
			//check each separation entry
			for (var index = 0; index < separatedTaskIds.length; index++) {
				var separatedTasksCopy = separatedTaskIds[index];
				var t = 0; //counter
				//extract task ids of stored elements
				while (separatedTasksCopy.indexOf(';') > -1) {
					singleTaskIdsStoredSeparation[t] = separatedTasksCopy.substring(0, separatedTasksCopy.indexOf(';'));
					separatedTasksCopy = separatedTasksCopy.substring((separatedTasksCopy.indexOf(';')) + 2);
					t++;
				}
				singleTaskIdsStoredSeparation[t] = separatedTasksCopy;
				
				if (singleTaskIds.length >= singleTaskIdsStoredSeparation.length) {
					var equalCounter = 0;
					for (var x = 0; x < singleTaskIdsStoredSeparation.length; x++) {
						for (var y = 0; y < singleTaskIds.length; y++) {
							if (singleTaskIdsStoredSeparation[x] == singleTaskIds[y]) {
								equalCounter++;
								break;
							}
						}
						if (equalCounter == singleTaskIdsStoredSeparation.length) { //entry found
							this.deleteSeparationEntry(currentTask, (index+1)); //delete entry
							break;
						}
					}
				}
				singleTaskIdsStoredSeparation.splice(0,singleTaskIdsStoredSeparation.length);
			}
		}
	},
	
	deleteSeparationEntry: function(taskElement, elementId) { //delete defined entry in given task
		var separations = taskElement.properties["oryx-separationofduties"]; //get saved separations
		var keptSeparations;
		var temp;
		var totalCount = parseInt(separations.substring((separations.indexOf(':')) + 1));
		totalCount = totalCount - 1;
		if(totalCount > 0)  { //remove specified entry
			separations = separations.substring(separations.indexOf('[')); //{'totalCount':3, 'items':
			keptSeparations = separations.substring(0,separations.indexOf('{')); //[
			separations = separations.substring(separations.indexOf('{')); //[
			temp = separations.substring(0,(separations.indexOf('"')) + 1); //{sodId:"
			separations = separations.substring((separations.indexOf('"')) + 1); //{sodId:"
			while(parseInt(separations) != elementId) {
				keptSeparations = keptSeparations + temp + separations.substring(0,(separations.indexOf('{')) - 2); //'id'", SeparatedTasks:"task_a; task_b"}
				separations = separations.substring((separations.indexOf('{')) - 2); //'id'", SeparatedTasks:"task_a; task_b"}
				temp = separations.substring(0,((separations.indexOf('"')) + 1)); //, {sodId:"
				separations = separations.substring((separations.indexOf('"')) + 1); //, {sodId:"
			}
			separations = separations.substring((separations.indexOf('}'))+3); //'sodId'", SeparatedTasks:"task_c; task_d; task_e"} or //{sodId:"'id'", SeparatedTasks:"task_c; task_d; task_e"},_
			var entry = "{'totalCount':" + totalCount + ", 'items':" + keptSeparations + separations + "]}";
			taskElement.setProperty("oryx-separationofduties", entry);
			
			entry = entry.toString();
			entry = entry.substring(0,entry.indexOf(']'));
		
		} else { //remove last entry 
			var entry = "";
			taskElement.setProperty("oryx-separationofduties", entry);
		}
	},
	
	getTaskById: function(taskId){
		var shapes = this.facade.getCanvas().getChildShapes(true);
		var task;
		for (var index = 0; index < shapes.length; index++) {
    		if (shapes[index].properties["oryx-activitytype"] == "Task") {
				if (shapes[index].properties["oryx-id"] == taskId) {
					task = shapes[index];
					break;
				}
			}
    	}
		return task;
	}
});