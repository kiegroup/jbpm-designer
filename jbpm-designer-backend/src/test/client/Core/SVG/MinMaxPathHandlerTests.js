var pathLineAbsD = "M100,10 L100,10 40,180 190,60 10,60 160,180 z";
var pathCubicBezierAbsD = "M40,140 L40,100 10,100 C10,10 90,10 90,100 L60,100 60,140 M140,50 C70,180 195,180 190,100";
var pathQuadraticBezierAbsD = "M50,50 Q-30,100 50,150 100,230 150,150 230,100 150,50 100,-30 50,50";
var pathEllipticArcAbsD = "M10,150 A15 15 180 0 1 70 140 A15 25 180 0 0 130 130 A15 55 180 0 1 190 120";

var pathLineRelD = "M100,10 l100,10 40,180 190,60 10,60 160,180 z";
var pathCubicBezierRelD = "M40,140 l40,100 10,100 c10,10 90,10 90,100 l60,100 60,140 m140,50 c0,180 195,180 190,100";
var pathQuadraticBezierRelD = "M50,50 q-30,100 50,150 100,230 150,150 230,100 150,50 100,-30 50,50";
var pathEllipticArcRelD = "M10,150 a15 15 180 0 1 70 140 a15 25 180 0 0 130 130 a15 55 180 0 1 190 120";

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a line path with absolute coordinates
 */
function testCalculateMinMaxLineAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathLineAbsD);

	assertEquals(10, handler.minX);
	assertEquals(10, handler.minY);
	assertEquals(190, handler.maxX);
	assertEquals(180, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a cubic Bezier path with absolute coordinates
 */
function testCalculateMinMaxCubicBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierAbsD);

	assertEquals(10, handler.minX);
	assertEquals(10, handler.minY);
	assertEquals(195, handler.maxX);
	assertEquals(180, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a quadratic Bezier path with absolute coordinates
 */
function testCalculateMinMaxQuadraticBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierAbsD);

	assertEquals(-30, handler.minX);
	assertEquals(-30, handler.minY);
	assertEquals(230, handler.maxX);
	assertEquals(230, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a elliptic arc path with absolute coordinates
 */
function testCalculateMinMaxEllipticArcAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcAbsD);

	assertEquals(10, handler.minX);
	assertEquals(120, handler.minY);
	assertEquals(190, handler.maxX);
	assertEquals(150, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a line path with relative coordinates
 */
function testCalculateMinMaxLineRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathLineRelD);

	assertEquals(100, handler.minX);
	assertEquals(10, handler.minY);
	assertEquals(600, handler.maxX);
	assertEquals(500, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a cubic Bezier path with relative coordinates
 */
function testCalculateMinMaxCubicBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierRelD);

	assertEquals(40, handler.minX);
	assertEquals(140, handler.minY);
	assertEquals(635, handler.maxX);
	assertEquals(910, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a quadratic Bezier path with relative coordinates
 */
function testCalculateMinMaxQuadraticBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierRelD);

	assertEquals(20, handler.minX);
	assertEquals(50, handler.minY);
	assertEquals(500, handler.maxX);
	assertEquals(450, handler.maxY);
}

/**
 * Test ORYX.Core.SVG.MinMaxPathHandler.calculateMinMax() for a elliptic arc path with relative coordinates
 */
function testCalculateMinMaxEllipticArcRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.MinMaxPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcRelD);

	assertEquals(10, handler.minX);
	assertEquals(150, handler.minY);
	assertEquals(400, handler.maxX);
	assertEquals(540, handler.maxY);
}