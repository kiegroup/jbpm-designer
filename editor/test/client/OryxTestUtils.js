// Definition of Clazz
	
var Clazz = function() {};
Clazz.prototype.construct = function() {};
Clazz.extend = function(def) {
	var classDef = function() {
		if (arguments[0] !== Clazz) { this.construct.apply(this, arguments); }
	};

	var proto = new this(Clazz);
	var superClass = this.prototype;

	for (var n in def) {
		var item = def[n];                        
		if (item instanceof Function) item.$ = superClass;
		proto[n] = item;
	}

	classDef.prototype = proto;

	//Give this new class the same static extend method    
	classDef.extend = this.extend;        
	return classDef;
};