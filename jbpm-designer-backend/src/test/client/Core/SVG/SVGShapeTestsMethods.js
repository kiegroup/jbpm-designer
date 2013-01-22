// Test update method

// rectangle
/**
 * Changing the x-Attribute, should result in a adjusted x-value of the DOM-Element.
 */
function testRectChangedXValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	testSVGShape.x = 99
	testSVGShape.update()
	var xAttr = testSVGRectElement.getAttributeNS(null, 'x')
	
	assertEquals('x and oldX should be equal now again.', testSVGShape.x, testSVGShape.oldX)
	assertEquals('x and xAttr in SVGElement should be equal now again.', testSVGShape.x, parseFloat(xAttr))

}

/**
 * Changing the y-Attribute, should result in a adjusted y-value of the DOM-Element.
 */
function testRectChangedYValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	testSVGShape.y = 99
	testSVGShape.update()
	var yAttr = testSVGRectElement.getAttributeNS(null, 'y')
	
	assertEquals('y and oldY should be equal now again.', testSVGShape.y, testSVGShape.oldY)
	assertEquals('y and yAttr in SVGElement should be equal now again.', testSVGShape.y, parseFloat(yAttr))

}

/**
 * Changing the width-Attribute, should result in a adjusted width-value of the DOM-Element.
 */
function testRectChangedWidthValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	testSVGShape.width = 99
	testSVGShape.update()
	var widthAttr = testSVGRectElement.getAttributeNS(null, 'width')
	
	assertEquals('width and oldWidth should be equal now again.', testSVGShape.width, testSVGShape.oldWidth)
	assertEquals('width and widthAttr in SVGElement should be equal now again.', testSVGShape.width, parseFloat(widthAttr))
}

/**
 * Changing the height-Attribute, should result in a adjusted height-value of the DOM-Element.
 */
function testRectChangedHeightValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	testSVGShape.height = 99
	testSVGShape.update()
	var heightAttr = testSVGRectElement.getAttributeNS(null, 'height')
	
	assertEquals('height and oldHeight should be equal now again.', testSVGShape.height, testSVGShape.oldHeight)
	assertEquals('height and heightAttr in SVGElement should be equal now again.', testSVGShape.height, parseFloat(heightAttr))
}

/**
 * Tests if multiple changes at the same time handled.
 */
function testRectChangedMultipleValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement);
	testSVGShape.height = 99
	testSVGShape.width = 98
	testSVGShape.x = 97
	testSVGShape.y = 96
	testSVGShape.update()
	var heightAttr = testSVGRectElement.getAttributeNS(null, 'height')
	var widthAttr = testSVGRectElement.getAttributeNS(null, 'width')
	var xAttr = testSVGRectElement.getAttributeNS(null, 'x')
	var yAttr = testSVGRectElement.getAttributeNS(null, 'y')
	
	assertEquals('height and oldHeight should be equal now again.', testSVGShape.height, testSVGShape.oldHeight)
	assertEquals('height and heightAttr in SVGElement should be equal now again.', testSVGShape.height, parseFloat(heightAttr))
	
	assertEquals('width and oldWidth should be equal now again.', testSVGShape.width, testSVGShape.oldWidth)
	assertEquals('width and widthAttr in SVGElement should be equal now again.', testSVGShape.width, parseFloat(widthAttr))
	
	assertEquals('x and oldX should be equal now again.', testSVGShape.x, testSVGShape.oldX)
	assertEquals('x and xAttr in SVGElement should be equal now again.', testSVGShape.x, parseFloat(xAttr))
	
	assertEquals('y and oldY should be equal now again.', testSVGShape.y, testSVGShape.oldY)
	assertEquals('y and yAttr in SVGElement should be equal now again.', testSVGShape.y, parseFloat(yAttr))
}

// circle

/**
 * Changing the height-Attribute, should result in adjusted radius, cy, cx values of the SVGCircle DOM-Element.
 * It is expected, that the initial radius is lower than 1000/2.
 */
function testCircleChangedHeightValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
	var oldX = testSVGShape.x
	var oldY = testSVGShape.y
	
	testSVGShape.height = 1000
	testSVGShape.update()
	
	assertEquals('height and oldHeight should be equal now again', testSVGShape.height, testSVGShape.oldHeight)
	assertEquals('height and width should be equal now again', testSVGShape.oldHeight, testSVGShape.oldWidth )
	assertEquals('radius should height/2', testSVGShape.radius, testSVGShape.height/2)
	
	var cxAttr = this.element.getAttributeNS(null, "cx")
	var cyAttr = this.element.getAttributeNS(null, "cy")
	var rAttr = this.element.getAttributeNS(null, "r")
	
	assertEquals('parsed radius should be heigth/2', testSVGShape.height/2, parseFloat(rAttr))
	assertEquals('parsed cx should be oldx + radius', oldX + testSVGShape.radius, parseFloat(cx))
	assertEquals('parsed cy should be oldy + radius', oldY + testSVGShape.radius, parseFloat(cy))
}

/**
 * Changing the width-Attribute, should result in adjusted radius, cy, cx values of the SVGCircle DOM-Element.
 * It is expected, that the initial radius is lower than 1000/2.
 */
function testCircleChangedWidthValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
	var oldX = testSVGShape.x
	var oldY = testSVGShape.y
	
	testSVGShape.width = 1000
	testSVGShape.update()
	
	assertEquals('width and oldWidth should be equal now again', testSVGShape.width, testSVGShape.oldWidth)
	assertEquals('height and width should be equal now again', testSVGShape.oldHeight, testSVGShape.oldWidth )
	assertEquals('radius should height/2', testSVGShape.radius, testSVGShape.width/2)
	
	var cxAttr = this.element.getAttributeNS(null, "cx")
	var cyAttr = this.element.getAttributeNS(null, "cy")
	var rAttr = this.element.getAttributeNS(null, "r")
	
	assertEquals('parsed radius should be heigth/2', testSVGShape.height/2, parseFloat(rAttr))
	assertEquals('parsed cx should be oldx + radius', oldX + testSVGShape.radius, parseFloat(cx))
	assertEquals('parsed cy should be oldy + radius', oldY + testSVGShape.radius, parseFloat(cy))
}

/**
 * If height is set to a NaN-value, it should throw an exception.
 */
function testFailForNaNHeightValue() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
		testSVGShape.height = "NaN"
		testSVGShape.update()
		fail("Test should fail yet, with NaNException.")
	} catch(e) {
		if ((e instanceof JsUnitException)) {
			throw e;			
		}
	}
}

/**
 * If width is set to a NaN-value, it should throw an exception.
 */
function testFailForNaNWidthValue() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGCircleElement);
		testSVGShape.width = "NaN"
		testSVGShape.update()
		fail("Test should fail yet, with NaNException.")
	} catch(e) {
		if ((e instanceof JsUnitException)) {
			throw e;			
		}
	}
}

// ellipse

/**
 * A changed height value should result in a adjusted y-radius and cy value.
 */
function testNewHeightEllipse() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	testSVGShape.height = 1000
	var y = testSVGShape.y
	testSVGShape.update()
	
	// get new values
	var radiusY = testSVGEllipseElement.getAttributeNS(null, "ry")
	var cy = testSVGEllipseElement.getAttributeNS(null, "cy")
	
	assertEquals('The y-radius should be 500', parseFloat(radiusY), 500)
	assertEquals('The cy should be y+500', parseFloat(cy), y+500)
}

/**
 * A changed width value should result in a adjusted x-radius and cx value.
 */
function testNewWidthEllipse() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	testSVGShape.width = 1000
	var x = testSVGShape.x
	testSVGShape.update()
	
	// get new values
	var radiusX = testSVGEllipseElement.getAttributeNS(null, "rx")
	var cx = testSVGEllipseElement.getAttributeNS(null, "cx")
	
	assertEquals('The x-radius should be 500', parseFloat(radiusX), 500)
	assertEquals('The cx should be x+500', parseFloat(cx), x+500)
}

