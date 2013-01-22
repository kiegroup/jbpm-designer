// mocks

// mocking ORYX.Log
if(!ORYX) {var ORYX = {};}
if(!ORYX.Log) {ORYX.Log = {};}

// mocking ORYX.StencilSet.StencilSet
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.StencilSet) {ORYX.Core.StencilSet = {};}
ORYX.Core.StencilSet.StencilSet = Clazz.extend({
	construct : function(namespace) {
		arguments.callee.$.construct.apply(this, arguments);
		this._namespace = namespace
	},
	
	namespace : function() {
		return this._namespace
	},
	stencil : function(id) {
		if(id == "http://example.com#ANode" || id == "http://example.com#AnEdge")
			return {"stencil": true}
	}
})

// mocking ORYX.StencilSet.Rules
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.StencilSet) {ORYX.Core.StencilSet = {};}
ORYX.Core.StencilSet.Rules = Clazz.extend({
	construct : function(name) {
		arguments.callee.$.construct.apply(this, arguments);
		this._name = name
	},
	
	rulesName : function() {
		return this._name
	},
	
	initializeRules: function(stencilSet) {
		return
	}
})

// mocking ORYX.I18N.Language
if(!ORYX) {var ORYX = {};}
if(!ORYX.I18N) {ORYX.I18N = {};}
ORYX.I18N.Language = "de_at"

// stubs

// stubing ORYX.Log.trace()
ORYX.Log.trace = function(){return}

// Test Cases
function setUp() {
	// storage for loaded stencil sets by namespace
	ORYX.Core.StencilSet._stencilSetsByNamespace = new Hash();

	// storage for stencil sets by url
	ORYX.Core.StencilSet._stencilSetsByUrl = new Hash();

	// storage for stencil set namespaces by editor instances
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance = new Hash();

	// storage for rules by editor instances
	ORYX.Core.StencilSet._rulesByEditorInstance = new Hash();
}


/**
 * Test, if the right stencilset can be found by namespace.
 * Namespace always ends with a '#' by convention
 */
function testGetStencilSetByNamespace() {

	ORYX.Core.StencilSet._stencilSetsByNamespace["http://www.firstexample.org/stencilset#"] =  "with#"
	ORYX.Core.StencilSet._stencilSetsByNamespace["http://www.firstexample.org/Stencilset#"] =  "With#"
	ORYX.Core.StencilSet._stencilSetsByNamespace["http://www.firstexample.org/stencilset"] =  "without#"
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("http://www.firstexample.org/stencilset#")
	assertEquals("Checks, if the correct StencilSet is delivered by namespace", stencilset, "with#")
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("http://www.firstexample.org/stencilset")
	assertEquals("Checks, if the correct StencilSet is delivered by namespace", stencilset, "with#")
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("http://www.firstexample.org/stencilset#aaaaa")
	assertEquals("Checks, if the correct StencilSet is delivered by namespace", stencilset, "with#")
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("http://www.firstexample.org/Stencilset")
	assertEquals("Checks, if the correct StencilSet is delivered by namespace", stencilset, "With#")

}

/**
 * Tests, if non existing stencil set namespaces are handled correctly.
 */
function testWrongStencilSetNamespaces() {
	ORYX.Core.StencilSet._stencilSetsByNamespace["http://www.firstexample.org/stencilset#"] =  "with#"
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("##")
	assertEquals("Checks, if the wrong namespace syntax is handled correctly", stencilset, undefined)
	
	var stencilset = ORYX.Core.StencilSet.stencilSet("http://www.firstexample.org/stencilset2")
	assertEquals("Checks, if the wrong namespace syntax is handled correctly", stencilset, undefined)
}

/**
 * It is expected that numbers as namespace are not a
 */
function testFailForNumberAsStencilSetNamespace() {
	ORYX.Core.StencilSet._stencilSetsByNamespace[3] =  "testStencil";
	try {
		var stencilset = ORYX.Core.StencilSet.stencilSet(3)
		fail("Test should fail yet for number used as namespace.")
	}catch(e) {
		if(!( e instanceof JsUnitException)) {
			throw e
		}
	}	
}

/**
 * Tests, if the right stencil sets are returned for a editor instance.
 */
function testGetStencilSetsForEditorInstance() {
	// prepare stencil sets
	var stencilset1 = new ORYX.Core.StencilSet.StencilSet("http://www.firstexample.org/stencilset1#")
	var stencilset2 = new ORYX.Core.StencilSet.StencilSet("http://www.firstexample.org/stencilset2#")
	
	// prepare Hashes
	ORYX.Core.StencilSet._stencilSetsByNamespace[stencilset1.namespace()] =  stencilset1
	ORYX.Core.StencilSet._stencilSetsByNamespace[stencilset2.namespace()] =  stencilset2
	
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance["editor1"] = new Array();
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance["editor2"] = new Array();
	
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance["editor1"].push(stencilset1.namespace())
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance["editor1"].push(stencilset2.namespace())
	ORYX.Core.StencilSet._StencilSetNSByEditorInstance["editor2"].push(stencilset1.namespace())
	
	var stencilsets = ORYX.Core.StencilSet.stencilSets("editor1")
	var compareStencilSets = new Hash()
	compareStencilSets[stencilset1.namespace()] = stencilset1
	compareStencilSets[stencilset2.namespace()] = stencilset2
	
	// test if the appropriate stencilsets are passed
	assertEquals(2, $A(stencilsets).length)
	assertEquals("Checks, if the appropriate stencil sets are loaded.", stencilsets[stencilset1.namespace()], compareStencilSets[stencilset1.namespace()])
	assertEquals("Checks, if the appropriate stencil sets are loaded.", stencilsets[stencilset2.namespace()], compareStencilSets[stencilset2.namespace()])
	
	// tests for second editor instance
	var stencilsets = ORYX.Core.StencilSet.stencilSets("editor2")
	var compareStencilSets = new Hash()
	compareStencilSets[stencilset1.namespace()] = stencilset1
	
	assertEquals("Checks, if the appropriate stencil sets are loaded.", stencilsets[stencilset1.namespace()], compareStencilSets[stencilset1.namespace()])
	assertUndefined("The Editor2 instance should not have an reference to the stencilset2.", stencilsets[stencilset2.namespace()])
}

