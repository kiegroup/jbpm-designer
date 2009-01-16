// mocks
// mocking ORYX.Editor object
if(!ORYX) {var ORYX = {};}
if(!ORYX.Editor) {ORYX.Editor = {};}

// stubs

// stubbing ORYX.Editor.checkClassType

ORYX.Editor.checkClassType = function( classInst, classType ) {
		return classInst instanceof classType
}

//var testSVGRectElement = null;
//var x = null;
//var y = null;
//var cy = null;
//var cx = null;
//var r = null;
//var x1 = null;
//var y1 = null;
//var x2 = null;
//var y2 = null;
//var height = null;
//var width = null
//var ry = null;
//var rx = null;
//var points = null;
//var lessPoints = null;


/**
 * builds up testSVGElements
 */

function setUp(){
	
	// attribute definitions
	x = 1
	y = 2
	cy = 3
	cx = 4
	r = 5
	rx = 10
	ry = 11
	x1 = 6 // lower than x2
	y1 = 7 // lower than y2
	x2 = 8
	y2 = 9
	height = 100
	width = 101
	pointsX = new Array(1,2,3)
	pointsY = new Array(4,5,6) 
	points = "" + pointsX[0] +","+pointsY[0]+" "+ pointsX[1] +","+pointsY[1]+" "+ pointsX[2] +","+pointsY[2]
	lessPoints = "" + pointsX[0] +","+pointsY[0]+" "+ pointsX[1] +","+pointsY[1]
	
	// valid SVG-Rect-Element
	
	testSVGRectElement = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
	testSVGRectElement.setAttributeNS(null, "x", x);
	testSVGRectElement.setAttributeNS(null, "y", y);
	testSVGRectElement.setAttributeNS(null, "height", height);
	testSVGRectElement.setAttributeNS(null, "width", width);
	
	// valid SVG-Circle-Element
	
	testSVGCircleElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
	testSVGCircleElement.setAttributeNS(null, "cx", cx);
	testSVGCircleElement.setAttributeNS(null, "cy", cy);
	testSVGCircleElement.setAttributeNS(null, "r", r);
	
	// valid SVG-Ellipse-Element
	
	testSVGEllipseElement = document.createElementNS('http://www.w3.org/2000/svg', 'ellipse');
	testSVGEllipseElement.setAttributeNS(null, "cx", cx);
	testSVGEllipseElement.setAttributeNS(null, "cy", cy);
	testSVGEllipseElement.setAttributeNS(null, "ry", ry);
	testSVGEllipseElement.setAttributeNS(null, "rx", rx);
	
	// valid SVG-Line-Element
	
	testSVGLineElement = document.createElementNS('http://www.w3.org/2000/svg', 'line');
	testSVGLineElement.setAttributeNS(null, "x1", x1);
	testSVGLineElement.setAttributeNS(null, "y1", y1);
	testSVGLineElement.setAttributeNS(null, "x2", x2);
	testSVGLineElement.setAttributeNS(null, "y2", y2);
	
	// valid SVG-Line-Element (only a point)
	
	testSVGNullLineElement = document.createElementNS('http://www.w3.org/2000/svg', 'line');
	testSVGNullLineElement.setAttributeNS(null, "x1", 0);
	testSVGNullLineElement.setAttributeNS(null, "y1", 0);
	testSVGNullLineElement.setAttributeNS(null, "x2", 0);
	testSVGNullLineElement.setAttributeNS(null, "y2", 0);
	
	// valid SVG-PolyLine-Element
	
	testSVGPolylineElement = document.createElementNS('http://www.w3.org/2000/svg', 'polyline');
	testSVGPolylineElement.setAttributeNS(null, "points", points);
	
	// valid SVG-Polygon-Element
	
	testSVGPolygonElement = document.createElementNS('http://www.w3.org/2000/svg', 'polygon');
	testSVGPolygonElement.setAttributeNS(null, "points", points);
	
	// valid SVG-Image-Element
	
	testSVGImageElement = document.createElementNS('http://www.w3.org/2000/svg', 'image');
	testSVGImageElement.setAttributeNS(null, "x", x);
	testSVGImageElement.setAttributeNS(null, "y", y);
	testSVGImageElement.setAttributeNS(null, "height", height);
	testSVGImageElement.setAttributeNS(null, "width", width);
	
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


//  Good tests for rectangle

/**
 * Tests whether the a full specified SVG can be source of a new SVGShpe object.
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
 * This test checks whether the attribute values of a rectangle are parsed correctly.
 */

function testNewSVGRectGoodParsedValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	assertEquals('xAttr value check', testSVGShape.oldX, x)
	assertEquals('yAttr value check', testSVGShape.oldY, y)
	assertEquals('heightAttr value check', testSVGShape.height, height)
	assertEquals('widthAttr value check', testSVGShape.width, width)
}

/**
 * This test checks whether the oldXXX attribute values of a rectangle are equal.
 */

function testNewSVGRectGoodOldXXXValuesEqualXXXValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	assertEquals('xAttr value equal check', testSVGShape.oldX, testSVGShape.x)
	assertEquals('yAttr value equal check', testSVGShape.oldY, testSVGShape.y)
	assertEquals('heightAttr value equal check', testSVGShape.oldHeight, testSVGShape.height)
	assertEquals('xAttr value equal check', testSVGShape.oldWidth, testSVGShape.width)
}


//  Good tests for ellipse

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
 * This test checks whether the attribute values of a ellipse are parsed correctly.
 */

function testNewSVGEllipseGoodParsedValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	assertEquals('rAttr value check', testSVGShape.radius, r)
	assertEquals('xAttr value check', testSVGShape.oldX, cx-rx)
	assertEquals('yAttr value check', testSVGShape.oldY, cy-ry)
	assertEquals('heightAttr value check', testSVGShape.oldHeight, 2*ry)
	assertEquals('widthAttr value check', testSVGShape.oldWidth, 2*rx)
}

/**
 * This test checks whether the oldXXX attribute values of a ellipse are equal.
 */

function testNewSVGEllipseGoodOldXXXValuesEqualXXXValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	assertEquals('xAttr value equal check', testSVGShape.oldX, testSVGShape.x)
	assertEquals('yAttr value equal check', testSVGShape.oldY, testSVGShape.y)
	assertEquals('heightAttr value equal check', testSVGShape.oldHeight, testSVGShape.height)
	assertEquals('xAttr value equal check', testSVGShape.oldWidth, testSVGShape.width)
}


//  Good tests for image

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
 * This test checks whether the attribute values of a image are parsed correctly.
 */

function testNewSVGImageGoodParsedValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGImageElement);
	assertEquals('xAttr value check', testSVGShape.oldX, x)
	assertEquals('yAttr value check', testSVGShape.oldY, y)
	assertEquals('heightAttr value check', testSVGShape.oldHeight, height)
	assertEquals('widthAttr value check', testSVGShape.oldWidth, width)
}

/**
 * This test checks whether the oldXXX attribute values of a image are equal.
 */

function testNewSVGImageGoodOldXXXValuesEqualXXXValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGImageElement);
	assertEquals('xAttr value equal check', testSVGShape.oldX, testSVGShape.x)
	assertEquals('yAttr value equal check', testSVGShape.oldY, testSVGShape.y)
	assertEquals('heightAttr value equal check', testSVGShape.oldHeight, testSVGShape.height)
	assertEquals('xAttr value equal check', testSVGShape.oldWidth, testSVGShape.width)
}

/**
 * Checks whether the type of a SVGImageElement is recognized correctly.
 * It is expected, that the type property has the value 'Rect'
 */
function testNewSVGImageGoodType() {
	
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGImageElement); 
	assertEquals('type check', testSVGShape.type, 'Rect' )
}


//  Good tests for circle

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
 * This test checks whether the attribute values of a cricle are parsed correctly.
 */

function testNewSVGCircleGoodParsedValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
	assertEquals('rAttr value check', testSVGShape.radius, r)
	assertEquals('xAttr value check', testSVGShape.oldX, cx-r)
	assertEquals('yAttr value check', testSVGShape.oldY, cy-r)
	assertEquals('heightAttr value check', testSVGShape.height, 2*r)
	assertEquals('widthAttr value check', testSVGShape.width, 2*r)
}

/**
 * This test checks whether the oldXXX attribute values of a circle are equal.
 */

function testNewSVGCircleGoodOldXXXValuesEqualXXXValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
	assertEquals('xAttr value equal check', testSVGShape.oldX, testSVGShape.x)
	assertEquals('yAttr value equal check', testSVGShape.oldY, testSVGShape.y)
	assertEquals('heightAttr value equal check', testSVGShape.oldHeight, testSVGShape.height)
	assertEquals('xAttr value equal check', testSVGShape.oldWidth, testSVGShape.width)
}


//  Good tests for line

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
 * This test checks whether the attribute values of a line are parsed correctly.
 */

function testNewSVGLineGoodParsedValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	assertEquals('xAttr value check', testSVGShape.oldX, x1)
	assertEquals('yAttr value check', testSVGShape.oldY, y1)
	assertEquals('heightAttr value check', testSVGShape.height, x2-x1 )
	assertEquals('widthAttr value check', testSVGShape.width, y2-y1)
}

/**
 * This test checks whether the oldXXX attribute values of a line are equal.
 */

function testNewSVGLineGoodOldXXXValuesEqualXXXValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	assertEquals('xAttr value equal check', testSVGShape.oldX, testSVGShape.x)
	assertEquals('yAttr value equal check', testSVGShape.oldY, testSVGShape.y)
	assertEquals('heightAttr value equal check', testSVGShape.oldHeight, testSVGShape.height)
	assertEquals('xAttr value equal check', testSVGShape.oldWidth, testSVGShape.width)
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


// Good tests for polyline/polygon

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
 * The x2-attribute is missing. So init method should throw an exception.
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
 * The points-attribute is missing. So init method should throw an exception.
 */
function testNewSVGPolylineBadMissingPointsAttribute() {

	try {
		testSVGPolyline.removeAttributeNS(null, "points")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The points-attribute is missing. So init method should throw an exception.
 */
function testNewSVGPolygonBadMissingPointsAttribute() {

	try {
		testSVGPolylgon.removeAttributeNS(null, "points")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolygonElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The points-attribute value has a wrong type. So init method should throw an exception.
 */
function testNewSVGPolylineBadPointsAttributeValueType() {

	try {
		testSVGPolylineElement.setAttributeNS(null, "points", "a?,4 uz,?= 78,9r")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}

/**
 * The delivered Polygon only consists of tow points. So init method should throw an exception.
 */
function testNewSVGPolygonBadTooLessPoints() {

	try {
		testSVGPolygonElement.setAttributeNS(null, "points", "1,1 45,56")
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement); 
		fail("Generate a new SVGShape object should fail yet.")
	} catch(e) {
		if((e instanceof JsUnitException)){
			throw e;
		}
	}	
}
