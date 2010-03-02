var pathLineAbsD = "M100,10 L100,10 40,180 z";
var pathCubicBezierAbsD = "M40,140 L40,100 10,100 C10,10 90,10 90,100";
var pathQuadraticBezierAbsD = "M50,50 Q-30,100 50,150 100,230 160,170";
var pathEllipticArcAbsD = "M10,150 A15 15 180 0 1 70 140 A15 25 180 0 0 130 130";

var pathLineRelD = "M100,10 l100,10 40,180 z";
var pathCubicBezierRelD = "M40,140 l40,100 10,100 c10,10 90,10 90,100";
var pathQuadraticBezierRelD = "M50,50 q-30,100 50,150 100,230 160,170";
var pathEllipticArcRelD = "M10,150 a15 15 180 0 1 70 140 a15 25 180 0 0 130 130";

/**
 * Test ORYX.Core.SVG.EditPathHandler for a line path with absolute coordinates
 */
function testEditLineAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathLineAbsD);

	assertEquals(" M160 8  L160 8  L70 25  z", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for a cubic Bezier path with absolute coordinates
 */
function testEditCubicBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierAbsD);

	assertEquals(" M70 21  L70 17  L25 17  C25 8 145 8 145 17 ", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for a quadratic Bezier path with absolute coordinates
 */
function testEditQuadraticBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierAbsD);
	
	assertEquals(" M85 12  Q-35 17 85 22  Q160 30 250 24 ", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for an elliptic arc path with absolute coordinates
 */
function testEditEllipticArcAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcAbsD);

	assertEquals(" M25 22  A22.5 1.5 180 0 1 115 21  A22.5 2.5 180 0 0 205 20 ", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for a line path with relative coordinates
 */
function testEditLineRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathLineRelD);

	assertEquals(" M160 8  l150 1  l60 18  z", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for a cubic Bezier path with relative coordinates
 */
function testEditCubicBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierRelD);

	assertEquals(" M70 21  l60 10  l15 10  c15 1 135 1 135 10 ", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for a quadratic Bezier path with relative coordinates
 */
function testEditQuadraticBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierRelD);

	assertEquals(" M85 12  q-45 10 75 15  q150 23 240 17 ", handler.d);
}

/**
 * Test ORYX.Core.SVG.EditPathHandler for an elliptic arc path with relative coordinates
 */
function testEditEllipticArcRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.EditPathHandler();
	handler.init(40, 10, 20, 30, 1.5, 0.1);
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcRelD);

	assertEquals(" M25 22  a22.5 1.5 180 0 1 105 14  a22.5 2.5 180 0 0 195 13 ", handler.d);
}