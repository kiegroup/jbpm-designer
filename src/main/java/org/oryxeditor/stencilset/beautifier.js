importClass(java.lang.System);

var KEY_DELIMITER = '';

function beautify(json){

    return __beautify(json, 0);
}

/**
 * Returns the number of slots for an object.
 * @param {Object} obj the object to inspect.
 */
function slot_count(obj){

    var items = 0;
    for (i in obj) {
        items++;
    }
    return items;
}

/**
 * Returns the name of the first slot of an object.
 * @param {Object} obj the object to inspect.
 */
function first_slot(obj){
    for (i in obj) {
        return i;
    }
}


/**
 * Determines whether an object is primitive and may be represented by a one-liner.
 * @param {Object} obj the object to inspect.
 * @param {Object} tolerant whether primitive subarrays are allowed. defaults to true.
 */
function is_primitive(obj, tolerant){
    return __is_primitive(obj, !(tolerant === false))
    
}

function __is_primitive(obj, tolerant){

    // inspect all slots.
    var items = 0;
    for (i in obj) {
    
        // count the items.
        items++;
        
        var value = obj[i];
        var type = typeof value;
        
        // if we're tolerant and this is a short array, it needs to be primitive itself
        if (tolerant && (value instanceof Array) && (value.length < 3)) {
        
            if (!is_primitive(value)) {
                return false;
            }
        }
        else 
        
            // if this is a function or an object, it may need more lines.
            if ((type == 'function') || (type == 'object')) {
            
                return false;
            }
    }
    
    // if it is still considered primitive, limit slot count.
    return items < 8;
}

function generate_indent(number){

    var result = '';
    for (i = 0; i < number; i++) {
        result += '  ';
    }
    return result;
}

function __beautify(json, indentation){

    // create indentations and increment next.
    var indent = generate_indent(indentation), next_indent = generate_indent(++indentation);
    
    // json knows only null values, no undefines.
    if ((json === undefined) || (json === null)) {
        return "null";
    }
    
    if (json instanceof Array) {
    
        if (json.length == 0) {
            return '[]';
        }
        
        if (json.length == 1) {
            return '[' + __beautify(json[0], indentation) + ']';
        }
        
        System.out.println("Array too long! Items: " + json.length);
    }
    
    
    switch (typeof json) {
    
        case "number":
            return json;
            
        case "string":
            return '"' + json + '"';
            
        case "boolean":
            return json ? "true" : "false";
            
        case "object":
            
            var slots = slot_count(json);
            
            if (slots == 0) {
                return '{}';
            }
            
            if (slots == 1) {
                var key = first_slot(json);
                var value = json[key];
                return '{ ' + KEY_DELIMITER + key + KEY_DELIMITER + ': ' + __beautify(value) + ' }';
            }
            
            var primitive = is_primitive(json, true);
            var delimiter = primitive ? ' ' : '\n';
            var result = primitive ? '{ ' : ('{' + delimiter)
            var current_slot = 0;
            
            for (o in json) {
                var comma = (++current_slot == slots) ? '' : ',';
                if (!primitive) {
                    result += next_indent;
                }
                result += KEY_DELIMITER + o + KEY_DELIMITER + ': ';
                result += __beautify(json[o], primitive ? 0 : indentation);
                result += comma + delimiter;
            }
            result += primitive ? '}' : (indent + '}');
            return result;
            
        default:
            System.out.println("No case for " + (typeof json) + json);
    }
    
    return "Oioioi. Something went wrong.";
}
