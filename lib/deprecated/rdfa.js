/**
 *	RDF/A in Javascript
 *	Ben Adida - ben@mit.edu
 *  Nathan Yergler - nathan@creativecommons.org
 *
 *	licensed under GPL v2
 */

// EXPECTING __RDFA_BASE
if (typeof(__RDFA_BASE) == 'undefined')
  __RDFA_BASE = 'http://www.w3.org/2001/sw/BestPractices/HTML/rdfa-bookmarklet/';

if (typeof(__RDFA_VERSION_SUBDIR) == 'undefined')
  __RDFA_VERSION_SUBDIR = '2006-05-22/';

// setup the basic
if (!RDFA) {
    RDFA = new Object();
}

// internal data structures
RDFA.triples = new Array();
RDFA.bnode_counter = 0;

//
// dummy callbacks in case they're not defined
//
if (!RDFA.CALLBACK_NEW_TRIPLE_WITH_URI_OBJECT)
    RDFA.CALLBACK_NEW_TRIPLE_WITH_URI_OBJECT = function(foo,bar) {};

if (!RDFA.CALLBACK_NEW_TRIPLE_WITH_LITERAL_OBJECT)
    RDFA.CALLBACK_NEW_TRIPLE_WITH_LITERAL_OBJECT = function(foo,bar) {};

if (!RDFA.CALLBACK_NEW_TRIPLE_WITH_SUBJECT)
    RDFA.CALLBACK_NEW_TRIPLE_WITH_SUBJECT = function(foo,bar) {};

//
// A better associative array
//
Array.prototype.add = function(name,value) {
    this.push(value);
    this[name] = value;

    // keep a list of names
    if (!this.names) {
        this.names = new Array();
    }

    this.names.push(name);
};

// a shallow copy of an array (only the named items)
Array.prototype.copy = function() {
    var the_copy = new Array();

    if (this.names) {
        // loop and copy
        for (var i=0; i < this.names.length; i++) {
            the_copy.add(this.names[i],this[this.names[i]]);
        }
    }

    return the_copy;
};

//
//
//

// XML Namespace abstraction
RDFA.Namespace = function(prefix, uri) {
    this.prefix = prefix;
    this.uri = uri;
};

RDFA.Namespace.prototype.equals = function(other) {
    return (this.uri == other.uri);
};

// RDF/A CURIE abstraction
RDFA.CURIE = function(ns,suffix) {
    this.ns = ns;
    this.suffix = suffix;
};

RDFA.CURIE.prototype.pretty = function() {
    return (this.ns? this.ns.prefix:'?') + ':' + this.suffix;
};

RDFA.CURIE.prototype.uri = function() {
    return (this.ns? this.ns.uri + this.suffix:'');
};

RDFA.CURIE.prototype.equals = function(other) {
    return (this.ns.equals(other.ns) && (this.suffix == other.suffix));
};

RDFA.CURIE.parse = function(str, namespaces) {
    var position = str.indexOf(':');

    // this will work even if prefix == -1
    var prefix = str.substring(0,position);
    var suffix = str.substring(position+1);

    var curie = new RDFA.CURIE(namespaces[prefix],suffix);
    return curie;
};

RDFA.CURIE.prettyCURIEorURI = function(str) {
    if (str[0] == '[')
        return str.substring(1,str.length - 1);
    else
        return '<' + str + '>';
}

RDFA.CURIE.prettyCURIEorURIinHTML = function(str) {
    if (str[0] == '[')
        return str.substring(1,str.length - 1);
    else
        return '&lt;' + str + '&gt;';
}

// RDF Triple abstraction
RDFA.Triple = function() {
    this.subject = '';
    this.predicate = '';
    this.object = '';
    this.object_literal_p = null;
};

RDFA.Triple.prototype.setLiteral= function(is_literal) {
    this.object_literal_p = is_literal;
};

RDFA.Triple.prototype.pretty = function() {

    // subject
    var pretty_string = RDFA.CURIE.prettyCURIEorURI(this.subject) + ' ';
    
    // predicate
    pretty_string += this.predicate.pretty() + ' ';
    
    if (this.object_literal_p) {
        pretty_string+= '"'+ this.object + '"';
    } else {
        pretty_string+= RDFA.CURIE.prettyCURIEorURI(this.object);
    }

    return pretty_string;
};

