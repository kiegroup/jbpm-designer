
// Mocks

// mocking ORYX.Log
if (!ORYX) {
	ORYX = {};
}
if (!ORYX.Log) {
	ORYX.Log = {};
}

// mocking Stencil Class
if (!ORYX) {
	ORYX = {};
}
if (!ORYX.Core) {
	ORYX.Core = {};
}
if (!ORYX.Core.StencilSet) {
	ORYX.Core.StencilSet = {};
}

ORYX.Core.StencilSet.Stencil = Clazz.extend({
			construct : function(stencil, namespace, baseUrl, stencilSet) {
				arguments.callee.$.construct.apply(this, arguments);
				this._jsonStencil = stencil
				this.namespace = namespace
				this.baseUrl = baseUrl
				this.stencilSet = stencilSet
				this._properties = new Hash()
				// add id of stencil to its roles
				this._jsonStencil.roles.push(this._jsonStencil.id);

				// prepend namespace to each role
				this._jsonStencil.roles.each((function(role, index) {
							this._jsonStencil.roles[index] = namespace + role;
						}).bind(this));

				// delete duplicate roles
				this._jsonStencil.roles = this._jsonStencil.roles.uniq();

				// make id unique by prepending namespace of stencil set
				this._jsonStencil.id = namespace + this._jsonStencil.id;
			},
			id : function() {
				return this._jsonStencil.id;
			},
			type : function() {
				return this._jsonStencil.type;
			},
			addProperty : function(property, namespace) {
				this._properties[property.id] = property
			},
			property : function(id) {
				return this._properties[id]
			},
			roles : function() {
				return this._jsonStencil.roles
			},
			removeProperty: function(id) {
				this._properties.remove(id)
			}
			,

		})

// mocking Ajax Object
Ajax = {}

// Stubs

// stubing Ajax.request method
Ajax.Request = function(source, options) {
	//TODO: use Hash instead of if conditions
	
	var response = {}
	// valid stencil set
	if (source == "TestStencilSet") {
		response.responseText = Object.toJSON(testStencilSetJSON)
	}

	// valid stencil set, but without explicitly marked root element
	if (source == "StencilSetWithoutRoot") {
		response.responseText = Object.toJSON(testStencilSetWithoutRootJSON)
	}

	// invalid stencil set
	if (source == "TestCorruptedStencilSet") {
		response.responseText = Object.toJSON(testCorruptedStencilSetJSON)
	}

	// simulate failure while processing AJAX request
	if (source == "request fail") {
		options.onFailure(response)
		return
	}

	// load stencil set extension
	if (source == "StencilSetExtension") {
		response.responseText = Object.toJSON(testStencilSetExtensionJSON)
	}

	// load stencil set Extension (namespace does not ends with a pound sign)
	if (source == "StencilSetExtensionWithoutNamespace#") {
		var extension = testStencilSetExtensionJSON
		extension["extends"] = "http://b3mn.org/stencilset/testB"

		response.responseText = Object.toJSON(extension)
	}

	// load stencil set Extension (namespace does not match the current stencil
	// set namespace)
	if (source == "StencilSetExtensionNotMatchingNamespace") {
		var extension = testStencilSetExtensionJSON
		extension["extends"] = "http://b3mn.org/stencilset/testBBB#"

		response.responseText = Object.toJSON(extension)
	}

	// loads an invalid stencil set Extension
	if (source == "StencilSetExtensionInvalid") {
		var extension = "Invalid"

		response.responseText = Object.toJSON(extension)
	}

	// {
	// "StencilSetExtensionNewStencil": function(){
	// var extension = testStencilSetExtensionJSON
	// extension.stencils.push({
	// "type":"node",
	// "id":"newStencil"
	// })
	// return extension;
	// }
	// }

	// response.responseText = Object.toJSON(map[source]());

	// loads a stencil set extension, which adds a stencil to the curret set
	if (source == "StencilSetExtensionNewStencil") {
		var extension = testStencilSetExtensionJSON
		extension.stencils.push({
					"type" : "node",
					"id" : "newStencil",
					"roles": []
					
				})
		response.responseText = Object.toJSON(extension)
	}

	// loads a stencil set extension, which adds a property package to the
	// current set
	if (source == "StencilSetExtensionNewProperties") {
		var extension = testStencilSetExtensionAddPropJSON
		response.responseText = Object.toJSON(extension)
	}
	
	// loads a stencil set extension, which removes a properties from a stencil of the current set
	if (source == "StencilSetExtensionRemoveProperties") {
		var extension = testStencilSetExtensionRemovePropJSON
		response.responseText = Object.toJSON(extension)
	}
	
	
	// loads a stencil set extension, which removes stencils from the current set
	if (source == "StencilSetExtensionRemoveStencils") {
		var extension = testStencilSetExtensionRemoveStencilsJSON
		response.responseText = Object.toJSON(extension)
	}

	options.onSuccess(response)
}

