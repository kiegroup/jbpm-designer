// mocks
// mocking ORYX.Editor object
if(!ORYX) {var ORYX = {};}
if(!ORYX.Editor) {ORYX.Editor = {};}

// stubs

// stubbing ORYX.Editor.checkClassType

ORYX.Editor.checkClassType = function( classInst, classType ) {
		return classInst instanceof classType
}

var testSVGRectElement = null;

/**
 * builds up testSVGElements
 */

function setUp(){
	// valid SVG-Rect-Element
	
	testSVGRectElement = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
	testSVGRectElement.setAttributeNS(null, "x", 1);
	testSVGRectElement.setAttributeNS(null, "y", 2);
	testSVGRectElement.setAttributeNS(null, "height", 100);
	testSVGRectElement.setAttributeNS(null, "width", 100);
	
	// valid SVG-Circle-Element
	
	testSVGCircleElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
	testSVGCircleElement.setAttributeNS(null, "cx", 1);
	testSVGCircleElement.setAttributeNS(null, "cy", 2);
	testSVGCircleElement.setAttributeNS(null, "r", 100);
	
	// valid SVG-Ellipse-Element
	
	testSVGEllipseElement = document.createElementNS('http://www.w3.org/2000/svg', 'ellipse');
	testSVGEllipseElement.setAttributeNS(null, "cx", 1);
	testSVGEllipseElement.setAttributeNS(null, "cy", 2);
	testSVGEllipseElement.setAttributeNS(null, "ry", 100);
	testSVGEllipseElement.setAttributeNS(null, "rx", 100);
	
	// valid SVG-Line-Element
	
	testSVGLineElement = document.createElementNS('http://www.w3.org/2000/svg', 'line');
	testSVGLineElement.setAttributeNS(null, "x1", 1);
	testSVGLineElement.setAttributeNS(null, "y1", 2);
	testSVGLineElement.setAttributeNS(null, "x2", 100);
	testSVGLineElement.setAttributeNS(null, "y2", 100);
	
	// valid SVG-Line-Element (only a point)
	
	testSVGNullLineElement = document.createElementNS('http://www.w3.org/2000/svg', 'line');
	testSVGNullLineElement.setAttributeNS(null, "x1", 0);
	testSVGNullLineElement.setAttributeNS(null, "y1", 0);
	testSVGNullLineElement.setAttributeNS(null, "x2", 0);
	testSVGNullLineElement.setAttributeNS(null, "y2", 0);
	
	// valid SVG-PolyLine-Element
	
	testSVGPolylineElement = document.createElementNS('http://www.w3.org/2000/svg', 'polyline');
	testSVGPolylineElement.setAttributeNS(null, "points", "0,0 445,5 4324, 234");
	
	// valid SVG-Polygon-Element
	
	testSVGPolygonElement = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
	testSVGPolygonElement.setAttributeNS(null, "points", "789,45 445,5 4324, 234");
	
	// valid SVG-Image-Element
	
	testSVGImageElement = document.createElementNS('http://www.w3.org/2000/svg', 'image');
	testSVGImageElement.setAttributeNS(null, "x", 1);
	testSVGImageElement.setAttributeNS(null, "y", 2);
	testSVGImageElement.setAttributeNS(null, "height", 100);
	testSVGImageElement.setAttributeNS(null, "width", 100);
	
	// XML-Element, but no SVG
	
	testNoSVGElement = document.createElementNS('http://www.w3.org/1999/xhtml/', 'html')
	
	// nonsense object
	
	testNonsenseElement = "blskfj/()&%"
}

/**
 * Resets the testSVGElements
 */
function tearDown() {
	testSVGRectElement = null;
	testSVGCircleElement = null;
	testSVGImageElement = null;
	testNoSVGElement = null;
	testNonsenseElement = null;
}

/**
 * Tests wheater the a full specified SVG can be source of a new SVGShpe object.
 */
function testNewSVGRectGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGRectElement is recognized correctly.
 * It expects, that the type property has the value 'Rect'
 */
function testNewSVGRectGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
	assertEquals('type check', testSVGShape.type, 'Rect' )
}

