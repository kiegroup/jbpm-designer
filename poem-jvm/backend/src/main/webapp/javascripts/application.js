// Place your application-specific JavaScript functions and classes here
// This file is automatically included by javascript_include_tag :defaults

function die(msg) {
	if (msg) {
		if (Object.isString(msg)) {
			alert(msg);
		}
		else {
			alert(typeof(msg) + msg)
		}
	}
	throw "script stopped via die()"
}


/** 
 * prototype.js convenience functions reimplementation 
 */
if (!Array.prototype.each) {
	$break = "each_iteration_break";
	
	Array.prototype.each = function(callback) {
		try {
			for (var i = 0, length = this.length; i < length; i++) {
				callback(this[i]);
			}
		}
		catch (error) {
			if (error != $break) {
				throw error;
			}
		}
		return this;
	}
}


if (!String.prototype.strip) {
	String.prototype.strip = function() {
	    return this.replace(/^\s+/, '').replace(/\s+$/, '');
	}
}

if (!Object.isArray) {
	Object.isArray = function(object) {
		return object instanceof Array;
	}
}

// string is tricky, since typeof(new String) is object and "" instanceof string evaluates to false
if (!Object.isString) {
	Object.isString = function(object) {
		return typeof object == "string";
	}
}

if (!Object.isEmpty) {
	Object.isEmpty = function(object){
		switch (typeof(object)) {
			// objects are considered empty if they do not contain any elements but functions
			case "object":
				for (var i in object) {
					if (typeof(object[i]) != "undefined" && typeof(object[i]) != "function") {
						return false;
					}
				}
				return true;
			// strings are considered empty if they are empty ;), strings with spaces are not empty
			case "string":
				return object.length == 0
		}
		
		return false;
	}
}

function $(element) {
	if (arguments.length > 1) {
		for (var i = 0, elements = [], length = arguments.length; i < length; i++) {
			elements.push($(arguments[i]));
		}
		return elements;
	}

	if (Object.isString(element)) {
		element = document.getElementById(element);
	}
	
	return element;
}