// stubing ORYX.Log.warn method

ORYX.Log.warn = function(string) {
	var warning = string
}

// stubing ORYX.Log.debug method

ORYX.Log.debug = function(string) {
	ORYX.Log.debugInfo = string
}

/**
 * Tests, if missing parameters are handled.
 */
function testFailForMissingSourceParameter() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet()
		fail("Test case should fail yet, because of missing source parameter")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * Tests, if NoSuchFile Error is handled.
 */
function testFailForNoSuchFile() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet("./badDir/zzz.json")

		fail("Test case should fail yet, because of NoSuchFile Error.")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * Tests, if corrupted JSON-Format is handled.
 */
function testFailForCorruptedJSON() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestCorruptedStencilSet")

		fail("Test case should fail yet, because of corrupted stencil set json format.")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * A valid stencil set is passed to the constructor. So afterwards a new
 * instance of ORYX.Core.StencilSet.StencilSet should exist.
 */
function testConstructor() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	assertTrue("A new StencilSet should be created.",
			testStencilSet instanceof ORYX.Core.StencilSet.StencilSet)
	assertUndefined(testStencilSet.errornous)
}

/**
 * Simulate a failure on Ajax request and test if it is handled.
 */
function testFailureOnAjaxRequest() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet("request fail")
		fail("Test should fail yet, because of failed ajax request.")
	} catch (e) {
		if (!(e == "Loading stencil set request fail failed.")) {
			throw e
		}
	}
}

/**
 * In a stencil set without explicitly marked root element, the
 * findRootStencilName method should return the first one.
 */
function testFindRootStencilNameWithoutMarkedRoot() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("StencilSetWithoutRoot")
	var root = testStencilSet.findRootStencilName()
	assertEquals("Diagram should be the root Stencil", "http://b3mn.org/stencilset/test#Diagram", root)
}

/**
 * FindRootStencilName should return the stencil marked as root element
 */
function testFindRootStencilNameWithMarkedRoot() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var root = testStencilSet.findRootStencilName()
	assertEquals("Diagram should be the root Stencil", "http://b3mn.org/stencilset/testB#Diagram", root)
}

/**
 * Two stencil sets are equal if both have the same namespace. It should be
 * checked if the passed stencilset is an instance of
 * ORYX.Core.StencilSet.StencilSet
 */
function testFailForInvalidParameterStencilSetEqual() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet("StencilSetWithoutRoot")
		testStencilSet.equals("invaildStencilSet")
		fail("Test should fail yet for passing invalid stencil set")
	} catch (e) {
		if (e != "Invaild Stencilset") {
			throw e
		}
	}

}

/**
 * If both stencil sets have the same namespace it shell return true.
 */
function testEqualStencilSets() {
	var testStencilSet1 = new ORYX.Core.StencilSet.StencilSet("StencilSetWithoutRoot")
	var testStencilSet2 = new ORYX.Core.StencilSet.StencilSet("StencilSetWithoutRoot")
	assertTrue("The two stencil sets should be equal.", testStencilSet1
					.equals(testStencilSet2))
}

/**
 * If both stencil sets have not the same namespace it shell return true.
 */
function testEqualStencilSets() {
	var testStencilSet1 = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var testStencilSet2 = new ORYX.Core.StencilSet.StencilSet("StencilSetWithoutRoot")
	assertFalse("The two stencil sets should not be equal.", testStencilSet1
					.equals(testStencilSet2))
}

/**
 * If the stencils method gets invalid parameters, it should raise an exception.
 */
function testInvalidParametersForStencilsMethod() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	try {
		testStencilSet.stencils("invalidstencil", "invalidrules")
	} catch (e) {
		if (e != "invalid parameters") {
			throw e
		}
	}
}