/**
 * Tests whether the a full specified SVGEllipse can be source of a new SVGShpe object.
 */
function testNewSVGEllipseGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGEllipseElement is recognized correctly.
 * It expects, that the type property has the value 'Ellipse'
 */
function testNewSVGEllipseGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
	assertEquals('type check', testSVGShape.type, 'Ellipse' )
}


/**
 * Tests wheater the a full specified SVG-Image-Element can be source of a new SVGShpe object.
 */
function testNewSVGImageGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGImageElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGImageElement is recognized correctly.
 * It is expected, that the type property has the value 'Rect'
 */
function testNewSVGImageGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGImageElement); 
	assertEquals('type check', testSVGShape.type, 'Rect' )
}

/**
 * Tests whether the a full specified SVGCircleElement can be source of a new SVGShpe object.
 */
function testNewSVGCircleGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGCircleElement is recognized correctly.
 * It expects, that the type property has the value 'Circle'
 */
function testNewSVGCircleGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
	assertEquals('type check', testSVGShape.type, 'Circle' )
}

/**
 * Tests whether the a full specified SVGLineElement can be source of a new SVGShpe object.
 */
function testNewSVGLineGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGLineElement is recognized correctly.
 * It expects, that the type property has the value 'Line'
 */
function testNewSVGLineGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
	assertEquals('type check', testSVGShape.type, 'Line' )
}

/**
 * Tests whether the a full specified SVGLineElement can be source of a new SVGShpe object. But the line is actually only a point.
 */
function testNewNullSVGLineGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGLineElement is recognized correctly. But the Line is actually only a point.
 * It expects, that the type property has the value 'Line'
 */
function testNewNullSVGLineGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
	assertEquals('type check', testSVGShape.type, 'Line' )
}

/**
 * Tests whether the a full specified SVGPolylineElement can be source of a new SVGShpe object.
 */
function testNewSVGPolylineGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGPolylineElement is recognized correctly.
 * It expects, that the type property has the value 'Polyline'
 */
function testNewSVGPolylineGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement); 
	assertEquals('type check', testSVGShape.type, 'Polyline' )
}

/**
 * Tests whether the a full specified SVGPolygonElement can be source of a new SVGShpe object.
 */
