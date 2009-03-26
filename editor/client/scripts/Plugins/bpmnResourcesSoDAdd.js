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

ORYX.Plugins.ResourcesSoDAdd = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.ResourcesSoDAdd.name,
            'functionality': this.defineSoD.bind(this),
            'group': ORYX.I18N.ResourcesSoDAdd.group,
            'dropDownGroupIcon': ORYX.PATH + "images/sod.png",
            'icon': ORYX.PATH + "images/sod+.png",
            'description': ORYX.I18N.ResourcesSoDAdd.desc,
            'index': 1,
            'toggle': false,
            'minShape': 2,
            'maxShape': 0
        });
		
    },
    
    defineSoD: function(){
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
    			if(i == 0) { //get ids of tasks to be separated
					selectedTasks = taskElements[i].properties["oryx-id"];
    			} else {
    				selectedTasks = selectedTasks + "; " + taskElements[i].properties["oryx-id"];
    			}
    			i++;
    		}
    	}
		
    	if(taskElements.length > 1) {
			for (var taskCounter = 0; taskCounter < taskElements.length; taskCounter++) {
    		//variable declaration
        	var savedSeparationsData = []; //already saved separations
        	var savedBindingsData = false; //already saved bindings
        	
        	//check whether other separations are defined for current task
        	if(taskElements[taskCounter].properties["oryx-separationofduties"] != "") {
        		savedSeparationsData = this.separationsCheck(taskElements[taskCounter], selectedTasks);
        		if (savedSeparationsData[3] == true) {
	        		break;
	        	}
        	} else {
				savedSeparationsData[1] = 0; //total number of separation entries is 0 as field is empty
				savedSeparationsData[2] = 0; //highest id used is 0 as no id is used
			}
        	
        	//check whether a contradicting binding is defined for current task
        	if(taskElements[taskCounter].properties["oryx-bindingsofduties"] != ("")) {
        		savedBindingsData = this.bindingsCheck(taskElements[taskCounter], selectedTasks);
	        	if (savedBindingsData == true) {
	        		break;
	        	}
        	}
        	
        	//write entry
	    	this.writeEntry(savedSeparationsData, selectedTasks, taskElements[taskCounter]);
    		}
		} else {
			alert("Please select at least two tasks to define constraints for.");
		}
    },
    
    separationsCheck: function(taskElement, selectedTasks) { //check for existing separations with same tasks
		var returnValue = [];
		//get previous separation of duties assignments
		var elementIds = []; //collection of used ids for saved separations
		var taskIds = []; //collection of saved task ids
		var totalCount = 0; //number of defined separations for a specific task
    	var highestId = 1; //next element id for separations of a specific task
    	var j = 0; //counter
    	var m = 0; //counter
    	var n = 0; //counter
		var separations = taskElement.properties["oryx-separationofduties"]; //get saved separations
    	separations = separations.toString();
    	//store saved separations including task ids
    	var savedSeparations = separations.substring(0,separations.indexOf(']'));
    	//extract number of entries and element ids of these entries
    	while(separations.indexOf(',') > -1) {
    		if(j%2 == 1) { //get element ids as integer
    			elementIds[m] = parseInt(separations.substring((separations.indexOf('"')) + 1));
    			m++;
		    	separations = separations.substring((separations.indexOf(',')) + 1);
    		} else if(j == 0) { //get total count as integer
    			totalCount = parseInt(separations.substring((separations.indexOf(':')) + 1));
    			separations = separations.substring((separations.indexOf(',')) + 1);
    		} else { //get task ids of separations
    			separations = separations.substring((separations.indexOf('"')) + 1);
    			taskIds[n] = separations.substring(0,separations.indexOf('"'));
    			n++;
    			separations = separations.substring((separations.indexOf(',')) + 1);
    		}
	        j++;
    	}
    	if(separations.indexOf('"') > -1) {
    		taskIds[n] = separations.substring((separations.indexOf('"')) + 1);
    	} else {
    		taskIds[n] = separations; //get last task ids entry
    	}
    	taskIds[n]= taskIds[n].substring(0,taskIds[n].indexOf('"'));
    	
        //check whether entry does already exist
        var separatedTasksCopy = selectedTasks; //working copy
        var singleTaskIdsInsertedSeparation = []; //task ids of new separations
        var singleTaskIdsSavedSeparations = []; //task ids of saved separations
		
        var y = 0; //counter
        
		var equal = false; //duplicate existing?
        
        //extract task ids of newly entered separation tasks
    	while(separatedTasksCopy.indexOf(';') > -1) {
    		singleTaskIdsInsertedSeparation[y] = separatedTasksCopy.substring(0, separatedTasksCopy.indexOf(';'));
    		separatedTasksCopy = separatedTasksCopy.substring((separatedTasksCopy.indexOf(';'))+2);
	        y++;
        }
    	singleTaskIdsInsertedSeparation[y] = separatedTasksCopy;
        
        //extract task ids of stored separation tasks
        for(var index = 0; index < taskIds.length; index++) {
        	var z = 0; //counter
			var equalCounter = 0;
			while(taskIds[index].indexOf(';') > -1) {
        		singleTaskIdsSavedSeparations[z] = taskIds[index].substring(0, taskIds[index].indexOf(';'));
        		taskIds[index] = taskIds[index].substring((taskIds[index].indexOf(';'))+2);
		        z++;
	        }
        	singleTaskIdsSavedSeparations[z] = taskIds[index];
        	z++;
        	
			//compare all collected tasks/ task ids
        	if(singleTaskIdsInsertedSeparation.length == singleTaskIdsSavedSeparations.length) {
        		for(var x = 0; x < singleTaskIdsSavedSeparations.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedSeparation.length; w++) {
        				if(singleTaskIdsInsertedSeparation[w] == singleTaskIdsSavedSeparations[x]) {
        					equal = true;
        					break;
        				}
        				equal = false;
        			}
        			if(equal == false) { //one item of set 1 not found in set 2
    					break;
    				}
        		}
        	} else if(singleTaskIdsInsertedSeparation.length > singleTaskIdsSavedSeparations.length) {
        		for(var x = 0; x < singleTaskIdsSavedSeparations.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedSeparation.length; w++) {
						if (singleTaskIdsInsertedSeparation[w] == singleTaskIdsSavedSeparations[x]) {
							equalCounter++;
							break;
						}
					}
					if(equalCounter == singleTaskIdsSavedSeparations.length) { //all saved tasks are found in new entries
						var result = this.deleteSeparationEntry(taskElement, (index+1)); //delete existing entry to write new entry later and following updating the entry
						savedSeparations = result[0];
						totalCount = result[1];
						equalCounter = 0;
						break;
					}
				}
				equal = false;
        	} else {
        		for(var x = 0; x < singleTaskIdsInsertedSeparation.length; x++) {
        			for(var w = 0; w < singleTaskIdsSavedSeparations.length; w++) {
        				if(singleTaskIdsSavedSeparations[w] == singleTaskIdsInsertedSeparation[x]) {
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
			singleTaskIdsSavedSeparations.splice(0,singleTaskIdsSavedSeparations.length);
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
    	returnValue[0] = savedSeparations;
    	returnValue[1] = totalCount;
    	returnValue[2] = highestId;
    	returnValue[3] = equal;
    	return returnValue;
    },
    
    bindingsCheck: function(taskElement, selectedTasks) { //check for existing bindings with same tasks
    	var returnValue;
		//get previous binding of duties assignments
		var boundTaskIds = []; //collection of saved task ids
    	var l = 0; //counter
    	var q = 0; //counter
		var bindings = taskElement.properties["oryx-bindingsofduties"]; //get saved bindings
    	bindings = bindings.toString();
    	//extract number of entries and element ids of these entries
    	while(bindings.indexOf(',') > -1) {
    		if(l%2 == 1) { //ignore element ids
		    	bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		} else if(l == 0) { //ignore totalCount
    			bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		} else {
    			bindings = bindings.substring((bindings.indexOf('"')) + 1);
    			boundTaskIds[q] = bindings.substring(0,bindings.indexOf('"'));
    			q++;
    			bindings = bindings.substring((bindings.indexOf(',')) + 1);
    		}
	        l++;
    	}
    	if(bindings.indexOf('"') > -1) {
    		boundTaskIds[q] = bindings.substring((bindings.indexOf('"')) + 1);
    	} else {
    		boundTaskIds[q] = bindings; //never reached?
    	}
    	boundTaskIds[q]= boundTaskIds[q].substring(0,boundTaskIds[q].indexOf('"'));
    	
        //check whether entry does already exist
        var boundTasksCopy = selectedTasks; //working copy
        var singleTaskIdsInsertedBinding = []; ////task ids of new bindings
        var singleTaskIdsSavedBinding = []; //task ids of saved bindings
        var s = 0; //counter
        var t = 0; //counter
        var duplicate = false; //duplicate existing?
        
        //extract task ids of newly entered separation tasks
    	while(boundTasksCopy.indexOf(';') > -1) {
    		singleTaskIdsInsertedBinding[s] = boundTasksCopy.substring(0, boundTasksCopy.indexOf(';'));
    		boundTasksCopy = boundTasksCopy.substring((boundTasksCopy.indexOf(';'))+2);
	        s++;
        }
    	singleTaskIdsInsertedBinding[s] = boundTasksCopy;
        
        //extract task ids of stored bound tasks
        for(var index = 0; index < boundTaskIds.length; index++) {
        	while(boundTaskIds[index].indexOf(';') > -1) {
        		singleTaskIdsSavedBinding[t] = boundTaskIds[index].substring(0, boundTaskIds[index].indexOf(';'));
        		boundTaskIds[index] = boundTaskIds[index].substring((boundTaskIds[index].indexOf(';'))+2);
		        t++;
	        }
        	singleTaskIdsSavedBinding[t] = boundTaskIds[index];
        	
	        var check = 0;
        	if(singleTaskIdsInsertedBinding.length >= singleTaskIdsSavedBinding.length) {
        		for(var x = 0; x < singleTaskIdsSavedBinding.length; x++) {
        			for(var w = 0; w < singleTaskIdsInsertedBinding.length; w++) {
        				if(singleTaskIdsInsertedBinding[w] == singleTaskIdsSavedBinding[x]) {
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
        		for(var x = 0; x < singleTaskIdsInsertedBinding.length; x++) {
        			for(var w = 0; w < singleTaskIdsSavedBinding.length; w++) {
        				if(singleTaskIdsSavedBinding[w] == singleTaskIdsInsertedBinding[x]) {
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
				var bodDelete = confirm("Do you want to delete the existing contradicting binding for allowing this separation constraint to be entered? Otherwise, this separation constraint will be discarded.");
				if(bodDelete == true) {
					this.findBindingEntry(selectedTasks);
					duplicate = false;
				}
	        	break;
	        }
        }//end check whether binding entry does already exist
    	
        returnValue = duplicate;
        return returnValue;
    },
    
    writeEntry: function(savedSeparationsData, selectedTasks, taskElement) { //create JSON object and write to task attributes
    	if(savedSeparationsData[1] > 0) { //saved entries do exist
    		savedSeparationsData[1]++; //number of entries
    		savedSeparationsData[2]++; //highestId needs to be higher than all found ids 
    		savedSeparationsData[0] = savedSeparationsData[0].substring((savedSeparationsData[0].indexOf(':'))+2);
    		savedSeparationsData[0] = "{'totalCount':" + savedSeparationsData[1] + savedSeparationsData[0] + ", {sodId:\"" + savedSeparationsData[2] + "\", SeparatedTasks:\"" + selectedTasks + "\"}]}";
    	} else { //first entry to be done
    		savedSeparationsData[1] = 1;
    		savedSeparationsData[2]++; //highestId needs to be higher than all found ids
    		savedSeparationsData[0] = "{'totalCount':" + savedSeparationsData[1] + ", 'items':[{sodId:\"" + savedSeparationsData[2] + "\", SeparatedTasks:\"" + selectedTasks + "\"}]}";
    	}
    	taskElement.setProperty("oryx-separationofduties", savedSeparationsData[0]);
    },
	
	deleteSeparationEntry: function(taskElement, elementId) { //delete given entry from given task from separation contraints
		var returnValue = [];
		var separations = taskElement.properties["oryx-separationofduties"]; //get saved separations
		var keptSeparations;
		var temp;
		var totalCount = parseInt(separations.substring((separations.indexOf(':')) + 1));
		totalCount = totalCount - 1;
		if(totalCount > 0)  {
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
		
		} else {
			var entry = "";
			taskElement.setProperty("oryx-separationofduties", entry);
		}
		
		returnValue[0] = entry;
		returnValue[1] = totalCount;
		return returnValue;
	},
	
	findBindingEntry: function(taskIds) { //find binding entry with given task ids to be deleted at all affected tasks
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
			var bindings = currentTask.properties["oryx-bindingsofduties"]; //get saved bindings
			var boundTaskIds = [];
			var totalCount = 0;
			var i = 0; //counter
			var j = 0; //counter
			while(bindings.indexOf(',') > -1) {
	    		if(i%2 == 1) { //get element ids as integer
			    	bindings = bindings.substring((bindings.indexOf(',')) + 1);
	    		} else if(i == 0) { //get totalCount
	    			totalCount = parseInt(bindings.substring((bindings.indexOf(':')) + 1));
					bindings = bindings.substring((bindings.indexOf(',')) + 1);
	    		} else { //get task ids
	    			bindings = bindings.substring((bindings.indexOf('"')) + 1);
	    			boundTaskIds[j] = bindings.substring(0,bindings.indexOf('"'));
	    			j++;
	    			bindings = bindings.substring((bindings.indexOf(',')) + 1);
	    		}
		        i++;
	    	}
			//transform id string into array - one constraint set equals one array entry
	    	if(bindings.indexOf('"') > -1) {
	    		boundTaskIds[j] = bindings.substring((bindings.indexOf('"')) + 1);
	    	} else {
	    		boundTaskIds[j] = bindings; //never reached??
	    	}
	    	boundTaskIds[j]= boundTaskIds[j].substring(0,boundTaskIds[j].indexOf('"'));
						
			var singleTaskIdsStoredBinding = [];
			
			//check each binding entry
			for (var index = 0; index < boundTaskIds.length; index++) {
				var boundTasksCopy = boundTaskIds[index];
				var t = 0; //counter
				//extract task ids of stored elements
				while (boundTasksCopy.indexOf(';') > -1) {
					singleTaskIdsStoredBinding[t] = boundTasksCopy.substring(0, boundTasksCopy.indexOf(';'));
					boundTasksCopy = boundTasksCopy.substring((boundTasksCopy.indexOf(';')) + 2);
					t++;
				}
				singleTaskIdsStoredBinding[t] = boundTasksCopy;
				
				if (singleTaskIds.length >= singleTaskIdsStoredBinding.length) {
					var equalCounter = 0;
					for (var x = 0; x < singleTaskIdsStoredBinding.length; x++) {
						for (var y = 0; y < singleTaskIds.length; y++) {
							if (singleTaskIdsStoredBinding[x] == singleTaskIds[y]) {
								equalCounter++;
								break;
							}
						}
						if (equalCounter == singleTaskIdsStoredBinding.length) { //entry found
							this.deleteBindingEntry(currentTask, (index+1)); //delete entry
							break;
						}
					}
				}
				singleTaskIdsStoredBinding.splice(0,singleTaskIdsStoredBinding.length);
			}
		}
	},
	
	deleteBindingEntry: function(taskElement, elementId) { //delete defined entry in given task
		var bindings = taskElement.properties["oryx-bindingsofduties"]; //get saved bindings
		var keptBindings;
		var temp;
		var totalCount = parseInt(bindings.substring((bindings.indexOf(':')) + 1));
		totalCount = totalCount - 1;
		if(totalCount > 0)  { //remove specified entry
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
		
		} else { //remove last entry
			var entry = "";
			taskElement.setProperty("oryx-bindingsofduties", entry);
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