/**
 * If only the height value changes, the width value should be unchanged.
 */
function testEllipseUnchangedWidth() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	var width = testSVGShape.width;
	
	testSVGShape.height = 1000
	testSVGShape.update()
	
	assertEquals('Width should be unchanged', width, testSVGShape.width)
}

/**
 * If only the width value changes, the height value should be unchanged.
 */
function testEllipseUnchangedHeight() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGEllipseElement);
	var height = testSVGShape.height;
	
	testSVGShape.width = 1000
	testSVGShape.update()
	
	assertEquals('Height should be unchanged', height, testSVGShape.height)
}

// line

/**
 * A changed x value should result in changed x1 and x2 values.
 */
function testLineNewXValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	testSVGShape.x = 1000
	testSVGShape.update()
	
	var x1 = testSVGLineElement.getAttributeNS(null, "x1")
	var x2 = testSVGLineElement.getAttributeNS(null, "x2")
	
	assertEquals('x1 should equals x', parseFloat(x1), 1000)
	assertEquals('x2 should equals x + widht', parseFloat(x2), 1000+testSVGShape.width)
}

/**
 * If the new x-value is NaN, it should throw an exception.
 */
function testNaNXValue() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
		testSVGShape.x = "NaN"
		testSVGShape.update()
		fail('Test should fail yet with a NaNException.')
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * If the new y-value is NaN, it should throw an exception.
 */
function testNaNYValue() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
		testSVGShape.y = "NaN"
		testSVGShape.update()
		fail('Test should fail yet with a NaNException.')
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * A changed y value should result in changed y1 and y2 values.
 */
function testLineNewYValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	testSVGShape.y = 1000
	testSVGShape.update()
	
	var y1 = testSVGLineElement.getAttributeNS(null, "y1")
	var y2 = testSVGLineElement.getAttributeNS(null, "y2")
	
	assertEquals('y1 should equals y', parseFloat(y1), 1000)
	assertEquals('y2 should equals y + height', parseFloat(y2), 1000+testSVGShape.height)
}

/**
 * A changed width value should result in changed x2 value.
 */
function testLineNewWidthValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	var x = testSVGShape.x
	var y = testSVGShape.y
	var height = testSVGShape.height
	
	testSVGShape.width = 1000
	testSVGShape.update()
	
	var y1 = testSVGLineElement.getAttributeNS(null, "y1")
	var y2 = testSVGLineElement.getAttributeNS(null, "y2")
	var x2 = testSVGLineElement.getAttributeNS(null, "x2")
	var x1 = testSVGLineElement.getAttributeNS(null, "x1")
	
	assertEquals('x1 should be unchanged', x, parseFloat(x1))
	assertEquals('y1 should be unchanged', y, parseFloat(y1))
	assertEquals('x2 should equals x+1000', parseFloat(x2), 1000+x)
	assertEquals('y2 should be unchanged', parseFloat(y2), y+height)
}

/**
 * If negative value are passed for width or height, it should work.
 */
function testNegativeWidth() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
		testSVGShape.width = -1
		testSVGShape.update()
		fail("Test should fail yet for negative width value")
	} catch (e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * If negative value are passed for width or height, it should work.
 */
function testNegativeHeight() {
	try {
		var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
		testSVGShape.height = -1
		testSVGShape.update()
		fail("Test should fail yet for negative height value")
	} catch (e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
	
}

/**
 * A changed height value should result in changed y2 value.
 */
function testLineNewHeightValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGLineElement);
	var x = testSVGShape.x
	var y = testSVGShape.y
	var width = testSVGShape.width
	
	testSVGShape.height = 1000
	testSVGShape.update()
	
	var y1 = testSVGLineElement.getAttributeNS(null, "y1")
	var y2 = testSVGLineElement.getAttributeNS(null, "y2")
	var x2 = testSVGLineElement.getAttributeNS(null, "x2")
	var x1 = testSVGLineElement.getAttributeNS(null, "x1")
	
	assertEquals('x1 should be unchanged', x, parseFloat(x1))
	assertEquals('y1 should be unchanged', y, parseFloat(y1))
	assertEquals('x2 should be unchanged', parseFloat(x2), x+width)
	assertEquals('y2 should equals y +1000', parseFloat(y2), y+1000)
}