/**
 * If no parameters are passed, the stencils method should return all available
 * stencils.
 */
function testStencilsAll() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var stencils = testStencilSet.stencils()

	assertEquals("Number elements should be equal",
			testStencilSet._availableStencils.size(), stencils.size())
	stencils.each(function(stencil) {
				var equalStencil = testStencilSet._availableStencils.find(
						function(aStencil) {
							return aStencil.value._jsonStencil == stencil._jsonStencil
						})
				assertNotUndefined("Stencil should be there.", equalStencil)
			})
}

/**
 * Test stencils method with reject all containment rule.
 */
function testStencilsRejectAll() {
	var rules = {}
	rules.canContain = function(arg) {
		return false
	}
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var stencils = testStencilSet.stencils(
			testStencilSet._stencils.values()[0], rules)
	// there is one edge inside the test stencil set
	assertTrue("No returned stencil are expected", stencils.length == 1) 
}

/**
 * Test stencils method with an all accepting containment rule.
 */
function testStencilsAcceptAll() {
	// prepare rule object
	var rules = {}
	rules.canContain = function(arg) {
		return true
	}

	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var stencils = testStencilSet.stencils(
			testStencilSet._stencils.values()[0], rules)
	assertEquals("Number elements should be equal",
			testStencilSet._availableStencils.size() + 1, stencils.size())
	stencils.each(function(stencil) {
		var equalStencil = testStencilSet._availableStencils.find(function(
						aStencil) {
					return aStencil.value._jsonStencil.id == stencil._jsonStencil.id
				})
		assertNotUndefined("Stencil should be there.", equalStencil)
	})
}

/**
 * Test if the node method returns the correct stencils.
 */
function testGetNodes() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")

	var nodes = testStencilSet.nodes()

	// SequenceFlow is the only edge in this stencil set
	var nodeStencils = testStencilSet._availableStencils
	nodeStencils.remove('http://b3mn.org/stencilset/testB#SequenceFlow')

	assertEquals("Both sets should have the same quantity.", nodes.size(), nodeStencils.size())

	nodes.each(function(nodeA) {
				var node = nodeStencils.find(function(nodeB) {
							return nodeA == nodeB.value
						})
				assertNotUndefined("The node should exist.", node)
				delete node
			})
}

/**
 * The test stencil set contains only one edge. So the edges method should
 * return an array with the sequenceflow stencil.
 */
function testGetEdges() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")

	// use #edges method to retrieve edge stencils
	var edges = testStencilSet.edges()

	assertEquals("The SequencFlow stencil should be contained in edges.", edges[0]._jsonStencil.id, "http://b3mn.org/stencilset/testB#SequenceFlow")
}

/**
 * The edges method should also works well, if there are no edge available in
 * the stencil set.
 */
function testGetEdgesNoEdges() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet._availableStencils.remove('http://b3mn.org/stencilset/testB#SequenceFlow')
	// Now the stencil set has no edges

	var edges = testStencilSet.edges()

	assertEquals("Edges should be empty.", edges.size(), 0)
}

/**
 * #stencil should return the appropriate stencil by id.
 */
function testGetStencilById() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	// get Diagram node
	var stencil = testStencilSet.stencil('http://b3mn.org/stencilset/testB#Diagram')
	assertEquals("The ID of the stencil should be 'Diagram'",
			stencil._jsonStencil.id, 'http://b3mn.org/stencilset/testB#Diagram')
}

/**
 * #stencil should return undefined if no stencil matches the id.
 */
function testGetStencilByIdNotExists() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")

	var stencil = testStencilSet.stencil('notexists')
	assertUndefined('No stencil should be returned.', stencil)
}

/**
 * Tests get namespace method
 */
function testGetNamespace() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var namespace = testStencilSet.namespace()

	assertEquals("Check namespace of testStencilSet", namespace, "http://b3mn.org/stencilset/testB#")
}

/**
 * Tests get rules from json source.
 */
function testGetJSONRules() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	var rules = testStencilSet.jsonRules()

	assertNotUndefined('ContainmentRules should exist.', rules.containmentRules)
	assertNotUndefined('ConnectionRules should exist.', rules.connectionRules)
}

/**
 * Tests get source of stencil set
 */