RDFA.Triple.prototype.prettyhtml = function() {
    var pretty_subject = this.subject;

    var pretty_string= RDFA.CURIE.prettyCURIEorURIinHTML(this.subject) + ' <a href="' + this.predicate.uri() + '">' + this.predicate.pretty() + '</a> ';

    if (this.object_literal_p) {
        pretty_string+= '"'+ this.object + '"';
    } else {
        pretty_string+= RDFA.CURIE.prettyCURIEorURIinHTML(this.object);
    }

    return pretty_string;
};


//
// This would be done by editing Node.prototype if all browsers supported it... (-Ben)
//
RDFA.getNodeAttributeValue = function(element, attr) {
    if (!element)
        return null;

    if (element.getAttribute) {
        if (element.getAttribute(attr))
            return(element.getAttribute(attr));
    }

    if (!element.attributes)
        return null;

	if (!element.attributes[attr])
		return null;

	return element.attributes[attr].value;
};

RDFA.setNodeAttributeValue = function(element, attr, value) {
    if (!element)
        return;

    if (element.setAttribute) {
        element.setAttribute(attr,value);
        return;
    }

    if (!element.attributes)
        element.attributes = new Object();

    element.attributes[attr] = new Object();
    element.attributes[attr].value = value;
};

//
// Support for loading other files
//

RDFA.GRDDL = new Object();

RDFA.GRDDL.CALLBACKS = new Array();

RDFA.GRDDL.DONE_LOADING = function(url) {
    RDFA.GRDDL.CALLBACKS[url]();
};

RDFA.GRDDL.load = function(url, callback)
{
    var s = document.createElement("script");
    s.type = 'text/javascript';
    s.src = url;

    // set up the callback
    RDFA.GRDDL.CALLBACKS[url] = callback;

    // add it to the document tree, load it up!
    document.getElementsByTagName('head')[0].appendChild(s);
};

//
// Support of in-place-GRDDL
//

RDFA.GRDDL._profiles = new Array();

RDFA.GRDDL.addProfile = function(js_url) {
    RDFA.GRDDL._profiles[RDFA.GRDDL._profiles.length] = js_url;
};

RDFA.GRDDL.runProfiles = function(callback) {
    var next_profile = RDFA.GRDDL._profiles.shift();
    //alert('going through profile ' + next_profile);

    if (!next_profile) {
        //alert('no more profiles!');
        callback();
        return;
    }

    // load the next profile, and when that is done, run the next profiles
    RDFA.GRDDL.load(next_profile, function() {
        //alert('back from profile ' + next_profile);
        RDFA.GRDDL.runProfiles(callback);
    });
}


//
//
//

RDFA.add_triple = function (subject, predicate, object, literal_p) {
    var triple = new RDFA.Triple();
    triple.subject = subject;
    triple.predicate = predicate;
    triple.object = object;
    triple.setLiteral(literal_p);

    // set up the array for that subject
    if (!RDFA.triples[triple.subject]) {
        RDFA.triples.add(triple.subject, new Array());
    }

    // we have to index by a string, so let's get the unique string, the URI
    var predicate_uri = triple.predicate.uri();

    if (!RDFA.triples[triple.subject][predicate_uri]) {
        RDFA.triples[triple.subject][predicate_uri] = new Array();
    }

    // store the triple
    var the_array = RDFA.triples[triple.subject][predicate_uri];
    the_array.push(triple);

	return triple;
};

RDFA.get_special_subject = function(element) {
	// ABOUT overrides ID
	if (RDFA.getNodeAttributeValue(element,'about'))
		return RDFA.getNodeAttributeValue(element,'about');

    // there is no ABOUT, but this might be the HEAD
    if (element.name == 'head')
        return ""

	// ID
	if (RDFA.getNodeAttributeValue(element,'id'))
		return "#" + RDFA.getNodeAttributeValue(element,'id');

	// BNODE, let's set it up if we need to
	if (!element.special_subject) {
		element.special_subject = '[_:' + element.nodeName + RDFA.bnode_counter + ']';
		RDFA.bnode_counter++;
	}

	return element.special_subject
};

