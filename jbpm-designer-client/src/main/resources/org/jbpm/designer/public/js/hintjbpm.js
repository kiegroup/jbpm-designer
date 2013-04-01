(function () {
  function forEach(arr, f) {
    for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
  }
  
  function arrayContains(arr, item) {
    if (!Array.prototype.indexOf) {
      var i = arr.length;
      while (i--) {
        if (arr[i] === item) {
          return true;
        }
      }
      return false;
    }
    return arr.indexOf(item) != -1;
  }

  function scriptHint(editor, keywords, hintType, getToken) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = getToken(editor, cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
		if (!/^[\w$_]*$/.test(token.string)) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: token.string == "." ? "property" : null};
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "property") {
      tprop = getToken(editor, {line: cur.line, ch: tprop.start});
      if (tprop.string != ".") return;
      tprop = getToken(editor, {line: cur.line, ch: tprop.start});
      if (tprop.string == ')') {
        var level = 1;
        do {
          tprop = getToken(editor, {line: cur.line, ch: tprop.start});
          switch (tprop.string) {
          case ')': level++; break;
          case '(': level--; break;
          default: break;
          }
        } while (level > 0)
        tprop = getToken(editor, {line: cur.line, ch: tprop.start});
				if (tprop.className == 'variable')
					tprop.className = 'function';
				else return; // no clue
      }
      if (!context) var context = [];
      context.push(tprop);
    }
    if(hintType && hintType == "form") {
    	return {list: getCompletionsForForms(token, context, keywords),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
    } else {
    	return {list: getCompletions(token, context, keywords),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
    }
  }

  CodeMirror.jbpmHint = function(editor) {
    return scriptHint(editor, kcontextMethods, "script",
                      function (e, cur) {return e.getTokenAt(cur);});
  };
  
  CodeMirror.formsHint = function(editor) {
	    return scriptHint(editor, [], "form",
	                      function (e, cur) {return e.getTokenAt(cur);});
  };

  var kcontextMethods = ("getProcessInstance() getNodeInstance() getVariable(variableName) setVariable(variableName,value) getKnowledgeRuntime()").split(" ");
  var genericProps = ("return kcontext").split(" ");
  
  function getCompletions(token, context, keywords) {
    var found = [], start = token.string;
    
    function maybeAdd(str) {
        if (str.indexOf(start) == 0 && !arrayContains(found, str)) {
        	if(str.indexOf(":") > 0) {
        		var valueParts = str.split(":");
        		found.push(valueParts[0]);
        	} else {
        		found.push(str);
        	}
        }
    }
    
    if(context) {
    	var contextVal = context.pop().string;
    	if(contextVal == "kcontext") {
    		forEach(kcontextMethods, maybeAdd);
    	}
    } else {
    	  forEach(genericProps, maybeAdd);
    	  var processJSON = ORYX.EDITOR.getSerializedJSON();
    	  var processVars = jsonPath(processJSON.evalJSON(), "$.properties.vardefs");
    	  if(processVars) {
    		  if(processVars.toString().length > 0)
    			  forEach(processVars.toString().split(","), maybeAdd);
    	  }
    	  var processGlobals = jsonPath(processJSON.evalJSON(), "$.properties.globals");
    	  if(processGlobals) {
    		  if(processGlobals.toString().length > 0)
    			  forEach(processGlobals.toString().split(","), maybeAdd);
    	  }
          var processdataobjectstr = "";
          var childShapes = jsonPath(processJSON.evalJSON(), "$.childShapes.*");
          for(var i = 0; i < childShapes.length;i++) {
              if(childShapes[i].stencil.id == 'DataObject') {
                  processdataobjectstr += childShapes[i].properties.name;
                  processdataobjectstr += ",";
              }
          }
          if (processdataobjectstr.endsWith(",")) {
              processdataobjectstr = processdataobjectstr.substr(0, processdataobjectstr.length - 1);
          }
          forEach(processdataobjectstr.toString().split(","), maybeAdd);
    }
    return found;
  }
  
  function getCompletionsForForms(token, context, keywords) {
	    var found = [], start = token.string;
	    
	    function maybeAdd(str) {
	        if (str.indexOf(start) == 0 && !arrayContains(found, str)) {
	        	if(str.indexOf(":") > 0) {
	        		var valueParts = str.split(":");
	        		found.push(valueParts[0]);
	        	} else {
	        		found.push(str);
	        	}
	        }
	    }
	    
	    // check if task is selected
	    var selection = ORYX.EDITOR._pluginFacade.getSelection();
	    if(selection && selection.length == 1) {
			var shape = selection.first();
			var shapeid = shape.resourceId;
			var processJSON = ORYX.EDITOR.getSerializedJSON();
			
			var childshapes = jsonPath(processJSON.evalJSON(), "$.childShapes.*");
			for(var i=0;i<childshapes.length;i++){
		        var csobj = childshapes[i];
		        if(csobj.resourceId == shapeid) {
		        	var datainputs = csobj.properties.datainputset;
		        	var dataoutputs = csobj.properties.dataoutputset;
		        	var datainParts = datainputs.split(",");
		        	for(var j=0; j < datainParts.length; j++) {
		        		var nextPart = datainParts[j];
		        		if(nextPart.indexOf(":") > 0) {
		        			var innerParts = nextPart.split(":");
		        			maybeAdd('${'+innerParts[0]+'}');
		        		} else {
		        			maybeAdd('${'+nextPart+'}');
		        		}
		        	}
		        	var dataoutParts = dataoutputs.split(",");
		        	for(var k=0; k < dataoutParts.length; k++) {
		        		var nextPart = dataoutParts[k];
		        		if(nextPart.indexOf(":") > 0) {
		        			var innerParts = nextPart.split(":");
		        			maybeAdd(innerParts[0]);
		        		} else {
		        			maybeAdd(nextPart);
		        		}
		        	}
		        }
			}
		} else {
			var processJSON = ORYX.EDITOR.getSerializedJSON();
	    	  var processVars = jsonPath(processJSON.evalJSON(), "$.properties.vardefs");
	    	  if(processVars) {
	    		  if(processVars.toString().length > 0)
	    			  forEach(processVars.toString().split(","), maybeAdd);
	    	  }
	    	  var processGlobals = jsonPath(processJSON.evalJSON(), "$.properties.globals");
	    	  if(processGlobals) {
	    		  if(processGlobals.toString().length > 0)
	    			  forEach(processGlobals.toString().split(","), maybeAdd);
	    	  }
              var processdataobjectstr = "";
              var childShapes = jsonPath(processJSON.evalJSON(), "$.childShapes.*");
              for(var i = 0; i < childShapes.length;i++) {
                  if(childShapes[i].stencil.id == 'DataObject') {
                      processdataobjectstr += childShapes[i].properties.name;
                      processdataobjectstr += ",";
                  }
              }
              if (processdataobjectstr.endsWith(",")) {
                  processdataobjectstr = processdataobjectstr.substr(0, processdataobjectstr.length - 1);
              }
              forEach(processdataobjectstr.toString().split(","), maybeAdd);
		}
	    return found;
	  }
})();
