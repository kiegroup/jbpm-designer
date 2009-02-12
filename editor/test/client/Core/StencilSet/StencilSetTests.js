
// Mocks

// mocking Ajax Object
Ajax = {}

// Stubs

// stubing Ajax.request method
Ajax.Request = function(source, options) {
	var response = {}
	response.responseText = Object.toJSON(testStencilSetJSON)
	options.onSuccess(response)
} 


//var testStencilSet = new ORYX.Core.StencilSet.StencilSet("./fixtures/StencilSet/testStencilSet.json")

function setUp() {
	testStencilSetURL = "../../../test/client/Core/fixtures/StencilSet/testStencilSet.json"
	testCorruptedStencilSetURL = "../../../test/client/Core/fixtures/StencilSet/testCorruptedStencilSet.json"
}

/**
 * Tests, if missing parameters are handled.
 */
function testFailForMissingSourceParameter() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet()
		fail("Test case should fail yet, because of missing source parameter")
	} catch(e) {
		if((e instanceof JsUnitException)) {
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
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * Tests, if corrupted JSON-Format is handled.
 */
function testFailForCorruptedJSON() {
	try {
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet(testCorruptedStencilSetURL)
		//alert(testStencilSet)
		fail("Test case should fail yet, because of corrupted stencil set json format.")
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

function testConstructor() {
	try {
		alert("hier ss")
		alert(testStencilSetJSON)
		var testStencilSet = new ORYX.Core.StencilSet.StencilSet("./test/url.json")
	} catch (e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
	assertFalse(testStencilSet.errornous)
}