function testGetSource() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	assertEquals('Stencil set source should be "TestStencilSet".', testStencilSet.source(), "TestStencilSet")
}

/**
 * Test get extensions.
 */
function testGetExtensions() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")

	// Should return an empty hash.
	assertTrue("Should be an instance of hash",
			testStencilSet.extensions() instanceof Hash)
	assertEquals("At the moment there should not be any extensions", testStencilSet.extensions().size(), 0)
}

/**
 * Loads a stencil set extension and checks if it stored correctly.
 */
function testLoadStencilSetExtension() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtension")

	assertNotUndefined("The 'TestStencilSetExtension' should exist.", testStencilSet.extensions()["http://testStencilSetExtension#"])
}

/**
 * Test, that the namespace of the extended stencil set ends with a '#' is
 * assured.
 */
function testExtensionExtendNamespaceEndWithPoundSign() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtensionWithoutNamespace#")

	// pound sign at the end should be assured
	var extends = testStencilSet.extensions()["http://testStencilSetExtension#"].extends
	assertEquals("Pound sign should be added at the end.", extends, "http://b3mn.org/stencilset/testB#")
}

/**
 * If namespace defined under extends inside the extension does not match the
 * stencil set's namespace, the stencil set extension should be ignored.
 */
function testNotMatchingNamespace() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtensionNotMatchingNamespace")

	var extension = testStencilSet.extensions()["http://testStencilSetExtension#"]
	assertUndefined("The stencil set extension must not been added to the extension Hash-Map",extension)
}

/**
 * Test it with an invalid extension. An error should be thrown.
 */
function testFailForInvalidExtension() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtensionInvalid")

	var message = "StencilSet.addExtension: Something went wrong when initialising the stencil set extension. TypeError: jsonExtension.extends is undefined"
	assertEquals("Validate error message", message, ORYX.Log.debugInfo)
}

/**
 * This test tries to a new stencil to the stencil set over a stencil set
 * extension.
 */
function testAddNewStencil() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtensionNewStencil")

	// check if the new stencil was added

	assertNotUndefined("The new stencil should been added.", testStencilSet.stencil("http://b3mn.org/stencilset/testB#newStencil"))
}

/**
 * This test uses the possibility of a stencil set extension to add property packages to some stencils
 * and verify if they were only added to the stencils "Service1" and "Service2"
 */
function testAddPropertyPackageOverExtension() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	testStencilSet.addExtension("StencilSetExtensionNewProperties")

	assertNotUndefined("Now the new property should exist for Service1",
			testStencilSet.stencil("http://b3mn.org/stencilset/testB#Service1").property("testExtensionProp"))
	
	assertNotUndefined("Now the new property should exist for Service2",
			testStencilSet.stencil("http://b3mn.org/stencilset/testB#Service2").property("testExtensionProp"))	
			
	assertUndefined("But 'Diagram' should not have the property 'testExtensionProp'",
			testStencilSet.stencil("http://b3mn.org/stencilset/testB#Diagram").property("testExtensionProp"))
}

/**
 * This test uses the functionality of a stencil set extension to remove properties from a stencil
 * and verifies the result.
 */
function testRemovePropertiesOverExtension() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	
	// removes the properties value and description from stencil "Service1"
	
	testStencilSet.addExtension("StencilSetExtensionRemoveProperties")
	
	assertUndefined("Service1 should not have the property value",testStencilSet.stencil("http://b3mn.org/stencilset/testB#Service1").property("value"))
	assertUndefined("Service1 should not have the property description",testStencilSet.stencil("http://b3mn.org/stencilset/testB#Service1").property("description"))
}

/**
 * This test uses the functionality of a stencil set extension to remove stencils from a stencil set
 * and verifies the result.
 */
function testRemoveStencilsOverExtension() {
	var testStencilSet = new ORYX.Core.StencilSet.StencilSet("TestStencilSet")
	
	// removes the stencils "Service1" and "Service2"
	
	testStencilSet.addExtension("StencilSetExtensionRemoveStencils")
	
	assertUndefined("Stencil 'Service1' should not exist.",testStencilSet._availableStencils["http://b3mn.org/stencilset/testB#Service1"])
	assertUndefined("Stencil 'Service2' should not exist.",testStencilSet._availableStencils["http://b3mn.org/stencilset/testB#Service2"])
}