//
// Process Namespaces
//
RDFA.add_namespaces = function(element, namespaces) {
    // we only copy the namespaces array if we really need to
    var copied_yet = 0;

    // go through the attributes
    var attributes = element.attributes;

    if (!attributes)
        return namespaces;

    for (var i=0; i<attributes.length; i++) {
        if (attributes[i].name.substring(0,5) == "xmlns") {
            if (!copied_yet) {
                namespaces = namespaces.copy();
                copied_yet = 1;
            }

            if (attributes[i].name.length == 5) {
                namespaces.add('',new RDFA.Namespace('',attributes[i].value));
            }

            if (attributes[i].name[5] != ':')
                continue;

            var prefix = attributes[i].name.substring(6);
            var uri = attributes[i].value;

            namespaces.add(prefix, new RDFA.Namespace(prefix,uri));
        }
    }

    return namespaces;
};

// this function takes a given element in the DOM tree and:
//
// - determines RDF/a statements about this particular element and adds the triples.
// - recurses down the DOM tree appropriately
//
// the namespaces is an associative array where the default namespace is namespaces['']
//
RDFA.traverse = function (element, inherited_about, explicit_about, namespaces) {

    // are there namespaces declared
    namespaces = RDFA.add_namespaces(element,namespaces);

    // determine the current about
    var current_about = inherited_about;
	var element_to_callback = element;

    // do we explicitly override it?
    var new_explicit_about = null;
    if (RDFA.getNodeAttributeValue(element,'about')) {
        new_explicit_about = RDFA.getNodeAttributeValue(element,'about');
        current_about = new_explicit_about;
    }

    // determine the object
    var el_object = null;
    if (RDFA.getNodeAttributeValue(element,'href'))
	el_object = RDFA.getNodeAttributeValue(element,'href');
    if (RDFA.getNodeAttributeValue(element,'src'))
	el_object = RDFA.getNodeAttributeValue(element,'src');
    
    // LINK
    if (element.nodeName == 'link' || element.nodeName == 'meta') {
	current_about = RDFA.get_special_subject(element.parentNode);
	element_to_callback = element.parentNode;
    }

    // REL attribute
    if (RDFA.getNodeAttributeValue(element,'rel')) {
	var triple = RDFA.add_triple(current_about, RDFA.CURIE.parse(RDFA.getNodeAttributeValue(element,'rel'),namespaces), el_object, 0);
        RDFA.CALLBACK_NEW_TRIPLE_WITH_URI_OBJECT(element_to_callback, triple);
    }

    // REV attribute
    if (RDFA.getNodeAttributeValue(element,'rev')) {
        var triple = RDFA.add_triple(el_object, RDFA.CURIE.parse(RDFA.getNodeAttributeValue(element,'rev'),namespaces), current_about, 0);
        RDFA.CALLBACK_NEW_TRIPLE_WITH_URI_OBJECT(element_to_callback, triple);
    }
    
    // PROPERTY attribute
    if (RDFA.getNodeAttributeValue(element,'property')) {
        var content = RDFA.getNodeAttributeValue(element,'content');
	
        if (!content)
            content = element.textContent;
	
        var triple = RDFA.add_triple(current_about, RDFA.CURIE.parse(RDFA.getNodeAttributeValue(element,'property'),namespaces), content, 1);
        RDFA.CALLBACK_NEW_TRIPLE_WITH_LITERAL_OBJECT(element_to_callback, triple);
    }


    // recurse down the children
    var children = element.childNodes;
    for (var i=0; i < children.length; i++) {
	RDFA.traverse(children[i], current_about, new_explicit_about, namespaces);
    }
};

RDFA.getTriples = function(subject, predicate) {
    if (!RDFA.triples[subject])
        return null;

    return RDFA.triples[subject][predicate.uri()];
};

RDFA.parse = function() {
    // by default, about is the current URL, and the namespace is XHTML1
    var xhtml = new RDFA.Namespace('xhtml','http://www.w3.org/1999/xhtml');
    var namespaces = new Array();

    // set up default namespace
    namespaces.add('',xhtml);

    // do the profiles, and then traverse
    RDFA.GRDDL.runProfiles(function() {
        //alert('now traversing.... ');
        RDFA.traverse(document, '', null, namespaces);

        RDFA.CALLBACK_DONE_PARSING();
    });
};

RDFA.log = function(str) {
    alert(str);
};

RDFA.CALLBACK_DONE_LOADING();