function testNewSVGPolygonGood() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolygonElement); 
		fail("Generate a new SVGShape object should fail here.")
	} catch(e) {
		if(!(e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Checks whether the type of a SVGPolygonElement is recognized correctly.
 * It expects, that the type property has the value 'Polyline'
 */
function testNewSVGPolygonGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolygonElement); 
	assertEquals('type check', testSVGShape.type, 'Polyline' )
}



// bad tests for rectangle 

/**
 * The x-attribute is missing. So init method should throw an exception.
 */
function testNewSVGRectBadMissingXAttribute() {

	try {
		testSVGRectElement.removeAttributeNS(null, "x")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y-attribute is missing. So init method should throw an exception.
 */
function testNewSVGRectBadMissingYAttribute() {

	try {
		testSVGRectElement.removeAttributeNS(null, "y")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The height-attribute is missing. So init method should throw an exception.
 */
function testNewSVGRectBadMissingHeightAttribute() {

	try {
		testSVGRectElement.removeAttributeNS(null, "height")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The width-attribute is missing. So init method should throw an exception.
 */
function testNewSVGRectBadMissingWidthAttribute() {

	try {
		testSVGRectElement.removeAttributeNS(null, "width")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if the delivered parameter to the constructor is no SVG-Element. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadNoSVGElement() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testNoSVGElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if the delivered parameter to the constructor is a nonsense object. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadNonsenseElement() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testNonsenseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if the delivered parameter to the constructor is a nonsense object. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadNonsenseElement() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testNonsenseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if no parameter is delivered to the constructor. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadNoConstructorParameter() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if to many parameter are delivered to the constructor. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadToManyConstructorParameters() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement, "tooMuch"); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if to many and bad parameter are delivered to the constructor. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadToManyAndBadConstructorParameters() {

	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(34, "tooMuch"); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * Tests what happens, if attributes of SVG-Elements have a wrong type. 
 * It is expected, that an error occurs.
 */
function testNewSVGRectBadWrongSVGElementAttributeTypes() {

	try {
		testSVGRectElement.setAttributeNS(null, "height", "%$u(")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement, "tooMuch"); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


// bad tests for circle

/**
 * The cx-attribute is missing. So init method should throw an exception.
 */
function testNewSVGCircleBadMissingCXAttribute() {

	try {
		testSVGCircleElement.removeAttributeNS(null, "cx")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The cy-attribute is missing. So init method should throw an exception.
 */
function testNewSVGCircleBadMissingCYAttribute() {

	try {
		testSVGCircleElement.removeAttributeNS(null, "cy")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The r-attribute is missing. So init method should throw an exception.
 */
function testNewSVGCircleBadMissingRAttribute() {

	try {
		testSVGCircleElement.removeAttributeNS(null, "r")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The cx-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGCircleBadCXAttributeValueType() {

	try {
		testSVGCircleElement.setAttributeNS(null, "cx", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The cy-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGCircleBadCYAttributeValueType() {

	try {
		testSVGCircleElement.setAttributeNS(null, "cy", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The r-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGCircleBadRAttributeValueType() {

	try {
		testSVGCircleElement.setAttributeNS(null, "r", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


// bad tests for ellipse

/**
 * The cx-attribute is missing. So init method should throw an exception.
 */
function testNewSVGEllipseBadMissingCXAttribute() {

	try {
		testSVGEllipseElement.removeAttributeNS(null, "cx")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The cy-attribute is missing. So init method should throw an exception.
 */
function testNewSVGEllipseBadMissingCYAttribute() {

	try {
		testSVGEllipseElement.removeAttributeNS(null, "cy")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The ry-attribute is missing. So init method should throw an exception.
 */
function testNewSVGEllipseBadMissingRYAttribute() {

	try {
		testSVGEllipseElement.removeAttributeNS(null, "ry")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The rx-attribute is missing. So init method should throw an exception.
 */
function testNewSVGEllipseBadMissingRXAttribute() {

	try {
		testSVGEllipseElement.removeAttributeNS(null, "rx")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}



/**
 * The cx-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGEllipseBadCXAttributeValueType() {

	try {
		testSVGEllipseElement.setAttributeNS(null, "cx", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The cy-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGEllipseBadCYAttributeValueType() {

	try {
		testSVGEllipseElement.setAttributeNS(null, "cy", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The ry-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGEllipseBadRYAttributeValueType() {

	try {
		testSVGEllipseElement.setAttributeNS(null, "ry", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The rx-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGEllipseBadRYAttributeValueType() {

	try {
		testSVGEllipseElement.setAttributeNS(null, "rx", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


// bad tests for line

/**
 * The x1-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingX1Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "x1")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y1-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingY1Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "y1")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The x2-attribute is missing. So init methode sould throw an exception.
 */
function testNewSVGLineBadMissingX2Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "x2")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y2-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingY2Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "y2")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


/**
 * The x1-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGLineBadX1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "x1", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y1-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGLineBadY1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "y1", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The x2-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGLineBadX1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "x2", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y2-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGLineBadY2AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "y2", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


// bad tests for Polyline/Polygon

/**
 * The x1-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingX1Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "x1")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y1-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingY1Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "y1")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The x2-attribute is missing. So init method should throw an exception.
 */
function testNewSVGPolylineBadMissingAttribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "x2")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y2-attribute is missing. So init method should throw an exception.
 */
function testNewSVGLineBadMissingY2Attribute() {

	try {
		testSVGLineElement.removeAttributeNS(null, "y2")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}


/**
 * The x1-attribute value has a wrong type. So init methode should throw an exception.
 */
function testNewSVGLineBadX1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "x1", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y1-attribute value has a wrong type. So init methode should throw an exception.
 */
function testNewSVGLineBadY1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "y1", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The x2-attribute value has a wrong type. So init methode should throw an exception.
 */
function testNewSVGLineBadX1AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "x2", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The y2-attribute value has a wrong type. So init methode should throw an exception.
 */
function testNewSVGLineBadY2AttributeValueType() {

	try {
		testSVGLineElement.setAttributeNS(null, "y2", "a?")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}
