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

  function scriptHint(editor, keywords, getToken) {
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
    return {list: getCompletions(token, context, keywords),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
  }

  CodeMirror.jbpmHint = function(editor) {
    return scriptHint(editor, kcontextMethods,
                      function (e, cur) {return e.getTokenAt(cur);});
  }

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
    }
    return found;
  }
})();
