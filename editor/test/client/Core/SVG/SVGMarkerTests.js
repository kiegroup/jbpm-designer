
//mocks
/**
 * Init package
 */
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}
if(!ORYX.Core.SVG) {ORYX.Core.SVG = {};}

//stubs


//tests

function setUp() {
	testSVGMarkerElement = document.createElementNS('http://www.w3.org/2000/svg', 'marker')
}

/**
 * Tests if an exception is thrown if no valid SVGMarkerElement is passed to the constructor.
 */
function testFailForAnInvalidSVGMarkerElement() {
	try {
		testSVGMarker = new ORYX.Core.SVG.SVGMarker(5);
		fail("SVGMarker test should fail yet.")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e;
		}
	}
}

/**
 * Creates a SVGMarker instance, from an empty SVGMarkerElement.
 */
function testCreateSVGMarkerFromDOMElement() {
	try {
		testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
		fail(" ")
	} catch (e) {
		if (!(e instanceof JsUnitException)) {
			throw e;
		}
	}
}

/**
 * No further attributes specified. So check if default values are set.
 */
function testDefaultValues() {
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertNotNaN("Is oldRefX a Number?",Number(testSVGMarker.oldRefX))
	assertNotNaN("Is oldRefY a Number?",Number(testSVGMarker.oldRefY))
	assertNotNaN("Is oldMarkerWidth a Number?",Number(testSVGMarker.oldMarkerWidth))
	assertNotNaN("Is oldMarkerHeight a Number?",Number(testSVGMarker.oldMarkerHeight))
	assertFalse("Not optional",testSVGMarker.optional)
	assertTrue("Enabled",testSVGMarker.enabled)
	assertFalse("Should not be resizable",testSVGMarker.resize)
}

/**
 * Test parsing of id attribute
 */
function testIDValue() {
	testSVGMarkerElement.setAttributeNS(null, "id", "myMarker")
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertEquals("ID value should be 'myMarker'", testSVGMarker.id, "myMarker")
}

/**
 * Test parsing of refX attribute
 */
function testRefXValue() {
	testSVGMarkerElement.setAttributeNS(null, "refX", 89.45)
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertEquals("Checks RefX value.", testSVGMarker.refX, 89.45)
	assertEquals("Checks oldRefX value.", testSVGMarker.oldRefX, 89.45)
}

/**
 * Test parsing of refX as String attribute
 */
function testRefXStringValue() {
	testSVGMarkerElement.setAttributeNS(null, "refX", "89.45")
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertEquals("Checks RefX value.", testSVGMarker.refX, 89.45)
	assertEquals("Checks oldRefX value.", testSVGMarker.oldRefX, 89.45)
}

/**
 * If RefX is not a number, it should throw an Exception.
 */
function testFailForNaNRefXValue() {
	try {
		testSVGMarkerElement.setAttributeNS(null, "refX", "two")
		testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
		fail("Test should fail yet.")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e;
		}
	}
}

/**
 * Test parsing of refY attribute
 */
function testRefYValue() {
	testSVGMarkerElement.setAttributeNS(null, "refY", 89.45)
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertEquals("Checks RefY value.", testSVGMarker.refY, 89.45)
	assertEquals("Checks oldRefX value.", testSVGMarker.oldRefY, 89.45)
}

/**
 * Test parsing of refY as String attribute
 */
function testRefYStringValue() {
	testSVGMarkerElement.setAttributeNS(null, "refY", "89.45")
	testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
	assertEquals("Checks RefY value.", testSVGMarker.refY, 89.45)
	assertEquals("Checks oldRefY value.", testSVGMarker.oldRefY, 89.45)
}

/**
 * If RefY is not a number, it should throw an Exception.
 */
function testFailForNaNRefYValue() {
	try {
		testSVGMarkerElement.setAttributeNS(null, "refY", "two")
		testSVGMarker = new ORYX.Core.SVG.SVGMarker(testSVGMarkerElement);
		fail("Test should fail yet.")
	} catch (e) {
		if ((e instanceof JsUnitException)) {
			throw e;
		}
	}
}