/**
 * Test if not existing editor instances are handled.
 * It should return an empty Hash.
 */
function testNotExistingEditorInstances() {
	assertEquals("A not existing editor instance should not have any reference to a stencil set", 0, $A(ORYX.Core.StencilSet.stencilSets("editorXX")).length)
}

/**
 * Tests if undefined is returned if the stencilset or stencil does not exists.
 */
function testGetUndefindedStencil() {
	assertUndefined(ORYX.Core.StencilSet.stencil("http://notexistingStencilSet.com#ANode"))
	
	// preparation for not existing stencil in existing stencil set
	var stencilset1 = new ORYX.Core.StencilSet.StencilSet("http://www.firstexample.org/stencilset1#")
	ORYX.Core.StencilSet._stencilSetsByNamespace[stencilset1.namespace()] =  stencilset1
	
	assertUndefined(ORYX.Core.StencilSet.stencil("http://www.firstexample.org/stencilset1#ANotNode"))
}

/**
 * Test if the appropriated rules for an editor instance are returned
 */
function testRulesForEditorInstance() {
	var rules1 = new ORYX.Core.StencilSet.Rules("rule1")
	
	// prepare Hash
	ORYX.Core.StencilSet._rulesByEditorInstance["first"] = rules1
	
	// test
	assertEquals("Rules of first editor should be rules1", rules1, ORYX.Core.StencilSet.rules("first"))
	assertTrue("Rules should be created for a new editor instance", ORYX.Core.StencilSet.rules("second") instanceof ORYX.Core.StencilSet.Rules)
}

/**
 * Test tries to load a stencil form a given stencilset.
 */
function testGetStencilFromStencilSet() {
	// prepare stencil set
	
	var stencilset = new ORYX.Core.StencilSet.StencilSet("http://example.com#")
	
	// prepare Hash
	ORYX.Core.StencilSet._stencilSetsByNamespace[stencilset.namespace()] =  stencilset
	
	// node
	var stencil = ORYX.Core.StencilSet.stencil("http://example.com#ANode")
	assertTrue("Verify return value from stubed stencil method.", stencil["stencil"])
	
	// edge
	var stencil = ORYX.Core.StencilSet.stencil("http://example.com#AnEdge")
	assertTrue("Verify return value from stubed stencil method.", stencil["stencil"])
}

/**
 * The test loads a new stencilset.
 */
function testLoadNewStencilSet() {
	ORYX.Core.StencilSet.loadStencilSet("http://example.com#", "myId")
	
	//test if the new stencil set exists
	assertTrue("Test existence of stencil set.",ORYX.Core.StencilSet._stencilSetsByNamespace["http://example.com#"] instanceof ORYX.Core.StencilSet.StencilSet)
	
	assertTrue("Test existence of stencil set in stencil set by url hash.",ORYX.Core.StencilSet._stencilSetsByUrl["http://example.com#"] instanceof ORYX.Core.StencilSet.StencilSet)
	
	assertTrue("Test if rules are set correctly.", ORYX.Core.StencilSet._rulesByEditorInstance["myId"] instanceof ORYX.Core.StencilSet.Rules)
}

/**
 *  Test getTranslation with an invalid JSON-object.
 */
function testFailForInvalidJSONInTranslation() {
	
	try {
		ORYX.Core.StencilSet.getTranslation("invalidjsonObject", "title")
		fail("Test should fail yet for invalid JSON.")
	} catch (e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * If a Attributes does not exists, it should return undefined.
 */
function testNotExistingAttributeTranslationInJSON() {
	var json = {
			"type":			"node",
			"id":			"Diagram",
			"title":		"Diagram",	
			"title_de": 	"Diagram_de",
			"title_de_at": "Diagram_de_at"
			}
			
	assertUndefined("Test not existing Attribute.",ORYX.Core.StencilSet.getTranslation(json, "name"))
}

/**
 * Tries to get an attribute for different languages.
 */
function testgetTranslationOfJSONAttr() {
	var json = {
			"type":			"node",
			"id":			"Diagram",
			"title":		"Diagram",	
			"title_de": 	"Diagram_de",
			"title_de_at": "Diagram_de_at",
			"title_ch": "Diagram_ch"
			}
	
	// get title with lang de
	ORYX.I18N.Language = "de"		
	assertEquals("Get title_de",ORYX.Core.StencilSet.getTranslation(json, "title"), "Diagram_de")
	
	// get title with lang de_at
	ORYX.I18N.Language = "de_at"		
	assertEquals("Get title_de_at",ORYX.Core.StencilSet.getTranslation(json, "title"), "Diagram_de_at")
	
	// get title default
	ORYX.I18N.Language = "ar"		
	assertEquals("Get title default",ORYX.Core.StencilSet.getTranslation(json, "title"), "Diagram")
	
	// get title_ch with lang ch_zz
	ORYX.I18N.Language = "ch_zz"		
	assertEquals("Get title_de",ORYX.Core.StencilSet.getTranslation(json, "title"), "Diagram_ch")
}