// polyline

/**
 * Given: x=1 y=4 width=2 height=3 points=1,4 2,5 3,7
 * A new height value should scale in vertical direction.
 */
function testPolylineNewHeightValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	
	testSVGShape.height = 9
	testSVGShape.update()
	var points = testSVGPolylineElement.getAttributeNS(null, "points");
	points = points.replace(/,/g, " ").split(" ").without("");
	
	// test point values
	assertEquals('x1 should be unchanged', parseFloat(points[0]), 1)
	assertEquals('x2 should be unchanged', parseFloat(points[2]), 2)
	assertEquals('x3 should be unchanged', parseFloat(points[4]), 3)
	
	assertEquals('y1 should be scaled', parseFloat(points[1]), 4)
	assertEquals('y2 should be scaled', parseFloat(points[3]), 7)
	assertEquals('y3 should be scaled', parseFloat(points[5]), 13)
}

/**
 * Given: x=1 y=4 width=2 height=3 points=1,4 2,5 3,7
 * A new width value should scale in horizontal direction.
 */
function testPolylineNewWidthValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	
	testSVGShape.width = 10
	testSVGShape.update()
	
	var points = testSVGPolylineElement.getAttributeNS(null, "points");
	points = points.replace(/,/g, " ").split(" ").without("");
	
	// test point values
	assertEquals('x1 should be scaled', parseFloat(points[0]), 1)
	assertEquals('x2 should be scaled', parseFloat(points[2]), 6)
	assertEquals('x3 should be scaled', parseFloat(points[4]), 11)
	
	assertEquals('y1 should be unchanged', parseFloat(points[1]), 4)
	assertEquals('y2 should be unchanged', parseFloat(points[3]), 5)
	assertEquals('y3 should be unchanged', parseFloat(points[5]), 7)
}

/**
 * Given: x=1 y=4 width=2 height=3 points=1,4 2,5 3,7
 * A new y value should translate in vertical direction.
 */
function testPolylineNewYValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	
	testSVGShape.y = 10
	testSVGShape.update()
	var points = testSVGPolylineElement.getAttributeNS(null, "points");
	points = points.replace(/,/g, " ").split(" ").without("");
	
	// test point values
	assertEquals('x1 should be unchanged', parseFloat(points[0]), 1)
	assertEquals('x2 should be unchanged', parseFloat(points[2]), 2)
	assertEquals('x3 should be unchanged', parseFloat(points[4]), 3)
	
	assertEquals('y1 should be tanslated', parseFloat(points[1]), 10)
	assertEquals('y2 should be tanslated', parseFloat(points[3]), 11)
	assertEquals('y3 should be tanslated', parseFloat(points[5]), 13)
}

/**
 * Given: x=1 y=4 width=2 height=3 points=1,4 2,5 3,7
 * A new x value should result in a translation in horizontal direction.
 */
function testPolylineNewXValue() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	testSVGShape.x = 10
	testSVGShape.update()
	
	var points = testSVGPolylineElement.getAttributeNS(null, "points");
	points = points.replace(/,/g, " ").split(" ").without("");
	
	// test point values
	assertEquals('x1 should be translated', parseFloat(points[0]), 10)
	assertEquals('x2 should be translated', parseFloat(points[2]), 11)
	assertEquals('x3 should be translated', parseFloat(points[4]), 12)
	
	assertEquals('y1 should be unchanged', parseFloat(points[1]), 4)
	assertEquals('y2 should be unchanged', parseFloat(points[3]), 5)
	assertEquals('y3 should be unchanged', parseFloat(points[5]), 7)
}

/**
 * A corrupt points attribute should result in an exception.
 */
function testFailForCorruptPointsAttribute() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	testSVGPolylineElement.setAttributeNS(null, 'points', "1,4 2,5 3,6 5,")
	testSVGShape.widht = 1000
	try {
		testSVGShape.update()
		fail('Test should fail yet for corrupt points attribute.')
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * Update methode should not fail if width oer oldWidth == 0
 */
function testPolylineZeroValueInWidth() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	testSVGShape.widht = 0
	// test for width == 0
	try {
		testSVGShape.update()
		fail('Test should not fail yet for 0-value.')
	} catch(e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
	
	// now oldWidth == 0
	testSVGShape.width = 5
	try {
		testSVGShape.update()
		fail('Test should not fail yet for 0-value.')
	} catch(e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 */
function testPolylineZeroValueInHeight() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPolylineElement);
	testSVGShape.heigth = 0
	// test for heigth == 0
	try {
		testSVGShape.update()
		fail('Test should not fail yet for 0-value.')
	} catch(e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
	
	// now oldHeigth == 0
	testSVGShape.heigth = 5
	try {
		testSVGShape.update()
		fail('Test should not fail yet for 0-value.')
	} catch(e) {
		if(!(e instanceof JsUnitException)) {
			throw e
		}
	}
}

// path

///**
// * Update methode should not fail if width oer oldWidth == 0
// */
//function testPathZeroValueInWidth() {
//	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPathElement);
//	testSVGShape.widht = 0
//	// test for width == 0
//	try {
//		testSVGShape.update()
//		fail('Test should not fail yet for 0-value.')
//	} catch(e) {
//		if(!(e instanceof JsUnitException)) {
//			throw e
//		}
//	}
//	
//	// now oldWidth == 0
//	testSVGShape.width = 5
//	try {
//		testSVGShape.update()
//		fail('Test should not fail yet for 0-value.')
//	} catch(e) {
//		if(!(e instanceof JsUnitException)) {
//			throw e
//		}
//	}
//}
//
///**
// * Update methode should not fail if height oer oldHeight == 0
// */
//function testPathZeroValueInHeight() {
//	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGPathElement);
//	testSVGShape.heigth = 0
//	// test for heigth == 0
//	try {
//		testSVGShape.update()
//		fail('Test should not fail yet for 0-value.')
//	} catch(e) {
//		if(!(e instanceof JsUnitException)) {
//			throw e
//		}
//	}
//	
//	// now oldHeigth == 0
//	testSVGShape.heigth = 5
//	try {
//		testSVGShape.update()
//		fail('Test should not fail yet for 0-value.')
//	} catch(e) {
//		if(!(e instanceof JsUnitException)) {
//			throw e
//		}
//	}
//}

// isPointIncluded
// only need to test for rect. Others tested in Math tests.
/**
 * Should fail, if passed values are NaN.
 */
function testIsPointIncludedFailForNaNValues() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement)
	try {
		testSVGShape.isPointIncluded("NaN","Nan")
		fail("Test should fail yet for NaN-Values.")
	} catch(e) {
		if((e instanceof JsUnitException)) {
			throw e
		}
	}
}

/**
 * Test for a point, that is outside the rectagle
 */
function testIsPointIncludedPointOutside() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement)
	assertFalse(testSVGShape.isPointIncluded(1000,5000))
}

/**
 * Test for a point, that is outside the rectagle
 */
function testIsPointIncludedPointOutside() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement)
	assertTrue(testSVGShape.isPointIncluded(50,60))
}

/**
 * Test for a point, that is on the line.
 */
function testIsPointIncludedPointOnLine() {
	var testSVGShape = new ORYX.Core.SVG.SVGShape(testSVGRectElement)
	assertTrue(testSVGShape.isPointIncluded(testSVGShape.x,testSVGShape.y))
}








































